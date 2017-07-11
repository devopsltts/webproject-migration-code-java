package java.net;

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
import java.util.List;
import java.util.Vector;

final class SocketPermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private transient List<SocketPermission> perms = new ArrayList();
  private static final long serialVersionUID = 2787186408602843674L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Vector.class) };
  
  public SocketPermissionCollection() {}
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof SocketPermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
    }
    synchronized (this)
    {
      this.perms.add(0, (SocketPermission)paramPermission);
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof SocketPermission)) {
      return false;
    }
    SocketPermission localSocketPermission1 = (SocketPermission)paramPermission;
    int i = localSocketPermission1.getMask();
    int j = 0;
    int k = i;
    synchronized (this)
    {
      int m = this.perms.size();
      for (int n = 0; n < m; n++)
      {
        SocketPermission localSocketPermission2 = (SocketPermission)this.perms.get(n);
        if (((k & localSocketPermission2.getMask()) != 0) && (localSocketPermission2.impliesIgnoreMask(localSocketPermission1)))
        {
          j |= localSocketPermission2.getMask();
          if ((j & i) == i) {
            return true;
          }
          k = i ^ j;
        }
      }
    }
    return false;
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
