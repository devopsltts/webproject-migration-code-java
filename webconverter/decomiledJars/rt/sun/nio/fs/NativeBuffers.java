package sun.nio.fs;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

class NativeBuffers
{
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  private static final int TEMP_BUF_POOL_SIZE = 3;
  private static ThreadLocal<NativeBuffer[]> threadLocal = new ThreadLocal();
  
  private NativeBuffers() {}
  
  static NativeBuffer allocNativeBuffer(int paramInt)
  {
    if (paramInt < 2048) {
      paramInt = 2048;
    }
    return new NativeBuffer(paramInt);
  }
  
  static NativeBuffer getNativeBufferFromCache(int paramInt)
  {
    NativeBuffer[] arrayOfNativeBuffer = (NativeBuffer[])threadLocal.get();
    if (arrayOfNativeBuffer != null) {
      for (int i = 0; i < 3; i++)
      {
        NativeBuffer localNativeBuffer = arrayOfNativeBuffer[i];
        if ((localNativeBuffer != null) && (localNativeBuffer.size() >= paramInt))
        {
          arrayOfNativeBuffer[i] = null;
          return localNativeBuffer;
        }
      }
    }
    return null;
  }
  
  static NativeBuffer getNativeBuffer(int paramInt)
  {
    NativeBuffer localNativeBuffer = getNativeBufferFromCache(paramInt);
    if (localNativeBuffer != null)
    {
      localNativeBuffer.setOwner(null);
      return localNativeBuffer;
    }
    return allocNativeBuffer(paramInt);
  }
  
  static void releaseNativeBuffer(NativeBuffer paramNativeBuffer)
  {
    NativeBuffer[] arrayOfNativeBuffer = (NativeBuffer[])threadLocal.get();
    if (arrayOfNativeBuffer == null)
    {
      arrayOfNativeBuffer = new NativeBuffer[3];
      arrayOfNativeBuffer[0] = paramNativeBuffer;
      threadLocal.set(arrayOfNativeBuffer);
      return;
    }
    for (int i = 0; i < 3; i++) {
      if (arrayOfNativeBuffer[i] == null)
      {
        arrayOfNativeBuffer[i] = paramNativeBuffer;
        return;
      }
    }
    for (i = 0; i < 3; i++)
    {
      NativeBuffer localNativeBuffer = arrayOfNativeBuffer[i];
      if (localNativeBuffer.size() < paramNativeBuffer.size())
      {
        localNativeBuffer.cleaner().clean();
        arrayOfNativeBuffer[i] = paramNativeBuffer;
        return;
      }
    }
    paramNativeBuffer.cleaner().clean();
  }
  
  static void copyCStringToNativeBuffer(byte[] paramArrayOfByte, NativeBuffer paramNativeBuffer)
  {
    long l1 = Unsafe.ARRAY_BYTE_BASE_OFFSET;
    long l2 = paramArrayOfByte.length;
    assert (paramNativeBuffer.size() >= l2 + 1L);
    unsafe.copyMemory(paramArrayOfByte, l1, null, paramNativeBuffer.address(), l2);
    unsafe.putByte(paramNativeBuffer.address() + l2, (byte)0);
  }
  
  static NativeBuffer asNativeBuffer(byte[] paramArrayOfByte)
  {
    NativeBuffer localNativeBuffer = getNativeBuffer(paramArrayOfByte.length + 1);
    copyCStringToNativeBuffer(paramArrayOfByte, localNativeBuffer);
    return localNativeBuffer;
  }
}
