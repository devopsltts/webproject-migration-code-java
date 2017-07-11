package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public class FutureTask<V>
  implements RunnableFuture<V>
{
  private volatile int state;
  private static final int NEW = 0;
  private static final int COMPLETING = 1;
  private static final int NORMAL = 2;
  private static final int EXCEPTIONAL = 3;
  private static final int CANCELLED = 4;
  private static final int INTERRUPTING = 5;
  private static final int INTERRUPTED = 6;
  private Callable<V> callable;
  private Object outcome;
  private volatile Thread runner;
  private volatile WaitNode waiters;
  private static final Unsafe UNSAFE;
  private static final long stateOffset;
  private static final long runnerOffset;
  private static final long waitersOffset;
  
  private V report(int paramInt)
    throws ExecutionException
  {
    Object localObject = this.outcome;
    if (paramInt == 2) {
      return localObject;
    }
    if (paramInt >= 4) {
      throw new CancellationException();
    }
    throw new ExecutionException((Throwable)localObject);
  }
  
  public FutureTask(Callable<V> paramCallable)
  {
    if (paramCallable == null) {
      throw new NullPointerException();
    }
    this.callable = paramCallable;
    this.state = 0;
  }
  
  public FutureTask(Runnable paramRunnable, V paramV)
  {
    this.callable = Executors.callable(paramRunnable, paramV);
    this.state = 0;
  }
  
  public boolean isCancelled()
  {
    return this.state >= 4;
  }
  
  public boolean isDone()
  {
    return this.state != 0;
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    if (this.state == 0)
    {
      if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, paramBoolean ? 5 : 4)) {}
    }
    else {
      return false;
    }
    try
    {
      if (paramBoolean) {}
      try
      {
        Thread localThread = this.runner;
        if (localThread != null) {
          localThread.interrupt();
        }
        UNSAFE.putOrderedInt(this, stateOffset, 6);
      }
      finally
      {
        UNSAFE.putOrderedInt(this, stateOffset, 6);
      }
    }
    finally
    {
      finishCompletion();
    }
    return true;
  }
  
  public V get()
    throws InterruptedException, ExecutionException
  {
    int i = this.state;
    if (i <= 1) {
      i = awaitDone(false, 0L);
    }
    return report(i);
  }
  
  public V get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    if (paramTimeUnit == null) {
      throw new NullPointerException();
    }
    int i = this.state;
    if ((i <= 1) && ((i = awaitDone(true, paramTimeUnit.toNanos(paramLong))) <= 1)) {
      throw new TimeoutException();
    }
    return report(i);
  }
  
  protected void done() {}
  
  protected void set(V paramV)
  {
    if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, 1))
    {
      this.outcome = paramV;
      UNSAFE.putOrderedInt(this, stateOffset, 2);
      finishCompletion();
    }
  }
  
  protected void setException(Throwable paramThrowable)
  {
    if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, 1))
    {
      this.outcome = paramThrowable;
      UNSAFE.putOrderedInt(this, stateOffset, 3);
      finishCompletion();
    }
  }
  
  public void run()
  {
    if ((this.state != 0) || (!UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))) {
      return;
    }
    try
    {
      Callable localCallable = this.callable;
      if ((localCallable != null) && (this.state == 0))
      {
        Object localObject1;
        int j;
        try
        {
          localObject1 = localCallable.call();
          j = 1;
        }
        catch (Throwable localThrowable)
        {
          localObject1 = null;
          j = 0;
          setException(localThrowable);
        }
        if (j != 0) {
          set(localObject1);
        }
      }
    }
    finally
    {
      int i;
      this.runner = null;
      int k = this.state;
      if (k >= 5) {
        handlePossibleCancellationInterrupt(k);
      }
    }
  }
  
  protected boolean runAndReset()
  {
    if ((this.state != 0) || (!UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))) {
      return false;
    }
    int i = 0;
    int j = this.state;
    try
    {
      Callable localCallable = this.callable;
      if ((localCallable != null) && (j == 0)) {
        try
        {
          localCallable.call();
          i = 1;
        }
        catch (Throwable localThrowable)
        {
          setException(localThrowable);
        }
      }
    }
    finally
    {
      this.runner = null;
      j = this.state;
      if (j >= 5) {
        handlePossibleCancellationInterrupt(j);
      }
    }
    return (i != 0) && (j == 0);
  }
  
  private void handlePossibleCancellationInterrupt(int paramInt)
  {
    if (paramInt == 5) {
      while (this.state == 5) {
        Thread.yield();
      }
    }
  }
  
  private void finishCompletion()
  {
    Object localObject;
    while ((localObject = this.waiters) != null) {
      if (UNSAFE.compareAndSwapObject(this, waitersOffset, localObject, null)) {
        for (;;)
        {
          Thread localThread = ((WaitNode)localObject).thread;
          if (localThread != null)
          {
            ((WaitNode)localObject).thread = null;
            LockSupport.unpark(localThread);
          }
          WaitNode localWaitNode = ((WaitNode)localObject).next;
          if (localWaitNode == null) {
            break;
          }
          ((WaitNode)localObject).next = null;
          localObject = localWaitNode;
        }
      }
    }
    done();
    this.callable = null;
  }
  
  private int awaitDone(boolean paramBoolean, long paramLong)
    throws InterruptedException
  {
    long l = paramBoolean ? System.nanoTime() + paramLong : 0L;
    WaitNode localWaitNode = null;
    boolean bool = false;
    for (;;)
    {
      if (Thread.interrupted())
      {
        removeWaiter(localWaitNode);
        throw new InterruptedException();
      }
      int i = this.state;
      if (i > 1)
      {
        if (localWaitNode != null) {
          localWaitNode.thread = null;
        }
        return i;
      }
      if (i == 1)
      {
        Thread.yield();
      }
      else if (localWaitNode == null)
      {
        localWaitNode = new WaitNode();
      }
      else if (!bool)
      {
        bool = UNSAFE.compareAndSwapObject(this, waitersOffset, localWaitNode.next = this.waiters, localWaitNode);
      }
      else if (paramBoolean)
      {
        paramLong = l - System.nanoTime();
        if (paramLong <= 0L)
        {
          removeWaiter(localWaitNode);
          return this.state;
        }
        LockSupport.parkNanos(this, paramLong);
      }
      else
      {
        LockSupport.park(this);
      }
    }
  }
  
  private void removeWaiter(WaitNode paramWaitNode)
  {
    if (paramWaitNode != null)
    {
      paramWaitNode.thread = null;
      Object localObject1 = null;
      WaitNode localWaitNode;
      for (Object localObject2 = this.waiters;; localObject2 = localWaitNode)
      {
        if (localObject2 == null) {
          return;
        }
        localWaitNode = ((WaitNode)localObject2).next;
        if (((WaitNode)localObject2).thread != null)
        {
          localObject1 = localObject2;
        }
        else
        {
          if (localObject1 != null)
          {
            localObject1.next = localWaitNode;
            if (localObject1.thread != null) {
              continue;
            }
            break;
          }
          if (!UNSAFE.compareAndSwapObject(this, waitersOffset, localObject2, localWaitNode)) {
            break;
          }
        }
      }
    }
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      FutureTask localFutureTask = FutureTask.class;
      stateOffset = UNSAFE.objectFieldOffset(localFutureTask.getDeclaredField("state"));
      runnerOffset = UNSAFE.objectFieldOffset(localFutureTask.getDeclaredField("runner"));
      waitersOffset = UNSAFE.objectFieldOffset(localFutureTask.getDeclaredField("waiters"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class WaitNode
  {
    volatile Thread thread = Thread.currentThread();
    volatile WaitNode next;
    
    WaitNode() {}
  }
}
