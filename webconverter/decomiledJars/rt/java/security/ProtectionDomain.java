package java.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import sun.misc.JavaSecurityAccess;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.JavaSecurityProtectionDomainAccess.ProtectionDomainCache;
import sun.misc.SharedSecrets;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public class ProtectionDomain
{
  private CodeSource codesource;
  private ClassLoader classloader;
  private Principal[] principals;
  private PermissionCollection permissions;
  private boolean hasAllPerm = false;
  private boolean staticPermissions;
  final Key key = new Key();
  private static final Debug debug;
  
  public ProtectionDomain(CodeSource paramCodeSource, PermissionCollection paramPermissionCollection)
  {
    this.codesource = paramCodeSource;
    if (paramPermissionCollection != null)
    {
      this.permissions = paramPermissionCollection;
      this.permissions.setReadOnly();
      if (((paramPermissionCollection instanceof Permissions)) && (((Permissions)paramPermissionCollection).allPermission != null)) {
        this.hasAllPerm = true;
      }
    }
    this.classloader = null;
    this.principals = new Principal[0];
    this.staticPermissions = true;
  }
  
  public ProtectionDomain(CodeSource paramCodeSource, PermissionCollection paramPermissionCollection, ClassLoader paramClassLoader, Principal[] paramArrayOfPrincipal)
  {
    this.codesource = paramCodeSource;
    if (paramPermissionCollection != null)
    {
      this.permissions = paramPermissionCollection;
      this.permissions.setReadOnly();
      if (((paramPermissionCollection instanceof Permissions)) && (((Permissions)paramPermissionCollection).allPermission != null)) {
        this.hasAllPerm = true;
      }
    }
    this.classloader = paramClassLoader;
    this.principals = (paramArrayOfPrincipal != null ? (Principal[])paramArrayOfPrincipal.clone() : new Principal[0]);
    this.staticPermissions = false;
  }
  
  public final CodeSource getCodeSource()
  {
    return this.codesource;
  }
  
  public final ClassLoader getClassLoader()
  {
    return this.classloader;
  }
  
  public final Principal[] getPrincipals()
  {
    return (Principal[])this.principals.clone();
  }
  
  public final PermissionCollection getPermissions()
  {
    return this.permissions;
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (this.hasAllPerm) {
      return true;
    }
    if ((!this.staticPermissions) && (Policy.getPolicyNoCheck().implies(this, paramPermission))) {
      return true;
    }
    if (this.permissions != null) {
      return this.permissions.implies(paramPermission);
    }
    return false;
  }
  
  boolean impliesCreateAccessControlContext()
  {
    return implies(SecurityConstants.CREATE_ACC_PERMISSION);
  }
  
  public String toString()
  {
    String str = "<no principals>";
    if ((this.principals != null) && (this.principals.length > 0))
    {
      localObject = new StringBuilder("(principals ");
      for (int i = 0; i < this.principals.length; i++)
      {
        ((StringBuilder)localObject).append(this.principals[i].getClass().getName() + " \"" + this.principals[i].getName() + "\"");
        if (i < this.principals.length - 1) {
          ((StringBuilder)localObject).append(",\n");
        } else {
          ((StringBuilder)localObject).append(")\n");
        }
      }
      str = ((StringBuilder)localObject).toString();
    }
    Object localObject = (Policy.isSet()) && (seeAllp()) ? mergePermissions() : getPermissions();
    return "ProtectionDomain  " + this.codesource + "\n" + " " + this.classloader + "\n" + " " + str + "\n" + " " + localObject + "\n";
  }
  
  private static boolean seeAllp()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      return true;
    }
    if (debug != null)
    {
      if ((localSecurityManager.getClass().getClassLoader() == null) && (Policy.getPolicyNoCheck().getClass().getClassLoader() == null)) {
        return true;
      }
    }
    else {
      try
      {
        localSecurityManager.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
        return true;
      }
      catch (SecurityException localSecurityException) {}
    }
    return false;
  }
  
  private PermissionCollection mergePermissions()
  {
    if (this.staticPermissions) {
      return this.permissions;
    }
    PermissionCollection localPermissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
    {
      public PermissionCollection run()
      {
        Policy localPolicy = Policy.getPolicyNoCheck();
        return localPolicy.getPermissions(ProtectionDomain.this);
      }
    });
    Permissions localPermissions = new Permissions();
    int i = 32;
    int j = 8;
    ArrayList localArrayList1 = new ArrayList(j);
    ArrayList localArrayList2 = new ArrayList(i);
    Enumeration localEnumeration;
    if (this.permissions != null) {
      synchronized (this.permissions)
      {
        localEnumeration = this.permissions.elements();
        while (localEnumeration.hasMoreElements()) {
          localArrayList1.add(localEnumeration.nextElement());
        }
      }
    }
    if (localPermissionCollection != null) {
      synchronized (localPermissionCollection)
      {
        localEnumeration = localPermissionCollection.elements();
        while (localEnumeration.hasMoreElements())
        {
          localArrayList2.add(localEnumeration.nextElement());
          j++;
        }
      }
    }
    if ((localPermissionCollection != null) && (this.permissions != null)) {
      synchronized (this.permissions)
      {
        localEnumeration = this.permissions.elements();
        while (localEnumeration.hasMoreElements())
        {
          Permission localPermission1 = (Permission)localEnumeration.nextElement();
          Class localClass = localPermission1.getClass();
          String str1 = localPermission1.getActions();
          String str2 = localPermission1.getName();
          for (int m = 0; m < localArrayList2.size(); m++)
          {
            Permission localPermission2 = (Permission)localArrayList2.get(m);
            if ((localClass.isInstance(localPermission2)) && (str2.equals(localPermission2.getName())) && (str1.equals(localPermission2.getActions())))
            {
              localArrayList2.remove(m);
              break;
            }
          }
        }
      }
    }
    int k;
    if (localPermissionCollection != null) {
      for (k = localArrayList2.size() - 1; k >= 0; k--) {
        localPermissions.add((Permission)localArrayList2.get(k));
      }
    }
    if (this.permissions != null) {
      for (k = localArrayList1.size() - 1; k >= 0; k--) {
        localPermissions.add((Permission)localArrayList1.get(k));
      }
    }
    return localPermissions;
  }
  
  static
  {
    SharedSecrets.setJavaSecurityAccess(new JavaSecurityAccessImpl(null));
    debug = Debug.getInstance("domain");
    SharedSecrets.setJavaSecurityProtectionDomainAccess(new JavaSecurityProtectionDomainAccess()
    {
      public JavaSecurityProtectionDomainAccess.ProtectionDomainCache getProtectionDomainCache()
      {
        new JavaSecurityProtectionDomainAccess.ProtectionDomainCache()
        {
          private final Map<ProtectionDomain.Key, PermissionCollection> map = Collections.synchronizedMap(new WeakHashMap());
          
          public void put(ProtectionDomain paramAnonymous2ProtectionDomain, PermissionCollection paramAnonymous2PermissionCollection)
          {
            this.map.put(paramAnonymous2ProtectionDomain == null ? null : paramAnonymous2ProtectionDomain.key, paramAnonymous2PermissionCollection);
          }
          
          public PermissionCollection get(ProtectionDomain paramAnonymous2ProtectionDomain)
          {
            return paramAnonymous2ProtectionDomain == null ? (PermissionCollection)this.map.get(null) : (PermissionCollection)this.map.get(paramAnonymous2ProtectionDomain.key);
          }
        };
      }
    });
  }
  
  private static class JavaSecurityAccessImpl
    implements JavaSecurityAccess
  {
    private JavaSecurityAccessImpl() {}
    
    public <T> T doIntersectionPrivilege(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2)
    {
      if (paramPrivilegedAction == null) {
        throw new NullPointerException();
      }
      return AccessController.doPrivileged(paramPrivilegedAction, getCombinedACC(paramAccessControlContext2, paramAccessControlContext1));
    }
    
    public <T> T doIntersectionPrivilege(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext)
    {
      return doIntersectionPrivilege(paramPrivilegedAction, AccessController.getContext(), paramAccessControlContext);
    }
    
    private static AccessControlContext getCombinedACC(AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2)
    {
      AccessControlContext localAccessControlContext = new AccessControlContext(paramAccessControlContext1, paramAccessControlContext2.getCombiner(), true);
      return new AccessControlContext(paramAccessControlContext2.getContext(), localAccessControlContext).optimize();
    }
  }
  
  final class Key
  {
    Key() {}
  }
}
