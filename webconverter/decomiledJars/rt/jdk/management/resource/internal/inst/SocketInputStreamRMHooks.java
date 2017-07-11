package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.SocketInputStream")
@TypeMapping(from="jdk.management.resource.internal.inst.SocketInputStreamRMHooks$AbstractPlainSocketImpl", to="java.net.AbstractPlainSocketImpl")
public final class SocketInputStreamRMHooks
{
  private AbstractPlainSocketImpl impl = null;
  
  public SocketInputStreamRMHooks() {}
  
  @InstrumentationMethod
  private int socketRead(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if (paramInt2 < 0) {
      return socketRead(paramFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(Integer.valueOf(this.impl.localport));
    ResourceRequest localResourceRequest = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
    long l = 0L;
    try
    {
      l = Math.max(localResourceRequest.request(paramInt2, localResourceIdImpl), 0L);
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    paramInt2 = Math.min(paramInt2, (int)l);
    int i = 0;
    int j;
    try
    {
      j = socketRead(paramFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
      i = Math.max(j, 0);
    }
    finally
    {
      localResourceRequest.request(-(l - i), localResourceIdImpl);
    }
    return j;
  }
  
  static class AbstractPlainSocketImpl
  {
    protected int localport;
    
    AbstractPlainSocketImpl() {}
  }
}
