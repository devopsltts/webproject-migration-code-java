package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract interface AsyncProviderCallback<T>
{
  public abstract void send(@Nullable T paramT);
  
  public abstract void sendError(@NotNull Throwable paramThrowable);
}
