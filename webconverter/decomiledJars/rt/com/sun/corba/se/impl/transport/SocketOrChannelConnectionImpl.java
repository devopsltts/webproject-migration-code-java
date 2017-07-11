package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.CachedCodeBase;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetContext;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.CancelRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;
import com.sun.corba.se.spi.transport.ORBSocketFactory;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.SystemException;
import sun.corba.OutputStreamFactory;

public class SocketOrChannelConnectionImpl
  extends EventHandlerBase
  implements CorbaConnection, Work
{
  public static boolean dprintWriteLocks = false;
  protected long enqueueTime;
  protected SocketChannel socketChannel;
  protected CorbaContactInfo contactInfo;
  protected Acceptor acceptor;
  protected ConnectionCache connectionCache;
  protected Socket socket;
  protected long timeStamp = 0L;
  protected boolean isServer = false;
  protected int requestId = 5;
  protected CorbaResponseWaitingRoom responseWaitingRoom;
  protected int state;
  protected Object stateEvent = new Object();
  protected Object writeEvent = new Object();
  protected boolean writeLocked;
  protected int serverRequestCount = 0;
  Map serverRequestMap = null;
  protected boolean postInitialContexts = false;
  protected IOR codeBaseServerIOR;
  protected CachedCodeBase cachedCodeBase = new CachedCodeBase(this);
  protected ORBUtilSystemException wrapper;
  protected ReadTimeouts readTimeouts;
  protected boolean shouldReadGiopHeaderOnly;
  protected CorbaMessageMediator partialMessageMediator = null;
  protected CodeSetComponentInfo.CodeSetContext codeSetContext = null;
  protected MessageMediator clientReply_1_1;
  protected MessageMediator serverRequest_1_1;
  
  public SocketChannel getSocketChannel()
  {
    return this.socketChannel;
  }
  
  protected SocketOrChannelConnectionImpl(ORB paramORB)
  {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    setWork(this);
    this.responseWaitingRoom = new CorbaResponseWaitingRoomImpl(paramORB, this);
    setReadTimeouts(paramORB.getORBData().getTransportTCPReadTimeouts());
  }
  
  protected SocketOrChannelConnectionImpl(ORB paramORB, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramORB);
    setUseSelectThreadToWait(paramBoolean1);
    setUseWorkerThreadForEvent(paramBoolean2);
  }
  
  public SocketOrChannelConnectionImpl(ORB paramORB, CorbaContactInfo paramCorbaContactInfo, boolean paramBoolean1, boolean paramBoolean2, String paramString1, String paramString2, int paramInt)
  {
    this(paramORB, paramBoolean1, paramBoolean2);
    this.contactInfo = paramCorbaContactInfo;
    try
    {
      this.socket = paramORB.getORBData().getSocketFactory().createSocket(paramString1, new InetSocketAddress(paramString2, paramInt));
      this.socketChannel = this.socket.getChannel();
      if (this.socketChannel != null)
      {
        boolean bool = !paramBoolean1;
        this.socketChannel.configureBlocking(bool);
      }
      else
      {
        setUseSelectThreadToWait(false);
      }
      if (paramORB.transportDebugFlag) {
        dprint(".initialize: connection created: " + this.socket);
      }
    }
    catch (Throwable localThrowable)
    {
      throw this.wrapper.connectFailure(localThrowable, paramString1, paramString2, Integer.toString(paramInt));
    }
    this.state = 1;
  }
  
  public SocketOrChannelConnectionImpl(ORB paramORB, CorbaContactInfo paramCorbaContactInfo, String paramString1, String paramString2, int paramInt)
  {
    this(paramORB, paramCorbaContactInfo, paramORB.getORBData().connectionSocketUseSelectThreadToWait(), paramORB.getORBData().connectionSocketUseWorkerThreadForEvent(), paramString1, paramString2, paramInt);
  }
  
  public SocketOrChannelConnectionImpl(ORB paramORB, Acceptor paramAcceptor, Socket paramSocket, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramORB, paramBoolean1, paramBoolean2);
    this.socket = paramSocket;
    this.socketChannel = paramSocket.getChannel();
    if (this.socketChannel != null) {
      try
      {
        boolean bool = !paramBoolean1;
        this.socketChannel.configureBlocking(bool);
      }
      catch (IOException localIOException)
      {
        RuntimeException localRuntimeException = new RuntimeException();
        localRuntimeException.initCause(localIOException);
        throw localRuntimeException;
      }
    }
    this.acceptor = paramAcceptor;
    this.serverRequestMap = Collections.synchronizedMap(new HashMap());
    this.isServer = true;
    this.state = 2;
  }
  
  public SocketOrChannelConnectionImpl(ORB paramORB, Acceptor paramAcceptor, Socket paramSocket)
  {
    this(paramORB, paramAcceptor, paramSocket, paramSocket.getChannel() == null ? false : paramORB.getORBData().connectionSocketUseSelectThreadToWait(), paramSocket.getChannel() == null ? false : paramORB.getORBData().connectionSocketUseWorkerThreadForEvent());
  }
  
  public boolean shouldRegisterReadEvent()
  {
    return true;
  }
  
  public boolean shouldRegisterServerReadEvent()
  {
    return true;
  }
  
  public boolean read()
  {
    try
    {
      if (this.orb.transportDebugFlag) {
        dprint(".read->: " + this);
      }
      CorbaMessageMediator localCorbaMessageMediator = readBits();
      if (localCorbaMessageMediator != null)
      {
        bool = dispatch(localCorbaMessageMediator);
        return bool;
      }
      boolean bool = true;
      return bool;
    }
    finally
    {
      if (this.orb.transportDebugFlag) {
        dprint(".read<-: " + this);
      }
    }
  }
  
  protected CorbaMessageMediator readBits()
  {
    try
    {
      if (this.orb.transportDebugFlag) {
        dprint(".readBits->: " + this);
      }
      MessageMediator localMessageMediator;
      if (this.contactInfo != null) {
        localMessageMediator = this.contactInfo.createMessageMediator(this.orb, this);
      } else if (this.acceptor != null) {
        localMessageMediator = this.acceptor.createMessageMediator(this.orb, this);
      } else {
        throw new RuntimeException("SocketOrChannelConnectionImpl.readBits");
      }
      CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)localMessageMediator;
      return localCorbaMessageMediator;
    }
    catch (ThreadDeath localThreadDeath)
    {
      if (this.orb.transportDebugFlag) {
        dprint(".readBits: " + this + ": ThreadDeath: " + localThreadDeath, localThreadDeath);
      }
      try
      {
        purgeCalls(this.wrapper.connectionAbort(localThreadDeath), false, false);
      }
      catch (Throwable localThrowable2)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".readBits: " + this + ": purgeCalls: Throwable: " + localThrowable2, localThrowable2);
        }
      }
      throw localThreadDeath;
    }
    catch (Throwable localThrowable1)
    {
      if (this.orb.transportDebugFlag) {
        dprint(".readBits: " + this + ": Throwable: " + localThrowable1, localThrowable1);
      }
      try
      {
        if ((localThrowable1 instanceof INTERNAL)) {
          sendMessageError(GIOPVersion.DEFAULT_VERSION);
        }
      }
      catch (IOException localIOException)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".readBits: " + this + ": sendMessageError: IOException: " + localIOException, localIOException);
        }
      }
      this.orb.getTransportManager().getSelector(0).unregisterForEvent(this);
      purgeCalls(this.wrapper.connectionAbort(localThrowable1), true, false);
    }
    finally
    {
      if (this.orb.transportDebugFlag) {
        dprint(".readBits<-: " + this);
      }
    }
    return null;
  }
  
  protected CorbaMessageMediator finishReadingBits(MessageMediator paramMessageMediator)
  {
    try
    {
      if (this.orb.transportDebugFlag) {
        dprint(".finishReadingBits->: " + this);
      }
      if (this.contactInfo != null) {
        paramMessageMediator = this.contactInfo.finishCreatingMessageMediator(this.orb, this, paramMessageMediator);
      } else if (this.acceptor != null) {
        paramMessageMediator = this.acceptor.finishCreatingMessageMediator(this.orb, this, paramMessageMediator);
      } else {
        throw new RuntimeException("SocketOrChannelConnectionImpl.finishReadingBits");
      }
      CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
      return localCorbaMessageMediator;
    }
    catch (ThreadDeath localThreadDeath)
    {
      if (this.orb.transportDebugFlag) {
        dprint(".finishReadingBits: " + this + ": ThreadDeath: " + localThreadDeath, localThreadDeath);
      }
      try
      {
        purgeCalls(this.wrapper.connectionAbort(localThreadDeath), false, false);
      }
      catch (Throwable localThrowable2)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".finishReadingBits: " + this + ": purgeCalls: Throwable: " + localThrowable2, localThrowable2);
        }
      }
      throw localThreadDeath;
    }
    catch (Throwable localThrowable1)
    {
      if (this.orb.transportDebugFlag) {
        dprint(".finishReadingBits: " + this + ": Throwable: " + localThrowable1, localThrowable1);
      }
      try
      {
        if ((localThrowable1 instanceof INTERNAL)) {
          sendMessageError(GIOPVersion.DEFAULT_VERSION);
        }
      }
      catch (IOException localIOException)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".finishReadingBits: " + this + ": sendMessageError: IOException: " + localIOException, localIOException);
        }
      }
      this.orb.getTransportManager().getSelector(0).unregisterForEvent(this);
      purgeCalls(this.wrapper.connectionAbort(localThrowable1), true, false);
    }
    finally
    {
      if (this.orb.transportDebugFlag) {
        dprint(".finishReadingBits<-: " + this);
      }
    }
    return null;
  }
  
  protected boolean dispatch(CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      if (this.orb.transportDebugFlag) {
        dprint(".dispatch->: " + this);
      }
      boolean bool1 = paramCorbaMessageMediator.getProtocolHandler().handleRequest(paramCorbaMessageMediator);
      boolean bool2 = bool1;
      return bool2;
    }
    catch (ThreadDeath localThreadDeath)
    {
      if (this.orb.transportDebugFlag) {
        dprint(".dispatch: ThreadDeath", localThreadDeath);
      }
      try
      {
        purgeCalls(this.wrapper.connectionAbort(localThreadDeath), false, false);
      }
      catch (Throwable localThrowable2)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".dispatch: purgeCalls: Throwable", localThrowable2);
        }
      }
      throw localThreadDeath;
    }
    catch (Throwable localThrowable1)
    {
      if (this.orb.transportDebugFlag) {
        dprint(".dispatch: Throwable", localThrowable1);
      }
      try
      {
        if ((localThrowable1 instanceof INTERNAL)) {
          sendMessageError(GIOPVersion.DEFAULT_VERSION);
        }
      }
      catch (IOException localIOException)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".dispatch: sendMessageError: IOException", localIOException);
        }
      }
      purgeCalls(this.wrapper.connectionAbort(localThrowable1), false, false);
    }
    finally
    {
      if (this.orb.transportDebugFlag) {
        dprint(".dispatch<-: " + this);
      }
    }
    return true;
  }
  
  public boolean shouldUseDirectByteBuffers()
  {
    return getSocketChannel() != null;
  }
  
  public ByteBuffer read(int paramInt1, int paramInt2, int paramInt3, long paramLong)
    throws IOException
  {
    if (shouldUseDirectByteBuffers())
    {
      localObject = this.orb.getByteBufferPool().getByteBuffer(paramInt1);
      if (this.orb.transportDebugFlag)
      {
        int i = System.identityHashCode(localObject);
        StringBuffer localStringBuffer = new StringBuffer(80);
        localStringBuffer.append(".read: got ByteBuffer id (");
        localStringBuffer.append(i).append(") from ByteBufferPool.");
        String str = localStringBuffer.toString();
        dprint(str);
      }
      ((ByteBuffer)localObject).position(paramInt2);
      ((ByteBuffer)localObject).limit(paramInt1);
      readFully((ByteBuffer)localObject, paramInt3, paramLong);
      return localObject;
    }
    Object localObject = new byte[paramInt1];
    readFully(getSocket().getInputStream(), (byte[])localObject, paramInt2, paramInt3, paramLong);
    ByteBuffer localByteBuffer = ByteBuffer.wrap((byte[])localObject);
    localByteBuffer.limit(paramInt1);
    return localByteBuffer;
  }
  
  public ByteBuffer read(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    int i = paramInt1 + paramInt2;
    if (shouldUseDirectByteBuffers())
    {
      if (!paramByteBuffer.isDirect()) {
        throw this.wrapper.unexpectedNonDirectByteBufferWithChannelSocket();
      }
      if (i > paramByteBuffer.capacity())
      {
        if (this.orb.transportDebugFlag)
        {
          int j = System.identityHashCode(paramByteBuffer);
          StringBuffer localStringBuffer = new StringBuffer(80);
          localStringBuffer.append(".read: releasing ByteBuffer id (").append(j).append(") to ByteBufferPool.");
          String str = localStringBuffer.toString();
          dprint(str);
        }
        this.orb.getByteBufferPool().releaseByteBuffer(paramByteBuffer);
        paramByteBuffer = this.orb.getByteBufferPool().getByteBuffer(i);
      }
      paramByteBuffer.position(paramInt1);
      paramByteBuffer.limit(i);
      readFully(paramByteBuffer, paramInt2, paramLong);
      paramByteBuffer.position(0);
      paramByteBuffer.limit(i);
      return paramByteBuffer;
    }
    if (paramByteBuffer.isDirect()) {
      throw this.wrapper.unexpectedDirectByteBufferWithNonChannelSocket();
    }
    byte[] arrayOfByte = new byte[i];
    readFully(getSocket().getInputStream(), arrayOfByte, paramInt1, paramInt2, paramLong);
    return ByteBuffer.wrap(arrayOfByte);
  }
  
  public void readFully(ByteBuffer paramByteBuffer, int paramInt, long paramLong)
    throws IOException
  {
    int i = 0;
    int j = 0;
    long l1 = this.readTimeouts.get_initial_time_to_wait();
    long l2 = 0L;
    do
    {
      j = getSocketChannel().read(paramByteBuffer);
      if (j < 0) {
        throw new IOException("End-of-stream");
      }
      if (j == 0) {
        try
        {
          Thread.sleep(l1);
          l2 += l1;
          l1 = (l1 * this.readTimeouts.get_backoff_factor());
        }
        catch (InterruptedException localInterruptedException)
        {
          if (this.orb.transportDebugFlag) {
            dprint("readFully(): unexpected exception " + localInterruptedException.toString());
          }
        }
      } else {
        i += j;
      }
    } while ((i < paramInt) && (l2 < paramLong));
    if ((i < paramInt) && (l2 >= paramLong)) {
      throw this.wrapper.transportReadTimeoutExceeded(new Integer(paramInt), new Integer(i), new Long(paramLong), new Long(l2));
    }
    getConnectionCache().stampTime(this);
  }
  
  public void readFully(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    int i = 0;
    int j = 0;
    long l1 = this.readTimeouts.get_initial_time_to_wait();
    long l2 = 0L;
    do
    {
      j = paramInputStream.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j < 0) {
        throw new IOException("End-of-stream");
      }
      if (j == 0) {
        try
        {
          Thread.sleep(l1);
          l2 += l1;
          l1 = (l1 * this.readTimeouts.get_backoff_factor());
        }
        catch (InterruptedException localInterruptedException)
        {
          if (this.orb.transportDebugFlag) {
            dprint("readFully(): unexpected exception " + localInterruptedException.toString());
          }
        }
      } else {
        i += j;
      }
    } while ((i < paramInt2) && (l2 < paramLong));
    if ((i < paramInt2) && (l2 >= paramLong)) {
      throw this.wrapper.transportReadTimeoutExceeded(new Integer(paramInt2), new Integer(i), new Long(paramLong), new Long(l2));
    }
    getConnectionCache().stampTime(this);
  }
  
  public void write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (shouldUseDirectByteBuffers())
    {
      do
      {
        getSocketChannel().write(paramByteBuffer);
      } while (paramByteBuffer.hasRemaining());
    }
    else
    {
      if (!paramByteBuffer.hasArray()) {
        throw this.wrapper.unexpectedDirectByteBufferWithNonChannelSocket();
      }
      byte[] arrayOfByte = paramByteBuffer.array();
      getSocket().getOutputStream().write(arrayOfByte, 0, paramByteBuffer.limit());
      getSocket().getOutputStream().flush();
    }
    getConnectionCache().stampTime(this);
  }
  
  public synchronized void close()
  {
    try
    {
      if (this.orb.transportDebugFlag) {
        dprint(".close->: " + this);
      }
      writeLock();
      if (isBusy())
      {
        writeUnlock();
        if (this.orb.transportDebugFlag) {
          dprint(".close: isBusy so no close: " + this);
        }
        return;
      }
      try
      {
        try
        {
          sendCloseConnection(GIOPVersion.V1_0);
        }
        catch (Throwable localThrowable)
        {
          this.wrapper.exceptionWhenSendingCloseConnection(localThrowable);
        }
        synchronized (this.stateEvent)
        {
          this.state = 3;
          this.stateEvent.notifyAll();
        }
        purgeCalls(this.wrapper.connectionRebind(), false, true);
      }
      catch (Exception localException)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".close: exception: " + this, localException);
        }
      }
      try
      {
        Selector localSelector = this.orb.getTransportManager().getSelector(0);
        localSelector.unregisterForEvent(this);
        if (this.socketChannel != null) {
          this.socketChannel.close();
        }
        this.socket.close();
      }
      catch (IOException localIOException)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".close: " + this, localIOException);
        }
      }
      closeConnectionResources();
    }
    finally
    {
      if (this.orb.transportDebugFlag) {
        dprint(".close<-: " + this);
      }
    }
  }
  
  public void closeConnectionResources()
  {
    if (this.orb.transportDebugFlag) {
      dprint(".closeConnectionResources->: " + this);
    }
    Selector localSelector = this.orb.getTransportManager().getSelector(0);
    localSelector.unregisterForEvent(this);
    try
    {
      if (this.socketChannel != null) {
        this.socketChannel.close();
      }
      if ((this.socket != null) && (!this.socket.isClosed())) {
        this.socket.close();
      }
    }
    catch (IOException localIOException)
    {
      if (this.orb.transportDebugFlag) {
        dprint(".closeConnectionResources: " + this, localIOException);
      }
    }
    if (this.orb.transportDebugFlag) {
      dprint(".closeConnectionResources<-: " + this);
    }
  }
  
  public Acceptor getAcceptor()
  {
    return this.acceptor;
  }
  
  public ContactInfo getContactInfo()
  {
    return this.contactInfo;
  }
  
  public EventHandler getEventHandler()
  {
    return this;
  }
  
  public OutputObject createOutputObject(MessageMediator paramMessageMediator)
  {
    throw new RuntimeException("*****SocketOrChannelConnectionImpl.createOutputObject - should not be called.");
  }
  
  public boolean isServer()
  {
    return this.isServer;
  }
  
  public boolean isBusy()
  {
    return (this.serverRequestCount > 0) || (getResponseWaitingRoom().numberRegistered() > 0);
  }
  
  public long getTimeStamp()
  {
    return this.timeStamp;
  }
  
  public void setTimeStamp(long paramLong)
  {
    this.timeStamp = paramLong;
  }
  
  public void setState(String paramString)
  {
    synchronized (this.stateEvent)
    {
      if (paramString.equals("ESTABLISHED"))
      {
        this.state = 2;
        this.stateEvent.notifyAll();
      }
    }
  }
  
  /* Error */
  public void writeLock()
  {
    // Byte code:
    //   0: getstatic 801	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprintWriteLocks	Z
    //   3: ifeq +36 -> 39
    //   6: aload_0
    //   7: getfield 815	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   10: getfield 827	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   13: ifeq +26 -> 39
    //   16: aload_0
    //   17: new 481	java/lang/StringBuilder
    //   20: dup
    //   21: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   24: ldc 38
    //   26: invokevirtual 929	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: aload_0
    //   30: invokevirtual 928	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   33: invokevirtual 925	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   36: invokevirtual 885	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprint	(Ljava/lang/String;)V
    //   39: aload_0
    //   40: getfield 798	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:state	I
    //   43: istore_1
    //   44: iload_1
    //   45: tableswitch	default:+325->370, 1:+35->80, 2:+114->159, 3:+325->370, 4:+290->335, 5:+255->300
    //   80: aload_0
    //   81: getfield 820	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:stateEvent	Ljava/lang/Object;
    //   84: dup
    //   85: astore_2
    //   86: monitorenter
    //   87: aload_0
    //   88: getfield 798	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:state	I
    //   91: iconst_1
    //   92: if_icmpeq +8 -> 100
    //   95: aload_2
    //   96: monitorexit
    //   97: goto +316 -> 413
    //   100: aload_0
    //   101: getfield 820	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:stateEvent	Ljava/lang/Object;
    //   104: invokevirtual 913	java/lang/Object:wait	()V
    //   107: goto +37 -> 144
    //   110: astore_3
    //   111: aload_0
    //   112: getfield 815	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   115: getfield 827	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   118: ifeq +26 -> 144
    //   121: aload_0
    //   122: new 481	java/lang/StringBuilder
    //   125: dup
    //   126: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   129: ldc 40
    //   131: invokevirtual 929	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: aload_0
    //   135: invokevirtual 928	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   138: invokevirtual 925	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   141: invokevirtual 885	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprint	(Ljava/lang/String;)V
    //   144: aload_2
    //   145: monitorexit
    //   146: goto +10 -> 156
    //   149: astore 4
    //   151: aload_2
    //   152: monitorexit
    //   153: aload 4
    //   155: athrow
    //   156: goto +257 -> 413
    //   159: aload_0
    //   160: getfield 821	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:writeEvent	Ljava/lang/Object;
    //   163: dup
    //   164: astore_2
    //   165: monitorenter
    //   166: aload_0
    //   167: getfield 806	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:writeLocked	Z
    //   170: ifne +50 -> 220
    //   173: aload_0
    //   174: iconst_1
    //   175: putfield 806	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:writeLocked	Z
    //   178: aload_2
    //   179: monitorexit
    //   180: getstatic 801	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprintWriteLocks	Z
    //   183: ifeq +36 -> 219
    //   186: aload_0
    //   187: getfield 815	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   190: getfield 827	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   193: ifeq +26 -> 219
    //   196: aload_0
    //   197: new 481	java/lang/StringBuilder
    //   200: dup
    //   201: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   204: ldc 43
    //   206: invokevirtual 929	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: aload_0
    //   210: invokevirtual 928	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   213: invokevirtual 925	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   216: invokevirtual 885	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprint	(Ljava/lang/String;)V
    //   219: return
    //   220: aload_0
    //   221: getfield 798	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:state	I
    //   224: iconst_2
    //   225: if_icmpne +23 -> 248
    //   228: aload_0
    //   229: getfield 806	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:writeLocked	Z
    //   232: ifeq +16 -> 248
    //   235: aload_0
    //   236: getfield 821	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:writeEvent	Ljava/lang/Object;
    //   239: ldc2_w 414
    //   242: invokevirtual 914	java/lang/Object:wait	(J)V
    //   245: goto -25 -> 220
    //   248: goto +37 -> 285
    //   251: astore_3
    //   252: aload_0
    //   253: getfield 815	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   256: getfield 827	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   259: ifeq +26 -> 285
    //   262: aload_0
    //   263: new 481	java/lang/StringBuilder
    //   266: dup
    //   267: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   270: ldc 39
    //   272: invokevirtual 929	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   275: aload_0
    //   276: invokevirtual 928	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   279: invokevirtual 925	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   282: invokevirtual 885	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprint	(Ljava/lang/String;)V
    //   285: aload_2
    //   286: monitorexit
    //   287: goto +10 -> 297
    //   290: astore 5
    //   292: aload_2
    //   293: monitorexit
    //   294: aload 5
    //   296: athrow
    //   297: goto +116 -> 413
    //   300: aload_0
    //   301: getfield 820	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:stateEvent	Ljava/lang/Object;
    //   304: dup
    //   305: astore_2
    //   306: monitorenter
    //   307: aload_0
    //   308: getfield 798	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:state	I
    //   311: iconst_5
    //   312: if_icmpeq +8 -> 320
    //   315: aload_2
    //   316: monitorexit
    //   317: goto +96 -> 413
    //   320: aload_0
    //   321: getfield 809	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:wrapper	Lcom/sun/corba/se/impl/logging/ORBUtilSystemException;
    //   324: invokevirtual 840	com/sun/corba/se/impl/logging/ORBUtilSystemException:writeErrorSend	()Lorg/omg/CORBA/COMM_FAILURE;
    //   327: athrow
    //   328: astore 6
    //   330: aload_2
    //   331: monitorexit
    //   332: aload 6
    //   334: athrow
    //   335: aload_0
    //   336: getfield 820	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:stateEvent	Ljava/lang/Object;
    //   339: dup
    //   340: astore_2
    //   341: monitorenter
    //   342: aload_0
    //   343: getfield 798	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:state	I
    //   346: iconst_4
    //   347: if_icmpeq +8 -> 355
    //   350: aload_2
    //   351: monitorexit
    //   352: goto +61 -> 413
    //   355: aload_0
    //   356: getfield 809	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:wrapper	Lcom/sun/corba/se/impl/logging/ORBUtilSystemException;
    //   359: invokevirtual 838	com/sun/corba/se/impl/logging/ORBUtilSystemException:connectionCloseRebind	()Lorg/omg/CORBA/COMM_FAILURE;
    //   362: athrow
    //   363: astore 7
    //   365: aload_2
    //   366: monitorexit
    //   367: aload 7
    //   369: athrow
    //   370: aload_0
    //   371: getfield 815	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   374: getfield 827	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   377: ifeq +26 -> 403
    //   380: aload_0
    //   381: new 481	java/lang/StringBuilder
    //   384: dup
    //   385: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   388: ldc 42
    //   390: invokevirtual 929	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   393: aload_0
    //   394: invokevirtual 928	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   397: invokevirtual 925	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   400: invokevirtual 885	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprint	(Ljava/lang/String;)V
    //   403: new 478	java/lang/RuntimeException
    //   406: dup
    //   407: ldc 41
    //   409: invokespecial 917	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   412: athrow
    //   413: goto -374 -> 39
    //   416: astore 8
    //   418: getstatic 801	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprintWriteLocks	Z
    //   421: ifeq +36 -> 457
    //   424: aload_0
    //   425: getfield 815	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   428: getfield 827	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   431: ifeq +26 -> 457
    //   434: aload_0
    //   435: new 481	java/lang/StringBuilder
    //   438: dup
    //   439: invokespecial 924	java/lang/StringBuilder:<init>	()V
    //   442: ldc 43
    //   444: invokevirtual 929	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   447: aload_0
    //   448: invokevirtual 928	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   451: invokevirtual 925	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   454: invokevirtual 885	com/sun/corba/se/impl/transport/SocketOrChannelConnectionImpl:dprint	(Ljava/lang/String;)V
    //   457: aload 8
    //   459: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	460	0	this	SocketOrChannelConnectionImpl
    //   43	2	1	i	int
    //   110	1	3	localInterruptedException1	InterruptedException
    //   251	1	3	localInterruptedException2	InterruptedException
    //   149	5	4	localObject1	Object
    //   290	5	5	localObject2	Object
    //   328	5	6	localObject3	Object
    //   363	5	7	localObject4	Object
    //   416	42	8	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   100	107	110	java/lang/InterruptedException
    //   87	97	149	finally
    //   100	146	149	finally
    //   149	153	149	finally
    //   220	248	251	java/lang/InterruptedException
    //   166	180	290	finally
    //   220	287	290	finally
    //   290	294	290	finally
    //   307	317	328	finally
    //   320	332	328	finally
    //   342	352	363	finally
    //   355	367	363	finally
    //   0	180	416	finally
    //   220	418	416	finally
  }
  
  public void writeUnlock()
  {
    try
    {
      if ((dprintWriteLocks) && (this.orb.transportDebugFlag)) {
        dprint(".writeUnlock->: " + this);
      }
      synchronized (this.writeEvent)
      {
        this.writeLocked = false;
        this.writeEvent.notify();
      }
      if ((dprintWriteLocks) && (this.orb.transportDebugFlag)) {
        dprint(".writeUnlock<-: " + this);
      }
    }
    finally
    {
      if ((dprintWriteLocks) && (this.orb.transportDebugFlag)) {
        dprint(".writeUnlock<-: " + this);
      }
    }
  }
  
  public void sendWithoutLock(OutputObject paramOutputObject)
  {
    try
    {
      CDROutputObject localCDROutputObject = (CDROutputObject)paramOutputObject;
      localCDROutputObject.writeTo(this);
    }
    catch (IOException localIOException)
    {
      COMM_FAILURE localCOMM_FAILURE = this.wrapper.writeErrorSend(localIOException);
      purgeCalls(localCOMM_FAILURE, false, true);
      throw localCOMM_FAILURE;
    }
  }
  
  public void registerWaiter(MessageMediator paramMessageMediator)
  {
    this.responseWaitingRoom.registerWaiter(paramMessageMediator);
  }
  
  public void unregisterWaiter(MessageMediator paramMessageMediator)
  {
    this.responseWaitingRoom.unregisterWaiter(paramMessageMediator);
  }
  
  public InputObject waitForResponse(MessageMediator paramMessageMediator)
  {
    return this.responseWaitingRoom.waitForResponse(paramMessageMediator);
  }
  
  public void setConnectionCache(ConnectionCache paramConnectionCache)
  {
    this.connectionCache = paramConnectionCache;
  }
  
  public ConnectionCache getConnectionCache()
  {
    return this.connectionCache;
  }
  
  public void setUseSelectThreadToWait(boolean paramBoolean)
  {
    this.useSelectThreadToWait = paramBoolean;
    setReadGiopHeaderOnly(shouldUseSelectThreadToWait());
  }
  
  public void handleEvent()
  {
    if (this.orb.transportDebugFlag) {
      dprint(".handleEvent->: " + this);
    }
    getSelectionKey().interestOps(getSelectionKey().interestOps() & (getInterestOps() ^ 0xFFFFFFFF));
    if (shouldUseWorkerThreadForEvent())
    {
      Object localObject = null;
      try
      {
        int i = 0;
        if (shouldReadGiopHeaderOnly())
        {
          this.partialMessageMediator = readBits();
          i = this.partialMessageMediator.getThreadPoolToUse();
        }
        if (this.orb.transportDebugFlag) {
          dprint(".handleEvent: addWork to pool: " + i);
        }
        this.orb.getThreadPoolManager().getThreadPool(i).getWorkQueue(0).addWork(getWork());
      }
      catch (NoSuchThreadPoolException localNoSuchThreadPoolException)
      {
        localObject = localNoSuchThreadPoolException;
      }
      catch (NoSuchWorkQueueException localNoSuchWorkQueueException)
      {
        localObject = localNoSuchWorkQueueException;
      }
      if (localObject != null)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".handleEvent: " + localObject);
        }
        INTERNAL localINTERNAL = new INTERNAL("NoSuchThreadPoolException");
        localINTERNAL.initCause(localObject);
        throw localINTERNAL;
      }
    }
    else
    {
      if (this.orb.transportDebugFlag) {
        dprint(".handleEvent: doWork");
      }
      getWork().doWork();
    }
    if (this.orb.transportDebugFlag) {
      dprint(".handleEvent<-: " + this);
    }
  }
  
  public SelectableChannel getChannel()
  {
    return this.socketChannel;
  }
  
  public int getInterestOps()
  {
    return 1;
  }
  
  public Connection getConnection()
  {
    return this;
  }
  
  public String getName()
  {
    return toString();
  }
  
  public void doWork()
  {
    try
    {
      if (this.orb.transportDebugFlag) {
        dprint(".doWork->: " + this);
      }
      if (!shouldReadGiopHeaderOnly())
      {
        read();
      }
      else
      {
        CorbaMessageMediator localCorbaMessageMediator = getPartialMessageMediator();
        localCorbaMessageMediator = finishReadingBits(localCorbaMessageMediator);
        if (localCorbaMessageMediator != null) {
          dispatch(localCorbaMessageMediator);
        }
      }
    }
    catch (Throwable localThrowable)
    {
      if (this.orb.transportDebugFlag) {
        dprint(".doWork: ignoring Throwable: " + localThrowable + " " + this);
      }
    }
    finally
    {
      if (this.orb.transportDebugFlag) {
        dprint(".doWork<-: " + this);
      }
    }
  }
  
  public void setEnqueueTime(long paramLong)
  {
    this.enqueueTime = paramLong;
  }
  
  public long getEnqueueTime()
  {
    return this.enqueueTime;
  }
  
  public boolean shouldReadGiopHeaderOnly()
  {
    return this.shouldReadGiopHeaderOnly;
  }
  
  protected void setReadGiopHeaderOnly(boolean paramBoolean)
  {
    this.shouldReadGiopHeaderOnly = paramBoolean;
  }
  
  public ResponseWaitingRoom getResponseWaitingRoom()
  {
    return this.responseWaitingRoom;
  }
  
  public void serverRequestMapPut(int paramInt, CorbaMessageMediator paramCorbaMessageMediator)
  {
    this.serverRequestMap.put(new Integer(paramInt), paramCorbaMessageMediator);
  }
  
  public CorbaMessageMediator serverRequestMapGet(int paramInt)
  {
    return (CorbaMessageMediator)this.serverRequestMap.get(new Integer(paramInt));
  }
  
  public void serverRequestMapRemove(int paramInt)
  {
    this.serverRequestMap.remove(new Integer(paramInt));
  }
  
  public Socket getSocket()
  {
    return this.socket;
  }
  
  public synchronized void serverRequestProcessingBegins()
  {
    this.serverRequestCount += 1;
  }
  
  public synchronized void serverRequestProcessingEnds()
  {
    this.serverRequestCount -= 1;
  }
  
  public synchronized int getNextRequestId()
  {
    return this.requestId++;
  }
  
  public ORB getBroker()
  {
    return this.orb;
  }
  
  public CodeSetComponentInfo.CodeSetContext getCodeSetContext()
  {
    if (this.codeSetContext == null) {
      synchronized (this)
      {
        return this.codeSetContext;
      }
    }
    return this.codeSetContext;
  }
  
  public synchronized void setCodeSetContext(CodeSetComponentInfo.CodeSetContext paramCodeSetContext)
  {
    if (this.codeSetContext == null)
    {
      if ((OSFCodeSetRegistry.lookupEntry(paramCodeSetContext.getCharCodeSet()) == null) || (OSFCodeSetRegistry.lookupEntry(paramCodeSetContext.getWCharCodeSet()) == null)) {
        throw this.wrapper.badCodesetsFromClient();
      }
      this.codeSetContext = paramCodeSetContext;
    }
  }
  
  public MessageMediator clientRequestMapGet(int paramInt)
  {
    return this.responseWaitingRoom.getMessageMediator(paramInt);
  }
  
  public void clientReply_1_1_Put(MessageMediator paramMessageMediator)
  {
    this.clientReply_1_1 = paramMessageMediator;
  }
  
  public MessageMediator clientReply_1_1_Get()
  {
    return this.clientReply_1_1;
  }
  
  public void clientReply_1_1_Remove()
  {
    this.clientReply_1_1 = null;
  }
  
  public void serverRequest_1_1_Put(MessageMediator paramMessageMediator)
  {
    this.serverRequest_1_1 = paramMessageMediator;
  }
  
  public MessageMediator serverRequest_1_1_Get()
  {
    return this.serverRequest_1_1;
  }
  
  public void serverRequest_1_1_Remove()
  {
    this.serverRequest_1_1 = null;
  }
  
  protected String getStateString(int paramInt)
  {
    synchronized (this.stateEvent)
    {
      switch (paramInt)
      {
      case 1: 
        return "OPENING";
      case 2: 
        return "ESTABLISHED";
      case 3: 
        return "CLOSE_SENT";
      case 4: 
        return "CLOSE_RECVD";
      case 5: 
        return "ABORT";
      }
      return "???";
    }
  }
  
  public synchronized boolean isPostInitialContexts()
  {
    return this.postInitialContexts;
  }
  
  public synchronized void setPostInitialContexts()
  {
    this.postInitialContexts = true;
  }
  
  public void purgeCalls(SystemException paramSystemException, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramSystemException.minor;
    try
    {
      if (this.orb.transportDebugFlag) {
        dprint(".purgeCalls->: " + i + "/" + paramBoolean1 + "/" + paramBoolean2 + " " + this);
      }
      synchronized (this.stateEvent)
      {
        if ((this.state == 5) || (this.state == 4))
        {
          if (this.orb.transportDebugFlag) {
            dprint(".purgeCalls: exiting since state is: " + getStateString(this.state) + " " + this);
          }
          return;
        }
      }
      try
      {
        if (!paramBoolean2) {
          writeLock();
        }
      }
      catch (SystemException localSystemException)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".purgeCalls: SystemException" + localSystemException + "; continuing " + this);
        }
      }
      synchronized (this.stateEvent)
      {
        if (i == 1398079697)
        {
          this.state = 4;
          paramSystemException.completed = CompletionStatus.COMPLETED_NO;
        }
        else
        {
          this.state = 5;
          paramSystemException.completed = CompletionStatus.COMPLETED_MAYBE;
        }
        this.stateEvent.notifyAll();
      }
      try
      {
        this.socket.getInputStream().close();
        this.socket.getOutputStream().close();
        this.socket.close();
      }
      catch (Exception localException)
      {
        if (this.orb.transportDebugFlag) {
          dprint(".purgeCalls: Exception closing socket: " + localException + " " + this);
        }
      }
      this.responseWaitingRoom.signalExceptionToAllWaiters(paramSystemException);
    }
    finally
    {
      if (this.contactInfo != null) {
        ((OutboundConnectionCache)getConnectionCache()).remove(this.contactInfo);
      } else if (this.acceptor != null) {
        ((InboundConnectionCache)getConnectionCache()).remove(this);
      }
      writeUnlock();
      if (this.orb.transportDebugFlag) {
        dprint(".purgeCalls<-: " + i + "/" + paramBoolean1 + "/" + paramBoolean2 + " " + this);
      }
    }
  }
  
  public void sendCloseConnection(GIOPVersion paramGIOPVersion)
    throws IOException
  {
    Message localMessage = MessageBase.createCloseConnection(paramGIOPVersion);
    sendHelper(paramGIOPVersion, localMessage);
  }
  
  public void sendMessageError(GIOPVersion paramGIOPVersion)
    throws IOException
  {
    Message localMessage = MessageBase.createMessageError(paramGIOPVersion);
    sendHelper(paramGIOPVersion, localMessage);
  }
  
  public void sendCancelRequest(GIOPVersion paramGIOPVersion, int paramInt)
    throws IOException
  {
    CancelRequestMessage localCancelRequestMessage = MessageBase.createCancelRequest(paramGIOPVersion, paramInt);
    sendHelper(paramGIOPVersion, localCancelRequestMessage);
  }
  
  protected void sendHelper(GIOPVersion paramGIOPVersion, Message paramMessage)
    throws IOException
  {
    CDROutputObject localCDROutputObject = OutputStreamFactory.newCDROutputObject(this.orb, null, paramGIOPVersion, this, paramMessage, (byte)1);
    paramMessage.write(localCDROutputObject);
    localCDROutputObject.writeTo(this);
  }
  
  public void sendCancelRequestWithLock(GIOPVersion paramGIOPVersion, int paramInt)
    throws IOException
  {
    writeLock();
    try
    {
      sendCancelRequest(paramGIOPVersion, paramInt);
      writeUnlock();
    }
    finally
    {
      writeUnlock();
    }
  }
  
  public final void setCodeBaseIOR(IOR paramIOR)
  {
    this.codeBaseServerIOR = paramIOR;
  }
  
  public final IOR getCodeBaseIOR()
  {
    return this.codeBaseServerIOR;
  }
  
  public final CodeBase getCodeBase()
  {
    return this.cachedCodeBase;
  }
  
  protected void setReadTimeouts(ReadTimeouts paramReadTimeouts)
  {
    this.readTimeouts = paramReadTimeouts;
  }
  
  protected void setPartialMessageMediator(CorbaMessageMediator paramCorbaMessageMediator)
  {
    this.partialMessageMediator = paramCorbaMessageMediator;
  }
  
  protected CorbaMessageMediator getPartialMessageMediator()
  {
    return this.partialMessageMediator;
  }
  
  public String toString()
  {
    synchronized (this.stateEvent)
    {
      return "SocketOrChannelConnectionImpl[ " + (this.socketChannel == null ? this.socket.toString() : this.socketChannel.toString()) + " " + getStateString(this.state) + " " + shouldUseSelectThreadToWait() + " " + shouldUseWorkerThreadForEvent() + " " + shouldReadGiopHeaderOnly() + "]";
    }
  }
  
  public void dprint(String paramString)
  {
    ORBUtility.dprint("SocketOrChannelConnectionImpl", paramString);
  }
  
  protected void dprint(String paramString, Throwable paramThrowable)
  {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
}
