package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class SecuritySupport
{
  private SecuritySupport() {}
  
  public static ClassLoader getContextClassLoader()
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
  
  public static InputStream getResourceAsStream(Class paramClass, final String paramString)
    throws IOException
  {
    try
    {
      (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          return this.val$c.getResourceAsStream(paramString);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  public static URL[] getResources(ClassLoader paramClassLoader, final String paramString)
  {
    (URL[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        URL[] arrayOfURL = null;
        try
        {
          ArrayList localArrayList = new ArrayList();
          Enumeration localEnumeration = this.val$cl.getResources(paramString);
          while ((localEnumeration != null) && (localEnumeration.hasMoreElements()))
          {
            URL localURL = (URL)localEnumeration.nextElement();
            if (localURL != null) {
              localArrayList.add(localURL);
            }
          }
          if (localArrayList.size() > 0)
          {
            arrayOfURL = new URL[localArrayList.size()];
            arrayOfURL = (URL[])localArrayList.toArray(arrayOfURL);
          }
        }
        catch (IOException localIOException) {}catch (SecurityException localSecurityException) {}
        return arrayOfURL;
      }
    });
  }
  
  public static URL[] getSystemResources(String paramString)
  {
    (URL[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        URL[] arrayOfURL = null;
        try
        {
          ArrayList localArrayList = new ArrayList();
          Enumeration localEnumeration = ClassLoader.getSystemResources(this.val$name);
          while ((localEnumeration != null) && (localEnumeration.hasMoreElements()))
          {
            URL localURL = (URL)localEnumeration.nextElement();
            if (localURL != null) {
              localArrayList.add(localURL);
            }
          }
          if (localArrayList.size() > 0)
          {
            arrayOfURL = new URL[localArrayList.size()];
            arrayOfURL = (URL[])localArrayList.toArray(arrayOfURL);
          }
        }
        catch (IOException localIOException) {}catch (SecurityException localSecurityException) {}
        return arrayOfURL;
      }
    });
  }
  
  public static InputStream openStream(URL paramURL)
    throws IOException
  {
    try
    {
      (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          return this.val$url.openStream();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
}
