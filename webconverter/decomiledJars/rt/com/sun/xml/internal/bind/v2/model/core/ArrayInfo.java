package com.sun.xml.internal.bind.v2.model.core;

public abstract interface ArrayInfo<T, C>
  extends NonElement<T, C>
{
  public abstract NonElement<T, C> getItemType();
}
