package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTUserPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -8737649811939033735L;
  private String name;
  
  public NTUserPrincipal(String paramString)
  {
    if (paramString == null)
    {
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "name" };
      throw new NullPointerException(localMessageFormat.format(arrayOfObject));
    }
    this.name = paramString;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String toString()
  {
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("NTUserPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { this.name };
    return localMessageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof NTUserPrincipal)) {
      return false;
    }
    NTUserPrincipal localNTUserPrincipal = (NTUserPrincipal)paramObject;
    return this.name.equals(localNTUserPrincipal.getName());
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
}
