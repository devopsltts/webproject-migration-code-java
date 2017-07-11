package com.sun.xml.internal.ws.spi.db;

public abstract interface PropertyGetter
{
  public abstract Class getType();
  
  public abstract <A> A getAnnotation(Class<A> paramClass);
  
  public abstract Object get(Object paramObject);
}
