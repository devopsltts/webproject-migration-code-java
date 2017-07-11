package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;

public class LocalNameQualifiedNamesMap
  extends KeyIntMap
{
  private LocalNameQualifiedNamesMap _readOnlyMap;
  private int _index;
  private Entry[] _table = new Entry[this._capacity];
  
  public LocalNameQualifiedNamesMap(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
  }
  
  public LocalNameQualifiedNamesMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public LocalNameQualifiedNamesMap()
  {
    this(16, 0.75F);
  }
  
  public final void clear()
  {
    for (int i = 0; i < this._table.length; i++) {
      this._table[i] = null;
    }
    this._size = 0;
    if (this._readOnlyMap != null) {
      this._index = this._readOnlyMap.getIndex();
    } else {
      this._index = 0;
    }
  }
  
  public final void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean)
  {
    if (!(paramKeyIntMap instanceof LocalNameQualifiedNamesMap)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap }));
    }
    setReadOnlyMap((LocalNameQualifiedNamesMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(LocalNameQualifiedNamesMap paramLocalNameQualifiedNamesMap, boolean paramBoolean)
  {
    this._readOnlyMap = paramLocalNameQualifiedNamesMap;
    if (this._readOnlyMap != null)
    {
      this._readOnlyMapSize = this._readOnlyMap.size();
      this._index = this._readOnlyMap.getIndex();
      if (paramBoolean) {
        clear();
      }
    }
    else
    {
      this._readOnlyMapSize = 0;
      this._index = 0;
    }
  }
  
  public final boolean isQNameFromReadOnlyMap(QualifiedName paramQualifiedName)
  {
    return (this._readOnlyMap != null) && (paramQualifiedName.index <= this._readOnlyMap.getIndex());
  }
  
  public final int getNextIndex()
  {
    return this._index++;
  }
  
  public final int getIndex()
  {
    return this._index;
  }
  
  public final Entry obtainEntry(String paramString)
  {
    int i = hashHash(paramString.hashCode());
    if (this._readOnlyMap != null)
    {
      Entry localEntry1 = this._readOnlyMap.getEntry(paramString, i);
      if (localEntry1 != null) {
        return localEntry1;
      }
    }
    int j = indexFor(i, this._table.length);
    for (Entry localEntry2 = this._table[j]; localEntry2 != null; localEntry2 = localEntry2._next) {
      if ((localEntry2._hash == i) && (eq(paramString, localEntry2._key))) {
        return localEntry2;
      }
    }
    return addEntry(paramString, i, j);
  }
  
  public final Entry obtainDynamicEntry(String paramString)
  {
    int i = hashHash(paramString.hashCode());
    int j = indexFor(i, this._table.length);
    for (Entry localEntry = this._table[j]; localEntry != null; localEntry = localEntry._next) {
      if ((localEntry._hash == i) && (eq(paramString, localEntry._key))) {
        return localEntry;
      }
    }
    return addEntry(paramString, i, j);
  }
  
  private final Entry getEntry(String paramString, int paramInt)
  {
    if (this._readOnlyMap != null)
    {
      Entry localEntry1 = this._readOnlyMap.getEntry(paramString, paramInt);
      if (localEntry1 != null) {
        return localEntry1;
      }
    }
    int i = indexFor(paramInt, this._table.length);
    for (Entry localEntry2 = this._table[i]; localEntry2 != null; localEntry2 = localEntry2._next) {
      if ((localEntry2._hash == paramInt) && (eq(paramString, localEntry2._key))) {
        return localEntry2;
      }
    }
    return null;
  }
  
  private final Entry addEntry(String paramString, int paramInt1, int paramInt2)
  {
    Entry localEntry = this._table[paramInt2];
    this._table[paramInt2] = new Entry(paramString, paramInt1, localEntry);
    localEntry = this._table[paramInt2];
    if (this._size++ >= this._threshold) {
      resize(2 * this._table.length);
    }
    return localEntry;
  }
  
  private final void resize(int paramInt)
  {
    this._capacity = paramInt;
    Entry[] arrayOfEntry1 = this._table;
    int i = arrayOfEntry1.length;
    if (i == 1048576)
    {
      this._threshold = Integer.MAX_VALUE;
      return;
    }
    Entry[] arrayOfEntry2 = new Entry[this._capacity];
    transfer(arrayOfEntry2);
    this._table = arrayOfEntry2;
    this._threshold = ((int)(this._capacity * this._loadFactor));
  }
  
  private final void transfer(Entry[] paramArrayOfEntry)
  {
    Entry[] arrayOfEntry = this._table;
    int i = paramArrayOfEntry.length;
    for (int j = 0; j < arrayOfEntry.length; j++)
    {
      Object localObject = arrayOfEntry[j];
      if (localObject != null)
      {
        arrayOfEntry[j] = null;
        do
        {
          Entry localEntry = ((Entry)localObject)._next;
          int k = indexFor(((Entry)localObject)._hash, i);
          ((Entry)localObject)._next = paramArrayOfEntry[k];
          paramArrayOfEntry[k] = localObject;
          localObject = localEntry;
        } while (localObject != null);
      }
    }
  }
  
  private final boolean eq(String paramString1, String paramString2)
  {
    return (paramString1 == paramString2) || (paramString1.equals(paramString2));
  }
  
  public static class Entry
  {
    final String _key;
    final int _hash;
    public QualifiedName[] _value;
    public int _valueIndex;
    Entry _next;
    
    public Entry(String paramString, int paramInt, Entry paramEntry)
    {
      this._key = paramString;
      this._hash = paramInt;
      this._next = paramEntry;
      this._value = new QualifiedName[1];
    }
    
    public void addQualifiedName(QualifiedName paramQualifiedName)
    {
      if (this._valueIndex < this._value.length)
      {
        this._value[(this._valueIndex++)] = paramQualifiedName;
      }
      else if (this._valueIndex == this._value.length)
      {
        QualifiedName[] arrayOfQualifiedName = new QualifiedName[this._valueIndex * 3 / 2 + 1];
        System.arraycopy(this._value, 0, arrayOfQualifiedName, 0, this._valueIndex);
        this._value = arrayOfQualifiedName;
        this._value[(this._valueIndex++)] = paramQualifiedName;
      }
    }
  }
}
