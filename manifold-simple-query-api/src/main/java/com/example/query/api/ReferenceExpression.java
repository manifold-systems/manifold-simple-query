package com.example.query.api;

import manifold.ext.props.rt.api.val;
import manifold.rt.api.util.ManClassUtil;
import manifold.util.ReflectUtil;

import java.util.function.Function;

/**
 * Direct entity property references are transformed to instances of this class.
 */
public class ReferenceExpression implements Expression
{
  @val String typeName;
  @val String memberName;

  public ReferenceExpression( String typeName, String memberName )
  {
    this.typeName = typeName;
    this.memberName = memberName;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitReferenceExpression( this );
  }

  public String toString()
  {
    return ManClassUtil.getShortClassName( typeName ) + "#" + memberName;
  }
}
