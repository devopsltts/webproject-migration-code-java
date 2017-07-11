package javax.xml.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

class SecuritySupport
{
  SecuritySupport() {}
  
  ClassLoader getContextClassLoader()
    throws SecurityException
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        localClassLoader = Thread.currentThread().getContextClassLoader();
        if (localClassLoader == null) {
          localClassLoader = ClassLoader.getSystemClassLoader();
        }
        return localClassLoader;
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
          localInputStream = Object.class.getResourceAsStream(paramString);
        } else {
          localInputStream = paramClassLoader.getResourceAsStream(paramString);
        }
        return localInputStream;
      }
    });
  }
  
  boolean doesFileExist(final File paramFile)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return new Boolean(paramFile.exists());
      }
    })).booleanValue();
  }
}
