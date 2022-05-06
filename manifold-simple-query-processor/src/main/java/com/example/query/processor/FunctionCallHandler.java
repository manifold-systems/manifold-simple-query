package com.example.query.processor;

import com.example.query.api.MethodCallExpression;
import manifold.util.ReflectUtil;

import java.util.Arrays;

/**
 * Simple function handler
 */
public class FunctionCallHandler
{
  public static final Object UNHANDLED = new Object() {};

  /**
   * Dispatches a function call. Do whatever you like here. The main idea is that the functions for queries on data
   * sources external to Java can be managed separately. Most important is the {@link MethodCallExpression}, which
   * abstracts function calls so they can be mapped to a query model suitable for external data sources (SQL, JSON,
   * etc.) where they execute within the external system. As such, the domain of function calls should be limited to the
   * functions available to the query's targeted data source[s].
   */
  public static Object invoke( Object receiver, String name, String[] paramTypes, Object[] args )
  {
    Object result = UNHANDLED;
    switch( name )
    {
      // just to demonstrate specific methods can be handled separately
      case "compareTo":
        result = compareTo( receiver, args );
        break;
      case "contains":
        result = contains( receiver, args );
        break;
    }
    if( result == UNHANDLED )
    {
      // use reflection to make the call
      result = handleAnyCall( receiver, name, paramTypes, args );
    }
    return result;
  }

  private static Object handleAnyCall( Object receiver, String name, String[] paramTypeNames, Object[] args )
  {
    Class<?>[] paramTypes = Arrays.stream( paramTypeNames ).map( ( String fqn ) -> fqn.equals("Array[]") ? Object[].class : ReflectUtil.type( fqn ) ).toArray( Class[]::new );
    if( receiver instanceof Class )
    {
      return ReflectUtil.method( (Class<?>)receiver, name, paramTypes ).invokeStatic( args );
    }
    return ReflectUtil.method( receiver, name, paramTypes ).invoke( args );
  }

  private static Object contains( Object receiver, Object[] args )
  {
    if( receiver instanceof String )
    {
      return ((String)receiver).contains( (CharSequence)args[0] );
    }
    return UNHANDLED;
  }

  private static Object compareTo( Object receiver, Object[] args )
  {
    if( receiver instanceof Comparable )
    {
      //noinspection unchecked,rawtypes
      return ((Comparable)receiver).compareTo( args[0] );
    }
    return UNHANDLED;
  }
}
