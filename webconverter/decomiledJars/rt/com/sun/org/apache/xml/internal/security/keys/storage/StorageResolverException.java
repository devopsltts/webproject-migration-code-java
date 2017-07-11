package com.sun.org.apache.xml.internal.security.keys.storage;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class StorageResolverException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  
  public StorageResolverException() {}
  
  public StorageResolverException(String paramString)
  {
    super(paramString);
  }
  
  public StorageResolverException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public StorageResolverException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public StorageResolverException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}
