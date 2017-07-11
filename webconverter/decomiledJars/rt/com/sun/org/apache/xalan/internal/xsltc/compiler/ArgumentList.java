package com.sun.org.apache.xalan.internal.xsltc.compiler;

final class ArgumentList
{
  private final Expression _arg;
  private final ArgumentList _rest;
  
  public ArgumentList(Expression paramExpression, ArgumentList paramArgumentList)
  {
    this._arg = paramExpression;
    this._rest = paramArgumentList;
  }
  
  public String toString()
  {
    return this._arg.toString() + ", " + this._rest.toString();
  }
}
