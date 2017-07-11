package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

final class KrbServicePermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private transient List<Permission> perms = new ArrayList();
  private static final long serialVersionUID = -4118834211490102011L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Vector.class) };
  
  public KrbServicePermissionCollection() {}
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof ServicePermission)) {
      return false;
    }
    ServicePermission localServicePermission1 = (ServicePermission)paramPermission;
    Permission localPermission1 = localServicePermission1.getMask();
    if (localPermission1 == 0)
    {
      Iterator localIterator = this.perms.iterator();
      while (localIterator.hasNext())
      {
        localPermission2 = (Permission)localIterator.next();
        ServicePermission localServicePermission2 = (ServicePermission)localPermission2;
        if (localServicePermission2.impliesIgnoreMask(localServicePermission1)) {
          return true;
        }
      }
      return false;
    }
    int i = 0;
    Permission localPermission2 = localPermission1;
    synchronized (this)
    {
      int j = this.perms.size();
      for (int k = 0; k < j; k++)
      {
        ServicePermission localServicePermission3 = (ServicePermission)this.perms.get(k);
        if (((localPermission2 & localServicePermission3.getMask()) != 0) && (localServicePermission3.impliesIgnoreMask(localServicePermission1)))
        {
          i |= localServicePermission3.getMask();
          if ((i & localPermission1) == localPermission1) {
            return true;
          }
          localPermission2 = localPermission1 ^ i;
        }
      }
    }
    return false;
  }
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof ServicePermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
    }
    synchronized (this)
    {
      this.perms.add(0, paramPermission);
    }
  }
  
  public Enumeration<Permission> elements()
  {
    synchronized (this)
    {
      return Collections.enumeration(this.perms);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Vector localVector = new Vector(this.perms.size());
    synchronized (this)
    {
      localVector.addAll(this.perms);
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("permissions", localVector);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Vector localVector = (Vector)localGetField.get("permissions", null);
    this.perms = new ArrayList(localVector.size());
    this.perms.addAll(localVector);
  }
}
