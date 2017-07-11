package com.sun.corba.se.impl.protocol;

public class RequestCanceledException
  extends RuntimeException
{
  private int requestId = 0;
  
  public RequestCanceledException(int paramInt)
  {
    this.requestId = paramInt;
  }
  
  public int getRequestId()
  {
    return this.requestId;
  }
}
