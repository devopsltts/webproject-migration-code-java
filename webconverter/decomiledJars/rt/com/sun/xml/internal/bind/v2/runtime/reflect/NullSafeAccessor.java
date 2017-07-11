package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;

public class NullSafeAccessor<B, V, P>
  extends Accessor<B, V>
{
  private final Accessor<B, V> core;
  private final Lister<B, V, ?, P> lister;
  
  public NullSafeAccessor(Accessor<B, V> paramAccessor, Lister<B, V, ?, P> paramLister)
  {
    super(paramAccessor.getValueType());
    this.core = paramAccessor;
    this.lister = paramLister;
  }
  
  public V get(B paramB)
    throws AccessorException
  {
    Object localObject1 = this.core.get(paramB);
    if (localObject1 == null)
    {
      Object localObject2 = this.lister.startPacking(paramB, this.core);
      this.lister.endPacking(localObject2, paramB, this.core);
      localObject1 = this.core.get(paramB);
    }
    return localObject1;
  }
  
  public void set(B paramB, V paramV)
    throws AccessorException
  {
    this.core.set(paramB, paramV);
  }
}
