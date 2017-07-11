package sun.nio.ch;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class WindowsSelectorImpl
  extends SelectorImpl
{
  private final int INIT_CAP = 8;
  private static final int MAX_SELECTABLE_FDS = 1024;
  private SelectionKeyImpl[] channelArray = new SelectionKeyImpl[8];
  private PollArrayWrapper pollWrapper = new PollArrayWrapper(8);
  private int totalChannels = 1;
  private int threadsCount = 0;
  private final List<SelectThread> threads = new ArrayList();
  private final Pipe wakeupPipe = Pipe.open();
  private final int wakeupSourceFd = ((SelChImpl)this.wakeupPipe.source()).getFDVal();
  private final int wakeupSinkFd;
  private Object closeLock = new Object();
  private final FdMap fdMap = new FdMap(null);
  private final SubSelector subSelector = new SubSelector(null);
  private long timeout;
  private final Object interruptLock = new Object();
  private volatile boolean interruptTriggered = false;
  private final StartLock startLock = new StartLock(null);
  private final FinishLock finishLock = new FinishLock(null);
  private long updateCount = 0L;
  
  WindowsSelectorImpl(SelectorProvider paramSelectorProvider)
    throws IOException
  {
    super(paramSelectorProvider);
    SinkChannelImpl localSinkChannelImpl = (SinkChannelImpl)this.wakeupPipe.sink();
    localSinkChannelImpl.sc.socket().setTcpNoDelay(true);
    this.wakeupSinkFd = localSinkChannelImpl.getFDVal();
    this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, 0);
  }
  
  protected int doSelect(long paramLong)
    throws IOException
  {
    if (this.channelArray == null) {
      throw new ClosedSelectorException();
    }
    this.timeout = paramLong;
    processDeregisterQueue();
    if (this.interruptTriggered)
    {
      resetWakeupSocket();
      return 0;
    }
    adjustThreadsCount();
    this.finishLock.reset();
    this.startLock.startThreads();
    try
    {
      begin();
      try
      {
        this.subSelector.poll();
      }
      catch (IOException localIOException)
      {
        this.finishLock.setException(localIOException);
      }
      if (this.threads.size() > 0) {
        this.finishLock.waitForHelperThreads();
      }
    }
    finally
    {
      end();
    }
    this.finishLock.checkForException();
    processDeregisterQueue();
    int i = updateSelectedKeys();
    resetWakeupSocket();
    return i;
  }
  
  private void adjustThreadsCount()
  {
    int i;
    if (this.threadsCount > this.threads.size()) {
      for (i = this.threads.size(); i < this.threadsCount; i++)
      {
        SelectThread localSelectThread = new SelectThread(i, null);
        this.threads.add(localSelectThread);
        localSelectThread.setDaemon(true);
        localSelectThread.start();
      }
    } else if (this.threadsCount < this.threads.size()) {
      for (i = this.threads.size() - 1; i >= this.threadsCount; i--) {
        ((SelectThread)this.threads.remove(i)).makeZombie();
      }
    }
  }
  
  private void setWakeupSocket()
  {
    setWakeupSocket0(this.wakeupSinkFd);
  }
  
  private native void setWakeupSocket0(int paramInt);
  
  private void resetWakeupSocket()
  {
    synchronized (this.interruptLock)
    {
      if (!this.interruptTriggered) {
        return;
      }
      resetWakeupSocket0(this.wakeupSourceFd);
      this.interruptTriggered = false;
    }
  }
  
  private native void resetWakeupSocket0(int paramInt);
  
  private native boolean discardUrgentData(int paramInt);
  
  private int updateSelectedKeys()
  {
    this.updateCount += 1L;
    int i = 0;
    i += this.subSelector.processSelectedKeys(this.updateCount);
    Iterator localIterator = this.threads.iterator();
    while (localIterator.hasNext())
    {
      SelectThread localSelectThread = (SelectThread)localIterator.next();
      i += localSelectThread.subSelector.processSelectedKeys(this.updateCount);
    }
    return i;
  }
  
  protected void implClose()
    throws IOException
  {
    synchronized (this.closeLock)
    {
      if ((this.channelArray != null) && (this.pollWrapper != null))
      {
        synchronized (this.interruptLock)
        {
          this.interruptTriggered = true;
        }
        this.wakeupPipe.sink().close();
        this.wakeupPipe.source().close();
        Object localObject2;
        for (int i = 1; i < this.totalChannels; i++) {
          if (i % 1024 != 0)
          {
            deregister(this.channelArray[i]);
            localObject2 = this.channelArray[i].channel();
            if ((!((SelectableChannel)localObject2).isOpen()) && (!((SelectableChannel)localObject2).isRegistered())) {
              ((SelChImpl)localObject2).kill();
            }
          }
        }
        this.pollWrapper.free();
        this.pollWrapper = null;
        this.selectedKeys = null;
        this.channelArray = null;
        Iterator localIterator = this.threads.iterator();
        while (localIterator.hasNext())
        {
          localObject2 = (SelectThread)localIterator.next();
          ((SelectThread)localObject2).makeZombie();
        }
        this.startLock.startThreads();
      }
    }
  }
  
  protected void implRegister(SelectionKeyImpl paramSelectionKeyImpl)
  {
    synchronized (this.closeLock)
    {
      if (this.pollWrapper == null) {
        throw new ClosedSelectorException();
      }
      growIfNeeded();
      this.channelArray[this.totalChannels] = paramSelectionKeyImpl;
      paramSelectionKeyImpl.setIndex(this.totalChannels);
      this.fdMap.put(paramSelectionKeyImpl);
      this.keys.add(paramSelectionKeyImpl);
      this.pollWrapper.addEntry(this.totalChannels, paramSelectionKeyImpl);
      this.totalChannels += 1;
    }
  }
  
  private void growIfNeeded()
  {
    if (this.channelArray.length == this.totalChannels)
    {
      int i = this.totalChannels * 2;
      SelectionKeyImpl[] arrayOfSelectionKeyImpl = new SelectionKeyImpl[i];
      System.arraycopy(this.channelArray, 1, arrayOfSelectionKeyImpl, 1, this.totalChannels - 1);
      this.channelArray = arrayOfSelectionKeyImpl;
      this.pollWrapper.grow(i);
    }
    if (this.totalChannels % 1024 == 0)
    {
      this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, this.totalChannels);
      this.totalChannels += 1;
      this.threadsCount += 1;
    }
  }
  
  protected void implDereg(SelectionKeyImpl paramSelectionKeyImpl)
    throws IOException
  {
    int i = paramSelectionKeyImpl.getIndex();
    assert (i >= 0);
    synchronized (this.closeLock)
    {
      if (i != this.totalChannels - 1)
      {
        SelectionKeyImpl localSelectionKeyImpl = this.channelArray[(this.totalChannels - 1)];
        this.channelArray[i] = localSelectionKeyImpl;
        localSelectionKeyImpl.setIndex(i);
        this.pollWrapper.replaceEntry(this.pollWrapper, this.totalChannels - 1, this.pollWrapper, i);
      }
      paramSelectionKeyImpl.setIndex(-1);
    }
    this.channelArray[(this.totalChannels - 1)] = null;
    this.totalChannels -= 1;
    if ((this.totalChannels != 1) && (this.totalChannels % 1024 == 1))
    {
      this.totalChannels -= 1;
      this.threadsCount -= 1;
    }
    this.fdMap.remove(paramSelectionKeyImpl);
    this.keys.remove(paramSelectionKeyImpl);
    this.selectedKeys.remove(paramSelectionKeyImpl);
    deregister(paramSelectionKeyImpl);
    ??? = paramSelectionKeyImpl.channel();
    if ((!((SelectableChannel)???).isOpen()) && (!((SelectableChannel)???).isRegistered())) {
      ((SelChImpl)???).kill();
    }
  }
  
  public void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt)
  {
    synchronized (this.closeLock)
    {
      if (this.pollWrapper == null) {
        throw new ClosedSelectorException();
      }
      int i = paramSelectionKeyImpl.getIndex();
      if (i == -1) {
        throw new CancelledKeyException();
      }
      this.pollWrapper.putEventOps(i, paramInt);
    }
  }
  
  public Selector wakeup()
  {
    synchronized (this.interruptLock)
    {
      if (!this.interruptTriggered)
      {
        setWakeupSocket();
        this.interruptTriggered = true;
      }
    }
    return this;
  }
  
  static
  {
    IOUtil.load();
  }
  
  private static final class FdMap
    extends HashMap<Integer, WindowsSelectorImpl.MapEntry>
  {
    static final long serialVersionUID = 0L;
    
    private FdMap() {}
    
    private WindowsSelectorImpl.MapEntry get(int paramInt)
    {
      return (WindowsSelectorImpl.MapEntry)get(new Integer(paramInt));
    }
    
    private WindowsSelectorImpl.MapEntry put(SelectionKeyImpl paramSelectionKeyImpl)
    {
      return (WindowsSelectorImpl.MapEntry)put(new Integer(paramSelectionKeyImpl.channel.getFDVal()), new WindowsSelectorImpl.MapEntry(paramSelectionKeyImpl));
    }
    
    private WindowsSelectorImpl.MapEntry remove(SelectionKeyImpl paramSelectionKeyImpl)
    {
      Integer localInteger = new Integer(paramSelectionKeyImpl.channel.getFDVal());
      WindowsSelectorImpl.MapEntry localMapEntry = (WindowsSelectorImpl.MapEntry)get(localInteger);
      if ((localMapEntry != null) && (localMapEntry.ski.channel == paramSelectionKeyImpl.channel)) {
        return (WindowsSelectorImpl.MapEntry)remove(localInteger);
      }
      return null;
    }
  }
  
  private final class FinishLock
  {
    private int threadsToFinish;
    IOException exception = null;
    
    private FinishLock() {}
    
    private void reset()
    {
      this.threadsToFinish = WindowsSelectorImpl.this.threads.size();
    }
    
    private synchronized void threadFinished()
    {
      if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size()) {
        WindowsSelectorImpl.this.wakeup();
      }
      this.threadsToFinish -= 1;
      if (this.threadsToFinish == 0) {
        notify();
      }
    }
    
    private synchronized void waitForHelperThreads()
    {
      if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size()) {
        WindowsSelectorImpl.this.wakeup();
      }
      while (this.threadsToFinish != 0) {
        try
        {
          WindowsSelectorImpl.this.finishLock.wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
        }
      }
    }
    
    private synchronized void setException(IOException paramIOException)
    {
      this.exception = paramIOException;
    }
    
    private void checkForException()
      throws IOException
    {
      if (this.exception == null) {
        return;
      }
      StringBuffer localStringBuffer = new StringBuffer("An exception occurred during the execution of select(): \n");
      localStringBuffer.append(this.exception);
      localStringBuffer.append('\n');
      this.exception = null;
      throw new IOException(localStringBuffer.toString());
    }
  }
  
  private static final class MapEntry
  {
    SelectionKeyImpl ski;
    long updateCount = 0L;
    long clearedCount = 0L;
    
    MapEntry(SelectionKeyImpl paramSelectionKeyImpl)
    {
      this.ski = paramSelectionKeyImpl;
    }
  }
  
  private final class SelectThread
    extends Thread
  {
    private final int index;
    final WindowsSelectorImpl.SubSelector subSelector;
    private long lastRun = 0L;
    private volatile boolean zombie;
    
    private SelectThread(int paramInt)
    {
      this.index = paramInt;
      this.subSelector = new WindowsSelectorImpl.SubSelector(WindowsSelectorImpl.this, paramInt, null);
      this.lastRun = WindowsSelectorImpl.StartLock.access$2400(WindowsSelectorImpl.this.startLock);
    }
    
    void makeZombie()
    {
      this.zombie = true;
    }
    
    boolean isZombie()
    {
      return this.zombie;
    }
    
    public void run()
    {
      for (;;)
      {
        if (WindowsSelectorImpl.StartLock.access$2500(WindowsSelectorImpl.this.startLock, this)) {
          return;
        }
        try
        {
          WindowsSelectorImpl.SubSelector.access$2600(this.subSelector, this.index);
        }
        catch (IOException localIOException)
        {
          WindowsSelectorImpl.this.finishLock.setException(localIOException);
        }
        WindowsSelectorImpl.this.finishLock.threadFinished();
      }
    }
  }
  
  private final class StartLock
  {
    private long runsCounter;
    
    private StartLock() {}
    
    private synchronized void startThreads()
    {
      this.runsCounter += 1L;
      notifyAll();
    }
    
    private synchronized boolean waitForStart(WindowsSelectorImpl.SelectThread paramSelectThread)
    {
      while (this.runsCounter == paramSelectThread.lastRun) {
        try
        {
          WindowsSelectorImpl.this.startLock.wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
        }
      }
      if (paramSelectThread.isZombie()) {
        return true;
      }
      paramSelectThread.lastRun = this.runsCounter;
      return false;
    }
  }
  
  private final class SubSelector
  {
    private final int pollArrayIndex;
    private final int[] readFds = new int['Ё'];
    private final int[] writeFds = new int['Ё'];
    private final int[] exceptFds = new int['Ё'];
    
    private SubSelector()
    {
      this.pollArrayIndex = 0;
    }
    
    private SubSelector(int paramInt)
    {
      this.pollArrayIndex = ((paramInt + 1) * 1024);
    }
    
    private int poll()
      throws IOException
    {
      return poll0(WindowsSelectorImpl.this.pollWrapper.pollArrayAddress, Math.min(WindowsSelectorImpl.this.totalChannels, 1024), this.readFds, this.writeFds, this.exceptFds, WindowsSelectorImpl.this.timeout);
    }
    
    private int poll(int paramInt)
      throws IOException
    {
      return poll0(WindowsSelectorImpl.this.pollWrapper.pollArrayAddress + this.pollArrayIndex * PollArrayWrapper.SIZE_POLLFD, Math.min(1024, WindowsSelectorImpl.this.totalChannels - (paramInt + 1) * 1024), this.readFds, this.writeFds, this.exceptFds, WindowsSelectorImpl.this.timeout);
    }
    
    private native int poll0(long paramLong1, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, long paramLong2);
    
    private int processSelectedKeys(long paramLong)
    {
      int i = 0;
      i += processFDSet(paramLong, this.readFds, Net.POLLIN, false);
      i += processFDSet(paramLong, this.writeFds, Net.POLLCONN | Net.POLLOUT, false);
      i += processFDSet(paramLong, this.exceptFds, Net.POLLIN | Net.POLLCONN | Net.POLLOUT, true);
      return i;
    }
    
    private int processFDSet(long paramLong, int[] paramArrayOfInt, int paramInt, boolean paramBoolean)
    {
      int i = 0;
      for (int j = 1; j <= paramArrayOfInt[0]; j++)
      {
        int k = paramArrayOfInt[j];
        if (k == WindowsSelectorImpl.this.wakeupSourceFd)
        {
          synchronized (WindowsSelectorImpl.this.interruptLock)
          {
            WindowsSelectorImpl.this.interruptTriggered = true;
          }
        }
        else
        {
          ??? = WindowsSelectorImpl.this.fdMap.get(k);
          if (??? != null)
          {
            SelectionKeyImpl localSelectionKeyImpl = ((WindowsSelectorImpl.MapEntry)???).ski;
            if ((!paramBoolean) || (!(localSelectionKeyImpl.channel() instanceof SocketChannelImpl)) || (!WindowsSelectorImpl.this.discardUrgentData(k))) {
              if (WindowsSelectorImpl.this.selectedKeys.contains(localSelectionKeyImpl))
              {
                if (((WindowsSelectorImpl.MapEntry)???).clearedCount != paramLong)
                {
                  if ((localSelectionKeyImpl.channel.translateAndSetReadyOps(paramInt, localSelectionKeyImpl)) && (((WindowsSelectorImpl.MapEntry)???).updateCount != paramLong))
                  {
                    ((WindowsSelectorImpl.MapEntry)???).updateCount = paramLong;
                    i++;
                  }
                }
                else if ((localSelectionKeyImpl.channel.translateAndUpdateReadyOps(paramInt, localSelectionKeyImpl)) && (((WindowsSelectorImpl.MapEntry)???).updateCount != paramLong))
                {
                  ((WindowsSelectorImpl.MapEntry)???).updateCount = paramLong;
                  i++;
                }
                ((WindowsSelectorImpl.MapEntry)???).clearedCount = paramLong;
              }
              else
              {
                if (((WindowsSelectorImpl.MapEntry)???).clearedCount != paramLong)
                {
                  localSelectionKeyImpl.channel.translateAndSetReadyOps(paramInt, localSelectionKeyImpl);
                  if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0)
                  {
                    WindowsSelectorImpl.this.selectedKeys.add(localSelectionKeyImpl);
                    ((WindowsSelectorImpl.MapEntry)???).updateCount = paramLong;
                    i++;
                  }
                }
                else
                {
                  localSelectionKeyImpl.channel.translateAndUpdateReadyOps(paramInt, localSelectionKeyImpl);
                  if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0)
                  {
                    WindowsSelectorImpl.this.selectedKeys.add(localSelectionKeyImpl);
                    ((WindowsSelectorImpl.MapEntry)???).updateCount = paramLong;
                    i++;
                  }
                }
                ((WindowsSelectorImpl.MapEntry)???).clearedCount = paramLong;
              }
            }
          }
        }
      }
      return i;
    }
  }
}
