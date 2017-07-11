package com.sun.jndi.ldap;

import java.net.MalformedURLException;
import java.net.URL;

abstract class VersionHelper
{
  private static VersionHelper helper = null;
  
  VersionHelper() {}
  
  static VersionHelper getVersionHelper()
  {
    return helper;
  }
  
  abstract ClassLoader getURLClassLoader(String[] paramArrayOfString)
    throws MalformedURLException;
  
  protected static URL[] getUrlArray(String[] paramArrayOfString)
    throws MalformedURLException
  {
    URL[] arrayOfURL = new URL[paramArrayOfString.length];
    for (int i = 0; i < arrayOfURL.length; i++) {
      arrayOfURL[i] = new URL(paramArrayOfString[i]);
    }
    return arrayOfURL;
  }
  
  abstract Class<?> loadClass(String paramString)
    throws ClassNotFoundException;
  
  abstract Thread createThread(Runnable paramRunnable);
  
  static
  {
    try
    {
      Class.forName("java.net.URLClassLoader");
      Class.forName("java.security.PrivilegedAction");
      helper = (VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper12").newInstance();
    }
    catch (Exception localException1) {}
    if (helper == null) {
      try
      {
        helper = (VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper11").newInstance();
      }
      catch (Exception localException2) {}
    }
  }
}
