package com.example.query.api;

import manifold.ext.props.rt.api.val;

/**
 */
public class UnaryExpression extends Expression
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
  public UnaryExpression( Operator operator, Object operand, String type )
  {
    super( type );
    this.operator = operator;
    this.operand = operand;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitUnary( this );
  }

  public String toString()
  {
    return '(' + operator.symbol + operand + ')';
  }
}
