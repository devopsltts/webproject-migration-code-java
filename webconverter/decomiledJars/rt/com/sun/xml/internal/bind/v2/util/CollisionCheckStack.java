package com.sun.xml.internal.bind.v2.util;

import java.util.AbstractList;
import java.util.Arrays;

public final class CollisionCheckStack<E>
  extends AbstractList<E>
{
  private Object[] data = new Object[16];
  private int[] next = new int[16];
  private int size = 0;
  private boolean latestPushResult = false;
  private boolean useIdentity = true;
  private final int[] initialHash = new int[17];
  
  public CollisionCheckStack() {}
  
  public void setUseIdentity(boolean paramBoolean)
  {
    this.useIdentity = paramBoolean;
  }
  
  public boolean getUseIdentity()
  {
    return this.useIdentity;
  }
  
  public boolean getLatestPushResult()
  {
    return this.latestPushResult;
  }
  
  public boolean push(E paramE)
  {
    if (this.data.length == this.size) {
      expandCapacity();
    }
    this.data[this.size] = paramE;
    int i = hash(paramE);
    boolean bool = findDuplicate(paramE, i);
    this.next[this.size] = this.initialHash[i];
    this.initialHash[i] = (this.size + 1);
    this.size += 1;
    this.latestPushResult = bool;
    return this.latestPushResult;
  }
  
  public void pushNocheck(E paramE)
  {
    if (this.data.length == this.size) {
      expandCapacity();
    }
    this.data[this.size] = paramE;
    this.next[this.size] = -1;
    this.size += 1;
  }
  
  public boolean findDuplicate(E paramE)
  {
    int i = hash(paramE);
    return findDuplicate(paramE, i);
  }
  
  public E get(int paramInt)
  {
    return this.data[paramInt];
  }
  
  public int size()
  {
    return this.size;
  }
  
  private int hash(Object paramObject)
  {
    return ((this.useIdentity ? System.identityHashCode(paramObject) : paramObject.hashCode()) & 0x7FFFFFFF) % this.initialHash.length;
  }
  
  public E pop()
  {
    this.size -= 1;
    Object localObject = this.data[this.size];
    this.data[this.size] = null;
    int i = this.next[this.size];
    if (i >= 0)
    {
      int j = hash(localObject);
      assert (this.initialHash[j] == this.size + 1);
      this.initialHash[j] = i;
    }
    return localObject;
  }
  
  public E peek()
  {
    return this.data[(this.size - 1)];
  }
  
  private boolean findDuplicate(E paramE, int paramInt)
  {
    for (int i = this.initialHash[paramInt]; i != 0; i = this.next[i])
    {
      i--;
      Object localObject = this.data[i];
      if (this.useIdentity)
      {
        if (localObject == paramE) {
          return true;
        }
      }
      else if (paramE.equals(localObject)) {
        return true;
      }
    }
    return false;
  }
  
  private void expandCapacity()
  {
    int i = this.data.length;
    int j = i * 2;
    Object[] arrayOfObject = new Object[j];
    int[] arrayOfInt = new int[j];
    System.arraycopy(this.data, 0, arrayOfObject, 0, i);
    System.arraycopy(this.next, 0, arrayOfInt, 0, i);
    this.data = arrayOfObject;
    this.next = arrayOfInt;
  }
  
  public void reset()
  {
    if (this.size > 0)
    {
      this.size = 0;
      Arrays.fill(this.initialHash, 0);
    }
  }
  
  public String getCycleString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = size() - 1;
    Object localObject1 = get(i);
    localStringBuilder.append(localObject1);
    Object localObject2;
    do
    {
      localStringBuilder.append(" -> ");
      localObject2 = get(--i);
      localStringBuilder.append(localObject2);
    } while (localObject1 != localObject2);
    return localStringBuilder.toString();
  }
}
