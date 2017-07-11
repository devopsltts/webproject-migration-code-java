package com.sun.jndi.ldap;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

final class VersionHelper12
  extends VersionHelper
{
  private static final String TRUST_URL_CODEBASE_PROPERTY = "com.sun.jndi.ldap.object.trustURLCodebase";
  private static final String trustURLCodebase = (String)AccessController.doPrivileged(new PrivilegedAction()
  {
    public String run()
    {
      return System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
    }
  });
  
  VersionHelper12() {}
  
  ClassLoader getURLClassLoader(String[] paramArrayOfString)
    throws MalformedURLException
  {
    ClassLoader localClassLoader = getContextClassLoader();
    if ((paramArrayOfString != null) && ("true".equalsIgnoreCase(trustURLCodebase))) {
      return URLClassLoader.newInstance(getUrlArray(paramArrayOfString), localClassLoader);
    }
    return localClassLoader;
  }
  
  Class<?> loadClass(String paramString)
    throws ClassNotFoundException
  {
    ClassLoader localClassLoader = getContextClassLoader();
    return Class.forName(paramString, true, localClassLoader);
  }
  
  private ClassLoader getContextClassLoader()
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return Thread.currentThread().getContextClassLoader();
      }
    });
  }
  
  Thread createThread(final Runnable paramRunnable)
  {
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    (Thread)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Thread run()
      {
        return SharedSecrets.getJavaLangAccess().newThreadWithAcc(paramRunnable, localAccessControlContext);
      }
    });
  }
}
