package com.sun.org.apache.xerces.internal.util;

public class SymbolTable
{
  protected static final int TABLE_SIZE = 101;
  protected static final int MAX_HASH_COLLISIONS = 40;
  protected static final int MULTIPLIERS_SIZE = 32;
  protected static final int MULTIPLIERS_MASK = 31;
  protected Entry[] fBuckets = null;
  protected int fTableSize;
  protected transient int fCount;
  protected int fThreshold;
  protected float fLoadFactor;
  protected final int fCollisionThreshold;
  protected int[] fHashMultipliers;
  
  public SymbolTable(int paramInt, float paramFloat)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    }
    if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
      throw new IllegalArgumentException("Illegal Load: " + paramFloat);
    }
    if (paramInt == 0) {
      paramInt = 1;
    }
    this.fLoadFactor = paramFloat;
    this.fTableSize = paramInt;
    this.fBuckets = new Entry[this.fTableSize];
    this.fThreshold = ((int)(this.fTableSize * paramFloat));
    this.fCollisionThreshold = ((int)(40.0F * paramFloat));
    this.fCount = 0;
  }
  
  public SymbolTable(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public SymbolTable()
  {
    this(101, 0.75F);
  }
  
  public String addSymbol(String paramString)
  {
    int i = 0;
    int j = hash(paramString) % this.fTableSize;
    for (Entry localEntry = this.fBuckets[j]; localEntry != null; localEntry = localEntry.next)
    {
      if (localEntry.symbol.equals(paramString)) {
        return localEntry.symbol;
      }
      i++;
    }
    return addSymbol0(paramString, j, i);
  }
  
  private String addSymbol0(String paramString, int paramInt1, int paramInt2)
  {
    if (this.fCount >= this.fThreshold)
    {
      rehash();
      paramInt1 = hash(paramString) % this.fTableSize;
    }
    else if (paramInt2 >= this.fCollisionThreshold)
    {
      rebalance();
      paramInt1 = hash(paramString) % this.fTableSize;
    }
    Entry localEntry = new Entry(paramString, this.fBuckets[paramInt1]);
    this.fBuckets[paramInt1] = localEntry;
    this.fCount += 1;
    return localEntry.symbol;
  }
  
  public String addSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = hash(paramArrayOfChar, paramInt1, paramInt2) % this.fTableSize;
    label88:
    for (Entry localEntry = this.fBuckets[j]; localEntry != null; localEntry = localEntry.next)
    {
      if (paramInt2 == localEntry.characters.length)
      {
        for (int k = 0; k < paramInt2; k++) {
          if (paramArrayOfChar[(paramInt1 + k)] != localEntry.characters[k])
          {
            i++;
            break label88;
          }
        }
        return localEntry.symbol;
      }
      i++;
    }
    return addSymbol0(paramArrayOfChar, paramInt1, paramInt2, j, i);
  }
  
  private String addSymbol0(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.fCount >= this.fThreshold)
    {
      rehash();
      paramInt3 = hash(paramArrayOfChar, paramInt1, paramInt2) % this.fTableSize;
    }
    else if (paramInt4 >= this.fCollisionThreshold)
    {
      rebalance();
      paramInt3 = hash(paramArrayOfChar, paramInt1, paramInt2) % this.fTableSize;
    }
    Entry localEntry = new Entry(paramArrayOfChar, paramInt1, paramInt2, this.fBuckets[paramInt3]);
    this.fBuckets[paramInt3] = localEntry;
    this.fCount += 1;
    return localEntry.symbol;
  }
  
  public int hash(String paramString)
  {
    if (this.fHashMultipliers == null) {
      return paramString.hashCode() & 0x7FFFFFFF;
    }
    return hash0(paramString);
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
  
  public int hash(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (this.fHashMultipliers == null)
    {
      int i = 0;
      for (int j = 0; j < paramInt2; j++) {
        i = i * 31 + paramArrayOfChar[(paramInt1 + j)];
      }
      return i & 0x7FFFFFFF;
    }
    return hash0(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private int hash0(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = 0;
    int[] arrayOfInt = this.fHashMultipliers;
    for (int j = 0; j < paramInt2; j++) {
      i = i * arrayOfInt[(j & 0x1F)] + paramArrayOfChar[(paramInt1 + j)];
    }
    return i & 0x7FFFFFFF;
  }
  
  protected void rehash()
  {
    rehashCommon(this.fBuckets.length * 2 + 1);
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
    this.fThreshold = ((int)(paramInt * this.fLoadFactor));
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
        int k = hash(localEntry2.symbol) % paramInt;
        localEntry2.next = arrayOfEntry2[k];
        arrayOfEntry2[k] = localEntry2;
      }
    }
  }
  
  public boolean containsSymbol(String paramString)
  {
    int i = hash(paramString) % this.fTableSize;
    int j = paramString.length();
    label76:
    for (Entry localEntry = this.fBuckets[i]; localEntry != null; localEntry = localEntry.next) {
      if (j == localEntry.characters.length)
      {
        for (int k = 0; k < j; k++) {
          if (paramString.charAt(k) != localEntry.characters[k]) {
            break label76;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public boolean containsSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = hash(paramArrayOfChar, paramInt1, paramInt2) % this.fTableSize;
    label75:
    for (Entry localEntry = this.fBuckets[i]; localEntry != null; localEntry = localEntry.next) {
      if (paramInt2 == localEntry.characters.length)
      {
        for (int j = 0; j < paramInt2; j++) {
          if (paramArrayOfChar[(paramInt1 + j)] != localEntry.characters[j]) {
            break label75;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  protected static final class Entry
  {
    public final String symbol;
    public final char[] characters;
    public Entry next;
    
    public Entry(String paramString, Entry paramEntry)
    {
      this.symbol = paramString.intern();
      this.characters = new char[paramString.length()];
      paramString.getChars(0, this.characters.length, this.characters, 0);
      this.next = paramEntry;
    }
    
    public Entry(char[] paramArrayOfChar, int paramInt1, int paramInt2, Entry paramEntry)
    {
      this.characters = new char[paramInt2];
      System.arraycopy(paramArrayOfChar, paramInt1, this.characters, 0, paramInt2);
      this.symbol = new String(this.characters).intern();
      this.next = paramEntry;
    }
  }
}
