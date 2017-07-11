package jdk.management.resource.internal.inst;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.Permission;
import java.util.Arrays;
import java.util.PropertyPermission;
import jdk.internal.instrumentation.Logger;
import jdk.internal.instrumentation.Tracer;
import sun.security.action.GetPropertyAction;

public final class InitInstrumentation
  implements Runnable
{
  volatile boolean initialized = false;
  static final Class<?>[] hooks;
  
  public InitInstrumentation() {}
  
  public synchronized void run()
  {
    if (!this.initialized)
    {
      try
      {
        Tracer localTracer = Tracer.getInstance();
        localTracer.addInstrumentations(Arrays.asList(hooks), TestLogger.tlogger);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        TestLogger.tlogger.error("Unable to load class: " + localClassNotFoundException.getMessage(), localClassNotFoundException);
      }
      catch (Exception localException)
      {
        TestLogger.tlogger.error("Unable to load class: " + localException.getMessage(), localException);
      }
      this.initialized = true;
    }
  }
  
  static
  {
    Class[] arrayOfClass1 = { AbstractInterruptibleChannelRMHooks.class, AbstractPlainDatagramSocketImplRMHooks.class, AbstractPlainSocketImplRMHooks.class, AsynchronousServerSocketChannelImplRMHooks.class, AsynchronousSocketChannelImplRMHooks.class, BaseSSLSocketImplRMHooks.class, DatagramChannelImplRMHooks.class, DatagramDispatcherRMHooks.class, DatagramSocketRMHooks.class, FileChannelImplRMHooks.class, FileInputStreamRMHooks.class, FileOutputStreamRMHooks.class, NetRMHooks.class, RandomAccessFileRMHooks.class, ServerSocketRMHooks.class, ServerSocketChannelImplRMHooks.class, SocketChannelImplRMHooks.class, SocketDispatcherRMHooks.class, SocketInputStreamRMHooks.class, SocketOutputStreamRMHooks.class, SocketRMHooks.class, SSLSocketImplRMHooks.class, SSLServerSocketImplRMHooks.class, ThreadRMHooks.class, WrapInstrumentationRMHooks.class };
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"), null, new Permission[] { new PropertyPermission("os.name", "read") });
    Class[] arrayOfClass2;
    if (str.startsWith("Windows")) {
      arrayOfClass2 = new Class[] { WindowsAsynchronousFileChannelImplRMHooks.class, WindowsAsynchronousServerSocketChannelImplRMHooks.class, WindowsAsynchronousSocketChannelImplRMHooks.class };
    } else {
      arrayOfClass2 = new Class[] { SimpleAsynchronousFileChannelImplRMHooks.class, UnixAsynchronousServerSocketChannelImplRMHooks.class, UnixAsynchronousSocketChannelImplRMHooks.class };
    }
    hooks = new Class[arrayOfClass1.length + arrayOfClass2.length];
    System.arraycopy(arrayOfClass1, 0, hooks, 0, arrayOfClass1.length);
    System.arraycopy(arrayOfClass2, 0, hooks, arrayOfClass1.length, arrayOfClass2.length);
  }
  
  static class TestLogger
    implements Logger
  {
    static final TestLogger tlogger = new TestLogger();
    
    TestLogger() {}
    
    public void debug(String paramString)
    {
      System.out.printf("TestLogger debug: %s%n", new Object[] { paramString });
    }
    
    public void error(String paramString)
    {
      System.out.printf("TestLogger error: %s%n", new Object[] { paramString });
    }
    
    public void error(String paramString, Throwable paramThrowable)
    {
      System.out.printf("TestLogger error: %s, ex: %s%n", new Object[] { paramString, paramThrowable });
      paramThrowable.printStackTrace();
    }
    
    public void info(String paramString)
    {
      System.out.printf("TestLogger info: %s%n", new Object[] { paramString });
    }
    
    public void trace(String paramString) {}
    
    public void warn(String paramString)
    {
      System.out.printf("TestLogger warning: %s%n", new Object[] { paramString });
    }
  }
}
