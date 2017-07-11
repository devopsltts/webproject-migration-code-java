package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class IOUtil
{
  static final int IOV_MAX = iovMax();
  
  private IOUtil() {}
  
  static int write(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher)
    throws IOException
  {
    if ((paramByteBuffer instanceof DirectBuffer)) {
      return writeFromNativeBuffer(paramFileDescriptor, paramByteBuffer, paramLong, paramNativeDispatcher);
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
      int m = writeFromNativeBuffer(paramFileDescriptor, localByteBuffer, paramLong, paramNativeDispatcher);
      if (m > 0) {
        paramByteBuffer.position(i + m);
      }
      int n = m;
      return n;
    }
    finally
    {
      Util.offerFirstTemporaryDirectBuffer(localByteBuffer);
    }
  }
  
  private static int writeFromNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher)
    throws IOException
  {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = 0;
    if (k == 0) {
      return 0;
    }
    if (paramLong != -1L) {
      m = paramNativeDispatcher.pwrite(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k, paramLong);
    } else {
      m = paramNativeDispatcher.write(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k);
    }
    if (m > 0) {
      paramByteBuffer.position(i + m);
    }
    return m;
  }
  
  static long write(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, NativeDispatcher paramNativeDispatcher)
    throws IOException
  {
    return write(paramFileDescriptor, paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length, paramNativeDispatcher);
  }
  
  static long write(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, NativeDispatcher paramNativeDispatcher)
    throws IOException
  {
    IOVecWrapper localIOVecWrapper = IOVecWrapper.get(paramInt2);
    int i = 0;
    int j = 0;
    try
    {
      int k = paramInt1 + paramInt2;
      int i1;
      for (int m = paramInt1; (m < k) && (j < IOV_MAX); m++)
      {
        Object localObject1 = paramArrayOfByteBuffer[m];
        int n = ((ByteBuffer)localObject1).position();
        i1 = ((ByteBuffer)localObject1).limit();
        assert (n <= i1);
        int i2 = n <= i1 ? i1 - n : 0;
        if (i2 > 0)
        {
          localIOVecWrapper.setBuffer(j, (ByteBuffer)localObject1, n, i2);
          if (!(localObject1 instanceof DirectBuffer))
          {
            ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(i2);
            localByteBuffer2.put((ByteBuffer)localObject1);
            localByteBuffer2.flip();
            localIOVecWrapper.setShadow(j, localByteBuffer2);
            ((ByteBuffer)localObject1).position(n);
            localObject1 = localByteBuffer2;
            n = localByteBuffer2.position();
          }
          localIOVecWrapper.putBase(j, ((DirectBuffer)localObject1).address() + n);
          localIOVecWrapper.putLen(j, i2);
          j++;
        }
      }
      if (j == 0)
      {
        l1 = 0L;
        ByteBuffer localByteBuffer1;
        return l1;
      }
      long l1 = paramNativeDispatcher.writev(paramFileDescriptor, localIOVecWrapper.address, j);
      long l2 = l1;
      int i4;
      for (int i3 = 0; i3 < j; i3++)
      {
        if (l2 > 0L)
        {
          localByteBuffer3 = localIOVecWrapper.getBuffer(i3);
          i4 = localIOVecWrapper.getPosition(i3);
          int i5 = localIOVecWrapper.getRemaining(i3);
          int i6 = l2 > i5 ? i5 : (int)l2;
          localByteBuffer3.position(i4 + i6);
          l2 -= i6;
        }
        ByteBuffer localByteBuffer3 = localIOVecWrapper.getShadow(i3);
        if (localByteBuffer3 != null) {
          Util.offerLastTemporaryDirectBuffer(localByteBuffer3);
        }
        localIOVecWrapper.clearRefs(i3);
      }
      i = 1;
      long l3 = l1;
      ByteBuffer localByteBuffer4;
      return l3;
    }
    finally
    {
      if (i == 0) {
        for (int i7 = 0; i7 < j; i7++)
        {
          ByteBuffer localByteBuffer5 = localIOVecWrapper.getShadow(i7);
          if (localByteBuffer5 != null) {
            Util.offerLastTemporaryDirectBuffer(localByteBuffer5);
          }
          localIOVecWrapper.clearRefs(i7);
        }
      }
    }
  }
  
  static int read(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher)
    throws IOException
  {
    if (paramByteBuffer.isReadOnly()) {
      throw new IllegalArgumentException("Read-only buffer");
    }
    if ((paramByteBuffer instanceof DirectBuffer)) {
      return readIntoNativeBuffer(paramFileDescriptor, paramByteBuffer, paramLong, paramNativeDispatcher);
    }
    ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(paramByteBuffer.remaining());
    try
    {
      int i = readIntoNativeBuffer(paramFileDescriptor, localByteBuffer, paramLong, paramNativeDispatcher);
      localByteBuffer.flip();
      if (i > 0) {
        paramByteBuffer.put(localByteBuffer);
      }
      int j = i;
      return j;
    }
    finally
    {
      Util.offerFirstTemporaryDirectBuffer(localByteBuffer);
    }
  }
  
  private static int readIntoNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher)
    throws IOException
  {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    if (k == 0) {
      return 0;
    }
    int m = 0;
    if (paramLong != -1L) {
      m = paramNativeDispatcher.pread(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k, paramLong);
    } else {
      m = paramNativeDispatcher.read(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k);
    }
    if (m > 0) {
      paramByteBuffer.position(i + m);
    }
    return m;
  }
  
  static long read(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, NativeDispatcher paramNativeDispatcher)
    throws IOException
  {
    return read(paramFileDescriptor, paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length, paramNativeDispatcher);
  }
  
  static long read(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, NativeDispatcher paramNativeDispatcher)
    throws IOException
  {
    IOVecWrapper localIOVecWrapper = IOVecWrapper.get(paramInt2);
    int i = 0;
    int j = 0;
    try
    {
      int k = paramInt1 + paramInt2;
      int i1;
      for (int m = paramInt1; (m < k) && (j < IOV_MAX); m++)
      {
        Object localObject1 = paramArrayOfByteBuffer[m];
        if (((ByteBuffer)localObject1).isReadOnly()) {
          throw new IllegalArgumentException("Read-only buffer");
        }
        int n = ((ByteBuffer)localObject1).position();
        i1 = ((ByteBuffer)localObject1).limit();
        assert (n <= i1);
        int i2 = n <= i1 ? i1 - n : 0;
        if (i2 > 0)
        {
          localIOVecWrapper.setBuffer(j, (ByteBuffer)localObject1, n, i2);
          if (!(localObject1 instanceof DirectBuffer))
          {
            ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(i2);
            localIOVecWrapper.setShadow(j, localByteBuffer2);
            localObject1 = localByteBuffer2;
            n = localByteBuffer2.position();
          }
          localIOVecWrapper.putBase(j, ((DirectBuffer)localObject1).address() + n);
          localIOVecWrapper.putLen(j, i2);
          j++;
        }
      }
      if (j == 0)
      {
        l1 = 0L;
        ByteBuffer localByteBuffer1;
        return l1;
      }
      long l1 = paramNativeDispatcher.readv(paramFileDescriptor, localIOVecWrapper.address, j);
      long l2 = l1;
      for (int i3 = 0; i3 < j; i3++)
      {
        ByteBuffer localByteBuffer3 = localIOVecWrapper.getShadow(i3);
        if (l2 > 0L)
        {
          ByteBuffer localByteBuffer4 = localIOVecWrapper.getBuffer(i3);
          int i5 = localIOVecWrapper.getRemaining(i3);
          int i6 = l2 > i5 ? i5 : (int)l2;
          if (localByteBuffer3 == null)
          {
            int i7 = localIOVecWrapper.getPosition(i3);
            localByteBuffer4.position(i7 + i6);
          }
          else
          {
            localByteBuffer3.limit(localByteBuffer3.position() + i6);
            localByteBuffer4.put(localByteBuffer3);
          }
          l2 -= i6;
        }
        if (localByteBuffer3 != null) {
          Util.offerLastTemporaryDirectBuffer(localByteBuffer3);
        }
        localIOVecWrapper.clearRefs(i3);
      }
      i = 1;
      long l3 = l1;
      int i4;
      ByteBuffer localByteBuffer5;
      return l3;
    }
    finally
    {
      if (i == 0) {
        for (int i8 = 0; i8 < j; i8++)
        {
          ByteBuffer localByteBuffer6 = localIOVecWrapper.getShadow(i8);
          if (localByteBuffer6 != null) {
            Util.offerLastTemporaryDirectBuffer(localByteBuffer6);
          }
          localIOVecWrapper.clearRefs(i8);
        }
      }
    }
  }
  
  public static FileDescriptor newFD(int paramInt)
  {
    FileDescriptor localFileDescriptor = new FileDescriptor();
    setfdVal(localFileDescriptor, paramInt);
    return localFileDescriptor;
  }
  
  static native boolean randomBytes(byte[] paramArrayOfByte);
  
  static native long makePipe(boolean paramBoolean);
  
  static native boolean drain(int paramInt)
    throws IOException;
  
  public static native void configureBlocking(FileDescriptor paramFileDescriptor, boolean paramBoolean)
    throws IOException;
  
  public static native int fdVal(FileDescriptor paramFileDescriptor);
  
  static native void setfdVal(FileDescriptor paramFileDescriptor, int paramInt);
  
  static native int fdLimit();
  
  static native int iovMax();
  
  static native void initIDs();
  
  public static void load() {}
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        System.loadLibrary("nio");
        return null;
      }
    });
    initIDs();
  }
}
