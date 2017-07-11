package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.lang.reflect.Array;
import java.util.AbstractList;

public final class ObjectListImpl
  extends AbstractList
  implements ObjectList
{
  public static final ObjectListImpl EMPTY_LIST = new ObjectListImpl(new Object[0], 0);
  private final Object[] fArray;
  private final int fLength;
  
  public ObjectListImpl(Object[] paramArrayOfObject, int paramInt)
  {
    this.fArray = paramArrayOfObject;
    this.fLength = paramInt;
  }
  
  public int getLength()
  {
    return this.fLength;
  }
  
  public boolean contains(Object paramObject)
  {
    int i;
    if (paramObject == null) {
      for (i = 0; i < this.fLength; i++) {
        if (this.fArray[i] == null) {
          return true;
        }
      }
    } else {
      for (i = 0; i < this.fLength; i++) {
        if (paramObject.equals(this.fArray[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Object item(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.fLength)) {
      return null;
    }
    return this.fArray[paramInt];
  }
  
  public Object get(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.fLength)) {
      return this.fArray[paramInt];
    }
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size()
  {
    return getLength();
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[this.fLength];
    toArray0(arrayOfObject);
    return arrayOfObject;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length < this.fLength)
    {
      Class localClass1 = paramArrayOfObject.getClass();
      Class localClass2 = localClass1.getComponentType();
      paramArrayOfObject = (Object[])Array.newInstance(localClass2, this.fLength);
    }
    toArray0(paramArrayOfObject);
    if (paramArrayOfObject.length > this.fLength) {
      paramArrayOfObject[this.fLength] = null;
    }
    return paramArrayOfObject;
  }
  
  private void toArray0(Object[] paramArrayOfObject)
  {
    if (this.fLength > 0) {
      System.arraycopy(this.fArray, 0, paramArrayOfObject, 0, this.fLength);
    }
  }
}
