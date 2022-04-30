package com.example.query.api;

import manifold.ext.props.rt.api.val;
import manifold.rt.api.util.ManClassUtil;

/**
 * A direct entity property reference is transformed to an instance of this class.
 */
public class ReferenceExpression implements Expression
{
  @val String typeName;
  @val String memberName;
  @val MemberKind memberKind;

  public ReferenceExpression( String typeName, String memberName, MemberKind kind )
  {
    this.typeName = typeName;
    this.memberName = memberName;
    this.memberKind = kind;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitReferenceExpression( this );
  }

  public String toString()
  {
    return ManClassUtil.getShortClassName( typeName ) + "#" +
      memberName + (memberKind == MemberKind.Method ? "()" : "");
  }
}
