package com.example.query.api;

import manifold.ext.props.rt.api.var;
import manifold.ext.rt.api.Self;

public abstract class QueryBase<T>
{
  @var Expression constraints;
  @var ReferenceExpression orderBy;

  public QueryBase( BinaryExpression constraints )
  {
    this.constraints = constraints;
  }

  /**
   * @param constraints Consists of direct references to entity properties using normal Java expressions.
   * @return This {@link QueryBase}, preserving entity property references in the {@code constraints} as {@link Expression}s.
   */
  public @Self QueryBase<T> where( boolean constraints )
  {
    throw new IllegalStateException();
  }
  /** Invoked from transformed AST */
  public @Self QueryBase<T> where( Expression constraints )
  {
    this.constraints = constraints;
    return this;
  }

  /**
   * @param entityFieldRef Consists of a direct reference to an entity property using a normal Java expression.
   * @return This {@link QueryBase}, preserving entity property references as {@link Expression}s.
   */
  public @Self QueryBase<T> orderBy( Object entityFieldRef )
  {
    throw new IllegalStateException();
  }
  /** Invoked from transformed AST */
  public @Self QueryBase<T> orderBy( ReferenceExpression orderBy )
  {
    this.orderBy = orderBy;
    return this;
  }

  public String toString()
  {
    return
      "WHERE......" + constraints + '\n' +
      "ORDER BY..." + orderBy;
  }
}
