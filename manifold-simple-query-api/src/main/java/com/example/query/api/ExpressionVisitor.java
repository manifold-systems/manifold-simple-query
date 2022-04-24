package com.example.query.api;

public interface ExpressionVisitor
{
  Object visitReferenceExpression( ReferenceExpression expr );
  Object visitUnaryExpression( UnaryExpression expr );
  Object visitBinaryExpression( BinaryExpression expr );
}
