package com.sun.xml.internal.bind.v2.bytecode;

import java.security.AccessController;
import java.security.PrivilegedAction;

class SecureLoader
{
  SecureLoader() {}
  
  static ClassLoader getContextClassLoader()
  {
    if (System.getSecurityManager() == null) {
      return Thread.currentThread().getContextClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Thread.currentThread().getContextClassLoader();
      }
    });
  }
  
  static ClassLoader getClassClassLoader(Class paramClass)
  {
    if (System.getSecurityManager() == null) {
      return paramClass.getClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return this.val$c.getClassLoader();
      }
    });
  }
  
  static ClassLoader getSystemClassLoader()
  {
    if (System.getSecurityManager() == null) {
      return ClassLoader.getSystemClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return ClassLoader.getSystemClassLoader();
      }
    });
  }
}
