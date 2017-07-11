package java.util.concurrent;

public class ExecutorCompletionService<V>
  implements CompletionService<V>
{
  private final Executor executor;
  private final AbstractExecutorService aes;
  private final BlockingQueue<Future<V>> completionQueue;
  
  private RunnableFuture<V> newTaskFor(Callable<V> paramCallable)
  {
    if (this.aes == null) {
      return new FutureTask(paramCallable);
    }
    return this.aes.newTaskFor(paramCallable);
  }
  
  private RunnableFuture<V> newTaskFor(Runnable paramRunnable, V paramV)
  {
    if (this.aes == null) {
      return new FutureTask(paramRunnable, paramV);
    }
    return this.aes.newTaskFor(paramRunnable, paramV);
  }
  
  public ExecutorCompletionService(Executor paramExecutor)
  {
    if (paramExecutor == null) {
      throw new NullPointerException();
    }
    this.executor = paramExecutor;
    this.aes = ((paramExecutor instanceof AbstractExecutorService) ? (AbstractExecutorService)paramExecutor : null);
    this.completionQueue = new LinkedBlockingQueue();
  }
  
  public ExecutorCompletionService(Executor paramExecutor, BlockingQueue<Future<V>> paramBlockingQueue)
  {
    if ((paramExecutor == null) || (paramBlockingQueue == null)) {
      throw new NullPointerException();
    }
    this.executor = paramExecutor;
    this.aes = ((paramExecutor instanceof AbstractExecutorService) ? (AbstractExecutorService)paramExecutor : null);
    this.completionQueue = paramBlockingQueue;
  }
  
  public Future<V> submit(Callable<V> paramCallable)
  {
    if (paramCallable == null) {
      throw new NullPointerException();
    }
    RunnableFuture localRunnableFuture = newTaskFor(paramCallable);
    this.executor.execute(new QueueingFuture(localRunnableFuture));
    return localRunnableFuture;
  }
  
  public Future<V> submit(Runnable paramRunnable, V paramV)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    RunnableFuture localRunnableFuture = newTaskFor(paramRunnable, paramV);
    this.executor.execute(new QueueingFuture(localRunnableFuture));
    return localRunnableFuture;
  }
  
  public Future<V> take()
    throws InterruptedException
  {
    return (Future)this.completionQueue.take();
  }
  
  public Future<V> poll()
  {
    return (Future)this.completionQueue.poll();
  }
  
  public Future<V> poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return (Future)this.completionQueue.poll(paramLong, paramTimeUnit);
  }
  
  private class QueueingFuture
    extends FutureTask<Void>
  {
    private final Future<V> task;
    
    QueueingFuture()
    {
      super(null);
      this.task = localRunnable;
    }
    
    protected void done()
    {
      ExecutorCompletionService.this.completionQueue.add(this.task);
    }
  }
}
