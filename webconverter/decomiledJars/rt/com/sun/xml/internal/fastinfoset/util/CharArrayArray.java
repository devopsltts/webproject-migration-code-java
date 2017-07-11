package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class CharArrayArray
  extends ValueArray
{
  private CharArray[] _array;
  private CharArrayArray _readOnlyArray;
  
  public CharArrayArray(int paramInt1, int paramInt2)
  {
    this._array = new CharArray[paramInt1];
    this._maximumCapacity = paramInt2;
  }
  
  public CharArrayArray()
  {
    this(10, Integer.MAX_VALUE);
  }
  
  public final void clear()
  {
    for (int i = 0; i < this._size; i++) {
      this._array[i] = null;
    }
    this._size = 0;
  }
  
  public final CharArray[] getArray()
  {
    if (this._array == null) {
      return null;
    }
    CharArray[] arrayOfCharArray = new CharArray[this._array.length];
    System.arraycopy(this._array, 0, arrayOfCharArray, 0, this._array.length);
    return arrayOfCharArray;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean)
  {
    if (!(paramValueArray instanceof CharArrayArray)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray }));
    }
    setReadOnlyArray((CharArrayArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(CharArrayArray paramCharArrayArray, boolean paramBoolean)
  {
    if (paramCharArrayArray != null)
    {
      this._readOnlyArray = paramCharArrayArray;
      this._readOnlyArraySize = paramCharArrayArray.getSize();
      if (paramBoolean) {
        clear();
      }
    }
  }
  
  public final CharArray get(int paramInt)
  {
    if (this._readOnlyArray == null) {
      return this._array[paramInt];
    }
    if (paramInt < this._readOnlyArraySize) {
      return this._readOnlyArray.get(paramInt);
    }
    return this._array[(paramInt - this._readOnlyArraySize)];
  }
  
  public final void add(CharArray paramCharArray)
  {
    if (this._size == this._array.length) {
      resize();
    }
    this._array[(this._size++)] = paramCharArray;
  }
  
  protected final void resize()
  {
    if (this._size == this._maximumCapacity) {
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
    }
    int i = this._size * 3 / 2 + 1;
    if (i > this._maximumCapacity) {
      i = this._maximumCapacity;
    }
    CharArray[] arrayOfCharArray = new CharArray[i];
    System.arraycopy(this._array, 0, arrayOfCharArray, 0, this._size);
    this._array = arrayOfCharArray;
  }
}
