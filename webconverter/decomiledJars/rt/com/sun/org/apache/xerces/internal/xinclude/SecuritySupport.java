package com.sun.org.apache.xerces.internal.xinclude;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

final class SecuritySupport
{
  private static final SecuritySupport securitySupport = new SecuritySupport();
  
  static SecuritySupport getInstance()
  {
    return securitySupport;
  }
  
  ClassLoader getContextClassLoader()
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        try
        {
          localClassLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (SecurityException localSecurityException) {}
        return localClassLoader;
      }
    });
  }
  
  ClassLoader getSystemClassLoader()
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        try
        {
          localClassLoader = ClassLoader.getSystemClassLoader();
        }
        catch (SecurityException localSecurityException) {}
        return localClassLoader;
      }
    });
  }
  
  ClassLoader getParentClassLoader(final ClassLoader paramClassLoader)
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        try
        {
          localClassLoader = paramClassLoader.getParent();
        }
        catch (SecurityException localSecurityException) {}
        return localClassLoader == paramClassLoader ? null : localClassLoader;
      }
    });
  }
  
  String getSystemProperty(final String paramString)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperty(paramString);
      }
    });
  }
  
  FileInputStream getFileInputStream(final File paramFile)
    throws FileNotFoundException
  {
    try
    {
      (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws FileNotFoundException
        {
          return new FileInputStream(paramFile);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((FileNotFoundException)localPrivilegedActionException.getException());
    }
  }
  
  InputStream getResourceAsStream(final ClassLoader paramClassLoader, final String paramString)
  {
    (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        InputStream localInputStream;
        if (paramClassLoader == null) {
          localInputStream = ClassLoader.getSystemResourceAsStream(paramString);
        } else {
          localInputStream = paramClassLoader.getResourceAsStream(paramString);
        }
        return localInputStream;
      }
    });
  }
  
  boolean getFileExists(final File paramFile)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return new Boolean(paramFile.exists());
      }
    })).booleanValue();
  }
  
  long getLastModified(final File paramFile)
  {
    ((Long)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return new Long(paramFile.lastModified());
      }
    })).longValue();
  }
  
  private SecuritySupport() {}
}
