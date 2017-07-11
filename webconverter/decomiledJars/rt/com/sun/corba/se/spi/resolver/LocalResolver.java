package com.sun.corba.se.spi.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public abstract interface LocalResolver
  extends Resolver
{
  public abstract void register(String paramString, Closure paramClosure);
}
