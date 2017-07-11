package sun.misc;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.SortedSet;
import java.util.TreeSet;

public class GC
{
  private static final long NO_TARGET = Long.MAX_VALUE;
  private static long latencyTarget = Long.MAX_VALUE;
  private static Thread daemon = null;
  private static Object lock = new LatencyLock(null);
  
  private GC() {}
  
  public static native long maxObjectInspectionAge();
  
  private static void setLatencyTarget(long paramLong)
  {
    latencyTarget = paramLong;
    if (daemon == null) {
      Daemon.create();
    } else {
      lock.notify();
    }
  }
  
  public static LatencyRequest requestLatency(long paramLong)
  {
    return new LatencyRequest(paramLong, null);
  }
  
  public static long currentLatencyTarget()
  {
    long l = latencyTarget;
    return l == Long.MAX_VALUE ? 0L : l;
  }
  
  private static class Daemon
    extends Thread
  {
    public void run()
    {
      for (;;)
      {
        synchronized (GC.lock)
        {
          long l1 = GC.latencyTarget;
          if (l1 == Long.MAX_VALUE)
          {
            GC.access$302(null);
            return;
          }
          long l2 = GC.maxObjectInspectionAge();
          if (l2 >= l1)
          {
            System.gc();
            l2 = 0L;
          }
          try
          {
            GC.lock.wait(l1 - l2);
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }
    }
    
    private Daemon(ThreadGroup paramThreadGroup)
    {
      super("GC Daemon");
    }
    
    public static void create()
    {
      PrivilegedAction local1 = new PrivilegedAction()
      {
        public Void run()
        {
          Object localObject1 = Thread.currentThread().getThreadGroup();
          for (Object localObject2 = localObject1; localObject2 != null; localObject2 = ((ThreadGroup)localObject1).getParent()) {
            localObject1 = localObject2;
          }
          localObject2 = new GC.Daemon((ThreadGroup)localObject1, null);
          ((GC.Daemon)localObject2).setDaemon(true);
          ((GC.Daemon)localObject2).setPriority(2);
          ((GC.Daemon)localObject2).start();
          GC.access$302((Thread)localObject2);
          return null;
        }
      };
      AccessController.doPrivileged(local1);
    }
  }
  
  private static class LatencyLock
  {
    private LatencyLock() {}
  }
  
  public static class LatencyRequest
    implements Comparable<LatencyRequest>
  {
    private static long counter = 0L;
    private static SortedSet<LatencyRequest> requests = null;
    private long latency;
    private long id;
    
    private static void adjustLatencyIfNeeded()
    {
      if ((requests == null) || (requests.isEmpty()))
      {
        if (GC.latencyTarget != Long.MAX_VALUE) {
          GC.setLatencyTarget(Long.MAX_VALUE);
        }
      }
      else
      {
        LatencyRequest localLatencyRequest = (LatencyRequest)requests.first();
        if (localLatencyRequest.latency != GC.latencyTarget) {
          GC.setLatencyTarget(localLatencyRequest.latency);
        }
      }
    }
    
    private LatencyRequest(long paramLong)
    {
      if (paramLong <= 0L) {
        throw new IllegalArgumentException("Non-positive latency: " + paramLong);
      }
      this.latency = paramLong;
      synchronized (GC.lock)
      {
        this.id = (++counter);
        if (requests == null) {
          requests = new TreeSet();
        }
        requests.add(this);
        adjustLatencyIfNeeded();
      }
    }
    
    public void cancel()
    {
      synchronized (GC.lock)
      {
        if (this.latency == Long.MAX_VALUE) {
          throw new IllegalStateException("Request already cancelled");
        }
        if (!requests.remove(this)) {
          throw new InternalError("Latency request " + this + " not found");
        }
        if (requests.isEmpty()) {
          requests = null;
        }
        this.latency = Long.MAX_VALUE;
        adjustLatencyIfNeeded();
      }
    }
    
    public int compareTo(LatencyRequest paramLatencyRequest)
    {
      long l = this.latency - paramLatencyRequest.latency;
      if (l == 0L) {
        l = this.id - paramLatencyRequest.id;
      }
      return l > 0L ? 1 : l < 0L ? -1 : 0;
    }
    
    public String toString()
    {
      return LatencyRequest.class.getName() + "[" + this.latency + "," + this.id + "]";
    }
  }
}
