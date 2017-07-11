package com.sun.security.auth;

import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;

@Exported(false)
@Deprecated
public class SolarisNumericGroupPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = 2345199581042573224L;
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private String name;
  private boolean primaryGroup;
  
  public SolarisNumericGroupPrincipal(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new NullPointerException(rb.getString("provided.null.name"));
    }
    this.name = paramString;
    this.primaryGroup = paramBoolean;
  }
  
  public SolarisNumericGroupPrincipal(long paramLong, boolean paramBoolean)
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
    return rb.getString("SolarisNumericGroupPrincipal.Supplementary.Group.") + this.name;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SolarisNumericGroupPrincipal)) {
      return false;
    }
    SolarisNumericGroupPrincipal localSolarisNumericGroupPrincipal = (SolarisNumericGroupPrincipal)paramObject;
    return (getName().equals(localSolarisNumericGroupPrincipal.getName())) && (isPrimaryGroup() == localSolarisNumericGroupPrincipal.isPrimaryGroup());
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
}
