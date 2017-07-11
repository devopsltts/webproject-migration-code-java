package com.sun.org.apache.xml.internal.utils.res;

public class LongArrayWrapper
{
  private long[] m_long;
  
  public LongArrayWrapper(long[] paramArrayOfLong)
  {
    this.m_long = paramArrayOfLong;
  }
  
  public long getLong(int paramInt)
  {
    return this.m_long[paramInt];
  }
  
  public int getLength()
  {
    return this.m_long.length;
  }
}
