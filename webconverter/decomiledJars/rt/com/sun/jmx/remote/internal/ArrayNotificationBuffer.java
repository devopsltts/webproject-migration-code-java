package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryEval;
import javax.management.QueryExp;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;

public class ArrayNotificationBuffer
  implements NotificationBuffer
{
  private boolean disposed = false;
  private static final Object globalLock = new Object();
  private static final HashMap<MBeanServer, ArrayNotificationBuffer> mbsToBuffer = new HashMap(1);
  private final Collection<ShareBuffer> sharers = new HashSet(1);
  private final NotificationListener bufferListener = new BufferListener(null);
  private static final QueryExp broadcasterQuery = new BroadcasterQuery(null);
  private static final NotificationFilter creationFilter;
  private final NotificationListener creationListener = new NotificationListener()
  {
    public void handleNotification(Notification paramAnonymousNotification, Object paramAnonymousObject)
    {
      ArrayNotificationBuffer.logger.debug("creationListener", "handleNotification called");
      ArrayNotificationBuffer.this.createdNotification((MBeanServerNotification)paramAnonymousNotification);
    }
  };
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ArrayNotificationBuffer");
  private final MBeanServer mBeanServer;
  private final ArrayQueue<NamedNotification> queue;
  private int queueSize;
  private long earliestSequenceNumber;
  private long nextSequenceNumber;
  private Set<ObjectName> createdDuringQuery;
  static final String broadcasterClass = NotificationBroadcaster.class.getName();
  
  public static NotificationBuffer getNotificationBuffer(MBeanServer paramMBeanServer, Map<String, ?> paramMap)
  {
    if (paramMap == null) {
      paramMap = Collections.emptyMap();
    }
    int i = EnvHelp.getNotifBufferSize(paramMap);
    ArrayNotificationBuffer localArrayNotificationBuffer;
    int j;
    ShareBuffer localShareBuffer;
    synchronized (globalLock)
    {
      localArrayNotificationBuffer = (ArrayNotificationBuffer)mbsToBuffer.get(paramMBeanServer);
      j = localArrayNotificationBuffer == null ? 1 : 0;
      if (j != 0)
      {
        localArrayNotificationBuffer = new ArrayNotificationBuffer(paramMBeanServer, i);
        mbsToBuffer.put(paramMBeanServer, localArrayNotificationBuffer);
      }
      ArrayNotificationBuffer tmp71_70 = localArrayNotificationBuffer;
      tmp71_70.getClass();
      localShareBuffer = new ShareBuffer(tmp71_70, i);
    }
    if (j != 0) {
      localArrayNotificationBuffer.createListeners();
    }
    return localShareBuffer;
  }
  
  static void removeNotificationBuffer(MBeanServer paramMBeanServer)
  {
    synchronized (globalLock)
    {
      mbsToBuffer.remove(paramMBeanServer);
    }
  }
  
  void addSharer(ShareBuffer paramShareBuffer)
  {
    synchronized (globalLock)
    {
      synchronized (this)
      {
        if (paramShareBuffer.getSize() > this.queueSize) {
          resize(paramShareBuffer.getSize());
        }
      }
      this.sharers.add(paramShareBuffer);
    }
  }
  
  private void removeSharer(ShareBuffer paramShareBuffer)
  {
    boolean bool;
    synchronized (globalLock)
    {
      this.sharers.remove(paramShareBuffer);
      bool = this.sharers.isEmpty();
      if (bool)
      {
        removeNotificationBuffer(this.mBeanServer);
      }
      else
      {
        int i = 0;
        Iterator localIterator = this.sharers.iterator();
        while (localIterator.hasNext())
        {
          ShareBuffer localShareBuffer = (ShareBuffer)localIterator.next();
          int j = localShareBuffer.getSize();
          if (j > i) {
            i = j;
          }
        }
        if (i < this.queueSize) {
          resize(i);
        }
      }
    }
    if (bool)
    {
      synchronized (this)
      {
        this.disposed = true;
        notifyAll();
      }
      destroyListeners();
    }
  }
  
  private synchronized void resize(int paramInt)
  {
    if (paramInt == this.queueSize) {
      return;
    }
    while (this.queue.size() > paramInt) {
      dropNotification();
    }
    this.queue.resize(paramInt);
    this.queueSize = paramInt;
  }
  
  private ArrayNotificationBuffer(MBeanServer paramMBeanServer, int paramInt)
  {
    if (logger.traceOn()) {
      logger.trace("Constructor", "queueSize=" + paramInt);
    }
    if ((paramMBeanServer == null) || (paramInt < 1)) {
      throw new IllegalArgumentException("Bad args");
    }
    this.mBeanServer = paramMBeanServer;
    this.queueSize = paramInt;
    this.queue = new ArrayQueue(paramInt);
    this.earliestSequenceNumber = System.currentTimeMillis();
    this.nextSequenceNumber = this.earliestSequenceNumber;
    logger.trace("Constructor", "ends");
  }
  
  private synchronized boolean isDisposed()
  {
    return this.disposed;
  }
  
  public void dispose()
  {
    throw new UnsupportedOperationException();
  }
  
  public NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt)
    throws InterruptedException
  {
    logger.trace("fetchNotifications", "starts");
    if ((paramLong1 < 0L) || (isDisposed())) {
      synchronized (this)
      {
        return new NotificationResult(earliestSequenceNumber(), nextSequenceNumber(), new TargetedNotification[0]);
      }
    }
    if ((paramNotificationBufferFilter == null) || (paramLong1 < 0L) || (paramLong2 < 0L) || (paramInt < 0))
    {
      logger.trace("fetchNotifications", "Bad args");
      throw new IllegalArgumentException("Bad args to fetch");
    }
    if (logger.debugOn()) {
      logger.trace("fetchNotifications", "filter=" + paramNotificationBufferFilter + "; startSeq=" + paramLong1 + "; timeout=" + paramLong2 + "; max=" + paramInt);
    }
    if (paramLong1 > nextSequenceNumber())
    {
      ??? = "Start sequence number too big: " + paramLong1 + " > " + nextSequenceNumber();
      logger.trace("fetchNotifications", (String)???);
      throw new IllegalArgumentException((String)???);
    }
    long l1 = System.currentTimeMillis() + paramLong2;
    if (l1 < 0L) {
      l1 = Long.MAX_VALUE;
    }
    if (logger.debugOn()) {
      logger.debug("fetchNotifications", "endTime=" + l1);
    }
    long l2 = -1L;
    long l3 = paramLong1;
    ArrayList localArrayList1 = new ArrayList();
    for (;;)
    {
      logger.debug("fetchNotifications", "main loop starts");
      NamedNotification localNamedNotification;
      synchronized (this)
      {
        if (l2 < 0L)
        {
          l2 = earliestSequenceNumber();
          if (logger.debugOn()) {
            logger.debug("fetchNotifications", "earliestSeq=" + l2);
          }
          if (l3 < l2)
          {
            l3 = l2;
            logger.debug("fetchNotifications", "nextSeq=earliestSeq");
          }
        }
        else
        {
          l2 = earliestSequenceNumber();
        }
        if (l3 < l2)
        {
          logger.trace("fetchNotifications", "nextSeq=" + l3 + " < " + "earliestSeq=" + l2 + " so may have lost notifs");
          break;
        }
        if (l3 < nextSequenceNumber())
        {
          localNamedNotification = notificationAt(l3);
          if (!(paramNotificationBufferFilter instanceof ServerNotifForwarder.NotifForwarderBufferFilter))
          {
            try
            {
              ServerNotifForwarder.checkMBeanPermission(this.mBeanServer, localNamedNotification.getObjectName(), "addNotificationListener");
            }
            catch (InstanceNotFoundException|SecurityException localInstanceNotFoundException)
            {
              if (logger.debugOn()) {
                logger.debug("fetchNotifications", "candidate: " + localNamedNotification + " skipped. exception " + localInstanceNotFoundException);
              }
              l3 += 1L;
            }
            continue;
          }
          if (logger.debugOn())
          {
            logger.debug("fetchNotifications", "candidate: " + localNamedNotification);
            logger.debug("fetchNotifications", "nextSeq now " + l3);
          }
        }
        else
        {
          if (localArrayList1.size() > 0)
          {
            logger.debug("fetchNotifications", "no more notifs but have some so don't wait");
            break;
          }
          long l4 = l1 - System.currentTimeMillis();
          if (l4 <= 0L)
          {
            logger.debug("fetchNotifications", "timeout");
            break;
          }
          if (isDisposed())
          {
            if (logger.debugOn()) {
              logger.debug("fetchNotifications", "dispose callled, no wait");
            }
            return new NotificationResult(earliestSequenceNumber(), nextSequenceNumber(), new TargetedNotification[0]);
          }
          if (logger.debugOn()) {
            logger.debug("fetchNotifications", "wait(" + l4 + ")");
          }
          wait(l4);
          continue;
        }
      }
      ??? = localNamedNotification.getObjectName();
      localObject2 = localNamedNotification.getNotification();
      ArrayList localArrayList2 = new ArrayList();
      logger.debug("fetchNotifications", "applying filter to candidate");
      paramNotificationBufferFilter.apply(localArrayList2, (ObjectName)???, (Notification)localObject2);
      if (localArrayList2.size() > 0)
      {
        if (paramInt <= 0)
        {
          logger.debug("fetchNotifications", "reached maxNotifications");
          break;
        }
        paramInt--;
        if (logger.debugOn()) {
          logger.debug("fetchNotifications", "add: " + localArrayList2);
        }
        localArrayList1.addAll(localArrayList2);
      }
      l3 += 1L;
    }
    int i = localArrayList1.size();
    ??? = new TargetedNotification[i];
    localArrayList1.toArray((Object[])???);
    Object localObject2 = new NotificationResult(l2, l3, (TargetedNotification[])???);
    if (logger.debugOn()) {
      logger.debug("fetchNotifications", ((NotificationResult)localObject2).toString());
    }
    logger.trace("fetchNotifications", "ends");
    return localObject2;
  }
  
  synchronized long earliestSequenceNumber()
  {
    return this.earliestSequenceNumber;
  }
  
  synchronized long nextSequenceNumber()
  {
    return this.nextSequenceNumber;
  }
  
  synchronized void addNotification(NamedNotification paramNamedNotification)
  {
    if (logger.traceOn()) {
      logger.trace("addNotification", paramNamedNotification.toString());
    }
    while (this.queue.size() >= this.queueSize)
    {
      dropNotification();
      if (logger.debugOn()) {
        logger.debug("addNotification", "dropped oldest notif, earliestSeq=" + this.earliestSequenceNumber);
      }
    }
    this.queue.add(paramNamedNotification);
    this.nextSequenceNumber += 1L;
    if (logger.debugOn()) {
      logger.debug("addNotification", "nextSeq=" + this.nextSequenceNumber);
    }
    notifyAll();
  }
  
  private void dropNotification()
  {
    this.queue.remove(0);
    this.earliestSequenceNumber += 1L;
  }
  
  synchronized NamedNotification notificationAt(long paramLong)
  {
    long l = paramLong - this.earliestSequenceNumber;
    if ((l < 0L) || (l > 2147483647L))
    {
      String str = "Bad sequence number: " + paramLong + " (earliest " + this.earliestSequenceNumber + ")";
      logger.trace("notificationAt", str);
      throw new IllegalArgumentException(str);
    }
    return (NamedNotification)this.queue.get((int)l);
  }
  
  private void createListeners()
  {
    logger.debug("createListeners", "starts");
    synchronized (this)
    {
      this.createdDuringQuery = new HashSet();
    }
    Object localObject3;
    try
    {
      addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener, creationFilter, null);
      logger.debug("createListeners", "added creationListener");
    }
    catch (Exception localException)
    {
      localObject3 = new IllegalArgumentException("Can't add listener to MBean server delegate: " + localException);
      EnvHelp.initCause((Throwable)localObject3, localException);
      logger.fine("createListeners", "Can't add listener to MBean server delegate: " + localException);
      logger.debug("createListeners", localException);
      throw ((Throwable)localObject3);
    }
    Object localObject1 = queryNames(null, broadcasterQuery);
    localObject1 = new HashSet((Collection)localObject1);
    synchronized (this)
    {
      ((Set)localObject1).addAll(this.createdDuringQuery);
      this.createdDuringQuery = null;
    }
    ??? = ((Set)localObject1).iterator();
    while (((Iterator)???).hasNext())
    {
      localObject3 = (ObjectName)((Iterator)???).next();
      addBufferListener((ObjectName)localObject3);
    }
    logger.debug("createListeners", "ends");
  }
  
  private void addBufferListener(ObjectName paramObjectName)
  {
    checkNoLocks();
    if (logger.debugOn()) {
      logger.debug("addBufferListener", paramObjectName.toString());
    }
    try
    {
      addNotificationListener(paramObjectName, this.bufferListener, null, paramObjectName);
    }
    catch (Exception localException)
    {
      logger.trace("addBufferListener", localException);
    }
  }
  
  private void removeBufferListener(ObjectName paramObjectName)
  {
    checkNoLocks();
    if (logger.debugOn()) {
      logger.debug("removeBufferListener", paramObjectName.toString());
    }
    try
    {
      removeNotificationListener(paramObjectName, this.bufferListener);
    }
    catch (Exception localException)
    {
      logger.trace("removeBufferListener", localException);
    }
  }
  
  private void addNotificationListener(final ObjectName paramObjectName, final NotificationListener paramNotificationListener, final NotificationFilter paramNotificationFilter, final Object paramObject)
    throws Exception
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws InstanceNotFoundException
        {
          ArrayNotificationBuffer.this.mBeanServer.addNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject);
          return null;
        }
      });
    }
    catch (Exception localException)
    {
      throw extractException(localException);
    }
  }
  
  private void removeNotificationListener(final ObjectName paramObjectName, final NotificationListener paramNotificationListener)
    throws Exception
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws Exception
        {
          ArrayNotificationBuffer.this.mBeanServer.removeNotificationListener(paramObjectName, paramNotificationListener);
          return null;
        }
      });
    }
    catch (Exception localException)
    {
      throw extractException(localException);
    }
  }
  
  private Set<ObjectName> queryNames(final ObjectName paramObjectName, final QueryExp paramQueryExp)
  {
    PrivilegedAction local3 = new PrivilegedAction()
    {
      public Set<ObjectName> run()
      {
        return ArrayNotificationBuffer.this.mBeanServer.queryNames(paramObjectName, paramQueryExp);
      }
    };
    try
    {
      return (Set)AccessController.doPrivileged(local3);
    }
    catch (RuntimeException localRuntimeException)
    {
      logger.fine("queryNames", "Failed to query names: " + localRuntimeException);
      logger.debug("queryNames", localRuntimeException);
      throw localRuntimeException;
    }
  }
  
  private static boolean isInstanceOf(MBeanServer paramMBeanServer, final ObjectName paramObjectName, final String paramString)
  {
    PrivilegedExceptionAction local4 = new PrivilegedExceptionAction()
    {
      public Boolean run()
        throws InstanceNotFoundException
      {
        return Boolean.valueOf(this.val$mbs.isInstanceOf(paramObjectName, paramString));
      }
    };
    try
    {
      return ((Boolean)AccessController.doPrivileged(local4)).booleanValue();
    }
    catch (Exception localException)
    {
      logger.fine("isInstanceOf", "failed: " + localException);
      logger.debug("isInstanceOf", localException);
    }
    return false;
  }
  
  private void createdNotification(MBeanServerNotification paramMBeanServerNotification)
  {
    if (!paramMBeanServerNotification.getType().equals("JMX.mbean.registered"))
    {
      logger.warning("createNotification", "bad type: " + paramMBeanServerNotification.getType());
      return;
    }
    ObjectName localObjectName = paramMBeanServerNotification.getMBeanName();
    if (logger.debugOn()) {
      logger.debug("createdNotification", "for: " + localObjectName);
    }
    synchronized (this)
    {
      if (this.createdDuringQuery != null)
      {
        this.createdDuringQuery.add(localObjectName);
        return;
      }
    }
    if (isInstanceOf(this.mBeanServer, localObjectName, broadcasterClass))
    {
      addBufferListener(localObjectName);
      if (isDisposed()) {
        removeBufferListener(localObjectName);
      }
    }
  }
  
  private void destroyListeners()
  {
    checkNoLocks();
    logger.debug("destroyListeners", "starts");
    try
    {
      removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener);
    }
    catch (Exception localException)
    {
      logger.warning("remove listener from MBeanServer delegate", localException);
    }
    Set localSet = queryNames(null, broadcasterQuery);
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      ObjectName localObjectName = (ObjectName)localIterator.next();
      if (logger.debugOn()) {
        logger.debug("destroyListeners", "remove listener from " + localObjectName);
      }
      removeBufferListener(localObjectName);
    }
    logger.debug("destroyListeners", "ends");
  }
  
  private void checkNoLocks()
  {
    if ((Thread.holdsLock(this)) || (Thread.holdsLock(globalLock))) {
      logger.warning("checkNoLocks", "lock protocol violation");
    }
  }
  
  private static Exception extractException(Exception paramException)
  {
    while ((paramException instanceof PrivilegedActionException)) {
      paramException = ((PrivilegedActionException)paramException).getException();
    }
    return paramException;
  }
  
  static
  {
    NotificationFilterSupport localNotificationFilterSupport = new NotificationFilterSupport();
    localNotificationFilterSupport.enableType("JMX.mbean.registered");
    creationFilter = localNotificationFilterSupport;
  }
  
  private static class BroadcasterQuery
    extends QueryEval
    implements QueryExp
  {
    private static final long serialVersionUID = 7378487660587592048L;
    
    private BroadcasterQuery() {}
    
    public boolean apply(ObjectName paramObjectName)
    {
      MBeanServer localMBeanServer = QueryEval.getMBeanServer();
      return ArrayNotificationBuffer.isInstanceOf(localMBeanServer, paramObjectName, ArrayNotificationBuffer.broadcasterClass);
    }
  }
  
  private class BufferListener
    implements NotificationListener
  {
    private BufferListener() {}
    
    public void handleNotification(Notification paramNotification, Object paramObject)
    {
      if (ArrayNotificationBuffer.logger.debugOn()) {
        ArrayNotificationBuffer.logger.debug("BufferListener.handleNotification", "notif=" + paramNotification + "; handback=" + paramObject);
      }
      ObjectName localObjectName = (ObjectName)paramObject;
      ArrayNotificationBuffer.this.addNotification(new ArrayNotificationBuffer.NamedNotification(localObjectName, paramNotification));
    }
  }
  
  private static class NamedNotification
  {
    private final ObjectName sender;
    private final Notification notification;
    
    NamedNotification(ObjectName paramObjectName, Notification paramNotification)
    {
      this.sender = paramObjectName;
      this.notification = paramNotification;
    }
    
    ObjectName getObjectName()
    {
      return this.sender;
    }
    
    Notification getNotification()
    {
      return this.notification;
    }
    
    public String toString()
    {
      return "NamedNotification(" + this.sender + ", " + this.notification + ")";
    }
  }
  
  private class ShareBuffer
    implements NotificationBuffer
  {
    private final int size;
    
    ShareBuffer(int paramInt)
    {
      this.size = paramInt;
      ArrayNotificationBuffer.this.addSharer(this);
    }
    
    public NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt)
      throws InterruptedException
    {
      ArrayNotificationBuffer localArrayNotificationBuffer = ArrayNotificationBuffer.this;
      return localArrayNotificationBuffer.fetchNotifications(paramNotificationBufferFilter, paramLong1, paramLong2, paramInt);
    }
    
    public void dispose()
    {
      ArrayNotificationBuffer.this.removeSharer(this);
    }
    
    int getSize()
    {
      return this.size;
    }
  }
}
