package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class IdentityHashMap<K, V>
  extends AbstractMap<K, V>
  implements Map<K, V>, Serializable, Cloneable
{
  private static final int DEFAULT_CAPACITY = 32;
  private static final int MINIMUM_CAPACITY = 4;
  private static final int MAXIMUM_CAPACITY = 536870912;
  transient Object[] table;
  int size;
  transient int modCount;
  static final Object NULL_KEY = new Object();
  private transient Set<Map.Entry<K, V>> entrySet;
  private static final long serialVersionUID = 8188218128353913216L;
  
  private static Object maskNull(Object paramObject)
  {
    return paramObject == null ? NULL_KEY : paramObject;
  }
  
  static final Object unmaskNull(Object paramObject)
  {
    return paramObject == NULL_KEY ? null : paramObject;
  }
  
  public IdentityHashMap()
  {
    init(32);
  }
  
  public IdentityHashMap(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("expectedMaxSize is negative: " + paramInt);
    }
    init(capacity(paramInt));
  }
  
  private static int capacity(int paramInt)
  {
    return paramInt <= 2 ? 4 : paramInt > 178956970 ? 536870912 : Integer.highestOneBit(paramInt + (paramInt << 1));
  }
  
  private void init(int paramInt)
  {
    this.table = new Object[2 * paramInt];
  }
  
  public IdentityHashMap(Map<? extends K, ? extends V> paramMap)
  {
    this((int)((1 + paramMap.size()) * 1.1D));
    putAll(paramMap);
  }
  
  public int size()
  {
    return this.size;
  }
  
  public boolean isEmpty()
  {
    return this.size == 0;
  }
  
  private static int hash(Object paramObject, int paramInt)
  {
    int i = System.identityHashCode(paramObject);
    return (i << 1) - (i << 8) & paramInt - 1;
  }
  
  private static int nextKeyIndex(int paramInt1, int paramInt2)
  {
    return paramInt1 + 2 < paramInt2 ? paramInt1 + 2 : 0;
  }
  
  public V get(Object paramObject)
  {
    Object localObject1 = maskNull(paramObject);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    for (int j = hash(localObject1, i);; j = nextKeyIndex(j, i))
    {
      Object localObject2 = arrayOfObject[j];
      if (localObject2 == localObject1) {
        return arrayOfObject[(j + 1)];
      }
      if (localObject2 == null) {
        return null;
      }
    }
  }
  
  public boolean containsKey(Object paramObject)
  {
    Object localObject1 = maskNull(paramObject);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    for (int j = hash(localObject1, i);; j = nextKeyIndex(j, i))
    {
      Object localObject2 = arrayOfObject[j];
      if (localObject2 == localObject1) {
        return true;
      }
      if (localObject2 == null) {
        return false;
      }
    }
  }
  
  public boolean containsValue(Object paramObject)
  {
    Object[] arrayOfObject = this.table;
    for (int i = 1; i < arrayOfObject.length; i += 2) {
      if ((arrayOfObject[i] == paramObject) && (arrayOfObject[(i - 1)] != null)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean containsMapping(Object paramObject1, Object paramObject2)
  {
    Object localObject1 = maskNull(paramObject1);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    for (int j = hash(localObject1, i);; j = nextKeyIndex(j, i))
    {
      Object localObject2 = arrayOfObject[j];
      if (localObject2 == localObject1) {
        return arrayOfObject[(j + 1)] == paramObject2;
      }
      if (localObject2 == null) {
        return false;
      }
    }
  }
  
  public V put(K paramK, V paramV)
  {
    Object localObject1 = maskNull(paramK);
    Object[] arrayOfObject;
    int i;
    int j;
    int k;
    do
    {
      arrayOfObject = this.table;
      i = arrayOfObject.length;
      Object localObject2;
      for (j = hash(localObject1, i); (localObject2 = arrayOfObject[j]) != null; j = nextKeyIndex(j, i)) {
        if (localObject2 == localObject1)
        {
          Object localObject3 = arrayOfObject[(j + 1)];
          arrayOfObject[(j + 1)] = paramV;
          return localObject3;
        }
      }
      k = this.size + 1;
    } while ((k + (k << 1) > i) && (resize(i)));
    this.modCount += 1;
    arrayOfObject[j] = localObject1;
    arrayOfObject[(j + 1)] = paramV;
    this.size = k;
    return null;
  }
  
  private boolean resize(int paramInt)
  {
    int i = paramInt * 2;
    Object[] arrayOfObject1 = this.table;
    int j = arrayOfObject1.length;
    if (j == 1073741824)
    {
      if (this.size == 536870911) {
        throw new IllegalStateException("Capacity exhausted.");
      }
      return false;
    }
    if (j >= i) {
      return false;
    }
    Object[] arrayOfObject2 = new Object[i];
    for (int k = 0; k < j; k += 2)
    {
      Object localObject1 = arrayOfObject1[k];
      if (localObject1 != null)
      {
        Object localObject2 = arrayOfObject1[(k + 1)];
        arrayOfObject1[k] = null;
        arrayOfObject1[(k + 1)] = null;
        for (int m = hash(localObject1, i); arrayOfObject2[m] != null; m = nextKeyIndex(m, i)) {}
        arrayOfObject2[m] = localObject1;
        arrayOfObject2[(m + 1)] = localObject2;
      }
    }
    this.table = arrayOfObject2;
    return true;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    int i = paramMap.size();
    if (i == 0) {
      return;
    }
    if (i > this.size) {
      resize(capacity(i));
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public V remove(Object paramObject)
  {
    Object localObject1 = maskNull(paramObject);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    for (int j = hash(localObject1, i);; j = nextKeyIndex(j, i))
    {
      Object localObject2 = arrayOfObject[j];
      if (localObject2 == localObject1)
      {
        this.modCount += 1;
        this.size -= 1;
        Object localObject3 = arrayOfObject[(j + 1)];
        arrayOfObject[(j + 1)] = null;
        arrayOfObject[j] = null;
        closeDeletion(j);
        return localObject3;
      }
      if (localObject2 == null) {
        return null;
      }
    }
  }
  
  private boolean removeMapping(Object paramObject1, Object paramObject2)
  {
    Object localObject1 = maskNull(paramObject1);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    for (int j = hash(localObject1, i);; j = nextKeyIndex(j, i))
    {
      Object localObject2 = arrayOfObject[j];
      if (localObject2 == localObject1)
      {
        if (arrayOfObject[(j + 1)] != paramObject2) {
          return false;
        }
        this.modCount += 1;
        this.size -= 1;
        arrayOfObject[j] = null;
        arrayOfObject[(j + 1)] = null;
        closeDeletion(j);
        return true;
      }
      if (localObject2 == null) {
        return false;
      }
    }
  }
  
  private void closeDeletion(int paramInt)
  {
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    Object localObject;
    for (int j = nextKeyIndex(paramInt, i); (localObject = arrayOfObject[j]) != null; j = nextKeyIndex(j, i))
    {
      int k = hash(localObject, i);
      if (((j < k) && ((k <= paramInt) || (paramInt <= j))) || ((k <= paramInt) && (paramInt <= j)))
      {
        arrayOfObject[paramInt] = localObject;
        arrayOfObject[(paramInt + 1)] = arrayOfObject[(j + 1)];
        arrayOfObject[j] = null;
        arrayOfObject[(j + 1)] = null;
        paramInt = j;
      }
    }
  }
  
  public void clear()
  {
    this.modCount += 1;
    Object[] arrayOfObject = this.table;
    for (int i = 0; i < arrayOfObject.length; i++) {
      arrayOfObject[i] = null;
    }
    this.size = 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    Object localObject1;
    if ((paramObject instanceof IdentityHashMap))
    {
      localObject1 = (IdentityHashMap)paramObject;
      if (((IdentityHashMap)localObject1).size() != this.size) {
        return false;
      }
      Object[] arrayOfObject = ((IdentityHashMap)localObject1).table;
      for (int i = 0; i < arrayOfObject.length; i += 2)
      {
        Object localObject2 = arrayOfObject[i];
        if ((localObject2 != null) && (!containsMapping(localObject2, arrayOfObject[(i + 1)]))) {
          return false;
        }
      }
      return true;
    }
    if ((paramObject instanceof Map))
    {
      localObject1 = (Map)paramObject;
      return entrySet().equals(((Map)localObject1).entrySet());
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = 0;
    Object[] arrayOfObject = this.table;
    for (int j = 0; j < arrayOfObject.length; j += 2)
    {
      Object localObject1 = arrayOfObject[j];
      if (localObject1 != null)
      {
        Object localObject2 = unmaskNull(localObject1);
        i += (System.identityHashCode(localObject2) ^ System.identityHashCode(arrayOfObject[(j + 1)]));
      }
    }
    return i;
  }
  
  public Object clone()
  {
    try
    {
      IdentityHashMap localIdentityHashMap = (IdentityHashMap)super.clone();
      localIdentityHashMap.entrySet = null;
      localIdentityHashMap.table = ((Object[])this.table.clone());
      return localIdentityHashMap;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public Set<K> keySet()
  {
    Set localSet = this.keySet;
    if (localSet != null) {
      return localSet;
    }
    return this.keySet = new KeySet(null);
  }
  
  public Collection<V> values()
  {
    Collection localCollection = this.values;
    if (localCollection != null) {
      return localCollection;
    }
    return this.values = new Values(null);
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    Set localSet = this.entrySet;
    if (localSet != null) {
      return localSet;
    }
    return this.entrySet = new EntrySet(null);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.size);
    Object[] arrayOfObject = this.table;
    for (int i = 0; i < arrayOfObject.length; i += 2)
    {
      Object localObject = arrayOfObject[i];
      if (localObject != null)
      {
        paramObjectOutputStream.writeObject(unmaskNull(localObject));
        paramObjectOutputStream.writeObject(arrayOfObject[(i + 1)]);
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i < 0) {
      throw new StreamCorruptedException("Illegal mappings count: " + i);
    }
    init(capacity(i));
    for (int j = 0; j < i; j++)
    {
      Object localObject1 = paramObjectInputStream.readObject();
      Object localObject2 = paramObjectInputStream.readObject();
      putForCreate(localObject1, localObject2);
    }
  }
  
  private void putForCreate(K paramK, V paramV)
    throws StreamCorruptedException
  {
    Object localObject1 = maskNull(paramK);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    Object localObject2;
    for (int j = hash(localObject1, i); (localObject2 = arrayOfObject[j]) != null; j = nextKeyIndex(j, i)) {
      if (localObject2 == localObject1) {
        throw new StreamCorruptedException();
      }
    }
    arrayOfObject[j] = localObject1;
    arrayOfObject[(j + 1)] = paramV;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer)
  {
    Objects.requireNonNull(paramBiConsumer);
    int i = this.modCount;
    Object[] arrayOfObject = this.table;
    for (int j = 0; j < arrayOfObject.length; j += 2)
    {
      Object localObject = arrayOfObject[j];
      if (localObject != null) {
        paramBiConsumer.accept(unmaskNull(localObject), arrayOfObject[(j + 1)]);
      }
      if (this.modCount != i) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    Objects.requireNonNull(paramBiFunction);
    int i = this.modCount;
    Object[] arrayOfObject = this.table;
    for (int j = 0; j < arrayOfObject.length; j += 2)
    {
      Object localObject = arrayOfObject[j];
      if (localObject != null) {
        arrayOfObject[(j + 1)] = paramBiFunction.apply(unmaskNull(localObject), arrayOfObject[(j + 1)]);
      }
      if (this.modCount != i) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  private class EntryIterator
    extends IdentityHashMap<K, V>.IdentityHashMapIterator<Map.Entry<K, V>>
  {
    private IdentityHashMap<K, V>.EntryIterator.Entry lastReturnedEntry;
    
    private EntryIterator()
    {
      super(null);
    }
    
    public Map.Entry<K, V> next()
    {
      this.lastReturnedEntry = new Entry(nextIndex(), null);
      return this.lastReturnedEntry;
    }
    
    public void remove()
    {
      this.lastReturnedIndex = (null == this.lastReturnedEntry ? -1 : this.lastReturnedEntry.index);
      super.remove();
      this.lastReturnedEntry.index = this.lastReturnedIndex;
      this.lastReturnedEntry = null;
    }
    
    private class Entry
      implements Map.Entry<K, V>
    {
      private int index;
      
      private Entry(int paramInt)
      {
        this.index = paramInt;
      }
      
      public K getKey()
      {
        checkIndexForEntryUse();
        return IdentityHashMap.unmaskNull(IdentityHashMap.EntryIterator.this.traversalTable[this.index]);
      }
      
      public V getValue()
      {
        checkIndexForEntryUse();
        return IdentityHashMap.EntryIterator.this.traversalTable[(this.index + 1)];
      }
      
      public V setValue(V paramV)
      {
        checkIndexForEntryUse();
        Object localObject = IdentityHashMap.EntryIterator.this.traversalTable[(this.index + 1)];
        IdentityHashMap.EntryIterator.this.traversalTable[(this.index + 1)] = paramV;
        if (IdentityHashMap.EntryIterator.this.traversalTable != IdentityHashMap.this.table) {
          IdentityHashMap.this.put(IdentityHashMap.EntryIterator.this.traversalTable[this.index], paramV);
        }
        return localObject;
      }
      
      public boolean equals(Object paramObject)
      {
        if (this.index < 0) {
          return super.equals(paramObject);
        }
        if (!(paramObject instanceof Map.Entry)) {
          return false;
        }
        Map.Entry localEntry = (Map.Entry)paramObject;
        return (localEntry.getKey() == IdentityHashMap.unmaskNull(IdentityHashMap.EntryIterator.this.traversalTable[this.index])) && (localEntry.getValue() == IdentityHashMap.EntryIterator.this.traversalTable[(this.index + 1)]);
      }
      
      public int hashCode()
      {
        if (IdentityHashMap.EntryIterator.this.lastReturnedIndex < 0) {
          return super.hashCode();
        }
        return System.identityHashCode(IdentityHashMap.unmaskNull(IdentityHashMap.EntryIterator.this.traversalTable[this.index])) ^ System.identityHashCode(IdentityHashMap.EntryIterator.this.traversalTable[(this.index + 1)]);
      }
      
      public String toString()
      {
        if (this.index < 0) {
          return super.toString();
        }
        return IdentityHashMap.unmaskNull(IdentityHashMap.EntryIterator.this.traversalTable[this.index]) + "=" + IdentityHashMap.EntryIterator.this.traversalTable[(this.index + 1)];
      }
      
      private void checkIndexForEntryUse()
      {
        if (this.index < 0) {
          throw new IllegalStateException("Entry was removed");
        }
      }
    }
  }
  
  private class EntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new IdentityHashMap.EntryIterator(IdentityHashMap.this, null);
    }
    
    public boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return IdentityHashMap.this.containsMapping(localEntry.getKey(), localEntry.getValue());
    }
    
    public boolean remove(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return IdentityHashMap.this.removeMapping(localEntry.getKey(), localEntry.getValue());
    }
    
    public int size()
    {
      return IdentityHashMap.this.size;
    }
    
    public void clear()
    {
      IdentityHashMap.this.clear();
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      Objects.requireNonNull(paramCollection);
      boolean bool = false;
      Iterator localIterator = iterator();
      while (localIterator.hasNext()) {
        if (paramCollection.contains(localIterator.next()))
        {
          localIterator.remove();
          bool = true;
        }
      }
      return bool;
    }
    
    public Object[] toArray()
    {
      return toArray(new Object[0]);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      int i = IdentityHashMap.this.modCount;
      int j = size();
      if (paramArrayOfT.length < j) {
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), j);
      }
      Object[] arrayOfObject = IdentityHashMap.this.table;
      int k = 0;
      for (int m = 0; m < arrayOfObject.length; m += 2)
      {
        Object localObject;
        if ((localObject = arrayOfObject[m]) != null)
        {
          if (k >= j) {
            throw new ConcurrentModificationException();
          }
          paramArrayOfT[(k++)] = new AbstractMap.SimpleEntry(IdentityHashMap.unmaskNull(localObject), arrayOfObject[(m + 1)]);
        }
      }
      if ((k < j) || (i != IdentityHashMap.this.modCount)) {
        throw new ConcurrentModificationException();
      }
      if (k < paramArrayOfT.length) {
        paramArrayOfT[k] = null;
      }
      return paramArrayOfT;
    }
    
    public Spliterator<Map.Entry<K, V>> spliterator()
    {
      return new IdentityHashMap.EntrySpliterator(IdentityHashMap.this, 0, -1, 0, 0);
    }
  }
  
  static final class EntrySpliterator<K, V>
    extends IdentityHashMap.IdentityHashMapSpliterator<K, V>
    implements Spliterator<Map.Entry<K, V>>
  {
    EntrySpliterator(IdentityHashMap<K, V> paramIdentityHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public EntrySpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1 & 0xFFFFFFFE;
      return j >= k ? null : new EntrySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      IdentityHashMap localIdentityHashMap;
      Object[] arrayOfObject;
      int i;
      int j;
      if (((localIdentityHashMap = this.map) != null) && ((arrayOfObject = localIdentityHashMap.table) != null) && ((i = this.index) >= 0) && ((this.index = j = getFence()) <= arrayOfObject.length))
      {
        while (i < j)
        {
          Object localObject1 = arrayOfObject[i];
          if (localObject1 != null)
          {
            Object localObject2 = IdentityHashMap.unmaskNull(localObject1);
            Object localObject3 = arrayOfObject[(i + 1)];
            paramConsumer.accept(new AbstractMap.SimpleImmutableEntry(localObject2, localObject3));
          }
          i += 2;
        }
        if (localIdentityHashMap.modCount == this.expectedModCount) {
          return;
        }
      }
      throw new ConcurrentModificationException();
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Object[] arrayOfObject = this.map.table;
      int i = getFence();
      while (this.index < i)
      {
        Object localObject1 = arrayOfObject[this.index];
        Object localObject2 = arrayOfObject[(this.index + 1)];
        this.index += 2;
        if (localObject1 != null)
        {
          Object localObject3 = IdentityHashMap.unmaskNull(localObject1);
          paramConsumer.accept(new AbstractMap.SimpleImmutableEntry(localObject3, localObject2));
          if (this.map.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
          }
          return true;
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return ((this.fence < 0) || (this.est == this.map.size) ? 64 : 0) | 0x1;
    }
  }
  
  private abstract class IdentityHashMapIterator<T>
    implements Iterator<T>
  {
    int index = IdentityHashMap.this.size != 0 ? 0 : IdentityHashMap.this.table.length;
    int expectedModCount = IdentityHashMap.this.modCount;
    int lastReturnedIndex = -1;
    boolean indexValid;
    Object[] traversalTable = IdentityHashMap.this.table;
    
    private IdentityHashMapIterator() {}
    
    public boolean hasNext()
    {
      Object[] arrayOfObject = this.traversalTable;
      for (int i = this.index; i < arrayOfObject.length; i += 2)
      {
        Object localObject = arrayOfObject[i];
        if (localObject != null)
        {
          this.index = i;
          return this.indexValid = 1;
        }
      }
      this.index = arrayOfObject.length;
      return false;
    }
    
    protected int nextIndex()
    {
      if (IdentityHashMap.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if ((!this.indexValid) && (!hasNext())) {
        throw new NoSuchElementException();
      }
      this.indexValid = false;
      this.lastReturnedIndex = this.index;
      this.index += 2;
      return this.lastReturnedIndex;
    }
    
    public void remove()
    {
      if (this.lastReturnedIndex == -1) {
        throw new IllegalStateException();
      }
      if (IdentityHashMap.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
      this.expectedModCount = (++IdentityHashMap.this.modCount);
      int i = this.lastReturnedIndex;
      this.lastReturnedIndex = -1;
      this.index = i;
      this.indexValid = false;
      Object[] arrayOfObject1 = this.traversalTable;
      int j = arrayOfObject1.length;
      int k = i;
      Object localObject1 = arrayOfObject1[k];
      arrayOfObject1[k] = null;
      arrayOfObject1[(k + 1)] = null;
      if (arrayOfObject1 != IdentityHashMap.this.table)
      {
        IdentityHashMap.this.remove(localObject1);
        this.expectedModCount = IdentityHashMap.this.modCount;
        return;
      }
      IdentityHashMap.this.size -= 1;
      Object localObject2;
      for (int m = IdentityHashMap.nextKeyIndex(k, j); (localObject2 = arrayOfObject1[m]) != null; m = IdentityHashMap.nextKeyIndex(m, j))
      {
        int n = IdentityHashMap.hash(localObject2, j);
        if (((m < n) && ((n <= k) || (k <= m))) || ((n <= k) && (k <= m)))
        {
          if ((m < i) && (k >= i) && (this.traversalTable == IdentityHashMap.this.table))
          {
            int i1 = j - i;
            Object[] arrayOfObject2 = new Object[i1];
            System.arraycopy(arrayOfObject1, i, arrayOfObject2, 0, i1);
            this.traversalTable = arrayOfObject2;
            this.index = 0;
          }
          arrayOfObject1[k] = localObject2;
          arrayOfObject1[(k + 1)] = arrayOfObject1[(m + 1)];
          arrayOfObject1[m] = null;
          arrayOfObject1[(m + 1)] = null;
          k = m;
        }
      }
    }
  }
  
  static class IdentityHashMapSpliterator<K, V>
  {
    final IdentityHashMap<K, V> map;
    int index;
    int fence;
    int est;
    int expectedModCount;
    
    IdentityHashMapSpliterator(IdentityHashMap<K, V> paramIdentityHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.map = paramIdentityHashMap;
      this.index = paramInt1;
      this.fence = paramInt2;
      this.est = paramInt3;
      this.expectedModCount = paramInt4;
    }
    
    final int getFence()
    {
      int i;
      if ((i = this.fence) < 0)
      {
        this.est = this.map.size;
        this.expectedModCount = this.map.modCount;
        i = this.fence = this.map.table.length;
      }
      return i;
    }
    
    public final long estimateSize()
    {
      getFence();
      return this.est;
    }
  }
  
  private class KeyIterator
    extends IdentityHashMap<K, V>.IdentityHashMapIterator<K>
  {
    private KeyIterator()
    {
      super(null);
    }
    
    public K next()
    {
      return IdentityHashMap.unmaskNull(this.traversalTable[nextIndex()]);
    }
  }
  
  private class KeySet
    extends AbstractSet<K>
  {
    private KeySet() {}
    
    public Iterator<K> iterator()
    {
      return new IdentityHashMap.KeyIterator(IdentityHashMap.this, null);
    }
    
    public int size()
    {
      return IdentityHashMap.this.size;
    }
    
    public boolean contains(Object paramObject)
    {
      return IdentityHashMap.this.containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      int i = IdentityHashMap.this.size;
      IdentityHashMap.this.remove(paramObject);
      return IdentityHashMap.this.size != i;
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      Objects.requireNonNull(paramCollection);
      boolean bool = false;
      Iterator localIterator = iterator();
      while (localIterator.hasNext()) {
        if (paramCollection.contains(localIterator.next()))
        {
          localIterator.remove();
          bool = true;
        }
      }
      return bool;
    }
    
    public void clear()
    {
      IdentityHashMap.this.clear();
    }
    
    public int hashCode()
    {
      int i = 0;
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        i += System.identityHashCode(localObject);
      }
      return i;
    }
    
    public Object[] toArray()
    {
      return toArray(new Object[0]);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      int i = IdentityHashMap.this.modCount;
      int j = size();
      if (paramArrayOfT.length < j) {
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), j);
      }
      Object[] arrayOfObject = IdentityHashMap.this.table;
      int k = 0;
      for (int m = 0; m < arrayOfObject.length; m += 2)
      {
        Object localObject;
        if ((localObject = arrayOfObject[m]) != null)
        {
          if (k >= j) {
            throw new ConcurrentModificationException();
          }
          paramArrayOfT[(k++)] = IdentityHashMap.unmaskNull(localObject);
        }
      }
      if ((k < j) || (i != IdentityHashMap.this.modCount)) {
        throw new ConcurrentModificationException();
      }
      if (k < paramArrayOfT.length) {
        paramArrayOfT[k] = null;
      }
      return paramArrayOfT;
    }
    
    public Spliterator<K> spliterator()
    {
      return new IdentityHashMap.KeySpliterator(IdentityHashMap.this, 0, -1, 0, 0);
    }
  }
  
  static final class KeySpliterator<K, V>
    extends IdentityHashMap.IdentityHashMapSpliterator<K, V>
    implements Spliterator<K>
  {
    KeySpliterator(IdentityHashMap<K, V> paramIdentityHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public KeySpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1 & 0xFFFFFFFE;
      return j >= k ? null : new KeySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      IdentityHashMap localIdentityHashMap;
      Object[] arrayOfObject;
      int i;
      int j;
      if (((localIdentityHashMap = this.map) != null) && ((arrayOfObject = localIdentityHashMap.table) != null) && ((i = this.index) >= 0) && ((this.index = j = getFence()) <= arrayOfObject.length))
      {
        while (i < j)
        {
          Object localObject;
          if ((localObject = arrayOfObject[i]) != null) {
            paramConsumer.accept(IdentityHashMap.unmaskNull(localObject));
          }
          i += 2;
        }
        if (localIdentityHashMap.modCount == this.expectedModCount) {
          return;
        }
      }
      throw new ConcurrentModificationException();
    }
    
    public boolean tryAdvance(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Object[] arrayOfObject = this.map.table;
      int i = getFence();
      while (this.index < i)
      {
        Object localObject = arrayOfObject[this.index];
        this.index += 2;
        if (localObject != null)
        {
          paramConsumer.accept(IdentityHashMap.unmaskNull(localObject));
          if (this.map.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
          }
          return true;
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return ((this.fence < 0) || (this.est == this.map.size) ? 64 : 0) | 0x1;
    }
  }
  
  private class ValueIterator
    extends IdentityHashMap<K, V>.IdentityHashMapIterator<V>
  {
    private ValueIterator()
    {
      super(null);
    }
    
    public V next()
    {
      return this.traversalTable[(nextIndex() + 1)];
    }
  }
  
  static final class ValueSpliterator<K, V>
    extends IdentityHashMap.IdentityHashMapSpliterator<K, V>
    implements Spliterator<V>
  {
    ValueSpliterator(IdentityHashMap<K, V> paramIdentityHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public ValueSpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1 & 0xFFFFFFFE;
      return j >= k ? null : new ValueSpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      IdentityHashMap localIdentityHashMap;
      Object[] arrayOfObject;
      int i;
      int j;
      if (((localIdentityHashMap = this.map) != null) && ((arrayOfObject = localIdentityHashMap.table) != null) && ((i = this.index) >= 0) && ((this.index = j = getFence()) <= arrayOfObject.length))
      {
        while (i < j)
        {
          if (arrayOfObject[i] != null)
          {
            Object localObject = arrayOfObject[(i + 1)];
            paramConsumer.accept(localObject);
          }
          i += 2;
        }
        if (localIdentityHashMap.modCount == this.expectedModCount) {
          return;
        }
      }
      throw new ConcurrentModificationException();
    }
    
    public boolean tryAdvance(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Object[] arrayOfObject = this.map.table;
      int i = getFence();
      while (this.index < i)
      {
        Object localObject1 = arrayOfObject[this.index];
        Object localObject2 = arrayOfObject[(this.index + 1)];
        this.index += 2;
        if (localObject1 != null)
        {
          paramConsumer.accept(localObject2);
          if (this.map.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
          }
          return true;
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return (this.fence < 0) || (this.est == this.map.size) ? 64 : 0;
    }
  }
  
  private class Values
    extends AbstractCollection<V>
  {
    private Values() {}
    
    public Iterator<V> iterator()
    {
      return new IdentityHashMap.ValueIterator(IdentityHashMap.this, null);
    }
    
    public int size()
    {
      return IdentityHashMap.this.size;
    }
    
    public boolean contains(Object paramObject)
    {
      return IdentityHashMap.this.containsValue(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      Iterator localIterator = iterator();
      while (localIterator.hasNext()) {
        if (localIterator.next() == paramObject)
        {
          localIterator.remove();
          return true;
        }
      }
      return false;
    }
    
    public void clear()
    {
      IdentityHashMap.this.clear();
    }
    
    public Object[] toArray()
    {
      return toArray(new Object[0]);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      int i = IdentityHashMap.this.modCount;
      int j = size();
      if (paramArrayOfT.length < j) {
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), j);
      }
      Object[] arrayOfObject = IdentityHashMap.this.table;
      int k = 0;
      for (int m = 0; m < arrayOfObject.length; m += 2) {
        if (arrayOfObject[m] != null)
        {
          if (k >= j) {
            throw new ConcurrentModificationException();
          }
          paramArrayOfT[(k++)] = arrayOfObject[(m + 1)];
        }
      }
      if ((k < j) || (i != IdentityHashMap.this.modCount)) {
        throw new ConcurrentModificationException();
      }
      if (k < paramArrayOfT.length) {
        paramArrayOfT[k] = null;
      }
      return paramArrayOfT;
    }
    
    public Spliterator<V> spliterator()
    {
      return new IdentityHashMap.ValueSpliterator(IdentityHashMap.this, 0, -1, 0, 0);
    }
  }
}
