package com.sun.org.apache.xml.internal.utils.res;

public class StringArrayWrapper
{
  private String[] m_string;
  
  public StringArrayWrapper(String[] paramArrayOfString)
  {
    this.m_string = paramArrayOfString;
  }
  
  public String getString(int paramInt)
  {
    return this.m_string[paramInt];
  }
  
  public int getLength()
  {
    return this.m_string.length;
  }
}
