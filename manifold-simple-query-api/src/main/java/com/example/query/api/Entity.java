package com.example.query.api;

import manifold.ext.rt.api.Self;
import manifold.tuple.rt.api.Tuple;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A root class for queryable entities. Note, the static methods here could be moved to Object via extension class so
 * that all classes are queryable. For now, queries are limited to Entity classes.
 */
public abstract class Entity
{
  /**
   * Builds a complete query, which may include where-clause, orderBy-clause, etc.
   * <pre>
   *   Person.query( (p, q) -> q
   *     .where(p.age >= 18 && p.gender == male)
   *     .orderBy(p.name)); </pre>
   * Note this method's use of {@link Self}. As a static method {@literal @}Self relieves the derived class from having
   * to shadow this method with its own type -- {@code Person.query(...)} returns {@code Query&lt;Person&gt;}.
   * @param query A function providing Query and Entity contexts. The function returns the same Query.
   * @return A {@link Query} which preserves entity property references.
   */
  public static Query<@Self Entity> query( BiFunction<@Self Entity, Query<@Self Entity>, Query<@Self Entity>> query  )
  {
    return query.apply( null, new Query( null ) );
  }
  public static <T extends Tuple> TupleQuery<T, @Self Entity> query( Function<@Self Entity, TupleQuery<T, @Self Entity>> query  )
  {
    return query.apply( null );
  }

  public <T extends Tuple> TupleQuery<T, @Self Entity> select( T selection  ) //todo: transform this call to select( T, Expression[] tupleExprs )
  {
    //noinspection unchecked
    return new TupleQuery<>( (Class<T>)selection.getClass(), null );
  }
  public static <T extends Tuple> TupleQuery<T, @Self Entity> select( Class<T> selection, Expression[] tupleExprs )
  {
    return new TupleQuery<>( selection, tupleExprs );
  }
}
