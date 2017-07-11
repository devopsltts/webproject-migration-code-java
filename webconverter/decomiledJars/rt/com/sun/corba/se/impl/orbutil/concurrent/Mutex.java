package com.sun.corba.se.impl.orbutil.concurrent;

public class Mutex
  implements Sync
{
  protected boolean inuse_ = false;
  
  public Mutex() {}
  
  public void acquire()
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this)
    {
      try
      {
        while (this.inuse_) {
          wait();
        }
        this.inuse_ = true;
      }
      catch (InterruptedException localInterruptedException)
      {
        notify();
        throw localInterruptedException;
      }
    }
  }
  
  public synchronized void release()
  {
    this.inuse_ = false;
    notify();
  }
  
  public boolean attempt(long paramLong)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this)
    {
      if (!this.inuse_)
      {
        this.inuse_ = true;
        return true;
      }
      if (paramLong <= 0L) {
        return false;
      }
      long l1 = paramLong;
      long l2 = System.currentTimeMillis();
      try
      {
        do
        {
          wait(l1);
          if (!this.inuse_)
          {
            this.inuse_ = true;
            return true;
          }
          l1 = paramLong - (System.currentTimeMillis() - l2);
        } while (l1 > 0L);
        return false;
      }
      catch (InterruptedException localInterruptedException)
      {
        notify();
        throw localInterruptedException;
      }
    }
  }
}
