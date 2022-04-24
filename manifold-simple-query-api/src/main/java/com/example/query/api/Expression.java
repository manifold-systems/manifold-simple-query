package com.example.query.api;

public interface Expression
{
  Object accept( ExpressionVisitor visitor );
}
