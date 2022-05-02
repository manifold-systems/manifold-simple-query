package manifoldsampleasttransformer.extensions.com.example.query.api.Query;

import com.example.query.api.Entity;
import com.example.query.api.Query;
import com.example.query.api.ReferenceExpression;
import com.example.query.processor.SimpleEvaluator;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends {@link Query} with {@code run(Iterable<T>)} method to handle queries on simple Iterable data sources.
 */
@Extension
public class MyQueryExtension
{
  /**
   * Executes this {@code Query} by evaluating the {@link Query#constraints} and {@link Query#orderBy} using
   * {@link SimpleEvaluator}, a simple, reflection-based expression evaluator.
   * @param dataSource An Iterable of entities.
   * @return The filtered and sorted results as an ordered List.
   */
  public static <T extends Entity> List<T> run( @This Query<T> thiz, Iterable<T> dataSource )
  {
    List<T> results = filter( thiz, dataSource );
    sort( thiz, results );
    return results;
  }

  private static <T extends Entity> List<T> filter( Query<T> thiz, Iterable<T> dataSource )
  {
    Entity[] csr = {null};
    SimpleEvaluator evaluator = new SimpleEvaluator( name -> csr[0] );
    List<T> results = new ArrayList<>();
    for( T t: dataSource )
    {
       csr[0] = t;
       if( thiz.constraints == null || (Boolean)evaluator.evaluate( thiz.constraints ) )
       {
         results.add( t );
       }
    }
    return results;
  }

  private static <T extends Entity> void sort( Query<T> thiz, List<T> results )
  {
    ReferenceExpression orderBy = thiz.orderBy;
    Entity[] csr = {null};
    SimpleEvaluator evaluator = new SimpleEvaluator( name -> csr[0] );
    if( orderBy != null )
    {
      results.sort( ( e1, e2 ) -> {
        csr[0] = e1;
        Object value1 = evaluator.evaluate( orderBy );
        csr[0] = e2;
        Object value2 = evaluator.evaluate( orderBy );
        return ((Comparable)value1).compareTo( value2 );
      } );
    }
  }
}