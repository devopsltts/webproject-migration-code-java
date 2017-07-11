package java.util.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class LogRecord
  implements Serializable
{
  private static final AtomicLong globalSequenceNumber = new AtomicLong(0L);
  private static final int MIN_SEQUENTIAL_THREAD_ID = 1073741823;
  private static final AtomicInteger nextThreadId = new AtomicInteger(1073741823);
  private static final ThreadLocal<Integer> threadIds = new ThreadLocal();
  private Level level;
  private long sequenceNumber;
  private String sourceClassName;
  private String sourceMethodName;
  private String message;
  private int threadID;
  private long millis;
  private Throwable thrown;
  private String loggerName;
  private String resourceBundleName;
  private transient boolean needToInferCaller;
  private transient Object[] parameters;
  private transient ResourceBundle resourceBundle;
  private static final long serialVersionUID = 5372048053134512534L;
  
  private int defaultThreadID()
  {
    long l = Thread.currentThread().getId();
    if (l < 1073741823L) {
      return (int)l;
    }
    Integer localInteger = (Integer)threadIds.get();
    if (localInteger == null)
    {
      localInteger = Integer.valueOf(nextThreadId.getAndIncrement());
      threadIds.set(localInteger);
    }
    return localInteger.intValue();
  }
  
  public LogRecord(Level paramLevel, String paramString)
  {
    paramLevel.getClass();
    this.level = paramLevel;
    this.message = paramString;
    this.sequenceNumber = globalSequenceNumber.getAndIncrement();
    this.threadID = defaultThreadID();
    this.millis = System.currentTimeMillis();
    this.needToInferCaller = true;
  }
  
  public String getLoggerName()
  {
    return this.loggerName;
  }
  
  public void setLoggerName(String paramString)
  {
    this.loggerName = paramString;
  }
  
  public ResourceBundle getResourceBundle()
  {
    return this.resourceBundle;
  }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle)
  {
    this.resourceBundle = paramResourceBundle;
  }
  
  public String getResourceBundleName()
  {
    return this.resourceBundleName;
  }
  
  public void setResourceBundleName(String paramString)
  {
    this.resourceBundleName = paramString;
  }
  
  public Level getLevel()
  {
    return this.level;
  }
  
  public void setLevel(Level paramLevel)
  {
    if (paramLevel == null) {
      throw new NullPointerException();
    }
    this.level = paramLevel;
  }
  
  public long getSequenceNumber()
  {
    return this.sequenceNumber;
  }
  
  public void setSequenceNumber(long paramLong)
  {
    this.sequenceNumber = paramLong;
  }
  
  public String getSourceClassName()
  {
    if (this.needToInferCaller) {
      inferCaller();
    }
    return this.sourceClassName;
  }
  
  public void setSourceClassName(String paramString)
  {
    this.sourceClassName = paramString;
    this.needToInferCaller = false;
  }
  
  public String getSourceMethodName()
  {
    if (this.needToInferCaller) {
      inferCaller();
    }
    return this.sourceMethodName;
  }
  
  public void setSourceMethodName(String paramString)
  {
    this.sourceMethodName = paramString;
    this.needToInferCaller = false;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public void setMessage(String paramString)
  {
    this.message = paramString;
  }
  
  public Object[] getParameters()
  {
    return this.parameters;
  }
  
  public void setParameters(Object[] paramArrayOfObject)
  {
    this.parameters = paramArrayOfObject;
  }
  
  public int getThreadID()
  {
    return this.threadID;
  }
  
  public void setThreadID(int paramInt)
  {
    this.threadID = paramInt;
  }
  
  public long getMillis()
  {
    return this.millis;
  }
  
  public void setMillis(long paramLong)
  {
    this.millis = paramLong;
  }
  
  public Throwable getThrown()
  {
    return this.thrown;
  }
  
  public void setThrown(Throwable paramThrowable)
  {
    this.thrown = paramThrowable;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeByte(1);
    paramObjectOutputStream.writeByte(0);
    if (this.parameters == null)
    {
      paramObjectOutputStream.writeInt(-1);
      return;
    }
    paramObjectOutputStream.writeInt(this.parameters.length);
    for (int i = 0; i < this.parameters.length; i++) {
      if (this.parameters[i] == null) {
        paramObjectOutputStream.writeObject(null);
      } else {
        paramObjectOutputStream.writeObject(this.parameters[i].toString());
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readByte();
    int j = paramObjectInputStream.readByte();
    if (i != 1) {
      throw new IOException("LogRecord: bad version: " + i + "." + j);
    }
    int k = paramObjectInputStream.readInt();
    if (k == -1)
    {
      this.parameters = null;
    }
    else
    {
      this.parameters = new Object[k];
      for (int m = 0; m < this.parameters.length; m++) {
        this.parameters[m] = paramObjectInputStream.readObject();
      }
    }
    if (this.resourceBundleName != null) {
      try
      {
        ResourceBundle localResourceBundle = ResourceBundle.getBundle(this.resourceBundleName, Locale.getDefault(), ClassLoader.getSystemClassLoader());
        this.resourceBundle = localResourceBundle;
      }
      catch (MissingResourceException localMissingResourceException)
      {
        this.resourceBundle = null;
      }
    }
    this.needToInferCaller = false;
  }
  
  private void inferCaller()
  {
    this.needToInferCaller = false;
    JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
    Throwable localThrowable = new Throwable();
    int i = localJavaLangAccess.getStackTraceDepth(localThrowable);
    int j = 1;
    for (int k = 0; k < i; k++)
    {
      StackTraceElement localStackTraceElement = localJavaLangAccess.getStackTraceElement(localThrowable, k);
      String str = localStackTraceElement.getClassName();
      boolean bool = isLoggerImplFrame(str);
      if (j != 0)
      {
        if (bool) {
          j = 0;
        }
      }
      else if ((!bool) && (!str.startsWith("java.lang.reflect.")) && (!str.startsWith("sun.reflect.")))
      {
        setSourceClassName(str);
        setSourceMethodName(localStackTraceElement.getMethodName());
        return;
      }
    }
  }
  
  private boolean isLoggerImplFrame(String paramString)
  {
    return (paramString.equals("java.util.logging.Logger")) || (paramString.startsWith("java.util.logging.LoggingProxyImpl")) || (paramString.startsWith("sun.util.logging."));
  }
}
