package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.WindowsAsynchronousServerSocketChannelImpl")
public class WindowsAsynchronousServerSocketChannelImplRMHooks
{
  protected final FileDescriptor fd = null;
  protected volatile InetSocketAddress localAddress = null;
  
  public WindowsAsynchronousServerSocketChannelImplRMHooks() {}
  
  @InstrumentationMethod
  void implClose()
    throws IOException
  {
    try
    {
      implClose();
      ResourceIdImpl localResourceIdImpl1 = ResourceIdImpl.of(this.fd);
      ResourceRequest localResourceRequest1;
      if (localResourceIdImpl1 != null)
      {
        localResourceRequest1 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
        localResourceRequest1.request(-1L, localResourceIdImpl1);
      }
      if (this.localAddress != null)
      {
        localResourceIdImpl1 = ResourceIdImpl.of(this.localAddress);
        localResourceRequest1 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
        localResourceRequest1.request(-1L, localResourceIdImpl1);
      }
    }
    finally
    {
      ResourceIdImpl localResourceIdImpl2 = ResourceIdImpl.of(this.fd);
      ResourceRequest localResourceRequest2;
      if (localResourceIdImpl2 != null)
      {
        localResourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
        localResourceRequest2.request(-1L, localResourceIdImpl2);
      }
      if (this.localAddress != null)
      {
        localResourceIdImpl2 = ResourceIdImpl.of(this.localAddress);
        localResourceRequest2 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
        localResourceRequest2.request(-1L, localResourceIdImpl2);
      }
    }
  }
}
