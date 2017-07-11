package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public final class IIOPHelper
{
  private static final String IMPL_CLASS = "com.sun.jmx.remote.protocol.iiop.IIOPProxyImpl";
  private static final IIOPProxy proxy = (IIOPProxy)AccessController.doPrivileged(new PrivilegedAction()
  {
    public IIOPProxy run()
    {
      try
      {
        Class localClass = Class.forName("com.sun.jmx.remote.protocol.iiop.IIOPProxyImpl", true, IIOPHelper.class.getClassLoader());
        return (IIOPProxy)localClass.newInstance();
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        return null;
      }
      catch (InstantiationException localInstantiationException)
      {
        throw new AssertionError(localInstantiationException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
  });
  
  private IIOPHelper() {}
  
  public static boolean isAvailable()
  {
    return proxy != null;
  }
  
  private static void ensureAvailable()
  {
    if (proxy == null) {
      throw new AssertionError("Should not here");
    }
  }
  
  public static boolean isStub(Object paramObject)
  {
    return proxy == null ? false : proxy.isStub(paramObject);
  }
  
  public static Object getDelegate(Object paramObject)
  {
    ensureAvailable();
    return proxy.getDelegate(paramObject);
  }
  
  public static void setDelegate(Object paramObject1, Object paramObject2)
  {
    ensureAvailable();
    proxy.setDelegate(paramObject1, paramObject2);
  }
  
  public static Object getOrb(Object paramObject)
  {
    ensureAvailable();
    return proxy.getOrb(paramObject);
  }
  
  public static void connect(Object paramObject1, Object paramObject2)
    throws IOException
  {
    if (proxy == null) {
      throw new IOException("Connection to ORB failed, RMI/IIOP not available");
    }
    proxy.connect(paramObject1, paramObject2);
  }
  
  public static boolean isOrb(Object paramObject)
  {
    return proxy == null ? false : proxy.isOrb(paramObject);
  }
  
  public static Object createOrb(String[] paramArrayOfString, Properties paramProperties)
    throws IOException
  {
    if (proxy == null) {
      throw new IOException("ORB initialization failed, RMI/IIOP not available");
    }
    return proxy.createOrb(paramArrayOfString, paramProperties);
  }
  
  public static Object stringToObject(Object paramObject, String paramString)
  {
    ensureAvailable();
    return proxy.stringToObject(paramObject, paramString);
  }
  
  public static String objectToString(Object paramObject1, Object paramObject2)
  {
    ensureAvailable();
    return proxy.objectToString(paramObject1, paramObject2);
  }
  
  public static <T> T narrow(Object paramObject, Class<T> paramClass)
  {
    ensureAvailable();
    return proxy.narrow(paramObject, paramClass);
  }
  
  public static void exportObject(Remote paramRemote)
    throws IOException
  {
    if (proxy == null) {
      throw new IOException("RMI object cannot be exported, RMI/IIOP not available");
    }
    proxy.exportObject(paramRemote);
  }
  
  public static void unexportObject(Remote paramRemote)
    throws IOException
  {
    if (proxy == null) {
      throw new NoSuchObjectException("Object not exported");
    }
    proxy.unexportObject(paramRemote);
  }
  
  public static Remote toStub(Remote paramRemote)
    throws IOException
  {
    if (proxy == null) {
      throw new NoSuchObjectException("Object not exported");
    }
    return proxy.toStub(paramRemote);
  }
}
