package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Cancelable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.ThreadLocalContainerResolver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public final class Fiber
  implements Runnable, Cancelable, ComponentRegistry
{
  private final List<Listener> _listeners = new ArrayList();
  private Tube[] conts = new Tube[16];
  private int contsSize;
  private Tube next;
  private Packet packet;
  private Throwable throwable;
  public final Engine owner;
  private volatile int suspendedCount = 0;
  private volatile boolean isInsideSuspendCallbacks = false;
  private boolean synchronous;
  private boolean interrupted;
  private final int id;
  private List<FiberContextSwitchInterceptor> interceptors;
  @Nullable
  private ClassLoader contextClassLoader;
  @Nullable
  private CompletionCallback completionCallback;
  private boolean isDeliverThrowableInPacket = false;
  private Thread currentThread;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = this.lock.newCondition();
  private volatile boolean isCanceled;
  private boolean started;
  private boolean startedSync;
  private static final PlaceholderTube PLACEHOLDER = new PlaceholderTube(null);
  private static final ThreadLocal<Fiber> CURRENT_FIBER = new ThreadLocal();
  private static final AtomicInteger iotaGen = new AtomicInteger();
  private static final Logger LOGGER = Logger.getLogger(Fiber.class.getName());
  private static final ReentrantLock serializedExecutionLock = new ReentrantLock();
  public static volatile boolean serializeExecution = Boolean.getBoolean(Fiber.class.getName() + ".serialize");
  private final Set<Component> components = new CopyOnWriteArraySet();
  
  /**
   * @deprecated
   */
  public void addListener(Listener paramListener)
  {
    synchronized (this._listeners)
    {
      if (!this._listeners.contains(paramListener)) {
        this._listeners.add(paramListener);
      }
    }
  }
  
  /**
   * @deprecated
   */
  public void removeListener(Listener paramListener)
  {
    synchronized (this._listeners)
    {
      this._listeners.remove(paramListener);
    }
  }
  
  List<Listener> getCurrentListeners()
  {
    synchronized (this._listeners)
    {
      return new ArrayList(this._listeners);
    }
  }
  
  private void clearListeners()
  {
    synchronized (this._listeners)
    {
      this._listeners.clear();
    }
  }
  
  public void setDeliverThrowableInPacket(boolean paramBoolean)
  {
    this.isDeliverThrowableInPacket = paramBoolean;
  }
  
  Fiber(Engine paramEngine)
  {
    this.owner = paramEngine;
    this.id = iotaGen.incrementAndGet();
    if (isTraceEnabled()) {
      LOGGER.log(Level.FINE, "{0} created", getName());
    }
    this.contextClassLoader = Thread.currentThread().getContextClassLoader();
  }
  
  public void start(@NotNull Tube paramTube, @NotNull Packet paramPacket, @Nullable CompletionCallback paramCompletionCallback)
  {
    start(paramTube, paramPacket, paramCompletionCallback, false);
  }
  
  private void dumpFiberContext(String paramString)
  {
    if (isTraceEnabled())
    {
      String str1 = null;
      String str2 = null;
      if (this.packet != null) {
        for (SOAPVersion localSOAPVersion : SOAPVersion.values())
        {
          for (AddressingVersion localAddressingVersion : AddressingVersion.values())
          {
            str1 = this.packet.getMessage() != null ? AddressingUtils.getAction(this.packet.getMessage().getHeaders(), localAddressingVersion, localSOAPVersion) : null;
            str2 = this.packet.getMessage() != null ? AddressingUtils.getMessageID(this.packet.getMessage().getHeaders(), localAddressingVersion, localSOAPVersion) : null;
            if ((str1 != null) || (str2 != null)) {
              break;
            }
          }
          if ((str1 != null) || (str2 != null)) {
            break;
          }
        }
      }
      if ((str1 == null) && (str2 == null)) {
        ??? = "NO ACTION or MSG ID";
      } else {
        ??? = "'" + str1 + "' and msgId '" + str2 + "'";
      }
      String str3;
      if (this.next != null) {
        str3 = this.next.toString() + ".processRequest()";
      } else {
        str3 = peekCont() + ".processResponse()";
      }
      LOGGER.log(Level.FINE, "{0} {1} with {2} and ''current'' tube {3} from thread {4} with Packet: {5}", new Object[] { getName(), paramString, ???, str3, Thread.currentThread().getName(), this.packet != null ? this.packet.toShortString() : null });
    }
  }
  
  public void start(@NotNull Tube paramTube, @NotNull Packet paramPacket, @Nullable CompletionCallback paramCompletionCallback, boolean paramBoolean)
  {
    this.next = paramTube;
    this.packet = paramPacket;
    this.completionCallback = paramCompletionCallback;
    if (paramBoolean)
    {
      this.startedSync = true;
      dumpFiberContext("starting (sync)");
      run();
    }
    else
    {
      this.started = true;
      dumpFiberContext("starting (async)");
      this.owner.addRunnable(this);
    }
  }
  
  public void resume(@NotNull Packet paramPacket)
  {
    resume(paramPacket, false);
  }
  
  public void resume(@NotNull Packet paramPacket, boolean paramBoolean)
  {
    resume(paramPacket, paramBoolean, null);
  }
  
  public void resume(@NotNull Packet paramPacket, boolean paramBoolean, CompletionCallback paramCompletionCallback)
  {
    this.lock.lock();
    try
    {
      if (paramCompletionCallback != null) {
        setCompletionCallback(paramCompletionCallback);
      }
      if (isTraceEnabled()) {
        LOGGER.log(Level.FINE, "{0} resuming. Will have suspendedCount={1}", new Object[] { getName(), Integer.valueOf(this.suspendedCount - 1) });
      }
      this.packet = paramPacket;
      if (--this.suspendedCount == 0)
      {
        if (!this.isInsideSuspendCallbacks)
        {
          List localList = getCurrentListeners();
          Iterator localIterator = localList.iterator();
          while (localIterator.hasNext())
          {
            Listener localListener = (Listener)localIterator.next();
            try
            {
              localListener.fiberResumed(this);
            }
            catch (Throwable localThrowable)
            {
              if (isTraceEnabled()) {
                LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { localListener, localThrowable.getMessage() });
              }
            }
          }
          if (this.synchronous)
          {
            this.condition.signalAll();
          }
          else if ((paramBoolean) || (this.startedSync))
          {
            run();
          }
          else
          {
            dumpFiberContext("resuming (async)");
            this.owner.addRunnable(this);
          }
        }
      }
      else if (isTraceEnabled()) {
        LOGGER.log(Level.FINE, "{0} taking no action on resume because suspendedCount != 0: {1}", new Object[] { getName(), Integer.valueOf(this.suspendedCount) });
      }
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  public void resumeAndReturn(@NotNull Packet paramPacket, boolean paramBoolean)
  {
    if (isTraceEnabled()) {
      LOGGER.log(Level.FINE, "{0} resumed with Return Packet", getName());
    }
    this.next = null;
    resume(paramPacket, paramBoolean);
  }
  
  public void resume(@NotNull Throwable paramThrowable)
  {
    resume(paramThrowable, this.packet, false);
  }
  
  public void resume(@NotNull Throwable paramThrowable, @NotNull Packet paramPacket)
  {
    resume(paramThrowable, paramPacket, false);
  }
  
  public void resume(@NotNull Throwable paramThrowable, boolean paramBoolean)
  {
    resume(paramThrowable, this.packet, paramBoolean);
  }
  
  public void resume(@NotNull Throwable paramThrowable, @NotNull Packet paramPacket, boolean paramBoolean)
  {
    if (isTraceEnabled()) {
      LOGGER.log(Level.FINE, "{0} resumed with Return Throwable", getName());
    }
    this.next = null;
    this.throwable = paramThrowable;
    resume(paramPacket, paramBoolean);
  }
  
  public void cancel(boolean paramBoolean)
  {
    this.isCanceled = true;
    if (paramBoolean) {
      synchronized (this)
      {
        if (this.currentThread != null) {
          this.currentThread.interrupt();
        }
      }
    }
  }
  
  private boolean suspend(Holder<Boolean> paramHolder, Runnable paramRunnable)
  {
    if (isTraceEnabled())
    {
      LOGGER.log(Level.FINE, "{0} suspending. Will have suspendedCount={1}", new Object[] { getName(), Integer.valueOf(this.suspendedCount + 1) });
      if (this.suspendedCount > 0) {
        LOGGER.log(Level.FINE, "WARNING - {0} suspended more than resumed. Will require more than one resume to actually resume this fiber.", getName());
      }
    }
    List localList = getCurrentListeners();
    Iterator localIterator;
    Listener localListener;
    if (++this.suspendedCount == 1)
    {
      this.isInsideSuspendCallbacks = true;
      try
      {
        localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          localListener = (Listener)localIterator.next();
          try
          {
            localListener.fiberSuspended(this);
          }
          catch (Throwable localThrowable2)
          {
            if (isTraceEnabled()) {
              LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { localListener, localThrowable2.getMessage() });
            }
          }
        }
      }
      finally
      {
        this.isInsideSuspendCallbacks = false;
      }
    }
    if (this.suspendedCount <= 0)
    {
      localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        localListener = (Listener)localIterator.next();
        try
        {
          localListener.fiberResumed(this);
        }
        catch (Throwable localThrowable3)
        {
          if (isTraceEnabled()) {
            LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { localListener, localThrowable3.getMessage() });
          }
        }
      }
    }
    else if (paramRunnable != null)
    {
      if (!this.synchronous)
      {
        synchronized (this)
        {
          this.currentThread = null;
        }
        this.lock.unlock();
        assert (!this.lock.isHeldByCurrentThread());
        paramHolder.value = Boolean.FALSE;
        try
        {
          paramRunnable.run();
        }
        catch (Throwable localThrowable1)
        {
          throw new OnExitRunnableException(localThrowable1);
        }
        return true;
      }
      if (isTraceEnabled()) {
        LOGGER.fine("onExitRunnable used with synchronous Fiber execution -- not exiting current thread");
      }
      paramRunnable.run();
    }
    return false;
  }
  
  public synchronized void addInterceptor(@NotNull FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor)
  {
    if (this.interceptors == null)
    {
      this.interceptors = new ArrayList();
    }
    else
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.addAll(this.interceptors);
      this.interceptors = localArrayList;
    }
    this.interceptors.add(paramFiberContextSwitchInterceptor);
  }
  
  public synchronized boolean removeInterceptor(@NotNull FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor)
  {
    if (this.interceptors != null)
    {
      boolean bool = this.interceptors.remove(paramFiberContextSwitchInterceptor);
      if (this.interceptors.isEmpty())
      {
        this.interceptors = null;
      }
      else
      {
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(this.interceptors);
        this.interceptors = localArrayList;
      }
      return bool;
    }
    return false;
  }
  
  @Nullable
  public ClassLoader getContextClassLoader()
  {
    return this.contextClassLoader;
  }
  
  public ClassLoader setContextClassLoader(@Nullable ClassLoader paramClassLoader)
  {
    ClassLoader localClassLoader = this.contextClassLoader;
    this.contextClassLoader = paramClassLoader;
    return localClassLoader;
  }
  
  @Deprecated
  public void run()
  {
    Container localContainer = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
    try
    {
      assert (!this.synchronous);
      if (!doRun()) {
        if ((this.startedSync) && (this.suspendedCount == 0) && ((this.next != null) || (this.contsSize > 0)))
        {
          this.startedSync = false;
          dumpFiberContext("restarting (async) after startSync");
          this.owner.addRunnable(this);
        }
        else
        {
          completionCheck();
        }
      }
      ContainerResolver.getDefault().exitContainer(localContainer);
    }
    finally
    {
      ContainerResolver.getDefault().exitContainer(localContainer);
    }
  }
  
  /* Error */
  @NotNull
  public Packet runSync(@NotNull Tube paramTube, @NotNull Packet paramPacket)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 681	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   7: aload_0
    //   8: getfield 585	com/sun/xml/internal/ws/api/pipe/Fiber:conts	[Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   11: astore_3
    //   12: aload_0
    //   13: getfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   16: istore 4
    //   18: aload_0
    //   19: getfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   22: istore 5
    //   24: aload_0
    //   25: getfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   28: astore 6
    //   30: iload 4
    //   32: ifle +17 -> 49
    //   35: aload_0
    //   36: bipush 16
    //   38: anewarray 308	com/sun/xml/internal/ws/api/pipe/Tube
    //   41: putfield 585	com/sun/xml/internal/ws/api/pipe/Fiber:conts	[Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   44: aload_0
    //   45: iconst_0
    //   46: putfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   49: aload_0
    //   50: iconst_1
    //   51: putfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   54: aload_0
    //   55: aload_2
    //   56: putfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   59: aload_0
    //   60: aload_1
    //   61: putfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   64: aload_0
    //   65: invokespecial 622	com/sun/xml/internal/ws/api/pipe/Fiber:doRun	()Z
    //   68: pop
    //   69: aload_0
    //   70: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   73: ifnull +79 -> 152
    //   76: aload_0
    //   77: getfield 574	com/sun/xml/internal/ws/api/pipe/Fiber:isDeliverThrowableInPacket	Z
    //   80: ifeq +24 -> 104
    //   83: aload_0
    //   84: getfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   87: new 307	com/sun/xml/internal/ws/api/pipe/ThrowableContainerPropertySet
    //   90: dup
    //   91: aload_0
    //   92: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   95: invokespecial 644	com/sun/xml/internal/ws/api/pipe/ThrowableContainerPropertySet:<init>	(Ljava/lang/Throwable;)V
    //   98: invokevirtual 614	com/sun/xml/internal/ws/api/message/Packet:addSatellite	(Lcom/oracle/webservices/internal/api/message/PropertySet;)V
    //   101: goto +51 -> 152
    //   104: aload_0
    //   105: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   108: instanceof 322
    //   111: ifeq +11 -> 122
    //   114: aload_0
    //   115: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   118: checkcast 322	java/lang/RuntimeException
    //   121: athrow
    //   122: aload_0
    //   123: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   126: instanceof 316
    //   129: ifeq +11 -> 140
    //   132: aload_0
    //   133: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   136: checkcast 316	java/lang/Error
    //   139: athrow
    //   140: new 312	java/lang/AssertionError
    //   143: dup
    //   144: aload_0
    //   145: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   148: invokespecial 649	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
    //   151: athrow
    //   152: aload_0
    //   153: getfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   156: astore 7
    //   158: aload_0
    //   159: aload_3
    //   160: putfield 585	com/sun/xml/internal/ws/api/pipe/Fiber:conts	[Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   163: aload_0
    //   164: iload 4
    //   166: putfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   169: aload_0
    //   170: iload 5
    //   172: putfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   175: aload_0
    //   176: aload 6
    //   178: putfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   181: aload_0
    //   182: getfield 572	com/sun/xml/internal/ws/api/pipe/Fiber:interrupted	Z
    //   185: ifeq +14 -> 199
    //   188: invokestatic 670	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   191: invokevirtual 666	java/lang/Thread:interrupt	()V
    //   194: aload_0
    //   195: iconst_0
    //   196: putfield 572	com/sun/xml/internal/ws/api/pipe/Fiber:interrupted	Z
    //   199: aload_0
    //   200: getfield 577	com/sun/xml/internal/ws/api/pipe/Fiber:started	Z
    //   203: ifne +14 -> 217
    //   206: aload_0
    //   207: getfield 578	com/sun/xml/internal/ws/api/pipe/Fiber:startedSync	Z
    //   210: ifne +7 -> 217
    //   213: aload_0
    //   214: invokespecial 620	com/sun/xml/internal/ws/api/pipe/Fiber:completionCheck	()V
    //   217: aload_0
    //   218: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   221: invokevirtual 682	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   224: aload 7
    //   226: areturn
    //   227: astore 8
    //   229: aload_0
    //   230: aload_3
    //   231: putfield 585	com/sun/xml/internal/ws/api/pipe/Fiber:conts	[Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   234: aload_0
    //   235: iload 4
    //   237: putfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   240: aload_0
    //   241: iload 5
    //   243: putfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   246: aload_0
    //   247: aload 6
    //   249: putfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   252: aload_0
    //   253: getfield 572	com/sun/xml/internal/ws/api/pipe/Fiber:interrupted	Z
    //   256: ifeq +14 -> 270
    //   259: invokestatic 670	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   262: invokevirtual 666	java/lang/Thread:interrupt	()V
    //   265: aload_0
    //   266: iconst_0
    //   267: putfield 572	com/sun/xml/internal/ws/api/pipe/Fiber:interrupted	Z
    //   270: aload_0
    //   271: getfield 577	com/sun/xml/internal/ws/api/pipe/Fiber:started	Z
    //   274: ifne +14 -> 288
    //   277: aload_0
    //   278: getfield 578	com/sun/xml/internal/ws/api/pipe/Fiber:startedSync	Z
    //   281: ifne +7 -> 288
    //   284: aload_0
    //   285: invokespecial 620	com/sun/xml/internal/ws/api/pipe/Fiber:completionCheck	()V
    //   288: aload 8
    //   290: athrow
    //   291: astore 9
    //   293: aload_0
    //   294: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   297: invokevirtual 682	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   300: aload 9
    //   302: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	303	0	this	Fiber
    //   0	303	1	paramTube	Tube
    //   0	303	2	paramPacket	Packet
    //   11	220	3	arrayOfTube	Tube[]
    //   16	220	4	i	int
    //   22	220	5	bool	boolean
    //   28	220	6	localTube	Tube
    //   156	69	7	localPacket	Packet
    //   227	62	8	localObject1	Object
    //   291	10	9	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   49	158	227	finally
    //   227	229	227	finally
    //   7	217	291	finally
    //   227	293	291	finally
  }
  
  private void completionCheck()
  {
    this.lock.lock();
    try
    {
      if ((!this.isCanceled) && (this.contsSize == 0) && (this.suspendedCount == 0))
      {
        if (isTraceEnabled()) {
          LOGGER.log(Level.FINE, "{0} completed", getName());
        }
        clearListeners();
        this.condition.signalAll();
        if (this.completionCallback != null) {
          if (this.throwable != null)
          {
            if (this.isDeliverThrowableInPacket)
            {
              this.packet.addSatellite(new ThrowableContainerPropertySet(this.throwable));
              this.completionCallback.onCompletion(this.packet);
            }
            else
            {
              this.completionCallback.onCompletion(this.throwable);
            }
          }
          else {
            this.completionCallback.onCompletion(this.packet);
          }
        }
      }
      this.lock.unlock();
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  private boolean doRun()
  {
    dumpFiberContext("running");
    if (serializeExecution)
    {
      serializedExecutionLock.lock();
      try
      {
        boolean bool = _doRun(this.next);
        return bool;
      }
      finally
      {
        serializedExecutionLock.unlock();
      }
    }
    return _doRun(this.next);
  }
  
  private boolean _doRun(Tube paramTube)
  {
    Holder localHolder = new Holder(Boolean.TRUE);
    this.lock.lock();
    try
    {
      List localList;
      ClassLoader localClassLoader;
      synchronized (this)
      {
        localList = this.interceptors;
        this.currentThread = Thread.currentThread();
        if (isTraceEnabled()) {
          LOGGER.log(Level.FINE, "Thread entering _doRun(): {0}", this.currentThread);
        }
        localClassLoader = this.currentThread.getContextClassLoader();
        this.currentThread.setContextClassLoader(this.contextClassLoader);
      }
      try
      {
        int i;
        do
        {
          boolean bool2;
          if (localList == null)
          {
            this.next = paramTube;
            if (__doRun(localHolder, null))
            {
              bool2 = true;
              Thread localThread2 = Thread.currentThread();
              localThread2.setContextClassLoader(localClassLoader);
              if (isTraceEnabled()) {
                LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", localThread2);
              }
              return bool2;
            }
          }
          else
          {
            paramTube = new InterceptorHandler(localHolder, localList).invoke(paramTube);
            if (paramTube == PLACEHOLDER)
            {
              bool2 = true;
              ??? = Thread.currentThread();
              ((Thread)???).setContextClassLoader(localClassLoader);
              if (isTraceEnabled()) {
                LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", ???);
              }
              return bool2;
            }
          }
          synchronized (this)
          {
            i = localList != this.interceptors ? 1 : 0;
            if (i != 0) {
              localList = this.interceptors;
            }
          }
        } while (i != 0);
      }
      catch (OnExitRunnableException localOnExitRunnableException)
      {
        Thread localThread1;
        ??? = localOnExitRunnableException.target;
        if ((??? instanceof WebServiceException)) {
          throw ((WebServiceException)???);
        }
        throw new WebServiceException((Throwable)???);
      }
      finally
      {
        Thread localThread3 = Thread.currentThread();
        localThread3.setContextClassLoader(localClassLoader);
        if (isTraceEnabled()) {
          LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", localThread3);
        }
      }
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      if (((Boolean)localHolder.value).booleanValue())
      {
        synchronized (this)
        {
          this.currentThread = null;
        }
        this.lock.unlock();
      }
    }
  }
  
  private boolean __doRun(Holder<Boolean> paramHolder, List<FiberContextSwitchInterceptor> paramList)
  {
    assert (this.lock.isHeldByCurrentThread());
    Fiber localFiber = (Fiber)CURRENT_FIBER.get();
    CURRENT_FIBER.set(this);
    boolean bool1 = LOGGER.isLoggable(Level.FINER);
    try
    {
      int i = 0;
      while (isReady(paramList))
      {
        if (this.isCanceled)
        {
          this.next = null;
          this.throwable = null;
          this.contsSize = 0;
          break;
        }
        try
        {
          boolean bool2;
          Tube localTube;
          NextAction localNextAction;
          if (this.throwable != null)
          {
            if ((this.contsSize == 0) || (i != 0))
            {
              this.contsSize = 0;
              bool2 = false;
              return bool2;
            }
            localTube = popCont();
            if (bool1) {
              LOGGER.log(Level.FINER, "{0} {1}.processException({2})", new Object[] { getName(), localTube, this.throwable });
            }
            localNextAction = localTube.processException(this.throwable);
          }
          else if (this.next != null)
          {
            if (bool1) {
              LOGGER.log(Level.FINER, "{0} {1}.processRequest({2})", new Object[] { getName(), this.next, this.packet != null ? "Packet@" + Integer.toHexString(this.packet.hashCode()) : "null" });
            }
            localNextAction = this.next.processRequest(this.packet);
            localTube = this.next;
          }
          else
          {
            if ((this.contsSize == 0) || (i != 0))
            {
              this.contsSize = 0;
              bool2 = false;
              return bool2;
            }
            localTube = popCont();
            if (bool1) {
              LOGGER.log(Level.FINER, "{0} {1}.processResponse({2})", new Object[] { getName(), localTube, this.packet != null ? "Packet@" + Integer.toHexString(this.packet.hashCode()) : "null" });
            }
            localNextAction = localTube.processResponse(this.packet);
          }
          if (bool1) {
            LOGGER.log(Level.FINER, "{0} {1} returned with {2}", new Object[] { getName(), localTube, localNextAction });
          }
          if (localNextAction.kind != 4)
          {
            if ((localNextAction.kind != 3) && (localNextAction.kind != 5)) {
              this.packet = localNextAction.packet;
            }
            this.throwable = localNextAction.throwable;
          }
          switch (localNextAction.kind)
          {
          case 0: 
          case 7: 
            pushCont(localTube);
          case 1: 
            this.next = localNextAction.next;
            if ((localNextAction.kind != 7) || (!this.startedSync)) {
              break label697;
            }
            bool2 = false;
            return bool2;
          case 5: 
          case 6: 
            i = 1;
            if (isTraceEnabled()) {
              LOGGER.log(Level.FINE, "Fiber {0} is aborting a response due to exception: {1}", new Object[] { this, localNextAction.throwable });
            }
          case 2: 
          case 3: 
            this.next = null;
            break;
          case 4: 
            if (this.next != null) {
              pushCont(localTube);
            }
            this.next = localNextAction.next;
            if (!suspend(paramHolder, localNextAction.onExitRunnable)) {
              break label697;
            }
            bool2 = true;
            return bool2;
          }
          throw new AssertionError();
        }
        catch (RuntimeException localRuntimeException)
        {
          if (bool1) {
            LOGGER.log(Level.FINER, getName() + " Caught " + localRuntimeException + ". Start stack unwinding", localRuntimeException);
          }
          this.throwable = localRuntimeException;
        }
        catch (Error localError)
        {
          label697:
          if (bool1) {
            LOGGER.log(Level.FINER, getName() + " Caught " + localError + ". Start stack unwinding", localError);
          }
          this.throwable = localError;
        }
        dumpFiberContext("After tube execution");
      }
    }
    finally
    {
      CURRENT_FIBER.set(localFiber);
    }
    return false;
  }
  
  private void pushCont(Tube paramTube)
  {
    this.conts[(this.contsSize++)] = paramTube;
    int i = this.conts.length;
    if (this.contsSize == i)
    {
      Tube[] arrayOfTube = new Tube[i * 2];
      System.arraycopy(this.conts, 0, arrayOfTube, 0, i);
      this.conts = arrayOfTube;
    }
  }
  
  private Tube popCont()
  {
    return this.conts[(--this.contsSize)];
  }
  
  private Tube peekCont()
  {
    int i = this.contsSize - 1;
    if ((i >= 0) && (i < this.conts.length)) {
      return this.conts[i];
    }
    return null;
  }
  
  public void resetCont(Tube[] paramArrayOfTube, int paramInt)
  {
    this.conts = paramArrayOfTube;
    this.contsSize = paramInt;
  }
  
  private boolean isReady(List<FiberContextSwitchInterceptor> paramList)
  {
    if (this.synchronous)
    {
      while (this.suspendedCount == 1) {
        try
        {
          if (isTraceEnabled()) {
            LOGGER.log(Level.FINE, "{0} is blocking thread {1}", new Object[] { getName(), Thread.currentThread().getName() });
          }
          this.condition.await();
        }
        catch (InterruptedException localInterruptedException)
        {
          this.interrupted = true;
        }
      }
      synchronized (this)
      {
        return this.interceptors == paramList;
      }
    }
    if (this.suspendedCount > 0) {
      return false;
    }
    synchronized (this)
    {
      return this.interceptors == paramList;
    }
  }
  
  private String getName()
  {
    return "engine-" + this.owner.id + "fiber-" + this.id;
  }
  
  public String toString()
  {
    return getName();
  }
  
  @Nullable
  public Packet getPacket()
  {
    return this.packet;
  }
  
  public CompletionCallback getCompletionCallback()
  {
    return this.completionCallback;
  }
  
  public void setCompletionCallback(CompletionCallback paramCompletionCallback)
  {
    this.completionCallback = paramCompletionCallback;
  }
  
  public static boolean isSynchronous()
  {
    return current().synchronous;
  }
  
  public boolean isStartedSync()
  {
    return this.startedSync;
  }
  
  @NotNull
  public static Fiber current()
  {
    Fiber localFiber = (Fiber)CURRENT_FIBER.get();
    if (localFiber == null) {
      throw new IllegalStateException("Can be only used from fibers");
    }
    return localFiber;
  }
  
  public static Fiber getCurrentIfSet()
  {
    return (Fiber)CURRENT_FIBER.get();
  }
  
  private static boolean isTraceEnabled()
  {
    return LOGGER.isLoggable(Level.FINE);
  }
  
  public <S> S getSPI(Class<S> paramClass)
  {
    Iterator localIterator = this.components.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      Object localObject = localComponent.getSPI(paramClass);
      if (localObject != null) {
        return localObject;
      }
    }
    return null;
  }
  
  public Set<Component> getComponents()
  {
    return this.components;
  }
  
  public static abstract interface CompletionCallback
  {
    public abstract void onCompletion(@NotNull Packet paramPacket);
    
    public abstract void onCompletion(@NotNull Throwable paramThrowable);
  }
  
  private class InterceptorHandler
    implements FiberContextSwitchInterceptor.Work<Tube, Tube>
  {
    private final Holder<Boolean> isUnlockRequired;
    private final List<FiberContextSwitchInterceptor> ints;
    private int idx;
    
    public InterceptorHandler(List<FiberContextSwitchInterceptor> paramList)
    {
      this.isUnlockRequired = paramList;
      Object localObject;
      this.ints = localObject;
    }
    
    Tube invoke(Tube paramTube)
    {
      this.idx = 0;
      return execute(paramTube);
    }
    
    public Tube execute(Tube paramTube)
    {
      if (this.idx == this.ints.size())
      {
        Fiber.this.next = paramTube;
        if (Fiber.this.__doRun(this.isUnlockRequired, this.ints)) {
          return Fiber.PLACEHOLDER;
        }
      }
      else
      {
        FiberContextSwitchInterceptor localFiberContextSwitchInterceptor = (FiberContextSwitchInterceptor)this.ints.get(this.idx++);
        return (Tube)localFiberContextSwitchInterceptor.execute(Fiber.this, paramTube, this);
      }
      return Fiber.this.next;
    }
  }
  
  /**
   * @deprecated
   */
  public static abstract interface Listener
  {
    public abstract void fiberSuspended(Fiber paramFiber);
    
    public abstract void fiberResumed(Fiber paramFiber);
  }
  
  private static final class OnExitRunnableException
    extends RuntimeException
  {
    private static final long serialVersionUID = 1L;
    Throwable target;
    
    public OnExitRunnableException(Throwable paramThrowable)
    {
      super();
      this.target = paramThrowable;
    }
  }
  
  private static class PlaceholderTube
    extends AbstractTubeImpl
  {
    private PlaceholderTube() {}
    
    public NextAction processRequest(Packet paramPacket)
    {
      throw new UnsupportedOperationException();
    }
    
    public NextAction processResponse(Packet paramPacket)
    {
      throw new UnsupportedOperationException();
    }
    
    public NextAction processException(Throwable paramThrowable)
    {
      return doThrow(paramThrowable);
    }
    
    public void preDestroy() {}
    
    public PlaceholderTube copy(TubeCloner paramTubeCloner)
    {
      throw new UnsupportedOperationException();
    }
  }
}
