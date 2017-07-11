package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public final class Permissions
  extends PermissionCollection
  implements Serializable
{
  private transient Map<Class<?>, PermissionCollection> permsMap = new HashMap(11);
  private transient boolean hasUnresolved = false;
  PermissionCollection allPermission = null;
  private static final long serialVersionUID = 4858622370623524688L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("perms", Hashtable.class), new ObjectStreamField("allPermission", PermissionCollection.class) };
  
  public Permissions() {}
  
  public void add(Permission paramPermission)
  {
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly Permissions object");
    }
    PermissionCollection localPermissionCollection;
    synchronized (this)
    {
      localPermissionCollection = getPermissionCollection(paramPermission, true);
      localPermissionCollection.add(paramPermission);
    }
    if ((paramPermission instanceof AllPermission)) {
      this.allPermission = localPermissionCollection;
    }
    if ((paramPermission instanceof UnresolvedPermission)) {
      this.hasUnresolved = true;
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (this.allPermission != null) {
      return true;
    }
    synchronized (this)
    {
      PermissionCollection localPermissionCollection = getPermissionCollection(paramPermission, false);
      if (localPermissionCollection != null) {
        return localPermissionCollection.implies(paramPermission);
      }
      return false;
    }
  }
  
  public Enumeration<Permission> elements()
  {
    synchronized (this)
    {
      return new PermissionsEnumerator(this.permsMap.values().iterator());
    }
  }
  
  private PermissionCollection getPermissionCollection(Permission paramPermission, boolean paramBoolean)
  {
    Class localClass = paramPermission.getClass();
    Object localObject = (PermissionCollection)this.permsMap.get(localClass);
    if ((!this.hasUnresolved) && (!paramBoolean)) {
      return localObject;
    }
    if (localObject == null)
    {
      localObject = this.hasUnresolved ? getUnresolvedPermissions(paramPermission) : null;
      if ((localObject == null) && (paramBoolean))
      {
        localObject = paramPermission.newPermissionCollection();
        if (localObject == null) {
          localObject = new PermissionsHash();
        }
      }
      if (localObject != null) {
        this.permsMap.put(localClass, localObject);
      }
    }
    return localObject;
  }
  
  private PermissionCollection getUnresolvedPermissions(Permission paramPermission)
  {
    UnresolvedPermissionCollection localUnresolvedPermissionCollection = (UnresolvedPermissionCollection)this.permsMap.get(UnresolvedPermission.class);
    if (localUnresolvedPermissionCollection == null) {
      return null;
    }
    List localList = localUnresolvedPermissionCollection.getUnresolvedPermissions(paramPermission);
    if (localList == null) {
      return null;
    }
    Certificate[] arrayOfCertificate = null;
    Object[] arrayOfObject = paramPermission.getClass().getSigners();
    int i = 0;
    if (arrayOfObject != null)
    {
      for (int j = 0; j < arrayOfObject.length; j++) {
        if ((arrayOfObject[j] instanceof Certificate)) {
          i++;
        }
      }
      arrayOfCertificate = new Certificate[i];
      i = 0;
      for (j = 0; j < arrayOfObject.length; j++) {
        if ((arrayOfObject[j] instanceof Certificate)) {
          arrayOfCertificate[(i++)] = ((Certificate)arrayOfObject[j]);
        }
      }
    }
    Object localObject1 = null;
    synchronized (localList)
    {
      int k = localList.size();
      for (int m = 0; m < k; m++)
      {
        UnresolvedPermission localUnresolvedPermission = (UnresolvedPermission)localList.get(m);
        Permission localPermission = localUnresolvedPermission.resolve(paramPermission, arrayOfCertificate);
        if (localPermission != null)
        {
          if (localObject1 == null)
          {
            localObject1 = paramPermission.newPermissionCollection();
            if (localObject1 == null) {
              localObject1 = new PermissionsHash();
            }
          }
          ((PermissionCollection)localObject1).add(localPermission);
        }
      }
    }
    return localObject1;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = new Hashtable(this.permsMap.size() * 2);
    synchronized (this)
    {
      localHashtable.putAll(this.permsMap);
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("allPermission", this.allPermission);
    ((ObjectOutputStream.PutField)???).put("perms", localHashtable);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    this.allPermission = ((PermissionCollection)localGetField.get("allPermission", null));
    Hashtable localHashtable = (Hashtable)localGetField.get("perms", null);
    this.permsMap = new HashMap(localHashtable.size() * 2);
    this.permsMap.putAll(localHashtable);
    UnresolvedPermissionCollection localUnresolvedPermissionCollection = (UnresolvedPermissionCollection)this.permsMap.get(UnresolvedPermission.class);
    this.hasUnresolved = ((localUnresolvedPermissionCollection != null) && (localUnresolvedPermissionCollection.elements().hasMoreElements()));
  }
}
