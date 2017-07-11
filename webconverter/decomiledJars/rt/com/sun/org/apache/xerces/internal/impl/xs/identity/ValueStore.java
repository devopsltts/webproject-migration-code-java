package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.ShortList;

public abstract interface ValueStore
{
  public abstract void addValue(Field paramField, Object paramObject, short paramShort, ShortList paramShortList);
  
  public abstract void reportError(String paramString, Object[] paramArrayOfObject);
}
