package java.rmi.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LoggingPermission;

@Deprecated
public class LogStream
  extends PrintStream
{
  private static Map<String, LogStream> known = new HashMap(5);
  private static PrintStream defaultStream = System.err;
  private String name;
  private OutputStream logOut;
  private OutputStreamWriter logWriter;
  private StringBuffer buffer = new StringBuffer();
  private ByteArrayOutputStream bufOut = (ByteArrayOutputStream)this.out;
  public static final int SILENT = 0;
  public static final int BRIEF = 10;
  public static final int VERBOSE = 20;
  
  @Deprecated
  private LogStream(String paramString, OutputStream paramOutputStream)
  {
    super(new ByteArrayOutputStream());
    this.name = paramString;
    setOutputStream(paramOutputStream);
  }
  
  @Deprecated
  public static LogStream log(String paramString)
  {
    LogStream localLogStream;
    synchronized (known)
    {
      localLogStream = (LogStream)known.get(paramString);
      if (localLogStream == null) {
        localLogStream = new LogStream(paramString, defaultStream);
      }
      known.put(paramString, localLogStream);
    }
    return localLogStream;
  }
  
  @Deprecated
  public static synchronized PrintStream getDefaultStream()
  {
    return defaultStream;
  }
  
  @Deprecated
  public static synchronized void setDefaultStream(PrintStream paramPrintStream)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new LoggingPermission("control", null));
    }
    defaultStream = paramPrintStream;
  }
  
  @Deprecated
  public synchronized OutputStream getOutputStream()
  {
    return this.logOut;
  }
  
  @Deprecated
  public synchronized void setOutputStream(OutputStream paramOutputStream)
  {
    this.logOut = paramOutputStream;
    this.logWriter = new OutputStreamWriter(this.logOut);
  }
  
  @Deprecated
  public void write(int paramInt)
  {
    if (paramInt == 10) {
      synchronized (this)
      {
        synchronized (this.logOut)
        {
          this.buffer.setLength(0);
          this.buffer.append(new Date().toString());
          this.buffer.append(':');
          this.buffer.append(this.name);
          this.buffer.append(':');
          this.buffer.append(Thread.currentThread().getName());
          this.buffer.append(':');
          try
          {
            this.logWriter.write(this.buffer.toString());
            this.logWriter.flush();
            this.bufOut.writeTo(this.logOut);
            this.logOut.write(paramInt);
            this.logOut.flush();
          }
          catch (IOException localIOException)
          {
            setError();
          }
          finally
          {
            this.bufOut.reset();
          }
        }
      }
    } else {
      super.write(paramInt);
    }
  }
  
  @Deprecated
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt2);
    }
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfByte[(paramInt1 + i)]);
    }
  }
  
  @Deprecated
  public String toString()
  {
    return this.name;
  }
  
  @Deprecated
  public static int parseLevel(String paramString)
  {
    if ((paramString == null) || (paramString.length() < 1)) {
      return -1;
    }
    try
    {
      return Integer.parseInt(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      if (paramString.length() < 1) {
        return -1;
      }
      if ("SILENT".startsWith(paramString.toUpperCase())) {
        return 0;
      }
      if ("BRIEF".startsWith(paramString.toUpperCase())) {
        return 10;
      }
      if ("VERBOSE".startsWith(paramString.toUpperCase())) {
        return 20;
      }
    }
    return -1;
  }
}
