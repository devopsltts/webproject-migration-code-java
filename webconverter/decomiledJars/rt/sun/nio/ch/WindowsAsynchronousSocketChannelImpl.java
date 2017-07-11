package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

class WindowsAsynchronousSocketChannelImpl
  extends AsynchronousSocketChannelImpl
  implements Iocp.OverlappedChannel
{
  private static final Unsafe unsafe = ;
  private static int addressSize = unsafe.addressSize();
  private static final int SIZEOF_WSABUF = dependsArch(8, 16);
  private static final int OFFSETOF_LEN = 0;
  private static final int OFFSETOF_BUF = dependsArch(4, 8);
  private static final int MAX_WSABUF = 16;
  private static final int SIZEOF_WSABUFARRAY = 16 * SIZEOF_WSABUF;
  final long handle;
  private final Iocp iocp;
  private final int completionKey;
  private final PendingIoCache ioCache;
  private final long readBufferArray;
  private final long writeBufferArray;
  
  private static int dependsArch(int paramInt1, int paramInt2)
  {
    return addressSize == 4 ? paramInt1 : paramInt2;
  }
  
  WindowsAsynchronousSocketChannelImpl(Iocp paramIocp, boolean paramBoolean)
    throws IOException
  {
    super(paramIocp);
    long l = IOUtil.fdVal(this.fd);
    int i = 0;
    try
    {
      i = paramIocp.associate(this, l);
    }
    catch (ShutdownChannelGroupException localShutdownChannelGroupException)
    {
      if (paramBoolean)
      {
        closesocket0(l);
        throw localShutdownChannelGroupException;
      }
    }
    catch (IOException localIOException)
    {
      closesocket0(l);
      throw localIOException;
    }
    this.handle = l;
    this.iocp = paramIocp;
    this.completionKey = i;
    this.ioCache = new PendingIoCache();
    this.readBufferArray = unsafe.allocateMemory(SIZEOF_WSABUFARRAY);
    this.writeBufferArray = unsafe.allocateMemory(SIZEOF_WSABUFARRAY);
  }
  
  WindowsAsynchronousSocketChannelImpl(Iocp paramIocp)
    throws IOException
  {
    this(paramIocp, true);
  }
  
  public AsynchronousChannelGroupImpl group()
  {
    return this.iocp;
  }
  
  public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong)
  {
    return this.ioCache.remove(paramLong);
  }
  
  long handle()
  {
    return this.handle;
  }
  
  void setConnected(InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2)
  {
    synchronized (this.stateLock)
    {
      this.state = 2;
      this.localAddress = paramInetSocketAddress1;
      this.remoteAddress = paramInetSocketAddress2;
    }
  }
  
  void implClose()
    throws IOException
  {
    closesocket0(this.handle);
    this.ioCache.close();
    unsafe.freeMemory(this.readBufferArray);
    unsafe.freeMemory(this.writeBufferArray);
    if (this.completionKey != 0) {
      this.iocp.disassociate(this.completionKey);
    }
  }
  
  public void onCancel(PendingFuture<?, ?> paramPendingFuture)
  {
    if ((paramPendingFuture.getContext() instanceof ConnectTask)) {
      killConnect();
    }
    if ((paramPendingFuture.getContext() instanceof ReadTask)) {
      killReading();
    }
    if ((paramPendingFuture.getContext() instanceof WriteTask)) {
      killWriting();
    }
  }
  
  private void doPrivilegedBind(final SocketAddress paramSocketAddress)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws IOException
        {
          WindowsAsynchronousSocketChannelImpl.this.bind(paramSocketAddress);
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler)
  {
    if (!isOpen())
    {
      localObject1 = new ClosedChannelException();
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure((Throwable)localObject1);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)localObject1);
      return null;
    }
    Object localObject1 = Net.checkAddress(paramSocketAddress);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkConnect(((InetSocketAddress)localObject1).getAddress().getHostAddress(), ((InetSocketAddress)localObject1).getPort());
    }
    Object localObject2 = null;
    synchronized (this.stateLock)
    {
      if (this.state == 2) {
        throw new AlreadyConnectedException();
      }
      if (this.state == 1) {
        throw new ConnectionPendingException();
      }
      if (this.localAddress == null) {
        try
        {
          InetSocketAddress localInetSocketAddress = new InetSocketAddress(0);
          if (localSecurityManager == null) {
            bind(localInetSocketAddress);
          } else {
            doPrivilegedBind(localInetSocketAddress);
          }
        }
        catch (IOException localIOException2)
        {
          localObject2 = localIOException2;
        }
      }
      if (localObject2 == null) {
        this.state = 1;
      }
    }
    if (localObject2 != null)
    {
      try
      {
        close();
      }
      catch (IOException localIOException1) {}
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure(localObject2);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, localObject2);
      return null;
    }
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ConnectTask localConnectTask = new ConnectTask((InetSocketAddress)localObject1, localPendingFuture);
    localPendingFuture.setContext(localConnectTask);
    if (Iocp.supportsThreadAgnosticIo()) {
      localConnectTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localConnectTask);
    }
    return localPendingFuture;
  }
  
  <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ByteBuffer[] arrayOfByteBuffer;
    if (paramBoolean)
    {
      arrayOfByteBuffer = paramArrayOfByteBuffer;
    }
    else
    {
      arrayOfByteBuffer = new ByteBuffer[1];
      arrayOfByteBuffer[0] = paramByteBuffer;
    }
    final ReadTask localReadTask = new ReadTask(arrayOfByteBuffer, paramBoolean, localPendingFuture);
    localPendingFuture.setContext(localReadTask);
    if (paramLong > 0L)
    {
      Future localFuture = this.iocp.schedule(new Runnable()
      {
        public void run()
        {
          localReadTask.timeout();
        }
      }, paramLong, paramTimeUnit);
      localPendingFuture.setTimeoutTask(localFuture);
    }
    if (Iocp.supportsThreadAgnosticIo()) {
      localReadTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localReadTask);
    }
    return localPendingFuture;
  }
  
  <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ByteBuffer[] arrayOfByteBuffer;
    if (paramBoolean)
    {
      arrayOfByteBuffer = paramArrayOfByteBuffer;
    }
    else
    {
      arrayOfByteBuffer = new ByteBuffer[1];
      arrayOfByteBuffer[0] = paramByteBuffer;
    }
    final WriteTask localWriteTask = new WriteTask(arrayOfByteBuffer, paramBoolean, localPendingFuture);
    localPendingFuture.setContext(localWriteTask);
    if (paramLong > 0L)
    {
      Future localFuture = this.iocp.schedule(new Runnable()
      {
        public void run()
        {
          localWriteTask.timeout();
        }
      }, paramLong, paramTimeUnit);
      localPendingFuture.setTimeoutTask(localFuture);
    }
    if (Iocp.supportsThreadAgnosticIo()) {
      localWriteTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localWriteTask);
    }
    return localPendingFuture;
  }
  
  private static native void initIDs();
  
  private static native int connect0(long paramLong1, boolean paramBoolean, InetAddress paramInetAddress, int paramInt, long paramLong2)
    throws IOException;
  
  private static native void updateConnectContext(long paramLong)
    throws IOException;
  
  private static native int read0(long paramLong1, int paramInt, long paramLong2, long paramLong3)
    throws IOException;
  
  private static native int write0(long paramLong1, int paramInt, long paramLong2, long paramLong3)
    throws IOException;
  
  private static native void shutdown0(long paramLong, int paramInt)
    throws IOException;
  
  private static native void closesocket0(long paramLong)
    throws IOException;
  
  static
  {
    IOUtil.load();
    initIDs();
  }
  
  private class ConnectTask<A>
    implements Runnable, Iocp.ResultHandler
  {
    private final InetSocketAddress remote;
    private final PendingFuture<Void, A> result;
    
    ConnectTask(PendingFuture<Void, A> paramPendingFuture)
    {
      this.remote = paramPendingFuture;
      Object localObject;
      this.result = localObject;
    }
    
    private void closeChannel()
    {
      try
      {
        WindowsAsynchronousSocketChannelImpl.this.close();
      }
      catch (IOException localIOException) {}
    }
    
    private IOException toIOException(Throwable paramThrowable)
    {
      if ((paramThrowable instanceof IOException))
      {
        if ((paramThrowable instanceof ClosedChannelException)) {
          paramThrowable = new AsynchronousCloseException();
        }
        return (IOException)paramThrowable;
      }
      return new IOException(paramThrowable);
    }
    
    private void afterConnect()
      throws IOException
    {
      WindowsAsynchronousSocketChannelImpl.updateConnectContext(WindowsAsynchronousSocketChannelImpl.this.handle);
      synchronized (WindowsAsynchronousSocketChannelImpl.this.stateLock)
      {
        WindowsAsynchronousSocketChannelImpl.this.state = 2;
        WindowsAsynchronousSocketChannelImpl.this.remoteAddress = this.remote;
      }
    }
    
    public void run()
    {
      long l = 0L;
      Object localObject1 = null;
      try
      {
        WindowsAsynchronousSocketChannelImpl.this.begin();
        synchronized (this.result)
        {
          l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
          int i = WindowsAsynchronousSocketChannelImpl.connect0(WindowsAsynchronousSocketChannelImpl.this.handle, Net.isIPv6Available(), this.remote.getAddress(), this.remote.getPort(), l);
          if (i == -2) {
            return;
          }
          afterConnect();
          this.result.setResult(null);
        }
      }
      catch (Throwable localThrowable)
      {
        if (l != 0L) {
          WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l);
        }
        localObject1 = localThrowable;
      }
      finally
      {
        WindowsAsynchronousSocketChannelImpl.this.end();
      }
      if (localObject1 != null)
      {
        closeChannel();
        this.result.setFailure(toIOException(localObject1));
      }
      Invoker.invoke(this.result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      Object localObject1 = null;
      try
      {
        WindowsAsynchronousSocketChannelImpl.this.begin();
        afterConnect();
        this.result.setResult(null);
      }
      catch (Throwable localThrowable)
      {
        localObject1 = localThrowable;
      }
      finally
      {
        WindowsAsynchronousSocketChannelImpl.this.end();
      }
      if (localObject1 != null)
      {
        closeChannel();
        this.result.setFailure(toIOException(localObject1));
      }
      if (paramBoolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      if (WindowsAsynchronousSocketChannelImpl.this.isOpen())
      {
        closeChannel();
        this.result.setFailure(paramIOException);
      }
      else
      {
        this.result.setFailure(new AsynchronousCloseException());
      }
      Invoker.invoke(this.result);
    }
  }
  
  private class ReadTask<V, A>
    implements Runnable, Iocp.ResultHandler
  {
    private final ByteBuffer[] bufs;
    private final int numBufs;
    private final boolean scatteringRead;
    private final PendingFuture<V, A> result;
    private ByteBuffer[] shadow;
    
    ReadTask(boolean paramBoolean, PendingFuture<V, A> paramPendingFuture)
    {
      this.bufs = paramBoolean;
      this.numBufs = (paramBoolean.length > 16 ? 16 : paramBoolean.length);
      this.scatteringRead = paramPendingFuture;
      Object localObject;
      this.result = localObject;
    }
    
    void prepareBuffers()
    {
      this.shadow = new ByteBuffer[this.numBufs];
      long l1 = WindowsAsynchronousSocketChannelImpl.this.readBufferArray;
      for (int i = 0; i < this.numBufs; i++)
      {
        ByteBuffer localByteBuffer1 = this.bufs[i];
        int j = localByteBuffer1.position();
        int k = localByteBuffer1.limit();
        assert (j <= k);
        int m = j <= k ? k - j : 0;
        long l2;
        if (!(localByteBuffer1 instanceof DirectBuffer))
        {
          ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(m);
          this.shadow[i] = localByteBuffer2;
          l2 = ((DirectBuffer)localByteBuffer2).address();
        }
        else
        {
          this.shadow[i] = localByteBuffer1;
          l2 = ((DirectBuffer)localByteBuffer1).address() + j;
        }
        WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(l1 + WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, l2);
        WindowsAsynchronousSocketChannelImpl.unsafe.putInt(l1 + 0L, m);
        l1 += WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
      }
    }
    
    void updateBuffers(int paramInt)
    {
      for (int i = 0; i < this.numBufs; i++)
      {
        ByteBuffer localByteBuffer = this.shadow[i];
        int j = localByteBuffer.position();
        int k = localByteBuffer.remaining();
        int m;
        if (paramInt >= k)
        {
          paramInt -= k;
          m = j + k;
          try
          {
            localByteBuffer.position(m);
          }
          catch (IllegalArgumentException localIllegalArgumentException1) {}
        }
        else
        {
          if (paramInt <= 0) {
            break;
          }
          assert (j + paramInt < 2147483647L);
          m = j + paramInt;
          try
          {
            localByteBuffer.position(m);
          }
          catch (IllegalArgumentException localIllegalArgumentException2) {}
          break;
        }
      }
      for (i = 0; i < this.numBufs; i++) {
        if (!(this.bufs[i] instanceof DirectBuffer))
        {
          this.shadow[i].flip();
          try
          {
            this.bufs[i].put(this.shadow[i]);
          }
          catch (BufferOverflowException localBufferOverflowException) {}
        }
      }
    }
    
    void releaseBuffers()
    {
      for (int i = 0; i < this.numBufs; i++) {
        if (!(this.bufs[i] instanceof DirectBuffer)) {
          Util.releaseTemporaryDirectBuffer(this.shadow[i]);
        }
      }
    }
    
    public void run()
    {
      long l = 0L;
      int i = 0;
      int j = 0;
      try
      {
        WindowsAsynchronousSocketChannelImpl.this.begin();
        prepareBuffers();
        i = 1;
        l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
        int k = WindowsAsynchronousSocketChannelImpl.read0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.readBufferArray, l);
        if (k == -2)
        {
          j = 1;
          return;
        }
        if (k == -1)
        {
          WindowsAsynchronousSocketChannelImpl.this.enableReading();
          if (this.scatteringRead) {
            this.result.setResult(Long.valueOf(-1L));
          } else {
            this.result.setResult(Integer.valueOf(-1));
          }
        }
        else
        {
          throw new InternalError("Read completed immediately");
        }
      }
      catch (Throwable localThrowable)
      {
        WindowsAsynchronousSocketChannelImpl.this.enableReading();
        Object localObject1;
        if ((localThrowable instanceof ClosedChannelException)) {
          localObject1 = new AsynchronousCloseException();
        }
        if (!(localObject1 instanceof IOException)) {
          localObject1 = new IOException((Throwable)localObject1);
        }
        this.result.setFailure((Throwable)localObject1);
      }
      finally
      {
        if (j == 0)
        {
          if (l != 0L) {
            WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l);
          }
          if (i != 0) {
            releaseBuffers();
          }
        }
        WindowsAsynchronousSocketChannelImpl.this.end();
      }
      Invoker.invoke(this.result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      if (paramInt == 0) {
        paramInt = -1;
      } else {
        updateBuffers(paramInt);
      }
      releaseBuffers();
      synchronized (this.result)
      {
        if (this.result.isDone()) {
          return;
        }
        WindowsAsynchronousSocketChannelImpl.this.enableReading();
        if (this.scatteringRead) {
          this.result.setResult(Long.valueOf(paramInt));
        } else {
          this.result.setResult(Integer.valueOf(paramInt));
        }
      }
      if (paramBoolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      releaseBuffers();
      if (!WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
        paramIOException = new AsynchronousCloseException();
      }
      synchronized (this.result)
      {
        if (this.result.isDone()) {
          return;
        }
        WindowsAsynchronousSocketChannelImpl.this.enableReading();
        this.result.setFailure(paramIOException);
      }
      Invoker.invoke(this.result);
    }
    
    void timeout()
    {
      synchronized (this.result)
      {
        if (this.result.isDone()) {
          return;
        }
        WindowsAsynchronousSocketChannelImpl.this.enableReading(true);
        this.result.setFailure(new InterruptedByTimeoutException());
      }
      Invoker.invoke(this.result);
    }
  }
  
  private class WriteTask<V, A>
    implements Runnable, Iocp.ResultHandler
  {
    private final ByteBuffer[] bufs;
    private final int numBufs;
    private final boolean gatheringWrite;
    private final PendingFuture<V, A> result;
    private ByteBuffer[] shadow;
    
    WriteTask(boolean paramBoolean, PendingFuture<V, A> paramPendingFuture)
    {
      this.bufs = paramBoolean;
      this.numBufs = (paramBoolean.length > 16 ? 16 : paramBoolean.length);
      this.gatheringWrite = paramPendingFuture;
      Object localObject;
      this.result = localObject;
    }
    
    void prepareBuffers()
    {
      this.shadow = new ByteBuffer[this.numBufs];
      long l1 = WindowsAsynchronousSocketChannelImpl.this.writeBufferArray;
      for (int i = 0; i < this.numBufs; i++)
      {
        ByteBuffer localByteBuffer1 = this.bufs[i];
        int j = localByteBuffer1.position();
        int k = localByteBuffer1.limit();
        assert (j <= k);
        int m = j <= k ? k - j : 0;
        long l2;
        if (!(localByteBuffer1 instanceof DirectBuffer))
        {
          ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(m);
          localByteBuffer2.put(localByteBuffer1);
          localByteBuffer2.flip();
          localByteBuffer1.position(j);
          this.shadow[i] = localByteBuffer2;
          l2 = ((DirectBuffer)localByteBuffer2).address();
        }
        else
        {
          this.shadow[i] = localByteBuffer1;
          l2 = ((DirectBuffer)localByteBuffer1).address() + j;
        }
        WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(l1 + WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, l2);
        WindowsAsynchronousSocketChannelImpl.unsafe.putInt(l1 + 0L, m);
        l1 += WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
      }
    }
    
    void updateBuffers(int paramInt)
    {
      for (int i = 0; i < this.numBufs; i++)
      {
        ByteBuffer localByteBuffer = this.bufs[i];
        int j = localByteBuffer.position();
        int k = localByteBuffer.limit();
        int m = j <= k ? k - j : k;
        int n;
        if (paramInt >= m)
        {
          paramInt -= m;
          n = j + m;
          try
          {
            localByteBuffer.position(n);
          }
          catch (IllegalArgumentException localIllegalArgumentException1) {}
        }
        else
        {
          if (paramInt <= 0) {
            break;
          }
          assert (j + paramInt < 2147483647L);
          n = j + paramInt;
          try
          {
            localByteBuffer.position(n);
          }
          catch (IllegalArgumentException localIllegalArgumentException2) {}
          break;
        }
      }
    }
    
    void releaseBuffers()
    {
      for (int i = 0; i < this.numBufs; i++) {
        if (!(this.bufs[i] instanceof DirectBuffer)) {
          Util.releaseTemporaryDirectBuffer(this.shadow[i]);
        }
      }
    }
    
    public void run()
    {
      long l = 0L;
      int i = 0;
      int j = 0;
      int k = 0;
      try
      {
        WindowsAsynchronousSocketChannelImpl.this.begin();
        prepareBuffers();
        i = 1;
        l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
        int m = WindowsAsynchronousSocketChannelImpl.write0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.writeBufferArray, l);
        if (m == -2)
        {
          j = 1;
          return;
        }
        if (m == -1)
        {
          k = 1;
          throw new ClosedChannelException();
        }
        throw new InternalError("Write completed immediately");
      }
      catch (Throwable localThrowable)
      {
        WindowsAsynchronousSocketChannelImpl.this.enableWriting();
        Object localObject1;
        if ((k == 0) && ((localThrowable instanceof ClosedChannelException))) {
          localObject1 = new AsynchronousCloseException();
        }
        if (!(localObject1 instanceof IOException)) {
          localObject1 = new IOException((Throwable)localObject1);
        }
        this.result.setFailure((Throwable)localObject1);
      }
      finally
      {
        if (j == 0)
        {
          if (l != 0L) {
            WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l);
          }
          if (i != 0) {
            releaseBuffers();
          }
        }
        WindowsAsynchronousSocketChannelImpl.this.end();
      }
      Invoker.invoke(this.result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      updateBuffers(paramInt);
      releaseBuffers();
      synchronized (this.result)
      {
        if (this.result.isDone()) {
          return;
        }
        WindowsAsynchronousSocketChannelImpl.this.enableWriting();
        if (this.gatheringWrite) {
          this.result.setResult(Long.valueOf(paramInt));
        } else {
          this.result.setResult(Integer.valueOf(paramInt));
        }
      }
      if (paramBoolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      releaseBuffers();
      if (!WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
        paramIOException = new AsynchronousCloseException();
      }
      synchronized (this.result)
      {
        if (this.result.isDone()) {
          return;
        }
        WindowsAsynchronousSocketChannelImpl.this.enableWriting();
        this.result.setFailure(paramIOException);
      }
      Invoker.invoke(this.result);
    }
    
    void timeout()
    {
      synchronized (this.result)
      {
        if (this.result.isDone()) {
          return;
        }
        WindowsAsynchronousSocketChannelImpl.this.enableWriting(true);
        this.result.setFailure(new InterruptedByTimeoutException());
      }
      Invoker.invoke(this.result);
    }
  }
}
