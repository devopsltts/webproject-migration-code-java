package javax.security.auth;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.DomainCombiner;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import sun.security.util.Debug;

public class SubjectDomainCombiner
  implements DomainCombiner
{
  private Subject subject;
  private WeakKeyValueMap<ProtectionDomain, ProtectionDomain> cachedPDs = new WeakKeyValueMap(null);
  private Set<Principal> principalSet;
  private Principal[] principals;
  private static final Debug debug = Debug.getInstance("combiner", "\t[SubjectDomainCombiner]");
  private static final boolean useJavaxPolicy = Policy.isCustomPolicySet(debug);
  private static final boolean allowCaching = (useJavaxPolicy) && (cachePolicy());
  
  public SubjectDomainCombiner(Subject paramSubject)
  {
    this.subject = paramSubject;
    if (paramSubject.isReadOnly())
    {
      this.principalSet = paramSubject.getPrincipals();
      this.principals = ((Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]));
    }
  }
  
  public Subject getSubject()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AuthPermission("getSubjectFromDomainCombiner"));
    }
    return this.subject;
  }
  
  public ProtectionDomain[] combine(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2)
  {
    if (debug != null)
    {
      if (this.subject == null)
      {
        debug.println("null subject");
      }
      else
      {
        final Subject localSubject = this.subject;
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            SubjectDomainCombiner.debug.println(localSubject.toString());
            return null;
          }
        });
      }
      printInputDomains(paramArrayOfProtectionDomain1, paramArrayOfProtectionDomain2);
    }
    if ((paramArrayOfProtectionDomain1 == null) || (paramArrayOfProtectionDomain1.length == 0)) {
      return paramArrayOfProtectionDomain2;
    }
    paramArrayOfProtectionDomain1 = optimize(paramArrayOfProtectionDomain1);
    if (debug != null)
    {
      debug.println("after optimize");
      printInputDomains(paramArrayOfProtectionDomain1, paramArrayOfProtectionDomain2);
    }
    if ((paramArrayOfProtectionDomain1 == null) && (paramArrayOfProtectionDomain2 == null)) {
      return null;
    }
    if (useJavaxPolicy) {
      return combineJavaxPolicy(paramArrayOfProtectionDomain1, paramArrayOfProtectionDomain2);
    }
    Object localObject1 = paramArrayOfProtectionDomain1 == null ? 0 : paramArrayOfProtectionDomain1.length;
    Object localObject2 = paramArrayOfProtectionDomain2 == null ? 0 : paramArrayOfProtectionDomain2.length;
    ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[localObject1 + localObject2];
    int i = 1;
    synchronized (this.cachedPDs)
    {
      Object localObject3;
      if ((!this.subject.isReadOnly()) && (!this.subject.getPrincipals().equals(this.principalSet)))
      {
        localObject3 = this.subject.getPrincipals();
        synchronized (localObject3)
        {
          this.principalSet = new HashSet((Collection)localObject3);
        }
        this.principals = ((Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]));
        this.cachedPDs.clear();
        if (debug != null) {
          debug.println("Subject mutated - clearing cache");
        }
      }
      for (??? = 0; ??? < localObject1; ???++)
      {
        ProtectionDomain localProtectionDomain = paramArrayOfProtectionDomain1[???];
        localObject3 = (ProtectionDomain)this.cachedPDs.getValue(localProtectionDomain);
        if (localObject3 == null)
        {
          localObject3 = new ProtectionDomain(localProtectionDomain.getCodeSource(), localProtectionDomain.getPermissions(), localProtectionDomain.getClassLoader(), this.principals);
          this.cachedPDs.putValue(localProtectionDomain, localObject3);
        }
        else
        {
          i = 0;
        }
        arrayOfProtectionDomain[???] = localObject3;
      }
    }
    if (debug != null)
    {
      debug.println("updated current: ");
      for (??? = 0; ??? < localObject1; ???++) {
        debug.println("\tupdated[" + ??? + "] = " + printDomain(arrayOfProtectionDomain[???]));
      }
    }
    if (localObject2 > 0)
    {
      System.arraycopy(paramArrayOfProtectionDomain2, 0, arrayOfProtectionDomain, localObject1, localObject2);
      if (i == 0) {
        arrayOfProtectionDomain = optimize(arrayOfProtectionDomain);
      }
    }
    if (debug != null) {
      if ((arrayOfProtectionDomain == null) || (arrayOfProtectionDomain.length == 0))
      {
        debug.println("returning null");
      }
      else
      {
        debug.println("combinedDomains: ");
        for (int j = 0; j < arrayOfProtectionDomain.length; j++) {
          debug.println("newDomain " + j + ": " + printDomain(arrayOfProtectionDomain[j]));
        }
      }
    }
    if ((arrayOfProtectionDomain == null) || (arrayOfProtectionDomain.length == 0)) {
      return null;
    }
    return arrayOfProtectionDomain;
  }
  
  private ProtectionDomain[] combineJavaxPolicy(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2)
  {
    if (!allowCaching) {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          Policy.getPolicy().refresh();
          return null;
        }
      });
    }
    Object localObject1 = paramArrayOfProtectionDomain1 == null ? 0 : paramArrayOfProtectionDomain1.length;
    Object localObject2 = paramArrayOfProtectionDomain2 == null ? 0 : paramArrayOfProtectionDomain2.length;
    ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[localObject1 + localObject2];
    synchronized (this.cachedPDs)
    {
      if ((!this.subject.isReadOnly()) && (!this.subject.getPrincipals().equals(this.principalSet)))
      {
        Set localSet = this.subject.getPrincipals();
        synchronized (localSet)
        {
          this.principalSet = new HashSet(localSet);
        }
        this.principals = ((Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]));
        this.cachedPDs.clear();
        if (debug != null) {
          debug.println("Subject mutated - clearing cache");
        }
      }
      for (Object localObject3 = 0; localObject3 < localObject1; localObject3++)
      {
        ??? = paramArrayOfProtectionDomain1[localObject3];
        ProtectionDomain localProtectionDomain = (ProtectionDomain)this.cachedPDs.getValue(???);
        if (localProtectionDomain == null)
        {
          Permissions localPermissions = new Permissions();
          PermissionCollection localPermissionCollection1 = ((ProtectionDomain)???).getPermissions();
          Enumeration localEnumeration;
          if (localPermissionCollection1 != null) {
            synchronized (localPermissionCollection1)
            {
              localEnumeration = localPermissionCollection1.elements();
              while (localEnumeration.hasMoreElements())
              {
                localObject5 = (Permission)localEnumeration.nextElement();
                localPermissions.add((Permission)localObject5);
              }
            }
          }
          ??? = ((ProtectionDomain)???).getCodeSource();
          Object localObject5 = this.subject;
          PermissionCollection localPermissionCollection2 = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
          {
            public PermissionCollection run()
            {
              return Policy.getPolicy().getPermissions(this.val$finalS, this.val$finalCs);
            }
          });
          synchronized (localPermissionCollection2)
          {
            localEnumeration = localPermissionCollection2.elements();
            while (localEnumeration.hasMoreElements())
            {
              Permission localPermission = (Permission)localEnumeration.nextElement();
              if (!localPermissions.implies(localPermission))
              {
                localPermissions.add(localPermission);
                if (debug != null) {
                  debug.println("Adding perm " + localPermission + "\n");
                }
              }
            }
          }
          localProtectionDomain = new ProtectionDomain((CodeSource)???, localPermissions, ((ProtectionDomain)???).getClassLoader(), this.principals);
          if (allowCaching) {
            this.cachedPDs.putValue(???, localProtectionDomain);
          }
        }
        arrayOfProtectionDomain[localObject3] = localProtectionDomain;
      }
    }
    if (debug != null)
    {
      debug.println("updated current: ");
      for (??? = 0; ??? < localObject1; ???++) {
        debug.println("\tupdated[" + ??? + "] = " + arrayOfProtectionDomain[???]);
      }
    }
    if (localObject2 > 0) {
      System.arraycopy(paramArrayOfProtectionDomain2, 0, arrayOfProtectionDomain, localObject1, localObject2);
    }
    if (debug != null) {
      if ((arrayOfProtectionDomain == null) || (arrayOfProtectionDomain.length == 0))
      {
        debug.println("returning null");
      }
      else
      {
        debug.println("combinedDomains: ");
        for (int i = 0; i < arrayOfProtectionDomain.length; i++) {
          debug.println("newDomain " + i + ": " + arrayOfProtectionDomain[i].toString());
        }
      }
    }
    if ((arrayOfProtectionDomain == null) || (arrayOfProtectionDomain.length == 0)) {
      return null;
    }
    return arrayOfProtectionDomain;
  }
  
  private static ProtectionDomain[] optimize(ProtectionDomain[] paramArrayOfProtectionDomain)
  {
    if ((paramArrayOfProtectionDomain == null) || (paramArrayOfProtectionDomain.length == 0)) {
      return null;
    }
    Object localObject = new ProtectionDomain[paramArrayOfProtectionDomain.length];
    int i = 0;
    for (int j = 0; j < paramArrayOfProtectionDomain.length; j++)
    {
      ProtectionDomain localProtectionDomain;
      if ((localProtectionDomain = paramArrayOfProtectionDomain[j]) != null)
      {
        int k = 0;
        for (int m = 0; (m < i) && (k == 0); m++) {
          k = localObject[m] == localProtectionDomain ? 1 : 0;
        }
        if (k == 0) {
          localObject[(i++)] = localProtectionDomain;
        }
      }
    }
    if ((i > 0) && (i < paramArrayOfProtectionDomain.length))
    {
      ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[i];
      System.arraycopy(localObject, 0, arrayOfProtectionDomain, 0, arrayOfProtectionDomain.length);
      localObject = arrayOfProtectionDomain;
    }
    return (i == 0) || (localObject.length == 0) ? null : localObject;
  }
  
  private static boolean cachePolicy()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("cache.auth.policy");
      }
    });
    if (str != null) {
      return Boolean.parseBoolean(str);
    }
    return true;
  }
  
  private static void printInputDomains(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2)
  {
    int i;
    if ((paramArrayOfProtectionDomain1 == null) || (paramArrayOfProtectionDomain1.length == 0)) {
      debug.println("currentDomains null or 0 length");
    } else {
      for (i = 0; (paramArrayOfProtectionDomain1 != null) && (i < paramArrayOfProtectionDomain1.length); i++) {
        if (paramArrayOfProtectionDomain1[i] == null) {
          debug.println("currentDomain " + i + ": SystemDomain");
        } else {
          debug.println("currentDomain " + i + ": " + printDomain(paramArrayOfProtectionDomain1[i]));
        }
      }
    }
    if ((paramArrayOfProtectionDomain2 == null) || (paramArrayOfProtectionDomain2.length == 0))
    {
      debug.println("assignedDomains null or 0 length");
    }
    else
    {
      debug.println("assignedDomains = ");
      for (i = 0; (paramArrayOfProtectionDomain2 != null) && (i < paramArrayOfProtectionDomain2.length); i++) {
        if (paramArrayOfProtectionDomain2[i] == null) {
          debug.println("assignedDomain " + i + ": SystemDomain");
        } else {
          debug.println("assignedDomain " + i + ": " + printDomain(paramArrayOfProtectionDomain2[i]));
        }
      }
    }
  }
  
  private static String printDomain(ProtectionDomain paramProtectionDomain)
  {
    if (paramProtectionDomain == null) {
      return "null";
    }
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return this.val$pd.toString();
      }
    });
  }
  
  private static class WeakKeyValueMap<K, V>
    extends WeakHashMap<K, WeakReference<V>>
  {
    private WeakKeyValueMap() {}
    
    public V getValue(K paramK)
    {
      WeakReference localWeakReference = (WeakReference)super.get(paramK);
      if (localWeakReference != null) {
        return localWeakReference.get();
      }
      return null;
    }
    
    public V putValue(K paramK, V paramV)
    {
      WeakReference localWeakReference = (WeakReference)super.put(paramK, new WeakReference(paramV));
      if (localWeakReference != null) {
        return localWeakReference.get();
      }
      return null;
    }
  }
}
