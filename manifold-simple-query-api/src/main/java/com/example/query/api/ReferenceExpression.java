package com.example.query.api;

import manifold.ext.props.rt.api.val;
import manifold.rt.api.util.ManClassUtil;

/**
 * A direct entity property reference is transformed to an instance of this class.
 */
public class ReferenceExpression extends Expression
{
  @val String memberName;
  @val MemberKind memberKind;

  public ReferenceExpression( String type, String memberName, MemberKind kind )
  {
    super( type );
    this.memberName = memberName;
    this.memberKind = kind;
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitReference( this );
  }

  public String toString()
  {
    return ManClassUtil.getShortClassName( type ) + "#" +
      memberName + (memberKind == MemberKind.Method ? "()" : "");
  }
}
