package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;

final class PropertyPermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private transient Map<String, PropertyPermission> perms = new HashMap(32);
  private boolean all_allowed = false;
  private static final long serialVersionUID = 7015263904581634791L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE) };
  
  public PropertyPermissionCollection() {}
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof PropertyPermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
    }
    PropertyPermission localPropertyPermission1 = (PropertyPermission)paramPermission;
    String str1 = localPropertyPermission1.getName();
    synchronized (this)
    {
      PropertyPermission localPropertyPermission2 = (PropertyPermission)this.perms.get(str1);
      if (localPropertyPermission2 != null)
      {
        int i = localPropertyPermission2.getMask();
        int j = localPropertyPermission1.getMask();
        if (i != j)
        {
          int k = i | j;
          String str2 = PropertyPermission.getActions(k);
          this.perms.put(str1, new PropertyPermission(str1, str2));
        }
      }
      else
      {
        this.perms.put(str1, localPropertyPermission1);
      }
    }
    if ((!this.all_allowed) && (str1.equals("*"))) {
      this.all_allowed = true;
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof PropertyPermission)) {
      return false;
    }
    PropertyPermission localPropertyPermission1 = (PropertyPermission)paramPermission;
    int i = localPropertyPermission1.getMask();
    int j = 0;
    PropertyPermission localPropertyPermission2;
    if (this.all_allowed)
    {
      synchronized (this)
      {
        localPropertyPermission2 = (PropertyPermission)this.perms.get("*");
      }
      if (localPropertyPermission2 != null)
      {
        j |= localPropertyPermission2.getMask();
        if ((j & i) == i) {
          return true;
        }
      }
    }
    ??? = localPropertyPermission1.getName();
    synchronized (this)
    {
      localPropertyPermission2 = (PropertyPermission)this.perms.get(???);
    }
    if (localPropertyPermission2 != null)
    {
      j |= localPropertyPermission2.getMask();
      if ((j & i) == i) {
        return true;
      }
    }
    int k;
    for (int m = ((String)???).length() - 1; (k = ((String)???).lastIndexOf(".", m)) != -1; m = k - 1)
    {
      ??? = ((String)???).substring(0, k + 1) + "*";
      synchronized (this)
      {
        localPropertyPermission2 = (PropertyPermission)this.perms.get(???);
      }
      if (localPropertyPermission2 != null)
      {
        j |= localPropertyPermission2.getMask();
        if ((j & i) == i) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Enumeration<Permission> elements()
  {
    synchronized (this)
    {
      return Collections.enumeration(this.perms.values());
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = new Hashtable(this.perms.size() * 2);
    synchronized (this)
    {
      localHashtable.putAll(this.perms);
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("all_allowed", this.all_allowed);
    ((ObjectOutputStream.PutField)???).put("permissions", localHashtable);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    this.all_allowed = localGetField.get("all_allowed", false);
    Hashtable localHashtable = (Hashtable)localGetField.get("permissions", null);
    this.perms = new HashMap(localHashtable.size() * 2);
    this.perms.putAll(localHashtable);
  }
}
