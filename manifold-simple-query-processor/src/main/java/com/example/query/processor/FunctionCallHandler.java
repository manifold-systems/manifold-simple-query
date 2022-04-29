package com.example.query.processor;

/**
 * Simple function handler
 */
public class FunctionCallHandler
{
  public static Object invoke( Object receiver, String name, Object[] args )
  {
    switch( name )
    {
      case "compareTo":
        return compareTo( receiver, args );
      case "contains":
        return contains( receiver, args );
    }
    throw unhandled( receiver, name );
  }

  private static Object contains( Object receiver, Object[] args )
  {
    if( receiver instanceof String )
    {
      return ((String)receiver).contains( (CharSequence)args[0] );
    }
    throw unhandled( receiver, "contains" );
  }

  private static int compareTo( Object receiver, Object[] args )
  {
    if( receiver instanceof Comparable )
    {
      return ((Comparable)receiver).compareTo( args[0] );
    }
    throw unhandled( receiver, "compareTo" );
  }

  private static UnsupportedOperationException unhandled( Object receiver, String name )
  {
    return new UnsupportedOperationException(
      "Unhandled function: '" + name + "' on " + receiver.getClass().getTypeName() );
  }
}
