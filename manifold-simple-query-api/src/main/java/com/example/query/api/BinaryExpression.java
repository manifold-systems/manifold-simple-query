package com.example.query.api;

import manifold.ext.props.rt.api.val;

/**
 * A tree of simple constraints accepting direct POJO field references.
 */
public class BinaryExpression extends Expression
{
  @val Object left;
  @val Operator operator;
  @val Object right;

  /**
   * Equality, relational, or arithmetic expression: <br>
   * {@code person.age > 18} <br>
   * {@code person.age > 18 && person.gender == Male}
   * @param left Entity property reference, literal, or other constraint.
   * @param operator Operator must be one of:<br>
   *                 equality: ({@code == !=}) <br>
   *                 relational: ({@code > >= < <=})
   *                 logical: ({@code && ||})
   *                 arithmetic: ({@code + - * / %})
   * @param right Entity property reference, literal, or other expression.
   * @param type The qualified Java type name of the resulting value.
   */
  public BinaryExpression( Object left, Operator operator, Object right, String type )
  {
    super( type );
    this.left = left;
    this.right = right;
    this.operator = operator;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitBinary( this );
  }

  public String toString()
  {
    return "(" + left + ' ' + operator.symbol + ' ' +  right + ')';
  }
}
