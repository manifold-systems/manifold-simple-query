package com.example.query.api;

import manifold.ext.props.rt.api.var;

import java.util.Arrays;

/**
 * A simple API where the query criteria expressions are supplied using direct entity references, which are transformed
 * via AST manipulation to {@link Expression}s.
 * <pre>
 * Query&lt;Person&gt; query = Person.query( (p, q) -> q
 *  .where(p.age >= 18 && p.gender == male)
 *  .orderBy(p.name);
 * </pre>
 * For the query execution side see {@link manifoldsampleasttransformer.extensions.com.example.query.api.Query.MyQueryExtension}.
 * @param <T> An {@link Entity} subclass such as "Person", "Address", or whatever.
 */
public class Query<T extends Entity>
{
  @var Expression constraints;
  @var ReferenceExpression orderBy;
  @var ReferenceExpression[] select; //todo: new feature: AnonRecord, generate "class Blah extends Entity implements AnonRecord" with proper fields

  public Query( BinaryExpression constraints )
  {
    this.constraints = constraints;
  }

  /**
   * @param constraints Consists of direct references to entity properties using normal Java expressions.
   * @return This {@link Query}, preserving entity property references in the {@code constraints} as {@link Expression}s.
   */
  public Query<T> where( boolean constraints )
  {
    throw new IllegalStateException();
  }
  /** Invoked from transformed AST */
  public Query<T> where( Expression constraints )
  {
    this.constraints = constraints;
    return this;
  }

  /**
   * @param entityFieldRef Consists of a direct reference to an entity property using a normal Java expression.
   * @return This {@link Query}, preserving entity property references as {@link Expression}s.
   */
  public Query<T> orderBy( Object entityFieldRef )
  {
    throw new IllegalStateException();
  }
  /** Invoked from transformed AST */
  public Query<T> orderBy( ReferenceExpression orderBy )
  {
    this.orderBy = orderBy;
    return this;
  }

  //todo: implement after finishing anonymous types feature
  public Query<T> select( Object... references )
  {
    throw new IllegalStateException();
  }
  public Query<T> select( ReferenceExpression... select )
  {
    this.select = select;
    return this;
  }

  public String toString()
  {
    return
      "WHERE......" + constraints + '\n' +
      "ORDER BY..." + orderBy + '\n' +
      "SELECT....." + Arrays.toString(select) + '\n';
  }
}
