package com.example.query.api;

import manifold.ext.props.rt.api.val;
import manifold.rt.api.util.ManObjectUtil;

import java.util.function.Function;

/**
 * A tree of simple constraints accepting direct POJO field references.
 */
public class BinaryExpression implements Expression
{
  @val Object left;
  @val Operator operator;
  @val Object right;

  /**
   * Equality or relational constraint: <br>
   * {@code person.age > 18} <br>
   * {@code person.age > 18 && person.gender == Male}
   * @param left Entity property reference, literal, or other constraint.
   * @param operator Operator must be one of:<br>
   *                 equality: ({@code == !=}) <br>
   *                 relational: ({@code > >= < <=})
   *                 logical: ({@code && ||})
   * @param right Entity property reference, literal, or other constraint.
   */
  public BinaryExpression( Object left, Operator operator, Object right )
  {
    this.left = left;
    this.right = right;
    this.operator = operator;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitBinaryExpression( this );
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append('(')
      .append( left ).append( ' ' )
      .append( operator.symbol ).append( ' ' )
      .append( right )
      .append( ')');
    return sb.toString();
  }
}
