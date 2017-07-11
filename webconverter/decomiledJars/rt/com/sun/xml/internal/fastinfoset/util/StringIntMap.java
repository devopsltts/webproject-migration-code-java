package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class StringIntMap
  extends KeyIntMap
{
  protected static final Entry NULL_ENTRY = new Entry(null, 0, -1, null);
  protected StringIntMap _readOnlyMap;
  protected Entry _lastEntry = NULL_ENTRY;
  protected Entry[] _table = new Entry[this._capacity];
  protected int _index;
  protected int _totalCharacterCount;
  
  public StringIntMap(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
  }
  
  public StringIntMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public StringIntMap()
  {
    this(16, 0.75F);
  }
  
  public void clear()
  {
    for (int i = 0; i < this._table.length; i++) {
      this._table[i] = null;
    }
    this._lastEntry = NULL_ENTRY;
    this._size = 0;
    this._index = this._readOnlyMapSize;
    this._totalCharacterCount = 0;
  }
  
  public void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean)
  {
    if (!(paramKeyIntMap instanceof StringIntMap)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap }));
    }
    setReadOnlyMap((StringIntMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(StringIntMap paramStringIntMap, boolean paramBoolean)
  {
    this._readOnlyMap = paramStringIntMap;
    if (this._readOnlyMap != null)
    {
      this._readOnlyMapSize = this._readOnlyMap.size();
      this._index = (this._size + this._readOnlyMapSize);
      if (paramBoolean) {
        clear();
      }
    }
    else
    {
      this._readOnlyMapSize = 0;
      this._index = this._size;
    }
  }
  
  public final int getNextIndex()
  {
    return this._index++;
  }
  
  public final int getIndex()
  {
    return this._index;
  }
  
  public final int obtainIndex(String paramString)
  {
    int i = hashHash(paramString.hashCode());
    if (this._readOnlyMap != null)
    {
      j = this._readOnlyMap.get(paramString, i);
      if (j != -1) {
        return j;
      }
    }
    int j = indexFor(i, this._table.length);
    for (Entry localEntry = this._table[j]; localEntry != null; localEntry = localEntry._next) {
      if ((localEntry._hash == i) && (eq(paramString, localEntry._key))) {
        return localEntry._value;
      }
    }
    addEntry(paramString, i, j);
    return -1;
  }
  
  public final void add(String paramString)
  {
    int i = hashHash(paramString.hashCode());
    int j = indexFor(i, this._table.length);
    addEntry(paramString, i, j);
  }
  
  public final int get(String paramString)
  {
    if (paramString == this._lastEntry._key) {
      return this._lastEntry._value;
    }
    return get(paramString, hashHash(paramString.hashCode()));
  }
  
  public final int getTotalCharacterCount()
  {
    return this._totalCharacterCount;
  }
  
  private final int get(String paramString, int paramInt)
  {
    if (this._readOnlyMap != null)
    {
      i = this._readOnlyMap.get(paramString, paramInt);
      if (i != -1) {
        return i;
      }
    }
    int i = indexFor(paramInt, this._table.length);
    for (Entry localEntry = this._table[i]; localEntry != null; localEntry = localEntry._next) {
      if ((localEntry._hash == paramInt) && (eq(paramString, localEntry._key)))
      {
        this._lastEntry = localEntry;
        return localEntry._value;
      }
    }
    return -1;
  }
  
  private final void addEntry(String paramString, int paramInt1, int paramInt2)
  {
    Entry localEntry = this._table[paramInt2];
    this._table[paramInt2] = new Entry(paramString, paramInt1, this._index++, localEntry);
    this._totalCharacterCount += paramString.length();
    if (this._size++ >= this._threshold) {
      resize(2 * this._table.length);
    }
  }
  
  protected final void resize(int paramInt)
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
  
  protected static class Entry
    extends KeyIntMap.BaseEntry
  {
    final String _key;
    Entry _next;
    
    public Entry(String paramString, int paramInt1, int paramInt2, Entry paramEntry)
    {
      super(paramInt2);
      this._key = paramString;
      this._next = paramEntry;
    }
  }
}
