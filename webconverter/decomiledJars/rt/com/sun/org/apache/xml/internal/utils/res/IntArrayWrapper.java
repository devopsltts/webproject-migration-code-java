package com.sun.org.apache.xml.internal.utils.res;

public class IntArrayWrapper
{
  private int[] m_int;
  
  public IntArrayWrapper(int[] paramArrayOfInt)
  {
    this.m_int = paramArrayOfInt;
  }
  
  public int getInt(int paramInt)
  {
    return this.m_int[paramInt];
  }
  
  public int getLength()
  {
    return this.m_int.length;
  }
}
