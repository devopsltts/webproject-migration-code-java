package com.sun.rmi.rmid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

public final class ExecOptionPermission
  extends Permission
{
  private transient boolean wildcard;
  private transient String name;
  private static final long serialVersionUID = 5842294756823092756L;
  
  public ExecOptionPermission(String paramString)
  {
    super(paramString);
    init(paramString);
  }
  
  public ExecOptionPermission(String paramString1, String paramString2)
  {
    this(paramString1);
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof ExecOptionPermission)) {
      return false;
    }
    ExecOptionPermission localExecOptionPermission = (ExecOptionPermission)paramPermission;
    if (this.wildcard)
    {
      if (localExecOptionPermission.wildcard) {
        return localExecOptionPermission.name.startsWith(this.name);
      }
      return (localExecOptionPermission.name.length() > this.name.length()) && (localExecOptionPermission.name.startsWith(this.name));
    }
    if (localExecOptionPermission.wildcard) {
      return false;
    }
    return this.name.equals(localExecOptionPermission.name);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject == null) || (paramObject.getClass() != getClass())) {
      return false;
    }
    ExecOptionPermission localExecOptionPermission = (ExecOptionPermission)paramObject;
    return getName().equals(localExecOptionPermission.getName());
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
  
  public String getActions()
  {
    return "";
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new ExecOptionPermissionCollection();
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(getName());
  }
  
  private void init(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("name can't be null");
    }
    if (paramString.equals("")) {
      throw new IllegalArgumentException("name can't be empty");
    }
    if ((paramString.endsWith(".*")) || (paramString.endsWith("=*")) || (paramString.equals("*")))
    {
      this.wildcard = true;
      if (paramString.length() == 1) {
        this.name = "";
      } else {
        this.name = paramString.substring(0, paramString.length() - 1);
      }
    }
    else
    {
      this.name = paramString;
    }
  }
  
  private static class ExecOptionPermissionCollection
    extends PermissionCollection
    implements Serializable
  {
    private Hashtable<String, Permission> permissions = new Hashtable(11);
    private boolean all_allowed = false;
    private static final long serialVersionUID = -1242475729790124375L;
    
    public ExecOptionPermissionCollection() {}
    
    public void add(Permission paramPermission)
    {
      if (!(paramPermission instanceof ExecOptionPermission)) {
        throw new IllegalArgumentException("invalid permission: " + paramPermission);
      }
      if (isReadOnly()) {
        throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
      }
      ExecOptionPermission localExecOptionPermission = (ExecOptionPermission)paramPermission;
      this.permissions.put(localExecOptionPermission.getName(), paramPermission);
      if ((!this.all_allowed) && (localExecOptionPermission.getName().equals("*"))) {
        this.all_allowed = true;
      }
    }
    
    public boolean implies(Permission paramPermission)
    {
      if (!(paramPermission instanceof ExecOptionPermission)) {
        return false;
      }
      ExecOptionPermission localExecOptionPermission = (ExecOptionPermission)paramPermission;
      if (this.all_allowed) {
        return true;
      }
      String str = localExecOptionPermission.getName();
      Permission localPermission = (Permission)this.permissions.get(str);
      if (localPermission != null) {
        return localPermission.implies(paramPermission);
      }
      int i;
      for (int j = str.length() - 1; (i = str.lastIndexOf(".", j)) != -1; j = i - 1)
      {
        str = str.substring(0, i + 1) + "*";
        localPermission = (Permission)this.permissions.get(str);
        if (localPermission != null) {
          return localPermission.implies(paramPermission);
        }
      }
      str = localExecOptionPermission.getName();
      for (j = str.length() - 1; (i = str.lastIndexOf("=", j)) != -1; j = i - 1)
      {
        str = str.substring(0, i + 1) + "*";
        localPermission = (Permission)this.permissions.get(str);
        if (localPermission != null) {
          return localPermission.implies(paramPermission);
        }
      }
      return false;
    }
    
    public Enumeration<Permission> elements()
    {
      return this.permissions.elements();
    }
  }
}
