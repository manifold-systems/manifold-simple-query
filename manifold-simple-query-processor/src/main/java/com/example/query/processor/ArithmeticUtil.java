package com.example.query.processor;

import java.math.BigDecimal;

public class ArithmeticUtil
{
  public static Object plus( Object left, Object right )
  {
    if( left == null || right == null )
    {
      throw new NullPointerException( "Null operand not supported in arithmetic expression." );
    }
    
    if( left instanceof Byte )
    {
      if( right instanceof Byte )
      {
        return (Byte)left + (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Byte)left + (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Byte)left + (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Byte)left + (Long)right;
      }
      if( right instanceof Float )
      {
        return (Byte)left + (Float)right;
      }
      if( right instanceof Double )
      {
        return (Byte)left + (Double)right;
      }
      if( right instanceof Character )
      {
        return (Byte)left + (Character)right;
      }
      if( right instanceof String )
      {
        return left + (String)right;
      }

      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Short )
    {
      if( right instanceof Byte )
      {
        return (Short)left + (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Short)left + (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Short)left + (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Short)left + (Long)right;
      }
      if( right instanceof Float )
      {
        return (Short)left + (Float)right;
      }
      if( right instanceof Double )
      {
        return (Short)left + (Double)right;
      }
      if( right instanceof Character )
      {
        return (Short)left + (Character)right;
      }
      if( right instanceof String )
      {
        return left + (String)right;
      }

      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Integer )
    {
      if( right instanceof Byte )
      {
        return (Integer)left + (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Integer)left + (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Integer)left + (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Integer)left + (Long)right;
      }
      if( right instanceof Float )
      {
        return (Integer)left + (Float)right;
      }
      if( right instanceof Double )
      {
        return (Integer)left + (Double)right;
      }
      if( right instanceof Character )
      {
        return (Integer)left + (Character)right;
      }
      if( right instanceof String )
      {
        return left + (String)right;
      }

      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Long )
    {
      if( right instanceof Byte )
      {
        return (Long)left + (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Long)left + (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Long)left + (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Long)left + (Long)right;
      }
      if( right instanceof Float )
      {
        return (Long)left + (Float)right;
      }
      if( right instanceof Double )
      {
        return (Long)left + (Double)right;
      }
      if( right instanceof Character )
      {
        return (Long)left + (Character)right;
      }
      if( right instanceof String )
      {
        return left + (String)right;
      }

      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Float )
    {
      if( right instanceof Byte )
      {
        return (Float)left + (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Float)left + (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Float)left + (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Float)left + (Long)right;
      }
      if( right instanceof Float )
      {
        return (Float)left + (Float)right;
      }
      if( right instanceof Double )
      {
        return (Float)left + (Double)right;
      }
      if( right instanceof Character )
      {
        return (Float)left + (Character)right;
      }
      if( right instanceof String )
      {
        return left + (String)right;
      }

      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Double )
    {
      if( right instanceof Byte )
      {
        return (Double)left + (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Double)left + (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Double)left + (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Double)left + (Long)right;
      }
      if( right instanceof Float )
      {
        return (Double)left + (Float)right;
      }
      if( right instanceof Double )
      {
        return (Double)left + (Double)right;
      }
      if( right instanceof Character )
      {
        return (Double)left + (Character)right;
      }
      if( right instanceof String )
      {
        return left + (String)right;
      }

      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Character )
    {
      if( right instanceof Byte )
      {
        return (Character)left + (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Character)left + (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Character)left + (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Character)left + (Long)right;
      }
      if( right instanceof Float )
      {
        return (Character)left + (Float)right;
      }
      if( right instanceof Double )
      {
        return (Character)left + (Double)right;
      }
      if( right instanceof Character )
      {
        return (Character)left + (Character)right;
      }
      if( right instanceof String )
      {
        return left + (String)right;
      }

      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof String )
    {
      return ((String)left) + right;
    }

    throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + left.getClass().getTypeName() );
  }

  public static Object minus( Object left, Object right )
  {
    if( left == null || right == null )
    {
      throw new NullPointerException( "Null operand not supported in arithmetic expression." );
    }

    if( left instanceof Byte )
    {
      if( right instanceof Byte )
      {
        return (Byte)left - (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Byte)left - (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Byte)left - (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Byte)left - (Long)right;
      }
      if( right instanceof Float )
      {
        return (Byte)left - (Float)right;
      }
      if( right instanceof Double )
      {
        return (Byte)left - (Double)right;
      }
      if( right instanceof Character )
      {
        return (Byte)left - (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Short )
    {
      if( right instanceof Byte )
      {
        return (Short)left - (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Short)left - (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Short)left - (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Short)left - (Long)right;
      }
      if( right instanceof Float )
      {
        return (Short)left - (Float)right;
      }
      if( right instanceof Double )
      {
        return (Short)left - (Double)right;
      }
      if( right instanceof Character )
      {
        return (Short)left - (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Integer )
    {
      if( right instanceof Byte )
      {
        return (Integer)left - (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Integer)left - (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Integer)left - (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Integer)left - (Long)right;
      }
      if( right instanceof Float )
      {
        return (Integer)left - (Float)right;
      }
      if( right instanceof Double )
      {
        return (Integer)left - (Double)right;
      }
      if( right instanceof Character )
      {
        return (Integer)left - (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Long )
    {
      if( right instanceof Byte )
      {
        return (Long)left - (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Long)left - (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Long)left - (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Long)left - (Long)right;
      }
      if( right instanceof Float )
      {
        return (Long)left - (Float)right;
      }
      if( right instanceof Double )
      {
        return (Long)left - (Double)right;
      }
      if( right instanceof Character )
      {
        return (Long)left - (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Float )
    {
      if( right instanceof Byte )
      {
        return (Float)left - (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Float)left - (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Float)left - (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Float)left - (Long)right;
      }
      if( right instanceof Float )
      {
        return (Float)left - (Float)right;
      }
      if( right instanceof Double )
      {
        return (Float)left - (Double)right;
      }
      if( right instanceof Character )
      {
        return (Float)left - (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Double )
    {
      if( right instanceof Byte )
      {
        return (Double)left - (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Double)left - (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Double)left - (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Double)left - (Long)right;
      }
      if( right instanceof Float )
      {
        return (Double)left - (Float)right;
      }
      if( right instanceof Double )
      {
        return (Double)left - (Double)right;
      }
      if( right instanceof Character )
      {
        return (Double)left - (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Character )
    {
      if( right instanceof Byte )
      {
        return (Character)left - (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Character)left - (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Character)left - (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Character)left - (Long)right;
      }
      if( right instanceof Float )
      {
        return (Character)left - (Float)right;
      }
      if( right instanceof Double )
      {
        return (Character)left - (Double)right;
      }
      if( right instanceof Character )
      {
        return (Character)left - (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else
    {
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + left.getClass().getTypeName() );
    }
  }

  public static Object times( Object left, Object right )
  {
    if( left == null || right == null )
    {
      throw new NullPointerException( "Null operand not supported in arithmetic expression." );
    }

    if( left instanceof Byte )
    {
      if( right instanceof Byte )
      {
        return (Byte)left * (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Byte)left * (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Byte)left * (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Byte)left * (Long)right;
      }
      if( right instanceof Float )
      {
        return (Byte)left * (Float)right;
      }
      if( right instanceof Double )
      {
        return (Byte)left * (Double)right;
      }
      if( right instanceof Character )
      {
        return (Byte)left * (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Short )
    {
      if( right instanceof Byte )
      {
        return (Short)left * (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Short)left * (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Short)left * (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Short)left * (Long)right;
      }
      if( right instanceof Float )
      {
        return (Short)left * (Float)right;
      }
      if( right instanceof Double )
      {
        return (Short)left * (Double)right;
      }
      if( right instanceof Character )
      {
        return (Short)left * (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Integer )
    {
      if( right instanceof Byte )
      {
        return (Integer)left * (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Integer)left * (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Integer)left * (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Integer)left * (Long)right;
      }
      if( right instanceof Float )
      {
        return (Integer)left * (Float)right;
      }
      if( right instanceof Double )
      {
        return (Integer)left * (Double)right;
      }
      if( right instanceof Character )
      {
        return (Integer)left * (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Long )
    {
      if( right instanceof Byte )
      {
        return (Long)left * (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Long)left * (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Long)left * (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Long)left * (Long)right;
      }
      if( right instanceof Float )
      {
        return (Long)left * (Float)right;
      }
      if( right instanceof Double )
      {
        return (Long)left * (Double)right;
      }
      if( right instanceof Character )
      {
        return (Long)left * (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Float )
    {
      if( right instanceof Byte )
      {
        return (Float)left * (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Float)left * (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Float)left * (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Float)left * (Long)right;
      }
      if( right instanceof Float )
      {
        return (Float)left * (Float)right;
      }
      if( right instanceof Double )
      {
        return (Float)left * (Double)right;
      }
      if( right instanceof Character )
      {
        return (Float)left * (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Double )
    {
      if( right instanceof Byte )
      {
        return (Double)left * (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Double)left * (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Double)left * (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Double)left * (Long)right;
      }
      if( right instanceof Float )
      {
        return (Double)left * (Float)right;
      }
      if( right instanceof Double )
      {
        return (Double)left * (Double)right;
      }
      if( right instanceof Character )
      {
        return (Double)left * (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Character )
    {
      if( right instanceof Byte )
      {
        return (Character)left * (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Character)left * (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Character)left * (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Character)left * (Long)right;
      }
      if( right instanceof Float )
      {
        return (Character)left * (Float)right;
      }
      if( right instanceof Double )
      {
        return (Character)left * (Double)right;
      }
      if( right instanceof Character )
      {
        return (Character)left * (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else
    {
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + left.getClass().getTypeName() );
    }
  }

  public static Object div( Object left, Object right )
  {
    if( left == null || right == null )
    {
      throw new NullPointerException( "Null operand not supported in arithmetic expression." );
    }

    if( left instanceof Byte )
    {
      if( right instanceof Byte )
      {
        return (Byte)left / (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Byte)left / (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Byte)left / (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Byte)left / (Long)right;
      }
      if( right instanceof Float )
      {
        return (Byte)left / (Float)right;
      }
      if( right instanceof Double )
      {
        return (Byte)left / (Double)right;
      }
      if( right instanceof Character )
      {
        return (Byte)left / (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Short )
    {
      if( right instanceof Byte )
      {
        return (Short)left / (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Short)left / (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Short)left / (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Short)left / (Long)right;
      }
      if( right instanceof Float )
      {
        return (Short)left / (Float)right;
      }
      if( right instanceof Double )
      {
        return (Short)left / (Double)right;
      }
      if( right instanceof Character )
      {
        return (Short)left / (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Integer )
    {
      if( right instanceof Byte )
      {
        return (Integer)left / (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Integer)left / (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Integer)left / (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Integer)left / (Long)right;
      }
      if( right instanceof Float )
      {
        return (Integer)left / (Float)right;
      }
      if( right instanceof Double )
      {
        return (Integer)left / (Double)right;
      }
      if( right instanceof Character )
      {
        return (Integer)left / (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Long )
    {
      if( right instanceof Byte )
      {
        return (Long)left / (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Long)left / (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Long)left / (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Long)left / (Long)right;
      }
      if( right instanceof Float )
      {
        return (Long)left / (Float)right;
      }
      if( right instanceof Double )
      {
        return (Long)left / (Double)right;
      }
      if( right instanceof Character )
      {
        return (Long)left / (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Float )
    {
      if( right instanceof Byte )
      {
        return (Float)left / (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Float)left / (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Float)left / (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Float)left / (Long)right;
      }
      if( right instanceof Float )
      {
        return (Float)left / (Float)right;
      }
      if( right instanceof Double )
      {
        return (Float)left / (Double)right;
      }
      if( right instanceof Character )
      {
        return (Float)left / (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Double )
    {
      if( right instanceof Byte )
      {
        return (Double)left / (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Double)left / (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Double)left / (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Double)left / (Long)right;
      }
      if( right instanceof Float )
      {
        return (Double)left / (Float)right;
      }
      if( right instanceof Double )
      {
        return (Double)left / (Double)right;
      }
      if( right instanceof Character )
      {
        return (Double)left / (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Character )
    {
      if( right instanceof Byte )
      {
        return (Character)left / (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Character)left / (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Character)left / (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Character)left / (Long)right;
      }
      if( right instanceof Float )
      {
        return (Character)left / (Float)right;
      }
      if( right instanceof Double )
      {
        return (Character)left / (Double)right;
      }
      if( right instanceof Character )
      {
        return (Character)left / (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else
    {
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + left.getClass().getTypeName() );
    }
  }

  public static Object rem( Object left, Object right )
  {
    if( left == null || right == null )
    {
      throw new NullPointerException( "Null operand not supported in arithmetic expression." );
    }

    if( left instanceof Byte )
    {
      if( right instanceof Byte )
      {
        return (Byte)left % (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Byte)left % (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Byte)left % (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Byte)left % (Long)right;
      }
      if( right instanceof Float )
      {
        return (Byte)left % (Float)right;
      }
      if( right instanceof Double )
      {
        return (Byte)left % (Double)right;
      }
      if( right instanceof Character )
      {
        return (Byte)left % (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Short )
    {
      if( right instanceof Byte )
      {
        return (Short)left % (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Short)left % (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Short)left % (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Short)left % (Long)right;
      }
      if( right instanceof Float )
      {
        return (Short)left % (Float)right;
      }
      if( right instanceof Double )
      {
        return (Short)left % (Double)right;
      }
      if( right instanceof Character )
      {
        return (Short)left % (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Integer )
    {
      if( right instanceof Byte )
      {
        return (Integer)left % (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Integer)left % (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Integer)left % (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Integer)left % (Long)right;
      }
      if( right instanceof Float )
      {
        return (Integer)left % (Float)right;
      }
      if( right instanceof Double )
      {
        return (Integer)left % (Double)right;
      }
      if( right instanceof Character )
      {
        return (Integer)left % (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Long )
    {
      if( right instanceof Byte )
      {
        return (Long)left % (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Long)left % (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Long)left % (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Long)left % (Long)right;
      }
      if( right instanceof Float )
      {
        return (Long)left % (Float)right;
      }
      if( right instanceof Double )
      {
        return (Long)left % (Double)right;
      }
      if( right instanceof Character )
      {
        return (Long)left % (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Float )
    {
      if( right instanceof Byte )
      {
        return (Float)left % (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Float)left % (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Float)left % (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Float)left % (Long)right;
      }
      if( right instanceof Float )
      {
        return (Float)left % (Float)right;
      }
      if( right instanceof Double )
      {
        return (Float)left % (Double)right;
      }
      if( right instanceof Character )
      {
        return (Float)left % (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Double )
    {
      if( right instanceof Byte )
      {
        return (Double)left % (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Double)left % (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Double)left % (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Double)left % (Long)right;
      }
      if( right instanceof Float )
      {
        return (Double)left % (Float)right;
      }
      if( right instanceof Double )
      {
        return (Double)left % (Double)right;
      }
      if( right instanceof Character )
      {
        return (Double)left % (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else if( left instanceof Character )
    {
      if( right instanceof Byte )
      {
        return (Character)left % (Byte)right;
      }
      if( right instanceof Short )
      {
        return (Character)left % (Short)right;
      }
      if( right instanceof Integer )
      {
        return (Character)left % (Integer)right;
      }
      if( right instanceof Long )
      {
        return (Character)left % (Long)right;
      }
      if( right instanceof Float )
      {
        return (Character)left % (Float)right;
      }
      if( right instanceof Double )
      {
        return (Character)left % (Double)right;
      }
      if( right instanceof Character )
      {
        return (Character)left % (Character)right;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + right.getClass().getTypeName() );
    }
    else
    {
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + left.getClass().getTypeName() );
    }
  }
  
  public static Object coerce( Object value, Class<?> type )
  {
    if( value == null  )
    {
      return null;
    }

    type = nonPrimitiveTypeFor( type );

    if( type == Byte.class )
    {
      if( value instanceof Byte )
      {
        return value;
      }
      if( value instanceof Short )
      {
        return ((Short)value).byteValue();
      }
      if( value instanceof Integer )
      {
        return ((Integer)value).byteValue();
      }
      if( value instanceof Long )
      {
        return ((Long)value).byteValue();
      }
      if( value instanceof Float )
      {
        return ((Float)value).byteValue();
      }
      if( value instanceof Double )
      {
        return ((Double)value).byteValue();
      }
      if( value instanceof Character )
      {
        return (byte)((Character)value).charValue();
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + value.getClass().getTypeName() );
    }
    else if( type == Short.class )
    {
      if( value instanceof Byte )
      {
        return ((Byte)value).shortValue();
      }
      if( value instanceof Short )
      {
        return value;
      }
      if( value instanceof Integer )
      {
        return ((Integer)value).shortValue();
      }
      if( value instanceof Long )
      {
        return ((Long)value).shortValue();
      }
      if( value instanceof Float )
      {
        return ((Float)value).shortValue();
      }
      if( value instanceof Double )
      {
        return ((Double)value).shortValue();
      }
      if( value instanceof Character )
      {
        return (short)((Character)value).charValue();
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + value.getClass().getTypeName() );
    }
    else if( type == Integer.class )
    {
      if( value instanceof Byte )
      {
        return ((Byte)value).intValue();
      }
      if( value instanceof Short )
      {
        return ((Short)value).intValue();
      }
      if( value instanceof Integer )
      {
        return value;
      }
      if( value instanceof Long )
      {
        return ((Long)value).intValue();
      }
      if( value instanceof Float )
      {
        return ((Float)value).intValue();
      }
      if( value instanceof Double )
      {
        return ((Double)value).intValue();
      }
      if( value instanceof Character )
      {
        return (int)(Character)value;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + value.getClass().getTypeName() );
    }
    else if( type == Long.class )
    {
      if( value instanceof Byte )
      {
        return ((Byte)value).longValue();
      }
      if( value instanceof Short )
      {
        return ((Short)value).longValue();
      }
      if( value instanceof Integer )
      {
        return ((Integer)value).longValue();
      }
      if( value instanceof Long )
      {
        return value;
      }
      if( value instanceof Float )
      {
        return ((Float)value).longValue();
      }
      if( value instanceof Double )
      {
        return ((Double)value).longValue();
      }
      if( value instanceof Character )
      {
        return (long)(Character)value;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + value.getClass().getTypeName() );
    }
    else if( type == Float.class )
    {
      if( value instanceof Byte )
      {
        return ((Byte)value).floatValue();
      }
      if( value instanceof Short )
      {
        return ((Short)value).floatValue();
      }
      if( value instanceof Integer )
      {
        return ((Integer)value).floatValue();
      }
      if( value instanceof Long )
      {
        return ((Long)value).floatValue();
      }
      if( value instanceof Float )
      {
        return value;
      }
      if( value instanceof Double )
      {
        return ((Double)value).floatValue();
      }
      if( value instanceof Character )
      {
        return (float)(Character)value;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + value.getClass().getTypeName() );
    }
    else if( type == Double.class )
    {
      if( value instanceof Byte )
      {
        return ((Byte)value).doubleValue();
      }
      if( value instanceof Short )
      {
        return ((Short)value).doubleValue();
      }
      if( value instanceof Integer )
      {
        return ((Integer)value).doubleValue();
      }
      if( value instanceof Long )
      {
        return ((Long)value).doubleValue();
      }
      if( value instanceof Float )
      {
        return ((Float)value).doubleValue();
      }
      if( value instanceof Double )
      {
        return value;
      }
      if( value instanceof Character )
      {
        return (double)(Character)value;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + value.getClass().getTypeName() );
    }
    else if( type == Character.class )
    {
      if( value instanceof Byte )
      {
        return (char)((Byte)value).intValue();
      }
      if( value instanceof Short )
      {
        return (char)((Short)value).intValue();
      }
      if( value instanceof Integer )
      {
        return (char)((Integer)value).intValue();
      }
      if( value instanceof Long )
      {
        return (char)((Long)value).intValue();
      }
      if( value instanceof Float )
      {
        return (char)((Float)value).intValue();
      }
      if( value instanceof Double )
      {
        return (char)((Double)value).intValue();
      }
      if( value instanceof Character )
      {
        return value;
      }
      throw new UnsupportedOperationException( "Unsupported arithmetic operand of type: " + value.getClass().getTypeName() );
    }
    return value;
  }

  public static Class<?> nonPrimitiveTypeFor( Class<?> type )
  {
    if( !type.isPrimitive() )
    {
      return type;
    }

    switch( type.getSimpleName() )
    {
      case "void":
        return Void.class;
      case "boolean":
        return Boolean.class;
      case "char":
        return Character.class;
      case "byte":
        return Byte.class;
      case "short":
        return Short.class;
      case "int":
        return Integer.class;
      case "long":
        return Long.class;
      case "float":
        return Float.class;
      case "double":
        return Double.class;
      default:
        throw new IllegalStateException();
    }
  }

  public static boolean equals( Object leftValue, Object rightValue )
  {
    if( leftValue == rightValue )
    {
      return true;
    }
    if( leftValue == null || rightValue == null )
    {
      return false;
    }

    Class<?> lclass = leftValue.getClass();
    Class<?> rclass = rightValue.getClass();
    if( lclass != rclass )
    {
      if( leftValue instanceof Number )
      {
        if( rightValue instanceof Number )
        {
          leftValue = new BigDecimal( leftValue.toString() );
          rightValue = new BigDecimal( rightValue.toString() );
          return ((BigDecimal)leftValue).compareTo( (BigDecimal)rightValue ) == 0;
        }
        else if( rightValue instanceof Character )
        {
          leftValue = new BigDecimal( leftValue.toString() );
          rightValue = new BigDecimal( String.valueOf( (double)(char)rightValue ) );
          return ((BigDecimal)leftValue).compareTo( (BigDecimal)rightValue ) == 0;
        }
      }
      else if( leftValue instanceof Character )
      {
        if( rightValue instanceof Number )
        {
          leftValue = new BigDecimal( String.valueOf( (double)(char)leftValue ) );
          rightValue = new BigDecimal( rightValue.toString() );
          return ((BigDecimal)leftValue).compareTo( (BigDecimal)rightValue ) == 0;
        }
      }
    }
    return leftValue.equals( rightValue );
  }

  public static int compareTo( Object leftValue, Object rightValue )
  {
    if( leftValue == rightValue )
    {
      return 0;
    }
    if( leftValue == null || rightValue == null )
    {
      throw new NullPointerException();
    }

    Class<?> lclass = leftValue.getClass();
    Class<?> rclass = rightValue.getClass();
    if( lclass != rclass )
    {
      if( leftValue instanceof Number )
      {
        if( rightValue instanceof Number )
        {
          leftValue = new BigDecimal( leftValue.toString() );
          rightValue = new BigDecimal( rightValue.toString() );
        }
        else if( rightValue instanceof Character )
        {
          leftValue = new BigDecimal( leftValue.toString() );
          rightValue = new BigDecimal( String.valueOf( (double)(char)rightValue ) );
        }
      }
      else if( leftValue instanceof Character )
      {
        if( rightValue instanceof Number )
        {
          leftValue = new BigDecimal( String.valueOf( (double)(char)leftValue ) );
          rightValue = new BigDecimal( rightValue.toString() );
        }
      }
    }
    return ((Comparable)leftValue).compareTo( rightValue );
  }
}
