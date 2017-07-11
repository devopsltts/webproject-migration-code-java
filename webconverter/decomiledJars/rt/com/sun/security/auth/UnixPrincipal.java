package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class UnixPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -2951667807323493631L;
  private String name;
  
  public UnixPrincipal(String paramString)
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
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("UnixPrincipal.name", "sun.security.util.AuthResources"));
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
    if (!(paramObject instanceof UnixPrincipal)) {
      return false;
    }
    UnixPrincipal localUnixPrincipal = (UnixPrincipal)paramObject;
    return getName().equals(localUnixPrincipal.getName());
  }
  
  public int hashCode()
  {
    return this.name.hashCode();
  }
}
