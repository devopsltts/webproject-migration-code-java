package com.sun.org.apache.xml.internal.utils;

public class StringToStringTableVector
{
  private int m_blocksize;
  private StringToStringTable[] m_map;
  private int m_firstFree = 0;
  private int m_mapSize;
  
  public StringToStringTableVector()
  {
    this.m_blocksize = 8;
    this.m_mapSize = this.m_blocksize;
    this.m_map = new StringToStringTable[this.m_blocksize];
  }
  
  public StringToStringTableVector(int paramInt)
  {
    this.m_blocksize = paramInt;
    this.m_mapSize = paramInt;
    this.m_map = new StringToStringTable[paramInt];
  }
  
  public final int getLength()
  {
    return this.m_firstFree;
  }
  
  public final int size()
  {
    return this.m_firstFree;
  }
  
  public final void addElement(StringToStringTable paramStringToStringTable)
  {
    if (this.m_firstFree + 1 >= this.m_mapSize)
    {
      this.m_mapSize += this.m_blocksize;
      StringToStringTable[] arrayOfStringToStringTable = new StringToStringTable[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfStringToStringTable, 0, this.m_firstFree + 1);
      this.m_map = arrayOfStringToStringTable;
    }
    this.m_map[this.m_firstFree] = paramStringToStringTable;
    this.m_firstFree += 1;
  }
  
  public final String get(String paramString)
  {
    for (int i = this.m_firstFree - 1; i >= 0; i--)
    {
      String str = this.m_map[i].get(paramString);
      if (str != null) {
        return str;
      }
    }
    return null;
  }
  
  public final boolean containsKey(String paramString)
  {
    for (int i = this.m_firstFree - 1; i >= 0; i--) {
      if (this.m_map[i].get(paramString) != null) {
        return true;
      }
    }
    return false;
  }
  
  public final void removeLastElem()
  {
    if (this.m_firstFree > 0)
    {
      this.m_map[this.m_firstFree] = null;
      this.m_firstFree -= 1;
    }
  }
  
  public final StringToStringTable elementAt(int paramInt)
  {
    return this.m_map[paramInt];
  }
  
  public final boolean contains(StringToStringTable paramStringToStringTable)
  {
    for (int i = 0; i < this.m_firstFree; i++) {
      if (this.m_map[i].equals(paramStringToStringTable)) {
        return true;
      }
    }
    return false;
  }
}
