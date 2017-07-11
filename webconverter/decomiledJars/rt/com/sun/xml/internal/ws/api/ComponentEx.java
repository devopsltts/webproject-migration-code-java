package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;

public abstract interface ComponentEx
  extends Component
{
  @NotNull
  public abstract <S> Iterable<S> getIterableSPI(@NotNull Class<S> paramClass);
}
