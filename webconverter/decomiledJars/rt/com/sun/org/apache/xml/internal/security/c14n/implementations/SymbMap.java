package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.util.ArrayList;
import java.util.List;

class SymbMap
  implements Cloneable
{
  int free = 23;
  NameSpaceSymbEntry[] entries = new NameSpaceSymbEntry[this.free];
  String[] keys = new String[this.free];
  
  SymbMap() {}
  
  void put(String paramString, NameSpaceSymbEntry paramNameSpaceSymbEntry)
  {
    int i = index(paramString);
    String str = this.keys[i];
    this.keys[i] = paramString;
    this.entries[i] = paramNameSpaceSymbEntry;
    if (((str == null) || (!str.equals(paramString))) && (--this.free == 0))
    {
      this.free = this.entries.length;
      int j = this.free << 2;
      rehash(j);
    }
  }
  
  List<NameSpaceSymbEntry> entrySet()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < this.entries.length; i++) {
      if ((this.entries[i] != null) && (!"".equals(this.entries[i].uri))) {
        localArrayList.add(this.entries[i]);
      }
    }
    return localArrayList;
  }
  
  protected int index(Object paramObject)
  {
    String[] arrayOfString = this.keys;
    int i = arrayOfString.length;
    int j = (paramObject.hashCode() & 0x7FFFFFFF) % i;
    String str = arrayOfString[j];
    if ((str == null) || (str.equals(paramObject))) {
      return j;
    }
    i--;
    do
    {
      j++;
      j = j == i ? 0 : j;
      str = arrayOfString[j];
    } while ((str != null) && (!str.equals(paramObject)));
    return j;
  }
  
  protected void rehash(int paramInt)
  {
    int i = this.keys.length;
    String[] arrayOfString = this.keys;
    NameSpaceSymbEntry[] arrayOfNameSpaceSymbEntry = this.entries;
    this.keys = new String[paramInt];
    this.entries = new NameSpaceSymbEntry[paramInt];
    int j = i;
    while (j-- > 0) {
      if (arrayOfString[j] != null)
      {
        String str = arrayOfString[j];
        int k = index(str);
        this.keys[k] = str;
        this.entries[k] = arrayOfNameSpaceSymbEntry[j];
      }
    }
  }
  
  NameSpaceSymbEntry get(String paramString)
  {
    return this.entries[index(paramString)];
  }
  
  protected Object clone()
  {
    try
    {
      SymbMap localSymbMap = (SymbMap)super.clone();
      localSymbMap.entries = new NameSpaceSymbEntry[this.entries.length];
      System.arraycopy(this.entries, 0, localSymbMap.entries, 0, this.entries.length);
      localSymbMap.keys = new String[this.keys.length];
      System.arraycopy(this.keys, 0, localSymbMap.keys, 0, this.keys.length);
      return localSymbMap;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
    }
    return null;
  }
}
