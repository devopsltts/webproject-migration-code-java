package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LinkedBlockingQueue<E>
  extends AbstractQueue<E>
  implements BlockingQueue<E>, Serializable
{
  private static final long serialVersionUID = -6903933977591709194L;
  private final int capacity;
  private final AtomicInteger count = new AtomicInteger();
  transient Node<E> head;
  private transient Node<E> last;
  private final ReentrantLock takeLock = new ReentrantLock();
  private final Condition notEmpty = this.takeLock.newCondition();
  private final ReentrantLock putLock = new ReentrantLock();
  private final Condition notFull = this.putLock.newCondition();
  
  private void signalNotEmpty()
  {
    ReentrantLock localReentrantLock = this.takeLock;
    localReentrantLock.lock();
    try
    {
      this.notEmpty.signal();
      localReentrantLock.unlock();
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private void signalNotFull()
  {
    ReentrantLock localReentrantLock = this.putLock;
    localReentrantLock.lock();
    try
    {
      this.notFull.signal();
      localReentrantLock.unlock();
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private void enqueue(Node<E> paramNode)
  {
    this.last = (this.last.next = paramNode);
  }
  
  private E dequeue()
  {
    Node localNode1 = this.head;
    Node localNode2 = localNode1.next;
    localNode1.next = localNode1;
    this.head = localNode2;
    Object localObject = localNode2.item;
    localNode2.item = null;
    return localObject;
  }
  
  void fullyLock()
  {
    this.putLock.lock();
    this.takeLock.lock();
  }
  
  void fullyUnlock()
  {
    this.takeLock.unlock();
    this.putLock.unlock();
  }
  
  public LinkedBlockingQueue()
  {
    this(Integer.MAX_VALUE);
  }
  
  public LinkedBlockingQueue(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    this.capacity = paramInt;
    this.last = (this.head = new Node(null));
  }
  
  public LinkedBlockingQueue(Collection<? extends E> paramCollection)
  {
    this(Integer.MAX_VALUE);
    ReentrantLock localReentrantLock = this.putLock;
    localReentrantLock.lock();
    try
    {
      int i = 0;
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = localIterator.next();
        if (localObject1 == null) {
          throw new NullPointerException();
        }
        if (i == this.capacity) {
          throw new IllegalStateException("Queue full");
        }
        enqueue(new Node(localObject1));
        i++;
      }
      this.count.set(i);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int size()
  {
    return this.count.get();
  }
  
  public int remainingCapacity()
  {
    return this.capacity - this.count.get();
  }
  
  public void put(E paramE)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    int i = -1;
    Node localNode = new Node(paramE);
    ReentrantLock localReentrantLock = this.putLock;
    AtomicInteger localAtomicInteger = this.count;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (localAtomicInteger.get() == this.capacity) {
        this.notFull.await();
      }
      enqueue(localNode);
      i = localAtomicInteger.getAndIncrement();
      if (i + 1 < this.capacity) {
        this.notFull.signal();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == 0) {
      signalNotEmpty();
    }
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    long l = paramTimeUnit.toNanos(paramLong);
    int i = -1;
    ReentrantLock localReentrantLock = this.putLock;
    AtomicInteger localAtomicInteger = this.count;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (localAtomicInteger.get() == this.capacity)
      {
        if (l <= 0L)
        {
          boolean bool = false;
          return bool;
        }
        l = this.notFull.awaitNanos(l);
      }
      enqueue(new Node(paramE));
      i = localAtomicInteger.getAndIncrement();
      if (i + 1 < this.capacity) {
        this.notFull.signal();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == 0) {
      signalNotEmpty();
    }
    return true;
  }
  
  public boolean offer(E paramE)
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    AtomicInteger localAtomicInteger = this.count;
    if (localAtomicInteger.get() == this.capacity) {
      return false;
    }
    int i = -1;
    Node localNode = new Node(paramE);
    ReentrantLock localReentrantLock = this.putLock;
    localReentrantLock.lock();
    try
    {
      if (localAtomicInteger.get() < this.capacity)
      {
        enqueue(localNode);
        i = localAtomicInteger.getAndIncrement();
        if (i + 1 < this.capacity) {
          this.notFull.signal();
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == 0) {
      signalNotEmpty();
    }
    return i >= 0;
  }
  
  public E take()
    throws InterruptedException
  {
    int i = -1;
    AtomicInteger localAtomicInteger = this.count;
    ReentrantLock localReentrantLock = this.takeLock;
    localReentrantLock.lockInterruptibly();
    Object localObject1;
    try
    {
      while (localAtomicInteger.get() == 0) {
        this.notEmpty.await();
      }
      localObject1 = dequeue();
      i = localAtomicInteger.getAndDecrement();
      if (i > 1) {
        this.notEmpty.signal();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == this.capacity) {
      signalNotFull();
    }
    return localObject1;
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    Object localObject1 = null;
    int i = -1;
    long l = paramTimeUnit.toNanos(paramLong);
    AtomicInteger localAtomicInteger = this.count;
    ReentrantLock localReentrantLock = this.takeLock;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (localAtomicInteger.get() == 0)
      {
        if (l <= 0L)
        {
          E ? = null;
          return ?;
        }
        l = this.notEmpty.awaitNanos(l);
      }
      localObject1 = dequeue();
      i = localAtomicInteger.getAndDecrement();
      if (i > 1) {
        this.notEmpty.signal();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == this.capacity) {
      signalNotFull();
    }
    return localObject1;
  }
  
  public E poll()
  {
    AtomicInteger localAtomicInteger = this.count;
    if (localAtomicInteger.get() == 0) {
      return null;
    }
    Object localObject1 = null;
    int i = -1;
    ReentrantLock localReentrantLock = this.takeLock;
    localReentrantLock.lock();
    try
    {
      if (localAtomicInteger.get() > 0)
      {
        localObject1 = dequeue();
        i = localAtomicInteger.getAndDecrement();
        if (i > 1) {
          this.notEmpty.signal();
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == this.capacity) {
      signalNotFull();
    }
    return localObject1;
  }
  
  public E peek()
  {
    if (this.count.get() == 0) {
      return null;
    }
    ReentrantLock localReentrantLock = this.takeLock;
    localReentrantLock.lock();
    try
    {
      Node localNode = this.head.next;
      if (localNode == null)
      {
        localObject1 = null;
        return localObject1;
      }
      Object localObject1 = localNode.item;
      return localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  void unlink(Node<E> paramNode1, Node<E> paramNode2)
  {
    paramNode1.item = null;
    paramNode2.next = paramNode1.next;
    if (this.last == paramNode1) {
      this.last = paramNode2;
    }
    if (this.count.getAndDecrement() == this.capacity) {
      this.notFull.signal();
    }
  }
  
  public boolean remove(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    fullyLock();
    try
    {
      Object localObject1 = this.head;
      for (Node localNode = ((Node)localObject1).next; localNode != null; localNode = localNode.next)
      {
        if (paramObject.equals(localNode.item))
        {
          unlink(localNode, (Node)localObject1);
          boolean bool2 = true;
          return bool2;
        }
        localObject1 = localNode;
      }
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    fullyLock();
    try
    {
      for (Node localNode = this.head.next; localNode != null; localNode = localNode.next) {
        if (paramObject.equals(localNode.item))
        {
          boolean bool2 = true;
          return bool2;
        }
      }
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  public Object[] toArray()
  {
    fullyLock();
    try
    {
      int i = this.count.get();
      Object[] arrayOfObject = new Object[i];
      int j = 0;
      for (Object localObject1 = this.head.next; localObject1 != null; localObject1 = ((Node)localObject1).next) {
        arrayOfObject[(j++)] = ((Node)localObject1).item;
      }
      localObject1 = arrayOfObject;
      return localObject1;
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    fullyLock();
    try
    {
      int i = this.count.get();
      if (paramArrayOfT.length < i) {
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
      }
      int j = 0;
      for (Object localObject1 = this.head.next; localObject1 != null; localObject1 = ((Node)localObject1).next) {
        paramArrayOfT[(j++)] = ((Node)localObject1).item;
      }
      if (paramArrayOfT.length > j) {
        paramArrayOfT[j] = null;
      }
      localObject1 = paramArrayOfT;
      return localObject1;
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  /* Error */
  public String toString()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 266	java/util/concurrent/LinkedBlockingQueue:fullyLock	()V
    //   4: aload_0
    //   5: getfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   8: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   11: astore_1
    //   12: aload_1
    //   13: ifnonnull +12 -> 25
    //   16: ldc 4
    //   18: astore_2
    //   19: aload_0
    //   20: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   23: aload_2
    //   24: areturn
    //   25: new 137	java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   32: astore_2
    //   33: aload_2
    //   34: bipush 91
    //   36: invokevirtual 262	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   39: pop
    //   40: aload_1
    //   41: getfield 247	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
    //   44: astore_3
    //   45: aload_2
    //   46: aload_3
    //   47: aload_0
    //   48: if_acmpne +8 -> 56
    //   51: ldc 2
    //   53: goto +4 -> 57
    //   56: aload_3
    //   57: invokevirtual 263	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   60: pop
    //   61: aload_1
    //   62: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   65: astore_1
    //   66: aload_1
    //   67: ifnonnull +21 -> 88
    //   70: aload_2
    //   71: bipush 93
    //   73: invokevirtual 262	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   76: invokevirtual 261	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   79: astore 4
    //   81: aload_0
    //   82: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   85: aload 4
    //   87: areturn
    //   88: aload_2
    //   89: bipush 44
    //   91: invokevirtual 262	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   94: bipush 32
    //   96: invokevirtual 262	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   99: pop
    //   100: goto -60 -> 40
    //   103: astore 5
    //   105: aload_0
    //   106: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   109: aload 5
    //   111: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	112	0	this	LinkedBlockingQueue
    //   11	56	1	localNode	Node
    //   18	71	2	localObject1	Object
    //   44	13	3	localObject2	Object
    //   79	7	4	str	String
    //   103	7	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   4	19	103	finally
    //   25	81	103	finally
    //   88	105	103	finally
  }
  
  public void clear()
  {
    fullyLock();
    try
    {
      Node localNode;
      for (Object localObject1 = this.head; (localNode = ((Node)localObject1).next) != null; localObject1 = localNode)
      {
        ((Node)localObject1).next = ((Node)localObject1);
        localNode.item = null;
      }
      this.head = this.last;
      if (this.count.getAndSet(0) == this.capacity) {
        this.notFull.signal();
      }
      fullyUnlock();
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  public int drainTo(Collection<? super E> paramCollection)
  {
    return drainTo(paramCollection, Integer.MAX_VALUE);
  }
  
  /* Error */
  public int drainTo(Collection<? super E> paramCollection, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +11 -> 12
    //   4: new 135	java/lang/NullPointerException
    //   7: dup
    //   8: invokespecial 257	java/lang/NullPointerException:<init>	()V
    //   11: athrow
    //   12: aload_1
    //   13: aload_0
    //   14: if_acmpne +11 -> 25
    //   17: new 131	java/lang/IllegalArgumentException
    //   20: dup
    //   21: invokespecial 254	java/lang/IllegalArgumentException:<init>	()V
    //   24: athrow
    //   25: iload_2
    //   26: ifgt +5 -> 31
    //   29: iconst_0
    //   30: ireturn
    //   31: iconst_0
    //   32: istore_3
    //   33: aload_0
    //   34: getfield 246	java/util/concurrent/LinkedBlockingQueue:takeLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   37: astore 4
    //   39: aload 4
    //   41: invokevirtual 288	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   44: iload_2
    //   45: aload_0
    //   46: getfield 242	java/util/concurrent/LinkedBlockingQueue:count	Ljava/util/concurrent/atomic/AtomicInteger;
    //   49: invokevirtual 280	java/util/concurrent/atomic/AtomicInteger:get	()I
    //   52: invokestatic 256	java/lang/Math:min	(II)I
    //   55: istore 5
    //   57: aload_0
    //   58: getfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   61: astore 6
    //   63: iconst_0
    //   64: istore 7
    //   66: iload 7
    //   68: iload 5
    //   70: if_icmpge +45 -> 115
    //   73: aload 6
    //   75: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   78: astore 8
    //   80: aload_1
    //   81: aload 8
    //   83: getfield 247	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
    //   86: invokeinterface 292 2 0
    //   91: pop
    //   92: aload 8
    //   94: aconst_null
    //   95: putfield 247	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
    //   98: aload 6
    //   100: aload 6
    //   102: putfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   105: aload 8
    //   107: astore 6
    //   109: iinc 7 1
    //   112: goto -46 -> 66
    //   115: iload 5
    //   117: istore 8
    //   119: iload 7
    //   121: ifle +32 -> 153
    //   124: aload_0
    //   125: aload 6
    //   127: putfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   130: aload_0
    //   131: getfield 242	java/util/concurrent/LinkedBlockingQueue:count	Ljava/util/concurrent/atomic/AtomicInteger;
    //   134: iload 7
    //   136: ineg
    //   137: invokevirtual 284	java/util/concurrent/atomic/AtomicInteger:getAndAdd	(I)I
    //   140: aload_0
    //   141: getfield 239	java/util/concurrent/LinkedBlockingQueue:capacity	I
    //   144: if_icmpne +7 -> 151
    //   147: iconst_1
    //   148: goto +4 -> 152
    //   151: iconst_0
    //   152: istore_3
    //   153: aload 4
    //   155: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   158: iload_3
    //   159: ifeq +7 -> 166
    //   162: aload_0
    //   163: invokespecial 269	java/util/concurrent/LinkedBlockingQueue:signalNotFull	()V
    //   166: iload 8
    //   168: ireturn
    //   169: astore 9
    //   171: iload 7
    //   173: ifle +32 -> 205
    //   176: aload_0
    //   177: aload 6
    //   179: putfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   182: aload_0
    //   183: getfield 242	java/util/concurrent/LinkedBlockingQueue:count	Ljava/util/concurrent/atomic/AtomicInteger;
    //   186: iload 7
    //   188: ineg
    //   189: invokevirtual 284	java/util/concurrent/atomic/AtomicInteger:getAndAdd	(I)I
    //   192: aload_0
    //   193: getfield 239	java/util/concurrent/LinkedBlockingQueue:capacity	I
    //   196: if_icmpne +7 -> 203
    //   199: iconst_1
    //   200: goto +4 -> 204
    //   203: iconst_0
    //   204: istore_3
    //   205: aload 9
    //   207: athrow
    //   208: astore 10
    //   210: aload 4
    //   212: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   215: iload_3
    //   216: ifeq +7 -> 223
    //   219: aload_0
    //   220: invokespecial 269	java/util/concurrent/LinkedBlockingQueue:signalNotFull	()V
    //   223: aload 10
    //   225: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	226	0	this	LinkedBlockingQueue
    //   0	226	1	paramCollection	Collection<? super E>
    //   0	226	2	paramInt	int
    //   32	184	3	i	int
    //   37	174	4	localReentrantLock	ReentrantLock
    //   55	61	5	localNode1	Node
    //   61	117	6	localObject1	Object
    //   64	123	7	j	int
    //   78	89	8	localNode2	Node
    //   169	37	9	localObject2	Object
    //   208	16	10	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   66	119	169	finally
    //   169	171	169	finally
    //   44	153	208	finally
    //   169	210	208	finally
  }
  
  public Iterator<E> iterator()
  {
    return new Itr();
  }
  
  public Spliterator<E> spliterator()
  {
    return new LBQSpliterator(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    fullyLock();
    try
    {
      paramObjectOutputStream.defaultWriteObject();
      for (Node localNode = this.head.next; localNode != null; localNode = localNode.next) {
        paramObjectOutputStream.writeObject(localNode.item);
      }
      paramObjectOutputStream.writeObject(null);
      fullyUnlock();
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.count.set(0);
    this.last = (this.head = new Node(null));
    for (;;)
    {
      Object localObject = paramObjectInputStream.readObject();
      if (localObject == null) {
        break;
      }
      add(localObject);
    }
  }
  
  private class Itr
    implements Iterator<E>
  {
    private LinkedBlockingQueue.Node<E> current;
    private LinkedBlockingQueue.Node<E> lastRet;
    private E currentElement;
    
    Itr()
    {
      LinkedBlockingQueue.this.fullyLock();
      try
      {
        this.current = LinkedBlockingQueue.this.head.next;
        if (this.current != null) {
          this.currentElement = this.current.item;
        }
        LinkedBlockingQueue.this.fullyUnlock();
      }
      finally
      {
        LinkedBlockingQueue.this.fullyUnlock();
      }
    }
    
    public boolean hasNext()
    {
      return this.current != null;
    }
    
    private LinkedBlockingQueue.Node<E> nextNode(LinkedBlockingQueue.Node<E> paramNode)
    {
      for (;;)
      {
        LinkedBlockingQueue.Node localNode = paramNode.next;
        if (localNode == paramNode) {
          return LinkedBlockingQueue.this.head.next;
        }
        if ((localNode == null) || (localNode.item != null)) {
          return localNode;
        }
        paramNode = localNode;
      }
    }
    
    public E next()
    {
      LinkedBlockingQueue.this.fullyLock();
      try
      {
        if (this.current == null) {
          throw new NoSuchElementException();
        }
        Object localObject1 = this.currentElement;
        this.lastRet = this.current;
        this.current = nextNode(this.current);
        this.currentElement = (this.current == null ? null : this.current.item);
        Object localObject2 = localObject1;
        return localObject2;
      }
      finally
      {
        LinkedBlockingQueue.this.fullyUnlock();
      }
    }
    
    public void remove()
    {
      if (this.lastRet == null) {
        throw new IllegalStateException();
      }
      LinkedBlockingQueue.this.fullyLock();
      try
      {
        LinkedBlockingQueue.Node localNode1 = this.lastRet;
        this.lastRet = null;
        Object localObject1 = LinkedBlockingQueue.this.head;
        for (LinkedBlockingQueue.Node localNode2 = ((LinkedBlockingQueue.Node)localObject1).next; localNode2 != null; localNode2 = localNode2.next)
        {
          if (localNode2 == localNode1)
          {
            LinkedBlockingQueue.this.unlink(localNode2, (LinkedBlockingQueue.Node)localObject1);
            break;
          }
          localObject1 = localNode2;
        }
      }
      finally
      {
        LinkedBlockingQueue.this.fullyUnlock();
      }
    }
  }
  
  static final class LBQSpliterator<E>
    implements Spliterator<E>
  {
    static final int MAX_BATCH = 33554432;
    final LinkedBlockingQueue<E> queue;
    LinkedBlockingQueue.Node<E> current;
    int batch;
    boolean exhausted;
    long est;
    
    LBQSpliterator(LinkedBlockingQueue<E> paramLinkedBlockingQueue)
    {
      this.queue = paramLinkedBlockingQueue;
      this.est = paramLinkedBlockingQueue.size();
    }
    
    public long estimateSize()
    {
      return this.est;
    }
    
    public Spliterator<E> trySplit()
    {
      LinkedBlockingQueue localLinkedBlockingQueue = this.queue;
      int i = this.batch;
      int j = i >= 33554432 ? 33554432 : i <= 0 ? 1 : i + 1;
      LinkedBlockingQueue.Node localNode1;
      if ((!this.exhausted) && (((localNode1 = this.current) != null) || ((localNode1 = localLinkedBlockingQueue.head.next) != null)) && (localNode1.next != null))
      {
        Object[] arrayOfObject = new Object[j];
        int k = 0;
        LinkedBlockingQueue.Node localNode2 = this.current;
        localLinkedBlockingQueue.fullyLock();
        try
        {
          if ((localNode2 != null) || ((localNode2 = localLinkedBlockingQueue.head.next) != null)) {
            do
            {
              if ((arrayOfObject[k] =  = localNode2.item) != null) {
                k++;
              }
              if ((localNode2 = localNode2.next) == null) {
                break;
              }
            } while (k < j);
          }
        }
        finally
        {
          localLinkedBlockingQueue.fullyUnlock();
        }
        if ((this.current = localNode2) == null)
        {
          this.est = 0L;
          this.exhausted = true;
        }
        else if (this.est -= k < 0L)
        {
          this.est = 0L;
        }
        if (k > 0)
        {
          this.batch = k;
          return Spliterators.spliterator(arrayOfObject, 0, k, 4368);
        }
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      LinkedBlockingQueue localLinkedBlockingQueue = this.queue;
      if (!this.exhausted)
      {
        this.exhausted = true;
        LinkedBlockingQueue.Node localNode = this.current;
        do
        {
          Object localObject1 = null;
          localLinkedBlockingQueue.fullyLock();
          try
          {
            if (localNode == null) {
              localNode = localLinkedBlockingQueue.head.next;
            }
            while (localNode != null)
            {
              localObject1 = localNode.item;
              localNode = localNode.next;
              if (localObject1 != null) {
                break;
              }
            }
          }
          finally
          {
            localLinkedBlockingQueue.fullyUnlock();
          }
          if (localObject1 != null) {
            paramConsumer.accept(localObject1);
          }
        } while (localNode != null);
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      LinkedBlockingQueue localLinkedBlockingQueue = this.queue;
      if (!this.exhausted)
      {
        Object localObject1 = null;
        localLinkedBlockingQueue.fullyLock();
        try
        {
          if (this.current == null) {
            this.current = localLinkedBlockingQueue.head.next;
          }
          while (this.current != null)
          {
            localObject1 = this.current.item;
            this.current = this.current.next;
            if (localObject1 != null) {
              break;
            }
          }
        }
        finally
        {
          localLinkedBlockingQueue.fullyUnlock();
        }
        if (this.current == null) {
          this.exhausted = true;
        }
        if (localObject1 != null)
        {
          paramConsumer.accept(localObject1);
          return true;
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return 4368;
    }
  }
  
  static class Node<E>
  {
    E item;
    Node<E> next;
    
    Node(E paramE)
    {
      this.item = paramE;
    }
  }
}
