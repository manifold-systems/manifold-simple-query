package com.example.query.processor;

import com.example.query.api.*;
import manifold.ext.props.rt.api.val;
import manifold.rt.api.util.ManObjectUtil;
import manifold.util.ReflectUtil;

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
  public Object visitReferenceExpression( ReferenceExpression expr )
  {
    Entity entityValue = valueSupplier.apply( expr.typeName );
    if( expr.memberKind == MemberKind.Method )
    {
      return ReflectUtil.method( entityValue, expr.memberName ).invoke();
    }
    return ReflectUtil.field( entityValue, expr.memberName ).get();
  }

  @Override
  public Object visitMethodCallExpression( MethodCallExpression expr )
  {
    Object receiverValue = expr.receiver.accept( this );
    return FunctionCallHandler.invoke( receiverValue, expr.methodName, expr.paramTypes, expr.args );
  }

  @Override
  public Object visitUnaryExpression( UnaryExpression expr )
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

  @SuppressWarnings( {"unchecked", "rawtypes"} )
  @Override
  public Object visitBinaryExpression( BinaryExpression expr )
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
        return ((Comparable)leftValue).compareTo( rightValue ) > 0;
      case LT:
        return ((Comparable)leftValue).compareTo( rightValue ) < 0;
      case GE:
        return ((Comparable)leftValue).compareTo( rightValue ) >= 0;
      case LE:
        return ((Comparable)leftValue).compareTo( rightValue ) <= 0;
      case EQ:
        return ManObjectUtil.equals( leftValue, rightValue );
      case NE:
        return !ManObjectUtil.equals( leftValue, rightValue );
      default:
        throw new IllegalStateException( "Unexpected operator: " + expr.operator );
    }
  }
}
