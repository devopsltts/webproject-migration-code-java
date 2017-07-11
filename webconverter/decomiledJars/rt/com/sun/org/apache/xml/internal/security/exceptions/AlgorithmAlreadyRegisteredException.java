package com.sun.org.apache.xml.internal.security.exceptions;

public class AlgorithmAlreadyRegisteredException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  
  public AlgorithmAlreadyRegisteredException() {}
  
  public AlgorithmAlreadyRegisteredException(String paramString)
  {
    super(paramString);
  }
  
  public AlgorithmAlreadyRegisteredException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public AlgorithmAlreadyRegisteredException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public AlgorithmAlreadyRegisteredException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}
