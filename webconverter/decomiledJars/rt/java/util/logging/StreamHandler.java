package java.util.logging;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class StreamHandler
  extends Handler
{
  private OutputStream output;
  private boolean doneHeader;
  private volatile Writer writer;
  
  private void configure()
  {
    LogManager localLogManager = LogManager.getLogManager();
    String str = getClass().getName();
    setLevel(localLogManager.getLevelProperty(str + ".level", Level.INFO));
    setFilter(localLogManager.getFilterProperty(str + ".filter", null));
    setFormatter(localLogManager.getFormatterProperty(str + ".formatter", new SimpleFormatter()));
    try
    {
      setEncoding(localLogManager.getStringProperty(str + ".encoding", null));
    }
    catch (Exception localException1)
    {
      try
      {
        setEncoding(null);
      }
      catch (Exception localException2) {}
    }
  }
  
  public StreamHandler()
  {
    this.sealed = false;
    configure();
    this.sealed = true;
  }
  
  public StreamHandler(OutputStream paramOutputStream, Formatter paramFormatter)
  {
    this.sealed = false;
    configure();
    setFormatter(paramFormatter);
    setOutputStream(paramOutputStream);
    this.sealed = true;
  }
  
  protected synchronized void setOutputStream(OutputStream paramOutputStream)
    throws SecurityException
  {
    if (paramOutputStream == null) {
      throw new NullPointerException();
    }
    flushAndClose();
    this.output = paramOutputStream;
    this.doneHeader = false;
    String str = getEncoding();
    if (str == null) {
      this.writer = new OutputStreamWriter(this.output);
    } else {
      try
      {
        this.writer = new OutputStreamWriter(this.output, str);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new Error("Unexpected exception " + localUnsupportedEncodingException);
      }
    }
  }
  
  public synchronized void setEncoding(String paramString)
    throws SecurityException, UnsupportedEncodingException
  {
    super.setEncoding(paramString);
    if (this.output == null) {
      return;
    }
    flush();
    if (paramString == null) {
      this.writer = new OutputStreamWriter(this.output);
    } else {
      this.writer = new OutputStreamWriter(this.output, paramString);
    }
  }
  
  public synchronized void publish(LogRecord paramLogRecord)
  {
    if (!isLoggable(paramLogRecord)) {
      return;
    }
    String str;
    try
    {
      str = getFormatter().format(paramLogRecord);
    }
    catch (Exception localException1)
    {
      reportError(null, localException1, 5);
      return;
    }
    try
    {
      if (!this.doneHeader)
      {
        this.writer.write(getFormatter().getHead(this));
        this.doneHeader = true;
      }
      this.writer.write(str);
    }
    catch (Exception localException2)
    {
      reportError(null, localException2, 1);
    }
  }
  
  public boolean isLoggable(LogRecord paramLogRecord)
  {
    if ((this.writer == null) || (paramLogRecord == null)) {
      return false;
    }
    return super.isLoggable(paramLogRecord);
  }
  
  public synchronized void flush()
  {
    if (this.writer != null) {
      try
      {
        this.writer.flush();
      }
      catch (Exception localException)
      {
        reportError(null, localException, 2);
      }
    }
  }
  
  private synchronized void flushAndClose()
    throws SecurityException
  {
    checkPermission();
    if (this.writer != null)
    {
      try
      {
        if (!this.doneHeader)
        {
          this.writer.write(getFormatter().getHead(this));
          this.doneHeader = true;
        }
        this.writer.write(getFormatter().getTail(this));
        this.writer.flush();
        this.writer.close();
      }
      catch (Exception localException)
      {
        reportError(null, localException, 3);
      }
      this.writer = null;
      this.output = null;
    }
  }
  
  public synchronized void close()
    throws SecurityException
  {
    flushAndClose();
  }
}
