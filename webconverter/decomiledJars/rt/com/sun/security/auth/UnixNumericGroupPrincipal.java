package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class UnixNumericGroupPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = 3941535899328403223L;
  private String name;
  private boolean primaryGroup;
  
  public UnixNumericGroupPrincipal(String paramString, boolean paramBoolean)
  {
    if (paramString == null)
    {
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "name" };
      throw new NullPointerException(localMessageFormat.format(arrayOfObject));
    }
    this.name = paramString;
    this.primaryGroup = paramBoolean;
  }
  
  public UnixNumericGroupPrincipal(long paramLong, boolean paramBoolean)
  {
    this.name = new Long(paramLong).toString();
    this.primaryGroup = paramBoolean;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public long longValue()
  {
    return new Long(this.name).longValue();
  }
  
  public boolean isPrimaryGroup()
  {
    return this.primaryGroup;
  }
  
  public String toString()
  {
    if (this.primaryGroup)
    {
      localMessageFormat = new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Primary.Group.name", "sun.security.util.AuthResources"));
      arrayOfObject = new Object[] { this.name };
      return localMessageFormat.format(arrayOfObject);
    }
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Supplementary.Group.name", "sun.security.util.AuthResources"));
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
    if (!(paramObject instanceof UnixNumericGroupPrincipal)) {
      return false;
    }
    UnixNumericGroupPrincipal localUnixNumericGroupPrincipal = (UnixNumericGroupPrincipal)paramObject;
    return (getName().equals(localUnixNumericGroupPrincipal.getName())) && (isPrimaryGroup() == localUnixNumericGroupPrincipal.isPrimaryGroup());
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
}
