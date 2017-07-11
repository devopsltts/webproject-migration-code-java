package com.sun.xml.internal.bind.v2.model.core;

public abstract interface NonElementRef<T, C>
{
  public abstract NonElement<T, C> getTarget();
  
  public abstract PropertyInfo<T, C> getSource();
}
