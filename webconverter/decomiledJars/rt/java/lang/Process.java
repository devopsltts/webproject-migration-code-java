package java.lang;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public abstract class Process
{
  public Process() {}
  
  public abstract OutputStream getOutputStream();
  
  public abstract InputStream getInputStream();
  
  public abstract InputStream getErrorStream();
  
  public abstract int waitFor()
    throws InterruptedException;
  
  public boolean waitFor(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    long l1 = System.nanoTime();
    long l2 = paramTimeUnit.toNanos(paramLong);
    do
    {
      try
      {
        exitValue();
        return true;
      }
      catch (IllegalThreadStateException localIllegalThreadStateException)
      {
        if (l2 > 0L) {
          Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(l2) + 1L, 100L));
        }
        l2 = paramTimeUnit.toNanos(paramLong) - (System.nanoTime() - l1);
      }
    } while (l2 > 0L);
    return false;
  }
  
  public abstract int exitValue();
  
  public abstract void destroy();
  
  public Process destroyForcibly()
  {
    destroy();
    return this;
  }
  
  public boolean isAlive()
  {
    try
    {
      exitValue();
      return false;
    }
    catch (IllegalThreadStateException localIllegalThreadStateException) {}
    return true;
  }
}
