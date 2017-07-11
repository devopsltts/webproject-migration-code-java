package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.ResourceManager;

class DatagramChannelImpl
  extends DatagramChannel
  implements SelChImpl
{
  private static NativeDispatcher nd;
  private final FileDescriptor fd;
  private final int fdVal;
  private final ProtocolFamily family;
  private volatile long readerThread = 0L;
  private volatile long writerThread = 0L;
  private InetAddress cachedSenderInetAddress;
  private int cachedSenderPort;
  private final Object readLock = new Object();
  private final Object writeLock = new Object();
  private final Object stateLock = new Object();
  private static final int ST_UNINITIALIZED = -1;
  private static final int ST_UNCONNECTED = 0;
  private static final int ST_CONNECTED = 1;
  private static final int ST_KILLED = 2;
  private int state = -1;
  private InetSocketAddress localAddress;
  private InetSocketAddress remoteAddress;
  private DatagramSocket socket;
  private MembershipRegistry registry;
  private boolean reuseAddressEmulated;
  private boolean isReuseAddress;
  private SocketAddress sender;
  
  public DatagramChannelImpl(SelectorProvider paramSelectorProvider)
    throws IOException
  {
    super(paramSelectorProvider);
    ResourceManager.beforeUdpCreate();
    try
    {
      this.family = (Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET);
      this.fd = Net.socket(this.family, false);
      this.fdVal = IOUtil.fdVal(this.fd);
      this.state = 0;
    }
    catch (IOException localIOException)
    {
      ResourceManager.afterUdpClose();
      throw localIOException;
    }
  }
  
  public DatagramChannelImpl(SelectorProvider paramSelectorProvider, ProtocolFamily paramProtocolFamily)
    throws IOException
  {
    super(paramSelectorProvider);
    if ((paramProtocolFamily != StandardProtocolFamily.INET) && (paramProtocolFamily != StandardProtocolFamily.INET6))
    {
      if (paramProtocolFamily == null) {
        throw new NullPointerException("'family' is null");
      }
      throw new UnsupportedOperationException("Protocol family not supported");
    }
    if ((paramProtocolFamily == StandardProtocolFamily.INET6) && (!Net.isIPv6Available())) {
      throw new UnsupportedOperationException("IPv6 not available");
    }
    this.family = paramProtocolFamily;
    this.fd = Net.socket(paramProtocolFamily, false);
    this.fdVal = IOUtil.fdVal(this.fd);
    this.state = 0;
  }
  
  public DatagramChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor)
    throws IOException
  {
    super(paramSelectorProvider);
    this.family = (Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET);
    this.fd = paramFileDescriptor;
    this.fdVal = IOUtil.fdVal(paramFileDescriptor);
    this.state = 0;
    this.localAddress = Net.localAddress(paramFileDescriptor);
  }
  
  public DatagramSocket socket()
  {
    synchronized (this.stateLock)
    {
      if (this.socket == null) {
        this.socket = DatagramSocketAdaptor.create(this);
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
  
  public <T> DatagramChannel setOption(SocketOption<T> paramSocketOption, T paramT)
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
      ensureOpen();
      if ((paramSocketOption == StandardSocketOptions.IP_TOS) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP))
      {
        Net.setSocketOption(this.fd, this.family, paramSocketOption, paramT);
        return this;
      }
      if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF)
      {
        if (paramT == null) {
          throw new IllegalArgumentException("Cannot set IP_MULTICAST_IF to 'null'");
        }
        NetworkInterface localNetworkInterface = (NetworkInterface)paramT;
        if (this.family == StandardProtocolFamily.INET6)
        {
          int i = localNetworkInterface.getIndex();
          if (i == -1) {
            throw new IOException("Network interface cannot be identified");
          }
          Net.setInterface6(this.fd, i);
        }
        else
        {
          Inet4Address localInet4Address = Net.anyInet4Address(localNetworkInterface);
          if (localInet4Address == null) {
            throw new IOException("Network interface not configured for IPv4");
          }
          int j = Net.inet4AsInt(localInet4Address);
          Net.setInterface4(this.fd, j);
        }
        return this;
      }
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind()) && (this.localAddress != null))
      {
        this.reuseAddressEmulated = true;
        this.isReuseAddress = ((Boolean)paramT).booleanValue();
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
      ensureOpen();
      if ((paramSocketOption == StandardSocketOptions.IP_TOS) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP)) {
        return Net.getSocketOption(this.fd, this.family, paramSocketOption);
      }
      if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF)
      {
        if (this.family == StandardProtocolFamily.INET)
        {
          i = Net.getInterface4(this.fd);
          if (i == 0) {
            return null;
          }
          localObject1 = Net.inet4FromInt(i);
          NetworkInterface localNetworkInterface = NetworkInterface.getByInetAddress((InetAddress)localObject1);
          if (localNetworkInterface == null) {
            throw new IOException("Unable to map address to interface");
          }
          return localNetworkInterface;
        }
        int i = Net.getInterface6(this.fd);
        if (i == 0) {
          return null;
        }
        Object localObject1 = NetworkInterface.getByIndex(i);
        if (localObject1 == null) {
          throw new IOException("Unable to map index to interface");
        }
        return localObject1;
      }
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (this.reuseAddressEmulated)) {
        return Boolean.valueOf(this.isReuseAddress);
      }
      return Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
    }
  }
  
  public final Set<SocketOption<?>> supportedOptions()
  {
    return DefaultOptionsHolder.defaultOptions;
  }
  
  private void ensureOpen()
    throws ClosedChannelException
  {
    if (!isOpen()) {
      throw new ClosedChannelException();
    }
  }
  
  public SocketAddress receive(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (paramByteBuffer.isReadOnly()) {
      throw new IllegalArgumentException("Read-only buffer");
    }
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    synchronized (this.readLock)
    {
      ensureOpen();
      if (localAddress() == null) {
        bind(null);
      }
      int i = 0;
      ByteBuffer localByteBuffer = null;
      try
      {
        begin();
        if (!isOpen())
        {
          localObject1 = null;
          if (localByteBuffer != null) {
            Util.releaseTemporaryDirectBuffer(localByteBuffer);
          }
          this.readerThread = 0L;
          end((i > 0) || (i == -2));
          assert (IOStatus.check(i));
          return localObject1;
        }
        Object localObject1 = System.getSecurityManager();
        this.readerThread = NativeThread.current();
        if ((isConnected()) || (localObject1 == null))
        {
          do
          {
            i = receive(this.fd, paramByteBuffer);
          } while ((i == -3) && (isOpen()));
          if (i == -2)
          {
            localObject2 = null;
            if (localByteBuffer != null) {
              Util.releaseTemporaryDirectBuffer(localByteBuffer);
            }
            this.readerThread = 0L;
            end((i > 0) || (i == -2));
            assert (IOStatus.check(i));
            return localObject2;
          }
        }
        else
        {
          localByteBuffer = Util.getTemporaryDirectBuffer(paramByteBuffer.remaining());
          for (;;)
          {
            i = receive(this.fd, localByteBuffer);
            if ((i != -3) || (!isOpen()))
            {
              if (i == -2)
              {
                localObject2 = null;
                if (localByteBuffer != null) {
                  Util.releaseTemporaryDirectBuffer(localByteBuffer);
                }
                this.readerThread = 0L;
                end((i > 0) || (i == -2));
                assert (IOStatus.check(i));
                return localObject2;
              }
              localObject2 = (InetSocketAddress)this.sender;
              try
              {
                ((SecurityManager)localObject1).checkAccept(((InetSocketAddress)localObject2).getAddress().getHostAddress(), ((InetSocketAddress)localObject2).getPort());
              }
              catch (SecurityException localSecurityException)
              {
                localByteBuffer.clear();
                i = 0;
              }
            }
          }
          localByteBuffer.flip();
          paramByteBuffer.put(localByteBuffer);
        }
        Object localObject2 = this.sender;
        if (localByteBuffer != null) {
          Util.releaseTemporaryDirectBuffer(localByteBuffer);
        }
        this.readerThread = 0L;
        end((i > 0) || (i == -2));
        assert (IOStatus.check(i));
        return localObject2;
      }
      finally
      {
        if (localByteBuffer != null) {
          Util.releaseTemporaryDirectBuffer(localByteBuffer);
        }
        this.readerThread = 0L;
        end((i > 0) || (i == -2));
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  private int receive(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer)
    throws IOException
  {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    if (((paramByteBuffer instanceof DirectBuffer)) && (k > 0)) {
      return receiveIntoNativeBuffer(paramFileDescriptor, paramByteBuffer, k, i);
    }
    int m = Math.max(k, 1);
    ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(m);
    try
    {
      int n = receiveIntoNativeBuffer(paramFileDescriptor, localByteBuffer, m, 0);
      localByteBuffer.flip();
      if ((n > 0) && (k > 0)) {
        paramByteBuffer.put(localByteBuffer);
      }
      int i1 = n;
      return i1;
    }
    finally
    {
      Util.releaseTemporaryDirectBuffer(localByteBuffer);
    }
  }
  
  private int receiveIntoNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = receive0(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + paramInt2, paramInt1, isConnected());
    if (i > 0) {
      paramByteBuffer.position(paramInt2 + i);
    }
    return i;
  }
  
  public int send(ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress)
    throws IOException
  {
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    synchronized (this.writeLock)
    {
      ensureOpen();
      InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
      InetAddress localInetAddress = localInetSocketAddress.getAddress();
      if (localInetAddress == null) {
        throw new IOException("Target address not resolved");
      }
      synchronized (this.stateLock)
      {
        if (!isConnected())
        {
          if (paramSocketAddress == null) {
            throw new NullPointerException();
          }
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null) {
            if (localInetAddress.isMulticastAddress()) {
              localSecurityManager.checkMulticast(localInetAddress);
            } else {
              localSecurityManager.checkConnect(localInetAddress.getHostAddress(), localInetSocketAddress.getPort());
            }
          }
        }
        else
        {
          if (!paramSocketAddress.equals(this.remoteAddress)) {
            throw new IllegalArgumentException("Connected address not equal to target address");
          }
          return write(paramByteBuffer);
        }
      }
      int i = 0;
      try
      {
        begin();
        if (!isOpen())
        {
          int j = 0;
          this.writerThread = 0L;
          end((i > 0) || (i == -2));
          assert (IOStatus.check(i));
          return j;
        }
        this.writerThread = NativeThread.current();
        do
        {
          i = send(this.fd, paramByteBuffer, localInetSocketAddress);
        } while ((i == -3) && (isOpen()));
        synchronized (this.stateLock)
        {
          if ((isOpen()) && (this.localAddress == null)) {
            this.localAddress = Net.localAddress(this.fd);
          }
        }
        int k = IOStatus.normalize(i);
        this.writerThread = 0L;
        end((i > 0) || (i == -2));
        assert (IOStatus.check(i));
        return k;
      }
      finally
      {
        this.writerThread = 0L;
        end((i > 0) || (i == -2));
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  private int send(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    if ((paramByteBuffer instanceof DirectBuffer)) {
      return sendFromNativeBuffer(paramFileDescriptor, paramByteBuffer, paramInetSocketAddress);
    }
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(k);
    try
    {
      localByteBuffer.put(paramByteBuffer);
      localByteBuffer.flip();
      paramByteBuffer.position(i);
      int m = sendFromNativeBuffer(paramFileDescriptor, localByteBuffer, paramInetSocketAddress);
      if (m > 0) {
        paramByteBuffer.position(i + m);
      }
      int n = m;
      return n;
    }
    finally
    {
      Util.releaseTemporaryDirectBuffer(localByteBuffer);
    }
  }
  
  private int sendFromNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    boolean bool = this.family != StandardProtocolFamily.INET;
    int m;
    try
    {
      m = send0(bool, paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
    }
    catch (PortUnreachableException localPortUnreachableException)
    {
      if (isConnected()) {
        throw localPortUnreachableException;
      }
      m = k;
    }
    if (m > 0) {
      paramByteBuffer.position(i + m);
    }
    return m;
  }
  
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    synchronized (this.readLock)
    {
      synchronized (this.stateLock)
      {
        ensureOpen();
        if (!isConnected()) {
          throw new NotYetConnectedException();
        }
      }
      int i = 0;
      try
      {
        begin();
        if (!isOpen())
        {
          j = 0;
          this.readerThread = 0L;
          end((i > 0) || (i == -2));
          assert (IOStatus.check(i));
          return j;
        }
        this.readerThread = NativeThread.current();
        do
        {
          i = IOUtil.read(this.fd, paramByteBuffer, -1L, nd);
        } while ((i == -3) && (isOpen()));
        int j = IOStatus.normalize(i);
        this.readerThread = 0L;
        end((i > 0) || (i == -2));
        assert (IOStatus.check(i));
        return j;
      }
      finally
      {
        this.readerThread = 0L;
        end((i > 0) || (i == -2));
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
      synchronized (this.stateLock)
      {
        ensureOpen();
        if (!isConnected()) {
          throw new NotYetConnectedException();
        }
      }
      long l1 = 0L;
      try
      {
        begin();
        if (!isOpen())
        {
          l2 = 0L;
          this.readerThread = 0L;
          end((l1 > 0L) || (l1 == -2L));
          assert (IOStatus.check(l1));
          return l2;
        }
        this.readerThread = NativeThread.current();
        do
        {
          l1 = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        this.readerThread = 0L;
        end((l1 > 0L) || (l1 == -2L));
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        this.readerThread = 0L;
        end((l1 > 0L) || (l1 == -2L));
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
      synchronized (this.stateLock)
      {
        ensureOpen();
        if (!isConnected()) {
          throw new NotYetConnectedException();
        }
      }
      int i = 0;
      try
      {
        begin();
        if (!isOpen())
        {
          j = 0;
          this.writerThread = 0L;
          end((i > 0) || (i == -2));
          assert (IOStatus.check(i));
          return j;
        }
        this.writerThread = NativeThread.current();
        do
        {
          i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd);
        } while ((i == -3) && (isOpen()));
        int j = IOStatus.normalize(i);
        this.writerThread = 0L;
        end((i > 0) || (i == -2));
        assert (IOStatus.check(i));
        return j;
      }
      finally
      {
        this.writerThread = 0L;
        end((i > 0) || (i == -2));
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
      synchronized (this.stateLock)
      {
        ensureOpen();
        if (!isConnected()) {
          throw new NotYetConnectedException();
        }
      }
      long l1 = 0L;
      try
      {
        begin();
        if (!isOpen())
        {
          l2 = 0L;
          this.writerThread = 0L;
          end((l1 > 0L) || (l1 == -2L));
          assert (IOStatus.check(l1));
          return l2;
        }
        this.writerThread = NativeThread.current();
        do
        {
          l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        this.writerThread = 0L;
        end((l1 > 0L) || (l1 == -2L));
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        this.writerThread = 0L;
        end((l1 > 0L) || (l1 == -2L));
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
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
  
  public SocketAddress localAddress()
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
  
  public DatagramChannel bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    synchronized (this.readLock)
    {
      synchronized (this.writeLock)
      {
        synchronized (this.stateLock)
        {
          ensureOpen();
          if (this.localAddress != null) {
            throw new AlreadyBoundException();
          }
          InetSocketAddress localInetSocketAddress;
          if (paramSocketAddress == null)
          {
            if (this.family == StandardProtocolFamily.INET) {
              localInetSocketAddress = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0);
            } else {
              localInetSocketAddress = new InetSocketAddress(0);
            }
          }
          else
          {
            localInetSocketAddress = Net.checkAddress(paramSocketAddress);
            if (this.family == StandardProtocolFamily.INET)
            {
              localObject1 = localInetSocketAddress.getAddress();
              if (!(localObject1 instanceof Inet4Address)) {
                throw new UnsupportedAddressTypeException();
              }
            }
          }
          Object localObject1 = System.getSecurityManager();
          if (localObject1 != null) {
            ((SecurityManager)localObject1).checkListen(localInetSocketAddress.getPort());
          }
          Net.bind(this.family, this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
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
      if (this.state != 0) {
        throw new IllegalStateException("Connect already invoked");
      }
    }
  }
  
  public DatagramChannel connect(SocketAddress paramSocketAddress)
    throws IOException
  {
    int i = 0;
    synchronized (this.readLock)
    {
      synchronized (this.writeLock)
      {
        synchronized (this.stateLock)
        {
          ensureOpenAndUnconnected();
          InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null) {
            localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
          }
          int j = Net.connect(this.family, this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
          if (j <= 0) {
            throw new Error();
          }
          this.state = 1;
          this.remoteAddress = localInetSocketAddress;
          this.sender = localInetSocketAddress;
          this.cachedSenderInetAddress = localInetSocketAddress.getAddress();
          this.cachedSenderPort = localInetSocketAddress.getPort();
          this.localAddress = Net.localAddress(this.fd);
          boolean bool = false;
          synchronized (blockingLock())
          {
            try
            {
              bool = isBlocking();
              ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
              if (bool) {
                configureBlocking(false);
              }
              do
              {
                localByteBuffer.clear();
              } while (receive(localByteBuffer) != null);
            }
            finally
            {
              if (bool) {
                configureBlocking(true);
              }
            }
          }
        }
      }
    }
    return this;
  }
  
  public DatagramChannel disconnect()
    throws IOException
  {
    synchronized (this.readLock)
    {
      synchronized (this.writeLock)
      {
        synchronized (this.stateLock)
        {
          if ((!isConnected()) || (!isOpen())) {
            return this;
          }
          InetSocketAddress localInetSocketAddress = this.remoteAddress;
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null) {
            localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
          }
          boolean bool = this.family == StandardProtocolFamily.INET6;
          disconnect0(this.fd, bool);
          this.remoteAddress = null;
          this.state = 0;
          this.localAddress = Net.localAddress(this.fd);
        }
      }
    }
    return this;
  }
  
  private MembershipKey innerJoin(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2)
    throws IOException
  {
    if (!paramInetAddress1.isMulticastAddress()) {
      throw new IllegalArgumentException("Group not a multicast address");
    }
    if ((paramInetAddress1 instanceof Inet4Address))
    {
      if ((this.family == StandardProtocolFamily.INET6) && (!Net.canIPv6SocketJoinIPv4Group())) {
        throw new IllegalArgumentException("IPv6 socket cannot join IPv4 multicast group");
      }
    }
    else if ((paramInetAddress1 instanceof Inet6Address))
    {
      if (this.family != StandardProtocolFamily.INET6) {
        throw new IllegalArgumentException("Only IPv6 sockets can join IPv6 multicast group");
      }
    }
    else {
      throw new IllegalArgumentException("Address type not supported");
    }
    if (paramInetAddress2 != null)
    {
      if (paramInetAddress2.isAnyLocalAddress()) {
        throw new IllegalArgumentException("Source address is a wildcard address");
      }
      if (paramInetAddress2.isMulticastAddress()) {
        throw new IllegalArgumentException("Source address is multicast address");
      }
      if (paramInetAddress2.getClass() != paramInetAddress1.getClass()) {
        throw new IllegalArgumentException("Source address is different type to group");
      }
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(paramInetAddress1);
    }
    synchronized (this.stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      Object localObject1;
      if (this.registry == null)
      {
        this.registry = new MembershipRegistry();
      }
      else
      {
        localObject1 = this.registry.checkMembership(paramInetAddress1, paramNetworkInterface, paramInetAddress2);
        if (localObject1 != null) {
          return localObject1;
        }
      }
      int m;
      if ((this.family == StandardProtocolFamily.INET6) && (((paramInetAddress1 instanceof Inet6Address)) || (Net.canJoin6WithIPv4Group())))
      {
        int i = paramNetworkInterface.getIndex();
        if (i == -1) {
          throw new IOException("Network interface cannot be identified");
        }
        byte[] arrayOfByte1 = Net.inet6AsByteArray(paramInetAddress1);
        byte[] arrayOfByte2 = paramInetAddress2 == null ? null : Net.inet6AsByteArray(paramInetAddress2);
        m = Net.join6(this.fd, arrayOfByte1, i, arrayOfByte2);
        if (m == -2) {
          throw new UnsupportedOperationException();
        }
        localObject1 = new MembershipKeyImpl.Type6(this, paramInetAddress1, paramNetworkInterface, paramInetAddress2, arrayOfByte1, i, arrayOfByte2);
      }
      else
      {
        Inet4Address localInet4Address = Net.anyInet4Address(paramNetworkInterface);
        if (localInet4Address == null) {
          throw new IOException("Network interface not configured for IPv4");
        }
        int j = Net.inet4AsInt(paramInetAddress1);
        int k = Net.inet4AsInt(localInet4Address);
        m = paramInetAddress2 == null ? 0 : Net.inet4AsInt(paramInetAddress2);
        int n = Net.join4(this.fd, j, k, m);
        if (n == -2) {
          throw new UnsupportedOperationException();
        }
        localObject1 = new MembershipKeyImpl.Type4(this, paramInetAddress1, paramNetworkInterface, paramInetAddress2, j, k, m);
      }
      this.registry.add((MembershipKeyImpl)localObject1);
      return localObject1;
    }
  }
  
  public MembershipKey join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    return innerJoin(paramInetAddress, paramNetworkInterface, null);
  }
  
  public MembershipKey join(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2)
    throws IOException
  {
    if (paramInetAddress2 == null) {
      throw new NullPointerException("source address is null");
    }
    return innerJoin(paramInetAddress1, paramNetworkInterface, paramInetAddress2);
  }
  
  void drop(MembershipKeyImpl paramMembershipKeyImpl)
  {
    assert (paramMembershipKeyImpl.channel() == this);
    synchronized (this.stateLock)
    {
      if (!paramMembershipKeyImpl.isValid()) {
        return;
      }
      try
      {
        Object localObject1;
        if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6))
        {
          localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
          Net.drop6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), ((MembershipKeyImpl.Type6)localObject1).source());
        }
        else
        {
          localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
          Net.drop4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), ((MembershipKeyImpl.Type4)localObject1).source());
        }
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
      paramMembershipKeyImpl.invalidate();
      this.registry.remove(paramMembershipKeyImpl);
    }
  }
  
  void block(MembershipKeyImpl paramMembershipKeyImpl, InetAddress paramInetAddress)
    throws IOException
  {
    assert (paramMembershipKeyImpl.channel() == this);
    assert (paramMembershipKeyImpl.sourceAddress() == null);
    synchronized (this.stateLock)
    {
      if (!paramMembershipKeyImpl.isValid()) {
        throw new IllegalStateException("key is no longer valid");
      }
      if (paramInetAddress.isAnyLocalAddress()) {
        throw new IllegalArgumentException("Source address is a wildcard address");
      }
      if (paramInetAddress.isMulticastAddress()) {
        throw new IllegalArgumentException("Source address is multicast address");
      }
      if (paramInetAddress.getClass() != paramMembershipKeyImpl.group().getClass()) {
        throw new IllegalArgumentException("Source address is different type to group");
      }
      Object localObject1;
      int i;
      if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6))
      {
        localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
        i = Net.block6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), Net.inet6AsByteArray(paramInetAddress));
      }
      else
      {
        localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
        i = Net.block4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), Net.inet4AsInt(paramInetAddress));
      }
      if (i == -2) {
        throw new UnsupportedOperationException();
      }
    }
  }
  
  void unblock(MembershipKeyImpl paramMembershipKeyImpl, InetAddress paramInetAddress)
  {
    assert (paramMembershipKeyImpl.channel() == this);
    assert (paramMembershipKeyImpl.sourceAddress() == null);
    synchronized (this.stateLock)
    {
      if (!paramMembershipKeyImpl.isValid()) {
        throw new IllegalStateException("key is no longer valid");
      }
      try
      {
        Object localObject1;
        if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6))
        {
          localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
          Net.unblock6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), Net.inet6AsByteArray(paramInetAddress));
        }
        else
        {
          localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
          Net.unblock4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), Net.inet4AsInt(paramInetAddress));
        }
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
  }
  
  protected void implCloseSelectableChannel()
    throws IOException
  {
    synchronized (this.stateLock)
    {
      if (this.state != 2) {
        nd.preClose(this.fd);
      }
      ResourceManager.afterUdpClose();
      if (this.registry != null) {
        this.registry.invalidateAll();
      }
      long l;
      if ((l = this.readerThread) != 0L) {
        NativeThread.signal(l);
      }
      if ((l = this.writerThread) != 0L) {
        NativeThread.signal(l);
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
      if (this.state == 2) {
        return;
      }
      if (this.state == -1)
      {
        this.state = 2;
        return;
      }
      assert ((!isOpen()) && (!isRegistered()));
      nd.close(this.fd);
      this.state = 2;
    }
  }
  
  protected void finalize()
    throws IOException
  {
    if (this.fd != null) {
      close();
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
      return (k & (j ^ 0xFFFFFFFF)) != 0;
    }
    if (((paramInt1 & Net.POLLIN) != 0) && ((i & 0x1) != 0)) {
      k |= 0x1;
    }
    if (((paramInt1 & Net.POLLOUT) != 0) && ((i & 0x4) != 0)) {
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
            this.readerThread = 0L;
            end(i > 0);
            return j;
          }
          this.readerThread = NativeThread.current();
        }
        i = Net.poll(this.fd, paramInt, paramLong);
      }
      finally
      {
        this.readerThread = 0L;
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
      i |= Net.POLLIN;
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
  
  private static native void initIDs();
  
  private static native void disconnect0(FileDescriptor paramFileDescriptor, boolean paramBoolean)
    throws IOException;
  
  private native int receive0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt, boolean paramBoolean)
    throws IOException;
  
  private native int send0(boolean paramBoolean, FileDescriptor paramFileDescriptor, long paramLong, int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws IOException;
  
  static
  {
    nd = new DatagramDispatcher();
    IOUtil.load();
    initIDs();
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
      localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
      localHashSet.add(StandardSocketOptions.SO_BROADCAST);
      localHashSet.add(StandardSocketOptions.IP_TOS);
      localHashSet.add(StandardSocketOptions.IP_MULTICAST_IF);
      localHashSet.add(StandardSocketOptions.IP_MULTICAST_TTL);
      localHashSet.add(StandardSocketOptions.IP_MULTICAST_LOOP);
      if (ExtendedOptionsImpl.flowSupported()) {
        localHashSet.add(ExtendedSocketOptions.SO_FLOW_SLA);
      }
      return Collections.unmodifiableSet(localHashSet);
    }
  }
}
