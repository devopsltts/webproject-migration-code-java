package com.sun.xml.internal.ws.api.server;

import java.util.concurrent.Executor;

public class ThreadLocalContainerResolver
  extends ContainerResolver
{
  private ThreadLocal<Container> containerThreadLocal = new ThreadLocal()
  {
    protected Container initialValue()
    {
      return Container.NONE;
    }
  };
  
  public ThreadLocalContainerResolver() {}
  
  public Container getContainer()
  {
    return (Container)this.containerThreadLocal.get();
  }
  
  public Container enterContainer(Container paramContainer)
  {
    Container localContainer = (Container)this.containerThreadLocal.get();
    this.containerThreadLocal.set(paramContainer);
    return localContainer;
  }
  
  public void exitContainer(Container paramContainer)
  {
    this.containerThreadLocal.set(paramContainer);
  }
  
  public Executor wrapExecutor(final Container paramContainer, final Executor paramExecutor)
  {
    if (paramExecutor == null) {
      return null;
    }
    new Executor()
    {
      public void execute(final Runnable paramAnonymousRunnable)
      {
        paramExecutor.execute(new Runnable()
        {
          public void run()
          {
            Container localContainer = ThreadLocalContainerResolver.this.enterContainer(ThreadLocalContainerResolver.2.this.val$container);
            try
            {
              paramAnonymousRunnable.run();
              ThreadLocalContainerResolver.this.exitContainer(localContainer);
            }
            finally
            {
              ThreadLocalContainerResolver.this.exitContainer(localContainer);
            }
          }
        });
      }
    };
  }
}
