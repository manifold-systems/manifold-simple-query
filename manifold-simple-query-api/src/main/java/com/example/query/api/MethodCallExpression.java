package com.example.query.api;

import manifold.ext.props.rt.api.val;

public class MethodCallExpression extends Expression
{
  @val Expression receiver;
  @val String methodName;
  @val String[] paramTypes;
  @val Object[] args;

  public MethodCallExpression( Expression receiver, String methodName, String[] paramTypes, Object[] args, String type )
  {
    super( type );
    this.receiver = receiver;
    this.methodName = methodName;
    this.paramTypes = paramTypes;
    this.args = args;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitMethodCall( this );
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( receiver ).append( "." ).append( methodName ).append( '(' );
    for( int i = 0; i < args.length; i++ ) sb.append( i > 0 ? ", " : "" ).append( args[i] );
    sb.append( ')' );
    return sb.toString();
  }
}
