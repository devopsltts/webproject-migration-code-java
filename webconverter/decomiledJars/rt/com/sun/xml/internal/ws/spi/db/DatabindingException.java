package com.sun.xml.internal.ws.spi.db;

public class DatabindingException
  extends RuntimeException
{
  public DatabindingException() {}
  
  public DatabindingException(String paramString)
  {
    super(paramString);
  }
  
  public DatabindingException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public DatabindingException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
