package java.util.concurrent;

import sun.misc.Contended;
import sun.misc.Unsafe;

public class Exchanger<V>
{
  private static final int ASHIFT = 7;
  private static final int MMASK = 255;
  private static final int SEQ = 256;
  private static final int NCPU = Runtime.getRuntime().availableProcessors();
  static final int FULL = NCPU >= 510 ? 255 : NCPU >>> 1;
  private static final int SPINS = 1024;
  private static final Object NULL_ITEM = new Object();
  private static final Object TIMED_OUT = new Object();
  private final Participant participant = new Participant();
  private volatile Node[] arena;
  private volatile Node slot;
  private volatile int bound;
  private static final Unsafe U;
  private static final long BOUND;
  private static final long SLOT;
  private static final long MATCH;
  private static final long BLOCKER;
  private static final int ABASE;
  
  private final Object arenaExchange(Object paramObject, boolean paramBoolean, long paramLong)
  {
    Node[] arrayOfNode = this.arena;
    Node localNode1 = (Node)this.participant.get();
    int i = localNode1.index;
    for (;;)
    {
      long l1;
      Node localNode2 = (Node)U.getObjectVolatile(arrayOfNode, l1 = (i << 7) + ABASE);
      if ((localNode2 != null) && (U.compareAndSwapObject(arrayOfNode, l1, localNode2, null)))
      {
        Object localObject1 = localNode2.item;
        localNode2.match = paramObject;
        Thread localThread1 = localNode2.parked;
        if (localThread1 != null) {
          U.unpark(localThread1);
        }
        return localObject1;
      }
      int j;
      int k;
      if ((i <= (k = (j = this.bound) & 0xFF)) && (localNode2 == null))
      {
        localNode1.item = paramObject;
        if (U.compareAndSwapObject(arrayOfNode, l1, null, localNode1))
        {
          long l2 = (paramBoolean) && (k == 0) ? System.nanoTime() + paramLong : 0L;
          Thread localThread2 = Thread.currentThread();
          int n = localNode1.hash;
          int i1 = 1024;
          for (;;)
          {
            Object localObject2 = localNode1.match;
            if (localObject2 != null)
            {
              U.putOrderedObject(localNode1, MATCH, null);
              localNode1.item = null;
              localNode1.hash = n;
              return localObject2;
            }
            if (i1 > 0)
            {
              n ^= n << 1;
              n ^= n >>> 3;
              n ^= n << 10;
              if (n == 0)
              {
                n = 0x400 | (int)localThread2.getId();
              }
              else if (n < 0)
              {
                i1--;
                if ((i1 & 0x1FF) == 0) {
                  Thread.yield();
                }
              }
            }
            else if (U.getObjectVolatile(arrayOfNode, l1) != localNode1)
            {
              i1 = 1024;
            }
            else if ((!localThread2.isInterrupted()) && (k == 0) && ((!paramBoolean) || ((paramLong = l2 - System.nanoTime()) > 0L)))
            {
              U.putObject(localThread2, BLOCKER, this);
              localNode1.parked = localThread2;
              if (U.getObjectVolatile(arrayOfNode, l1) == localNode1) {
                U.park(false, paramLong);
              }
              localNode1.parked = null;
              U.putObject(localThread2, BLOCKER, null);
            }
            else if ((U.getObjectVolatile(arrayOfNode, l1) == localNode1) && (U.compareAndSwapObject(arrayOfNode, l1, localNode1, null)))
            {
              if (k != 0) {
                U.compareAndSwapInt(this, BOUND, j, j + 256 - 1);
              }
              localNode1.item = null;
              localNode1.hash = n;
              i = localNode1.index >>>= 1;
              if (Thread.interrupted()) {
                return null;
              }
              if ((!paramBoolean) || (k != 0) || (paramLong > 0L)) {
                break;
              }
              return TIMED_OUT;
            }
          }
        }
        else
        {
          localNode1.item = null;
        }
      }
      else
      {
        if (localNode1.bound != j)
        {
          localNode1.bound = j;
          localNode1.collides = 0;
          i = (i != k) || (k == 0) ? k : k - 1;
        }
        else
        {
          int m;
          if (((m = localNode1.collides) < k) || (k == FULL) || (!U.compareAndSwapInt(this, BOUND, j, j + 256 + 1)))
          {
            localNode1.collides = (m + 1);
            i = i == 0 ? k : i - 1;
          }
          else
          {
            i = k + 1;
          }
        }
        localNode1.index = i;
      }
    }
  }
  
  private final Object slotExchange(Object paramObject, boolean paramBoolean, long paramLong)
  {
    Node localNode1 = (Node)this.participant.get();
    Thread localThread1 = Thread.currentThread();
    if (localThread1.isInterrupted()) {
      return null;
    }
    for (;;)
    {
      Node localNode2;
      if ((localNode2 = this.slot) != null)
      {
        if (U.compareAndSwapObject(this, SLOT, localNode2, null))
        {
          Object localObject1 = localNode2.item;
          localNode2.match = paramObject;
          Thread localThread2 = localNode2.parked;
          if (localThread2 != null) {
            U.unpark(localThread2);
          }
          return localObject1;
        }
        if ((NCPU > 1) && (this.bound == 0) && (U.compareAndSwapInt(this, BOUND, 0, 256))) {
          this.arena = new Node[FULL + 2 << 7];
        }
      }
      else
      {
        if (this.arena != null) {
          return null;
        }
        localNode1.item = paramObject;
        if (U.compareAndSwapObject(this, SLOT, null, localNode1)) {
          break;
        }
        localNode1.item = null;
      }
    }
    int i = localNode1.hash;
    long l = paramBoolean ? System.nanoTime() + paramLong : 0L;
    int j = NCPU > 1 ? 1024 : 1;
    Object localObject2;
    while ((localObject2 = localNode1.match) == null) {
      if (j > 0)
      {
        i ^= i << 1;
        i ^= i >>> 3;
        i ^= i << 10;
        if (i == 0)
        {
          i = 0x400 | (int)localThread1.getId();
        }
        else if (i < 0)
        {
          j--;
          if ((j & 0x1FF) == 0) {
            Thread.yield();
          }
        }
      }
      else if (this.slot != localNode1)
      {
        j = 1024;
      }
      else if ((!localThread1.isInterrupted()) && (this.arena == null) && ((!paramBoolean) || ((paramLong = l - System.nanoTime()) > 0L)))
      {
        U.putObject(localThread1, BLOCKER, this);
        localNode1.parked = localThread1;
        if (this.slot == localNode1) {
          U.park(false, paramLong);
        }
        localNode1.parked = null;
        U.putObject(localThread1, BLOCKER, null);
      }
      else if (U.compareAndSwapObject(this, SLOT, localNode1, null))
      {
        localObject2 = (paramBoolean) && (paramLong <= 0L) && (!localThread1.isInterrupted()) ? TIMED_OUT : null;
      }
    }
    U.putOrderedObject(localNode1, MATCH, null);
    localNode1.item = null;
    localNode1.hash = i;
    return localObject2;
  }
  
  public Exchanger() {}
  
  public V exchange(V paramV)
    throws InterruptedException
  {
    V ? = paramV == null ? NULL_ITEM : paramV;
    Object localObject;
    if (((this.arena != null) || ((localObject = slotExchange(?, false, 0L)) == null)) && ((Thread.interrupted()) || ((localObject = arenaExchange(?, false, 0L)) == null))) {
      throw new InterruptedException();
    }
    return localObject == NULL_ITEM ? null : localObject;
  }
  
  public V exchange(V paramV, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, TimeoutException
  {
    V ? = paramV == null ? NULL_ITEM : paramV;
    long l = paramTimeUnit.toNanos(paramLong);
    Object localObject;
    if (((this.arena != null) || ((localObject = slotExchange(?, true, l)) == null)) && ((Thread.interrupted()) || ((localObject = arenaExchange(?, true, l)) == null))) {
      throw new InterruptedException();
    }
    if (localObject == TIMED_OUT) {
      throw new TimeoutException();
    }
    return localObject == NULL_ITEM ? null : localObject;
  }
  
  static
  {
    int i;
    try
    {
      U = Unsafe.getUnsafe();
      Exchanger localExchanger = Exchanger.class;
      Node localNode = Node.class;
      Node[] arrayOfNode = [Ljava.util.concurrent.Exchanger.Node.class;
      Thread localThread = Thread.class;
      BOUND = U.objectFieldOffset(localExchanger.getDeclaredField("bound"));
      SLOT = U.objectFieldOffset(localExchanger.getDeclaredField("slot"));
      MATCH = U.objectFieldOffset(localNode.getDeclaredField("match"));
      BLOCKER = U.objectFieldOffset(localThread.getDeclaredField("parkBlocker"));
      i = U.arrayIndexScale(arrayOfNode);
      ABASE = U.arrayBaseOffset(arrayOfNode) + 128;
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
    if (((i & i - 1) != 0) || (i > 128)) {
      throw new Error("Unsupported array scale");
    }
  }
  
  @Contended
  static final class Node
  {
    int index;
    int bound;
    int collides;
    int hash;
    Object item;
    volatile Object match;
    volatile Thread parked;
    
    Node() {}
  }
  
  static final class Participant
    extends ThreadLocal<Exchanger.Node>
  {
    Participant() {}
    
    public Exchanger.Node initialValue()
    {
      return new Exchanger.Node();
    }
  }
}
