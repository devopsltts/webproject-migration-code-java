package java.util;

class TimerThread
  extends Thread
{
  boolean newTasksMayBeScheduled = true;
  private TaskQueue queue;
  
  TimerThread(TaskQueue paramTaskQueue)
  {
    this.queue = paramTaskQueue;
  }
  
  public void run()
  {
    try
    {
      mainLoop();
      synchronized (this.queue)
      {
        this.newTasksMayBeScheduled = false;
        this.queue.clear();
      }
    }
    finally
    {
      synchronized (this.queue)
      {
        this.newTasksMayBeScheduled = false;
        this.queue.clear();
      }
    }
  }
  
  private void mainLoop()
  {
    try
    {
      for (;;)
      {
        TimerTask localTimerTask;
        int i;
        synchronized (this.queue)
        {
          if ((this.queue.isEmpty()) && (this.newTasksMayBeScheduled))
          {
            this.queue.wait();
            continue;
          }
          if (this.queue.isEmpty()) {
            break;
          }
          localTimerTask = this.queue.getMin();
          long l1;
          long l2;
          synchronized (localTimerTask.lock)
          {
            if (localTimerTask.state == 3)
            {
              this.queue.removeMin();
              continue;
            }
            l1 = System.currentTimeMillis();
            l2 = localTimerTask.nextExecutionTime;
            if ((i = l2 <= l1 ? 1 : 0) != 0) {
              if (localTimerTask.period == 0L)
              {
                this.queue.removeMin();
                localTimerTask.state = 2;
              }
              else
              {
                this.queue.rescheduleMin(localTimerTask.period < 0L ? l1 - localTimerTask.period : l2 + localTimerTask.period);
              }
            }
          }
          if (i == 0) {
            this.queue.wait(l2 - l1);
          }
        }
        if (i != 0) {
          localTimerTask.run();
        }
      }
    }
    catch (InterruptedException localInterruptedException) {}
  }
}
