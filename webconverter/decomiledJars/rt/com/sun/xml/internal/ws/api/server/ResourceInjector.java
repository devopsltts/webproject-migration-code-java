package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.server.DefaultResourceInjector;

public abstract class ResourceInjector
{
  public static final ResourceInjector STANDALONE = new DefaultResourceInjector();
  
  public ResourceInjector() {}
  
  public abstract void inject(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull Object paramObject);
}
