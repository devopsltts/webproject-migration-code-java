package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.server.provider.AsyncProviderInvokerTube;
import com.sun.xml.internal.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.internal.ws.server.provider.SyncProviderInvokerTube;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ProviderInvokerTubeFactory<T>
{
  private static final ProviderInvokerTubeFactory DEFAULT = new DefaultProviderInvokerTubeFactory(null);
  private static final Logger logger = Logger.getLogger(ProviderInvokerTubeFactory.class.getName());
  
  public ProviderInvokerTubeFactory() {}
  
  protected abstract ProviderInvokerTube<T> doCreate(@NotNull Class<T> paramClass, @NotNull Invoker paramInvoker, @NotNull ProviderArgumentsBuilder<?> paramProviderArgumentsBuilder, boolean paramBoolean);
  
  public static <T> ProviderInvokerTube<T> create(@Nullable ClassLoader paramClassLoader, @NotNull Container paramContainer, @NotNull Class<T> paramClass, @NotNull Invoker paramInvoker, @NotNull ProviderArgumentsBuilder<?> paramProviderArgumentsBuilder, boolean paramBoolean)
  {
    Iterator localIterator = ServiceFinder.find(ProviderInvokerTubeFactory.class, paramClassLoader, paramContainer).iterator();
    while (localIterator.hasNext())
    {
      ProviderInvokerTubeFactory localProviderInvokerTubeFactory = (ProviderInvokerTubeFactory)localIterator.next();
      ProviderInvokerTube localProviderInvokerTube = localProviderInvokerTubeFactory.doCreate(paramClass, paramInvoker, paramProviderArgumentsBuilder, paramBoolean);
      if (localProviderInvokerTube != null)
      {
        if (logger.isLoggable(Level.FINE)) {
          logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { localProviderInvokerTubeFactory.getClass(), localProviderInvokerTube });
        }
        return localProviderInvokerTube;
      }
    }
    return DEFAULT.createDefault(paramClass, paramInvoker, paramProviderArgumentsBuilder, paramBoolean);
  }
  
  protected ProviderInvokerTube<T> createDefault(@NotNull Class<T> paramClass, @NotNull Invoker paramInvoker, @NotNull ProviderArgumentsBuilder<?> paramProviderArgumentsBuilder, boolean paramBoolean)
  {
    return paramBoolean ? new AsyncProviderInvokerTube(paramInvoker, paramProviderArgumentsBuilder) : new SyncProviderInvokerTube(paramInvoker, paramProviderArgumentsBuilder);
  }
  
  private static class DefaultProviderInvokerTubeFactory<T>
    extends ProviderInvokerTubeFactory<T>
  {
    private DefaultProviderInvokerTubeFactory() {}
    
    public ProviderInvokerTube<T> doCreate(@NotNull Class<T> paramClass, @NotNull Invoker paramInvoker, @NotNull ProviderArgumentsBuilder<?> paramProviderArgumentsBuilder, boolean paramBoolean)
    {
      return createDefault(paramClass, paramInvoker, paramProviderArgumentsBuilder, paramBoolean);
    }
  }
}
