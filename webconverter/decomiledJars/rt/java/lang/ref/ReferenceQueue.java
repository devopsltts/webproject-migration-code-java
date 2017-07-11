package java.lang.ref;

import sun.misc.VM;

public class ReferenceQueue<T>
{
  static ReferenceQueue<Object> NULL = new Null(null);
  static ReferenceQueue<Object> ENQUEUED = new Null(null);
  private Lock lock = new Lock(null);
  private volatile Reference<? extends T> head = null;
  private long queueLength = 0L;
  
  public ReferenceQueue() {}
  
  boolean enqueue(Reference<? extends T> paramReference)
  {
    synchronized (this.lock)
    {
      ReferenceQueue localReferenceQueue = paramReference.queue;
      if ((localReferenceQueue == NULL) || (localReferenceQueue == ENQUEUED)) {
        return false;
      }
      assert (localReferenceQueue == this);
      paramReference.queue = ENQUEUED;
      paramReference.next = (this.head == null ? paramReference : this.head);
      this.head = paramReference;
      this.queueLength += 1L;
      if ((paramReference instanceof FinalReference)) {
        VM.addFinalRefCount(1);
      }
      this.lock.notifyAll();
      return true;
    }
  }
  
  private Reference<? extends T> reallyPoll()
  {
    Reference localReference = this.head;
    if (localReference != null)
    {
      this.head = (localReference.next == localReference ? null : localReference.next);
      localReference.queue = NULL;
      localReference.next = localReference;
      this.queueLength -= 1L;
      if ((localReference instanceof FinalReference)) {
        VM.addFinalRefCount(-1);
      }
      return localReference;
    }
    return null;
  }
  
  public Reference<? extends T> poll()
  {
    if (this.head == null) {
      return null;
    }
    synchronized (this.lock)
    {
      return reallyPoll();
    }
  }
  
  public Reference<? extends T> remove(long paramLong)
    throws IllegalArgumentException, InterruptedException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative timeout value");
    }
    synchronized (this.lock)
    {
      Reference localReference = reallyPoll();
      if (localReference != null) {
        return localReference;
      }
      long l1 = paramLong == 0L ? 0L : System.nanoTime();
      do
      {
        this.lock.wait(paramLong);
        localReference = reallyPoll();
        if (localReference != null) {
          return localReference;
        }
      } while (paramLong == 0L);
      long l2 = System.nanoTime();
      paramLong -= (l2 - l1) / 1000000L;
      if (paramLong <= 0L) {
        return null;
      }
      l1 = l2;
    }
  }
  
  public Reference<? extends T> remove()
    throws InterruptedException
  {
    return remove(0L);
  }
  
  private static class Lock
  {
    private Lock() {}
  }
  
  private static class Null<S>
    extends ReferenceQueue<S>
  {
    private Null() {}
    
    boolean enqueue(Reference<? extends S> paramReference)
    {
      return false;
    }
  }
}
