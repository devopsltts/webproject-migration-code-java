package java.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class WeakHashMap<K, V>
  extends AbstractMap<K, V>
  implements Map<K, V>
{
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final int MAXIMUM_CAPACITY = 1073741824;
  private static final float DEFAULT_LOAD_FACTOR = 0.75F;
  Entry<K, V>[] table;
  private int size;
  private int threshold;
  private final float loadFactor;
  private final ReferenceQueue<Object> queue = new ReferenceQueue();
  int modCount;
  private static final Object NULL_KEY = new Object();
  private transient Set<Map.Entry<K, V>> entrySet;
  
  private Entry<K, V>[] newTable(int paramInt)
  {
    return (Entry[])new Entry[paramInt];
  }
  
  public WeakHashMap(int paramInt, float paramFloat)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Illegal Initial Capacity: " + paramInt);
    }
    if (paramInt > 1073741824) {
      paramInt = 1073741824;
    }
    if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
      throw new IllegalArgumentException("Illegal Load factor: " + paramFloat);
    }
    int i = 1;
    while (i < paramInt) {
      i <<= 1;
    }
    this.table = newTable(i);
    this.loadFactor = paramFloat;
    this.threshold = ((int)(i * paramFloat));
  }
  
  public WeakHashMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public WeakHashMap()
  {
    this(16, 0.75F);
  }
  
  public WeakHashMap(Map<? extends K, ? extends V> paramMap)
  {
    this(Math.max((int)(paramMap.size() / 0.75F) + 1, 16), 0.75F);
    putAll(paramMap);
  }
  
  private static Object maskNull(Object paramObject)
  {
    return paramObject == null ? NULL_KEY : paramObject;
  }
  
  static Object unmaskNull(Object paramObject)
  {
    return paramObject == NULL_KEY ? null : paramObject;
  }
  
  private static boolean eq(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || (paramObject1.equals(paramObject2));
  }
  
  final int hash(Object paramObject)
  {
    int i = paramObject.hashCode();
    i ^= i >>> 20 ^ i >>> 12;
    return i ^ i >>> 7 ^ i >>> 4;
  }
  
  private static int indexFor(int paramInt1, int paramInt2)
  {
    return paramInt1 & paramInt2 - 1;
  }
  
  private void expungeStaleEntries()
  {
    Reference localReference;
    while ((localReference = this.queue.poll()) != null) {
      synchronized (this.queue)
      {
        Entry localEntry1 = (Entry)localReference;
        int i = indexFor(localEntry1.hash, this.table.length);
        Object localObject1 = this.table[i];
        Entry localEntry2;
        for (Object localObject2 = localObject1; localObject2 != null; localObject2 = localEntry2)
        {
          localEntry2 = localObject2.next;
          if (localObject2 == localEntry1)
          {
            if (localObject1 == localEntry1) {
              this.table[i] = localEntry2;
            } else {
              ((Entry)localObject1).next = localEntry2;
            }
            localEntry1.value = null;
            this.size -= 1;
            break;
          }
          localObject1 = localObject2;
        }
      }
    }
  }
  
  private Entry<K, V>[] getTable()
  {
    expungeStaleEntries();
    return this.table;
  }
  
  public int size()
  {
    if (this.size == 0) {
      return 0;
    }
    expungeStaleEntries();
    return this.size;
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public V get(Object paramObject)
  {
    Object localObject = maskNull(paramObject);
    int i = hash(localObject);
    Entry[] arrayOfEntry = getTable();
    int j = indexFor(i, arrayOfEntry.length);
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next) {
      if ((localEntry.hash == i) && (eq(localObject, localEntry.get()))) {
        return localEntry.value;
      }
    }
    return null;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return getEntry(paramObject) != null;
  }
  
  Entry<K, V> getEntry(Object paramObject)
  {
    Object localObject = maskNull(paramObject);
    int i = hash(localObject);
    Entry[] arrayOfEntry = getTable();
    int j = indexFor(i, arrayOfEntry.length);
    for (Entry localEntry = arrayOfEntry[j]; (localEntry != null) && ((localEntry.hash != i) || (!eq(localObject, localEntry.get()))); localEntry = localEntry.next) {}
    return localEntry;
  }
  
  public V put(K paramK, V paramV)
  {
    Object localObject1 = maskNull(paramK);
    int i = hash(localObject1);
    Entry[] arrayOfEntry = getTable();
    int j = indexFor(i, arrayOfEntry.length);
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next) {
      if ((i == localEntry.hash) && (eq(localObject1, localEntry.get())))
      {
        Object localObject2 = localEntry.value;
        if (paramV != localObject2) {
          localEntry.value = paramV;
        }
        return localObject2;
      }
    }
    this.modCount += 1;
    localEntry = arrayOfEntry[j];
    arrayOfEntry[j] = new Entry(localObject1, paramV, this.queue, i, localEntry);
    if (++this.size >= this.threshold) {
      resize(arrayOfEntry.length * 2);
    }
    return null;
  }
  
  void resize(int paramInt)
  {
    Entry[] arrayOfEntry1 = getTable();
    int i = arrayOfEntry1.length;
    if (i == 1073741824)
    {
      this.threshold = Integer.MAX_VALUE;
      return;
    }
    Entry[] arrayOfEntry2 = newTable(paramInt);
    transfer(arrayOfEntry1, arrayOfEntry2);
    this.table = arrayOfEntry2;
    if (this.size >= this.threshold / 2)
    {
      this.threshold = ((int)(paramInt * this.loadFactor));
    }
    else
    {
      expungeStaleEntries();
      transfer(arrayOfEntry2, arrayOfEntry1);
      this.table = arrayOfEntry1;
    }
  }
  
  private void transfer(Entry<K, V>[] paramArrayOfEntry1, Entry<K, V>[] paramArrayOfEntry2)
  {
    for (int i = 0; i < paramArrayOfEntry1.length; i++)
    {
      Object localObject1 = paramArrayOfEntry1[i];
      paramArrayOfEntry1[i] = null;
      while (localObject1 != null)
      {
        Entry localEntry = ((Entry)localObject1).next;
        Object localObject2 = ((Entry)localObject1).get();
        if (localObject2 == null)
        {
          ((Entry)localObject1).next = null;
          ((Entry)localObject1).value = null;
          this.size -= 1;
        }
        else
        {
          int j = indexFor(((Entry)localObject1).hash, paramArrayOfEntry2.length);
          ((Entry)localObject1).next = paramArrayOfEntry2[j];
          paramArrayOfEntry2[j] = localObject1;
        }
        localObject1 = localEntry;
      }
    }
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    int i = paramMap.size();
    if (i == 0) {
      return;
    }
    if (i > this.threshold)
    {
      int j = (int)(i / this.loadFactor + 1.0F);
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
    int i = hash(localObject1);
    Entry[] arrayOfEntry = getTable();
    int j = indexFor(i, arrayOfEntry.length);
    Object localObject2 = arrayOfEntry[j];
    Entry localEntry;
    for (Object localObject3 = localObject2; localObject3 != null; localObject3 = localEntry)
    {
      localEntry = localObject3.next;
      if ((i == localObject3.hash) && (eq(localObject1, localObject3.get())))
      {
        this.modCount += 1;
        this.size -= 1;
        if (localObject2 == localObject3) {
          arrayOfEntry[j] = localEntry;
        } else {
          ((Entry)localObject2).next = localEntry;
        }
        return localObject3.value;
      }
      localObject2 = localObject3;
    }
    return null;
  }
  
  boolean removeMapping(Object paramObject)
  {
    if (!(paramObject instanceof Map.Entry)) {
      return false;
    }
    Entry[] arrayOfEntry = getTable();
    Map.Entry localEntry = (Map.Entry)paramObject;
    Object localObject1 = maskNull(localEntry.getKey());
    int i = hash(localObject1);
    int j = indexFor(i, arrayOfEntry.length);
    Object localObject2 = arrayOfEntry[j];
    Entry localEntry1;
    for (Object localObject3 = localObject2; localObject3 != null; localObject3 = localEntry1)
    {
      localEntry1 = localObject3.next;
      if ((i == localObject3.hash) && (localObject3.equals(localEntry)))
      {
        this.modCount += 1;
        this.size -= 1;
        if (localObject2 == localObject3) {
          arrayOfEntry[j] = localEntry1;
        } else {
          ((Entry)localObject2).next = localEntry1;
        }
        return true;
      }
      localObject2 = localObject3;
    }
    return false;
  }
  
  public void clear()
  {
    while (this.queue.poll() != null) {}
    this.modCount += 1;
    Arrays.fill(this.table, null);
    this.size = 0;
    while (this.queue.poll() != null) {}
  }
  
  public boolean containsValue(Object paramObject)
  {
    if (paramObject == null) {
      return containsNullValue();
    }
    Entry[] arrayOfEntry = getTable();
    int i = arrayOfEntry.length;
    while (i-- > 0) {
      for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = localEntry.next) {
        if (paramObject.equals(localEntry.value)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean containsNullValue()
  {
    Entry[] arrayOfEntry = getTable();
    int i = arrayOfEntry.length;
    while (i-- > 0) {
      for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = localEntry.next) {
        if (localEntry.value == null) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Set<K> keySet()
  {
    Set localSet = this.keySet;
    return this.keySet = new KeySet(null);
  }
  
  public Collection<V> values()
  {
    Collection localCollection = this.values;
    return this.values = new Values(null);
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    Set localSet = this.entrySet;
    return this.entrySet = new EntrySet(null);
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer)
  {
    Objects.requireNonNull(paramBiConsumer);
    int i = this.modCount;
    Entry[] arrayOfEntry1 = getTable();
    for (Entry localEntry : arrayOfEntry1) {
      while (localEntry != null)
      {
        Object localObject = localEntry.get();
        if (localObject != null) {
          paramBiConsumer.accept(unmaskNull(localObject), localEntry.value);
        }
        localEntry = localEntry.next;
        if (i != this.modCount) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    Objects.requireNonNull(paramBiFunction);
    int i = this.modCount;
    Entry[] arrayOfEntry1 = getTable();
    for (Entry localEntry : arrayOfEntry1) {
      while (localEntry != null)
      {
        Object localObject = localEntry.get();
        if (localObject != null) {
          localEntry.value = paramBiFunction.apply(unmaskNull(localObject), localEntry.value);
        }
        localEntry = localEntry.next;
        if (i != this.modCount) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }
  
  private static class Entry<K, V>
    extends WeakReference<Object>
    implements Map.Entry<K, V>
  {
    V value;
    final int hash;
    Entry<K, V> next;
    
    Entry(Object paramObject, V paramV, ReferenceQueue<Object> paramReferenceQueue, int paramInt, Entry<K, V> paramEntry)
    {
      super(paramReferenceQueue);
      this.value = paramV;
      this.hash = paramInt;
      this.next = paramEntry;
    }
    
    public K getKey()
    {
      return WeakHashMap.unmaskNull(get());
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
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject1 = getKey();
      Object localObject2 = localEntry.getKey();
      if ((localObject1 == localObject2) || ((localObject1 != null) && (localObject1.equals(localObject2))))
      {
        Object localObject3 = getValue();
        Object localObject4 = localEntry.getValue();
        if ((localObject3 == localObject4) || ((localObject3 != null) && (localObject3.equals(localObject4)))) {
          return true;
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      Object localObject1 = getKey();
      Object localObject2 = getValue();
      return Objects.hashCode(localObject1) ^ Objects.hashCode(localObject2);
    }
    
    public String toString()
    {
      return getKey() + "=" + getValue();
    }
  }
  
  private class EntryIterator
    extends WeakHashMap<K, V>.HashIterator<Map.Entry<K, V>>
  {
    private EntryIterator()
    {
      super();
    }
    
    public Map.Entry<K, V> next()
    {
      return nextEntry();
    }
  }
  
  private class EntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new WeakHashMap.EntryIterator(WeakHashMap.this, null);
    }
    
    public boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      WeakHashMap.Entry localEntry1 = WeakHashMap.this.getEntry(localEntry.getKey());
      return (localEntry1 != null) && (localEntry1.equals(localEntry));
    }
    
    public boolean remove(Object paramObject)
    {
      return WeakHashMap.this.removeMapping(paramObject);
    }
    
    public int size()
    {
      return WeakHashMap.this.size();
    }
    
    public void clear()
    {
      WeakHashMap.this.clear();
    }
    
    private List<Map.Entry<K, V>> deepCopy()
    {
      ArrayList localArrayList = new ArrayList(size());
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localArrayList.add(new AbstractMap.SimpleEntry(localEntry));
      }
      return localArrayList;
    }
    
    public Object[] toArray()
    {
      return deepCopy().toArray();
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      return deepCopy().toArray(paramArrayOfT);
    }
    
    public Spliterator<Map.Entry<K, V>> spliterator()
    {
      return new WeakHashMap.EntrySpliterator(WeakHashMap.this, 0, -1, 0, 0);
    }
  }
  
  static final class EntrySpliterator<K, V>
    extends WeakHashMap.WeakHashMapSpliterator<K, V>
    implements Spliterator<Map.Entry<K, V>>
  {
    EntrySpliterator(WeakHashMap<K, V> paramWeakHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public EntrySpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return j >= k ? null : new EntrySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      WeakHashMap localWeakHashMap = this.map;
      WeakHashMap.Entry[] arrayOfEntry = localWeakHashMap.table;
      int j;
      int k;
      if ((j = this.fence) < 0)
      {
        k = this.expectedModCount = localWeakHashMap.modCount;
        j = this.fence = arrayOfEntry.length;
      }
      else
      {
        k = this.expectedModCount;
      }
      int i;
      if ((arrayOfEntry.length >= j) && ((i = this.index) >= 0) && ((i < (this.index = j)) || (this.current != null)))
      {
        WeakHashMap.Entry localEntry = this.current;
        this.current = null;
        do
        {
          if (localEntry == null)
          {
            localEntry = arrayOfEntry[(i++)];
          }
          else
          {
            Object localObject1 = localEntry.get();
            Object localObject2 = localEntry.value;
            localEntry = localEntry.next;
            if (localObject1 != null)
            {
              Object localObject3 = WeakHashMap.unmaskNull(localObject1);
              paramConsumer.accept(new AbstractMap.SimpleImmutableEntry(localObject3, localObject2));
            }
          }
        } while ((localEntry != null) || (i < j));
      }
      if (localWeakHashMap.modCount != k) {
        throw new ConcurrentModificationException();
      }
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      WeakHashMap.Entry[] arrayOfEntry = this.map.table;
      int i;
      if ((arrayOfEntry.length >= (i = getFence())) && (this.index >= 0)) {
        while ((this.current != null) || (this.index < i)) {
          if (this.current == null)
          {
            this.current = arrayOfEntry[(this.index++)];
          }
          else
          {
            Object localObject1 = this.current.get();
            Object localObject2 = this.current.value;
            this.current = this.current.next;
            if (localObject1 != null)
            {
              Object localObject3 = WeakHashMap.unmaskNull(localObject1);
              paramConsumer.accept(new AbstractMap.SimpleImmutableEntry(localObject3, localObject2));
              if (this.map.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
              }
              return true;
            }
          }
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return 1;
    }
  }
  
  private abstract class HashIterator<T>
    implements Iterator<T>
  {
    private int index = WeakHashMap.this.isEmpty() ? 0 : WeakHashMap.this.table.length;
    private WeakHashMap.Entry<K, V> entry;
    private WeakHashMap.Entry<K, V> lastReturned;
    private int expectedModCount = WeakHashMap.this.modCount;
    private Object nextKey;
    private Object currentKey;
    
    HashIterator() {}
    
    public boolean hasNext()
    {
      WeakHashMap.Entry[] arrayOfEntry = WeakHashMap.this.table;
      while (this.nextKey == null)
      {
        WeakHashMap.Entry localEntry = this.entry;
        int i = this.index;
        while ((localEntry == null) && (i > 0)) {
          localEntry = arrayOfEntry[(--i)];
        }
        this.entry = localEntry;
        this.index = i;
        if (localEntry == null)
        {
          this.currentKey = null;
          return false;
        }
        this.nextKey = localEntry.get();
        if (this.nextKey == null) {
          this.entry = this.entry.next;
        }
      }
      return true;
    }
    
    protected WeakHashMap.Entry<K, V> nextEntry()
    {
      if (WeakHashMap.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if ((this.nextKey == null) && (!hasNext())) {
        throw new NoSuchElementException();
      }
      this.lastReturned = this.entry;
      this.entry = this.entry.next;
      this.currentKey = this.nextKey;
      this.nextKey = null;
      return this.lastReturned;
    }
    
    public void remove()
    {
      if (this.lastReturned == null) {
        throw new IllegalStateException();
      }
      if (WeakHashMap.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
      WeakHashMap.this.remove(this.currentKey);
      this.expectedModCount = WeakHashMap.this.modCount;
      this.lastReturned = null;
      this.currentKey = null;
    }
  }
  
  private class KeyIterator
    extends WeakHashMap<K, V>.HashIterator<K>
  {
    private KeyIterator()
    {
      super();
    }
    
    public K next()
    {
      return nextEntry().getKey();
    }
  }
  
  private class KeySet
    extends AbstractSet<K>
  {
    private KeySet() {}
    
    public Iterator<K> iterator()
    {
      return new WeakHashMap.KeyIterator(WeakHashMap.this, null);
    }
    
    public int size()
    {
      return WeakHashMap.this.size();
    }
    
    public boolean contains(Object paramObject)
    {
      return WeakHashMap.this.containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      if (WeakHashMap.this.containsKey(paramObject))
      {
        WeakHashMap.this.remove(paramObject);
        return true;
      }
      return false;
    }
    
    public void clear()
    {
      WeakHashMap.this.clear();
    }
    
    public Spliterator<K> spliterator()
    {
      return new WeakHashMap.KeySpliterator(WeakHashMap.this, 0, -1, 0, 0);
    }
  }
  
  static final class KeySpliterator<K, V>
    extends WeakHashMap.WeakHashMapSpliterator<K, V>
    implements Spliterator<K>
  {
    KeySpliterator(WeakHashMap<K, V> paramWeakHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public KeySpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return j >= k ? null : new KeySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      WeakHashMap localWeakHashMap = this.map;
      WeakHashMap.Entry[] arrayOfEntry = localWeakHashMap.table;
      int j;
      int k;
      if ((j = this.fence) < 0)
      {
        k = this.expectedModCount = localWeakHashMap.modCount;
        j = this.fence = arrayOfEntry.length;
      }
      else
      {
        k = this.expectedModCount;
      }
      int i;
      if ((arrayOfEntry.length >= j) && ((i = this.index) >= 0) && ((i < (this.index = j)) || (this.current != null)))
      {
        WeakHashMap.Entry localEntry = this.current;
        this.current = null;
        do
        {
          if (localEntry == null)
          {
            localEntry = arrayOfEntry[(i++)];
          }
          else
          {
            Object localObject1 = localEntry.get();
            localEntry = localEntry.next;
            if (localObject1 != null)
            {
              Object localObject2 = WeakHashMap.unmaskNull(localObject1);
              paramConsumer.accept(localObject2);
            }
          }
        } while ((localEntry != null) || (i < j));
      }
      if (localWeakHashMap.modCount != k) {
        throw new ConcurrentModificationException();
      }
    }
    
    public boolean tryAdvance(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      WeakHashMap.Entry[] arrayOfEntry = this.map.table;
      int i;
      if ((arrayOfEntry.length >= (i = getFence())) && (this.index >= 0)) {
        while ((this.current != null) || (this.index < i)) {
          if (this.current == null)
          {
            this.current = arrayOfEntry[(this.index++)];
          }
          else
          {
            Object localObject1 = this.current.get();
            this.current = this.current.next;
            if (localObject1 != null)
            {
              Object localObject2 = WeakHashMap.unmaskNull(localObject1);
              paramConsumer.accept(localObject2);
              if (this.map.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
              }
              return true;
            }
          }
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return 1;
    }
  }
  
  private class ValueIterator
    extends WeakHashMap<K, V>.HashIterator<V>
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
  
  static final class ValueSpliterator<K, V>
    extends WeakHashMap.WeakHashMapSpliterator<K, V>
    implements Spliterator<V>
  {
    ValueSpliterator(WeakHashMap<K, V> paramWeakHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public ValueSpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return j >= k ? null : new ValueSpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      WeakHashMap localWeakHashMap = this.map;
      WeakHashMap.Entry[] arrayOfEntry = localWeakHashMap.table;
      int j;
      int k;
      if ((j = this.fence) < 0)
      {
        k = this.expectedModCount = localWeakHashMap.modCount;
        j = this.fence = arrayOfEntry.length;
      }
      else
      {
        k = this.expectedModCount;
      }
      int i;
      if ((arrayOfEntry.length >= j) && ((i = this.index) >= 0) && ((i < (this.index = j)) || (this.current != null)))
      {
        WeakHashMap.Entry localEntry = this.current;
        this.current = null;
        do
        {
          if (localEntry == null)
          {
            localEntry = arrayOfEntry[(i++)];
          }
          else
          {
            Object localObject1 = localEntry.get();
            Object localObject2 = localEntry.value;
            localEntry = localEntry.next;
            if (localObject1 != null) {
              paramConsumer.accept(localObject2);
            }
          }
        } while ((localEntry != null) || (i < j));
      }
      if (localWeakHashMap.modCount != k) {
        throw new ConcurrentModificationException();
      }
    }
    
    public boolean tryAdvance(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      WeakHashMap.Entry[] arrayOfEntry = this.map.table;
      int i;
      if ((arrayOfEntry.length >= (i = getFence())) && (this.index >= 0)) {
        while ((this.current != null) || (this.index < i)) {
          if (this.current == null)
          {
            this.current = arrayOfEntry[(this.index++)];
          }
          else
          {
            Object localObject1 = this.current.get();
            Object localObject2 = this.current.value;
            this.current = this.current.next;
            if (localObject1 != null)
            {
              paramConsumer.accept(localObject2);
              if (this.map.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
              }
              return true;
            }
          }
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return 0;
    }
  }
  
  private class Values
    extends AbstractCollection<V>
  {
    private Values() {}
    
    public Iterator<V> iterator()
    {
      return new WeakHashMap.ValueIterator(WeakHashMap.this, null);
    }
    
    public int size()
    {
      return WeakHashMap.this.size();
    }
    
    public boolean contains(Object paramObject)
    {
      return WeakHashMap.this.containsValue(paramObject);
    }
    
    public void clear()
    {
      WeakHashMap.this.clear();
    }
    
    public Spliterator<V> spliterator()
    {
      return new WeakHashMap.ValueSpliterator(WeakHashMap.this, 0, -1, 0, 0);
    }
  }
  
  static class WeakHashMapSpliterator<K, V>
  {
    final WeakHashMap<K, V> map;
    WeakHashMap.Entry<K, V> current;
    int index;
    int fence;
    int est;
    int expectedModCount;
    
    WeakHashMapSpliterator(WeakHashMap<K, V> paramWeakHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.map = paramWeakHashMap;
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
        WeakHashMap localWeakHashMap = this.map;
        this.est = localWeakHashMap.size();
        this.expectedModCount = localWeakHashMap.modCount;
        i = this.fence = localWeakHashMap.table.length;
      }
      return i;
    }
    
    public final long estimateSize()
    {
      getFence();
      return this.est;
    }
  }
}
