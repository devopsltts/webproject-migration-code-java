package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract interface Component
{
  @Nullable
  public abstract <S> S getSPI(@NotNull Class<S> paramClass);
}
