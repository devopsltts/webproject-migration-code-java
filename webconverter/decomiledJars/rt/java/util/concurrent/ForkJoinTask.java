package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public abstract class ForkJoinTask<V>
  implements Future<V>, Serializable
{
  volatile int status;
  static final int DONE_MASK = -268435456;
  static final int NORMAL = -268435456;
  static final int CANCELLED = -1073741824;
  static final int EXCEPTIONAL = Integer.MIN_VALUE;
  static final int SIGNAL = 65536;
  static final int SMASK = 65535;
  private static final ExceptionNode[] exceptionTable;
  private static final ReentrantLock exceptionTableLock = new ReentrantLock();
  private static final ReferenceQueue<Object> exceptionTableRefQueue = new ReferenceQueue();
  private static final int EXCEPTION_MAP_CAPACITY = 32;
  private static final long serialVersionUID = -7721805057305804111L;
  private static final Unsafe U;
  private static final long STATUS;
  
  public ForkJoinTask() {}
  
  private int setCompletion(int paramInt)
  {
    int i;
    do
    {
      if ((i = this.status) < 0) {
        return i;
      }
    } while (!U.compareAndSwapInt(this, STATUS, i, i | paramInt));
    if (i >>> 16 != 0) {
      synchronized (this)
      {
        notifyAll();
      }
    }
    return paramInt;
  }
  
  final int doExec()
  {
    int i;
    if ((i = this.status) >= 0)
    {
      boolean bool;
      try
      {
        bool = exec();
      }
      catch (Throwable localThrowable)
      {
        return setExceptionalCompletion(localThrowable);
      }
      if (bool) {
        i = setCompletion(-268435456);
      }
    }
    return i;
  }
  
  final void internalWait(long paramLong)
  {
    int i;
    if (((i = this.status) >= 0) && (U.compareAndSwapInt(this, STATUS, i, i | 0x10000))) {
      synchronized (this)
      {
        if (this.status >= 0) {
          try
          {
            wait(paramLong);
          }
          catch (InterruptedException localInterruptedException) {}
        } else {
          notifyAll();
        }
      }
    }
  }
  
  private int externalAwaitDone()
  {
    int i = ForkJoinPool.common.tryExternalUnpush(this) ? doExec() : (this instanceof CountedCompleter) ? ForkJoinPool.common.externalHelpComplete((CountedCompleter)this, 0) : 0;
    if ((i >= 0) && ((i = this.status) >= 0))
    {
      int j = 0;
      do
      {
        if (U.compareAndSwapInt(this, STATUS, i, i | 0x10000)) {
          synchronized (this)
          {
            if (this.status >= 0) {
              try
              {
                wait(0L);
              }
              catch (InterruptedException localInterruptedException)
              {
                j = 1;
              }
            } else {
              notifyAll();
            }
          }
        }
      } while ((i = this.status) >= 0);
      if (j != 0) {
        Thread.currentThread().interrupt();
      }
    }
    return i;
  }
  
  private int externalInterruptibleAwaitDone()
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    int i;
    if ((i = this.status) >= 0) {
      if ((i = ForkJoinPool.common.tryExternalUnpush(this) ? doExec() : (this instanceof CountedCompleter) ? ForkJoinPool.common.externalHelpComplete((CountedCompleter)this, 0) : 0) >= 0) {
        while ((i = this.status) >= 0) {
          if (U.compareAndSwapInt(this, STATUS, i, i | 0x10000)) {
            synchronized (this)
            {
              if (this.status >= 0) {
                wait(0L);
              } else {
                notifyAll();
              }
            }
          }
        }
      }
    }
    return i;
  }
  
  private int doJoin()
  {
    int i;
    Thread localThread;
    ForkJoinWorkerThread localForkJoinWorkerThread;
    ForkJoinPool.WorkQueue localWorkQueue;
    return ((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread) ? localForkJoinWorkerThread.pool.awaitJoin(localWorkQueue, this, 0L) : ((localWorkQueue = (localForkJoinWorkerThread = (ForkJoinWorkerThread)localThread).workQueue).tryUnpush(this)) && ((i = doExec()) < 0) ? i : (i = this.status) < 0 ? i : externalAwaitDone();
  }
  
  private int doInvoke()
  {
    int i;
    Thread localThread;
    ForkJoinWorkerThread localForkJoinWorkerThread;
    return ((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread) ? (localForkJoinWorkerThread = (ForkJoinWorkerThread)localThread).pool.awaitJoin(localForkJoinWorkerThread.workQueue, this, 0L) : (i = doExec()) < 0 ? i : externalAwaitDone();
  }
  
  final int recordExceptionalCompletion(Throwable paramThrowable)
  {
    int i;
    if ((i = this.status) >= 0)
    {
      int j = System.identityHashCode(this);
      ReentrantLock localReentrantLock = exceptionTableLock;
      localReentrantLock.lock();
      try
      {
        expungeStaleExceptions();
        ExceptionNode[] arrayOfExceptionNode = exceptionTable;
        int k = j & arrayOfExceptionNode.length - 1;
        for (ExceptionNode localExceptionNode = arrayOfExceptionNode[k];; localExceptionNode = localExceptionNode.next) {
          if (localExceptionNode == null) {
            arrayOfExceptionNode[k] = new ExceptionNode(this, paramThrowable, arrayOfExceptionNode[k]);
          } else {
            if (localExceptionNode.get() == this) {
              break;
            }
          }
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
      i = setCompletion(Integer.MIN_VALUE);
    }
    return i;
  }
  
  private int setExceptionalCompletion(Throwable paramThrowable)
  {
    int i = recordExceptionalCompletion(paramThrowable);
    if ((i & 0xF0000000) == Integer.MIN_VALUE) {
      internalPropagateException(paramThrowable);
    }
    return i;
  }
  
  void internalPropagateException(Throwable paramThrowable) {}
  
  static final void cancelIgnoringExceptions(ForkJoinTask<?> paramForkJoinTask)
  {
    if ((paramForkJoinTask != null) && (paramForkJoinTask.status >= 0)) {
      try
      {
        paramForkJoinTask.cancel(false);
      }
      catch (Throwable localThrowable) {}
    }
  }
  
  private void clearExceptionalCompletion()
  {
    int i = System.identityHashCode(this);
    ReentrantLock localReentrantLock = exceptionTableLock;
    localReentrantLock.lock();
    try
    {
      ExceptionNode[] arrayOfExceptionNode = exceptionTable;
      int j = i & arrayOfExceptionNode.length - 1;
      Object localObject1 = arrayOfExceptionNode[j];
      Object localObject2 = null;
      while (localObject1 != null)
      {
        ExceptionNode localExceptionNode = ((ExceptionNode)localObject1).next;
        if (((ExceptionNode)localObject1).get() == this)
        {
          if (localObject2 == null)
          {
            arrayOfExceptionNode[j] = localExceptionNode;
            break;
          }
          localObject2.next = localExceptionNode;
          break;
        }
        localObject2 = localObject1;
        localObject1 = localExceptionNode;
      }
      expungeStaleExceptions();
      this.status = 0;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private Throwable getThrowableException()
  {
    if ((this.status & 0xF0000000) != Integer.MIN_VALUE) {
      return null;
    }
    int i = System.identityHashCode(this);
    ReentrantLock localReentrantLock = exceptionTableLock;
    localReentrantLock.lock();
    Object localObject1;
    ExceptionNode localExceptionNode;
    try
    {
      expungeStaleExceptions();
      localObject1 = exceptionTable;
      for (localExceptionNode = localObject1[(i & localObject1.length - 1)]; (localExceptionNode != null) && (localExceptionNode.get() != this); localExceptionNode = localExceptionNode.next) {}
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if ((localExceptionNode == null) || ((localObject1 = localExceptionNode.ex) == null)) {
      return null;
    }
    if (localExceptionNode.thrower != Thread.currentThread().getId())
    {
      Class localClass = localObject1.getClass();
      try
      {
        Object localObject3 = null;
        Constructor[] arrayOfConstructor = localClass.getConstructors();
        for (int j = 0; j < arrayOfConstructor.length; j++)
        {
          Constructor localConstructor = arrayOfConstructor[j];
          Class[] arrayOfClass = localConstructor.getParameterTypes();
          if (arrayOfClass.length == 0)
          {
            localObject3 = localConstructor;
          }
          else if ((arrayOfClass.length == 1) && (arrayOfClass[0] == Throwable.class))
          {
            Throwable localThrowable2 = (Throwable)localConstructor.newInstance(new Object[] { localObject1 });
            return localThrowable2 == null ? localObject1 : localThrowable2;
          }
        }
        if (localObject3 != null)
        {
          Throwable localThrowable1 = (Throwable)localObject3.newInstance(new Object[0]);
          if (localThrowable1 != null)
          {
            localThrowable1.initCause((Throwable)localObject1);
            return localThrowable1;
          }
        }
      }
      catch (Exception localException) {}
    }
    return localObject1;
  }
  
  private static void expungeStaleExceptions()
  {
    Reference localReference;
    while ((localReference = exceptionTableRefQueue.poll()) != null) {
      if ((localReference instanceof ExceptionNode))
      {
        int i = ((ExceptionNode)localReference).hashCode;
        ExceptionNode[] arrayOfExceptionNode = exceptionTable;
        int j = i & arrayOfExceptionNode.length - 1;
        Object localObject1 = arrayOfExceptionNode[j];
        Object localObject2 = null;
        while (localObject1 != null)
        {
          ExceptionNode localExceptionNode = ((ExceptionNode)localObject1).next;
          if (localObject1 == localReference)
          {
            if (localObject2 == null)
            {
              arrayOfExceptionNode[j] = localExceptionNode;
              break;
            }
            localObject2.next = localExceptionNode;
            break;
          }
          localObject2 = localObject1;
          localObject1 = localExceptionNode;
        }
      }
    }
  }
  
  static final void helpExpungeStaleExceptions()
  {
    ReentrantLock localReentrantLock = exceptionTableLock;
    if (localReentrantLock.tryLock()) {
      try
      {
        expungeStaleExceptions();
        localReentrantLock.unlock();
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
  }
  
  static void rethrow(Throwable paramThrowable)
  {
    if (paramThrowable != null) {
      uncheckedThrow(paramThrowable);
    }
  }
  
  static <T extends Throwable> void uncheckedThrow(Throwable paramThrowable)
    throws Throwable
  {
    throw paramThrowable;
  }
  
  private void reportException(int paramInt)
  {
    if (paramInt == -1073741824) {
      throw new CancellationException();
    }
    if (paramInt == Integer.MIN_VALUE) {
      rethrow(getThrowableException());
    }
  }
  
  public final ForkJoinTask<V> fork()
  {
    Thread localThread;
    if (((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread)) {
      ((ForkJoinWorkerThread)localThread).workQueue.push(this);
    } else {
      ForkJoinPool.common.externalPush(this);
    }
    return this;
  }
  
  public final V join()
  {
    int i;
    if ((i = doJoin() & 0xF0000000) != -268435456) {
      reportException(i);
    }
    return getRawResult();
  }
  
  public final V invoke()
  {
    int i;
    if ((i = doInvoke() & 0xF0000000) != -268435456) {
      reportException(i);
    }
    return getRawResult();
  }
  
  public static void invokeAll(ForkJoinTask<?> paramForkJoinTask1, ForkJoinTask<?> paramForkJoinTask2)
  {
    paramForkJoinTask2.fork();
    int i;
    if ((i = paramForkJoinTask1.doInvoke() & 0xF0000000) != -268435456) {
      paramForkJoinTask1.reportException(i);
    }
    int j;
    if ((j = paramForkJoinTask2.doJoin() & 0xF0000000) != -268435456) {
      paramForkJoinTask2.reportException(j);
    }
  }
  
  public static void invokeAll(ForkJoinTask<?>... paramVarArgs)
  {
    Object localObject = null;
    int i = paramVarArgs.length - 1;
    ForkJoinTask<?> localForkJoinTask;
    for (int j = i; j >= 0; j--)
    {
      localForkJoinTask = paramVarArgs[j];
      if (localForkJoinTask == null)
      {
        if (localObject == null) {
          localObject = new NullPointerException();
        }
      }
      else if (j != 0) {
        localForkJoinTask.fork();
      } else if ((localForkJoinTask.doInvoke() < -268435456) && (localObject == null)) {
        localObject = localForkJoinTask.getException();
      }
    }
    for (j = 1; j <= i; j++)
    {
      localForkJoinTask = paramVarArgs[j];
      if (localForkJoinTask != null) {
        if (localObject != null) {
          localForkJoinTask.cancel(false);
        } else if (localForkJoinTask.doJoin() < -268435456) {
          localObject = localForkJoinTask.getException();
        }
      }
    }
    if (localObject != null) {
      rethrow((Throwable)localObject);
    }
  }
  
  public static <T extends ForkJoinTask<?>> Collection<T> invokeAll(Collection<T> paramCollection)
  {
    if ((!(paramCollection instanceof RandomAccess)) || (!(paramCollection instanceof List)))
    {
      invokeAll((ForkJoinTask[])paramCollection.toArray(new ForkJoinTask[paramCollection.size()]));
      return paramCollection;
    }
    List localList = (List)paramCollection;
    Object localObject = null;
    int i = localList.size() - 1;
    ForkJoinTask localForkJoinTask;
    for (int j = i; j >= 0; j--)
    {
      localForkJoinTask = (ForkJoinTask)localList.get(j);
      if (localForkJoinTask == null)
      {
        if (localObject == null) {
          localObject = new NullPointerException();
        }
      }
      else if (j != 0) {
        localForkJoinTask.fork();
      } else if ((localForkJoinTask.doInvoke() < -268435456) && (localObject == null)) {
        localObject = localForkJoinTask.getException();
      }
    }
    for (j = 1; j <= i; j++)
    {
      localForkJoinTask = (ForkJoinTask)localList.get(j);
      if (localForkJoinTask != null) {
        if (localObject != null) {
          localForkJoinTask.cancel(false);
        } else if (localForkJoinTask.doJoin() < -268435456) {
          localObject = localForkJoinTask.getException();
        }
      }
    }
    if (localObject != null) {
      rethrow((Throwable)localObject);
    }
    return paramCollection;
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    return (setCompletion(-1073741824) & 0xF0000000) == -1073741824;
  }
  
  public final boolean isDone()
  {
    return this.status < 0;
  }
  
  public final boolean isCancelled()
  {
    return (this.status & 0xF0000000) == -1073741824;
  }
  
  public final boolean isCompletedAbnormally()
  {
    return this.status < -268435456;
  }
  
  public final boolean isCompletedNormally()
  {
    return (this.status & 0xF0000000) == -268435456;
  }
  
  public final Throwable getException()
  {
    int i = this.status & 0xF0000000;
    return i == -1073741824 ? new CancellationException() : i >= -268435456 ? null : getThrowableException();
  }
  
  public void completeExceptionally(Throwable paramThrowable)
  {
    setExceptionalCompletion(((paramThrowable instanceof RuntimeException)) || ((paramThrowable instanceof Error)) ? paramThrowable : new RuntimeException(paramThrowable));
  }
  
  public void complete(V paramV)
  {
    try
    {
      setRawResult(paramV);
    }
    catch (Throwable localThrowable)
    {
      setExceptionalCompletion(localThrowable);
      return;
    }
    setCompletion(-268435456);
  }
  
  public final void quietlyComplete()
  {
    setCompletion(-268435456);
  }
  
  public final V get()
    throws InterruptedException, ExecutionException
  {
    int i = (Thread.currentThread() instanceof ForkJoinWorkerThread) ? doJoin() : externalInterruptibleAwaitDone();
    if ((i &= 0xF0000000) == -1073741824) {
      throw new CancellationException();
    }
    Throwable localThrowable;
    if ((i == Integer.MIN_VALUE) && ((localThrowable = getThrowableException()) != null)) {
      throw new ExecutionException(localThrowable);
    }
    return getRawResult();
  }
  
  public final V get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    long l1 = paramTimeUnit.toNanos(paramLong);
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    int i;
    if (((i = this.status) >= 0) && (l1 > 0L))
    {
      long l2 = System.nanoTime() + l1;
      long l3 = l2 == 0L ? 1L : l2;
      Thread localThread = Thread.currentThread();
      if ((localThread instanceof ForkJoinWorkerThread))
      {
        ForkJoinWorkerThread localForkJoinWorkerThread = (ForkJoinWorkerThread)localThread;
        i = localForkJoinWorkerThread.pool.awaitJoin(localForkJoinWorkerThread.workQueue, this, l3);
      }
      else if ((i = ForkJoinPool.common.tryExternalUnpush(this) ? doExec() : (this instanceof CountedCompleter) ? ForkJoinPool.common.externalHelpComplete((CountedCompleter)this, 0) : 0) >= 0)
      {
        long l4;
        while (((i = this.status) >= 0) && ((l4 = l3 - System.nanoTime()) > 0L))
        {
          long l5;
          if (((l5 = TimeUnit.NANOSECONDS.toMillis(l4)) > 0L) && (U.compareAndSwapInt(this, STATUS, i, i | 0x10000))) {
            synchronized (this)
            {
              if (this.status >= 0) {
                wait(l5);
              } else {
                notifyAll();
              }
            }
          }
        }
      }
    }
    if (i >= 0) {
      i = this.status;
    }
    if ((i &= 0xF0000000) != -268435456)
    {
      if (i == -1073741824) {
        throw new CancellationException();
      }
      if (i != Integer.MIN_VALUE) {
        throw new TimeoutException();
      }
      Throwable localThrowable;
      if ((localThrowable = getThrowableException()) != null) {
        throw new ExecutionException(localThrowable);
      }
    }
    return getRawResult();
  }
  
  public final void quietlyJoin()
  {
    doJoin();
  }
  
  public final void quietlyInvoke()
  {
    doInvoke();
  }
  
  public static void helpQuiesce()
  {
    Thread localThread;
    if (((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread))
    {
      ForkJoinWorkerThread localForkJoinWorkerThread = (ForkJoinWorkerThread)localThread;
      localForkJoinWorkerThread.pool.helpQuiescePool(localForkJoinWorkerThread.workQueue);
    }
    else
    {
      ForkJoinPool.quiesceCommonPool();
    }
  }
  
  public void reinitialize()
  {
    if ((this.status & 0xF0000000) == Integer.MIN_VALUE) {
      clearExceptionalCompletion();
    } else {
      this.status = 0;
    }
  }
  
  public static ForkJoinPool getPool()
  {
    Thread localThread = Thread.currentThread();
    return (localThread instanceof ForkJoinWorkerThread) ? ((ForkJoinWorkerThread)localThread).pool : null;
  }
  
  public static boolean inForkJoinPool()
  {
    return Thread.currentThread() instanceof ForkJoinWorkerThread;
  }
  
  public boolean tryUnfork()
  {
    Thread localThread;
    return ((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread) ? ((ForkJoinWorkerThread)localThread).workQueue.tryUnpush(this) : ForkJoinPool.common.tryExternalUnpush(this);
  }
  
  public static int getQueuedTaskCount()
  {
    Thread localThread;
    ForkJoinPool.WorkQueue localWorkQueue;
    if (((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread)) {
      localWorkQueue = ((ForkJoinWorkerThread)localThread).workQueue;
    } else {
      localWorkQueue = ForkJoinPool.commonSubmitterQueue();
    }
    return localWorkQueue == null ? 0 : localWorkQueue.queueSize();
  }
  
  public static int getSurplusQueuedTaskCount()
  {
    return ForkJoinPool.getSurplusQueuedTaskCount();
  }
  
  public abstract V getRawResult();
  
  protected abstract void setRawResult(V paramV);
  
  protected abstract boolean exec();
  
  protected static ForkJoinTask<?> peekNextLocalTask()
  {
    Thread localThread;
    ForkJoinPool.WorkQueue localWorkQueue;
    if (((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread)) {
      localWorkQueue = ((ForkJoinWorkerThread)localThread).workQueue;
    } else {
      localWorkQueue = ForkJoinPool.commonSubmitterQueue();
    }
    return localWorkQueue == null ? null : localWorkQueue.peek();
  }
  
  protected static ForkJoinTask<?> pollNextLocalTask()
  {
    Thread localThread;
    return ((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread) ? ((ForkJoinWorkerThread)localThread).workQueue.nextLocalTask() : null;
  }
  
  protected static ForkJoinTask<?> pollTask()
  {
    Thread localThread;
    ForkJoinWorkerThread localForkJoinWorkerThread;
    return ((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread) ? (localForkJoinWorkerThread = (ForkJoinWorkerThread)localThread).pool.nextTaskFor(localForkJoinWorkerThread.workQueue) : null;
  }
  
  public final short getForkJoinTaskTag()
  {
    return (short)this.status;
  }
  
  public final short setForkJoinTaskTag(short paramShort)
  {
    int i;
    while (!U.compareAndSwapInt(this, STATUS, i = this.status, i & 0xFFFF0000 | paramShort & 0xFFFF)) {}
    return (short)i;
  }
  
  public final boolean compareAndSetForkJoinTaskTag(short paramShort1, short paramShort2)
  {
    int i;
    do
    {
      if ((short)(i = this.status) != paramShort1) {
        return false;
      }
    } while (!U.compareAndSwapInt(this, STATUS, i, i & 0xFFFF0000 | paramShort2 & 0xFFFF));
    return true;
  }
  
  public static ForkJoinTask<?> adapt(Runnable paramRunnable)
  {
    return new AdaptedRunnableAction(paramRunnable);
  }
  
  public static <T> ForkJoinTask<T> adapt(Runnable paramRunnable, T paramT)
  {
    return new AdaptedRunnable(paramRunnable, paramT);
  }
  
  public static <T> ForkJoinTask<T> adapt(Callable<? extends T> paramCallable)
  {
    return new AdaptedCallable(paramCallable);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(getException());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Object localObject = paramObjectInputStream.readObject();
    if (localObject != null) {
      setExceptionalCompletion((Throwable)localObject);
    }
  }
  
  static
  {
    exceptionTable = new ExceptionNode[32];
    try
    {
      U = Unsafe.getUnsafe();
      ForkJoinTask localForkJoinTask = ForkJoinTask.class;
      STATUS = U.objectFieldOffset(localForkJoinTask.getDeclaredField("status"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class AdaptedCallable<T>
    extends ForkJoinTask<T>
    implements RunnableFuture<T>
  {
    final Callable<? extends T> callable;
    T result;
    private static final long serialVersionUID = 2838392045355241008L;
    
    AdaptedCallable(Callable<? extends T> paramCallable)
    {
      if (paramCallable == null) {
        throw new NullPointerException();
      }
      this.callable = paramCallable;
    }
    
    public final T getRawResult()
    {
      return this.result;
    }
    
    public final void setRawResult(T paramT)
    {
      this.result = paramT;
    }
    
    public final boolean exec()
    {
      try
      {
        this.result = this.callable.call();
        return true;
      }
      catch (Error localError)
      {
        throw localError;
      }
      catch (RuntimeException localRuntimeException)
      {
        throw localRuntimeException;
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
    }
    
    public final void run()
    {
      invoke();
    }
  }
  
  static final class AdaptedRunnable<T>
    extends ForkJoinTask<T>
    implements RunnableFuture<T>
  {
    final Runnable runnable;
    T result;
    private static final long serialVersionUID = 5232453952276885070L;
    
    AdaptedRunnable(Runnable paramRunnable, T paramT)
    {
      if (paramRunnable == null) {
        throw new NullPointerException();
      }
      this.runnable = paramRunnable;
      this.result = paramT;
    }
    
    public final T getRawResult()
    {
      return this.result;
    }
    
    public final void setRawResult(T paramT)
    {
      this.result = paramT;
    }
    
    public final boolean exec()
    {
      this.runnable.run();
      return true;
    }
    
    public final void run()
    {
      invoke();
    }
  }
  
  static final class AdaptedRunnableAction
    extends ForkJoinTask<Void>
    implements RunnableFuture<Void>
  {
    final Runnable runnable;
    private static final long serialVersionUID = 5232453952276885070L;
    
    AdaptedRunnableAction(Runnable paramRunnable)
    {
      if (paramRunnable == null) {
        throw new NullPointerException();
      }
      this.runnable = paramRunnable;
    }
    
    public final Void getRawResult()
    {
      return null;
    }
    
    public final void setRawResult(Void paramVoid) {}
    
    public final boolean exec()
    {
      this.runnable.run();
      return true;
    }
    
    public final void run()
    {
      invoke();
    }
  }
  
  static final class ExceptionNode
    extends WeakReference<ForkJoinTask<?>>
  {
    final Throwable ex;
    ExceptionNode next;
    final long thrower;
    final int hashCode;
    
    ExceptionNode(ForkJoinTask<?> paramForkJoinTask, Throwable paramThrowable, ExceptionNode paramExceptionNode)
    {
      super(ForkJoinTask.exceptionTableRefQueue);
      this.ex = paramThrowable;
      this.next = paramExceptionNode;
      this.thrower = Thread.currentThread().getId();
      this.hashCode = System.identityHashCode(paramForkJoinTask);
    }
  }
  
  static final class RunnableExecuteAction
    extends ForkJoinTask<Void>
  {
    final Runnable runnable;
    private static final long serialVersionUID = 5232453952276885070L;
    
    RunnableExecuteAction(Runnable paramRunnable)
    {
      if (paramRunnable == null) {
        throw new NullPointerException();
      }
      this.runnable = paramRunnable;
    }
    
    public final Void getRawResult()
    {
      return null;
    }
    
    public final void setRawResult(Void paramVoid) {}
    
    public final boolean exec()
    {
      this.runnable.run();
      return true;
    }
    
    void internalPropagateException(Throwable paramThrowable)
    {
      rethrow(paramThrowable);
    }
  }
}
