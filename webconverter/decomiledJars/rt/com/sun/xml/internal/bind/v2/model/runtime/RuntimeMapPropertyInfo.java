package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import java.lang.reflect.Type;

public abstract interface RuntimeMapPropertyInfo
  extends RuntimePropertyInfo, MapPropertyInfo<Type, Class>
{
  public abstract RuntimeNonElement getKeyType();
  
  public abstract RuntimeNonElement getValueType();
}
