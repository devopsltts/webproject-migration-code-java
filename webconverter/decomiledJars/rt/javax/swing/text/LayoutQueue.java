package javax.swing.text;

import java.util.Vector;
import sun.awt.AppContext;

public class LayoutQueue
{
  private static final Object DEFAULT_QUEUE = new Object();
  private Vector<Runnable> tasks = new Vector();
  private Thread worker;
  
  public LayoutQueue() {}
  
  public static LayoutQueue getDefaultQueue()
  {
    AppContext localAppContext = AppContext.getAppContext();
    synchronized (DEFAULT_QUEUE)
    {
      LayoutQueue localLayoutQueue = (LayoutQueue)localAppContext.get(DEFAULT_QUEUE);
      if (localLayoutQueue == null)
      {
        localLayoutQueue = new LayoutQueue();
        localAppContext.put(DEFAULT_QUEUE, localLayoutQueue);
      }
      return localLayoutQueue;
    }
  }
  
  public static void setDefaultQueue(LayoutQueue paramLayoutQueue)
  {
    synchronized (DEFAULT_QUEUE)
    {
      AppContext.getAppContext().put(DEFAULT_QUEUE, paramLayoutQueue);
    }
  }
  
  public synchronized void addTask(Runnable paramRunnable)
  {
    if (this.worker == null)
    {
      this.worker = new LayoutThread();
      this.worker.start();
    }
    this.tasks.addElement(paramRunnable);
    notifyAll();
  }
  
  protected synchronized Runnable waitForWork()
  {
    while (this.tasks.size() == 0) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException)
      {
        return null;
      }
    }
    Runnable localRunnable = (Runnable)this.tasks.firstElement();
    this.tasks.removeElementAt(0);
    return localRunnable;
  }
  
  class LayoutThread
    extends Thread
  {
    LayoutThread()
    {
      super();
      setPriority(1);
    }
    
    public void run()
    {
      Runnable localRunnable;
      do
      {
        localRunnable = LayoutQueue.this.waitForWork();
        if (localRunnable != null) {
          localRunnable.run();
        }
      } while (localRunnable != null);
    }
  }
}
