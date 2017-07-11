package com.sun.corba.se.impl.orbutil;

import java.util.ArrayList;

public class DenseIntMapImpl
{
  private ArrayList list = new ArrayList();
  
  public DenseIntMapImpl() {}
  
  private void checkKey(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Key must be >= 0.");
    }
  }
  
  public Object get(int paramInt)
  {
    checkKey(paramInt);
    Object localObject = null;
    if (paramInt < this.list.size()) {
      localObject = this.list.get(paramInt);
    }
    return localObject;
  }
  
  public void set(int paramInt, Object paramObject)
  {
    checkKey(paramInt);
    extend(paramInt);
    this.list.set(paramInt, paramObject);
  }
  
  private void extend(int paramInt)
  {
    if (paramInt >= this.list.size())
    {
      this.list.ensureCapacity(paramInt + 1);
      int i = this.list.size();
      while (i++ <= paramInt) {
        this.list.add(null);
      }
    }
  }
}
