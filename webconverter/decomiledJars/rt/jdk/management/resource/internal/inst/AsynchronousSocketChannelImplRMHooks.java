package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.AsynchronousSocketChannelImpl")
public class AsynchronousSocketChannelImplRMHooks
{
  public AsynchronousSocketChannelImplRMHooks() {}
  
  @InstrumentationMethod
  public final SocketAddress getLocalAddress()
    throws IOException
  {
    return getLocalAddress();
  }
  
  @InstrumentationMethod
  public final AsynchronousSocketChannel bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (getLocalAddress() == null)
    {
      localResourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new IOException("Resource limited: too many open socket channels");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        throw new IOException("Resource limited: too many open socket channels", localResourceRequestDeniedException);
      }
    }
    int i = 0;
    AsynchronousSocketChannel localAsynchronousSocketChannel = null;
    try
    {
      localAsynchronousSocketChannel = bind(paramSocketAddress);
      i = 1;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
    return localAsynchronousSocketChannel;
  }
}
