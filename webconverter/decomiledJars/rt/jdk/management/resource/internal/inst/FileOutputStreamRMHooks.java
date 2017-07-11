package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.io.FileOutputStream")
public final class FileOutputStreamRMHooks
{
  private final FileDescriptor fd = null;
  private final String path = null;
  private final Object closeLock = new Object();
  private volatile boolean closed = false;
  
  public FileOutputStreamRMHooks() {}
  
  @InstrumentationMethod
  private void open(String paramString, boolean paramBoolean)
    throws FileNotFoundException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    ResourceRequest localResourceRequest1 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
    long l1 = 0L;
    try
    {
      l1 = localResourceRequest1.request(1L, localResourceIdImpl);
      if (l1 < 1L) {
        throw new FileNotFoundException(paramString + ": resource limited: too many open files");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
    {
      FileNotFoundException localFileNotFoundException1 = new FileNotFoundException(paramString + ": resource limited: too many open files");
      localFileNotFoundException1.initCause(localResourceRequestDeniedException1);
      throw localFileNotFoundException1;
    }
    ResourceRequest localResourceRequest2 = null;
    long l2 = 0L;
    int i = 0;
    try
    {
      FileDescriptor localFileDescriptor = null;
      try
      {
        localFileDescriptor = getFD();
      }
      catch (IOException localIOException) {}
      localResourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(localFileDescriptor);
      try
      {
        l2 = localResourceRequest2.request(1L, localResourceIdImpl);
        if (l2 < 1L) {
          throw new FileNotFoundException(paramString + ": resource limited: too many open file descriptors");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
      {
        FileNotFoundException localFileNotFoundException2 = new FileNotFoundException(paramString + ": resource limited: too many open file descriptors");
        localFileNotFoundException2.initCause(localResourceRequestDeniedException2);
        throw localFileNotFoundException2;
      }
      open(paramString, paramBoolean);
      i = 1;
    }
    finally
    {
      localResourceRequest2.request(-(l2 - i), localResourceIdImpl);
      localResourceRequest1.request(-(l1 - i), localResourceIdImpl);
    }
  }
  
  @InstrumentationMethod
  public final FileDescriptor getFD()
    throws IOException
  {
    return getFD();
  }
  
  @InstrumentationMethod
  public void write(int paramInt)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    FileDescriptor localFileDescriptor = getFD();
    ResourceRequest localResourceRequest;
    if (localFileDescriptor == FileDescriptor.err) {
      localResourceRequest = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
    } else if (localFileDescriptor == FileDescriptor.out) {
      localResourceRequest = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
    } else {
      localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    }
    long l = 0L;
    try
    {
      l = localResourceRequest.request(1L, localResourceIdImpl);
      if (l < 1L) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int i = 0;
    try
    {
      write(paramInt);
      i = 1;
    }
    finally
    {
      localResourceRequest.request(-(l - i), localResourceIdImpl);
    }
  }
  
  @InstrumentationMethod
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null)
    {
      write(paramArrayOfByte);
      return;
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    FileDescriptor localFileDescriptor = getFD();
    ResourceRequest localResourceRequest;
    if (localFileDescriptor == FileDescriptor.err) {
      localResourceRequest = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
    } else if (localFileDescriptor == FileDescriptor.out) {
      localResourceRequest = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
    } else {
      localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    }
    int i = paramArrayOfByte.length;
    long l = 0L;
    try
    {
      l = localResourceRequest.request(i, localResourceIdImpl);
      if (l < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    try
    {
      write(paramArrayOfByte);
      j = i;
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
  }
  
  @InstrumentationMethod
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0)
    {
      write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    FileDescriptor localFileDescriptor = getFD();
    ResourceRequest localResourceRequest;
    if (localFileDescriptor == FileDescriptor.err) {
      localResourceRequest = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
    } else if (localFileDescriptor == FileDescriptor.out) {
      localResourceRequest = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
    } else {
      localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    }
    long l = 0L;
    try
    {
      l = localResourceRequest.request(paramInt2, localResourceIdImpl);
      if (l < paramInt2) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int i = 0;
    try
    {
      write(paramArrayOfByte, paramInt1, paramInt2);
      i = paramInt2;
    }
    finally
    {
      localResourceRequest.request(-(l - i), localResourceIdImpl);
    }
  }
  
  @InstrumentationMethod
  public void close()
    throws IOException
  {
    synchronized (this.closeLock)
    {
      if (this.closed) {
        return;
      }
    }
    JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    long l1;
    try
    {
      l1 = localJavaIOFileDescriptorAccess.getHandle(this.fd);
      if (l1 == -1L) {
        l1 = localJavaIOFileDescriptorAccess.get(this.fd);
      }
    }
    catch (UnsupportedOperationException localUnsupportedOperationException1)
    {
      l1 = localJavaIOFileDescriptorAccess.get(this.fd);
    }
    try
    {
      close();
    }
    finally
    {
      long l3;
      try
      {
        long l2;
        ResourceIdImpl localResourceIdImpl1;
        ResourceRequest localResourceRequest1;
        l3 = localJavaIOFileDescriptorAccess.getHandle(this.fd);
        if (l3 == -1L) {
          l3 = localJavaIOFileDescriptorAccess.get(this.fd);
        }
      }
      catch (UnsupportedOperationException localUnsupportedOperationException3)
      {
        l3 = localJavaIOFileDescriptorAccess.get(this.fd);
      }
      ResourceIdImpl localResourceIdImpl2 = ResourceIdImpl.of(this.path);
      if (l3 != l1)
      {
        localResourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
        localResourceRequest2.request(-1L, localResourceIdImpl2);
      }
      ResourceRequest localResourceRequest2 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      localResourceRequest2.request(-1L, localResourceIdImpl2);
    }
  }
}
