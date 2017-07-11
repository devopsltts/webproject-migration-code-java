package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import sun.rmi.server.LoaderHandler;

public class RMIClassLoader
{
  private static final RMIClassLoaderSpi defaultProvider = ;
  private static final RMIClassLoaderSpi provider = (RMIClassLoaderSpi)AccessController.doPrivileged(new PrivilegedAction()
  {
    public RMIClassLoaderSpi run()
    {
      return RMIClassLoader.access$000();
    }
  });
  
  private RMIClassLoader() {}
  
  @Deprecated
  public static Class<?> loadClass(String paramString)
    throws MalformedURLException, ClassNotFoundException
  {
    return loadClass((String)null, paramString);
  }
  
  public static Class<?> loadClass(URL paramURL, String paramString)
    throws MalformedURLException, ClassNotFoundException
  {
    return provider.loadClass(paramURL != null ? paramURL.toString() : null, paramString, null);
  }
  
  public static Class<?> loadClass(String paramString1, String paramString2)
    throws MalformedURLException, ClassNotFoundException
  {
    return provider.loadClass(paramString1, paramString2, null);
  }
  
  public static Class<?> loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader)
    throws MalformedURLException, ClassNotFoundException
  {
    return provider.loadClass(paramString1, paramString2, paramClassLoader);
  }
  
  public static Class<?> loadProxyClass(String paramString, String[] paramArrayOfString, ClassLoader paramClassLoader)
    throws ClassNotFoundException, MalformedURLException
  {
    return provider.loadProxyClass(paramString, paramArrayOfString, paramClassLoader);
  }
  
  public static ClassLoader getClassLoader(String paramString)
    throws MalformedURLException, SecurityException
  {
    return provider.getClassLoader(paramString);
  }
  
  public static String getClassAnnotation(Class<?> paramClass)
  {
    return provider.getClassAnnotation(paramClass);
  }
  
  public static RMIClassLoaderSpi getDefaultProviderInstance()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("setFactory"));
    }
    return defaultProvider;
  }
  
  @Deprecated
  public static Object getSecurityContext(ClassLoader paramClassLoader)
  {
    return LoaderHandler.getSecurityContext(paramClassLoader);
  }
  
  private static RMIClassLoaderSpi newDefaultProviderInstance()
  {
    new RMIClassLoaderSpi()
    {
      public Class<?> loadClass(String paramAnonymousString1, String paramAnonymousString2, ClassLoader paramAnonymousClassLoader)
        throws MalformedURLException, ClassNotFoundException
      {
        return LoaderHandler.loadClass(paramAnonymousString1, paramAnonymousString2, paramAnonymousClassLoader);
      }
      
      public Class<?> loadProxyClass(String paramAnonymousString, String[] paramAnonymousArrayOfString, ClassLoader paramAnonymousClassLoader)
        throws MalformedURLException, ClassNotFoundException
      {
        return LoaderHandler.loadProxyClass(paramAnonymousString, paramAnonymousArrayOfString, paramAnonymousClassLoader);
      }
      
      public ClassLoader getClassLoader(String paramAnonymousString)
        throws MalformedURLException
      {
        return LoaderHandler.getClassLoader(paramAnonymousString);
      }
      
      public String getClassAnnotation(Class<?> paramAnonymousClass)
      {
        return LoaderHandler.getClassAnnotation(paramAnonymousClass);
      }
    };
  }
  
  private static RMIClassLoaderSpi initializeProvider()
  {
    String str = System.getProperty("java.rmi.server.RMIClassLoaderSpi");
    if (str != null)
    {
      if (str.equals("default")) {
        return defaultProvider;
      }
      try
      {
        Class localClass = Class.forName(str, false, ClassLoader.getSystemClassLoader()).asSubclass(RMIClassLoaderSpi.class);
        return (RMIClassLoaderSpi)localClass.newInstance();
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new NoClassDefFoundError(localClassNotFoundException.getMessage());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalAccessError(localIllegalAccessException.getMessage());
      }
      catch (InstantiationException localInstantiationException)
      {
        throw new InstantiationError(localInstantiationException.getMessage());
      }
      catch (ClassCastException localClassCastException1)
      {
        LinkageError localLinkageError1 = new LinkageError("provider class not assignable to RMIClassLoaderSpi");
        localLinkageError1.initCause(localClassCastException1);
        throw localLinkageError1;
      }
    }
    Iterator localIterator = ServiceLoader.load(RMIClassLoaderSpi.class, ClassLoader.getSystemClassLoader()).iterator();
    if (localIterator.hasNext()) {
      try
      {
        return (RMIClassLoaderSpi)localIterator.next();
      }
      catch (ClassCastException localClassCastException2)
      {
        LinkageError localLinkageError2 = new LinkageError("provider class not assignable to RMIClassLoaderSpi");
        localLinkageError2.initCause(localClassCastException2);
        throw localLinkageError2;
      }
    }
    return defaultProvider;
  }
}
