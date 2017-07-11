package com.sun.org.apache.xpath.internal;

public abstract interface ExpressionOwner
{
  public abstract Expression getExpression();
  
  public abstract void setExpression(Expression paramExpression);
}
