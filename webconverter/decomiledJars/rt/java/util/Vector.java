package java.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Vector<E>
  extends AbstractList<E>
  implements List<E>, RandomAccess, Cloneable, Serializable
{
  protected Object[] elementData;
  protected int elementCount;
  protected int capacityIncrement;
  private static final long serialVersionUID = -2767605614048989439L;
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public Vector(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt1);
    }
    this.elementData = new Object[paramInt1];
    this.capacityIncrement = paramInt2;
  }
  
  public Vector(int paramInt)
  {
    this(paramInt, 0);
  }
  
  public Vector()
  {
    this(10);
  }
  
  public Vector(Collection<? extends E> paramCollection)
  {
    this.elementData = paramCollection.toArray();
    this.elementCount = this.elementData.length;
    if (this.elementData.getClass() != [Ljava.lang.Object.class) {
      this.elementData = Arrays.copyOf(this.elementData, this.elementCount, [Ljava.lang.Object.class);
    }
  }
  
  public synchronized void copyInto(Object[] paramArrayOfObject)
  {
    System.arraycopy(this.elementData, 0, paramArrayOfObject, 0, this.elementCount);
  }
  
  public synchronized void trimToSize()
  {
    this.modCount += 1;
    int i = this.elementData.length;
    if (this.elementCount < i) {
      this.elementData = Arrays.copyOf(this.elementData, this.elementCount);
    }
  }
  
  public synchronized void ensureCapacity(int paramInt)
  {
    if (paramInt > 0)
    {
      this.modCount += 1;
      ensureCapacityHelper(paramInt);
    }
  }
  
  private void ensureCapacityHelper(int paramInt)
  {
    if (paramInt - this.elementData.length > 0) {
      grow(paramInt);
    }
  }
  
  private void grow(int paramInt)
  {
    int i = this.elementData.length;
    int j = i + (this.capacityIncrement > 0 ? this.capacityIncrement : i);
    if (j - paramInt < 0) {
      j = paramInt;
    }
    if (j - 2147483639 > 0) {
      j = hugeCapacity(paramInt);
    }
    this.elementData = Arrays.copyOf(this.elementData, j);
  }
  
  private static int hugeCapacity(int paramInt)
  {
    if (paramInt < 0) {
      throw new OutOfMemoryError();
    }
    return paramInt > 2147483639 ? Integer.MAX_VALUE : 2147483639;
  }
  
  public synchronized void setSize(int paramInt)
  {
    this.modCount += 1;
    if (paramInt > this.elementCount) {
      ensureCapacityHelper(paramInt);
    } else {
      for (int i = paramInt; i < this.elementCount; i++) {
        this.elementData[i] = null;
      }
    }
    this.elementCount = paramInt;
  }
  
  public synchronized int capacity()
  {
    return this.elementData.length;
  }
  
  public synchronized int size()
  {
    return this.elementCount;
  }
  
  public synchronized boolean isEmpty()
  {
    return this.elementCount == 0;
  }
  
  public Enumeration<E> elements()
  {
    new Enumeration()
    {
      int count = 0;
      
      public boolean hasMoreElements()
      {
        return this.count < Vector.this.elementCount;
      }
      
      public E nextElement()
      {
        synchronized (Vector.this)
        {
          if (this.count < Vector.this.elementCount) {
            return Vector.this.elementData(this.count++);
          }
        }
        throw new NoSuchElementException("Vector Enumeration");
      }
    };
  }
  
  public boolean contains(Object paramObject)
  {
    return indexOf(paramObject, 0) >= 0;
  }
  
  public int indexOf(Object paramObject)
  {
    return indexOf(paramObject, 0);
  }
  
  public synchronized int indexOf(Object paramObject, int paramInt)
  {
    int i;
    if (paramObject == null) {
      for (i = paramInt; i < this.elementCount; i++) {
        if (this.elementData[i] == null) {
          return i;
        }
      }
    } else {
      for (i = paramInt; i < this.elementCount; i++) {
        if (paramObject.equals(this.elementData[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public synchronized int lastIndexOf(Object paramObject)
  {
    return lastIndexOf(paramObject, this.elementCount - 1);
  }
  
  public synchronized int lastIndexOf(Object paramObject, int paramInt)
  {
    if (paramInt >= this.elementCount) {
      throw new IndexOutOfBoundsException(paramInt + " >= " + this.elementCount);
    }
    int i;
    if (paramObject == null) {
      for (i = paramInt; i >= 0; i--) {
        if (this.elementData[i] == null) {
          return i;
        }
      }
    } else {
      for (i = paramInt; i >= 0; i--) {
        if (paramObject.equals(this.elementData[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public synchronized E elementAt(int paramInt)
  {
    if (paramInt >= this.elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + this.elementCount);
    }
    return elementData(paramInt);
  }
  
  public synchronized E firstElement()
  {
    if (this.elementCount == 0) {
      throw new NoSuchElementException();
    }
    return elementData(0);
  }
  
  public synchronized E lastElement()
  {
    if (this.elementCount == 0) {
      throw new NoSuchElementException();
    }
    return elementData(this.elementCount - 1);
  }
  
  public synchronized void setElementAt(E paramE, int paramInt)
  {
    if (paramInt >= this.elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + this.elementCount);
    }
    this.elementData[paramInt] = paramE;
  }
  
  public synchronized void removeElementAt(int paramInt)
  {
    this.modCount += 1;
    if (paramInt >= this.elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + this.elementCount);
    }
    if (paramInt < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    int i = this.elementCount - paramInt - 1;
    if (i > 0) {
      System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i);
    }
    this.elementCount -= 1;
    this.elementData[this.elementCount] = null;
  }
  
  public synchronized void insertElementAt(E paramE, int paramInt)
  {
    this.modCount += 1;
    if (paramInt > this.elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " > " + this.elementCount);
    }
    ensureCapacityHelper(this.elementCount + 1);
    System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + 1, this.elementCount - paramInt);
    this.elementData[paramInt] = paramE;
    this.elementCount += 1;
  }
  
  public synchronized void addElement(E paramE)
  {
    this.modCount += 1;
    ensureCapacityHelper(this.elementCount + 1);
    this.elementData[(this.elementCount++)] = paramE;
  }
  
  public synchronized boolean removeElement(Object paramObject)
  {
    this.modCount += 1;
    int i = indexOf(paramObject);
    if (i >= 0)
    {
      removeElementAt(i);
      return true;
    }
    return false;
  }
  
  public synchronized void removeAllElements()
  {
    this.modCount += 1;
    for (int i = 0; i < this.elementCount; i++) {
      this.elementData[i] = null;
    }
    this.elementCount = 0;
  }
  
  public synchronized Object clone()
  {
    try
    {
      Vector localVector = (Vector)super.clone();
      localVector.elementData = Arrays.copyOf(this.elementData, this.elementCount);
      localVector.modCount = 0;
      return localVector;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public synchronized Object[] toArray()
  {
    return Arrays.copyOf(this.elementData, this.elementCount);
  }
  
  public synchronized <T> T[] toArray(T[] paramArrayOfT)
  {
    if (paramArrayOfT.length < this.elementCount) {
      return (Object[])Arrays.copyOf(this.elementData, this.elementCount, paramArrayOfT.getClass());
    }
    System.arraycopy(this.elementData, 0, paramArrayOfT, 0, this.elementCount);
    if (paramArrayOfT.length > this.elementCount) {
      paramArrayOfT[this.elementCount] = null;
    }
    return paramArrayOfT;
  }
  
  E elementData(int paramInt)
  {
    return this.elementData[paramInt];
  }
  
  public synchronized E get(int paramInt)
  {
    if (paramInt >= this.elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    return elementData(paramInt);
  }
  
  public synchronized E set(int paramInt, E paramE)
  {
    if (paramInt >= this.elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    Object localObject = elementData(paramInt);
    this.elementData[paramInt] = paramE;
    return localObject;
  }
  
  public synchronized boolean add(E paramE)
  {
    this.modCount += 1;
    ensureCapacityHelper(this.elementCount + 1);
    this.elementData[(this.elementCount++)] = paramE;
    return true;
  }
  
  public boolean remove(Object paramObject)
  {
    return removeElement(paramObject);
  }
  
  public void add(int paramInt, E paramE)
  {
    insertElementAt(paramE, paramInt);
  }
  
  public synchronized E remove(int paramInt)
  {
    this.modCount += 1;
    if (paramInt >= this.elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    Object localObject = elementData(paramInt);
    int i = this.elementCount - paramInt - 1;
    if (i > 0) {
      System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i);
    }
    this.elementData[(--this.elementCount)] = null;
    return localObject;
  }
  
  public void clear()
  {
    removeAllElements();
  }
  
  public synchronized boolean containsAll(Collection<?> paramCollection)
  {
    return super.containsAll(paramCollection);
  }
  
  public synchronized boolean addAll(Collection<? extends E> paramCollection)
  {
    this.modCount += 1;
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityHelper(this.elementCount + i);
    System.arraycopy(arrayOfObject, 0, this.elementData, this.elementCount, i);
    this.elementCount += i;
    return i != 0;
  }
  
  public synchronized boolean removeAll(Collection<?> paramCollection)
  {
    return super.removeAll(paramCollection);
  }
  
  public synchronized boolean retainAll(Collection<?> paramCollection)
  {
    return super.retainAll(paramCollection);
  }
  
  public synchronized boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    this.modCount += 1;
    if ((paramInt < 0) || (paramInt > this.elementCount)) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityHelper(this.elementCount + i);
    int j = this.elementCount - paramInt;
    if (j > 0) {
      System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + i, j);
    }
    System.arraycopy(arrayOfObject, 0, this.elementData, paramInt, i);
    this.elementCount += i;
    return i != 0;
  }
  
  public synchronized boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public synchronized int hashCode()
  {
    return super.hashCode();
  }
  
  public synchronized String toString()
  {
    return super.toString();
  }
  
  public synchronized List<E> subList(int paramInt1, int paramInt2)
  {
    return Collections.synchronizedList(super.subList(paramInt1, paramInt2), this);
  }
  
  protected synchronized void removeRange(int paramInt1, int paramInt2)
  {
    this.modCount += 1;
    int i = this.elementCount - paramInt2;
    System.arraycopy(this.elementData, paramInt2, this.elementData, paramInt1, i);
    int j = this.elementCount - (paramInt2 - paramInt1);
    while (this.elementCount != j) {
      this.elementData[(--this.elementCount)] = null;
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    Object[] arrayOfObject;
    synchronized (this)
    {
      localPutField.put("capacityIncrement", this.capacityIncrement);
      localPutField.put("elementCount", this.elementCount);
      arrayOfObject = (Object[])this.elementData.clone();
    }
    localPutField.put("elementData", arrayOfObject);
    paramObjectOutputStream.writeFields();
  }
  
  public synchronized ListIterator<E> listIterator(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > this.elementCount)) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
    return new ListItr(paramInt);
  }
  
  public synchronized ListIterator<E> listIterator()
  {
    return new ListItr(0);
  }
  
  public synchronized Iterator<E> iterator()
  {
    return new Itr(null);
  }
  
  public synchronized void forEach(Consumer<? super E> paramConsumer)
  {
    Objects.requireNonNull(paramConsumer);
    int i = this.modCount;
    Object[] arrayOfObject = (Object[])this.elementData;
    int j = this.elementCount;
    for (int k = 0; (this.modCount == i) && (k < j); k++) {
      paramConsumer.accept(arrayOfObject[k]);
    }
    if (this.modCount != i) {
      throw new ConcurrentModificationException();
    }
  }
  
  public synchronized boolean removeIf(Predicate<? super E> paramPredicate)
  {
    Objects.requireNonNull(paramPredicate);
    int i = 0;
    int j = this.elementCount;
    BitSet localBitSet = new BitSet(j);
    int k = this.modCount;
    for (int m = 0; (this.modCount == k) && (m < j); m++)
    {
      Object localObject = this.elementData[m];
      if (paramPredicate.test(localObject))
      {
        localBitSet.set(m);
        i++;
      }
    }
    if (this.modCount != k) {
      throw new ConcurrentModificationException();
    }
    m = i > 0 ? 1 : 0;
    if (m != 0)
    {
      int n = j - i;
      int i1 = 0;
      for (int i2 = 0; (i1 < j) && (i2 < n); i2++)
      {
        i1 = localBitSet.nextClearBit(i1);
        this.elementData[i2] = this.elementData[i1];
        i1++;
      }
      for (i1 = n; i1 < j; i1++) {
        this.elementData[i1] = null;
      }
      this.elementCount = n;
      if (this.modCount != k) {
        throw new ConcurrentModificationException();
      }
      this.modCount += 1;
    }
    return m;
  }
  
  public synchronized void replaceAll(UnaryOperator<E> paramUnaryOperator)
  {
    Objects.requireNonNull(paramUnaryOperator);
    int i = this.modCount;
    int j = this.elementCount;
    for (int k = 0; (this.modCount == i) && (k < j); k++) {
      this.elementData[k] = paramUnaryOperator.apply(this.elementData[k]);
    }
    if (this.modCount != i) {
      throw new ConcurrentModificationException();
    }
    this.modCount += 1;
  }
  
  public synchronized void sort(Comparator<? super E> paramComparator)
  {
    int i = this.modCount;
    Arrays.sort((Object[])this.elementData, 0, this.elementCount, paramComparator);
    if (this.modCount != i) {
      throw new ConcurrentModificationException();
    }
    this.modCount += 1;
  }
  
  public Spliterator<E> spliterator()
  {
    return new VectorSpliterator(this, null, 0, -1, 0);
  }
  
  private class Itr
    implements Iterator<E>
  {
    int cursor;
    int lastRet = -1;
    int expectedModCount = Vector.this.modCount;
    
    private Itr() {}
    
    public boolean hasNext()
    {
      return this.cursor != Vector.this.elementCount;
    }
    
    public E next()
    {
      synchronized (Vector.this)
      {
        checkForComodification();
        int i = this.cursor;
        if (i >= Vector.this.elementCount) {
          throw new NoSuchElementException();
        }
        this.cursor = (i + 1);
        return Vector.this.elementData(this.lastRet = i);
      }
    }
    
    public void remove()
    {
      if (this.lastRet == -1) {
        throw new IllegalStateException();
      }
      synchronized (Vector.this)
      {
        checkForComodification();
        Vector.this.remove(this.lastRet);
        this.expectedModCount = Vector.this.modCount;
      }
      this.cursor = this.lastRet;
      this.lastRet = -1;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      synchronized (Vector.this)
      {
        int i = Vector.this.elementCount;
        int j = this.cursor;
        if (j >= i) {
          return;
        }
        Object[] arrayOfObject = (Object[])Vector.this.elementData;
        if (j >= arrayOfObject.length) {
          throw new ConcurrentModificationException();
        }
        while ((j != i) && (Vector.this.modCount == this.expectedModCount)) {
          paramConsumer.accept(arrayOfObject[(j++)]);
        }
        this.cursor = j;
        this.lastRet = (j - 1);
        checkForComodification();
      }
    }
    
    final void checkForComodification()
    {
      if (Vector.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  final class ListItr
    extends Vector<E>.Itr
    implements ListIterator<E>
  {
    ListItr(int paramInt)
    {
      super(null);
      this.cursor = paramInt;
    }
    
    public boolean hasPrevious()
    {
      return this.cursor != 0;
    }
    
    public int nextIndex()
    {
      return this.cursor;
    }
    
    public int previousIndex()
    {
      return this.cursor - 1;
    }
    
    public E previous()
    {
      synchronized (Vector.this)
      {
        checkForComodification();
        int i = this.cursor - 1;
        if (i < 0) {
          throw new NoSuchElementException();
        }
        this.cursor = i;
        return Vector.this.elementData(this.lastRet = i);
      }
    }
    
    public void set(E paramE)
    {
      if (this.lastRet == -1) {
        throw new IllegalStateException();
      }
      synchronized (Vector.this)
      {
        checkForComodification();
        Vector.this.set(this.lastRet, paramE);
      }
    }
    
    public void add(E paramE)
    {
      int i = this.cursor;
      synchronized (Vector.this)
      {
        checkForComodification();
        Vector.this.add(i, paramE);
        this.expectedModCount = Vector.this.modCount;
      }
      this.cursor = (i + 1);
      this.lastRet = -1;
    }
  }
  
  static final class VectorSpliterator<E>
    implements Spliterator<E>
  {
    private final Vector<E> list;
    private Object[] array;
    private int index;
    private int fence;
    private int expectedModCount;
    
    VectorSpliterator(Vector<E> paramVector, Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
    {
      this.list = paramVector;
      this.array = paramArrayOfObject;
      this.index = paramInt1;
      this.fence = paramInt2;
      this.expectedModCount = paramInt3;
    }
    
    private int getFence()
    {
      int i;
      if ((i = this.fence) < 0) {
        synchronized (this.list)
        {
          this.array = this.list.elementData;
          this.expectedModCount = this.list.modCount;
          i = this.fence = this.list.elementCount;
        }
      }
      return i;
    }
    
    public Spliterator<E> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return j >= k ? null : new VectorSpliterator(this.list, this.array, j, this.index = k, this.expectedModCount);
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i;
      if (getFence() > (i = this.index))
      {
        this.index = (i + 1);
        paramConsumer.accept(this.array[i]);
        if (this.list.modCount != this.expectedModCount) {
          throw new ConcurrentModificationException();
        }
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Vector localVector;
      if ((localVector = this.list) != null)
      {
        int j;
        Object[] arrayOfObject;
        if ((j = this.fence) < 0) {
          synchronized (localVector)
          {
            this.expectedModCount = localVector.modCount;
            arrayOfObject = this.array = localVector.elementData;
            j = this.fence = localVector.elementCount;
          }
        } else {
          arrayOfObject = this.array;
        }
        int i;
        if ((arrayOfObject != null) && ((i = this.index) >= 0) && ((this.index = j) <= arrayOfObject.length))
        {
          while (i < j) {
            paramConsumer.accept(arrayOfObject[(i++)]);
          }
          if (localVector.modCount == this.expectedModCount) {
            return;
          }
        }
      }
      throw new ConcurrentModificationException();
    }
    
    public long estimateSize()
    {
      return getFence() - this.index;
    }
    
    public int characteristics()
    {
      return 16464;
    }
  }
}
