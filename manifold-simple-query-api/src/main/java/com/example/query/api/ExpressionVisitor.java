package com.example.query.api;

public interface ExpressionVisitor
{
  Object visitReference( ReferenceExpression expr );
  Object visitMethodCall( MethodCallExpression expr );
  Object visitUnary( UnaryExpression expr );
  Object visitBinary( BinaryExpression expr );
  Object visitTypeCast( TypeCastExpression expr );
}
