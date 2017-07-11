package com.sun.org.apache.xerces.internal.util;

public class SymbolHash
{
  protected static final int TABLE_SIZE = 101;
  protected static final int MAX_HASH_COLLISIONS = 40;
  protected static final int MULTIPLIERS_SIZE = 32;
  protected static final int MULTIPLIERS_MASK = 31;
  protected int fTableSize;
  protected Entry[] fBuckets;
  protected int fNum = 0;
  protected int[] fHashMultipliers;
  
  public SymbolHash()
  {
    this(101);
  }
  
  public SymbolHash(int paramInt)
  {
    this.fTableSize = paramInt;
    this.fBuckets = new Entry[this.fTableSize];
  }
  
  public void put(Object paramObject1, Object paramObject2)
  {
    int i = 0;
    int j = hash(paramObject1);
    int k = j % this.fTableSize;
    for (Entry localEntry = this.fBuckets[k]; localEntry != null; localEntry = localEntry.next)
    {
      if (paramObject1.equals(localEntry.key))
      {
        localEntry.value = paramObject2;
        return;
      }
      i++;
    }
    if (this.fNum >= this.fTableSize)
    {
      rehash();
      k = j % this.fTableSize;
    }
    else if ((i >= 40) && ((paramObject1 instanceof String)))
    {
      rebalance();
      k = hash(paramObject1) % this.fTableSize;
    }
    localEntry = new Entry(paramObject1, paramObject2, this.fBuckets[k]);
    this.fBuckets[k] = localEntry;
    this.fNum += 1;
  }
  
  public Object get(Object paramObject)
  {
    int i = hash(paramObject) % this.fTableSize;
    Entry localEntry = search(paramObject, i);
    if (localEntry != null) {
      return localEntry.value;
    }
    return null;
  }
  
  public int getLength()
  {
    return this.fNum;
  }
  
  public int getValues(Object[] paramArrayOfObject, int paramInt)
  {
    int i = 0;
    int j = 0;
    while ((i < this.fTableSize) && (j < this.fNum))
    {
      for (Entry localEntry = this.fBuckets[i]; localEntry != null; localEntry = localEntry.next)
      {
        paramArrayOfObject[(paramInt + j)] = localEntry.value;
        j++;
      }
      i++;
    }
    return this.fNum;
  }
  
  public Object[] getEntries()
  {
    Object[] arrayOfObject = new Object[this.fNum << 1];
    int i = 0;
    int j = 0;
    while ((i < this.fTableSize) && (j < this.fNum << 1))
    {
      for (Entry localEntry = this.fBuckets[i]; localEntry != null; localEntry = localEntry.next)
      {
        arrayOfObject[j] = localEntry.key;
        arrayOfObject[(++j)] = localEntry.value;
        j++;
      }
      i++;
    }
    return arrayOfObject;
  }
  
  public SymbolHash makeClone()
  {
    SymbolHash localSymbolHash = new SymbolHash(this.fTableSize);
    localSymbolHash.fNum = this.fNum;
    localSymbolHash.fHashMultipliers = (this.fHashMultipliers != null ? (int[])this.fHashMultipliers.clone() : null);
    for (int i = 0; i < this.fTableSize; i++) {
      if (this.fBuckets[i] != null) {
        localSymbolHash.fBuckets[i] = this.fBuckets[i].makeClone();
      }
    }
    return localSymbolHash;
  }
  
  public void clear()
  {
    for (int i = 0; i < this.fTableSize; i++) {
      this.fBuckets[i] = null;
    }
    this.fNum = 0;
    this.fHashMultipliers = null;
  }
  
  protected Entry search(Object paramObject, int paramInt)
  {
    for (Entry localEntry = this.fBuckets[paramInt]; localEntry != null; localEntry = localEntry.next) {
      if (paramObject.equals(localEntry.key)) {
        return localEntry;
      }
    }
    return null;
  }
  
  protected int hash(Object paramObject)
  {
    if ((this.fHashMultipliers == null) || (!(paramObject instanceof String))) {
      return paramObject.hashCode() & 0x7FFFFFFF;
    }
    return hash0((String)paramObject);
  }
  
  private int hash0(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    int[] arrayOfInt = this.fHashMultipliers;
    for (int k = 0; k < j; k++) {
      i = i * arrayOfInt[(k & 0x1F)] + paramString.charAt(k);
    }
    return i & 0x7FFFFFFF;
  }
  
  protected void rehash()
  {
    rehashCommon((this.fBuckets.length << 1) + 1);
  }
  
  protected void rebalance()
  {
    if (this.fHashMultipliers == null) {
      this.fHashMultipliers = new int[32];
    }
    PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
    rehashCommon(this.fBuckets.length);
  }
  
  private void rehashCommon(int paramInt)
  {
    int i = this.fBuckets.length;
    Entry[] arrayOfEntry1 = this.fBuckets;
    Entry[] arrayOfEntry2 = new Entry[paramInt];
    this.fBuckets = arrayOfEntry2;
    this.fTableSize = this.fBuckets.length;
    int j = i;
    while (j-- > 0)
    {
      Entry localEntry1 = arrayOfEntry1[j];
      while (localEntry1 != null)
      {
        Entry localEntry2 = localEntry1;
        localEntry1 = localEntry1.next;
        int k = hash(localEntry2.key) % paramInt;
        localEntry2.next = arrayOfEntry2[k];
        arrayOfEntry2[k] = localEntry2;
      }
    }
  }
  
  protected static final class Entry
  {
    public Object key;
    public Object value;
    public Entry next;
    
    public Entry()
    {
      this.key = null;
      this.value = null;
      this.next = null;
    }
    
    public Entry(Object paramObject1, Object paramObject2, Entry paramEntry)
    {
      this.key = paramObject1;
      this.value = paramObject2;
      this.next = paramEntry;
    }
    
    public Entry makeClone()
    {
      Entry localEntry = new Entry();
      localEntry.key = this.key;
      localEntry.value = this.value;
      if (this.next != null) {
        localEntry.next = this.next.makeClone();
      }
      return localEntry;
    }
  }
}
