package java.util.concurrent.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract interface Condition
{
  public abstract void await()
    throws InterruptedException;
  
  public abstract void awaitUninterruptibly();
  
  public abstract long awaitNanos(long paramLong)
    throws InterruptedException;
  
  public abstract boolean await(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;
  
  public abstract boolean awaitUntil(Date paramDate)
    throws InterruptedException;
  
  public abstract void signal();
  
  public abstract void signalAll();
}
