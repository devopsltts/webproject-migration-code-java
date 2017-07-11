package java.awt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.awt.PeerEvent;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

class WaitDispatchSupport
  implements SecondaryLoop
{
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.event.WaitDispatchSupport");
  private EventDispatchThread dispatchThread;
  private EventFilter filter;
  private volatile Conditional extCondition;
  private volatile Conditional condition;
  private long interval;
  private static Timer timer;
  private TimerTask timerTask;
  private AtomicBoolean keepBlockingEDT = new AtomicBoolean(false);
  private AtomicBoolean keepBlockingCT = new AtomicBoolean(false);
  private final Runnable wakingRunnable = new Runnable()
  {
    public void run()
    {
      WaitDispatchSupport.log.fine("Wake up EDT");
      synchronized (WaitDispatchSupport.access$900())
      {
        WaitDispatchSupport.this.keepBlockingCT.set(false);
        WaitDispatchSupport.access$900().notifyAll();
      }
      WaitDispatchSupport.log.fine("Wake up EDT done");
    }
  };
  
  private static synchronized void initializeTimer()
  {
    if (timer == null) {
      timer = new Timer("AWT-WaitDispatchSupport-Timer", true);
    }
  }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread)
  {
    this(paramEventDispatchThread, null);
  }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread, Conditional paramConditional)
  {
    if (paramEventDispatchThread == null) {
      throw new IllegalArgumentException("The dispatchThread can not be null");
    }
    this.dispatchThread = paramEventDispatchThread;
    this.extCondition = paramConditional;
    this.condition = new Conditional()
    {
      public boolean evaluate()
      {
        if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINEST)) {
          WaitDispatchSupport.log.finest("evaluate(): blockingEDT=" + WaitDispatchSupport.this.keepBlockingEDT.get() + ", blockingCT=" + WaitDispatchSupport.this.keepBlockingCT.get());
        }
        int i = WaitDispatchSupport.this.extCondition != null ? WaitDispatchSupport.this.extCondition.evaluate() : 1;
        if ((!WaitDispatchSupport.this.keepBlockingEDT.get()) || (i == 0))
        {
          if (WaitDispatchSupport.this.timerTask != null)
          {
            WaitDispatchSupport.this.timerTask.cancel();
            WaitDispatchSupport.this.timerTask = null;
          }
          return false;
        }
        return true;
      }
    };
  }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread, Conditional paramConditional, EventFilter paramEventFilter, long paramLong)
  {
    this(paramEventDispatchThread, paramConditional);
    this.filter = paramEventFilter;
    if (paramLong < 0L) {
      throw new IllegalArgumentException("The interval value must be >= 0");
    }
    this.interval = paramLong;
    if (paramLong != 0L) {
      initializeTimer();
    }
  }
  
  public boolean enter()
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("enter(): blockingEDT=" + this.keepBlockingEDT.get() + ", blockingCT=" + this.keepBlockingCT.get());
    }
    if (!this.keepBlockingEDT.compareAndSet(false, true))
    {
      log.fine("The secondary loop is already running, aborting");
      return false;
    }
    final Runnable local2 = new Runnable()
    {
      public void run()
      {
        WaitDispatchSupport.log.fine("Starting a new event pump");
        if (WaitDispatchSupport.this.filter == null) {
          WaitDispatchSupport.this.dispatchThread.pumpEvents(WaitDispatchSupport.this.condition);
        } else {
          WaitDispatchSupport.this.dispatchThread.pumpEventsForFilter(WaitDispatchSupport.this.condition, WaitDispatchSupport.this.filter);
        }
      }
    };
    Thread localThread = Thread.currentThread();
    if (localThread == this.dispatchThread)
    {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
        log.finest("On dispatch thread: " + this.dispatchThread);
      }
      if (this.interval != 0L)
      {
        if (log.isLoggable(PlatformLogger.Level.FINEST)) {
          log.finest("scheduling the timer for " + this.interval + " ms");
        }
        timer.schedule(this.timerTask = new TimerTask()
        {
          public void run()
          {
            if (WaitDispatchSupport.this.keepBlockingEDT.compareAndSet(true, false)) {
              WaitDispatchSupport.this.wakeupEDT();
            }
          }
        }, this.interval);
      }
      SequencedEvent localSequencedEvent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
      if (localSequencedEvent != null)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("Dispose current SequencedEvent: " + localSequencedEvent);
        }
        localSequencedEvent.dispose();
      }
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          local2.run();
          return null;
        }
      });
    }
    else
    {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
        log.finest("On non-dispatch thread: " + localThread);
      }
      synchronized (getTreeLock())
      {
        if (this.filter != null) {
          this.dispatchThread.addEventFilter(this.filter);
        }
        try
        {
          EventQueue localEventQueue = this.dispatchThread.getEventQueue();
          localEventQueue.postEvent(new PeerEvent(this, local2, 1L));
          this.keepBlockingCT.set(true);
          if (this.interval > 0L)
          {
            long l = System.currentTimeMillis();
            while ((this.keepBlockingCT.get()) && ((this.extCondition == null) || (this.extCondition.evaluate())) && (l + this.interval > System.currentTimeMillis())) {
              getTreeLock().wait(this.interval);
            }
          }
          else
          {
            while ((this.keepBlockingCT.get()) && ((this.extCondition == null) || (this.extCondition.evaluate()))) {
              getTreeLock().wait();
            }
          }
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("waitDone " + this.keepBlockingEDT.get() + " " + this.keepBlockingCT.get());
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("Exception caught while waiting: " + localInterruptedException);
          }
        }
        finally
        {
          if (this.filter != null) {
            this.dispatchThread.removeEventFilter(this.filter);
          }
        }
        this.keepBlockingEDT.set(false);
        this.keepBlockingCT.set(false);
      }
    }
    return true;
  }
  
  public boolean exit()
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("exit(): blockingEDT=" + this.keepBlockingEDT.get() + ", blockingCT=" + this.keepBlockingCT.get());
    }
    if (this.keepBlockingEDT.compareAndSet(true, false))
    {
      wakeupEDT();
      return true;
    }
    return false;
  }
  
  private static final Object getTreeLock()
  {
    return Component.LOCK;
  }
  
  private void wakeupEDT()
  {
    if (log.isLoggable(PlatformLogger.Level.FINEST)) {
      log.finest("wakeupEDT(): EDT == " + this.dispatchThread);
    }
    EventQueue localEventQueue = this.dispatchThread.getEventQueue();
    localEventQueue.postEvent(new PeerEvent(this, this.wakingRunnable, 1L));
  }
}
