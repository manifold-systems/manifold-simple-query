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
import manifold.tuple.rt.api.Tuple;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;

import static com.example.query.api.MemberKind.Field;
import static com.example.query.api.MemberKind.Method;
import static manifold.api.type.ICompilerComponent.InitOrder.*;

/**
 * Transforms {@link Query} method calls involving entity property references, unary and binary operators, and function
 * calls.
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
  // true if references to properties (via manifold-props) should be used directly,
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
  private Symbol.ClassSymbol _tupleSym;
  private Symbol.ClassSymbol _binaryExprSym;
  private Symbol.ClassSymbol _unaryExprSym;
  private Symbol.ClassSymbol _referenceExprSym;
  private Symbol.ClassSymbol _typeReferenceExprSym;
  private Symbol.ClassSymbol _typeCastExprSym;
  private Symbol.ClassSymbol _expressionSym;
  private Symbol.ClassSymbol _methodCallExprSym;
  private Symbol.ClassSymbol _operatorSym;
  private Symbol.ClassSymbol _memberKindSym;
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
    _querySym = getClassSymbol( QueryBase.class );
    _tupleSym = getClassSymbol( Tuple.class );
    _unaryExprSym = getClassSymbol( UnaryExpression.class );
    _binaryExprSym = getClassSymbol( BinaryExpression.class );
    _expressionSym = getClassSymbol( Expression.class );
    _referenceExprSym = getClassSymbol( ReferenceExpression.class );
    _typeReferenceExprSym = getClassSymbol( TypeReferenceExpression.class );
    _typeCastExprSym = getClassSymbol( TypeCastExpression.class );
    _methodCallExprSym = getClassSymbol( MethodCallExpression.class );
    _operatorSym = getClassSymbol( Operator.class );
    _memberKindSym = getClassSymbol( MemberKind.class );

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

        // translate to `.where(Expression)`

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

        // translate to `.orderBy(ReferenceExpression)`

        result = _make.Apply( List.nil(),
          _make.Select( fa.selected, getOrderByMethod() ),
          List.of( tree.args.get( 0 ) ) );
        result.type = tree.type;
        result.pos = tree.pos;
        return;
      }
      else if( "select".equals( fa.getIdentifier().toString() ) &&
        paramTypes.length() == 1 && _types.isSameType( _types.erasure( paramTypes.get( 0 ) ), _tupleSym.type ) &&
        isEntityType( fa.selected.type ) )
      {
        super.visitApply( tree );

        // translate to `.select(Class, Expression[])`

        result = _make.Apply( List.nil(),
          _make.Select( _make.Ident( fa.selected.type.tsym ), getSelectMethod() ),
          List.of( _make.ClassLiteral( tree.args.get( 0 ).type ),
            translateTupleValueExpressions( (JCNewClass)((JCParens)tree.args.get( 0 )).getExpression() ) ) );
        result.type = tree.type;
        result.pos = tree.pos;
        return;
      }
      else if( isProcessingQuery() )
      {
        processQueryMethodCallArgs( tree );

        // translates to `new ReferenceExpression(...)` or `new MethodCallExpression(...)`

        translateEntityReferencesAndMethodCalls( tree );
        return;
      }
    }

    super.visitApply( tree );
  }

  private JCNewArray translateTupleValueExpressions( JCNewClass tree )
  {
    pushProcessingQuery();
    try
    {
      translate( tree.args );
    }
    finally
    {
      popProcessingQuery();
    }

    JCNewArray paramTypesArray = _make.NewArray(
      _make.Type( _expressionSym.type ), List.nil(), List.from( tree.args ) );
    paramTypesArray.setType( new Type.ArrayType( _expressionSym.type, _symtab.arrayClass ) );
    paramTypesArray.pos = tree.pos;
    return paramTypesArray;
  }


  private void translateEntityReferencesAndMethodCalls( JCMethodInvocation tree )
  {
    if( isProcessingQuery() )
    {
      if( tree.getMethodSelect() instanceof JCFieldAccess )
      {
        JCFieldAccess fa = (JCFieldAccess)tree.getMethodSelect();
        if( isEntityType( fa.selected.type ) && tree.args.isEmpty() ) // only consider 'getter' calls
        {
          // translate to `new ReferenceExpression(...)`

          String exprType = getTypeName( tree.type );
          JCLiteral exprTypeExpr = _make.Literal( exprType );
          exprTypeExpr.pos = tree.pos;
          String entityTypeName = getTypeName( fa.selected.type );
          JCLiteral receiverTypeExpr = _make.Literal( entityTypeName );
          receiverTypeExpr.pos = tree.pos;
          String memberName = fa.getIdentifier().toString();
          JCLiteral memberArg = _make.Literal( memberName );
          memberArg.pos = tree.pos;
          JCExpression methodMemberKindExpr = memberKindExpr( Method );
          methodMemberKindExpr.pos = tree.pos;
          result = _make.Create( getReferenceExprCtor(), List.of( exprTypeExpr, receiverTypeExpr, memberArg, methodMemberKindExpr ) );
          result.pos = tree.pos;
        }
        else if( isExpressionType( fa.selected.type ) && !fa.name.toString().isEmpty() )
        {
          // translate to `new MethodCallExpression(...)`

          JCLiteral nameExpr = _make.Literal( fa.name.toString() );
          nameExpr.pos = tree.pos;
          JCNewArray paramTypesArray = makeParamTypeArray( tree, fa );
          JCNewArray argsArray = makeArgsArray( tree, ((Symbol.MethodSymbol)fa.sym).isVarArgs() );
          JCLiteral typeNameExpr = _make.Literal( getTypeName( tree.type ) );
          typeNameExpr.pos = tree.pos;
          result = _make.Create( getMethodCallExprCtor(),
            List.of( fa.selected, nameExpr, paramTypesArray, argsArray, typeNameExpr ) );
          result.pos = tree.pos;
        }
        else if( ((JCFieldAccess)tree.meth).sym.getModifiers().contains( Modifier.STATIC ) )
        {
          // translate to static `new MethodCallExpression(...)`

          String className = getTypeName( fa.selected.type );
          JCLiteral classNameExpr = _make.Literal( className );
          classNameExpr.pos = fa.pos;
          JCExpression newTypeRef = _make.Create( getTypeReferenceExprCtor(), List.of( classNameExpr ) );
          newTypeRef.pos = fa.pos;

          JCLiteral nameExpr = _make.Literal( fa.name.toString() );
          nameExpr.pos = tree.pos;
          JCNewArray paramTypesArray = makeParamTypeArray( tree, fa );
          JCNewArray argsArray = makeArgsArray( tree, ((Symbol.MethodSymbol)fa.sym).isVarArgs() );
          JCLiteral typeNameExpr = _make.Literal( getTypeName( tree.type ) );
          typeNameExpr.pos = tree.pos;

          result = _make.Create( getMethodCallExprCtor(),
            List.of( newTypeRef, nameExpr, paramTypesArray, argsArray, typeNameExpr ) );
          result.pos = tree.pos;
        }
      }
    }
  }

  private JCExpression memberKindExpr( MemberKind memberKind )
  {
    Symbol memberKindSym = IDynamicJdk.instance()
      .getMembersByName( _memberKindSym, _names.fromString( memberKind.name() ) ).iterator().next();
    return _make.QualIdent( memberKindSym );
  }

  private JCNewArray makeArgsArray( JCMethodInvocation tree, boolean isVarArgs )
  {
    JCNewArray argsArray = _make.NewArray(
      _make.Type( _symtab.objectType ), List.nil(), List.from( isVarArgs ? handleVarArgs( tree ) : tree.args ) );
    argsArray.setType( new Type.ArrayType( _symtab.objectType, _symtab.arrayClass ) );
    argsArray.pos = tree.pos;
    return argsArray;
  }

  private List<JCExpression> handleVarArgs( JCMethodInvocation tree )
  {
    JCFieldAccess fa = (JCFieldAccess)tree.getMethodSelect();
    List<Symbol.VarSymbol> params = ((Symbol.MethodSymbol)fa.sym).params();
    List<JCExpression> newArgs = List.nil();
    for( int i = 0; i < params.size(); i++ )
    {
      if( i == params.size() - 1 )
      {
        ArrayList<JCExpression> varArgs = new ArrayList<>();
        for( int j = i; j < tree.args.length(); j++ )
        {
          varArgs.add( tree.args.get( j ) );
        }
        Type elemType = _types.erasure( tree.varargsElement );
        JCNewArray varArgsExpr = _make.NewArray( _make.Type( elemType ), List.nil(), List.from( varArgs ) );
        varArgsExpr.setType( new Type.ArrayType( elemType, _symtab.arrayClass ) );
        varArgsExpr.pos = tree.pos;

        newArgs = newArgs.append( varArgsExpr );
      }
      else
      {
        newArgs = newArgs.append( tree.args.get( i ) );
      }
    }
    return newArgs;
  }

  private JCNewArray makeParamTypeArray( JCMethodInvocation tree, JCFieldAccess fa )
  {
    List<Symbol.VarSymbol> parameters = ((Symbol.MethodSymbol)fa.sym).getParameters();
    ArrayList<JCLiteral> paramTypes = new ArrayList<>();
    for( Symbol.VarSymbol param : parameters )
    {
      JCLiteral typeNameExpr = _make.Literal( getTypeName( param.type ) );
      typeNameExpr.pos = tree.pos;
      paramTypes.add( typeNameExpr );
    }
    JCNewArray paramTypesArray = _make.NewArray(
      _make.Type( _symtab.classType ), List.nil(), List.from( paramTypes ) );
    paramTypesArray.setType( new Type.ArrayType( _symtab.stringType, _symtab.arrayClass ) );
    paramTypesArray.pos = tree.pos;
    return paramTypesArray;
  }

  private String getTypeName( Type type )
  {
    type = _types.erasure( type );
    StringBuilder sb = new StringBuilder( type.tsym.flatName() );
    for( ; type instanceof Type.ArrayType; type = ((Type.ArrayType)type).getComponentType() )
    {
      sb.append( "[]" );
    }
    return sb.toString();
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
  public void visitUnary( JCUnary tree )
  {
    super.visitUnary( tree );

    if( !isProcessingQuery() )
    {
      return;
    }

    Tag opcode = tree.getTag();
    if( opcode != Tag.NO_TAG && opcode != Tag.NOT && opcode != Tag.NEG )
    {
      return;
    }

    // `new UnaryExpression( operator, arg )`

    Name operatorName = _names.fromString( convertOp( opcode ).name() );
    Symbol operatorSym = IDynamicJdk.instance().getMembersByName( _operatorSym, operatorName ).iterator().next();
    JCExpression operatorExpr = _make.QualIdent( operatorSym );
    operatorExpr.pos = tree.arg.pos;
    JCLiteral typeNameExpr = _make.Literal( getTypeName( tree.type ) );
    typeNameExpr.pos = tree.pos;
    JCExpression constructUnaryExpr = _make.Create( getUnaryExprCtor(), List.of( operatorExpr, tree.arg, typeNameExpr ) );
    constructUnaryExpr.pos = tree.arg.pos;
    result = constructUnaryExpr;
  }

  @Override
  public void visitBinary( JCBinary tree )
  {
    super.visitBinary( tree );

    if( !isProcessingQuery() )
    {
      return;
    }

    // `new BinaryExpression( arg1, op, arg2 )`

    Tag opcode = tree.getTag();
    if( !isConvertibleBinaryOp( opcode ) )
    {
      return;
    }

    Name operatorName = _names.fromString( convertOp( opcode ).name() );
    Symbol operatorSym = IDynamicJdk.instance().getMembersByName( _operatorSym, operatorName ).iterator().next();
    JCExpression operatorExpr = _make.QualIdent( operatorSym );
    operatorExpr.pos = tree.lhs.pos;

    JCLiteral typeExpr = _make.Literal( getTypeName( tree.type ) );
    typeExpr.pos = tree.pos;

    JCExpression constructBinaryExpr = _make.Create( getBinaryExprCtor(),
      List.of( tree.lhs, operatorExpr, tree.rhs, typeExpr ) );
    constructBinaryExpr.pos = tree.pos;
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

    if( (isEntityType( tree.selected.type ) || isTupleType( tree.selected.type ))
      && !(tree.type instanceof Type.MethodType) ) // actual *field* access, as opposed to a method name reference
    {
      // `new ReferenceExpression(...)`

      String exprType = getTypeName( tree.type );
      JCLiteral exprTypeExpr = _make.Literal( exprType );
      String receiverTypeName = getTypeName( tree.selected.type );
      JCLiteral typeNameExpr = _make.Literal( receiverTypeName );
      typeNameExpr.pos = tree.pos;
      String fieldName = tree.getIdentifier().toString();
      JCLiteral memberArg = _make.Literal( fieldName );
      memberArg.pos = tree.pos;
      JCExpression fieldMemberKindExpr = memberKindExpr( Field );
      fieldMemberKindExpr.pos = tree.pos;
      JCExpression newRef = _make.Create( getReferenceExprCtor(), List.of( exprTypeExpr, typeNameExpr, memberArg, fieldMemberKindExpr ) );
      newRef.pos = tree.pos;
      result = newRef;
    }
  }

  @Override
  public void visitTypeCast( JCTypeCast tree )
  {
    super.visitTypeCast( tree );

    if( isProcessingQuery() )
    {
      Type type = tree.getExpression().type;
      if( isExpressionType( type ) )
      {
        String typeName = getTypeName( tree.getType().type );
        JCLiteral typeNameExpr = _make.Literal( typeName );
        typeNameExpr.pos = tree.pos;
        result = _make.Create( getTypeCastExprCtor(), List.of( tree.expr, typeNameExpr ) );
        result.pos = tree.pos;
      }
    }
  }

  private boolean isEntityType( Type type )
  {
    return isType( type, _entitySym.type );
  }
  private boolean isTupleType( Type type )
  {
    return isType( type, _tupleSym.type );
  }
  private boolean isQueryType( Type type )
  {
    return isType( type, _querySym.type );
  }
  private boolean isExpressionType( Type type )
  {
    return isType( type, _expressionSym.type );
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
      if( ctor.params.length() == 4 )
      {
        if( ctor.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) &&
          ctor.params.get( 1 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) &&
          ctor.params.get( 2 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) &&
          ctor.params.get( 3 ).type.tsym.getQualifiedName().toString().equals( MemberKind.class.getTypeName() ) )
        {
          return ctor;
        }
      }
    }
    throw new IllegalStateException( "missing ReferenceExpression constructor" );
  }

  private Symbol.MethodSymbol getTypeReferenceExprCtor()
  {
    for( Symbol s : IDynamicJdk.instance().getMembers( _typeReferenceExprSym, Symbol::isConstructor ) )
    {
      Symbol.MethodSymbol ctor = (Symbol.MethodSymbol)s;
      if( ctor.params.length() == 1 )
      {
        if( ctor.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) )
        {
          return ctor;
        }
      }
    }
    throw new IllegalStateException( "missing TypeReferenceExpression constructor" );
  }
  private Symbol.MethodSymbol getTypeCastExprCtor()
  {
    for( Symbol s : IDynamicJdk.instance().getMembers( _typeCastExprSym, Symbol::isConstructor ) )
    {
      Symbol.MethodSymbol ctor = (Symbol.MethodSymbol)s;
      if( ctor.params.length() == 2 )
      {
        if( ctor.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( Object.class.getTypeName() ) &&
          ctor.params.get( 1 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) )
        {
          return ctor;
        }
      }
    }
    throw new IllegalStateException( "missing TypeCastExpression constructor" );
  }
  private Symbol.MethodSymbol getMethodCallExprCtor()
  {
    for( Symbol s : IDynamicJdk.instance().getMembers( _methodCallExprSym, Symbol::isConstructor ) )
    {
      Symbol.MethodSymbol ctor = (Symbol.MethodSymbol)s;
      if( ctor.params.length() == 5 )
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
      if( ctor.params.length() == 3 )
      {
        if( ctor.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( Operator.class.getTypeName() ) &&
          ctor.params.get( 1 ).type.tsym.getQualifiedName().toString().equals( Object.class.getTypeName() ) &&
          ctor.params.get( 2 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) )
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
      if( ctor.params.length() == 4 )
      {
        if( ctor.params.get( 0 ).type.tsym.getQualifiedName().toString().equals( Object.class.getTypeName() ) &&
          ctor.params.get( 1 ).type.tsym.getQualifiedName().toString().equals( Operator.class.getTypeName() ) &&
          ctor.params.get( 2 ).type.tsym.getQualifiedName().toString().equals( Object.class.getTypeName() ) &&
          ctor.params.get( 3 ).type.tsym.getQualifiedName().toString().equals( String.class.getTypeName() ) )
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

  private Symbol.MethodSymbol getSelectMethod()
  {
    for( Symbol s : IDynamicJdk.instance().getMembersByName( _entitySym, _names.fromString( "select" ) ) )
    {
      Symbol.MethodSymbol m = (Symbol.MethodSymbol)s;
      if( m.params.length() == 2 )
      {
        Type firstParam = _types.erasure( m.params.get( 0 ).type );
        Type secondParam = m.params.get( 1 ).type;
        if( _types.isSameType( firstParam, _types.erasure( _symtab.classType ) ) &&
          secondParam instanceof Type.ArrayType &&
          ((Type.ArrayType)secondParam).elemtype.tsym.getQualifiedName().toString().equals( Expression.class.getTypeName() ) )
        {
          return m;
        }
      }
    }
    throw new IllegalStateException( "missing select() method" );
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
      case PLUS: return Operator.PLUS;
      case MINUS: return Operator.MINUS;
      case MUL: return Operator.TIMES;
      case DIV: return Operator.DIV;
      case MOD: return Operator.REM;
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
      case PLUS:
      case MINUS:
      case MUL:
      case DIV:
      case MOD:
        return true;
    }
    return false;
  }

  private Symbol.ClassSymbol getClassSymbol( Class<?> cls )
  {
    return IDynamicJdk.instance().getTypeElement( _context, _compilationUnit, cls.getTypeName() );
  }
}
