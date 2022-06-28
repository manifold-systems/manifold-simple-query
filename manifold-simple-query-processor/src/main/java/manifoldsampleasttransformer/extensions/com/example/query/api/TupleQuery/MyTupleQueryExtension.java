package manifoldsampleasttransformer.extensions.com.example.query.api.TupleQuery;

import com.example.query.api.Expression;
import com.example.query.api.Query;
import com.example.query.api.ReferenceExpression;
import com.example.query.api.TupleQuery;
import com.example.query.processor.SimpleEvaluator;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import manifold.tuple.rt.api.Tuple;
import manifold.util.ManExceptionUtil;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extends {@link TupleQuery} with {@code run(Iterable<T>)} method to handle queries on simple Iterable data sources.
 */
@Extension
public class MyTupleQueryExtension
{
  /**
   * Executes this {@code Query} by evaluating the {@link Query#constraints} and {@link Query#orderBy} using
   * {@link SimpleEvaluator}, a simple, reflection-based expression evaluator.
   * @param dataSource An Iterable of entities.
   * @return The filtered and sorted results as an ordered List.
   */
  public static <T extends Tuple, E> List<T> run( @This TupleQuery<T, E> thiz, Iterable<E> dataSource )
  {
    List<E> results = filter( thiz.selectTupleClass, thiz.tupleExprs, thiz.constraints, dataSource );
    sort( thiz.selectTupleClass, thiz.tupleExprs, thiz.orderBy, results );
    return map( thiz.selectTupleClass, thiz.tupleExprs, results );
  }

  private static <E> List<E> filter( Class<? extends Tuple> tupleClass, Expression[] tupleExprs, Expression constraints, Iterable<E> dataSource )
  {
    Object[] csr = {null, null};
    SimpleEvaluator evaluator = new SimpleEvaluator( name -> name.contains( "manifold_tuple_" ) ? csr[0] : csr[1] );
    List<E> results = new ArrayList<>();
    Constructor<?> ctor = tupleClass.getConstructors()[0];
    for( E e: dataSource )
    {
       csr[0] = constructTuple( e, ctor, tupleExprs );
       csr[1] = e;
       if( constraints == null || (Boolean)evaluator.evaluate( constraints ) )
       {
         results.add( e );
       }
    }
    return results;
  }

  private static <E> void sort( Class<? extends Tuple> tupleClass, Expression[] tupleExprs, ReferenceExpression orderBy, List<E> results )
  {
    Object[] csr = {null, null};
    SimpleEvaluator evaluator = new SimpleEvaluator( name -> name.contains( "manifold_tuple_" ) ? csr[0] : csr[1] );
    Constructor<?> ctor = tupleClass.getConstructors()[0];
    if( orderBy != null )
    {
      results.sort( ( e1, e2 ) -> {
        csr[0] = constructTuple( e1, ctor, tupleExprs );
        csr[1] = e1;
        Object value1 = evaluator.evaluate( orderBy );

        csr[0] = constructTuple( e2, ctor, tupleExprs );
        csr[1] = e2;
        Object value2 = evaluator.evaluate( orderBy );

        //noinspection unchecked,rawtypes
        return ((Comparable)value1).compareTo( value2 );
      } );
    }
  }

  private static <T extends Tuple, E> List<T> map( Class<T> select, Expression[] tupleExprs, List<E> results )
  {
    Constructor<?> ctor = select.getConstructors()[0];
    //noinspection unchecked
    return (List<T>)results.stream()
      .map( e -> constructTuple( e, ctor, tupleExprs ) )
      .collect( Collectors.toList() );
  }

  private static <E> Tuple constructTuple( E e, Constructor<?> ctor, Expression[] tupleExprs )
  {
    SimpleEvaluator evaluator = new SimpleEvaluator( name -> e );
    Object[] params = new Object[ctor.getParameterCount()];
    for( int i = 0; i < tupleExprs.length; i++ )
    {
      Expression expr = tupleExprs[i];
      Object value = evaluator.evaluate( expr );
      params[i] = value;
    }
    try
    {
      return (Tuple)ctor.newInstance( params );
    }
    catch( Exception ex )
    {
      throw ManExceptionUtil.unchecked( ex );
    }
  }
}