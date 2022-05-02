package com.example.query.api;

import manifold.ext.props.rt.api.val;

public abstract class Expression
{
  @val String type;

  public Expression( String type )
  {
    this.type = type;
  }

  public abstract Object accept( ExpressionVisitor visitor );
}
