package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class XMLConfigurationException
  extends XNIException
{
  static final long serialVersionUID = -5437427404547669188L;
  protected Status fType;
  protected String fIdentifier;
  
  public XMLConfigurationException(Status paramStatus, String paramString)
  {
    super(paramString);
    this.fType = paramStatus;
    this.fIdentifier = paramString;
  }
  
  public XMLConfigurationException(Status paramStatus, String paramString1, String paramString2)
  {
    super(paramString2);
    this.fType = paramStatus;
    this.fIdentifier = paramString1;
  }
  
  public Status getType()
  {
    return this.fType;
  }
  
  public String getIdentifier()
  {
    return this.fIdentifier;
  }
}
