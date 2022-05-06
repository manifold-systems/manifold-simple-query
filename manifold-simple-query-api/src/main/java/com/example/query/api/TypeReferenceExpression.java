package com.example.query.api;

/**
 * A direct type reference is transformed to an instance of this class.
 */
public class TypeReferenceExpression extends Expression
{
  public TypeReferenceExpression( String typeName )
  {
    super( typeName );
  }

  @Override
  public Object accept( ExpressionVisitor visitor )
  {
    return visitor.visitTypeReference( this );
  }

  public String toString()
  {
    return type;
  }
}
