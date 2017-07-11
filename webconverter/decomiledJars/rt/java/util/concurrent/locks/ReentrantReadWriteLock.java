package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class ReentrantReadWriteLock
  implements ReadWriteLock, Serializable
{
  private static final long serialVersionUID = -6992448646407690164L;
  private final ReadLock readerLock = new ReadLock(this);
  private final WriteLock writerLock = new WriteLock(this);
  final Sync sync = paramBoolean ? new FairSync() : new NonfairSync();
  private static final Unsafe UNSAFE;
  private static final long TID_OFFSET;
  
  public ReentrantReadWriteLock()
  {
    this(false);
  }
  
  public ReentrantReadWriteLock(boolean paramBoolean) {}
  
  public WriteLock writeLock()
  {
    return this.writerLock;
  }
  
  public ReadLock readLock()
  {
    return this.readerLock;
  }
  
  public final boolean isFair()
  {
    return this.sync instanceof FairSync;
  }
  
  protected Thread getOwner()
  {
    return this.sync.getOwner();
  }
  
  public int getReadLockCount()
  {
    return this.sync.getReadLockCount();
  }
  
  public boolean isWriteLocked()
  {
    return this.sync.isWriteLocked();
  }
  
  public boolean isWriteLockedByCurrentThread()
  {
    return this.sync.isHeldExclusively();
  }
  
  public int getWriteHoldCount()
  {
    return this.sync.getWriteHoldCount();
  }
  
  public int getReadHoldCount()
  {
    return this.sync.getReadHoldCount();
  }
  
  protected Collection<Thread> getQueuedWriterThreads()
  {
    return this.sync.getExclusiveQueuedThreads();
  }
  
  protected Collection<Thread> getQueuedReaderThreads()
  {
    return this.sync.getSharedQueuedThreads();
  }
  
  public final boolean hasQueuedThreads()
  {
    return this.sync.hasQueuedThreads();
  }
  
  public final boolean hasQueuedThread(Thread paramThread)
  {
    return this.sync.isQueued(paramThread);
  }
  
  public final int getQueueLength()
  {
    return this.sync.getQueueLength();
  }
  
  protected Collection<Thread> getQueuedThreads()
  {
    return this.sync.getQueuedThreads();
  }
  
  public boolean hasWaiters(Condition paramCondition)
  {
    if (paramCondition == null) {
      throw new NullPointerException();
    }
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject)) {
      throw new IllegalArgumentException("not owner");
    }
    return this.sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  public int getWaitQueueLength(Condition paramCondition)
  {
    if (paramCondition == null) {
      throw new NullPointerException();
    }
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject)) {
      throw new IllegalArgumentException("not owner");
    }
    return this.sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  protected Collection<Thread> getWaitingThreads(Condition paramCondition)
  {
    if (paramCondition == null) {
      throw new NullPointerException();
    }
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject)) {
      throw new IllegalArgumentException("not owner");
    }
    return this.sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  public String toString()
  {
    int i = this.sync.getCount();
    int j = Sync.exclusiveCount(i);
    int k = Sync.sharedCount(i);
    return super.toString() + "[Write locks = " + j + ", Read locks = " + k + "]";
  }
  
  static final long getThreadId(Thread paramThread)
  {
    return UNSAFE.getLongVolatile(paramThread, TID_OFFSET);
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Thread localThread = Thread.class;
      TID_OFFSET = UNSAFE.objectFieldOffset(localThread.getDeclaredField("tid"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class FairSync
    extends ReentrantReadWriteLock.Sync
  {
    private static final long serialVersionUID = -2274990926593161451L;
    
    FairSync() {}
    
    final boolean writerShouldBlock()
    {
      return hasQueuedPredecessors();
    }
    
    final boolean readerShouldBlock()
    {
      return hasQueuedPredecessors();
    }
  }
  
  static final class NonfairSync
    extends ReentrantReadWriteLock.Sync
  {
    private static final long serialVersionUID = -8159625535654395037L;
    
    NonfairSync() {}
    
    final boolean writerShouldBlock()
    {
      return false;
    }
    
    final boolean readerShouldBlock()
    {
      return apparentlyFirstQueuedIsExclusive();
    }
  }
  
  public static class ReadLock
    implements Lock, Serializable
  {
    private static final long serialVersionUID = -5992448646407690164L;
    private final ReentrantReadWriteLock.Sync sync;
    
    protected ReadLock(ReentrantReadWriteLock paramReentrantReadWriteLock)
    {
      this.sync = paramReentrantReadWriteLock.sync;
    }
    
    public void lock()
    {
      this.sync.acquireShared(1);
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      this.sync.acquireSharedInterruptibly(1);
    }
    
    public boolean tryLock()
    {
      return this.sync.tryReadLock();
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return this.sync.tryAcquireSharedNanos(1, paramTimeUnit.toNanos(paramLong));
    }
    
    public void unlock()
    {
      this.sync.releaseShared(1);
    }
    
    public Condition newCondition()
    {
      throw new UnsupportedOperationException();
    }
    
    public String toString()
    {
      int i = this.sync.getReadLockCount();
      return super.toString() + "[Read locks = " + i + "]";
    }
  }
  
  static abstract class Sync
    extends AbstractQueuedSynchronizer
  {
    private static final long serialVersionUID = 6317671515068378041L;
    static final int SHARED_SHIFT = 16;
    static final int SHARED_UNIT = 65536;
    static final int MAX_COUNT = 65535;
    static final int EXCLUSIVE_MASK = 65535;
    private transient ThreadLocalHoldCounter readHolds = new ThreadLocalHoldCounter();
    private transient HoldCounter cachedHoldCounter;
    private transient Thread firstReader = null;
    private transient int firstReaderHoldCount;
    
    static int sharedCount(int paramInt)
    {
      return paramInt >>> 16;
    }
    
    static int exclusiveCount(int paramInt)
    {
      return paramInt & 0xFFFF;
    }
    
    Sync()
    {
      setState(getState());
    }
    
    abstract boolean readerShouldBlock();
    
    abstract boolean writerShouldBlock();
    
    protected final boolean tryRelease(int paramInt)
    {
      if (!isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      int i = getState() - paramInt;
      boolean bool = exclusiveCount(i) == 0;
      if (bool) {
        setExclusiveOwnerThread(null);
      }
      setState(i);
      return bool;
    }
    
    protected final boolean tryAcquire(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      int j = exclusiveCount(i);
      if (i != 0)
      {
        if ((j == 0) || (localThread != getExclusiveOwnerThread())) {
          return false;
        }
        if (j + exclusiveCount(paramInt) > 65535) {
          throw new Error("Maximum lock count exceeded");
        }
        setState(i + paramInt);
        return true;
      }
      if ((writerShouldBlock()) || (!compareAndSetState(i, i + paramInt))) {
        return false;
      }
      setExclusiveOwnerThread(localThread);
      return true;
    }
    
    protected final boolean tryReleaseShared(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int j;
      if (this.firstReader == localThread)
      {
        if (this.firstReaderHoldCount == 1) {
          this.firstReader = null;
        } else {
          this.firstReaderHoldCount -= 1;
        }
      }
      else
      {
        HoldCounter localHoldCounter = this.cachedHoldCounter;
        if ((localHoldCounter == null) || (localHoldCounter.tid != ReentrantReadWriteLock.getThreadId(localThread))) {
          localHoldCounter = (HoldCounter)this.readHolds.get();
        }
        j = localHoldCounter.count;
        if (j <= 1)
        {
          this.readHolds.remove();
          if (j <= 0) {
            throw unmatchedUnlockException();
          }
        }
        localHoldCounter.count -= 1;
      }
      for (;;)
      {
        int i = getState();
        j = i - 65536;
        if (compareAndSetState(i, j)) {
          return j == 0;
        }
      }
    }
    
    private IllegalMonitorStateException unmatchedUnlockException()
    {
      return new IllegalMonitorStateException("attempt to unlock read lock, not locked by current thread");
    }
    
    protected final int tryAcquireShared(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      if ((exclusiveCount(i) != 0) && (getExclusiveOwnerThread() != localThread)) {
        return -1;
      }
      int j = sharedCount(i);
      if ((!readerShouldBlock()) && (j < 65535) && (compareAndSetState(i, i + 65536)))
      {
        if (j == 0)
        {
          this.firstReader = localThread;
          this.firstReaderHoldCount = 1;
        }
        else if (this.firstReader == localThread)
        {
          this.firstReaderHoldCount += 1;
        }
        else
        {
          HoldCounter localHoldCounter = this.cachedHoldCounter;
          if ((localHoldCounter == null) || (localHoldCounter.tid != ReentrantReadWriteLock.getThreadId(localThread))) {
            this.cachedHoldCounter = (localHoldCounter = (HoldCounter)this.readHolds.get());
          } else if (localHoldCounter.count == 0) {
            this.readHolds.set(localHoldCounter);
          }
          localHoldCounter.count += 1;
        }
        return 1;
      }
      return fullTryAcquireShared(localThread);
    }
    
    final int fullTryAcquireShared(Thread paramThread)
    {
      HoldCounter localHoldCounter = null;
      for (;;)
      {
        int i = getState();
        if (exclusiveCount(i) != 0)
        {
          if (getExclusiveOwnerThread() != paramThread) {
            return -1;
          }
        }
        else if ((readerShouldBlock()) && (this.firstReader != paramThread))
        {
          if (localHoldCounter == null)
          {
            localHoldCounter = this.cachedHoldCounter;
            if ((localHoldCounter == null) || (localHoldCounter.tid != ReentrantReadWriteLock.getThreadId(paramThread)))
            {
              localHoldCounter = (HoldCounter)this.readHolds.get();
              if (localHoldCounter.count == 0) {
                this.readHolds.remove();
              }
            }
          }
          if (localHoldCounter.count == 0) {
            return -1;
          }
        }
        if (sharedCount(i) == 65535) {
          throw new Error("Maximum lock count exceeded");
        }
        if (compareAndSetState(i, i + 65536))
        {
          if (sharedCount(i) == 0)
          {
            this.firstReader = paramThread;
            this.firstReaderHoldCount = 1;
          }
          else if (this.firstReader == paramThread)
          {
            this.firstReaderHoldCount += 1;
          }
          else
          {
            if (localHoldCounter == null) {
              localHoldCounter = this.cachedHoldCounter;
            }
            if ((localHoldCounter == null) || (localHoldCounter.tid != ReentrantReadWriteLock.getThreadId(paramThread))) {
              localHoldCounter = (HoldCounter)this.readHolds.get();
            } else if (localHoldCounter.count == 0) {
              this.readHolds.set(localHoldCounter);
            }
            localHoldCounter.count += 1;
            this.cachedHoldCounter = localHoldCounter;
          }
          return 1;
        }
      }
    }
    
    final boolean tryWriteLock()
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      if (i != 0)
      {
        int j = exclusiveCount(i);
        if ((j == 0) || (localThread != getExclusiveOwnerThread())) {
          return false;
        }
        if (j == 65535) {
          throw new Error("Maximum lock count exceeded");
        }
      }
      if (!compareAndSetState(i, i + 1)) {
        return false;
      }
      setExclusiveOwnerThread(localThread);
      return true;
    }
    
    final boolean tryReadLock()
    {
      Thread localThread = Thread.currentThread();
      for (;;)
      {
        int i = getState();
        if ((exclusiveCount(i) != 0) && (getExclusiveOwnerThread() != localThread)) {
          return false;
        }
        int j = sharedCount(i);
        if (j == 65535) {
          throw new Error("Maximum lock count exceeded");
        }
        if (compareAndSetState(i, i + 65536))
        {
          if (j == 0)
          {
            this.firstReader = localThread;
            this.firstReaderHoldCount = 1;
          }
          else if (this.firstReader == localThread)
          {
            this.firstReaderHoldCount += 1;
          }
          else
          {
            HoldCounter localHoldCounter = this.cachedHoldCounter;
            if ((localHoldCounter == null) || (localHoldCounter.tid != ReentrantReadWriteLock.getThreadId(localThread))) {
              this.cachedHoldCounter = (localHoldCounter = (HoldCounter)this.readHolds.get());
            } else if (localHoldCounter.count == 0) {
              this.readHolds.set(localHoldCounter);
            }
            localHoldCounter.count += 1;
          }
          return true;
        }
      }
    }
    
    protected final boolean isHeldExclusively()
    {
      return getExclusiveOwnerThread() == Thread.currentThread();
    }
    
    final AbstractQueuedSynchronizer.ConditionObject newCondition()
    {
      return new AbstractQueuedSynchronizer.ConditionObject(this);
    }
    
    final Thread getOwner()
    {
      return exclusiveCount(getState()) == 0 ? null : getExclusiveOwnerThread();
    }
    
    final int getReadLockCount()
    {
      return sharedCount(getState());
    }
    
    final boolean isWriteLocked()
    {
      return exclusiveCount(getState()) != 0;
    }
    
    final int getWriteHoldCount()
    {
      return isHeldExclusively() ? exclusiveCount(getState()) : 0;
    }
    
    final int getReadHoldCount()
    {
      if (getReadLockCount() == 0) {
        return 0;
      }
      Thread localThread = Thread.currentThread();
      if (this.firstReader == localThread) {
        return this.firstReaderHoldCount;
      }
      HoldCounter localHoldCounter = this.cachedHoldCounter;
      if ((localHoldCounter != null) && (localHoldCounter.tid == ReentrantReadWriteLock.getThreadId(localThread))) {
        return localHoldCounter.count;
      }
      int i = ((HoldCounter)this.readHolds.get()).count;
      if (i == 0) {
        this.readHolds.remove();
      }
      return i;
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      this.readHolds = new ThreadLocalHoldCounter();
      setState(0);
    }
    
    final int getCount()
    {
      return getState();
    }
    
    static final class HoldCounter
    {
      int count = 0;
      final long tid = ReentrantReadWriteLock.getThreadId(Thread.currentThread());
      
      HoldCounter() {}
    }
    
    static final class ThreadLocalHoldCounter
      extends ThreadLocal<ReentrantReadWriteLock.Sync.HoldCounter>
    {
      ThreadLocalHoldCounter() {}
      
      public ReentrantReadWriteLock.Sync.HoldCounter initialValue()
      {
        return new ReentrantReadWriteLock.Sync.HoldCounter();
      }
    }
  }
  
  public static class WriteLock
    implements Lock, Serializable
  {
    private static final long serialVersionUID = -4992448646407690164L;
    private final ReentrantReadWriteLock.Sync sync;
    
    protected WriteLock(ReentrantReadWriteLock paramReentrantReadWriteLock)
    {
      this.sync = paramReentrantReadWriteLock.sync;
    }
    
    public void lock()
    {
      this.sync.acquire(1);
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      this.sync.acquireInterruptibly(1);
    }
    
    public boolean tryLock()
    {
      return this.sync.tryWriteLock();
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return this.sync.tryAcquireNanos(1, paramTimeUnit.toNanos(paramLong));
    }
    
    public void unlock()
    {
      this.sync.release(1);
    }
    
    public Condition newCondition()
    {
      return this.sync.newCondition();
    }
    
    public String toString()
    {
      Thread localThread = this.sync.getOwner();
      return super.toString() + (localThread == null ? "[Unlocked]" : new StringBuilder().append("[Locked by thread ").append(localThread.getName()).append("]").toString());
    }
    
    public boolean isHeldByCurrentThread()
    {
      return this.sync.isHeldExclusively();
    }
    
    public int getHoldCount()
    {
      return this.sync.getWriteHoldCount();
    }
  }
}
