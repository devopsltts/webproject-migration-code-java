package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.NetHooks;

class SocketChannelImpl
  extends SocketChannel
  implements SelChImpl
{
  private static NativeDispatcher nd = new SocketDispatcher();
  private final FileDescriptor fd;
  private final int fdVal;
  private volatile long readerThread = 0L;
  private volatile long writerThread = 0L;
  private final Object readLock = new Object();
  private final Object writeLock = new Object();
  private final Object stateLock = new Object();
  private boolean isReuseAddress;
  private static final int ST_UNINITIALIZED = -1;
  private static final int ST_UNCONNECTED = 0;
  private static final int ST_PENDING = 1;
  private static final int ST_CONNECTED = 2;
  private static final int ST_KILLPENDING = 3;
  private static final int ST_KILLED = 4;
  private int state = -1;
  private InetSocketAddress localAddress;
  private InetSocketAddress remoteAddress;
  private boolean isInputOpen = true;
  private boolean isOutputOpen = true;
  private boolean readyToConnect = false;
  private Socket socket;
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider)
    throws IOException
  {
    super(paramSelectorProvider);
    this.fd = Net.socket(true);
    this.fdVal = IOUtil.fdVal(this.fd);
    this.state = 0;
  }
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean)
    throws IOException
  {
    super(paramSelectorProvider);
    this.fd = paramFileDescriptor;
    this.fdVal = IOUtil.fdVal(paramFileDescriptor);
    this.state = 0;
    if (paramBoolean) {
      this.localAddress = Net.localAddress(paramFileDescriptor);
    }
  }
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    super(paramSelectorProvider);
    this.fd = paramFileDescriptor;
    this.fdVal = IOUtil.fdVal(paramFileDescriptor);
    this.state = 2;
    this.localAddress = Net.localAddress(paramFileDescriptor);
    this.remoteAddress = paramInetSocketAddress;
  }
  
  public Socket socket()
  {
    synchronized (this.stateLock)
    {
      if (this.socket == null) {
        this.socket = SocketAdaptor.create(this);
      }
      return this.socket;
    }
  }
  
  public SocketAddress getLocalAddress()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      return Net.getRevealedLocalAddress(this.localAddress);
    }
  }
  
  public SocketAddress getRemoteAddress()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      return this.remoteAddress;
    }
  }
  
  public <T> SocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    if (paramSocketOption == null) {
      throw new NullPointerException();
    }
    if (!supportedOptions().contains(paramSocketOption)) {
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
    }
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (paramSocketOption == StandardSocketOptions.IP_TOS)
      {
        StandardProtocolFamily localStandardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
        Net.setSocketOption(this.fd, localStandardProtocolFamily, paramSocketOption, paramT);
        return this;
      }
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind()))
      {
        this.isReuseAddress = ((Boolean)paramT).booleanValue();
        return this;
      }
      Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
      return this;
    }
  }
  
  public <T> T getOption(SocketOption<T> paramSocketOption)
    throws IOException
  {
    if (paramSocketOption == null) {
      throw new NullPointerException();
    }
    if (!supportedOptions().contains(paramSocketOption)) {
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
    }
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind())) {
        return Boolean.valueOf(this.isReuseAddress);
      }
      if (paramSocketOption == StandardSocketOptions.IP_TOS)
      {
        StandardProtocolFamily localStandardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
        return Net.getSocketOption(this.fd, localStandardProtocolFamily, paramSocketOption);
      }
      return Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
    }
  }
  
  public final Set<SocketOption<?>> supportedOptions()
  {
    return DefaultOptionsHolder.defaultOptions;
  }
  
  private boolean ensureReadOpen()
    throws ClosedChannelException
  {
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!isConnected()) {
        throw new NotYetConnectedException();
      }
      return this.isInputOpen;
    }
  }
  
  private void ensureWriteOpen()
    throws ClosedChannelException
  {
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!this.isOutputOpen) {
        throw new ClosedChannelException();
      }
      if (!isConnected()) {
        throw new NotYetConnectedException();
      }
    }
  }
  
  private void readerCleanup()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      this.readerThread = 0L;
      if (this.state == 3) {
        kill();
      }
    }
  }
  
  private void writerCleanup()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      this.writerThread = 0L;
      if (this.state == 3) {
        kill();
      }
    }
  }
  
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    synchronized (this.readLock)
    {
      if (!ensureReadOpen()) {
        return -1;
      }
      int i = 0;
      try
      {
        begin();
        synchronized (this.stateLock)
        {
          if (!isOpen())
          {
            int k = 0;
            readerCleanup();
            end((i > 0) || (i == -2));
            synchronized (this.stateLock)
            {
              if ((i <= 0) && (!this.isInputOpen)) {
                return -1;
              }
            }
            assert (IOStatus.check(i));
            return k;
          }
          this.readerThread = NativeThread.current();
        }
        do
        {
          i = IOUtil.read(this.fd, paramByteBuffer, -1L, nd);
        } while ((i == -3) && (isOpen()));
        int j = IOStatus.normalize(i);
        readerCleanup();
        end((i > 0) || (i == -2));
        synchronized (this.stateLock)
        {
          if ((i <= 0) && (!this.isInputOpen)) {
            return -1;
          }
        }
        assert (IOStatus.check(i));
        return j;
      }
      finally
      {
        readerCleanup();
        end((i > 0) || (i == -2));
        synchronized (this.stateLock)
        {
          if ((i <= 0) && (!this.isInputOpen)) {
            return -1;
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    synchronized (this.readLock)
    {
      if (!ensureReadOpen()) {
        return -1L;
      }
      long l1 = 0L;
      try
      {
        begin();
        synchronized (this.stateLock)
        {
          if (!isOpen())
          {
            long l3 = 0L;
            readerCleanup();
            end((l1 > 0L) || (l1 == -2L));
            synchronized (this.stateLock)
            {
              if ((l1 <= 0L) && (!this.isInputOpen)) {
                return -1L;
              }
            }
            assert (IOStatus.check(l1));
            return l3;
          }
          this.readerThread = NativeThread.current();
        }
        do
        {
          l1 = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        readerCleanup();
        end((l1 > 0L) || (l1 == -2L));
        synchronized (this.stateLock)
        {
          if ((l1 <= 0L) && (!this.isInputOpen)) {
            return -1L;
          }
        }
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        readerCleanup();
        end((l1 > 0L) || (l1 == -2L));
        synchronized (this.stateLock)
        {
          if ((l1 <= 0L) && (!this.isInputOpen)) {
            return -1L;
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    synchronized (this.writeLock)
    {
      ensureWriteOpen();
      int i = 0;
      try
      {
        begin();
        synchronized (this.stateLock)
        {
          if (!isOpen())
          {
            int k = 0;
            writerCleanup();
            end((i > 0) || (i == -2));
            synchronized (this.stateLock)
            {
              if ((i <= 0) && (!this.isOutputOpen)) {
                throw new AsynchronousCloseException();
              }
            }
            assert (IOStatus.check(i));
            return k;
          }
          this.writerThread = NativeThread.current();
        }
        do
        {
          i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd);
        } while ((i == -3) && (isOpen()));
        int j = IOStatus.normalize(i);
        writerCleanup();
        end((i > 0) || (i == -2));
        synchronized (this.stateLock)
        {
          if ((i <= 0) && (!this.isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        assert (IOStatus.check(i));
        return j;
      }
      finally
      {
        writerCleanup();
        end((i > 0) || (i == -2));
        synchronized (this.stateLock)
        {
          if ((i <= 0) && (!this.isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    synchronized (this.writeLock)
    {
      ensureWriteOpen();
      long l1 = 0L;
      try
      {
        begin();
        synchronized (this.stateLock)
        {
          if (!isOpen())
          {
            long l3 = 0L;
            writerCleanup();
            end((l1 > 0L) || (l1 == -2L));
            synchronized (this.stateLock)
            {
              if ((l1 <= 0L) && (!this.isOutputOpen)) {
                throw new AsynchronousCloseException();
              }
            }
            assert (IOStatus.check(l1));
            return l3;
          }
          this.writerThread = NativeThread.current();
        }
        do
        {
          l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        writerCleanup();
        end((l1 > 0L) || (l1 == -2L));
        synchronized (this.stateLock)
        {
          if ((l1 <= 0L) && (!this.isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        writerCleanup();
        end((l1 > 0L) || (l1 == -2L));
        synchronized (this.stateLock)
        {
          if ((l1 <= 0L) && (!this.isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  int sendOutOfBandData(byte paramByte)
    throws IOException
  {
    synchronized (this.writeLock)
    {
      ensureWriteOpen();
      int i = 0;
      try
      {
        begin();
        synchronized (this.stateLock)
        {
          if (!isOpen())
          {
            int k = 0;
            writerCleanup();
            end((i > 0) || (i == -2));
            synchronized (this.stateLock)
            {
              if ((i <= 0) && (!this.isOutputOpen)) {
                throw new AsynchronousCloseException();
              }
            }
            assert (IOStatus.check(i));
            return k;
          }
          this.writerThread = NativeThread.current();
        }
        do
        {
          i = sendOutOfBandData(this.fd, paramByte);
        } while ((i == -3) && (isOpen()));
        int j = IOStatus.normalize(i);
        writerCleanup();
        end((i > 0) || (i == -2));
        synchronized (this.stateLock)
        {
          if ((i <= 0) && (!this.isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        assert (IOStatus.check(i));
        return j;
      }
      finally
      {
        writerCleanup();
        end((i > 0) || (i == -2));
        synchronized (this.stateLock)
        {
          if ((i <= 0) && (!this.isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  protected void implConfigureBlocking(boolean paramBoolean)
    throws IOException
  {
    IOUtil.configureBlocking(this.fd, paramBoolean);
  }
  
  public InetSocketAddress localAddress()
  {
    synchronized (this.stateLock)
    {
      return this.localAddress;
    }
  }
  
  public SocketAddress remoteAddress()
  {
    synchronized (this.stateLock)
    {
      return this.remoteAddress;
    }
  }
  
  public SocketChannel bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    synchronized (this.readLock)
    {
      synchronized (this.writeLock)
      {
        synchronized (this.stateLock)
        {
          if (!isOpen()) {
            throw new ClosedChannelException();
          }
          if (this.state == 1) {
            throw new ConnectionPendingException();
          }
          if (this.localAddress != null) {
            throw new AlreadyBoundException();
          }
          InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null) {
            localSecurityManager.checkListen(localInetSocketAddress.getPort());
          }
          NetHooks.beforeTcpBind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
          Net.bind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
          this.localAddress = Net.localAddress(this.fd);
        }
      }
    }
    return this;
  }
  
  public boolean isConnected()
  {
    synchronized (this.stateLock)
    {
      return this.state == 2;
    }
  }
  
  public boolean isConnectionPending()
  {
    synchronized (this.stateLock)
    {
      return this.state == 1;
    }
  }
  
  void ensureOpenAndUnconnected()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (this.state == 2) {
        throw new AlreadyConnectedException();
      }
      if (this.state == 1) {
        throw new ConnectionPendingException();
      }
    }
  }
  
  public boolean connect(SocketAddress paramSocketAddress)
    throws IOException
  {
    int i = 0;
    synchronized (this.readLock)
    {
      synchronized (this.writeLock)
      {
        ensureOpenAndUnconnected();
        InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
          localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
        }
        synchronized (blockingLock())
        {
          int j = 0;
          try
          {
            try
            {
              begin();
              synchronized (this.stateLock)
              {
                if (!isOpen())
                {
                  boolean bool = false;
                  readerCleanup();
                  end((j > 0) || (j == -2));
                  assert (IOStatus.check(j));
                  return bool;
                }
                if (this.localAddress == null) {
                  NetHooks.beforeTcpConnect(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
                }
                this.readerThread = NativeThread.current();
              }
              for (;;)
              {
                ??? = localInetSocketAddress.getAddress();
                if (???.isAnyLocalAddress()) {
                  ??? = InetAddress.getLocalHost();
                }
                j = Net.connect(this.fd, ???, localInetSocketAddress.getPort());
                if ((j != -3) || (!isOpen())) {
                  break;
                }
              }
            }
            finally
            {
              readerCleanup();
              end((j > 0) || (j == -2));
              if ((!$assertionsDisabled) && (!IOStatus.check(j))) {
                throw new AssertionError();
              }
            }
          }
          catch (IOException ???)
          {
            close();
            throw ???;
          }
          synchronized (this.stateLock)
          {
            this.remoteAddress = localInetSocketAddress;
            if (j > 0)
            {
              this.state = 2;
              if (isOpen()) {
                this.localAddress = Net.localAddress(this.fd);
              }
              return true;
            }
            if (!isBlocking()) {
              this.state = 1;
            } else if (!$assertionsDisabled) {
              throw new AssertionError();
            }
          }
        }
        return false;
      }
    }
  }
  
  public boolean finishConnect()
    throws IOException
  {
    synchronized (this.readLock)
    {
      synchronized (this.writeLock)
      {
        synchronized (this.stateLock)
        {
          if (!isOpen()) {
            throw new ClosedChannelException();
          }
          if (this.state == 2) {
            return true;
          }
          if (this.state != 1) {
            throw new NoConnectionPendingException();
          }
        }
        int i = 0;
        try
        {
          try
          {
            begin();
            synchronized (blockingLock())
            {
              synchronized (this.stateLock)
              {
                if (!isOpen())
                {
                  boolean bool = false;
                  synchronized (this.stateLock)
                  {
                    this.readerThread = 0L;
                    if (this.state == 3)
                    {
                      kill();
                      i = 0;
                    }
                  }
                  end((i > 0) || (i == -2));
                  assert (IOStatus.check(i));
                  return bool;
                }
                this.readerThread = NativeThread.current();
              }
              if (!isBlocking()) {
                for (;;)
                {
                  i = checkConnect(this.fd, false, this.readyToConnect);
                  if ((i != -3) || (!isOpen())) {
                    break;
                  }
                }
              }
              for (;;)
              {
                i = checkConnect(this.fd, true, this.readyToConnect);
                if (i != 0) {
                  if ((i != -3) || (!isOpen())) {
                    break;
                  }
                }
              }
            }
          }
          finally
          {
            synchronized (this.stateLock)
            {
              this.readerThread = 0L;
              if (this.state == 3)
              {
                kill();
                i = 0;
              }
            }
            end((i > 0) || (i == -2));
            if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
              throw new AssertionError();
            }
          }
        }
        catch (IOException localObject1)
        {
          close();
          throw ???;
        }
        if (i > 0)
        {
          synchronized (this.stateLock)
          {
            this.state = 2;
            if (isOpen()) {
              this.localAddress = Net.localAddress(this.fd);
            }
          }
          return true;
        }
        return false;
      }
    }
  }
  
  public SocketChannel shutdownInput()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!isConnected()) {
        throw new NotYetConnectedException();
      }
      if (this.isInputOpen)
      {
        Net.shutdown(this.fd, 0);
        if (this.readerThread != 0L) {
          NativeThread.signal(this.readerThread);
        }
        this.isInputOpen = false;
      }
      return this;
    }
  }
  
  public SocketChannel shutdownOutput()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!isConnected()) {
        throw new NotYetConnectedException();
      }
      if (this.isOutputOpen)
      {
        Net.shutdown(this.fd, 1);
        if (this.writerThread != 0L) {
          NativeThread.signal(this.writerThread);
        }
        this.isOutputOpen = false;
      }
      return this;
    }
  }
  
  public boolean isInputOpen()
  {
    synchronized (this.stateLock)
    {
      return this.isInputOpen;
    }
  }
  
  public boolean isOutputOpen()
  {
    synchronized (this.stateLock)
    {
      return this.isOutputOpen;
    }
  }
  
  protected void implCloseSelectableChannel()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      this.isInputOpen = false;
      this.isOutputOpen = false;
      if (this.state != 4) {
        nd.preClose(this.fd);
      }
      if (this.readerThread != 0L) {
        NativeThread.signal(this.readerThread);
      }
      if (this.writerThread != 0L) {
        NativeThread.signal(this.writerThread);
      }
      if (!isRegistered()) {
        kill();
      }
    }
  }
  
  public void kill()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      if (this.state == 4) {
        return;
      }
      if (this.state == -1)
      {
        this.state = 4;
        return;
      }
      assert ((!isOpen()) && (!isRegistered()));
      if ((this.readerThread == 0L) && (this.writerThread == 0L))
      {
        nd.close(this.fd);
        this.state = 4;
      }
      else
      {
        this.state = 3;
      }
    }
  }
  
  public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
  {
    int i = paramSelectionKeyImpl.nioInterestOps();
    int j = paramSelectionKeyImpl.nioReadyOps();
    int k = paramInt2;
    if ((paramInt1 & Net.POLLNVAL) != 0) {
      return false;
    }
    if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0)
    {
      k = i;
      paramSelectionKeyImpl.nioReadyOps(k);
      this.readyToConnect = true;
      return (k & (j ^ 0xFFFFFFFF)) != 0;
    }
    if (((paramInt1 & Net.POLLIN) != 0) && ((i & 0x1) != 0) && (this.state == 2)) {
      k |= 0x1;
    }
    if (((paramInt1 & Net.POLLCONN) != 0) && ((i & 0x8) != 0) && ((this.state == 0) || (this.state == 1)))
    {
      k |= 0x8;
      this.readyToConnect = true;
    }
    if (((paramInt1 & Net.POLLOUT) != 0) && ((i & 0x4) != 0) && (this.state == 2)) {
      k |= 0x4;
    }
    paramSelectionKeyImpl.nioReadyOps(k);
    return (k & (j ^ 0xFFFFFFFF)) != 0;
  }
  
  public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
  {
    return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
  }
  
  public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
  {
    return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
  }
  
  int poll(int paramInt, long paramLong)
    throws IOException
  {
    assert ((Thread.holdsLock(blockingLock())) && (!isBlocking()));
    synchronized (this.readLock)
    {
      int i = 0;
      try
      {
        begin();
        synchronized (this.stateLock)
        {
          if (!isOpen())
          {
            int j = 0;
            readerCleanup();
            end(i > 0);
            return j;
          }
          this.readerThread = NativeThread.current();
        }
        i = Net.poll(this.fd, paramInt, paramLong);
      }
      finally
      {
        readerCleanup();
        end(i > 0);
      }
      return i;
    }
  }
  
  public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
  {
    int i = 0;
    if ((paramInt & 0x1) != 0) {
      i |= Net.POLLIN;
    }
    if ((paramInt & 0x4) != 0) {
      i |= Net.POLLOUT;
    }
    if ((paramInt & 0x8) != 0) {
      i |= Net.POLLCONN;
    }
    paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
  }
  
  public FileDescriptor getFD()
  {
    return this.fd;
  }
  
  public int getFDVal()
  {
    return this.fdVal;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getClass().getSuperclass().getName());
    localStringBuffer.append('[');
    if (!isOpen()) {
      localStringBuffer.append("closed");
    } else {
      synchronized (this.stateLock)
      {
        switch (this.state)
        {
        case 0: 
          localStringBuffer.append("unconnected");
          break;
        case 1: 
          localStringBuffer.append("connection-pending");
          break;
        case 2: 
          localStringBuffer.append("connected");
          if (!this.isInputOpen) {
            localStringBuffer.append(" ishut");
          }
          if (!this.isOutputOpen) {
            localStringBuffer.append(" oshut");
          }
          break;
        }
        InetSocketAddress localInetSocketAddress = localAddress();
        if (localInetSocketAddress != null)
        {
          localStringBuffer.append(" local=");
          localStringBuffer.append(Net.getRevealedLocalAddressAsString(localInetSocketAddress));
        }
        if (remoteAddress() != null)
        {
          localStringBuffer.append(" remote=");
          localStringBuffer.append(remoteAddress().toString());
        }
      }
    }
    localStringBuffer.append(']');
    return localStringBuffer.toString();
  }
  
  private static native int checkConnect(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException;
  
  private static native int sendOutOfBandData(FileDescriptor paramFileDescriptor, byte paramByte)
    throws IOException;
  
  static
  {
    IOUtil.load();
  }
  
  private static class DefaultOptionsHolder
  {
    static final Set<SocketOption<?>> defaultOptions = ;
    
    private DefaultOptionsHolder() {}
    
    private static Set<SocketOption<?>> defaultOptions()
    {
      HashSet localHashSet = new HashSet(8);
      localHashSet.add(StandardSocketOptions.SO_SNDBUF);
      localHashSet.add(StandardSocketOptions.SO_RCVBUF);
      localHashSet.add(StandardSocketOptions.SO_KEEPALIVE);
      localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
      localHashSet.add(StandardSocketOptions.SO_LINGER);
      localHashSet.add(StandardSocketOptions.TCP_NODELAY);
      localHashSet.add(StandardSocketOptions.IP_TOS);
      localHashSet.add(ExtendedSocketOption.SO_OOBINLINE);
      if (ExtendedOptionsImpl.flowSupported()) {
        localHashSet.add(ExtendedSocketOptions.SO_FLOW_SLA);
      }
      return Collections.unmodifiableSet(localHashSet);
    }
  }
}
