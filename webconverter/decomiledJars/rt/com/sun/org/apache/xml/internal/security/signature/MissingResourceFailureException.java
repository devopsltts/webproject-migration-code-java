package com.sun.org.apache.xml.internal.security.signature;

public class MissingResourceFailureException
  extends XMLSignatureException
{
  private static final long serialVersionUID = 1L;
  private Reference uninitializedReference = null;
  
  public MissingResourceFailureException(String paramString, Reference paramReference)
  {
    super(paramString);
    this.uninitializedReference = paramReference;
  }
  
  public MissingResourceFailureException(String paramString, Object[] paramArrayOfObject, Reference paramReference)
  {
    super(paramString, paramArrayOfObject);
    this.uninitializedReference = paramReference;
  }
  
  public MissingResourceFailureException(String paramString, Exception paramException, Reference paramReference)
  {
    super(paramString, paramException);
    this.uninitializedReference = paramReference;
  }
  
  public MissingResourceFailureException(String paramString, Object[] paramArrayOfObject, Exception paramException, Reference paramReference)
  {
    super(paramString, paramArrayOfObject, paramException);
    this.uninitializedReference = paramReference;
  }
  
  public void setReference(Reference paramReference)
  {
    this.uninitializedReference = paramReference;
  }
  
  public Reference getReference()
  {
    return this.uninitializedReference;
  }
}
