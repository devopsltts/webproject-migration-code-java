package java.lang;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.misc.Contended;
import sun.misc.VM;
import sun.nio.ch.Interruptible;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.SecurityConstants;

public class Thread
  implements Runnable
{
  private volatile char[] name;
  private int priority;
  private Thread threadQ;
  private long eetop;
  private boolean single_step;
  private boolean daemon = false;
  private boolean stillborn = false;
  private Runnable target;
  private ThreadGroup group;
  private ClassLoader contextClassLoader;
  private AccessControlContext inheritedAccessControlContext;
  private static int threadInitNumber;
  ThreadLocal.ThreadLocalMap threadLocals = null;
  ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
  private long stackSize;
  private long nativeParkEventPointer;
  private long tid;
  private static long threadSeqNumber;
  private volatile int threadStatus = 0;
  volatile Object parkBlocker;
  private volatile Interruptible blocker;
  private final Object blockerLock = new Object();
  public static final int MIN_PRIORITY = 1;
  public static final int NORM_PRIORITY = 5;
  public static final int MAX_PRIORITY = 10;
  private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
  private static final RuntimePermission SUBCLASS_IMPLEMENTATION_PERMISSION = new RuntimePermission("enableContextClassLoaderOverride");
  private volatile UncaughtExceptionHandler uncaughtExceptionHandler;
  private static volatile UncaughtExceptionHandler defaultUncaughtExceptionHandler;
  @Contended("tlr")
  long threadLocalRandomSeed;
  @Contended("tlr")
  int threadLocalRandomProbe;
  @Contended("tlr")
  int threadLocalRandomSecondarySeed;
  
  private static native void registerNatives();
  
  private static synchronized int nextThreadNum()
  {
    return threadInitNumber++;
  }
  
  private static synchronized long nextThreadID()
  {
    return ++threadSeqNumber;
  }
  
  void blockedOn(Interruptible paramInterruptible)
  {
    synchronized (this.blockerLock)
    {
      this.blocker = paramInterruptible;
    }
  }
  
  public static native Thread currentThread();
  
  public static native void yield();
  
  public static native void sleep(long paramLong)
    throws InterruptedException;
  
  public static void sleep(long paramLong, int paramInt)
    throws InterruptedException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("timeout value is negative");
    }
    if ((paramInt < 0) || (paramInt > 999999)) {
      throw new IllegalArgumentException("nanosecond timeout value out of range");
    }
    if ((paramInt >= 500000) || ((paramInt != 0) && (paramLong == 0L))) {
      paramLong += 1L;
    }
    sleep(paramLong);
  }
  
  private void init(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, long paramLong)
  {
    init(paramThreadGroup, paramRunnable, paramString, paramLong, null);
  }
  
  private void init(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, long paramLong, AccessControlContext paramAccessControlContext)
  {
    if (paramString == null) {
      throw new NullPointerException("name cannot be null");
    }
    this.name = paramString.toCharArray();
    Thread localThread = currentThread();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (paramThreadGroup == null)
    {
      if (localSecurityManager != null) {
        paramThreadGroup = localSecurityManager.getThreadGroup();
      }
      if (paramThreadGroup == null) {
        paramThreadGroup = localThread.getThreadGroup();
      }
    }
    paramThreadGroup.checkAccess();
    if ((localSecurityManager != null) && (isCCLOverridden(getClass()))) {
      localSecurityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
    }
    paramThreadGroup.addUnstarted();
    this.group = paramThreadGroup;
    this.daemon = localThread.isDaemon();
    this.priority = localThread.getPriority();
    if ((localSecurityManager == null) || (isCCLOverridden(localThread.getClass()))) {
      this.contextClassLoader = localThread.getContextClassLoader();
    } else {
      this.contextClassLoader = localThread.contextClassLoader;
    }
    this.inheritedAccessControlContext = (paramAccessControlContext != null ? paramAccessControlContext : AccessController.getContext());
    this.target = paramRunnable;
    setPriority(this.priority);
    if (localThread.inheritableThreadLocals != null) {
      this.inheritableThreadLocals = ThreadLocal.createInheritedMap(localThread.inheritableThreadLocals);
    }
    this.stackSize = paramLong;
    this.tid = nextThreadID();
  }
  
  protected Object clone()
    throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException();
  }
  
  public Thread()
  {
    init(null, null, "Thread-" + nextThreadNum(), 0L);
  }
  
  public Thread(Runnable paramRunnable)
  {
    init(null, paramRunnable, "Thread-" + nextThreadNum(), 0L);
  }
  
  Thread(Runnable paramRunnable, AccessControlContext paramAccessControlContext)
  {
    init(null, paramRunnable, "Thread-" + nextThreadNum(), 0L, paramAccessControlContext);
  }
  
  public Thread(ThreadGroup paramThreadGroup, Runnable paramRunnable)
  {
    init(paramThreadGroup, paramRunnable, "Thread-" + nextThreadNum(), 0L);
  }
  
  public Thread(String paramString)
  {
    init(null, null, paramString, 0L);
  }
  
  public Thread(ThreadGroup paramThreadGroup, String paramString)
  {
    init(paramThreadGroup, null, paramString, 0L);
  }
  
  public Thread(Runnable paramRunnable, String paramString)
  {
    init(null, paramRunnable, paramString, 0L);
  }
  
  public Thread(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString)
  {
    init(paramThreadGroup, paramRunnable, paramString, 0L);
  }
  
  public Thread(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, long paramLong)
  {
    init(paramThreadGroup, paramRunnable, paramString, paramLong);
  }
  
  public synchronized void start()
  {
    if (this.threadStatus != 0) {
      throw new IllegalThreadStateException();
    }
    this.group.add(this);
    int i = 0;
    try
    {
      start0();
      i = 1;
      return;
    }
    finally
    {
      try
      {
        if (i == 0) {
          this.group.threadStartFailed(this);
        }
      }
      catch (Throwable localThrowable2) {}
    }
  }
  
  private native void start0();
  
  public void run()
  {
    if (this.target != null) {
      this.target.run();
    }
  }
  
  private void exit()
  {
    if (this.group != null)
    {
      this.group.threadTerminated(this);
      this.group = null;
    }
    this.target = null;
    this.threadLocals = null;
    this.inheritableThreadLocals = null;
    this.inheritedAccessControlContext = null;
    this.blocker = null;
    this.uncaughtExceptionHandler = null;
  }
  
  @Deprecated
  public final void stop()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      checkAccess();
      if (this != currentThread()) {
        localSecurityManager.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
      }
    }
    if (this.threadStatus != 0) {
      resume();
    }
    stop0(new ThreadDeath());
  }
  
  @Deprecated
  public final synchronized void stop(Throwable paramThrowable)
  {
    throw new UnsupportedOperationException();
  }
  
  public void interrupt()
  {
    if (this != currentThread()) {
      checkAccess();
    }
    synchronized (this.blockerLock)
    {
      Interruptible localInterruptible = this.blocker;
      if (localInterruptible != null)
      {
        interrupt0();
        localInterruptible.interrupt(this);
        return;
      }
    }
    interrupt0();
  }
  
  public static boolean interrupted()
  {
    return currentThread().isInterrupted(true);
  }
  
  public boolean isInterrupted()
  {
    return isInterrupted(false);
  }
  
  private native boolean isInterrupted(boolean paramBoolean);
  
  @Deprecated
  public void destroy()
  {
    throw new NoSuchMethodError();
  }
  
  public final native boolean isAlive();
  
  @Deprecated
  public final void suspend()
  {
    checkAccess();
    suspend0();
  }
  
  @Deprecated
  public final void resume()
  {
    checkAccess();
    resume0();
  }
  
  public final void setPriority(int paramInt)
  {
    checkAccess();
    if ((paramInt > 10) || (paramInt < 1)) {
      throw new IllegalArgumentException();
    }
    ThreadGroup localThreadGroup;
    if ((localThreadGroup = getThreadGroup()) != null)
    {
      if (paramInt > localThreadGroup.getMaxPriority()) {
        paramInt = localThreadGroup.getMaxPriority();
      }
      setPriority0(this.priority = paramInt);
    }
  }
  
  public final int getPriority()
  {
    return this.priority;
  }
  
  public final synchronized void setName(String paramString)
  {
    checkAccess();
    this.name = paramString.toCharArray();
    if (this.threadStatus != 0) {
      setNativeName(paramString);
    }
  }
  
  public final String getName()
  {
    return new String(this.name, true);
  }
  
  public final ThreadGroup getThreadGroup()
  {
    return this.group;
  }
  
  public static int activeCount()
  {
    return currentThread().getThreadGroup().activeCount();
  }
  
  public static int enumerate(Thread[] paramArrayOfThread)
  {
    return currentThread().getThreadGroup().enumerate(paramArrayOfThread);
  }
  
  @Deprecated
  public native int countStackFrames();
  
  public final synchronized void join(long paramLong)
    throws InterruptedException
  {
    long l1 = System.currentTimeMillis();
    long l2 = 0L;
    if (paramLong < 0L) {
      throw new IllegalArgumentException("timeout value is negative");
    }
    if (paramLong == 0L) {
      while (isAlive()) {
        wait(0L);
      }
    }
    while (isAlive())
    {
      long l3 = paramLong - l2;
      if (l3 <= 0L) {
        break;
      }
      wait(l3);
      l2 = System.currentTimeMillis() - l1;
    }
  }
  
  public final synchronized void join(long paramLong, int paramInt)
    throws InterruptedException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("timeout value is negative");
    }
    if ((paramInt < 0) || (paramInt > 999999)) {
      throw new IllegalArgumentException("nanosecond timeout value out of range");
    }
    if ((paramInt >= 500000) || ((paramInt != 0) && (paramLong == 0L))) {
      paramLong += 1L;
    }
    join(paramLong);
  }
  
  public final void join()
    throws InterruptedException
  {
    join(0L);
  }
  
  public static void dumpStack()
  {
    new Exception("Stack trace").printStackTrace();
  }
  
  public final void setDaemon(boolean paramBoolean)
  {
    checkAccess();
    if (isAlive()) {
      throw new IllegalThreadStateException();
    }
    this.daemon = paramBoolean;
  }
  
  public final boolean isDaemon()
  {
    return this.daemon;
  }
  
  public final void checkAccess()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkAccess(this);
    }
  }
  
  public String toString()
  {
    ThreadGroup localThreadGroup = getThreadGroup();
    if (localThreadGroup != null) {
      return "Thread[" + getName() + "," + getPriority() + "," + localThreadGroup.getName() + "]";
    }
    return "Thread[" + getName() + "," + getPriority() + "," + "" + "]";
  }
  
  @CallerSensitive
  public ClassLoader getContextClassLoader()
  {
    if (this.contextClassLoader == null) {
      return null;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      ClassLoader.checkClassLoaderPermission(this.contextClassLoader, Reflection.getCallerClass());
    }
    return this.contextClassLoader;
  }
  
  public void setContextClassLoader(ClassLoader paramClassLoader)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("setContextClassLoader"));
    }
    this.contextClassLoader = paramClassLoader;
  }
  
  public static native boolean holdsLock(Object paramObject);
  
  public StackTraceElement[] getStackTrace()
  {
    if (this != currentThread())
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(SecurityConstants.GET_STACK_TRACE_PERMISSION);
      }
      if (!isAlive()) {
        return EMPTY_STACK_TRACE;
      }
      StackTraceElement[][] arrayOfStackTraceElement = dumpThreads(new Thread[] { this });
      StackTraceElement[] arrayOfStackTraceElement1 = arrayOfStackTraceElement[0];
      if (arrayOfStackTraceElement1 == null) {
        arrayOfStackTraceElement1 = EMPTY_STACK_TRACE;
      }
      return arrayOfStackTraceElement1;
    }
    return new Exception().getStackTrace();
  }
  
  public static Map<Thread, StackTraceElement[]> getAllStackTraces()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(SecurityConstants.GET_STACK_TRACE_PERMISSION);
      localSecurityManager.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
    }
    Thread[] arrayOfThread = getThreads();
    StackTraceElement[][] arrayOfStackTraceElement = dumpThreads(arrayOfThread);
    HashMap localHashMap = new HashMap(arrayOfThread.length);
    for (int i = 0; i < arrayOfThread.length; i++)
    {
      StackTraceElement[] arrayOfStackTraceElement1 = arrayOfStackTraceElement[i];
      if (arrayOfStackTraceElement1 != null) {
        localHashMap.put(arrayOfThread[i], arrayOfStackTraceElement1);
      }
    }
    return localHashMap;
  }
  
  private static boolean isCCLOverridden(Class<?> paramClass)
  {
    if (paramClass == Thread.class) {
      return false;
    }
    processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
    WeakClassKey localWeakClassKey = new WeakClassKey(paramClass, Caches.subclassAuditsQueue);
    Boolean localBoolean = (Boolean)Caches.subclassAudits.get(localWeakClassKey);
    if (localBoolean == null)
    {
      localBoolean = Boolean.valueOf(auditSubclass(paramClass));
      Caches.subclassAudits.putIfAbsent(localWeakClassKey, localBoolean);
    }
    return localBoolean.booleanValue();
  }
  
  private static boolean auditSubclass(Class<?> paramClass)
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        Class localClass = this.val$subcl;
        while (localClass != Thread.class) {
          try
          {
            localClass.getDeclaredMethod("getContextClassLoader", new Class[0]);
            return Boolean.TRUE;
          }
          catch (NoSuchMethodException localNoSuchMethodException1)
          {
            try
            {
              Class[] arrayOfClass = { ClassLoader.class };
              localClass.getDeclaredMethod("setContextClassLoader", arrayOfClass);
              return Boolean.TRUE;
            }
            catch (NoSuchMethodException localNoSuchMethodException2)
            {
              localClass = localClass.getSuperclass();
            }
          }
        }
        return Boolean.FALSE;
      }
    });
    return localBoolean.booleanValue();
  }
  
  private static native StackTraceElement[][] dumpThreads(Thread[] paramArrayOfThread);
  
  private static native Thread[] getThreads();
  
  public long getId()
  {
    return this.tid;
  }
  
  public State getState()
  {
    return VM.toThreadState(this.threadStatus);
  }
  
  public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler paramUncaughtExceptionHandler)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("setDefaultUncaughtExceptionHandler"));
    }
    defaultUncaughtExceptionHandler = paramUncaughtExceptionHandler;
  }
  
  public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler()
  {
    return defaultUncaughtExceptionHandler;
  }
  
  public UncaughtExceptionHandler getUncaughtExceptionHandler()
  {
    return this.uncaughtExceptionHandler != null ? this.uncaughtExceptionHandler : this.group;
  }
  
  public void setUncaughtExceptionHandler(UncaughtExceptionHandler paramUncaughtExceptionHandler)
  {
    checkAccess();
    this.uncaughtExceptionHandler = paramUncaughtExceptionHandler;
  }
  
  private void dispatchUncaughtException(Throwable paramThrowable)
  {
    getUncaughtExceptionHandler().uncaughtException(this, paramThrowable);
  }
  
  static void processQueue(ReferenceQueue<Class<?>> paramReferenceQueue, ConcurrentMap<? extends WeakReference<Class<?>>, ?> paramConcurrentMap)
  {
    Reference localReference;
    while ((localReference = paramReferenceQueue.poll()) != null) {
      paramConcurrentMap.remove(localReference);
    }
  }
  
  private native void setPriority0(int paramInt);
  
  private native void stop0(Object paramObject);
  
  private native void suspend0();
  
  private native void resume0();
  
  private native void interrupt0();
  
  private native void setNativeName(String paramString);
  
  static {}
  
  private static class Caches
  {
    static final ConcurrentMap<Thread.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
    static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
    
    private Caches() {}
  }
  
  public static enum State
  {
    NEW,  RUNNABLE,  BLOCKED,  WAITING,  TIMED_WAITING,  TERMINATED;
    
    private State() {}
  }
  
  @FunctionalInterface
  public static abstract interface UncaughtExceptionHandler
  {
    public abstract void uncaughtException(Thread paramThread, Throwable paramThrowable);
  }
  
  static class WeakClassKey
    extends WeakReference<Class<?>>
  {
    private final int hash;
    
    WeakClassKey(Class<?> paramClass, ReferenceQueue<Class<?>> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      this.hash = System.identityHashCode(paramClass);
    }
    
    public int hashCode()
    {
      return this.hash;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof WeakClassKey))
      {
        Object localObject = get();
        return (localObject != null) && (localObject == ((WeakClassKey)paramObject).get());
      }
      return false;
    }
  }
}
