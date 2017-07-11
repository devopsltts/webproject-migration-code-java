package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.nio.channels.spi.AbstractInterruptibleChannel")
public final class AbstractInterruptibleChannelRMHooks
{
  private final Object closeLock = new Object();
  private volatile boolean open = true;
  
  public AbstractInterruptibleChannelRMHooks() {}
  
  @InstrumentationMethod
  public final void close()
    throws IOException
  {
    synchronized (this.closeLock)
    {
      if (!this.open) {
        return;
      }
    }
    ??? = null;
    SocketAddress localSocketAddress = null;
    Object localObject2;
    if (DatagramChannel.class.isInstance(this))
    {
      localObject2 = (DatagramChannel)this;
      localSocketAddress = ((DatagramChannel)localObject2).getLocalAddress();
      ??? = ResourceIdImpl.of(localSocketAddress);
    }
    else if (SocketChannel.class.isInstance(this))
    {
      localObject2 = (SocketChannel)this;
      localSocketAddress = ((SocketChannel)localObject2).getLocalAddress();
      ??? = ResourceIdImpl.of(localSocketAddress);
    }
    else if (ServerSocketChannel.class.isInstance(this))
    {
      localObject2 = (ServerSocketChannel)this;
      localSocketAddress = ((ServerSocketChannel)localObject2).getLocalAddress();
      ??? = ResourceIdImpl.of(localSocketAddress);
    }
    try
    {
      close();
    }
    finally
    {
      if (localSocketAddress != null)
      {
        ResourceRequest localResourceRequest;
        if (DatagramChannel.class.isInstance(this))
        {
          localResourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
          localResourceRequest.request(-1L, (ResourceId)???);
        }
        else if ((SocketChannel.class.isInstance(this)) || (ServerSocketChannel.class.isInstance(this)))
        {
          localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
          localResourceRequest.request(-1L, (ResourceId)???);
        }
      }
    }
  }
}
