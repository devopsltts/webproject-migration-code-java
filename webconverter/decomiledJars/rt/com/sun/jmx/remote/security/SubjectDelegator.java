package com.sun.jmx.remote.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.management.remote.SubjectDelegationPermission;
import javax.security.auth.Subject;

public class SubjectDelegator
{
  public SubjectDelegator() {}
  
  public AccessControlContext delegatedContext(AccessControlContext paramAccessControlContext, Subject paramSubject, boolean paramBoolean)
    throws SecurityException
  {
    if ((System.getSecurityManager() != null) && (paramAccessControlContext == null)) {
      throw new SecurityException("Illegal AccessControlContext: null");
    }
    Collection localCollection = getSubjectPrincipals(paramSubject);
    final ArrayList localArrayList = new ArrayList(localCollection.size());
    Object localObject = localCollection.iterator();
    while (((Iterator)localObject).hasNext())
    {
      Principal localPrincipal = (Principal)((Iterator)localObject).next();
      String str = localPrincipal.getClass().getName() + "." + localPrincipal.getName();
      localArrayList.add(new SubjectDelegationPermission(str));
    }
    localObject = new PrivilegedAction()
    {
      public Void run()
      {
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          Permission localPermission = (Permission)localIterator.next();
          AccessController.checkPermission(localPermission);
        }
        return null;
      }
    };
    AccessController.doPrivileged((PrivilegedAction)localObject, paramAccessControlContext);
    return getDelegatedAcc(paramSubject, paramBoolean);
  }
  
  private AccessControlContext getDelegatedAcc(Subject paramSubject, boolean paramBoolean)
  {
    if (paramBoolean) {
      return JMXSubjectDomainCombiner.getDomainCombinerContext(paramSubject);
    }
    return JMXSubjectDomainCombiner.getContext(paramSubject);
  }
  
  public static synchronized boolean checkRemoveCallerContext(Subject paramSubject)
  {
    try
    {
      Iterator localIterator = getSubjectPrincipals(paramSubject).iterator();
      while (localIterator.hasNext())
      {
        Principal localPrincipal = (Principal)localIterator.next();
        String str = localPrincipal.getClass().getName() + "." + localPrincipal.getName();
        SubjectDelegationPermission localSubjectDelegationPermission = new SubjectDelegationPermission(str);
        AccessController.checkPermission(localSubjectDelegationPermission);
      }
    }
    catch (SecurityException localSecurityException)
    {
      return false;
    }
    return true;
  }
  
  private static Collection<Principal> getSubjectPrincipals(Subject paramSubject)
  {
    if (paramSubject.isReadOnly()) {
      return paramSubject.getPrincipals();
    }
    List localList = Arrays.asList(paramSubject.getPrincipals().toArray(new Principal[0]));
    return Collections.unmodifiableList(localList);
  }
}
