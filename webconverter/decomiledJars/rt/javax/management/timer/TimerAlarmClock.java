package javax.management.timer;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class TimerAlarmClock
  extends TimerTask
{
  Timer listener = null;
  long timeout = 10000L;
  Date next = null;
  
  public TimerAlarmClock(Timer paramTimer, long paramLong)
  {
    this.listener = paramTimer;
    this.timeout = Math.max(0L, paramLong);
  }
  
  public TimerAlarmClock(Timer paramTimer, Date paramDate)
  {
    this.listener = paramTimer;
    this.next = paramDate;
  }
  
  public void run()
  {
    try
    {
      TimerAlarmClockNotification localTimerAlarmClockNotification = new TimerAlarmClockNotification(this);
      this.listener.notifyAlarmClock(localTimerAlarmClockNotification);
    }
    catch (Exception localException)
    {
      JmxProperties.TIMER_LOGGER.logp(Level.FINEST, Timer.class.getName(), "run", "Got unexpected exception when sending a notification", localException);
    }
  }
}
