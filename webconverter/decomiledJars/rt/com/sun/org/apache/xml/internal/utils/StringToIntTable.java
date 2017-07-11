package com.sun.org.apache.xml.internal.utils;

public class StringToIntTable
{
  public static final int INVALID_KEY = -10000;
  private int m_blocksize;
  private String[] m_map;
  private int[] m_values;
  private int m_firstFree = 0;
  private int m_mapSize;
  
  public StringToIntTable()
  {
    this.m_blocksize = 8;
    this.m_mapSize = this.m_blocksize;
    this.m_map = new String[this.m_blocksize];
    this.m_values = new int[this.m_blocksize];
  }
  
  public StringToIntTable(int paramInt)
  {
    this.m_blocksize = paramInt;
    this.m_mapSize = paramInt;
    this.m_map = new String[paramInt];
    this.m_values = new int[this.m_blocksize];
  }
  
  public final int getLength()
  {
    return this.m_firstFree;
  }
  
  public final void put(String paramString, int paramInt)
  {
    if (this.m_firstFree + 1 >= this.m_mapSize)
    {
      this.m_mapSize += this.m_blocksize;
      String[] arrayOfString = new String[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfString, 0, this.m_firstFree + 1);
      this.m_map = arrayOfString;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_values, 0, arrayOfInt, 0, this.m_firstFree + 1);
      this.m_values = arrayOfInt;
    }
    this.m_map[this.m_firstFree] = paramString;
    this.m_values[this.m_firstFree] = paramInt;
    this.m_firstFree += 1;
  }
  
  public final int get(String paramString)
  {
    for (int i = 0; i < this.m_firstFree; i++) {
      if (this.m_map[i].equals(paramString)) {
        return this.m_values[i];
      }
    }
    return 55536;
  }
  
  public final int getIgnoreCase(String paramString)
  {
    if (null == paramString) {
      return 55536;
    }
    for (int i = 0; i < this.m_firstFree; i++) {
      if (this.m_map[i].equalsIgnoreCase(paramString)) {
        return this.m_values[i];
      }
    }
    return 55536;
  }
  
  public final boolean contains(String paramString)
  {
    for (int i = 0; i < this.m_firstFree; i++) {
      if (this.m_map[i].equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public final String[] keys()
  {
    String[] arrayOfString = new String[this.m_firstFree];
    for (int i = 0; i < this.m_firstFree; i++) {
      arrayOfString[i] = this.m_map[i];
    }
    return arrayOfString;
  }
}
