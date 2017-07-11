package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import sun.nio.ch.DefaultAsynchronousChannelProvider;

public abstract class AsynchronousChannelProvider
{
  private static Void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("asynchronousChannelProvider"));
    }
    return null;
  }
  
  private AsynchronousChannelProvider(Void paramVoid) {}
  
  protected AsynchronousChannelProvider()
  {
    this(checkPermission());
  }
  
  public static AsynchronousChannelProvider provider()
  {
    return ProviderHolder.provider;
  }
  
  public abstract AsynchronousChannelGroup openAsynchronousChannelGroup(int paramInt, ThreadFactory paramThreadFactory)
    throws IOException;
  
  public abstract AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService paramExecutorService, int paramInt)
    throws IOException;
  
  public abstract AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup paramAsynchronousChannelGroup)
    throws IOException;
  
  public abstract AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup paramAsynchronousChannelGroup)
    throws IOException;
  
  private static class ProviderHolder
  {
    static final AsynchronousChannelProvider provider = ;
    
    private ProviderHolder() {}
    
    private static AsynchronousChannelProvider load()
    {
      (AsynchronousChannelProvider)AccessController.doPrivileged(new PrivilegedAction()
      {
        public AsynchronousChannelProvider run()
        {
          AsynchronousChannelProvider localAsynchronousChannelProvider = AsynchronousChannelProvider.ProviderHolder.access$000();
          if (localAsynchronousChannelProvider != null) {
            return localAsynchronousChannelProvider;
          }
          localAsynchronousChannelProvider = AsynchronousChannelProvider.ProviderHolder.access$100();
          if (localAsynchronousChannelProvider != null) {
            return localAsynchronousChannelProvider;
          }
          return DefaultAsynchronousChannelProvider.create();
        }
      });
    }
    
    private static AsynchronousChannelProvider loadProviderFromProperty()
    {
      String str = System.getProperty("java.nio.channels.spi.AsynchronousChannelProvider");
      if (str == null) {
        return null;
      }
      try
      {
        Class localClass = Class.forName(str, true, ClassLoader.getSystemClassLoader());
        return (AsynchronousChannelProvider)localClass.newInstance();
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new ServiceConfigurationError(null, localClassNotFoundException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new ServiceConfigurationError(null, localIllegalAccessException);
      }
      catch (InstantiationException localInstantiationException)
      {
        throw new ServiceConfigurationError(null, localInstantiationException);
      }
      catch (SecurityException localSecurityException)
      {
        throw new ServiceConfigurationError(null, localSecurityException);
      }
    }
    
    private static AsynchronousChannelProvider loadProviderAsService()
    {
      ServiceLoader localServiceLoader = ServiceLoader.load(AsynchronousChannelProvider.class, ClassLoader.getSystemClassLoader());
      Iterator localIterator = localServiceLoader.iterator();
      do
      {
        try
        {
          return localIterator.hasNext() ? (AsynchronousChannelProvider)localIterator.next() : null;
        }
        catch (ServiceConfigurationError localServiceConfigurationError) {}
      } while ((localServiceConfigurationError.getCause() instanceof SecurityException));
      throw localServiceConfigurationError;
    }
  }
}
