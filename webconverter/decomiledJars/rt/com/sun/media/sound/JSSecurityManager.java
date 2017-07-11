package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import javax.sound.sampled.AudioPermission;

final class JSSecurityManager
{
  private JSSecurityManager() {}
  
  private static boolean hasSecurityManager()
  {
    return System.getSecurityManager() != null;
  }
  
  static void checkRecordPermission()
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AudioPermission("record"));
    }
  }
  
  static void loadProperties(Properties paramProperties, final String paramString)
  {
    if (hasSecurityManager()) {
      try
      {
        PrivilegedAction local1 = new PrivilegedAction()
        {
          public Void run()
          {
            JSSecurityManager.loadPropertiesImpl(this.val$properties, paramString);
            return null;
          }
        };
        AccessController.doPrivileged(local1);
      }
      catch (Exception localException)
      {
        loadPropertiesImpl(paramProperties, paramString);
      }
    } else {
      loadPropertiesImpl(paramProperties, paramString);
    }
  }
  
  private static void loadPropertiesImpl(Properties paramProperties, String paramString)
  {
    String str = System.getProperty("java.home");
    try
    {
      if (str == null) {
        throw new Error("Can't find java.home ??");
      }
      File localFile = new File(str, "lib");
      localFile = new File(localFile, paramString);
      str = localFile.getCanonicalPath();
      FileInputStream localFileInputStream = new FileInputStream(str);
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
      try
      {
        paramProperties.load(localBufferedInputStream);
      }
      finally
      {
        if (localFileInputStream != null) {
          localFileInputStream.close();
        }
      }
    }
    catch (Throwable localThrowable) {}
  }
  
  static Thread createThread(Runnable paramRunnable, String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    Thread localThread = new Thread(paramRunnable);
    if (paramString != null) {
      localThread.setName(paramString);
    }
    localThread.setDaemon(paramBoolean1);
    if (paramInt >= 0) {
      localThread.setPriority(paramInt);
    }
    if (paramBoolean2) {
      localThread.start();
    }
    return localThread;
  }
  
  static synchronized <T> List<T> getProviders(Class<T> paramClass)
  {
    ArrayList localArrayList = new ArrayList(7);
    PrivilegedAction local2 = new PrivilegedAction()
    {
      public Iterator<T> run()
      {
        return ServiceLoader.load(this.val$providerClass).iterator();
      }
    };
    Iterator localIterator = (Iterator)AccessController.doPrivileged(local2);
    PrivilegedAction local3 = new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(this.val$ps.hasNext());
      }
    };
    while (((Boolean)AccessController.doPrivileged(local3)).booleanValue()) {
      try
      {
        Object localObject = localIterator.next();
        if (paramClass.isInstance(localObject)) {
          localArrayList.add(0, localObject);
        }
      }
      catch (Throwable localThrowable) {}
    }
    return localArrayList;
  }
}
