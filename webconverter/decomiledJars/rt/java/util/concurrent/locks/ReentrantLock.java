package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ReentrantLock
  implements Lock, Serializable
{
  private static final long serialVersionUID = 7373984872572414699L;
  private final Sync sync;
  
  public ReentrantLock()
  {
    this.sync = new NonfairSync();
  }
  
  public ReentrantLock(boolean paramBoolean)
  {
    this.sync = (paramBoolean ? new FairSync() : new NonfairSync());
  }
  
  public void lock()
  {
    this.sync.lock();
  }
  
  public void lockInterruptibly()
    throws InterruptedException
  {
    this.sync.acquireInterruptibly(1);
  }
  
  public boolean tryLock()
  {
    return this.sync.nonfairTryAcquire(1);
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
  
  public int getHoldCount()
  {
    return this.sync.getHoldCount();
  }
  
  public boolean isHeldByCurrentThread()
  {
    return this.sync.isHeldExclusively();
  }
  
  public boolean isLocked()
  {
    return this.sync.isLocked();
  }
  
  public final boolean isFair()
  {
    return this.sync instanceof FairSync;
  }
  
  protected Thread getOwner()
  {
    return this.sync.getOwner();
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
    Thread localThread = this.sync.getOwner();
    return super.toString() + (localThread == null ? "[Unlocked]" : new StringBuilder().append("[Locked by thread ").append(localThread.getName()).append("]").toString());
  }
  
  static final class FairSync
    extends ReentrantLock.Sync
  {
    private static final long serialVersionUID = -3000897897090466540L;
    
    FairSync() {}
    
    final void lock()
    {
      acquire(1);
    }
    
    protected final boolean tryAcquire(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      if (i == 0)
      {
        if ((!hasQueuedPredecessors()) && (compareAndSetState(0, paramInt)))
        {
          setExclusiveOwnerThread(localThread);
          return true;
        }
      }
      else if (localThread == getExclusiveOwnerThread())
      {
        int j = i + paramInt;
        if (j < 0) {
          throw new Error("Maximum lock count exceeded");
        }
        setState(j);
        return true;
      }
      return false;
    }
  }
  
  static final class NonfairSync
    extends ReentrantLock.Sync
  {
    private static final long serialVersionUID = 7316153563782823691L;
    
    NonfairSync() {}
    
    final void lock()
    {
      if (compareAndSetState(0, 1)) {
        setExclusiveOwnerThread(Thread.currentThread());
      } else {
        acquire(1);
      }
    }
    
    protected final boolean tryAcquire(int paramInt)
    {
      return nonfairTryAcquire(paramInt);
    }
  }
  
  static abstract class Sync
    extends AbstractQueuedSynchronizer
  {
    private static final long serialVersionUID = -5179523762034025860L;
    
    Sync() {}
    
    abstract void lock();
    
    final boolean nonfairTryAcquire(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      if (i == 0)
      {
        if (compareAndSetState(0, paramInt))
        {
          setExclusiveOwnerThread(localThread);
          return true;
        }
      }
      else if (localThread == getExclusiveOwnerThread())
      {
        int j = i + paramInt;
        if (j < 0) {
          throw new Error("Maximum lock count exceeded");
        }
        setState(j);
        return true;
      }
      return false;
    }
    
    protected final boolean tryRelease(int paramInt)
    {
      int i = getState() - paramInt;
      if (Thread.currentThread() != getExclusiveOwnerThread()) {
        throw new IllegalMonitorStateException();
      }
      boolean bool = false;
      if (i == 0)
      {
        bool = true;
        setExclusiveOwnerThread(null);
      }
      setState(i);
      return bool;
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
      return getState() == 0 ? null : getExclusiveOwnerThread();
    }
    
    final int getHoldCount()
    {
      return isHeldExclusively() ? getState() : 0;
    }
    
    final boolean isLocked()
    {
      return getState() != 0;
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      setState(0);
    }
  }
}
