package com.sun.net.httpserver.spi;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import jdk.Exported;
import sun.net.httpserver.DefaultHttpServerProvider;

@Exported
public abstract class HttpServerProvider
{
  private static final Object lock = new Object();
  private static HttpServerProvider provider = null;
  
  public abstract HttpServer createHttpServer(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException;
  
  public abstract HttpsServer createHttpsServer(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException;
  
  protected HttpServerProvider()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("httpServerProvider"));
    }
  }
  
  private static boolean loadProviderFromProperty()
  {
    String str = System.getProperty("com.sun.net.httpserver.HttpServerProvider");
    if (str == null) {
      return false;
    }
    try
    {
      Class localClass = Class.forName(str, true, ClassLoader.getSystemClassLoader());
      provider = (HttpServerProvider)localClass.newInstance();
      return true;
    }
    catch (ClassNotFoundException|IllegalAccessException|InstantiationException|SecurityException localClassNotFoundException)
    {
      throw new ServiceConfigurationError(null, localClassNotFoundException);
    }
  }
  
  private static boolean loadProviderAsService()
  {
    Iterator localIterator = ServiceLoader.load(HttpServerProvider.class, ClassLoader.getSystemClassLoader()).iterator();
    do
    {
      try
      {
        if (!localIterator.hasNext()) {
          return false;
        }
        provider = (HttpServerProvider)localIterator.next();
        return true;
      }
      catch (ServiceConfigurationError localServiceConfigurationError) {}
    } while ((localServiceConfigurationError.getCause() instanceof SecurityException));
    throw localServiceConfigurationError;
  }
  
  public static HttpServerProvider provider()
  {
    synchronized (lock)
    {
      if (provider != null) {
        return provider;
      }
      (HttpServerProvider)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          if (HttpServerProvider.access$000()) {
            return HttpServerProvider.provider;
          }
          if (HttpServerProvider.access$200()) {
            return HttpServerProvider.provider;
          }
          HttpServerProvider.access$102(new DefaultHttpServerProvider());
          return HttpServerProvider.provider;
        }
      });
    }
  }
}
