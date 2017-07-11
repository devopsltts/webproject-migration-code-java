package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.XMLEntityDescription;

public class XMLEntityDescriptionImpl
  extends XMLResourceIdentifierImpl
  implements XMLEntityDescription
{
  protected String fEntityName;
  
  public XMLEntityDescriptionImpl() {}
  
  public XMLEntityDescriptionImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    setDescription(paramString1, paramString2, paramString3, paramString4, paramString5);
  }
  
  public XMLEntityDescriptionImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    setDescription(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6);
  }
  
  public void setEntityName(String paramString)
  {
    this.fEntityName = paramString;
  }
  
  public String getEntityName()
  {
    return this.fEntityName;
  }
  
  public void setDescription(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    setDescription(paramString1, paramString2, paramString3, paramString4, paramString5, null);
  }
  
  public void setDescription(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    this.fEntityName = paramString1;
    setValues(paramString2, paramString3, paramString4, paramString5, paramString6);
  }
  
  public void clear()
  {
    super.clear();
    this.fEntityName = null;
  }
  
  public int hashCode()
  {
    int i = super.hashCode();
    if (this.fEntityName != null) {
      i += this.fEntityName.hashCode();
    }
    return i;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (this.fEntityName != null) {
      localStringBuffer.append(this.fEntityName);
    }
    localStringBuffer.append(':');
    if (this.fPublicId != null) {
      localStringBuffer.append(this.fPublicId);
    }
    localStringBuffer.append(':');
    if (this.fLiteralSystemId != null) {
      localStringBuffer.append(this.fLiteralSystemId);
    }
    localStringBuffer.append(':');
    if (this.fBaseSystemId != null) {
      localStringBuffer.append(this.fBaseSystemId);
    }
    localStringBuffer.append(':');
    if (this.fExpandedSystemId != null) {
      localStringBuffer.append(this.fExpandedSystemId);
    }
    localStringBuffer.append(':');
    if (this.fNamespace != null) {
      localStringBuffer.append(this.fNamespace);
    }
    return localStringBuffer.toString();
  }
}
