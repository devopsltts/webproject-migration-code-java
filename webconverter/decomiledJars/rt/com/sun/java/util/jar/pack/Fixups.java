package com.sun.java.util.jar.pack;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

final class Fixups
  extends AbstractCollection<Fixup>
{
  byte[] bytes;
  int head;
  int tail;
  int size;
  ConstantPool.Entry[] entries;
  int[] bigDescs;
  private static final int MINBIGSIZE = 1;
  private static final int[] noBigDescs = { 1 };
  private static final int LOC_SHIFT = 1;
  private static final int FMT_MASK = 1;
  private static final byte UNUSED_BYTE = 0;
  private static final byte OVERFLOW_BYTE = -1;
  private static final int BIGSIZE = 0;
  private static final int U2_FORMAT = 0;
  private static final int U1_FORMAT = 1;
  private static final int SPECIAL_LOC = 0;
  private static final int SPECIAL_FMT = 0;
  
  Fixups(byte[] paramArrayOfByte)
  {
    this.bytes = paramArrayOfByte;
    this.entries = new ConstantPool.Entry[3];
    this.bigDescs = noBigDescs;
  }
  
  Fixups()
  {
    this((byte[])null);
  }
  
  Fixups(byte[] paramArrayOfByte, Collection<Fixup> paramCollection)
  {
    this(paramArrayOfByte);
    addAll(paramCollection);
  }
  
  Fixups(Collection<Fixup> paramCollection)
  {
    this((byte[])null);
    addAll(paramCollection);
  }
  
  public int size()
  {
    return this.size;
  }
  
  public void trimToSize()
  {
    if (this.size != this.entries.length)
    {
      ConstantPool.Entry[] arrayOfEntry = this.entries;
      this.entries = new ConstantPool.Entry[this.size];
      System.arraycopy(arrayOfEntry, 0, this.entries, 0, this.size);
    }
    int i = this.bigDescs[0];
    if (i == 1)
    {
      this.bigDescs = noBigDescs;
    }
    else if (i != this.bigDescs.length)
    {
      int[] arrayOfInt = this.bigDescs;
      this.bigDescs = new int[i];
      System.arraycopy(arrayOfInt, 0, this.bigDescs, 0, i);
    }
  }
  
  public void visitRefs(Collection<ConstantPool.Entry> paramCollection)
  {
    for (int i = 0; i < this.size; i++) {
      paramCollection.add(this.entries[i]);
    }
  }
  
  public void clear()
  {
    if (this.bytes != null)
    {
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Fixup localFixup = (Fixup)localIterator.next();
        storeIndex(localFixup.location(), localFixup.format(), 0);
      }
    }
    this.size = 0;
    if (this.bigDescs != noBigDescs) {
      this.bigDescs[0] = 1;
    }
  }
  
  public byte[] getBytes()
  {
    return this.bytes;
  }
  
  public void setBytes(byte[] paramArrayOfByte)
  {
    if (this.bytes == paramArrayOfByte) {
      return;
    }
    ArrayList localArrayList1 = null;
    assert ((localArrayList1 = new ArrayList(this)) != null);
    if ((this.bytes == null) || (paramArrayOfByte == null))
    {
      ArrayList localArrayList2 = new ArrayList(this);
      clear();
      this.bytes = paramArrayOfByte;
      addAll(localArrayList2);
    }
    else
    {
      this.bytes = paramArrayOfByte;
    }
    assert (localArrayList1.equals(new ArrayList(this)));
  }
  
  static int fmtLen(int paramInt)
  {
    return 1 + (paramInt - 1) / -1;
  }
  
  static int descLoc(int paramInt)
  {
    return paramInt >>> 1;
  }
  
  static int descFmt(int paramInt)
  {
    return paramInt & 0x1;
  }
  
  static int descEnd(int paramInt)
  {
    return descLoc(paramInt) + fmtLen(descFmt(paramInt));
  }
  
  static int makeDesc(int paramInt1, int paramInt2)
  {
    int i = paramInt1 << 1 | paramInt2;
    assert (descLoc(i) == paramInt1);
    assert (descFmt(i) == paramInt2);
    return i;
  }
  
  int fetchDesc(int paramInt1, int paramInt2)
  {
    int i = this.bytes[paramInt1];
    assert (i != -1);
    int j;
    if (paramInt2 == 0)
    {
      int k = this.bytes[(paramInt1 + 1)];
      j = ((i & 0xFF) << 8) + (k & 0xFF);
    }
    else
    {
      j = i & 0xFF;
    }
    return j + (paramInt1 << 1);
  }
  
  boolean storeDesc(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.bytes == null) {
      return false;
    }
    int i = paramInt3 - (paramInt1 << 1);
    int j;
    switch (paramInt2)
    {
    case 0: 
      assert (this.bytes[(paramInt1 + 0)] == 0);
      assert (this.bytes[(paramInt1 + 1)] == 0);
      j = (byte)(i >> 8);
      int k = (byte)(i >> 0);
      if ((i == (i & 0xFFFF)) && (j != -1))
      {
        this.bytes[(paramInt1 + 0)] = j;
        this.bytes[(paramInt1 + 1)] = k;
        assert (fetchDesc(paramInt1, paramInt2) == paramInt3);
        return true;
      }
      break;
    case 1: 
      assert (this.bytes[paramInt1] == 0);
      j = (byte)i;
      if ((i == (i & 0xFF)) && (j != -1))
      {
        this.bytes[paramInt1] = j;
        assert (fetchDesc(paramInt1, paramInt2) == paramInt3);
        return true;
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    this.bytes[paramInt1] = -1;
    assert ((paramInt2 == 1) || ((this.bytes[(paramInt1 + 1)] = (byte)this.bigDescs[0]) != 999));
    return false;
  }
  
  void storeIndex(int paramInt1, int paramInt2, int paramInt3)
  {
    storeIndex(this.bytes, paramInt1, paramInt2, paramInt3);
  }
  
  static void storeIndex(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt2)
    {
    case 0: 
      assert (paramInt3 == (paramInt3 & 0xFFFF)) : paramInt3;
      paramArrayOfByte[(paramInt1 + 0)] = ((byte)(paramInt3 >> 8));
      paramArrayOfByte[(paramInt1 + 1)] = ((byte)(paramInt3 >> 0));
      break;
    case 1: 
      assert (paramInt3 == (paramInt3 & 0xFF)) : paramInt3;
      paramArrayOfByte[paramInt1] = ((byte)paramInt3);
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
  }
  
  void addU1(int paramInt, ConstantPool.Entry paramEntry)
  {
    add(paramInt, 1, paramEntry);
  }
  
  void addU2(int paramInt, ConstantPool.Entry paramEntry)
  {
    add(paramInt, 0, paramEntry);
  }
  
  public Iterator<Fixup> iterator()
  {
    return new Itr(null);
  }
  
  public void add(int paramInt1, int paramInt2, ConstantPool.Entry paramEntry)
  {
    addDesc(makeDesc(paramInt1, paramInt2), paramEntry);
  }
  
  public boolean add(Fixup paramFixup)
  {
    addDesc(paramFixup.desc, paramFixup.entry);
    return true;
  }
  
  public boolean addAll(Collection<? extends Fixup> paramCollection)
  {
    if ((paramCollection instanceof Fixups))
    {
      Fixups localFixups = (Fixups)paramCollection;
      if (localFixups.size == 0) {
        return false;
      }
      if ((this.size == 0) && (this.entries.length < localFixups.size)) {
        growEntries(localFixups.size);
      }
      ConstantPool.Entry[] arrayOfEntry = localFixups.entries;
      Fixups tmp58_57 = localFixups;
      tmp58_57.getClass();
      Itr localItr = new Itr(tmp58_57, null);
      while (localItr.hasNext())
      {
        int i = localItr.index;
        addDesc(localItr.nextDesc(), arrayOfEntry[i]);
      }
      return true;
    }
    return super.addAll(paramCollection);
  }
  
  private void addDesc(int paramInt, ConstantPool.Entry paramEntry)
  {
    if (this.entries.length == this.size) {
      growEntries(this.size * 2);
    }
    this.entries[this.size] = paramEntry;
    if (this.size == 0)
    {
      this.head = (this.tail = paramInt);
    }
    else
    {
      int i = this.tail;
      int j = descLoc(i);
      int k = descFmt(i);
      int m = fmtLen(k);
      int n = descLoc(paramInt);
      if (n < j + m) {
        badOverlap(n);
      }
      this.tail = paramInt;
      if (!storeDesc(j, k, paramInt))
      {
        int i1 = this.bigDescs[0];
        if (this.bigDescs.length == i1) {
          growBigDescs();
        }
        this.bigDescs[(i1++)] = paramInt;
        this.bigDescs[0] = i1;
      }
    }
    this.size += 1;
  }
  
  private void badOverlap(int paramInt)
  {
    throw new IllegalArgumentException("locs must be ascending and must not overlap:  " + paramInt + " >> " + this);
  }
  
  private void growEntries(int paramInt)
  {
    ConstantPool.Entry[] arrayOfEntry = this.entries;
    this.entries = new ConstantPool.Entry[Math.max(3, paramInt)];
    System.arraycopy(arrayOfEntry, 0, this.entries, 0, arrayOfEntry.length);
  }
  
  private void growBigDescs()
  {
    int[] arrayOfInt = this.bigDescs;
    this.bigDescs = new int[arrayOfInt.length * 2];
    System.arraycopy(arrayOfInt, 0, this.bigDescs, 0, arrayOfInt.length);
  }
  
  static Object addRefWithBytes(Object paramObject, byte[] paramArrayOfByte, ConstantPool.Entry paramEntry)
  {
    return add(paramObject, paramArrayOfByte, 0, 0, paramEntry);
  }
  
  static Object addRefWithLoc(Object paramObject, int paramInt, ConstantPool.Entry paramEntry)
  {
    return add(paramObject, null, paramInt, 0, paramEntry);
  }
  
  private static Object add(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ConstantPool.Entry paramEntry)
  {
    Fixups localFixups;
    if (paramObject == null)
    {
      if ((paramInt1 == 0) && (paramInt2 == 0)) {
        return paramEntry;
      }
      localFixups = new Fixups(paramArrayOfByte);
    }
    else if (!(paramObject instanceof Fixups))
    {
      ConstantPool.Entry localEntry = (ConstantPool.Entry)paramObject;
      localFixups = new Fixups(paramArrayOfByte);
      localFixups.add(0, 0, localEntry);
    }
    else
    {
      localFixups = (Fixups)paramObject;
      assert (localFixups.bytes == paramArrayOfByte);
    }
    localFixups.add(paramInt1, paramInt2, paramEntry);
    return localFixups;
  }
  
  public static void setBytes(Object paramObject, byte[] paramArrayOfByte)
  {
    if ((paramObject instanceof Fixups))
    {
      Fixups localFixups = (Fixups)paramObject;
      localFixups.setBytes(paramArrayOfByte);
    }
  }
  
  public static Object trimToSize(Object paramObject)
  {
    if ((paramObject instanceof Fixups))
    {
      Fixups localFixups = (Fixups)paramObject;
      localFixups.trimToSize();
      if (localFixups.size() == 0) {
        paramObject = null;
      }
    }
    return paramObject;
  }
  
  public static void visitRefs(Object paramObject, Collection<ConstantPool.Entry> paramCollection)
  {
    if (paramObject != null) {
      if (!(paramObject instanceof Fixups))
      {
        paramCollection.add((ConstantPool.Entry)paramObject);
      }
      else
      {
        Fixups localFixups = (Fixups)paramObject;
        localFixups.visitRefs(paramCollection);
      }
    }
  }
  
  public static void finishRefs(Object paramObject, byte[] paramArrayOfByte, ConstantPool.Index paramIndex)
  {
    if (paramObject == null) {
      return;
    }
    if (!(paramObject instanceof Fixups))
    {
      int i = paramIndex.indexOf((ConstantPool.Entry)paramObject);
      storeIndex(paramArrayOfByte, 0, 0, i);
      return;
    }
    Fixups localFixups = (Fixups)paramObject;
    assert (localFixups.bytes == paramArrayOfByte);
    localFixups.finishRefs(paramIndex);
  }
  
  void finishRefs(ConstantPool.Index paramIndex)
  {
    if (isEmpty()) {
      return;
    }
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Fixup localFixup = (Fixup)localIterator.next();
      int i = paramIndex.indexOf(localFixup.entry);
      storeIndex(localFixup.location(), localFixup.format(), i);
    }
    this.bytes = null;
    clear();
  }
  
  public static class Fixup
    implements Comparable<Fixup>
  {
    int desc;
    ConstantPool.Entry entry;
    
    Fixup(int paramInt, ConstantPool.Entry paramEntry)
    {
      this.desc = paramInt;
      this.entry = paramEntry;
    }
    
    public Fixup(int paramInt1, int paramInt2, ConstantPool.Entry paramEntry)
    {
      this.desc = Fixups.makeDesc(paramInt1, paramInt2);
      this.entry = paramEntry;
    }
    
    public int location()
    {
      return Fixups.descLoc(this.desc);
    }
    
    public int format()
    {
      return Fixups.descFmt(this.desc);
    }
    
    public ConstantPool.Entry entry()
    {
      return this.entry;
    }
    
    public int compareTo(Fixup paramFixup)
    {
      return location() - paramFixup.location();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Fixup)) {
        return false;
      }
      Fixup localFixup = (Fixup)paramObject;
      return (this.desc == localFixup.desc) && (this.entry == localFixup.entry);
    }
    
    public int hashCode()
    {
      int i = 7;
      i = 59 * i + this.desc;
      i = 59 * i + Objects.hashCode(this.entry);
      return i;
    }
    
    public String toString()
    {
      return "@" + location() + (format() == 1 ? ".1" : "") + "=" + this.entry;
    }
  }
  
  private class Itr
    implements Iterator<Fixups.Fixup>
  {
    int index = 0;
    int bigIndex = 1;
    int next = Fixups.this.head;
    
    private Itr() {}
    
    public boolean hasNext()
    {
      return this.index < Fixups.this.size;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    public Fixups.Fixup next()
    {
      int i = this.index;
      return new Fixups.Fixup(nextDesc(), Fixups.this.entries[i]);
    }
    
    int nextDesc()
    {
      this.index += 1;
      int i = this.next;
      if (this.index < Fixups.this.size)
      {
        int j = Fixups.descLoc(i);
        int k = Fixups.descFmt(i);
        if ((Fixups.this.bytes != null) && (Fixups.this.bytes[j] != -1))
        {
          this.next = Fixups.this.fetchDesc(j, k);
        }
        else
        {
          assert ((k == 1) || (Fixups.this.bytes == null) || (Fixups.this.bytes[(j + 1)] == (byte)this.bigIndex));
          this.next = Fixups.this.bigDescs[(this.bigIndex++)];
        }
      }
      return i;
    }
  }
}
