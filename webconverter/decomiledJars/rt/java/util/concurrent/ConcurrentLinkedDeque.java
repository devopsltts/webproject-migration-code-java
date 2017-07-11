package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class ConcurrentLinkedDeque<E>
  extends AbstractCollection<E>
  implements Deque<E>, Serializable
{
  private static final long serialVersionUID = 876323262645176354L;
  private volatile transient Node<E> head;
  private volatile transient Node<E> tail;
  private static final Node<Object> PREV_TERMINATOR = new Node();
  private static final Node<Object> NEXT_TERMINATOR;
  private static final int HOPS = 2;
  private static final Unsafe UNSAFE;
  private static final long headOffset;
  private static final long tailOffset;
  
  Node<E> prevTerminator()
  {
    return PREV_TERMINATOR;
  }
  
  Node<E> nextTerminator()
  {
    return NEXT_TERMINATOR;
  }
  
  private void linkFirst(E paramE)
  {
    checkNotNull(paramE);
    Node localNode1 = new Node(paramE);
    Node localNode2 = this.head;
    Object localObject = localNode2;
    do
    {
      Node localNode3;
      while (((localNode3 = ((Node)localObject).prev) != null) && ((localNode3 = (localObject = localNode3).prev) != null)) {
        localObject = localNode2 != (localNode2 = this.head) ? localNode2 : localNode3;
      }
      if (((Node)localObject).next == localObject) {
        break;
      }
      localNode1.lazySetNext((Node)localObject);
    } while (!((Node)localObject).casPrev(null, localNode1));
    if (localObject != localNode2) {
      casHead(localNode2, localNode1);
    }
  }
  
  private void linkLast(E paramE)
  {
    checkNotNull(paramE);
    Node localNode1 = new Node(paramE);
    Node localNode2 = this.tail;
    Object localObject = localNode2;
    do
    {
      Node localNode3;
      while (((localNode3 = ((Node)localObject).next) != null) && ((localNode3 = (localObject = localNode3).next) != null)) {
        localObject = localNode2 != (localNode2 = this.tail) ? localNode2 : localNode3;
      }
      if (((Node)localObject).prev == localObject) {
        break;
      }
      localNode1.lazySetPrev((Node)localObject);
    } while (!((Node)localObject).casNext(null, localNode1));
    if (localObject != localNode2) {
      casTail(localNode2, localNode1);
    }
  }
  
  void unlink(Node<E> paramNode)
  {
    Node localNode1 = paramNode.prev;
    Node localNode2 = paramNode.next;
    if (localNode1 == null)
    {
      unlinkFirst(paramNode, localNode2);
    }
    else if (localNode2 == null)
    {
      unlinkLast(paramNode, localNode1);
    }
    else
    {
      int k = 1;
      Object localObject3 = localNode1;
      Object localObject1;
      int i;
      Node localNode3;
      for (;;)
      {
        if (((Node)localObject3).item != null)
        {
          localObject1 = localObject3;
          i = 0;
          break;
        }
        localNode3 = ((Node)localObject3).prev;
        if (localNode3 == null)
        {
          if (((Node)localObject3).next == localObject3) {
            return;
          }
          localObject1 = localObject3;
          i = 1;
          break;
        }
        if (localObject3 == localNode3) {
          return;
        }
        localObject3 = localNode3;
        k++;
      }
      localObject3 = localNode2;
      Object localObject2;
      int j;
      for (;;)
      {
        if (((Node)localObject3).item != null)
        {
          localObject2 = localObject3;
          j = 0;
          break;
        }
        localNode3 = ((Node)localObject3).next;
        if (localNode3 == null)
        {
          if (((Node)localObject3).prev == localObject3) {
            return;
          }
          localObject2 = localObject3;
          j = 1;
          break;
        }
        if (localObject3 == localNode3) {
          return;
        }
        localObject3 = localNode3;
        k++;
      }
      if ((k < 2) && ((i | j) != 0)) {
        return;
      }
      skipDeletedSuccessors(localObject1);
      skipDeletedPredecessors(localObject2);
      if (((i | j) != 0) && (localObject1.next == localObject2) && (localObject2.prev == localObject1) && (i != 0 ? localObject1.prev == null : localObject1.item != null) && (j != 0 ? localObject2.next == null : localObject2.item != null))
      {
        updateHead();
        updateTail();
        paramNode.lazySetPrev(i != 0 ? prevTerminator() : paramNode);
        paramNode.lazySetNext(j != 0 ? nextTerminator() : paramNode);
      }
    }
  }
  
  private void unlinkFirst(Node<E> paramNode1, Node<E> paramNode2)
  {
    Object localObject1 = null;
    Node localNode;
    for (Object localObject2 = paramNode2;; localObject2 = localNode)
    {
      if ((((Node)localObject2).item != null) || ((localNode = ((Node)localObject2).next) == null))
      {
        if ((localObject1 != null) && (((Node)localObject2).prev != localObject2) && (paramNode1.casNext(paramNode2, (Node)localObject2)))
        {
          skipDeletedPredecessors((Node)localObject2);
          if ((paramNode1.prev == null) && ((((Node)localObject2).next == null) || (((Node)localObject2).item != null)) && (((Node)localObject2).prev == paramNode1))
          {
            updateHead();
            updateTail();
            ((Node)localObject1).lazySetNext((Node)localObject1);
            ((Node)localObject1).lazySetPrev(prevTerminator());
          }
        }
        return;
      }
      if (localObject2 == localNode) {
        return;
      }
      localObject1 = localObject2;
    }
  }
  
  private void unlinkLast(Node<E> paramNode1, Node<E> paramNode2)
  {
    Object localObject1 = null;
    Node localNode;
    for (Object localObject2 = paramNode2;; localObject2 = localNode)
    {
      if ((((Node)localObject2).item != null) || ((localNode = ((Node)localObject2).prev) == null))
      {
        if ((localObject1 != null) && (((Node)localObject2).next != localObject2) && (paramNode1.casPrev(paramNode2, (Node)localObject2)))
        {
          skipDeletedSuccessors((Node)localObject2);
          if ((paramNode1.next == null) && ((((Node)localObject2).prev == null) || (((Node)localObject2).item != null)) && (((Node)localObject2).next == paramNode1))
          {
            updateHead();
            updateTail();
            ((Node)localObject1).lazySetPrev((Node)localObject1);
            ((Node)localObject1).lazySetNext(nextTerminator());
          }
        }
        return;
      }
      if (localObject2 == localNode) {
        return;
      }
      localObject1 = localObject2;
    }
  }
  
  private final void updateHead()
  {
    Node localNode1;
    Object localObject;
    if (((localNode1 = this.head).item == null) && ((localObject = localNode1.prev) != null)) {
      for (;;)
      {
        Node localNode2;
        if (((localNode2 = ((Node)localObject).prev) == null) || ((localNode2 = (localObject = localNode2).prev) == null))
        {
          if (!casHead(localNode1, (Node)localObject)) {
            break;
          }
          return;
        }
        if (localNode1 != this.head) {
          break;
        }
        localObject = localNode2;
      }
    }
  }
  
  private final void updateTail()
  {
    Node localNode1;
    Object localObject;
    if (((localNode1 = this.tail).item == null) && ((localObject = localNode1.next) != null)) {
      for (;;)
      {
        Node localNode2;
        if (((localNode2 = ((Node)localObject).next) == null) || ((localNode2 = (localObject = localNode2).next) == null))
        {
          if (!casTail(localNode1, (Node)localObject)) {
            break;
          }
          return;
        }
        if (localNode1 != this.tail) {
          break;
        }
        localObject = localNode2;
      }
    }
  }
  
  private void skipDeletedPredecessors(Node<E> paramNode)
  {
    label69:
    do
    {
      Node localNode1 = paramNode.prev;
      Node localNode2;
      for (Object localObject = localNode1; ((Node)localObject).item == null; localObject = localNode2)
      {
        localNode2 = ((Node)localObject).prev;
        if (localNode2 == null)
        {
          if (((Node)localObject).next != localObject) {
            break;
          }
          break label69;
        }
        if (localObject == localNode2) {
          break label69;
        }
      }
      if ((localNode1 == localObject) || (paramNode.casPrev(localNode1, (Node)localObject))) {
        return;
      }
    } while ((paramNode.item != null) || (paramNode.next == null));
  }
  
  private void skipDeletedSuccessors(Node<E> paramNode)
  {
    label69:
    do
    {
      Node localNode1 = paramNode.next;
      Node localNode2;
      for (Object localObject = localNode1; ((Node)localObject).item == null; localObject = localNode2)
      {
        localNode2 = ((Node)localObject).next;
        if (localNode2 == null)
        {
          if (((Node)localObject).prev != localObject) {
            break;
          }
          break label69;
        }
        if (localObject == localNode2) {
          break label69;
        }
      }
      if ((localNode1 == localObject) || (paramNode.casNext(localNode1, (Node)localObject))) {
        return;
      }
    } while ((paramNode.item != null) || (paramNode.prev == null));
  }
  
  final Node<E> succ(Node<E> paramNode)
  {
    Node localNode = paramNode.next;
    return paramNode == localNode ? first() : localNode;
  }
  
  final Node<E> pred(Node<E> paramNode)
  {
    Node localNode = paramNode.prev;
    return paramNode == localNode ? last() : localNode;
  }
  
  Node<E> first()
  {
    Node localNode1;
    Object localObject;
    do
    {
      localNode1 = this.head;
      Node localNode2;
      for (localObject = localNode1; ((localNode2 = ((Node)localObject).prev) != null) && ((localNode2 = (localObject = localNode2).prev) != null); localObject = localNode1 != (localNode1 = this.head) ? localNode1 : localNode2) {}
    } while ((localObject != localNode1) && (!casHead(localNode1, (Node)localObject)));
    return localObject;
  }
  
  Node<E> last()
  {
    Node localNode1;
    Object localObject;
    do
    {
      localNode1 = this.tail;
      Node localNode2;
      for (localObject = localNode1; ((localNode2 = ((Node)localObject).next) != null) && ((localNode2 = (localObject = localNode2).next) != null); localObject = localNode1 != (localNode1 = this.tail) ? localNode1 : localNode2) {}
    } while ((localObject != localNode1) && (!casTail(localNode1, (Node)localObject)));
    return localObject;
  }
  
  private static void checkNotNull(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
  }
  
  private E screenNullResult(E paramE)
  {
    if (paramE == null) {
      throw new NoSuchElementException();
    }
    return paramE;
  }
  
  private ArrayList<E> toArrayList()
  {
    ArrayList localArrayList = new ArrayList();
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = localNode.item;
      if (localObject != null) {
        localArrayList.add(localObject);
      }
    }
    return localArrayList;
  }
  
  public ConcurrentLinkedDeque()
  {
    this.head = (this.tail = new Node(null));
  }
  
  public ConcurrentLinkedDeque(Collection<? extends E> paramCollection)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject3 = localIterator.next();
      checkNotNull(localObject3);
      Node localNode = new Node(localObject3);
      if (localObject1 == null)
      {
        localObject1 = localObject2 = localNode;
      }
      else
      {
        localObject2.lazySetNext(localNode);
        localNode.lazySetPrev(localObject2);
        localObject2 = localNode;
      }
    }
    initHeadTail(localObject1, localObject2);
  }
  
  private void initHeadTail(Node<E> paramNode1, Node<E> paramNode2)
  {
    if (paramNode1 == paramNode2) {
      if (paramNode1 == null)
      {
        paramNode1 = paramNode2 = new Node(null);
      }
      else
      {
        Node localNode = new Node(null);
        paramNode2.lazySetNext(localNode);
        localNode.lazySetPrev(paramNode2);
        paramNode2 = localNode;
      }
    }
    this.head = paramNode1;
    this.tail = paramNode2;
  }
  
  public void addFirst(E paramE)
  {
    linkFirst(paramE);
  }
  
  public void addLast(E paramE)
  {
    linkLast(paramE);
  }
  
  public boolean offerFirst(E paramE)
  {
    linkFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE)
  {
    linkLast(paramE);
    return true;
  }
  
  public E peekFirst()
  {
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = localNode.item;
      if (localObject != null) {
        return localObject;
      }
    }
    return null;
  }
  
  public E peekLast()
  {
    for (Node localNode = last(); localNode != null; localNode = pred(localNode))
    {
      Object localObject = localNode.item;
      if (localObject != null) {
        return localObject;
      }
    }
    return null;
  }
  
  public E getFirst()
  {
    return screenNullResult(peekFirst());
  }
  
  public E getLast()
  {
    return screenNullResult(peekLast());
  }
  
  public E pollFirst()
  {
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = localNode.item;
      if ((localObject != null) && (localNode.casItem(localObject, null)))
      {
        unlink(localNode);
        return localObject;
      }
    }
    return null;
  }
  
  public E pollLast()
  {
    for (Node localNode = last(); localNode != null; localNode = pred(localNode))
    {
      Object localObject = localNode.item;
      if ((localObject != null) && (localNode.casItem(localObject, null)))
      {
        unlink(localNode);
        return localObject;
      }
    }
    return null;
  }
  
  public E removeFirst()
  {
    return screenNullResult(pollFirst());
  }
  
  public E removeLast()
  {
    return screenNullResult(pollLast());
  }
  
  public boolean offer(E paramE)
  {
    return offerLast(paramE);
  }
  
  public boolean add(E paramE)
  {
    return offerLast(paramE);
  }
  
  public E poll()
  {
    return pollFirst();
  }
  
  public E peek()
  {
    return peekFirst();
  }
  
  public E remove()
  {
    return removeFirst();
  }
  
  public E pop()
  {
    return removeFirst();
  }
  
  public E element()
  {
    return getFirst();
  }
  
  public void push(E paramE)
  {
    addFirst(paramE);
  }
  
  public boolean removeFirstOccurrence(Object paramObject)
  {
    checkNotNull(paramObject);
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = localNode.item;
      if ((localObject != null) && (paramObject.equals(localObject)) && (localNode.casItem(localObject, null)))
      {
        unlink(localNode);
        return true;
      }
    }
    return false;
  }
  
  public boolean removeLastOccurrence(Object paramObject)
  {
    checkNotNull(paramObject);
    for (Node localNode = last(); localNode != null; localNode = pred(localNode))
    {
      Object localObject = localNode.item;
      if ((localObject != null) && (paramObject.equals(localObject)) && (localNode.casItem(localObject, null)))
      {
        unlink(localNode);
        return true;
      }
    }
    return false;
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = localNode.item;
      if ((localObject != null) && (paramObject.equals(localObject))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEmpty()
  {
    return peekFirst() == null;
  }
  
  public int size()
  {
    int i = 0;
    for (Node localNode = first(); localNode != null; localNode = succ(localNode)) {
      if (localNode.item != null)
      {
        i++;
        if (i == Integer.MAX_VALUE) {
          break;
        }
      }
    }
    return i;
  }
  
  public boolean remove(Object paramObject)
  {
    return removeFirstOccurrence(paramObject);
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    if (paramCollection == this) {
      throw new IllegalArgumentException();
    }
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = paramCollection.iterator();
    Node localNode;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = ((Iterator)localObject3).next();
      checkNotNull(localObject4);
      localNode = new Node(localObject4);
      if (localObject1 == null)
      {
        localObject1 = localObject2 = localNode;
      }
      else
      {
        localObject2.lazySetNext(localNode);
        localNode.lazySetPrev(localObject2);
        localObject2 = localNode;
      }
    }
    if (localObject1 == null) {
      return false;
    }
    localObject3 = this.tail;
    Object localObject4 = localObject3;
    do
    {
      while (((localNode = localObject4.next) != null) && ((localNode = (localObject4 = localNode).next) != null)) {
        localObject4 = localObject3 != (localObject3 = this.tail) ? localObject3 : localNode;
      }
      if (localObject4.prev == localObject4) {
        break;
      }
      localObject1.lazySetPrev(localObject4);
    } while (!localObject4.casNext(null, localObject1));
    if (!casTail((Node)localObject3, localObject2))
    {
      localObject3 = this.tail;
      if (localObject2.next == null) {
        casTail((Node)localObject3, localObject2);
      }
    }
    return true;
  }
  
  public void clear()
  {
    while (pollFirst() != null) {}
  }
  
  public Object[] toArray()
  {
    return toArrayList().toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    return toArrayList().toArray(paramArrayOfT);
  }
  
  public Iterator<E> iterator()
  {
    return new Itr(null);
  }
  
  public Iterator<E> descendingIterator()
  {
    return new DescendingItr(null);
  }
  
  public Spliterator<E> spliterator()
  {
    return new CLDSpliterator(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = localNode.item;
      if (localObject != null) {
        paramObjectOutputStream.writeObject(localObject);
      }
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3;
    while ((localObject3 = paramObjectInputStream.readObject()) != null)
    {
      Node localNode = new Node(localObject3);
      if (localObject1 == null)
      {
        localObject1 = localObject2 = localNode;
      }
      else
      {
        localObject2.lazySetNext(localNode);
        localNode.lazySetPrev(localObject2);
        localObject2 = localNode;
      }
    }
    initHeadTail(localObject1, localObject2);
  }
  
  private boolean casHead(Node<E> paramNode1, Node<E> paramNode2)
  {
    return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2);
  }
  
  private boolean casTail(Node<E> paramNode1, Node<E> paramNode2)
  {
    return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2);
  }
  
  static
  {
    PREV_TERMINATOR.next = PREV_TERMINATOR;
    NEXT_TERMINATOR = new Node();
    NEXT_TERMINATOR.prev = NEXT_TERMINATOR;
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      ConcurrentLinkedDeque localConcurrentLinkedDeque = ConcurrentLinkedDeque.class;
      headOffset = UNSAFE.objectFieldOffset(localConcurrentLinkedDeque.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(localConcurrentLinkedDeque.getDeclaredField("tail"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  private abstract class AbstractItr
    implements Iterator<E>
  {
    private ConcurrentLinkedDeque.Node<E> nextNode;
    private E nextItem;
    private ConcurrentLinkedDeque.Node<E> lastRet;
    
    abstract ConcurrentLinkedDeque.Node<E> startNode();
    
    abstract ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> paramNode);
    
    AbstractItr()
    {
      advance();
    }
    
    private void advance()
    {
      this.lastRet = this.nextNode;
      for (ConcurrentLinkedDeque.Node localNode = this.nextNode == null ? startNode() : nextNode(this.nextNode);; localNode = nextNode(localNode))
      {
        if (localNode == null)
        {
          this.nextNode = null;
          this.nextItem = null;
          break;
        }
        Object localObject = localNode.item;
        if (localObject != null)
        {
          this.nextNode = localNode;
          this.nextItem = localObject;
          break;
        }
      }
    }
    
    public boolean hasNext()
    {
      return this.nextItem != null;
    }
    
    public E next()
    {
      Object localObject = this.nextItem;
      if (localObject == null) {
        throw new NoSuchElementException();
      }
      advance();
      return localObject;
    }
    
    public void remove()
    {
      ConcurrentLinkedDeque.Node localNode = this.lastRet;
      if (localNode == null) {
        throw new IllegalStateException();
      }
      localNode.item = null;
      ConcurrentLinkedDeque.this.unlink(localNode);
      this.lastRet = null;
    }
  }
  
  static final class CLDSpliterator<E>
    implements Spliterator<E>
  {
    static final int MAX_BATCH = 33554432;
    final ConcurrentLinkedDeque<E> queue;
    ConcurrentLinkedDeque.Node<E> current;
    int batch;
    boolean exhausted;
    
    CLDSpliterator(ConcurrentLinkedDeque<E> paramConcurrentLinkedDeque)
    {
      this.queue = paramConcurrentLinkedDeque;
    }
    
    public Spliterator<E> trySplit()
    {
      ConcurrentLinkedDeque localConcurrentLinkedDeque = this.queue;
      int i = this.batch;
      int j = i >= 33554432 ? 33554432 : i <= 0 ? 1 : i + 1;
      ConcurrentLinkedDeque.Node localNode;
      if ((!this.exhausted) && (((localNode = this.current) != null) || ((localNode = localConcurrentLinkedDeque.first()) != null)))
      {
        if ((localNode.item == null) && (localNode == (localNode = localNode.next))) {
          this.current = (localNode = localConcurrentLinkedDeque.first());
        }
        if ((localNode != null) && (localNode.next != null))
        {
          Object[] arrayOfObject = new Object[j];
          int k = 0;
          do
          {
            if ((arrayOfObject[k] =  = localNode.item) != null) {
              k++;
            }
            if (localNode == (localNode = localNode.next)) {
              localNode = localConcurrentLinkedDeque.first();
            }
          } while ((localNode != null) && (k < j));
          if ((this.current = localNode) == null) {
            this.exhausted = true;
          }
          if (k > 0)
          {
            this.batch = k;
            return Spliterators.spliterator(arrayOfObject, 0, k, 4368);
          }
        }
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentLinkedDeque localConcurrentLinkedDeque = this.queue;
      ConcurrentLinkedDeque.Node localNode;
      if ((!this.exhausted) && (((localNode = this.current) != null) || ((localNode = localConcurrentLinkedDeque.first()) != null)))
      {
        this.exhausted = true;
        do
        {
          Object localObject = localNode.item;
          if (localNode == (localNode = localNode.next)) {
            localNode = localConcurrentLinkedDeque.first();
          }
          if (localObject != null) {
            paramConsumer.accept(localObject);
          }
        } while (localNode != null);
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentLinkedDeque localConcurrentLinkedDeque = this.queue;
      ConcurrentLinkedDeque.Node localNode;
      if ((!this.exhausted) && (((localNode = this.current) != null) || ((localNode = localConcurrentLinkedDeque.first()) != null)))
      {
        Object localObject;
        do
        {
          localObject = localNode.item;
          if (localNode == (localNode = localNode.next)) {
            localNode = localConcurrentLinkedDeque.first();
          }
        } while ((localObject == null) && (localNode != null));
        if ((this.current = localNode) == null) {
          this.exhausted = true;
        }
        if (localObject != null)
        {
          paramConsumer.accept(localObject);
          return true;
        }
      }
      return false;
    }
    
    public long estimateSize()
    {
      return Long.MAX_VALUE;
    }
    
    public int characteristics()
    {
      return 4368;
    }
  }
  
  private class DescendingItr
    extends ConcurrentLinkedDeque<E>.AbstractItr
  {
    private DescendingItr()
    {
      super();
    }
    
    ConcurrentLinkedDeque.Node<E> startNode()
    {
      return ConcurrentLinkedDeque.this.last();
    }
    
    ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> paramNode)
    {
      return ConcurrentLinkedDeque.this.pred(paramNode);
    }
  }
  
  private class Itr
    extends ConcurrentLinkedDeque<E>.AbstractItr
  {
    private Itr()
    {
      super();
    }
    
    ConcurrentLinkedDeque.Node<E> startNode()
    {
      return ConcurrentLinkedDeque.this.first();
    }
    
    ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> paramNode)
    {
      return ConcurrentLinkedDeque.this.succ(paramNode);
    }
  }
  
  static final class Node<E>
  {
    volatile Node<E> prev;
    volatile E item;
    volatile Node<E> next;
    private static final Unsafe UNSAFE;
    private static final long prevOffset;
    private static final long itemOffset;
    private static final long nextOffset;
    
    Node() {}
    
    Node(E paramE)
    {
      UNSAFE.putObject(this, itemOffset, paramE);
    }
    
    boolean casItem(E paramE1, E paramE2)
    {
      return UNSAFE.compareAndSwapObject(this, itemOffset, paramE1, paramE2);
    }
    
    void lazySetNext(Node<E> paramNode)
    {
      UNSAFE.putOrderedObject(this, nextOffset, paramNode);
    }
    
    boolean casNext(Node<E> paramNode1, Node<E> paramNode2)
    {
      return UNSAFE.compareAndSwapObject(this, nextOffset, paramNode1, paramNode2);
    }
    
    void lazySetPrev(Node<E> paramNode)
    {
      UNSAFE.putOrderedObject(this, prevOffset, paramNode);
    }
    
    boolean casPrev(Node<E> paramNode1, Node<E> paramNode2)
    {
      return UNSAFE.compareAndSwapObject(this, prevOffset, paramNode1, paramNode2);
    }
    
    static
    {
      try
      {
        UNSAFE = Unsafe.getUnsafe();
        Node localNode = Node.class;
        prevOffset = UNSAFE.objectFieldOffset(localNode.getDeclaredField("prev"));
        itemOffset = UNSAFE.objectFieldOffset(localNode.getDeclaredField("item"));
        nextOffset = UNSAFE.objectFieldOffset(localNode.getDeclaredField("next"));
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
    }
  }
}
