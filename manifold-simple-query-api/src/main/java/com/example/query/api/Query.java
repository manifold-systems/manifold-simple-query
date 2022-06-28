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
public class Query<T> extends QueryBase<T>
{
  public Query( BinaryExpression constraints )
  {
    super( constraints );
  }
}
