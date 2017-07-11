package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.Socket;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.SSLServerSocketImpl")
final class SSLServerSocketImplRMHooks
{
  SSLServerSocketImplRMHooks() {}
  
  @InstrumentationMethod
  public Socket accept()
    throws IOException
  {
    long l1 = 0L;
    long l2 = 0L;
    Socket localSocket = null;
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    try
    {
      localSocket = accept();
      l2 = 1L;
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(localSocket);
      localResourceIdImpl = ResourceIdImpl.of(localSocket.getLocalAddress());
      try
      {
        l1 = localResourceRequest.request(1L, localResourceIdImpl);
        if (l1 < 1L)
        {
          try
          {
            localSocket.close();
          }
          catch (IOException localIOException1) {}
          throw new IOException("Resource limited: too many open sockets");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        try
        {
          localSocket.close();
        }
        catch (IOException localIOException2) {}
        throw new IOException("Resource limited: too many open sockets", localResourceRequestDeniedException);
      }
      l2 = 1L;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
      }
    }
    return localSocket;
  }
}
