package com.sun.org.apache.xpath.internal;

public abstract interface XPathVisitable
{
  public abstract void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor);
}
