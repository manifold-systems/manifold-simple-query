package com.example.query.api;

import manifold.ext.props.rt.api.val;

public enum Operator
{
  NONE(""),
  NOT("!"), NEG("-"),
  AND("&&"), OR("||"),
  GT(">"), LT("<"), GE(">="), LE("<="),
  EQ("=="), NE("!=");

  @val String symbol;

  private Operator( String symbol )
  {
    this.symbol = symbol;
  }
}
