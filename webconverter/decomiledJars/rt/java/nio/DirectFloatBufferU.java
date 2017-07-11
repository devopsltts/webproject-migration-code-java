package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectFloatBufferU
  extends FloatBuffer
  implements DirectBuffer
{
  protected static final Unsafe unsafe = Bits.unsafe();
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset([F.class);
  protected static final boolean unaligned = Bits.unaligned();
  private final Object att;
  
  public Object attachment()
  {
    return this.att;
  }
  
  public Cleaner cleaner()
  {
    return null;
  }
  
  DirectFloatBufferU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = (paramDirectBuffer.address() + paramInt5);
    this.att = paramDirectBuffer;
  }
  
  public FloatBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = i << 2;
    assert (m >= 0);
    return new DirectFloatBufferU(this, -1, 0, k, k, m);
  }
  
  public FloatBuffer duplicate()
  {
    return new DirectFloatBufferU(this, markValue(), position(), limit(), capacity(), 0);
  }
  
  public FloatBuffer asReadOnlyBuffer()
  {
    return new DirectFloatBufferRU(this, markValue(), position(), limit(), capacity(), 0);
  }
  
  public long address()
  {
    return this.address;
  }
  
  private long ix(int paramInt)
  {
    return this.address + (paramInt << 2);
  }
  
  public float get()
  {
    return unsafe.getFloat(ix(nextGetIndex()));
  }
  
  public float get(int paramInt)
  {
    return unsafe.getFloat(ix(checkIndex(paramInt)));
  }
  
  public FloatBuffer get(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 << 2 > 6L)
    {
      checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
      int i = position();
      int j = limit();
      assert (i <= j);
      int k = i <= j ? j - i : 0;
      if (paramInt2 > k) {
        throw new BufferUnderflowException();
      }
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToIntArray(ix(i), paramArrayOfFloat, paramInt1 << 2, paramInt2 << 2);
      } else {
        Bits.copyToArray(ix(i), paramArrayOfFloat, arrayBaseOffset, paramInt1 << 2, paramInt2 << 2);
      }
      position(i + paramInt2);
    }
    else
    {
      super.get(paramArrayOfFloat, paramInt1, paramInt2);
    }
    return this;
  }
  
  public FloatBuffer put(float paramFloat)
  {
    unsafe.putFloat(ix(nextPutIndex()), paramFloat);
    return this;
  }
  
  public FloatBuffer put(int paramInt, float paramFloat)
  {
    unsafe.putFloat(ix(checkIndex(paramInt)), paramFloat);
    return this;
  }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer)
  {
    int j;
    int k;
    if ((paramFloatBuffer instanceof DirectFloatBufferU))
    {
      if (paramFloatBuffer == this) {
        throw new IllegalArgumentException();
      }
      DirectFloatBufferU localDirectFloatBufferU = (DirectFloatBufferU)paramFloatBuffer;
      j = localDirectFloatBufferU.position();
      k = localDirectFloatBufferU.limit();
      assert (j <= k);
      int m = j <= k ? k - j : 0;
      int n = position();
      int i1 = limit();
      assert (n <= i1);
      int i2 = n <= i1 ? i1 - n : 0;
      if (m > i2) {
        throw new BufferOverflowException();
      }
      unsafe.copyMemory(localDirectFloatBufferU.ix(j), ix(n), m << 2);
      localDirectFloatBufferU.position(j + m);
      position(n + m);
    }
    else if (paramFloatBuffer.hb != null)
    {
      int i = paramFloatBuffer.position();
      j = paramFloatBuffer.limit();
      assert (i <= j);
      k = i <= j ? j - i : 0;
      put(paramFloatBuffer.hb, paramFloatBuffer.offset + i, k);
      paramFloatBuffer.position(i + k);
    }
    else
    {
      super.put(paramFloatBuffer);
    }
    return this;
  }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 << 2 > 6L)
    {
      checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
      int i = position();
      int j = limit();
      assert (i <= j);
      int k = i <= j ? j - i : 0;
      if (paramInt2 > k) {
        throw new BufferOverflowException();
      }
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromIntArray(paramArrayOfFloat, paramInt1 << 2, ix(i), paramInt2 << 2);
      } else {
        Bits.copyFromArray(paramArrayOfFloat, arrayBaseOffset, paramInt1 << 2, ix(i), paramInt2 << 2);
      }
      position(i + paramInt2);
    }
    else
    {
      super.put(paramArrayOfFloat, paramInt1, paramInt2);
    }
    return this;
  }
  
  public FloatBuffer compact()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    unsafe.copyMemory(ix(i), ix(0), k << 2);
    position(k);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect()
  {
    return true;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
  }
}
