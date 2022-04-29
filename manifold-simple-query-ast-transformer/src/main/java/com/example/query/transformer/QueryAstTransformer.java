package com.example.query.transformer;

import com.example.query.api.*;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import manifold.api.type.ICompilerComponent;
import manifold.internal.javac.IDynamicJdk;
import manifold.internal.javac.JavacPlugin;
import manifold.internal.javac.TypeProcessor;

import java.util.ArrayList;

import static manifold.api.type.ICompilerComponent.InitOrder.*;

/**
 * Transforms {@link Query} method calls involving entity property references and use of binary and unary operators.
 * <pre>
 *    Query&lt;Person&gt; query = Person.query((p, q) -> q
 *       .where(p.age >= 25 && p.gender == male)
 *       .orderBy(p.name));
 * </pre>
 * translates to:
 * <pre>
 *    Query&lt;Person&gt; query = Person.query((p, q) -> q
 *       .where(new BinaryExpression(new BinaryExpression(new ReferenceExpression(Person.class, "age"), GE, 25), AND, new BinaryExpression(new ReferenceExpression(Person.class, "gender"), EQ, male))
 *       .orderBy(new ReferenceExpression(Person.class, "name"));
 * </pre>
 * The query is processed and executed by adding an extension method to {@link Query} that accepts a target object
 * having {@code Person} objects in its domain of data, such as Collection<Person>, a database connection, data service, etc.
 * <pre>
 * package extensions.com.example.query.api.Query;
 * import com.example.query.api.Entity;
 *{@literal @}Extension
 * public class MyQueryExtension {
 *   // The type of the dataSource parameter dictates how this method implements transforms the Query into a suitable
 *   // query for the target dataSource e.g., Collection, database connection, data service, etc.
 *   public static &lt;T&gt; Iterable&lt;T&gt; run(@This Query&lt;T&gt; thiz, Collection&lt;T&gt; dataSource) {
 *     // write code to process query criteria, apply to dataSource, and return results
 *   }
 * }
 * // using extension method
 * Iterable&lt;Person&gt; result = query.run(collectionOfPersons);
 * </pre>
 */
public class QueryAstTransformer extends TreeTranslator implements ICompilerComponent, TaskListener
{
  // true if references to properties (via manifold-props) should be used as ReferenceExpressions,
  // otherwise corresponding getter methods will be used if properties are used in the query definition
  private static final boolean KEEP_PROPERTY_REFERENCES = false;

  private TypeProcessor _tp ;
  CompilationUnitTree _compilationUnit;
  private Context _context;
  private Types _types;
  private Symtab _symtab;
  private TreeMaker _make;
  private Names _names;
  private Symbol.ClassSymbol _entitySym;
  private Symbol.ClassSymbol _querySym;
  private Symbol.ClassSymbol _binaryExprSym;
  private Symbol.ClassSymbol _unaryExprSym;
  private Symbol.ClassSymbol _referenceExprSym;
  private Symbol.ClassSymbol _methodCallExprSym;
  private Symbol.ClassSymbol _operatorSym;
  private int _processingQuery;

  @Override
  public void init( BasicJavacTask javacTask, TypeProcessor typeProcessor )
  {
    if( JavacPlugin.instance() == null )
    {
      return;
    }

    _tp = typeProcessor;
    javacTask.addTaskListener( this );
  }

  @Override
  public InitOrder initOrder( ICompilerComponent compilerComponent )
  {
    return
      compilerComponent.getClass().getSimpleName().equals( "PropertyProcessor" )
      ? KEEP_PROPERTY_REFERENCES
        ? Before // if manifold-props is used, property reference are mirrored directly in ReferenceExpressions
        : After  // if manifold-props is used, property references are translated as getter methods in ReferenceExpressions
      : NA;
  }

  @Override
  public void started( TaskEvent taskEvent )
  {
  }

  @Override
  public void finished( TaskEvent e )
  {
    if( e.getKind() != TaskEvent.Kind.ANALYZE )
    {
      // translate only fully attributed trees
      return;
    }

    _compilationUnit = e.getCompilationUnit();
    _context = _tp.getContext();
    _types = _tp.getTypes();
    _symtab = _tp.getSymtab();
    _make = _tp.getTreeMaker();
    _names = Names.instance( _context );
    _entitySym = getClassSymbol( Entity.class );
    _querySym = getClassSymbol( Query.class );
    _unaryExprSym = getClassSymbol( UnaryExpression.class );
    _binaryExprSym = getClassSymbol( BinaryExpression.class );
    _referenceExprSym = getClassSymbol( ReferenceExpression.class );
    _methodCallExprSym = getClassSymbol( MethodCallExpression.class );
    _operatorSym = getClassSymbol( Operator.class );

    for( Tree tree : _compilationUnit.getTypeDecls() )
    {
      if( tree instanceof JCClassDecl )
      {
        translate( (JCClassDecl)tree );
      }
    }
  }

  @Override
  public void visitApply( JCMethodInvocation tree )
  {
    if( _entitySym == null )
    {
      super.visitApply( tree );
      return;
    }

    JCExpression methodSelect = tree.getMethodSelect();
    if( methodSelect instanceof JCTree.JCFieldAccess )
    {
      JCTree.JCFieldAccess fa = (JCTree.JCFieldAccess)methodSelect;
      List<Type> paramTypes = fa.sym.type.getParameterTypes();
      if( "where".equals( fa.getIdentifier().toString() ) &&
        paramTypes.length() == 1 && _types.isSameType( paramTypes.get( 0 ), _symtab.booleanType ) &&
        isQueryType( fa.selected.type ) )
      {
        processQueryMethodCallArgs( tree );

        // the argument of where(...) that is translated to "new Constraint(...)" is passed to call to where(Constraint)
        result = _make.Apply( List.nil(),
          _make.Select( fa.selected, getWhereMethod() ),
          List.of( tree.args.get( 0 ) ) );
        result.type = tree.type;
        result.pos = tree.pos;
        return;
      }
      else if( "orderBy".equals( fa.getIdentifier().toString() ) &&
        paramTypes.length() == 1 && _types.isSameType( paramTypes.get( 0 ), _symtab.objectType ) &&
        isQueryType( fa.selected.type ) )
      {
        processQueryMethodCallArgs( tree );

        // the argument of orderBy(...) that is translated to "new ReferenceExpression...)" is passed to call to orderBy(Reference)
        result = _make.Apply( List.nil(),
          _make.Select( fa.selected, getOrderByMethod() ),
          List.of( tree.args.get( 0 ) ) );
        result.type = tree.type;
        result.pos = tree.pos;
        return;
      }
      else if( isProcessingQuery() && paramTypes.isEmpty() &&
        !_types.isSameType( _symtab.voidType, fa.sym.type.getReturnType() ) )
      {
        // process getter methods, translated as new ReferenceExpression...)
        processQueryMethodCallArgs( tree );
        result = tree.getMethodSelect();
        return;
      }
    }

    super.visitApply( tree );

    maybeMakeMethodCallExpr( tree );
  }

  private void maybeMakeMethodCallExpr( JCMethodInvocation tree )
  {
    if( isProcessingQuery() && tree.getMethodSelect() instanceof JCFieldAccess )
    {
      JCFieldAccess fa = (JCFieldAccess)tree.getMethodSelect();
      if( isReferenceType( fa.selected.type ) && !fa.name.toString().isEmpty() )
      {
        // translate to new MethodCallExpression()

        List<Symbol.VarSymbol> parameters = ((Symbol.MethodSymbol)fa.sym).getParameters();
        ArrayList<JCLiteral> paramTypes = new ArrayList<>();
        for( Symbol.VarSymbol param : parameters )
        {
          JCLiteral typeNameExpr = _make.Literal( param.type.tsym.getQualifiedName().toString() );
          typeNameExpr.pos = tree.pos;
          paramTypes.add( typeNameExpr );
        }
        JCTree.JCNewArray paramTypesArray = _make.NewArray(
          _make.Type( _symtab.classType ), List.nil(), List.from( paramTypes ) );
        paramTypesArray.setType( new Type.ArrayType( _symtab.stringType, _symtab.arrayClass ) );
        paramTypesArray.pos = tree.pos;

        JCNewArray argsArray = _make.NewArray(
          _make.Type( _symtab.objectType ), List.nil(), List.from( tree.args ) );
        argsArray.setType( new Type.ArrayType( _symtab.objectType, _symtab.arrayClass ) );
        argsArray.pos = tree.pos;
        JCLiteral nameExpr = _make.Literal( fa.name.toString() );
        nameExpr.pos = tree.pos;
        JCExpression constructMethodCallExpr = _make.Create( getMethodCallExprCtor(),
          List.of( fa.selected, nameExpr, paramTypesArray, argsArray ) );
        result = constructMethodCallExpr;
        result.pos = tree.pos;
      }
    }
  }

  private void processQueryMethodCallArgs( JCMethodInvocation tree )
  {
    pushProcessingQuery();
    try
    {
      super.visitApply( tree );
    }
    finally
    {
      popProcessingQuery();
    }
  }

  @Override
  public void visitUnary( JCUnary jcUnary )
  {
    super.visitUnary( jcUnary );

    if( !isProcessingQuery() )
    {
      return;
    }

    Tag opcode = jcUnary.getTag();
    if( opcode != Tag.NO_TAG && opcode != Tag.NOT && opcode != Tag.NEG )
    {
      return;
    }

    // generate: new Constraint( operator, arg )

    Name operatorName = _names.fromString( convertOp( opcode ).name() );
    Symbol operatorSym = IDynamicJdk.instance().getMembersByName( _operatorSym, operatorName ).iterator().next();
    JCExpression operatorExpr = _make.QualIdent( operatorSym );
    operatorExpr.pos = jcUnary.arg.pos;
    JCExpression constructUnaryExpr = _make.Create( getUnaryExprCtor(), List.of( operatorExpr, jcUnary.arg ) );
    constructUnaryExpr.pos = jcUnary.arg.pos;
    result = constructUnaryExpr;
  }

  @Override
  public void visitBinary( JCBinary jcBinary )
  {
    super.visitBinary( jcBinary );

    if( !isProcessingQuery() )
    {
      return;
    }

    // generate: new Constraint( arg1, op, arg2 ), return this as the tree

    Tag opcode = jcBinary.getTag();
    if( !isConvertibleBinaryOp( opcode ) )
    {
      return;
    }

    Name operatorName = _names.fromString( convertOp( opcode ).name() );
    Symbol operatorSym = IDynamicJdk.instance().getMembersByName( _operatorSym, operatorName ).iterator().next();
    JCExpression operatorExpr = _make.QualIdent( operatorSym );
    operatorExpr.pos = jcBinary.lhs.pos;
    JCExpression constructBinaryExpr = _make.Create( getBinaryExprCtor(),
      List.of( jcBinary.lhs, operatorExpr, jcBinary.rhs ) );
    constructBinaryExpr.pos = jcBinary.pos;
    result = constructBinaryExpr;
  }

  @Override
  public void visitSelect( JCFieldAccess tree )
  {
    super.visitSelect( tree );

    if( !isProcessingQuery() )
    {
      return;
    }

    // transform Entity field access to Reference

    if( isEntityType( tree.selected.type ) )
    {
      String entityTypeName = tree.selected.type.tsym.getQualifiedName().toString();
      String memberName = tree.getIdentifier().toString();
      JCLiteral entityArg = _make.Literal( entityTypeName );
      entityArg.pos = tree.pos;
      JCLiteral memberArg = _make.Literal( memberName );
      memberArg.pos = tree.pos;
      JCExpression newRef = _make.Create( getReferenceExprCtor(), List.of( entityArg, memberArg ) );
      newRef.pos = tree.pos;
      result = newRef;
    }
  }

  private boolean isEntityType( Type type )
  {
    return isType( type, _entitySym.type );
  }
  private boolean isQueryType( Type type )
  {
    return isType( type, _querySym.type );
  }
  private boolean isReferenceType( Type type )
  {
    return isType( type, _referenceExprSym.type );
  }
  private boolean isType( Type type, Type classType )
  {
    return type instanceof Type.ClassType && _types.isAssignable( type, _types.erasure( classType ) );
  }

  private Symbol.MethodSymbol getReferenceExprCtor()
  {
    for( Symbol s : IDynamicJdk.instance().getMembers( _referenceExprSym, Symbol::isConstructor ) )
    {
      Symbol.MethodSymbol ctor = (Symbol.MethodSymbol)s;
      if( ctor.params.length() == 2 )
      {
        if( ctor.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) &&
          ctor.params.get( 1 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) )
        {
          return ctor;
        }
      }
    }
    throw new IllegalStateException( "missing Reference constructor" );
  }
  private Symbol.MethodSymbol getMethodCallExprCtor()
  {
    for( Symbol s : IDynamicJdk.instance().getMembers( _methodCallExprSym, Symbol::isConstructor ) )
    {
      Symbol.MethodSymbol ctor = (Symbol.MethodSymbol)s;
      if( ctor.params.length() == 4 )
      {
        return ctor;
      }
    }
    throw new IllegalStateException( "missing MethodCallExpression constructor" );
  }

  private Symbol.MethodSymbol getUnaryExprCtor()
  {
    for( Symbol s : IDynamicJdk.instance().getMembers( _unaryExprSym, Symbol::isConstructor ) )
    {
      Symbol.MethodSymbol ctor = (Symbol.MethodSymbol)s;
      if( ctor.params.length() == 2 )
      {
        if( ctor.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( Operator.class.getTypeName() ) &&
          ctor.params.get( 1 ).type.tsym.getQualifiedName().toString().equals( Object.class.getTypeName() ) )
        {
          return ctor;
        }
      }
    }
    throw new IllegalStateException( "missing UnaryExpression constructor" );
  }
  private Symbol.MethodSymbol getBinaryExprCtor()
  {
    for( Symbol s : IDynamicJdk.instance().getMembers( _binaryExprSym, Symbol::isConstructor ) )
    {
      Symbol.MethodSymbol ctor = (Symbol.MethodSymbol)s;
      if( ctor.params.length() == 3 )
      {
        if( ctor.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( Object.class.getTypeName() ) &&
          ctor.params.get( 1 ).type.tsym.getQualifiedName().toString().equals( Operator.class.getTypeName() ) &&
          ctor.params.get( 2 ).type.tsym.getQualifiedName().toString().equals( Object.class.getTypeName() ) )
        {
          return ctor;
        }
      }
    }
    throw new IllegalStateException( "missing BinaryExpression constructor" );
  }
  private Symbol.MethodSymbol getWhereMethod()
  {
    for( Symbol s : IDynamicJdk.instance().getMembersByName( _querySym, _names.fromString( "where" ) ) )
    {
      Symbol.MethodSymbol m = (Symbol.MethodSymbol)s;
      if( m.params.length() == 1 )
      {
        if( m.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( Expression.class.getTypeName() ) )
        {
          return m;
        }
      }
    }
    throw new IllegalStateException( "missing where() method" );
  }

  private Symbol.MethodSymbol getOrderByMethod()
  {
    for( Symbol s : IDynamicJdk.instance().getMembersByName( _querySym, _names.fromString( "orderBy" ) ) )
    {
      Symbol.MethodSymbol m = (Symbol.MethodSymbol)s;
      if( m.params.length() == 1 )
      {
        if( m.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( ReferenceExpression.class.getTypeName() ) )
        {
          return m;
        }
      }
    }
    throw new IllegalStateException( "missing orderBy() method" );
  }

  public boolean isProcessingQuery() { return _processingQuery > 0; }
  public void pushProcessingQuery() { _processingQuery++; }
  public void popProcessingQuery()  { _processingQuery--; }

  private Operator convertOp( JCTree.Tag jcOp )
  {
    switch( jcOp )
    {
      case GT: return Operator.GT;
      case GE: return Operator.GE;
      case LT: return Operator.LT;
      case LE: return Operator.LE;
      case EQ: return Operator.EQ;
      case NE: return Operator.NE;
      case AND: return Operator.AND;
      case OR: return Operator.OR;
      case NOT: return Operator.NOT;
      case NEG: return Operator.NEG;
      case NO_TAG: return Operator.NONE;
      default: throw new IllegalStateException( "unhandled operator: " + jcOp.name() );
    }
  }

  private boolean isConvertibleBinaryOp( JCTree.Tag jcOp )
  {
    switch( jcOp )
    {
      case GT:
      case GE:
      case LT:
      case LE:
      case EQ:
      case NE:
      case AND:
      case OR:
        return true;
    }
    return false;
  }

  private Symbol.ClassSymbol getClassSymbol( Class<?> cls )
  {
    return IDynamicJdk.instance().getTypeElement( _context, _compilationUnit, cls.getTypeName() );
  }
}
