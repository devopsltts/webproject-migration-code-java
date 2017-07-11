package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.ServerSocketChannelImpl")
public final class ServerSocketChannelImplRMHooks
{
  private static NativeDispatcher nd;
  
  public ServerSocketChannelImplRMHooks() {}
  
  @InstrumentationMethod
  public SocketAddress getLocalAddress()
    throws IOException
  {
    return getLocalAddress();
  }
  
  @InstrumentationMethod
  public SocketChannel accept()
    throws IOException
  {
    long l1 = 0L;
    long l2 = 0L;
    SocketChannel localSocketChannel = null;
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    try
    {
      localSocketChannel = accept();
      if (localSocketChannel != null)
      {
        localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(localSocketChannel);
        localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
        try
        {
          l1 = localResourceRequest.request(1L, localResourceIdImpl);
          if (l1 < 1L)
          {
            try
            {
              localSocketChannel.close();
            }
            catch (IOException localIOException1) {}
            throw new IOException("Resource limited: too many open socket channels");
          }
        }
        catch (ResourceRequestDeniedException localResourceRequestDeniedException)
        {
          try
          {
            localSocketChannel.close();
          }
          catch (IOException localIOException2) {}
          throw new IOException("Resource limited: too many open socket channels", localResourceRequestDeniedException);
        }
        l2 = 1L;
      }
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
      }
    }
    return localSocketChannel;
  }
  
  public final void close()
    throws IOException
  {}
  
  @InstrumentationMethod
  private int accept(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
    throws IOException
  {
    int i = accept(paramFileDescriptor1, paramFileDescriptor2, paramArrayOfInetSocketAddress);
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(paramFileDescriptor2);
    if (localResourceIdImpl != null)
    {
      ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor2);
      long l1 = 0L;
      long l2 = 0L;
      try
      {
        try
        {
          l1 = localResourceRequest.request(1L, localResourceIdImpl);
          if (l1 < 1L) {
            throw new IOException("Resource limited: too many open file descriptors");
          }
        }
        catch (ResourceRequestDeniedException localResourceRequestDeniedException)
        {
          throw new IOException("Resource limited: too many open file descriptors", localResourceRequestDeniedException);
        }
        l2 = 1L;
      }
      finally
      {
        if (l2 == 0L) {
          try
          {
            nd.close(paramFileDescriptor2);
          }
          catch (IOException localIOException2) {}
        } else {
          localResourceRequest.request(-(l1 - 1L), localResourceIdImpl);
        }
      }
    }
    return i;
  }
  
  @InstrumentationMethod
  public ServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (getLocalAddress() == null)
    {
      localResourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      l = localResourceRequest.request(1L, localResourceIdImpl);
      if (l < 1L) {
        throw new ResourceRequestDeniedException("Resource limited: too many open socket channels");
      }
    }
    int i = 0;
    ServerSocketChannel localServerSocketChannel = null;
    try
    {
      localServerSocketChannel = bind(paramSocketAddress, paramInt);
      i = 1;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
    return localServerSocketChannel;
  }
  
  abstract class NativeDispatcher
  {
    NativeDispatcher() {}
    
    abstract void close(FileDescriptor paramFileDescriptor)
      throws IOException;
  }
}
