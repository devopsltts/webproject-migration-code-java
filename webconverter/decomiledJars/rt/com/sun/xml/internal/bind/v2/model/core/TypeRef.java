package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface TypeRef<T, C>
  extends NonElementRef<T, C>
{
  public abstract QName getTagName();
  
  public abstract boolean isNillable();
  
  public abstract String getDefaultValue();
}
