package com.sun.org.apache.xerces.internal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class SecuritySupport
{
  private static final SecuritySupport securitySupport = new SecuritySupport();
  static final Properties cacheProps = new Properties();
  static volatile boolean firstTime = true;
  
  public static SecuritySupport getInstance()
  {
    return securitySupport;
  }
  
  static ClassLoader getContextClassLoader()
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
  
  static ClassLoader getSystemClassLoader()
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
  
  static ClassLoader getParentClassLoader(ClassLoader paramClassLoader)
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        try
        {
          localClassLoader = this.val$cl.getParent();
        }
        catch (SecurityException localSecurityException) {}
        return localClassLoader == this.val$cl ? null : localClassLoader;
      }
    });
  }
  
  public static String getSystemProperty(String paramString)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperty(this.val$propName);
      }
    });
  }
  
  static FileInputStream getFileInputStream(File paramFile)
    throws FileNotFoundException
  {
    try
    {
      (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws FileNotFoundException
        {
          return new FileInputStream(this.val$file);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((FileNotFoundException)localPrivilegedActionException.getException());
    }
  }
  
  public static InputStream getResourceAsStream(String paramString)
  {
    if (System.getSecurityManager() != null) {
      return getResourceAsStream(null, paramString);
    }
    return getResourceAsStream(ObjectFactory.findClassLoader(), paramString);
  }
  
  public static InputStream getResourceAsStream(ClassLoader paramClassLoader, final String paramString)
  {
    (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        InputStream localInputStream;
        if (this.val$cl == null) {
          localInputStream = Object.class.getResourceAsStream("/" + paramString);
        } else {
          localInputStream = this.val$cl.getResourceAsStream(paramString);
        }
        return localInputStream;
      }
    });
  }
  
  public static ResourceBundle getResourceBundle(String paramString)
  {
    return getResourceBundle(paramString, Locale.getDefault());
  }
  
  public static ResourceBundle getResourceBundle(String paramString, final Locale paramLocale)
  {
    (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ResourceBundle run()
      {
        try
        {
          return PropertyResourceBundle.getBundle(this.val$bundle, paramLocale);
        }
        catch (MissingResourceException localMissingResourceException1)
        {
          try
          {
            return PropertyResourceBundle.getBundle(this.val$bundle, new Locale("en", "US"));
          }
          catch (MissingResourceException localMissingResourceException2)
          {
            throw new MissingResourceException("Could not load any resource bundle by " + this.val$bundle, this.val$bundle, "");
          }
        }
      }
    });
  }
  
  static boolean getFileExists(File paramFile)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return this.val$f.exists() ? Boolean.TRUE : Boolean.FALSE;
      }
    })).booleanValue();
  }
  
  static long getLastModified(File paramFile)
  {
    ((Long)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return new Long(this.val$f.lastModified());
      }
    })).longValue();
  }
  
  public static String sanitizePath(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    int i = paramString.lastIndexOf("/");
    if (i > 0) {
      return paramString.substring(i + 1, paramString.length());
    }
    return paramString;
  }
  
  public static String checkAccess(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    if ((paramString1 == null) || ((paramString2 != null) && (paramString2.equalsIgnoreCase(paramString3)))) {
      return null;
    }
    String str1;
    if (paramString1.indexOf(":") == -1)
    {
      str1 = "file";
    }
    else
    {
      URL localURL = new URL(paramString1);
      str1 = localURL.getProtocol();
      if (str1.equalsIgnoreCase("jar"))
      {
        String str2 = localURL.getPath();
        str1 = str2.substring(0, str2.indexOf(":"));
      }
    }
    if (isProtocolAllowed(str1, paramString2)) {
      return null;
    }
    return str1;
  }
  
  private static boolean isProtocolAllowed(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return false;
    }
    String[] arrayOfString1 = paramString2.split(",");
    for (String str : arrayOfString1)
    {
      str = str.trim();
      if (str.equalsIgnoreCase(paramString1)) {
        return true;
      }
    }
    return false;
  }
  
  public static String getJAXPSystemProperty(String paramString)
  {
    String str = getSystemProperty(paramString);
    if (str == null) {
      str = readJAXPProperty(paramString);
    }
    return str;
  }
  
  static String readJAXPProperty(String paramString)
  {
    str1 = null;
    FileInputStream localFileInputStream = null;
    try
    {
      if (firstTime) {
        synchronized (cacheProps)
        {
          if (firstTime)
          {
            String str2 = getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            File localFile = new File(str2);
            if (getFileExists(localFile))
            {
              localFileInputStream = getFileInputStream(localFile);
              cacheProps.load(localFileInputStream);
            }
            firstTime = false;
          }
        }
      }
      str1 = cacheProps.getProperty(paramString);
      return str1;
    }
    catch (Exception localException) {}finally
    {
      if (localFileInputStream != null) {
        try
        {
          localFileInputStream.close();
        }
        catch (IOException localIOException3) {}
      }
    }
  }
  
  private SecuritySupport() {}
}
