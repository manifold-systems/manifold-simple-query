package com.example.query.api;

import manifold.ext.props.rt.api.val;
import manifold.rt.api.util.ManObjectUtil;

import java.util.function.Function;

/**
 */
public class UnaryExpression implements Expression
{
  @val Operator operator;
  @val Object operand;

  /**
   * Unary constraint e.g., {@code !person.deceased}
   * @param operator Operator must be one of:<br>
   *                {@link Operator#NOT}: {@code !} <br>
   *                {@link Operator#NEG}: {@code -}
   * @param operand Must be boolean.
   */
  public UnaryExpression( Operator operator, Object operand )
  {
    this.operator = operator;
    this.operand = operand;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitUnaryExpression( this );
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append('(')
      .append( operator.symbol )
      .append( operand )
      .append( ')');
    return sb.toString();
  }
}
