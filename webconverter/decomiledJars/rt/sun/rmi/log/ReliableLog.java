package sun.rmi.log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class ReliableLog
{
  public static final int PreferredMajorVersion = 0;
  public static final int PreferredMinorVersion = 2;
  private boolean Debug = false;
  private static String snapshotPrefix = "Snapshot.";
  private static String logfilePrefix = "Logfile.";
  private static String versionFile = "Version_Number";
  private static String newVersionFile = "New_Version_Number";
  private static int intBytes = 4;
  private static long diskPageSize = 512L;
  private File dir;
  private int version = 0;
  private String logName = null;
  private LogFile log = null;
  private long snapshotBytes = 0L;
  private long logBytes = 0L;
  private int logEntries = 0;
  private long lastSnapshot = 0L;
  private long lastLog = 0L;
  private LogHandler handler;
  private final byte[] intBuf = new byte[4];
  private int majorFormatVersion = 0;
  private int minorFormatVersion = 0;
  private static final Constructor<? extends LogFile> logClassConstructor = getLogClassConstructor();
  
  public ReliableLog(String paramString, LogHandler paramLogHandler, boolean paramBoolean)
    throws IOException
  {
    this.dir = new File(paramString);
    if (((!this.dir.exists()) || (!this.dir.isDirectory())) && (!this.dir.mkdir())) {
      throw new IOException("could not create directory for log: " + paramString);
    }
    this.handler = paramLogHandler;
    this.lastSnapshot = 0L;
    this.lastLog = 0L;
    getVersion();
    if (this.version == 0) {
      try
      {
        snapshot(paramLogHandler.initialSnapshot());
      }
      catch (IOException localIOException)
      {
        throw localIOException;
      }
      catch (Exception localException)
      {
        throw new IOException("initial snapshot failed with exception: " + localException);
      }
    }
  }
  
  public ReliableLog(String paramString, LogHandler paramLogHandler)
    throws IOException
  {
    this(paramString, paramLogHandler, false);
  }
  
  public synchronized Object recover()
    throws IOException
  {
    if (this.Debug) {
      System.err.println("log.debug: recover()");
    }
    if (this.version == 0) {
      return null;
    }
    String str = versionName(snapshotPrefix);
    File localFile = new File(str);
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(localFile));
    if (this.Debug) {
      System.err.println("log.debug: recovering from " + str);
    }
    try
    {
      Object localObject1;
      try
      {
        localObject1 = this.handler.recover(localBufferedInputStream);
      }
      catch (IOException localIOException)
      {
        throw localIOException;
      }
      return recoverUpdates(localObject1);
    }
    catch (Exception localException)
    {
      if (this.Debug) {
        System.err.println("log.debug: recovery failed: " + localException);
      }
      throw new IOException("log recover failed with exception: " + localException);
      this.snapshotBytes = localFile.length();
    }
    finally
    {
      localBufferedInputStream.close();
    }
  }
  
  public synchronized void update(Object paramObject)
    throws IOException
  {
    update(paramObject, true);
  }
  
  public synchronized void update(Object paramObject, boolean paramBoolean)
    throws IOException
  {
    if (this.log == null) {
      throw new IOException("log is inaccessible, it may have been corrupted or closed");
    }
    long l1 = this.log.getFilePointer();
    boolean bool = this.log.checkSpansBoundary(l1);
    writeInt(this.log, bool ? Integer.MIN_VALUE : 0);
    try
    {
      this.handler.writeUpdate(new LogOutputStream(this.log), paramObject);
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (Exception localException)
    {
      throw ((IOException)new IOException("write update failed").initCause(localException));
    }
    this.log.sync();
    long l2 = this.log.getFilePointer();
    int i = (int)(l2 - l1 - intBytes);
    this.log.seek(l1);
    if (bool)
    {
      writeInt(this.log, i | 0x80000000);
      this.log.sync();
      this.log.seek(l1);
      this.log.writeByte(i >> 24);
      this.log.sync();
    }
    else
    {
      writeInt(this.log, i);
      this.log.sync();
    }
    this.log.seek(l2);
    this.logBytes = l2;
    this.lastLog = System.currentTimeMillis();
    this.logEntries += 1;
  }
  
  private static Constructor<? extends LogFile> getLogClassConstructor()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.log.class"));
    if (str != null) {
      try
      {
        ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
        {
          public ClassLoader run()
          {
            return ClassLoader.getSystemClassLoader();
          }
        });
        Class localClass = localClassLoader.loadClass(str).asSubclass(LogFile.class);
        return localClass.getConstructor(new Class[] { String.class, String.class });
      }
      catch (Exception localException)
      {
        System.err.println("Exception occurred:");
        localException.printStackTrace();
      }
    }
    return null;
  }
  
  public synchronized void snapshot(Object paramObject)
    throws IOException
  {
    int i = this.version;
    incrVersion();
    String str = versionName(snapshotPrefix);
    File localFile = new File(str);
    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
    try
    {
      try
      {
        this.handler.snapshot(localFileOutputStream, paramObject);
      }
      catch (IOException localIOException)
      {
        throw localIOException;
      }
      openLogFile(true);
    }
    catch (Exception localException)
    {
      throw new IOException("snapshot failed", localException);
      this.lastSnapshot = System.currentTimeMillis();
    }
    finally
    {
      localFileOutputStream.close();
      this.snapshotBytes = localFile.length();
    }
    writeVersionFile(true);
    commitToNewVersion();
    deleteSnapshot(i);
    deleteLogFile(i);
  }
  
  public synchronized void close()
    throws IOException
  {
    if (this.log == null) {
      return;
    }
    try
    {
      this.log.close();
      this.log = null;
    }
    finally
    {
      this.log = null;
    }
  }
  
  public long snapshotSize()
  {
    return this.snapshotBytes;
  }
  
  public long logSize()
  {
    return this.logBytes;
  }
  
  private void writeInt(DataOutput paramDataOutput, int paramInt)
    throws IOException
  {
    this.intBuf[0] = ((byte)(paramInt >> 24));
    this.intBuf[1] = ((byte)(paramInt >> 16));
    this.intBuf[2] = ((byte)(paramInt >> 8));
    this.intBuf[3] = ((byte)paramInt);
    paramDataOutput.write(this.intBuf);
  }
  
  private String fName(String paramString)
  {
    return this.dir.getPath() + File.separator + paramString;
  }
  
  private String versionName(String paramString)
  {
    return versionName(paramString, 0);
  }
  
  private String versionName(String paramString, int paramInt)
  {
    paramInt = paramInt == 0 ? this.version : paramInt;
    return fName(paramString) + String.valueOf(paramInt);
  }
  
  private void incrVersion()
  {
    do
    {
      this.version += 1;
    } while (this.version == 0);
  }
  
  private void deleteFile(String paramString)
    throws IOException
  {
    File localFile = new File(paramString);
    if (!localFile.delete()) {
      throw new IOException("couldn't remove file: " + paramString);
    }
  }
  
  private void deleteNewVersionFile()
    throws IOException
  {
    deleteFile(fName(newVersionFile));
  }
  
  private void deleteSnapshot(int paramInt)
    throws IOException
  {
    if (paramInt == 0) {
      return;
    }
    deleteFile(versionName(snapshotPrefix, paramInt));
  }
  
  private void deleteLogFile(int paramInt)
    throws IOException
  {
    if (paramInt == 0) {
      return;
    }
    deleteFile(versionName(logfilePrefix, paramInt));
  }
  
  private void openLogFile(boolean paramBoolean)
    throws IOException
  {
    try
    {
      close();
    }
    catch (IOException localIOException) {}
    this.logName = versionName(logfilePrefix);
    try
    {
      this.log = (logClassConstructor == null ? new LogFile(this.logName, "rw") : (LogFile)logClassConstructor.newInstance(new Object[] { this.logName, "rw" }));
    }
    catch (Exception localException)
    {
      throw ((IOException)new IOException("unable to construct LogFile instance").initCause(localException));
    }
    if (paramBoolean) {
      initializeLogFile();
    }
  }
  
  private void initializeLogFile()
    throws IOException
  {
    this.log.setLength(0L);
    this.majorFormatVersion = 0;
    writeInt(this.log, 0);
    this.minorFormatVersion = 2;
    writeInt(this.log, 2);
    this.logBytes = (intBytes * 2);
    this.logEntries = 0;
  }
  
  private void writeVersionFile(boolean paramBoolean)
    throws IOException
  {
    String str;
    if (paramBoolean) {
      str = newVersionFile;
    } else {
      str = versionFile;
    }
    FileOutputStream localFileOutputStream = new FileOutputStream(fName(str));
    Object localObject1 = null;
    try
    {
      DataOutputStream localDataOutputStream = new DataOutputStream(localFileOutputStream);
      Object localObject2 = null;
      try
      {
        writeInt(localDataOutputStream, this.version);
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = localThrowable4;
        throw localThrowable4;
      }
      finally {}
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileOutputStream.close();
          }
          catch (Throwable localThrowable6)
          {
            localObject1.addSuppressed(localThrowable6);
          }
        } else {
          localFileOutputStream.close();
        }
      }
    }
  }
  
  private void createFirstVersion()
    throws IOException
  {
    this.version = 0;
    writeVersionFile(false);
  }
  
  private void commitToNewVersion()
    throws IOException
  {
    writeVersionFile(false);
    deleteNewVersionFile();
  }
  
  private int readVersion(String paramString)
    throws IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(new FileInputStream(paramString));
    Object localObject1 = null;
    try
    {
      int i = localDataInputStream.readInt();
      return i;
    }
    catch (Throwable localThrowable1)
    {
      localObject1 = localThrowable1;
      throw localThrowable1;
    }
    finally
    {
      if (localDataInputStream != null) {
        if (localObject1 != null) {
          try
          {
            localDataInputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            localObject1.addSuppressed(localThrowable3);
          }
        } else {
          localDataInputStream.close();
        }
      }
    }
  }
  
  private void getVersion()
    throws IOException
  {
    try
    {
      this.version = readVersion(fName(newVersionFile));
      commitToNewVersion();
    }
    catch (IOException localIOException1)
    {
      try
      {
        deleteNewVersionFile();
      }
      catch (IOException localIOException2) {}
      try
      {
        this.version = readVersion(fName(versionFile));
      }
      catch (IOException localIOException3)
      {
        createFirstVersion();
      }
    }
  }
  
  private Object recoverUpdates(Object paramObject)
    throws IOException
  {
    this.logBytes = 0L;
    this.logEntries = 0;
    if (this.version == 0) {
      return paramObject;
    }
    String str = versionName(logfilePrefix);
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(str));
    DataInputStream localDataInputStream = new DataInputStream(localBufferedInputStream);
    if (this.Debug) {
      System.err.println("log.debug: reading updates from " + str);
    }
    try
    {
      this.majorFormatVersion = localDataInputStream.readInt();
      this.logBytes += intBytes;
      this.minorFormatVersion = localDataInputStream.readInt();
      this.logBytes += intBytes;
    }
    catch (EOFException localEOFException1)
    {
      openLogFile(true);
      localBufferedInputStream = null;
    }
    if (this.majorFormatVersion != 0)
    {
      if (this.Debug) {
        System.err.println("log.debug: major version mismatch: " + this.majorFormatVersion + "." + this.minorFormatVersion);
      }
      throw new IOException("Log file " + this.logName + " has a " + "version " + this.majorFormatVersion + "." + this.minorFormatVersion + " format, and this implementation " + " understands only version " + 0 + "." + 2);
    }
    try
    {
      while (localBufferedInputStream != null)
      {
        int i = 0;
        try
        {
          i = localDataInputStream.readInt();
        }
        catch (EOFException localEOFException2)
        {
          if (this.Debug) {
            System.err.println("log.debug: log was sync'd cleanly");
          }
          break;
        }
        if (i <= 0)
        {
          if (!this.Debug) {
            break;
          }
          System.err.println("log.debug: last update incomplete, updateLen = 0x" + Integer.toHexString(i));
          break;
        }
        if (localBufferedInputStream.available() < i)
        {
          if (!this.Debug) {
            break;
          }
          System.err.println("log.debug: log was truncated");
          break;
        }
        if (this.Debug) {
          System.err.println("log.debug: rdUpdate size " + i);
        }
        try
        {
          paramObject = this.handler.readUpdate(new LogInputStream(localBufferedInputStream, i), paramObject);
        }
        catch (IOException localIOException)
        {
          throw localIOException;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
          throw new IOException("read update failed with exception: " + localException);
        }
        this.logBytes += intBytes + i;
        this.logEntries += 1;
      }
    }
    finally
    {
      if (localBufferedInputStream != null) {
        localBufferedInputStream.close();
      }
    }
    if (this.Debug) {
      System.err.println("log.debug: recovered updates: " + this.logEntries);
    }
    openLogFile(false);
    if (this.log == null) {
      throw new IOException("rmid's log is inaccessible, it may have been corrupted or closed");
    }
    this.log.seek(this.logBytes);
    this.log.setLength(this.logBytes);
    return paramObject;
  }
  
  public static class LogFile
    extends RandomAccessFile
  {
    private final FileDescriptor fd = getFD();
    
    public LogFile(String paramString1, String paramString2)
      throws FileNotFoundException, IOException
    {
      super(paramString2);
    }
    
    protected void sync()
      throws IOException
    {
      this.fd.sync();
    }
    
    protected boolean checkSpansBoundary(long paramLong)
    {
      return paramLong % 512L > 508L;
    }
  }
}
