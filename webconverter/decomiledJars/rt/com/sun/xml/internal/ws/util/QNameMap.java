package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class QNameMap<V>
{
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final int MAXIMUM_CAPACITY = 1073741824;
  transient Entry<V>[] table = new Entry[16];
  transient int size;
  private int threshold = 12;
  private static final float DEFAULT_LOAD_FACTOR = 0.75F;
  private Set<Entry<V>> entrySet = null;
  private transient Iterable<V> views = new Iterable()
  {
    public Iterator<V> iterator()
    {
      return new QNameMap.ValueIterator(QNameMap.this, null);
    }
  };
  
  public QNameMap() {}
  
  public void put(String paramString1, String paramString2, V paramV)
  {
    assert (paramString2 != null);
    assert (paramString1 != null);
    int i = hash(paramString2);
    int j = indexFor(i, this.table.length);
    for (Entry localEntry = this.table[j]; localEntry != null; localEntry = localEntry.next) {
      if ((localEntry.hash == i) && (paramString2.equals(localEntry.localName)) && (paramString1.equals(localEntry.nsUri)))
      {
        localEntry.value = paramV;
        return;
      }
    }
    addEntry(i, paramString1, paramString2, paramV, j);
  }
  
  public void put(QName paramQName, V paramV)
  {
    put(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramV);
  }
  
  public V get(@NotNull String paramString1, String paramString2)
  {
    Entry localEntry = getEntry(paramString1, paramString2);
    if (localEntry == null) {
      return null;
    }
    return localEntry.value;
  }
  
  public V get(QName paramQName)
  {
    return get(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public int size()
  {
    return this.size;
  }
  
  public QNameMap<V> putAll(QNameMap<? extends V> paramQNameMap)
  {
    int i = paramQNameMap.size();
    if (i == 0) {
      return this;
    }
    if (i > this.threshold)
    {
      int j = i;
      if (j > 1073741824) {
        j = 1073741824;
      }
      int k = this.table.length;
      while (k < j) {
        k <<= 1;
      }
      if (k > this.table.length) {
        resize(k);
      }
    }
    Iterator localIterator = paramQNameMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      put(localEntry.nsUri, localEntry.localName, localEntry.getValue());
    }
    return this;
  }
  
  public QNameMap<V> putAll(Map<QName, ? extends V> paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      QName localQName = (QName)localEntry.getKey();
      put(localQName.getNamespaceURI(), localQName.getLocalPart(), localEntry.getValue());
    }
    return this;
  }
  
  private static int hash(String paramString)
  {
    int i = paramString.hashCode();
    i += (i << 9 ^ 0xFFFFFFFF);
    i ^= i >>> 14;
    i += (i << 4);
    i ^= i >>> 10;
    return i;
  }
  
  private static int indexFor(int paramInt1, int paramInt2)
  {
    return paramInt1 & paramInt2 - 1;
  }
  
  private void addEntry(int paramInt1, String paramString1, String paramString2, V paramV, int paramInt2)
  {
    Entry localEntry = this.table[paramInt2];
    this.table[paramInt2] = new Entry(paramInt1, paramString1, paramString2, paramV, localEntry);
    if (this.size++ >= this.threshold) {
      resize(2 * this.table.length);
    }
  }
  
  private void resize(int paramInt)
  {
    Entry[] arrayOfEntry1 = this.table;
    int i = arrayOfEntry1.length;
    if (i == 1073741824)
    {
      this.threshold = Integer.MAX_VALUE;
      return;
    }
    Entry[] arrayOfEntry2 = new Entry[paramInt];
    transfer(arrayOfEntry2);
    this.table = arrayOfEntry2;
    this.threshold = paramInt;
  }
  
  private void transfer(Entry<V>[] paramArrayOfEntry)
  {
    Entry[] arrayOfEntry = this.table;
    int i = paramArrayOfEntry.length;
    for (int j = 0; j < arrayOfEntry.length; j++)
    {
      Object localObject = arrayOfEntry[j];
      if (localObject != null)
      {
        arrayOfEntry[j] = null;
        do
        {
          Entry localEntry = ((Entry)localObject).next;
          int k = indexFor(((Entry)localObject).hash, i);
          ((Entry)localObject).next = paramArrayOfEntry[k];
          paramArrayOfEntry[k] = localObject;
          localObject = localEntry;
        } while (localObject != null);
      }
    }
  }
  
  public Entry<V> getOne()
  {
    for (Entry localEntry : this.table) {
      if (localEntry != null) {
        return localEntry;
      }
    }
    return null;
  }
  
  public Collection<QName> keySet()
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      localHashSet.add(localEntry.createQName());
    }
    return localHashSet;
  }
  
  public Iterable<V> values()
  {
    return this.views;
  }
  
  public boolean containsKey(@NotNull String paramString1, String paramString2)
  {
    return getEntry(paramString1, paramString2) != null;
  }
  
  public boolean isEmpty()
  {
    return this.size == 0;
  }
  
  public Set<Entry<V>> entrySet()
  {
    Set localSet = this.entrySet;
    return this.entrySet = new EntrySet(null);
  }
  
  private Iterator<Entry<V>> newEntryIterator()
  {
    return new EntryIterator(null);
  }
  
  private Entry<V> getEntry(@NotNull String paramString1, String paramString2)
  {
    int i = hash(paramString2);
    int j = indexFor(i, this.table.length);
    for (Entry localEntry = this.table[j]; (localEntry != null) && ((!paramString2.equals(localEntry.localName)) || (!paramString1.equals(localEntry.nsUri))); localEntry = localEntry.next) {}
    return localEntry;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('{');
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      if (localStringBuilder.length() > 1) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append('[');
      localStringBuilder.append(localEntry);
      localStringBuilder.append(']');
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public static final class Entry<V>
  {
    public final String nsUri;
    public final String localName;
    V value;
    final int hash;
    Entry<V> next;
    
    Entry(int paramInt, String paramString1, String paramString2, V paramV, Entry<V> paramEntry)
    {
      this.value = paramV;
      this.next = paramEntry;
      this.nsUri = paramString1;
      this.localName = paramString2;
      this.hash = paramInt;
    }
    
    public QName createQName()
    {
      return new QName(this.nsUri, this.localName);
    }
    
    public V getValue()
    {
      return this.value;
    }
    
    public V setValue(V paramV)
    {
      Object localObject = this.value;
      this.value = paramV;
      return localObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Entry)) {
        return false;
      }
      Entry localEntry = (Entry)paramObject;
      String str1 = this.nsUri;
      String str2 = localEntry.nsUri;
      String str3 = this.localName;
      String str4 = localEntry.localName;
      if ((str1.equals(str2)) && (str3.equals(str4)))
      {
        Object localObject1 = getValue();
        Object localObject2 = localEntry.getValue();
        if ((localObject1 == localObject2) || ((localObject1 != null) && (localObject1.equals(localObject2)))) {
          return true;
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.localName.hashCode() ^ (this.value == null ? 0 : this.value.hashCode());
    }
    
    public String toString()
    {
      return '"' + this.nsUri + "\",\"" + this.localName + "\"=" + getValue();
    }
  }
  
  private class EntryIterator
    extends QNameMap<V>.HashIterator<QNameMap.Entry<V>>
  {
    private EntryIterator()
    {
      super();
    }
    
    public QNameMap.Entry<V> next()
    {
      return nextEntry();
    }
  }
  
  private class EntrySet
    extends AbstractSet<QNameMap.Entry<V>>
  {
    private EntrySet() {}
    
    public Iterator<QNameMap.Entry<V>> iterator()
    {
      return QNameMap.this.newEntryIterator();
    }
    
    public boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof QNameMap.Entry)) {
        return false;
      }
      QNameMap.Entry localEntry1 = (QNameMap.Entry)paramObject;
      QNameMap.Entry localEntry2 = QNameMap.this.getEntry(localEntry1.nsUri, localEntry1.localName);
      return (localEntry2 != null) && (localEntry2.equals(localEntry1));
    }
    
    public boolean remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public int size()
    {
      return QNameMap.this.size;
    }
  }
  
  private abstract class HashIterator<E>
    implements Iterator<E>
  {
    QNameMap.Entry<V> next;
    int index;
    
    HashIterator()
    {
      QNameMap.Entry[] arrayOfEntry = QNameMap.this.table;
      int i = arrayOfEntry.length;
      QNameMap.Entry localEntry = null;
      while ((QNameMap.this.size != 0) && (i > 0) && ((localEntry = arrayOfEntry[(--i)]) == null)) {}
      this.next = localEntry;
      this.index = i;
    }
    
    public boolean hasNext()
    {
      return this.next != null;
    }
    
    QNameMap.Entry<V> nextEntry()
    {
      QNameMap.Entry localEntry1 = this.next;
      if (localEntry1 == null) {
        throw new NoSuchElementException();
      }
      QNameMap.Entry localEntry2 = localEntry1.next;
      QNameMap.Entry[] arrayOfEntry = QNameMap.this.table;
      int i = this.index;
      while ((localEntry2 == null) && (i > 0)) {
        localEntry2 = arrayOfEntry[(--i)];
      }
      this.index = i;
      this.next = localEntry2;
      return localEntry1;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class ValueIterator
    extends QNameMap<V>.HashIterator<V>
  {
    private ValueIterator()
    {
      super();
    }
    
    public V next()
    {
      return nextEntry().value;
    }
  }
}
