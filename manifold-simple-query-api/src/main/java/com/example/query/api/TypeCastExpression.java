package com.example.query.api;

import manifold.ext.props.rt.api.val;

public class TypeCastExpression extends Expression
{
  @val Object expr;

  public TypeCastExpression( Object expr, String type )
  {
    super( type );
    this.expr = expr;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitTypeCast( this );
  }

  @Override
  public String toString()
  {
    return "(" + type + ")" + expr.toString();
  }
}
