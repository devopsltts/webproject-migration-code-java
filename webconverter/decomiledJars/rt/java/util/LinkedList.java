package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.Consumer;

public class LinkedList<E>
  extends AbstractSequentialList<E>
  implements List<E>, Deque<E>, Cloneable, Serializable
{
  transient int size = 0;
  transient Node<E> first;
  transient Node<E> last;
  private static final long serialVersionUID = 876323262645176354L;
  
  public LinkedList() {}
  
  public LinkedList(Collection<? extends E> paramCollection)
  {
    this();
    addAll(paramCollection);
  }
  
  private void linkFirst(E paramE)
  {
    Node localNode1 = this.first;
    Node localNode2 = new Node(null, paramE, localNode1);
    this.first = localNode2;
    if (localNode1 == null) {
      this.last = localNode2;
    } else {
      localNode1.prev = localNode2;
    }
    this.size += 1;
    this.modCount += 1;
  }
  
  void linkLast(E paramE)
  {
    Node localNode1 = this.last;
    Node localNode2 = new Node(localNode1, paramE, null);
    this.last = localNode2;
    if (localNode1 == null) {
      this.first = localNode2;
    } else {
      localNode1.next = localNode2;
    }
    this.size += 1;
    this.modCount += 1;
  }
  
  void linkBefore(E paramE, Node<E> paramNode)
  {
    Node localNode1 = paramNode.prev;
    Node localNode2 = new Node(localNode1, paramE, paramNode);
    paramNode.prev = localNode2;
    if (localNode1 == null) {
      this.first = localNode2;
    } else {
      localNode1.next = localNode2;
    }
    this.size += 1;
    this.modCount += 1;
  }
  
  private E unlinkFirst(Node<E> paramNode)
  {
    Object localObject = paramNode.item;
    Node localNode = paramNode.next;
    paramNode.item = null;
    paramNode.next = null;
    this.first = localNode;
    if (localNode == null) {
      this.last = null;
    } else {
      localNode.prev = null;
    }
    this.size -= 1;
    this.modCount += 1;
    return localObject;
  }
  
  private E unlinkLast(Node<E> paramNode)
  {
    Object localObject = paramNode.item;
    Node localNode = paramNode.prev;
    paramNode.item = null;
    paramNode.prev = null;
    this.last = localNode;
    if (localNode == null) {
      this.first = null;
    } else {
      localNode.next = null;
    }
    this.size -= 1;
    this.modCount += 1;
    return localObject;
  }
  
  E unlink(Node<E> paramNode)
  {
    Object localObject = paramNode.item;
    Node localNode1 = paramNode.next;
    Node localNode2 = paramNode.prev;
    if (localNode2 == null)
    {
      this.first = localNode1;
    }
    else
    {
      localNode2.next = localNode1;
      paramNode.prev = null;
    }
    if (localNode1 == null)
    {
      this.last = localNode2;
    }
    else
    {
      localNode1.prev = localNode2;
      paramNode.next = null;
    }
    paramNode.item = null;
    this.size -= 1;
    this.modCount += 1;
    return localObject;
  }
  
  public E getFirst()
  {
    Node localNode = this.first;
    if (localNode == null) {
      throw new NoSuchElementException();
    }
    return localNode.item;
  }
  
  public E getLast()
  {
    Node localNode = this.last;
    if (localNode == null) {
      throw new NoSuchElementException();
    }
    return localNode.item;
  }
  
  public E removeFirst()
  {
    Node localNode = this.first;
    if (localNode == null) {
      throw new NoSuchElementException();
    }
    return unlinkFirst(localNode);
  }
  
  public E removeLast()
  {
    Node localNode = this.last;
    if (localNode == null) {
      throw new NoSuchElementException();
    }
    return unlinkLast(localNode);
  }
  
  public void addFirst(E paramE)
  {
    linkFirst(paramE);
  }
  
  public void addLast(E paramE)
  {
    linkLast(paramE);
  }
  
  public boolean contains(Object paramObject)
  {
    return indexOf(paramObject) != -1;
  }
  
  public int size()
  {
    return this.size;
  }
  
  public boolean add(E paramE)
  {
    linkLast(paramE);
    return true;
  }
  
  public boolean remove(Object paramObject)
  {
    Node localNode;
    if (paramObject == null) {
      for (localNode = this.first; localNode != null; localNode = localNode.next) {
        if (localNode.item == null)
        {
          unlink(localNode);
          return true;
        }
      }
    } else {
      for (localNode = this.first; localNode != null; localNode = localNode.next) {
        if (paramObject.equals(localNode.item))
        {
          unlink(localNode);
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    return addAll(this.size, paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    checkPositionIndex(paramInt);
    Object[] arrayOfObject1 = paramCollection.toArray();
    int i = arrayOfObject1.length;
    if (i == 0) {
      return false;
    }
    Node localNode1;
    Object localObject1;
    if (paramInt == this.size)
    {
      localNode1 = null;
      localObject1 = this.last;
    }
    else
    {
      localNode1 = node(paramInt);
      localObject1 = localNode1.prev;
    }
    for (Object localObject2 : arrayOfObject1)
    {
      Object localObject3 = localObject2;
      Node localNode2 = new Node((Node)localObject1, localObject3, null);
      if (localObject1 == null) {
        this.first = localNode2;
      } else {
        ((Node)localObject1).next = localNode2;
      }
      localObject1 = localNode2;
    }
    if (localNode1 == null)
    {
      this.last = ((Node)localObject1);
    }
    else
    {
      ((Node)localObject1).next = localNode1;
      localNode1.prev = ((Node)localObject1);
    }
    this.size += i;
    this.modCount += 1;
    return true;
  }
  
  public void clear()
  {
    Node localNode;
    for (Object localObject = this.first; localObject != null; localObject = localNode)
    {
      localNode = ((Node)localObject).next;
      ((Node)localObject).item = null;
      ((Node)localObject).next = null;
      ((Node)localObject).prev = null;
    }
    this.first = (this.last = null);
    this.size = 0;
    this.modCount += 1;
  }
  
  public E get(int paramInt)
  {
    checkElementIndex(paramInt);
    return node(paramInt).item;
  }
  
  public E set(int paramInt, E paramE)
  {
    checkElementIndex(paramInt);
    Node localNode = node(paramInt);
    Object localObject = localNode.item;
    localNode.item = paramE;
    return localObject;
  }
  
  public void add(int paramInt, E paramE)
  {
    checkPositionIndex(paramInt);
    if (paramInt == this.size) {
      linkLast(paramE);
    } else {
      linkBefore(paramE, node(paramInt));
    }
  }
  
  public E remove(int paramInt)
  {
    checkElementIndex(paramInt);
    return unlink(node(paramInt));
  }
  
  private boolean isElementIndex(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < this.size);
  }
  
  private boolean isPositionIndex(int paramInt)
  {
    return (paramInt >= 0) && (paramInt <= this.size);
  }
  
  private String outOfBoundsMsg(int paramInt)
  {
    return "Index: " + paramInt + ", Size: " + this.size;
  }
  
  private void checkElementIndex(int paramInt)
  {
    if (!isElementIndex(paramInt)) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  private void checkPositionIndex(int paramInt)
  {
    if (!isPositionIndex(paramInt)) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  Node<E> node(int paramInt)
  {
    if (paramInt < this.size >> 1)
    {
      localNode = this.first;
      for (i = 0; i < paramInt; i++) {
        localNode = localNode.next;
      }
      return localNode;
    }
    Node localNode = this.last;
    for (int i = this.size - 1; i > paramInt; i--) {
      localNode = localNode.prev;
    }
    return localNode;
  }
  
  public int indexOf(Object paramObject)
  {
    int i = 0;
    Node localNode;
    if (paramObject == null) {
      for (localNode = this.first; localNode != null; localNode = localNode.next)
      {
        if (localNode.item == null) {
          return i;
        }
        i++;
      }
    } else {
      for (localNode = this.first; localNode != null; localNode = localNode.next)
      {
        if (paramObject.equals(localNode.item)) {
          return i;
        }
        i++;
      }
    }
    return -1;
  }
  
  public int lastIndexOf(Object paramObject)
  {
    int i = this.size;
    Node localNode;
    if (paramObject == null) {
      for (localNode = this.last; localNode != null; localNode = localNode.prev)
      {
        i--;
        if (localNode.item == null) {
          return i;
        }
      }
    } else {
      for (localNode = this.last; localNode != null; localNode = localNode.prev)
      {
        i--;
        if (paramObject.equals(localNode.item)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public E peek()
  {
    Node localNode = this.first;
    return localNode == null ? null : localNode.item;
  }
  
  public E element()
  {
    return getFirst();
  }
  
  public E poll()
  {
    Node localNode = this.first;
    return localNode == null ? null : unlinkFirst(localNode);
  }
  
  public E remove()
  {
    return removeFirst();
  }
  
  public boolean offer(E paramE)
  {
    return add(paramE);
  }
  
  public boolean offerFirst(E paramE)
  {
    addFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE)
  {
    addLast(paramE);
    return true;
  }
  
  public E peekFirst()
  {
    Node localNode = this.first;
    return localNode == null ? null : localNode.item;
  }
  
  public E peekLast()
  {
    Node localNode = this.last;
    return localNode == null ? null : localNode.item;
  }
  
  public E pollFirst()
  {
    Node localNode = this.first;
    return localNode == null ? null : unlinkFirst(localNode);
  }
  
  public E pollLast()
  {
    Node localNode = this.last;
    return localNode == null ? null : unlinkLast(localNode);
  }
  
  public void push(E paramE)
  {
    addFirst(paramE);
  }
  
  public E pop()
  {
    return removeFirst();
  }
  
  public boolean removeFirstOccurrence(Object paramObject)
  {
    return remove(paramObject);
  }
  
  public boolean removeLastOccurrence(Object paramObject)
  {
    Node localNode;
    if (paramObject == null) {
      for (localNode = this.last; localNode != null; localNode = localNode.prev) {
        if (localNode.item == null)
        {
          unlink(localNode);
          return true;
        }
      }
    } else {
      for (localNode = this.last; localNode != null; localNode = localNode.prev) {
        if (paramObject.equals(localNode.item))
        {
          unlink(localNode);
          return true;
        }
      }
    }
    return false;
  }
  
  public ListIterator<E> listIterator(int paramInt)
  {
    checkPositionIndex(paramInt);
    return new ListItr(paramInt);
  }
  
  public Iterator<E> descendingIterator()
  {
    return new DescendingIterator(null);
  }
  
  private LinkedList<E> superClone()
  {
    try
    {
      return (LinkedList)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public Object clone()
  {
    LinkedList localLinkedList = superClone();
    localLinkedList.first = (localLinkedList.last = null);
    localLinkedList.size = 0;
    localLinkedList.modCount = 0;
    for (Node localNode = this.first; localNode != null; localNode = localNode.next) {
      localLinkedList.add(localNode.item);
    }
    return localLinkedList;
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[this.size];
    int i = 0;
    for (Node localNode = this.first; localNode != null; localNode = localNode.next) {
      arrayOfObject[(i++)] = localNode.item;
    }
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    if (paramArrayOfT.length < this.size) {
      paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), this.size);
    }
    int i = 0;
    T[] arrayOfT = paramArrayOfT;
    for (Node localNode = this.first; localNode != null; localNode = localNode.next) {
      arrayOfT[(i++)] = localNode.item;
    }
    if (paramArrayOfT.length > this.size) {
      paramArrayOfT[this.size] = null;
    }
    return paramArrayOfT;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.size);
    for (Node localNode = this.first; localNode != null; localNode = localNode.next) {
      paramObjectOutputStream.writeObject(localNode.item);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    for (int j = 0; j < i; j++) {
      linkLast(paramObjectInputStream.readObject());
    }
  }
  
  public Spliterator<E> spliterator()
  {
    return new LLSpliterator(this, -1, 0);
  }
  
  private class DescendingIterator
    implements Iterator<E>
  {
    private final LinkedList<E>.ListItr itr = new LinkedList.ListItr(LinkedList.this, LinkedList.this.size());
    
    private DescendingIterator() {}
    
    public boolean hasNext()
    {
      return this.itr.hasPrevious();
    }
    
    public E next()
    {
      return this.itr.previous();
    }
    
    public void remove()
    {
      this.itr.remove();
    }
  }
  
  static final class LLSpliterator<E>
    implements Spliterator<E>
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    final LinkedList<E> list;
    LinkedList.Node<E> current;
    int est;
    int expectedModCount;
    int batch;
    
    LLSpliterator(LinkedList<E> paramLinkedList, int paramInt1, int paramInt2)
    {
      this.list = paramLinkedList;
      this.est = paramInt1;
      this.expectedModCount = paramInt2;
    }
    
    final int getEst()
    {
      int i;
      if ((i = this.est) < 0)
      {
        LinkedList localLinkedList;
        if ((localLinkedList = this.list) == null)
        {
          i = this.est = 0;
        }
        else
        {
          this.expectedModCount = localLinkedList.modCount;
          this.current = localLinkedList.first;
          i = this.est = localLinkedList.size;
        }
      }
      return i;
    }
    
    public long estimateSize()
    {
      return getEst();
    }
    
    public Spliterator<E> trySplit()
    {
      int i = getEst();
      LinkedList.Node localNode;
      if ((i > 1) && ((localNode = this.current) != null))
      {
        int j = this.batch + 1024;
        if (j > i) {
          j = i;
        }
        if (j > 33554432) {
          j = 33554432;
        }
        Object[] arrayOfObject = new Object[j];
        int k = 0;
        do
        {
          arrayOfObject[(k++)] = localNode.item;
        } while (((localNode = localNode.next) != null) && (k < j));
        this.current = localNode;
        this.batch = k;
        this.est = (i - k);
        return Spliterators.spliterator(arrayOfObject, 0, k, 16);
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i;
      LinkedList.Node localNode;
      if (((i = getEst()) > 0) && ((localNode = this.current) != null))
      {
        this.current = null;
        this.est = 0;
        do
        {
          Object localObject = localNode.item;
          localNode = localNode.next;
          paramConsumer.accept(localObject);
          if (localNode == null) {
            break;
          }
          i--;
        } while (i > 0);
      }
      if (this.list.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      LinkedList.Node localNode;
      if ((getEst() > 0) && ((localNode = this.current) != null))
      {
        this.est -= 1;
        Object localObject = localNode.item;
        this.current = localNode.next;
        paramConsumer.accept(localObject);
        if (this.list.modCount != this.expectedModCount) {
          throw new ConcurrentModificationException();
        }
        return true;
      }
      return false;
    }
    
    public int characteristics()
    {
      return 16464;
    }
  }
  
  private class ListItr
    implements ListIterator<E>
  {
    private LinkedList.Node<E> lastReturned;
    private LinkedList.Node<E> next;
    private int nextIndex;
    private int expectedModCount = LinkedList.this.modCount;
    
    ListItr(int paramInt)
    {
      this.next = (paramInt == LinkedList.this.size ? null : LinkedList.this.node(paramInt));
      this.nextIndex = paramInt;
    }
    
    public boolean hasNext()
    {
      return this.nextIndex < LinkedList.this.size;
    }
    
    public E next()
    {
      checkForComodification();
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.lastReturned = this.next;
      this.next = this.next.next;
      this.nextIndex += 1;
      return this.lastReturned.item;
    }
    
    public boolean hasPrevious()
    {
      return this.nextIndex > 0;
    }
    
    public E previous()
    {
      checkForComodification();
      if (!hasPrevious()) {
        throw new NoSuchElementException();
      }
      this.lastReturned = (this.next = this.next == null ? LinkedList.this.last : this.next.prev);
      this.nextIndex -= 1;
      return this.lastReturned.item;
    }
    
    public int nextIndex()
    {
      return this.nextIndex;
    }
    
    public int previousIndex()
    {
      return this.nextIndex - 1;
    }
    
    public void remove()
    {
      checkForComodification();
      if (this.lastReturned == null) {
        throw new IllegalStateException();
      }
      LinkedList.Node localNode = this.lastReturned.next;
      LinkedList.this.unlink(this.lastReturned);
      if (this.next == this.lastReturned) {
        this.next = localNode;
      } else {
        this.nextIndex -= 1;
      }
      this.lastReturned = null;
      this.expectedModCount += 1;
    }
    
    public void set(E paramE)
    {
      if (this.lastReturned == null) {
        throw new IllegalStateException();
      }
      checkForComodification();
      this.lastReturned.item = paramE;
    }
    
    public void add(E paramE)
    {
      checkForComodification();
      this.lastReturned = null;
      if (this.next == null) {
        LinkedList.this.linkLast(paramE);
      } else {
        LinkedList.this.linkBefore(paramE, this.next);
      }
      this.nextIndex += 1;
      this.expectedModCount += 1;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      while ((LinkedList.this.modCount == this.expectedModCount) && (this.nextIndex < LinkedList.this.size))
      {
        paramConsumer.accept(this.next.item);
        this.lastReturned = this.next;
        this.next = this.next.next;
        this.nextIndex += 1;
      }
      checkForComodification();
    }
    
    final void checkForComodification()
    {
      if (LinkedList.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  private static class Node<E>
  {
    E item;
    Node<E> next;
    Node<E> prev;
    
    Node(Node<E> paramNode1, E paramE, Node<E> paramNode2)
    {
      this.item = paramE;
      this.next = paramNode2;
      this.prev = paramNode1;
    }
  }
}
