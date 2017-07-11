package javax.swing;

import java.awt.Component;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class ProgressMonitorInputStream
  extends FilterInputStream
{
  private ProgressMonitor monitor;
  private int nread = 0;
  private int size = 0;
  
  public ProgressMonitorInputStream(Component paramComponent, Object paramObject, InputStream paramInputStream)
  {
    super(paramInputStream);
    try
    {
      this.size = paramInputStream.available();
    }
    catch (IOException localIOException)
    {
      this.size = 0;
    }
    this.monitor = new ProgressMonitor(paramComponent, paramObject, null, 0, this.size);
  }
  
  public ProgressMonitor getProgressMonitor()
  {
    return this.monitor;
  }
  
  public int read()
    throws IOException
  {
    int i = this.in.read();
    if (i >= 0) {
      this.monitor.setProgress(++this.nread);
    }
    if (this.monitor.isCanceled())
    {
      InterruptedIOException localInterruptedIOException = new InterruptedIOException("progress");
      localInterruptedIOException.bytesTransferred = this.nread;
      throw localInterruptedIOException;
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = this.in.read(paramArrayOfByte);
    if (i > 0) {
      this.monitor.setProgress(this.nread += i);
    }
    if (this.monitor.isCanceled())
    {
      InterruptedIOException localInterruptedIOException = new InterruptedIOException("progress");
      localInterruptedIOException.bytesTransferred = this.nread;
      throw localInterruptedIOException;
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i > 0) {
      this.monitor.setProgress(this.nread += i);
    }
    if (this.monitor.isCanceled())
    {
      InterruptedIOException localInterruptedIOException = new InterruptedIOException("progress");
      localInterruptedIOException.bytesTransferred = this.nread;
      throw localInterruptedIOException;
    }
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    long l = this.in.skip(paramLong);
    if (l > 0L) {
      this.monitor.setProgress(this.nread = (int)(this.nread + l));
    }
    return l;
  }
  
  public void close()
    throws IOException
  {
    this.in.close();
    this.monitor.close();
  }
  
  public synchronized void reset()
    throws IOException
  {
    this.in.reset();
    this.nread = (this.size - this.in.available());
    this.monitor.setProgress(this.nread);
  }
}
