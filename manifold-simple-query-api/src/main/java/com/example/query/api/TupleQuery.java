package com.example.query.api;

import manifold.ext.props.rt.api.var;
import manifold.tuple.rt.api.Tuple;

import java.util.function.BiFunction;

//todo: extract an abstract super class from Query that has Query's fields so Query and TupleQuery can derive from it
public class TupleQuery<T extends Tuple, E> extends QueryBase<E>
{
  @var Class<T> selectTupleClass;
  @var Expression[] tupleExprs;

  public TupleQuery( Class<T> selectTupleClass, Expression[] tupleExprs )
  {
    super( null );
    this.selectTupleClass = selectTupleClass;
    this.tupleExprs = tupleExprs;
  }

  public TupleQuery<T, E> from( BiFunction<T, TupleQuery<T, E>, TupleQuery<T, E>> query  )
  {
    query.apply( null, this );
    return this;
  }
}
