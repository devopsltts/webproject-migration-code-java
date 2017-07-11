package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.StringList;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Vector;

public final class StringListImpl
  extends AbstractList
  implements StringList
{
  public static final StringListImpl EMPTY_LIST = new StringListImpl(new String[0], 0);
  private final String[] fArray;
  private final int fLength;
  private final Vector fVector;
  
  public StringListImpl(Vector paramVector)
  {
    this.fVector = paramVector;
    this.fLength = (paramVector == null ? 0 : paramVector.size());
    this.fArray = null;
  }
  
  public StringListImpl(String[] paramArrayOfString, int paramInt)
  {
    this.fArray = paramArrayOfString;
    this.fLength = paramInt;
    this.fVector = null;
  }
  
  public int getLength()
  {
    return this.fLength;
  }
  
  public boolean contains(String paramString)
  {
    if (this.fVector != null) {
      return this.fVector.contains(paramString);
    }
    int i;
    if (paramString == null) {
      for (i = 0; i < this.fLength; i++) {
        if (this.fArray[i] == null) {
          return true;
        }
      }
    } else {
      for (i = 0; i < this.fLength; i++) {
        if (paramString.equals(this.fArray[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  public String item(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.fLength)) {
      return null;
    }
    if (this.fVector != null) {
      return (String)this.fVector.elementAt(paramInt);
    }
    return this.fArray[paramInt];
  }
  
  public Object get(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.fLength))
    {
      if (this.fVector != null) {
        return this.fVector.elementAt(paramInt);
      }
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
    if (this.fVector != null) {
      return this.fVector.toArray();
    }
    Object[] arrayOfObject = new Object[this.fLength];
    toArray0(arrayOfObject);
    return arrayOfObject;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    if (this.fVector != null) {
      return this.fVector.toArray(paramArrayOfObject);
    }
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
