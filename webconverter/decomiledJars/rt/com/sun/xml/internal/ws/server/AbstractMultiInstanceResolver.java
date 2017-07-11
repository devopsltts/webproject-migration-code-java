package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public abstract class AbstractMultiInstanceResolver<T>
  extends AbstractInstanceResolver<T>
{
  protected final Class<T> clazz;
  private WSWebServiceContext webServiceContext;
  protected WSEndpoint owner;
  private final Method postConstructMethod;
  private final Method preDestroyMethod;
  private ResourceInjector resourceInjector;
  
  public AbstractMultiInstanceResolver(Class<T> paramClass)
  {
    this.clazz = paramClass;
    this.postConstructMethod = findAnnotatedMethod(paramClass, PostConstruct.class);
    this.preDestroyMethod = findAnnotatedMethod(paramClass, PreDestroy.class);
  }
  
  protected final void prepare(T paramT)
  {
    assert (this.webServiceContext != null);
    this.resourceInjector.inject(this.webServiceContext, paramT);
    invokeMethod(this.postConstructMethod, paramT, new Object[0]);
  }
  
  protected final T create()
  {
    Object localObject = createNewInstance(this.clazz);
    prepare(localObject);
    return localObject;
  }
  
  public void start(WSWebServiceContext paramWSWebServiceContext, WSEndpoint paramWSEndpoint)
  {
    this.resourceInjector = getResourceInjector(paramWSEndpoint);
    this.webServiceContext = paramWSWebServiceContext;
    this.owner = paramWSEndpoint;
  }
  
  protected final void dispose(T paramT)
  {
    invokeMethod(this.preDestroyMethod, paramT, new Object[0]);
  }
}
