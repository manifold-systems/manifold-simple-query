package com.example.query.processor;

import com.example.query.api.*;
import manifold.ext.props.rt.api.val;
import manifold.util.ReflectUtil;

import java.util.Arrays;
import java.util.function.Function;

/**
 * A simple query {@link Expression} evaluator using {@link ReflectUtil}.
 * <p/>
 * See {@code QueryAstTransformer}.
 */
public class SimpleEvaluator implements ExpressionVisitor
{
  @val Function<String, ? extends Entity> valueSupplier;

  public SimpleEvaluator( Function<String, ? extends Entity> valueSupplier )
  {
    this.valueSupplier = valueSupplier;
  }

  public Object evaluate( Expression expr )
  {
    return expr.accept( this );
  }

  @Override
  public Object visitReference( ReferenceExpression expr )
  {
    Entity entityValue = valueSupplier.apply( expr.type );
    if( expr.memberKind == MemberKind.Method )
    {
      return ReflectUtil.method( entityValue, expr.memberName ).invoke();
    }
    return ReflectUtil.field( entityValue, expr.memberName ).get();
  }

  @Override
  public Object visitTypeReference( TypeReferenceExpression expr )
  {
    return ReflectUtil.type( expr.type );
  }

  @Override
  public Object visitMethodCall( MethodCallExpression expr )
  {
    Object receiverValue = expr.receiver == null ? null : expr.receiver.accept( this );
    Object[] argValues = Arrays.stream( expr.args ).map( arg ->
      arg instanceof Expression ? ((Expression)arg).accept( this ) : arg ).toArray();
    return FunctionCallHandler.invoke( receiverValue, expr.methodName, expr.paramTypes, argValues );
  }

  @Override
  public Object visitUnary( UnaryExpression expr )
  {
    Object operandValue = expr.operand;
    if( operandValue instanceof Expression )
    {
      operandValue = ((Expression)operandValue).accept( this );
    }
    switch( expr.operator )
    {
      case NOT:
        return !(Boolean)operandValue;
      case NEG:
        throw new UnsupportedOperationException( "negation unary operator not implemented" ); //return -(Number)operandValue; lol
      default:
        throw new IllegalStateException( "Unexpected operator: " + expr.operator );
    }
  }

  @Override
  public Object visitTypeCast( TypeCastExpression typeCastExpr )
  {
    Object expr = typeCastExpr.expr;
    Object exprValue = expr instanceof Expression ? ((Expression)expr).accept( this ) : expr;
    Class<?> type = ReflectUtil.type( typeCastExpr.type );
    if( com.example.query.api.Expression.class.isAssignableFrom( type ) )
    {
      return exprValue;
    }
    return ArithmeticUtil.coerce( exprValue, type );
  }

  @Override
  public Object visitBinary( BinaryExpression expr )
  {
    Object leftValue = expr.left;
    if( leftValue instanceof Expression )
    {
      leftValue = ((Expression)leftValue).accept( this );
    }
    Object rightValue = expr.right;
    if( rightValue instanceof Expression )
    {
      rightValue = ((Expression)rightValue).accept( this );
    }
    switch( expr.operator )
    {
      case AND:
        return (Boolean)leftValue && (Boolean)rightValue;
      case OR:
        return (Boolean)leftValue || (Boolean)rightValue;
      case GT:
        return ArithmeticUtil.compareTo( leftValue, rightValue ) > 0;
      case LT:
        return ArithmeticUtil.compareTo( leftValue, rightValue ) < 0;
      case GE:
        return ArithmeticUtil.compareTo( leftValue, rightValue ) >= 0;
      case LE:
        return ArithmeticUtil.compareTo( leftValue, rightValue ) <= 0;
      case EQ:
        return ArithmeticUtil.equals( leftValue, rightValue );
      case NE:
        return !ArithmeticUtil.equals( leftValue, rightValue );
      case PLUS:
        return ArithmeticUtil.plus( leftValue, rightValue );
      case MINUS:
        return ArithmeticUtil.minus( leftValue, rightValue );
      case TIMES:
        return ArithmeticUtil.times( leftValue, rightValue );
      case DIV:
        return ArithmeticUtil.div( leftValue, rightValue );
      case REM:
        return ArithmeticUtil.rem( leftValue, rightValue );
      default:
        throw new IllegalStateException( "Unexpected operator: " + expr.operator );
    }
  }
}
