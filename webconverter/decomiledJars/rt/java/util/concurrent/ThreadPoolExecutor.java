package java.util.concurrent;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolExecutor
  extends AbstractExecutorService
{
  private final AtomicInteger ctl = new AtomicInteger(ctlOf(-536870912, 0));
  private static final int COUNT_BITS = 29;
  private static final int CAPACITY = 536870911;
  private static final int RUNNING = -536870912;
  private static final int SHUTDOWN = 0;
  private static final int STOP = 536870912;
  private static final int TIDYING = 1073741824;
  private static final int TERMINATED = 1610612736;
  private final BlockingQueue<Runnable> workQueue;
  private final ReentrantLock mainLock = new ReentrantLock();
  private final HashSet<Worker> workers = new HashSet();
  private final Condition termination = this.mainLock.newCondition();
  private int largestPoolSize;
  private long completedTaskCount;
  private volatile ThreadFactory threadFactory;
  private volatile RejectedExecutionHandler handler;
  private volatile long keepAliveTime;
  private volatile boolean allowCoreThreadTimeOut;
  private volatile int corePoolSize;
  private volatile int maximumPoolSize;
  private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();
  private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");
  private static final boolean ONLY_ONE = true;
  
  private static int runStateOf(int paramInt)
  {
    return paramInt & 0xE0000000;
  }
  
  private static int workerCountOf(int paramInt)
  {
    return paramInt & 0x1FFFFFFF;
  }
  
  private static int ctlOf(int paramInt1, int paramInt2)
  {
    return paramInt1 | paramInt2;
  }
  
  private static boolean runStateLessThan(int paramInt1, int paramInt2)
  {
    return paramInt1 < paramInt2;
  }
  
  private static boolean runStateAtLeast(int paramInt1, int paramInt2)
  {
    return paramInt1 >= paramInt2;
  }
  
  private static boolean isRunning(int paramInt)
  {
    return paramInt < 0;
  }
  
  private boolean compareAndIncrementWorkerCount(int paramInt)
  {
    return this.ctl.compareAndSet(paramInt, paramInt + 1);
  }
  
  private boolean compareAndDecrementWorkerCount(int paramInt)
  {
    return this.ctl.compareAndSet(paramInt, paramInt - 1);
  }
  
  private void decrementWorkerCount()
  {
    while (!compareAndDecrementWorkerCount(this.ctl.get())) {}
  }
  
  private void advanceRunState(int paramInt)
  {
    for (;;)
    {
      int i = this.ctl.get();
      if ((runStateAtLeast(i, paramInt)) || (this.ctl.compareAndSet(i, ctlOf(paramInt, workerCountOf(i))))) {
        break;
      }
    }
  }
  
  final void tryTerminate()
  {
    for (;;)
    {
      int i = this.ctl.get();
      if ((isRunning(i)) || (runStateAtLeast(i, 1073741824)) || ((runStateOf(i) == 0) && (!this.workQueue.isEmpty()))) {
        return;
      }
      if (workerCountOf(i) != 0)
      {
        interruptIdleWorkers(true);
        return;
      }
      ReentrantLock localReentrantLock = this.mainLock;
      localReentrantLock.lock();
      try
      {
        if (this.ctl.compareAndSet(i, ctlOf(1073741824, 0))) {
          try
          {
            terminated();
            this.ctl.set(ctlOf(1610612736, 0));
            this.termination.signalAll();
          }
          finally
          {
            this.ctl.set(ctlOf(1610612736, 0));
            this.termination.signalAll();
          }
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
  }
  
  private void checkShutdownAccess()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(shutdownPerm);
      ReentrantLock localReentrantLock = this.mainLock;
      localReentrantLock.lock();
      try
      {
        Iterator localIterator = this.workers.iterator();
        while (localIterator.hasNext())
        {
          Worker localWorker = (Worker)localIterator.next();
          localSecurityManager.checkAccess(localWorker.thread);
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
  }
  
  private void interruptWorkers()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      Iterator localIterator = this.workers.iterator();
      while (localIterator.hasNext())
      {
        Worker localWorker = (Worker)localIterator.next();
        localWorker.interruptIfStarted();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private void interruptIdleWorkers(boolean paramBoolean)
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      Iterator localIterator = this.workers.iterator();
      while (localIterator.hasNext())
      {
        Worker localWorker = (Worker)localIterator.next();
        Thread localThread = localWorker.thread;
        if ((!localThread.isInterrupted()) && (localWorker.tryLock())) {
          try {}catch (SecurityException localSecurityException) {}finally {}
        }
        if (paramBoolean) {
          break;
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private void interruptIdleWorkers()
  {
    interruptIdleWorkers(false);
  }
  
  final void reject(Runnable paramRunnable)
  {
    this.handler.rejectedExecution(paramRunnable, this);
  }
  
  void onShutdown() {}
  
  final boolean isRunningOrShutdown(boolean paramBoolean)
  {
    int i = runStateOf(this.ctl.get());
    return (i == -536870912) || ((i == 0) && (paramBoolean));
  }
  
  private List<Runnable> drainQueue()
  {
    BlockingQueue localBlockingQueue = this.workQueue;
    ArrayList localArrayList = new ArrayList();
    localBlockingQueue.drainTo(localArrayList);
    if (!localBlockingQueue.isEmpty()) {
      for (Runnable localRunnable : (Runnable[])localBlockingQueue.toArray(new Runnable[0])) {
        if (localBlockingQueue.remove(localRunnable)) {
          localArrayList.add(localRunnable);
        }
      }
    }
    return localArrayList;
  }
  
  private boolean addWorker(Runnable paramRunnable, boolean paramBoolean)
  {
    int i = this.ctl.get();
    int j = runStateOf(i);
    if ((j >= 0) && ((j != 0) || (paramRunnable != null) || (this.workQueue.isEmpty()))) {
      return false;
    }
    for (;;)
    {
      int k = workerCountOf(i);
      if (k < 536870911)
      {
        if (k < (paramBoolean ? this.corePoolSize : this.maximumPoolSize)) {}
      }
      else {
        return false;
      }
      if (compareAndIncrementWorkerCount(i)) {
        break label111;
      }
      i = this.ctl.get();
      if (runStateOf(i) != j) {
        break;
      }
    }
    label111:
    i = 0;
    j = 0;
    Worker localWorker = null;
    try
    {
      localWorker = new Worker(paramRunnable);
      Thread localThread = localWorker.thread;
      if (localThread != null)
      {
        ReentrantLock localReentrantLock = this.mainLock;
        localReentrantLock.lock();
        try
        {
          int m = runStateOf(this.ctl.get());
          if ((m < 0) || ((m == 0) && (paramRunnable == null)))
          {
            if (localThread.isAlive()) {
              throw new IllegalThreadStateException();
            }
            this.workers.add(localWorker);
            int n = this.workers.size();
            if (n > this.largestPoolSize) {
              this.largestPoolSize = n;
            }
            j = 1;
          }
        }
        finally
        {
          localReentrantLock.unlock();
        }
        if (j != 0)
        {
          localThread.start();
          i = 1;
        }
      }
    }
    finally
    {
      if (i == 0) {
        addWorkerFailed(localWorker);
      }
    }
    return i;
  }
  
  private void addWorkerFailed(Worker paramWorker)
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      if (paramWorker != null) {
        this.workers.remove(paramWorker);
      }
      decrementWorkerCount();
      tryTerminate();
      localReentrantLock.unlock();
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private void processWorkerExit(Worker paramWorker, boolean paramBoolean)
  {
    if (paramBoolean) {
      decrementWorkerCount();
    }
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      this.completedTaskCount += paramWorker.completedTasks;
      this.workers.remove(paramWorker);
    }
    finally
    {
      localReentrantLock.unlock();
    }
    tryTerminate();
    int i = this.ctl.get();
    if (runStateLessThan(i, 536870912))
    {
      if (!paramBoolean)
      {
        int j = this.allowCoreThreadTimeOut ? 0 : this.corePoolSize;
        if ((j == 0) && (!this.workQueue.isEmpty())) {
          j = 1;
        }
        if (workerCountOf(i) >= j) {
          return;
        }
      }
      addWorker(null, false);
    }
  }
  
  private Runnable getTask()
  {
    int i = 0;
    for (;;)
    {
      int j = this.ctl.get();
      int k = runStateOf(j);
      if ((k >= 0) && ((k >= 536870912) || (this.workQueue.isEmpty())))
      {
        decrementWorkerCount();
        return null;
      }
      int m = workerCountOf(j);
      int n = (this.allowCoreThreadTimeOut) || (m > this.corePoolSize) ? 1 : 0;
      if (((m > this.maximumPoolSize) || ((n != 0) && (i != 0))) && ((m > 1) || (this.workQueue.isEmpty())))
      {
        if (compareAndDecrementWorkerCount(j)) {
          return null;
        }
      }
      else {
        try
        {
          Runnable localRunnable = n != 0 ? (Runnable)this.workQueue.poll(this.keepAliveTime, TimeUnit.NANOSECONDS) : (Runnable)this.workQueue.take();
          if (localRunnable != null) {
            return localRunnable;
          }
          i = 1;
        }
        catch (InterruptedException localInterruptedException)
        {
          i = 0;
        }
      }
    }
  }
  
  final void runWorker(Worker paramWorker)
  {
    Thread localThread = Thread.currentThread();
    Runnable localRunnable = paramWorker.firstTask;
    paramWorker.firstTask = null;
    paramWorker.unlock();
    boolean bool = true;
    try
    {
      while ((localRunnable != null) || ((localRunnable = getTask()) != null))
      {
        paramWorker.lock();
        if (((runStateAtLeast(this.ctl.get(), 536870912)) || ((Thread.interrupted()) && (runStateAtLeast(this.ctl.get(), 536870912)))) && (!localThread.isInterrupted())) {
          localThread.interrupt();
        }
        try
        {
          beforeExecute(localThread, localRunnable);
          Object localObject1 = null;
          try
          {
            localRunnable.run();
          }
          catch (RuntimeException localRuntimeException)
          {
            localObject1 = localRuntimeException;
            throw localRuntimeException;
          }
          catch (Error localError)
          {
            localObject1 = localError;
            throw localError;
          }
          catch (Throwable localThrowable)
          {
            localObject1 = localThrowable;
            throw new Error(localThrowable);
          }
          finally
          {
            afterExecute(localRunnable, (Throwable)localObject1);
          }
          localRunnable = null;
          paramWorker.completedTasks += 1L;
          paramWorker.unlock();
        }
        finally
        {
          localRunnable = null;
          paramWorker.completedTasks += 1L;
          paramWorker.unlock();
        }
      }
      bool = false;
    }
    finally
    {
      processWorkerExit(paramWorker, bool);
    }
  }
  
  public ThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, BlockingQueue<Runnable> paramBlockingQueue)
  {
    this(paramInt1, paramInt2, paramLong, paramTimeUnit, paramBlockingQueue, Executors.defaultThreadFactory(), defaultHandler);
  }
  
  public ThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, BlockingQueue<Runnable> paramBlockingQueue, ThreadFactory paramThreadFactory)
  {
    this(paramInt1, paramInt2, paramLong, paramTimeUnit, paramBlockingQueue, paramThreadFactory, defaultHandler);
  }
  
  public ThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, BlockingQueue<Runnable> paramBlockingQueue, RejectedExecutionHandler paramRejectedExecutionHandler)
  {
    this(paramInt1, paramInt2, paramLong, paramTimeUnit, paramBlockingQueue, Executors.defaultThreadFactory(), paramRejectedExecutionHandler);
  }
  
  public ThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, BlockingQueue<Runnable> paramBlockingQueue, ThreadFactory paramThreadFactory, RejectedExecutionHandler paramRejectedExecutionHandler)
  {
    if ((paramInt1 < 0) || (paramInt2 <= 0) || (paramInt2 < paramInt1) || (paramLong < 0L)) {
      throw new IllegalArgumentException();
    }
    if ((paramBlockingQueue == null) || (paramThreadFactory == null) || (paramRejectedExecutionHandler == null)) {
      throw new NullPointerException();
    }
    this.corePoolSize = paramInt1;
    this.maximumPoolSize = paramInt2;
    this.workQueue = paramBlockingQueue;
    this.keepAliveTime = paramTimeUnit.toNanos(paramLong);
    this.threadFactory = paramThreadFactory;
    this.handler = paramRejectedExecutionHandler;
  }
  
  public void execute(Runnable paramRunnable)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    int i = this.ctl.get();
    if (workerCountOf(i) < this.corePoolSize)
    {
      if (addWorker(paramRunnable, true)) {
        return;
      }
      i = this.ctl.get();
    }
    if ((isRunning(i)) && (this.workQueue.offer(paramRunnable)))
    {
      int j = this.ctl.get();
      if ((!isRunning(j)) && (remove(paramRunnable))) {
        reject(paramRunnable);
      } else if (workerCountOf(j) == 0) {
        addWorker(null, false);
      }
    }
    else if (!addWorker(paramRunnable, false))
    {
      reject(paramRunnable);
    }
  }
  
  public void shutdown()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      checkShutdownAccess();
      advanceRunState(0);
      interruptIdleWorkers();
      onShutdown();
      localReentrantLock.unlock();
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public List<Runnable> shutdownNow()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    List localList;
    try
    {
      checkShutdownAccess();
      advanceRunState(536870912);
      interruptWorkers();
      localList = drainQueue();
      localReentrantLock.unlock();
    }
    finally
    {
      localReentrantLock.unlock();
    }
    return localList;
  }
  
  public boolean isShutdown()
  {
    return !isRunning(this.ctl.get());
  }
  
  public boolean isTerminating()
  {
    int i = this.ctl.get();
    return (!isRunning(i)) && (runStateLessThan(i, 1610612736));
  }
  
  public boolean isTerminated()
  {
    return runStateAtLeast(this.ctl.get(), 1610612736);
  }
  
  /* Error */
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_3
    //   1: lload_1
    //   2: invokevirtual 502	java/util/concurrent/TimeUnit:toNanos	(J)J
    //   5: lstore 4
    //   7: aload_0
    //   8: getfield 430	java/util/concurrent/ThreadPoolExecutor:mainLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   11: astore 6
    //   13: aload 6
    //   15: invokevirtual 509	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   18: aload_0
    //   19: getfield 428	java/util/concurrent/ThreadPoolExecutor:ctl	Ljava/util/concurrent/atomic/AtomicInteger;
    //   22: invokevirtual 504	java/util/concurrent/atomic/AtomicInteger:get	()I
    //   25: ldc 5
    //   27: invokestatic 482	java/util/concurrent/ThreadPoolExecutor:runStateAtLeast	(II)Z
    //   30: ifeq +14 -> 44
    //   33: iconst_1
    //   34: istore 7
    //   36: aload 6
    //   38: invokevirtual 510	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   41: iload 7
    //   43: ireturn
    //   44: lload 4
    //   46: lconst_0
    //   47: lcmp
    //   48: ifgt +14 -> 62
    //   51: iconst_0
    //   52: istore 7
    //   54: aload 6
    //   56: invokevirtual 510	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   59: iload 7
    //   61: ireturn
    //   62: aload_0
    //   63: getfield 429	java/util/concurrent/ThreadPoolExecutor:termination	Ljava/util/concurrent/locks/Condition;
    //   66: lload 4
    //   68: invokeinterface 529 3 0
    //   73: lstore 4
    //   75: goto -57 -> 18
    //   78: astore 8
    //   80: aload 6
    //   82: invokevirtual 510	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   85: aload 8
    //   87: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	ThreadPoolExecutor
    //   0	88	1	paramLong	long
    //   0	88	3	paramTimeUnit	TimeUnit
    //   5	69	4	l	long
    //   11	70	6	localReentrantLock	ReentrantLock
    //   34	26	7	bool	boolean
    //   78	8	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   18	36	78	finally
    //   44	54	78	finally
    //   62	80	78	finally
  }
  
  protected void finalize()
  {
    shutdown();
  }
  
  public void setThreadFactory(ThreadFactory paramThreadFactory)
  {
    if (paramThreadFactory == null) {
      throw new NullPointerException();
    }
    this.threadFactory = paramThreadFactory;
  }
  
  public ThreadFactory getThreadFactory()
  {
    return this.threadFactory;
  }
  
  public void setRejectedExecutionHandler(RejectedExecutionHandler paramRejectedExecutionHandler)
  {
    if (paramRejectedExecutionHandler == null) {
      throw new NullPointerException();
    }
    this.handler = paramRejectedExecutionHandler;
  }
  
  public RejectedExecutionHandler getRejectedExecutionHandler()
  {
    return this.handler;
  }
  
  public void setCorePoolSize(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    int i = paramInt - this.corePoolSize;
    this.corePoolSize = paramInt;
    if (workerCountOf(this.ctl.get()) > paramInt)
    {
      interruptIdleWorkers();
    }
    else if (i > 0)
    {
      int j = Math.min(i, this.workQueue.size());
      while ((j-- > 0) && (addWorker(null, true))) {
        if (this.workQueue.isEmpty()) {
          break;
        }
      }
    }
  }
  
  public int getCorePoolSize()
  {
    return this.corePoolSize;
  }
  
  public boolean prestartCoreThread()
  {
    return (workerCountOf(this.ctl.get()) < this.corePoolSize) && (addWorker(null, true));
  }
  
  void ensurePrestart()
  {
    int i = workerCountOf(this.ctl.get());
    if (i < this.corePoolSize) {
      addWorker(null, true);
    } else if (i == 0) {
      addWorker(null, false);
    }
  }
  
  public int prestartAllCoreThreads()
  {
    for (int i = 0; addWorker(null, true); i++) {}
    return i;
  }
  
  public boolean allowsCoreThreadTimeOut()
  {
    return this.allowCoreThreadTimeOut;
  }
  
  public void allowCoreThreadTimeOut(boolean paramBoolean)
  {
    if ((paramBoolean) && (this.keepAliveTime <= 0L)) {
      throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
    }
    if (paramBoolean != this.allowCoreThreadTimeOut)
    {
      this.allowCoreThreadTimeOut = paramBoolean;
      if (paramBoolean) {
        interruptIdleWorkers();
      }
    }
  }
  
  public void setMaximumPoolSize(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt < this.corePoolSize)) {
      throw new IllegalArgumentException();
    }
    this.maximumPoolSize = paramInt;
    if (workerCountOf(this.ctl.get()) > paramInt) {
      interruptIdleWorkers();
    }
  }
  
  public int getMaximumPoolSize()
  {
    return this.maximumPoolSize;
  }
  
  public void setKeepAliveTime(long paramLong, TimeUnit paramTimeUnit)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException();
    }
    if ((paramLong == 0L) && (allowsCoreThreadTimeOut())) {
      throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
    }
    long l1 = paramTimeUnit.toNanos(paramLong);
    long l2 = l1 - this.keepAliveTime;
    this.keepAliveTime = l1;
    if (l2 < 0L) {
      interruptIdleWorkers();
    }
  }
  
  public long getKeepAliveTime(TimeUnit paramTimeUnit)
  {
    return paramTimeUnit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS);
  }
  
  public BlockingQueue<Runnable> getQueue()
  {
    return this.workQueue;
  }
  
  public boolean remove(Runnable paramRunnable)
  {
    boolean bool = this.workQueue.remove(paramRunnable);
    tryTerminate();
    return bool;
  }
  
  public void purge()
  {
    BlockingQueue localBlockingQueue = this.workQueue;
    Object localObject1;
    int i;
    int j;
    try
    {
      Iterator localIterator = localBlockingQueue.iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (Runnable)localIterator.next();
        if (((localObject1 instanceof Future)) && (((Future)localObject1).isCancelled())) {
          localIterator.remove();
        }
      }
    }
    catch (ConcurrentModificationException localConcurrentModificationException)
    {
      localObject1 = localBlockingQueue.toArray();
      i = localObject1.length;
      j = 0;
    }
    while (j < i)
    {
      Object localObject2 = localObject1[j];
      if (((localObject2 instanceof Future)) && (((Future)localObject2).isCancelled())) {
        localBlockingQueue.remove(localObject2);
      }
      j++;
    }
    tryTerminate();
  }
  
  public int getPoolSize()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      int i = runStateAtLeast(this.ctl.get(), 1073741824) ? 0 : this.workers.size();
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int getActiveCount()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      Iterator localIterator1 = 0;
      Iterator localIterator2 = this.workers.iterator();
      while (localIterator2.hasNext())
      {
        Worker localWorker = (Worker)localIterator2.next();
        if (localWorker.isLocked()) {
          localIterator1++;
        }
      }
      localIterator2 = localIterator1;
      return localIterator2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int getLargestPoolSize()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      int i = this.largestPoolSize;
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public long getTaskCount()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      long l1 = this.completedTaskCount;
      Iterator localIterator = this.workers.iterator();
      while (localIterator.hasNext())
      {
        Worker localWorker = (Worker)localIterator.next();
        l1 += localWorker.completedTasks;
        if (localWorker.isLocked()) {
          l1 += 1L;
        }
      }
      long l2 = l1 + this.workQueue.size();
      return l2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public long getCompletedTaskCount()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    try
    {
      long l1 = this.completedTaskCount;
      Iterator localIterator = this.workers.iterator();
      while (localIterator.hasNext())
      {
        Worker localWorker = (Worker)localIterator.next();
        l1 += localWorker.completedTasks;
      }
      long l2 = l1;
      return l2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public String toString()
  {
    ReentrantLock localReentrantLock = this.mainLock;
    localReentrantLock.lock();
    long l;
    int j;
    int i;
    try
    {
      l = this.completedTaskCount;
      j = 0;
      i = this.workers.size();
      Iterator localIterator = this.workers.iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (Worker)localIterator.next();
        l += ((Worker)localObject1).completedTasks;
        if (((Worker)localObject1).isLocked()) {
          j++;
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    int k = this.ctl.get();
    Object localObject1 = runStateAtLeast(k, 1610612736) ? "Terminated" : runStateLessThan(k, 0) ? "Running" : "Shutting down";
    return super.toString() + "[" + (String)localObject1 + ", pool size = " + i + ", active threads = " + j + ", queued tasks = " + this.workQueue.size() + ", completed tasks = " + l + "]";
  }
  
  protected void beforeExecute(Thread paramThread, Runnable paramRunnable) {}
  
  protected void afterExecute(Runnable paramRunnable, Throwable paramThrowable) {}
  
  protected void terminated() {}
  
  public static class AbortPolicy
    implements RejectedExecutionHandler
  {
    public AbortPolicy() {}
    
    public void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor)
    {
      throw new RejectedExecutionException("Task " + paramRunnable.toString() + " rejected from " + paramThreadPoolExecutor.toString());
    }
  }
  
  public static class CallerRunsPolicy
    implements RejectedExecutionHandler
  {
    public CallerRunsPolicy() {}
    
    public void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor)
    {
      if (!paramThreadPoolExecutor.isShutdown()) {
        paramRunnable.run();
      }
    }
  }
  
  public static class DiscardOldestPolicy
    implements RejectedExecutionHandler
  {
    public DiscardOldestPolicy() {}
    
    public void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor)
    {
      if (!paramThreadPoolExecutor.isShutdown())
      {
        paramThreadPoolExecutor.getQueue().poll();
        paramThreadPoolExecutor.execute(paramRunnable);
      }
    }
  }
  
  public static class DiscardPolicy
    implements RejectedExecutionHandler
  {
    public DiscardPolicy() {}
    
    public void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor) {}
  }
  
  private final class Worker
    extends AbstractQueuedSynchronizer
    implements Runnable
  {
    private static final long serialVersionUID = 6138294804551838833L;
    final Thread thread;
    Runnable firstTask;
    volatile long completedTasks;
    
    Worker(Runnable paramRunnable)
    {
      setState(-1);
      this.firstTask = paramRunnable;
      this.thread = ThreadPoolExecutor.this.getThreadFactory().newThread(this);
    }
    
    public void run()
    {
      ThreadPoolExecutor.this.runWorker(this);
    }
    
    protected boolean isHeldExclusively()
    {
      return getState() != 0;
    }
    
    protected boolean tryAcquire(int paramInt)
    {
      if (compareAndSetState(0, 1))
      {
        setExclusiveOwnerThread(Thread.currentThread());
        return true;
      }
      return false;
    }
    
    protected boolean tryRelease(int paramInt)
    {
      setExclusiveOwnerThread(null);
      setState(0);
      return true;
    }
    
    public void lock()
    {
      acquire(1);
    }
    
    public boolean tryLock()
    {
      return tryAcquire(1);
    }
    
    public void unlock()
    {
      release(1);
    }
    
    public boolean isLocked()
    {
      return isHeldExclusively();
    }
    
    void interruptIfStarted()
    {
      Thread localThread;
      if ((getState() >= 0) && ((localThread = this.thread) != null) && (!localThread.isInterrupted())) {
        try
        {
          localThread.interrupt();
        }
        catch (SecurityException localSecurityException) {}
      }
    }
  }
}
