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

@InstrumentationTarget("java.io.RandomAccessFile")
public final class RandomAccessFileRMHooks
{
  private FileDescriptor fd;
  private final String path = null;
  private final Object closeLock = new Object();
  private volatile boolean closed = false;
  
  public RandomAccessFileRMHooks() {}
  
  @InstrumentationMethod
  private void open(String paramString, int paramInt)
    throws FileNotFoundException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    ResourceRequest localResourceRequest1 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
    long l1 = 0L;
    long l2 = 0L;
    try
    {
      l2 = localResourceRequest1.request(1L, localResourceIdImpl);
      if (l2 < 1L) {
        throw new FileNotFoundException(paramString + ": resource limited: too many open files");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
    {
      FileNotFoundException localFileNotFoundException1 = new FileNotFoundException(paramString + ": resource limited: too many open files");
      localFileNotFoundException1.initCause(localResourceRequestDeniedException1);
      throw localFileNotFoundException1;
    }
    ResourceRequest localResourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
    int i = 0;
    try
    {
      try
      {
        l1 = localResourceRequest2.request(1L, localResourceIdImpl);
        if (l1 < 1L) {
          throw new FileNotFoundException(paramString + ": resource limited: too many open file descriptors");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
      {
        FileNotFoundException localFileNotFoundException2 = new FileNotFoundException(paramString + ": resource limited: too many open file descriptors");
        localFileNotFoundException2.initCause(localResourceRequestDeniedException2);
        throw localFileNotFoundException2;
      }
      open(paramString, paramInt);
      i = 1;
    }
    finally
    {
      localResourceRequest2.request(-(l1 - i), localResourceIdImpl);
      localResourceRequest1.request(-(l2 - i), localResourceIdImpl);
    }
  }
  
  @InstrumentationMethod
  public int read()
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
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
    int i = -1;
    try
    {
      i = read();
    }
    finally
    {
      localResourceRequest.request(-(l - (i == -1 ? 0 : 1)), localResourceIdImpl);
    }
    return i;
  }
  
  @InstrumentationMethod
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      return read(paramArrayOfByte);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    int i = paramArrayOfByte.length;
    long l = 0L;
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    int k;
    try
    {
      if (l < i)
      {
        localResourceRequest.request(-l, localResourceIdImpl);
        k = read(paramArrayOfByte, 0, paramArrayOfByte.length);
        j = Math.max(k, 0);
      }
      else
      {
        k = read(paramArrayOfByte);
        j = Math.max(k, 0);
      }
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
    return k;
  }
  
  @InstrumentationMethod
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0) {
      return read(paramArrayOfByte, paramInt1, paramInt2);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
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
    int j = 0;
    try
    {
      j = read(paramArrayOfByte, paramInt1, paramInt2);
      i = Math.max(j, 0);
    }
    finally
    {
      localResourceRequest.request(-(l - i), localResourceIdImpl);
    }
    return j;
  }
  
  @InstrumentationMethod
  public void write(int paramInt)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
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
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
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
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
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
  public final void writeBytes(String paramString)
    throws IOException
  {
    if (paramString == null)
    {
      writeBytes(paramString);
      return;
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    int i = paramString.length();
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
      writeBytes(paramString);
      j = i;
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
  }
  
  @InstrumentationMethod
  public final void writeChars(String paramString)
    throws IOException
  {
    if (paramString == null)
    {
      writeChars(paramString);
      return;
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(this.path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    int i = 2 * paramString.length();
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
      writeChars(paramString);
      j = i;
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
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
