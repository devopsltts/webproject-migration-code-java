package com.sun.xml.internal.ws.api.model;

public enum ExceptionType
{
  WSDLException(0),  UserDefined(1);
  
  private final int exceptionType;
  
  private ExceptionType(int paramInt)
  {
    this.exceptionType = paramInt;
  }
  
  public int value()
  {
    return this.exceptionType;
  }
}
