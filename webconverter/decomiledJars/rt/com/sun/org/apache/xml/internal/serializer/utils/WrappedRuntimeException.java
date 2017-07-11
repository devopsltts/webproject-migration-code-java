package com.sun.org.apache.xml.internal.serializer.utils;

public final class WrappedRuntimeException
  extends RuntimeException
{
  static final long serialVersionUID = 7140414456714658073L;
  private Exception m_exception;
  
  public WrappedRuntimeException(Exception paramException)
  {
    super(paramException.getMessage());
    this.m_exception = paramException;
  }
  
  public WrappedRuntimeException(String paramString, Exception paramException)
  {
    super(paramString);
    this.m_exception = paramException;
  }
  
  public Exception getException()
  {
    return this.m_exception;
  }
}
