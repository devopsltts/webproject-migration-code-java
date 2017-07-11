package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.ThreadLocalContainerResolver;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Engine
{
  private volatile Executor threadPool;
  public final String id;
  private final Container container;
  
  String getId()
  {
    return this.id;
  }
  
  Container getContainer()
  {
    return this.container;
  }
  
  Executor getExecutor()
  {
    return this.threadPool;
  }
  
  public Engine(String paramString, Executor paramExecutor)
  {
    this(paramString, ContainerResolver.getDefault().getContainer(), paramExecutor);
  }
  
  public Engine(String paramString, Container paramContainer, Executor paramExecutor)
  {
    this(paramString, paramContainer);
    this.threadPool = (paramExecutor != null ? wrap(paramExecutor) : null);
  }
  
  public Engine(String paramString)
  {
    this(paramString, ContainerResolver.getDefault().getContainer());
  }
  
  public Engine(String paramString, Container paramContainer)
  {
    this.id = paramString;
    this.container = paramContainer;
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    this.threadPool = (paramExecutor != null ? wrap(paramExecutor) : null);
  }
  
  void addRunnable(Fiber paramFiber)
  {
    if (this.threadPool == null) {
      synchronized (this)
      {
        this.threadPool = wrap(Executors.newCachedThreadPool(new DaemonThreadFactory()));
      }
    }
    this.threadPool.execute(paramFiber);
  }
  
  private Executor wrap(Executor paramExecutor)
  {
    return ContainerResolver.getDefault().wrapExecutor(this.container, paramExecutor);
  }
  
  public Fiber createFiber()
  {
    return new Fiber(this);
  }
  
  private static class DaemonThreadFactory
    implements ThreadFactory
  {
    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix = "jaxws-engine-" + poolNumber.getAndIncrement() + "-thread-";
    
    DaemonThreadFactory() {}
    
    public Thread newThread(Runnable paramRunnable)
    {
      Thread localThread = new Thread(null, paramRunnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
      if (!localThread.isDaemon()) {
        localThread.setDaemon(true);
      }
      if (localThread.getPriority() != 5) {
        localThread.setPriority(5);
      }
      return localThread;
    }
  }
}
