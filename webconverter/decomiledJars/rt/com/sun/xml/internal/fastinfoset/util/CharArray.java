package com.sun.xml.internal.fastinfoset.util;

public class CharArray
  implements CharSequence
{
  public char[] ch;
  public int start;
  public int length;
  protected int _hash;
  
  protected CharArray() {}
  
  public CharArray(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    set(paramArrayOfChar, paramInt1, paramInt2, paramBoolean);
  }
  
  public final void set(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.ch = new char[paramInt2];
      this.start = 0;
      this.length = paramInt2;
      System.arraycopy(paramArrayOfChar, paramInt1, this.ch, 0, paramInt2);
    }
    else
    {
      this.ch = paramArrayOfChar;
      this.start = paramInt1;
      this.length = paramInt2;
    }
    this._hash = 0;
  }
  
  public final void cloneArray()
  {
    char[] arrayOfChar = new char[this.length];
    System.arraycopy(this.ch, this.start, arrayOfChar, 0, this.length);
    this.ch = arrayOfChar;
    this.start = 0;
  }
  
  public String toString()
  {
    return new String(this.ch, this.start, this.length);
  }
  
  public int hashCode()
  {
    if (this._hash == 0) {
      for (int i = this.start; i < this.start + this.length; i++) {
        this._hash = (31 * this._hash + this.ch[i]);
      }
    }
    return this._hash;
  }
  
  public static final int hashCode(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = 0;
    for (int j = paramInt1; j < paramInt1 + paramInt2; j++) {
      i = 31 * i + paramArrayOfChar[j];
    }
    return i;
  }
  
  public final boolean equalsCharArray(CharArray paramCharArray)
  {
    if (this == paramCharArray) {
      return true;
    }
    if (this.length == paramCharArray.length)
    {
      int i = this.length;
      int j = this.start;
      int k = paramCharArray.start;
      while (i-- != 0) {
        if (this.ch[(j++)] != paramCharArray.ch[(k++)]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public final boolean equalsCharArray(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (this.length == paramInt2)
    {
      int i = this.length;
      int j = this.start;
      int k = paramInt1;
      while (i-- != 0) {
        if (this.ch[(j++)] != paramArrayOfChar[(k++)]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof CharArray))
    {
      CharArray localCharArray = (CharArray)paramObject;
      if (this.length == localCharArray.length)
      {
        int i = this.length;
        int j = this.start;
        int k = localCharArray.start;
        while (i-- != 0) {
          if (this.ch[(j++)] != localCharArray.ch[(k++)]) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public final int length()
  {
    return this.length;
  }
  
  public final char charAt(int paramInt)
  {
    return this.ch[(this.start + paramInt)];
  }
  
  public final CharSequence subSequence(int paramInt1, int paramInt2)
  {
    return new CharArray(this.ch, this.start + paramInt1, paramInt2 - paramInt1, false);
  }
}
