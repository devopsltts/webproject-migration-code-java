package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class SingletonResolver<T>
  extends AbstractInstanceResolver<T>
{
  @NotNull
  private final T singleton;
  
  public SingletonResolver(@NotNull T paramT)
  {
    this.singleton = paramT;
  }
  
  @NotNull
  public T resolve(Packet paramPacket)
  {
    return this.singleton;
  }
  
  public void start(WSWebServiceContext paramWSWebServiceContext, WSEndpoint paramWSEndpoint)
  {
    getResourceInjector(paramWSEndpoint).inject(paramWSWebServiceContext, this.singleton);
    invokeMethod(findAnnotatedMethod(this.singleton.getClass(), PostConstruct.class), this.singleton, new Object[0]);
  }
  
  public void dispose()
  {
    invokeMethod(findAnnotatedMethod(this.singleton.getClass(), PreDestroy.class), this.singleton, new Object[0]);
  }
}
