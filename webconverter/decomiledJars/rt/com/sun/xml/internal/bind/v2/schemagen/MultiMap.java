package com.sun.xml.internal.bind.v2.schemagen;

import java.util.Map;
import java.util.TreeMap;

final class MultiMap<K extends Comparable<K>, V>
  extends TreeMap<K, V>
{
  private final V many;
  
  public MultiMap(V paramV)
  {
    this.many = paramV;
  }
  
  public V put(K paramK, V paramV)
  {
    Object localObject = super.put(paramK, paramV);
    if ((localObject != null) && (!localObject.equals(paramV))) {
      super.put(paramK, this.many);
    }
    return localObject;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    throw new UnsupportedOperationException();
  }
}
