package java.util.concurrent;

public abstract interface RejectedExecutionHandler
{
  public abstract void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor);
}
