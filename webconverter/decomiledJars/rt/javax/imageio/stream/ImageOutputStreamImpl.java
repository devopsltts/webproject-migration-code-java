package javax.imageio.stream;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteOrder;

public abstract class ImageOutputStreamImpl
  extends ImageInputStreamImpl
  implements ImageOutputStream
{
  public ImageOutputStreamImpl() {}
  
  public abstract void write(int paramInt)
    throws IOException;
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public abstract void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public void writeBoolean(boolean paramBoolean)
    throws IOException
  {
    write(paramBoolean ? 1 : 0);
  }
  
  public void writeByte(int paramInt)
    throws IOException
  {
    write(paramInt);
  }
  
  public void writeShort(int paramInt)
    throws IOException
  {
    if (this.byteOrder == ByteOrder.BIG_ENDIAN)
    {
      this.byteBuf[0] = ((byte)(paramInt >>> 8));
      this.byteBuf[1] = ((byte)(paramInt >>> 0));
    }
    else
    {
      this.byteBuf[0] = ((byte)(paramInt >>> 0));
      this.byteBuf[1] = ((byte)(paramInt >>> 8));
    }
    write(this.byteBuf, 0, 2);
  }
  
  public void writeChar(int paramInt)
    throws IOException
  {
    writeShort(paramInt);
  }
  
  public void writeInt(int paramInt)
    throws IOException
  {
    if (this.byteOrder == ByteOrder.BIG_ENDIAN)
    {
      this.byteBuf[0] = ((byte)(paramInt >>> 24));
      this.byteBuf[1] = ((byte)(paramInt >>> 16));
      this.byteBuf[2] = ((byte)(paramInt >>> 8));
      this.byteBuf[3] = ((byte)(paramInt >>> 0));
    }
    else
    {
      this.byteBuf[0] = ((byte)(paramInt >>> 0));
      this.byteBuf[1] = ((byte)(paramInt >>> 8));
      this.byteBuf[2] = ((byte)(paramInt >>> 16));
      this.byteBuf[3] = ((byte)(paramInt >>> 24));
    }
    write(this.byteBuf, 0, 4);
  }
  
  public void writeLong(long paramLong)
    throws IOException
  {
    if (this.byteOrder == ByteOrder.BIG_ENDIAN)
    {
      this.byteBuf[0] = ((byte)(int)(paramLong >>> 56));
      this.byteBuf[1] = ((byte)(int)(paramLong >>> 48));
      this.byteBuf[2] = ((byte)(int)(paramLong >>> 40));
      this.byteBuf[3] = ((byte)(int)(paramLong >>> 32));
      this.byteBuf[4] = ((byte)(int)(paramLong >>> 24));
      this.byteBuf[5] = ((byte)(int)(paramLong >>> 16));
      this.byteBuf[6] = ((byte)(int)(paramLong >>> 8));
      this.byteBuf[7] = ((byte)(int)(paramLong >>> 0));
    }
    else
    {
      this.byteBuf[0] = ((byte)(int)(paramLong >>> 0));
      this.byteBuf[1] = ((byte)(int)(paramLong >>> 8));
      this.byteBuf[2] = ((byte)(int)(paramLong >>> 16));
      this.byteBuf[3] = ((byte)(int)(paramLong >>> 24));
      this.byteBuf[4] = ((byte)(int)(paramLong >>> 32));
      this.byteBuf[5] = ((byte)(int)(paramLong >>> 40));
      this.byteBuf[6] = ((byte)(int)(paramLong >>> 48));
      this.byteBuf[7] = ((byte)(int)(paramLong >>> 56));
    }
    write(this.byteBuf, 0, 4);
    write(this.byteBuf, 4, 4);
  }
  
  public void writeFloat(float paramFloat)
    throws IOException
  {
    writeInt(Float.floatToIntBits(paramFloat));
  }
  
  public void writeDouble(double paramDouble)
    throws IOException
  {
    writeLong(Double.doubleToLongBits(paramDouble));
  }
  
  public void writeBytes(String paramString)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      write((byte)paramString.charAt(j));
    }
  }
  
  public void writeChars(String paramString)
    throws IOException
  {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i * 2];
    int j = 0;
    int k;
    int m;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (k = 0; k < i; k++)
      {
        m = paramString.charAt(k);
        arrayOfByte[(j++)] = ((byte)(m >>> 8));
        arrayOfByte[(j++)] = ((byte)(m >>> 0));
      }
    } else {
      for (k = 0; k < i; k++)
      {
        m = paramString.charAt(k);
        arrayOfByte[(j++)] = ((byte)(m >>> 0));
        arrayOfByte[(j++)] = ((byte)(m >>> 8));
      }
    }
    write(arrayOfByte, 0, i * 2);
  }
  
  public void writeUTF(String paramString)
    throws IOException
  {
    int i = paramString.length();
    int j = 0;
    char[] arrayOfChar = new char[i];
    int m = 0;
    paramString.getChars(0, i, arrayOfChar, 0);
    int k;
    for (int n = 0; n < i; n++)
    {
      k = arrayOfChar[n];
      if ((k >= 1) && (k <= 127)) {
        j++;
      } else if (k > 2047) {
        j += 3;
      } else {
        j += 2;
      }
    }
    if (j > 65535) {
      throw new UTFDataFormatException("utflen > 65536!");
    }
    byte[] arrayOfByte = new byte[j + 2];
    arrayOfByte[(m++)] = ((byte)(j >>> 8 & 0xFF));
    arrayOfByte[(m++)] = ((byte)(j >>> 0 & 0xFF));
    for (int i1 = 0; i1 < i; i1++)
    {
      k = arrayOfChar[i1];
      if ((k >= 1) && (k <= 127))
      {
        arrayOfByte[(m++)] = ((byte)k);
      }
      else if (k > 2047)
      {
        arrayOfByte[(m++)] = ((byte)(0xE0 | k >> 12 & 0xF));
        arrayOfByte[(m++)] = ((byte)(0x80 | k >> 6 & 0x3F));
        arrayOfByte[(m++)] = ((byte)(0x80 | k >> 0 & 0x3F));
      }
      else
      {
        arrayOfByte[(m++)] = ((byte)(0xC0 | k >> 6 & 0x1F));
        arrayOfByte[(m++)] = ((byte)(0x80 | k >> 0 & 0x3F));
      }
    }
    write(arrayOfByte, 0, j + 2);
  }
  
  public void writeShorts(short[] paramArrayOfShort, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfShort.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!");
    }
    byte[] arrayOfByte = new byte[paramInt2 * 2];
    int i = 0;
    int j;
    int k;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfShort[(paramInt1 + j)];
        arrayOfByte[(i++)] = ((byte)(k >>> 8));
        arrayOfByte[(i++)] = ((byte)(k >>> 0));
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfShort[(paramInt1 + j)];
        arrayOfByte[(i++)] = ((byte)(k >>> 0));
        arrayOfByte[(i++)] = ((byte)(k >>> 8));
      }
    }
    write(arrayOfByte, 0, paramInt2 * 2);
  }
  
  public void writeChars(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!");
    }
    byte[] arrayOfByte = new byte[paramInt2 * 2];
    int i = 0;
    int j;
    int k;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfChar[(paramInt1 + j)];
        arrayOfByte[(i++)] = ((byte)(k >>> 8));
        arrayOfByte[(i++)] = ((byte)(k >>> 0));
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfChar[(paramInt1 + j)];
        arrayOfByte[(i++)] = ((byte)(k >>> 0));
        arrayOfByte[(i++)] = ((byte)(k >>> 8));
      }
    }
    write(arrayOfByte, 0, paramInt2 * 2);
  }
  
  public void writeInts(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfInt.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!");
    }
    byte[] arrayOfByte = new byte[paramInt2 * 4];
    int i = 0;
    int j;
    int k;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfInt[(paramInt1 + j)];
        arrayOfByte[(i++)] = ((byte)(k >>> 24));
        arrayOfByte[(i++)] = ((byte)(k >>> 16));
        arrayOfByte[(i++)] = ((byte)(k >>> 8));
        arrayOfByte[(i++)] = ((byte)(k >>> 0));
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = paramArrayOfInt[(paramInt1 + j)];
        arrayOfByte[(i++)] = ((byte)(k >>> 0));
        arrayOfByte[(i++)] = ((byte)(k >>> 8));
        arrayOfByte[(i++)] = ((byte)(k >>> 16));
        arrayOfByte[(i++)] = ((byte)(k >>> 24));
      }
    }
    write(arrayOfByte, 0, paramInt2 * 4);
  }
  
  public void writeLongs(long[] paramArrayOfLong, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfLong.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!");
    }
    byte[] arrayOfByte = new byte[paramInt2 * 8];
    int i = 0;
    int j;
    long l;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        l = paramArrayOfLong[(paramInt1 + j)];
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 56));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 48));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 40));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 32));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 24));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 16));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 8));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 0));
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        l = paramArrayOfLong[(paramInt1 + j)];
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 0));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 8));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 16));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 24));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 32));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 40));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 48));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 56));
      }
    }
    write(arrayOfByte, 0, paramInt2 * 8);
  }
  
  public void writeFloats(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfFloat.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!");
    }
    byte[] arrayOfByte = new byte[paramInt2 * 4];
    int i = 0;
    int j;
    int k;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        k = Float.floatToIntBits(paramArrayOfFloat[(paramInt1 + j)]);
        arrayOfByte[(i++)] = ((byte)(k >>> 24));
        arrayOfByte[(i++)] = ((byte)(k >>> 16));
        arrayOfByte[(i++)] = ((byte)(k >>> 8));
        arrayOfByte[(i++)] = ((byte)(k >>> 0));
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        k = Float.floatToIntBits(paramArrayOfFloat[(paramInt1 + j)]);
        arrayOfByte[(i++)] = ((byte)(k >>> 0));
        arrayOfByte[(i++)] = ((byte)(k >>> 8));
        arrayOfByte[(i++)] = ((byte)(k >>> 16));
        arrayOfByte[(i++)] = ((byte)(k >>> 24));
      }
    }
    write(arrayOfByte, 0, paramInt2 * 4);
  }
  
  public void writeDoubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfDouble.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!");
    }
    byte[] arrayOfByte = new byte[paramInt2 * 8];
    int i = 0;
    int j;
    long l;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (j = 0; j < paramInt2; j++)
      {
        l = Double.doubleToLongBits(paramArrayOfDouble[(paramInt1 + j)]);
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 56));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 48));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 40));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 32));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 24));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 16));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 8));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 0));
      }
    } else {
      for (j = 0; j < paramInt2; j++)
      {
        l = Double.doubleToLongBits(paramArrayOfDouble[(paramInt1 + j)]);
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 0));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 8));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 16));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 24));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 32));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 40));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 48));
        arrayOfByte[(i++)] = ((byte)(int)(l >>> 56));
      }
    }
    write(arrayOfByte, 0, paramInt2 * 8);
  }
  
  public void writeBit(int paramInt)
    throws IOException
  {
    writeBits(1L & paramInt, 1);
  }
  
  public void writeBits(long paramLong, int paramInt)
    throws IOException
  {
    checkClosed();
    if ((paramInt < 0) || (paramInt > 64)) {
      throw new IllegalArgumentException("Bad value for numBits!");
    }
    if (paramInt == 0) {
      return;
    }
    int i;
    int j;
    int k;
    int m;
    if ((getStreamPosition() > 0L) || (this.bitOffset > 0))
    {
      i = this.bitOffset;
      j = read();
      if (j != -1) {
        seek(getStreamPosition() - 1L);
      } else {
        j = 0;
      }
      if (paramInt + i < 8)
      {
        k = 8 - (i + paramInt);
        m = -1 >>> 32 - paramInt;
        j &= (m << k ^ 0xFFFFFFFF);
        j = (int)(j | (paramLong & m) << k);
        write(j);
        seek(getStreamPosition() - 1L);
        this.bitOffset = (i + paramInt);
        paramInt = 0;
      }
      else
      {
        k = 8 - i;
        m = -1 >>> 32 - k;
        j &= (m ^ 0xFFFFFFFF);
        j = (int)(j | paramLong >> paramInt - k & m);
        write(j);
        paramInt -= k;
      }
    }
    if (paramInt > 7)
    {
      i = paramInt % 8;
      for (j = paramInt / 8; j > 0; j--)
      {
        k = (j - 1) * 8 + i;
        m = (int)(k == 0 ? paramLong & 0xFF : paramLong >> k & 0xFF);
        write(m);
      }
      paramInt = i;
    }
    if (paramInt != 0)
    {
      i = 0;
      i = read();
      if (i != -1) {
        seek(getStreamPosition() - 1L);
      } else {
        i = 0;
      }
      j = 8 - paramInt;
      k = -1 >>> 32 - paramInt;
      i &= (k << j ^ 0xFFFFFFFF);
      i = (int)(i | (paramLong & k) << j);
      write(i);
      seek(getStreamPosition() - 1L);
      this.bitOffset = paramInt;
    }
  }
  
  protected final void flushBits()
    throws IOException
  {
    checkClosed();
    if (this.bitOffset != 0)
    {
      int i = this.bitOffset;
      int j = read();
      if (j < 0)
      {
        j = 0;
        this.bitOffset = 0;
      }
      else
      {
        seek(getStreamPosition() - 1L);
        j &= -1 << 8 - i;
      }
      write(j);
    }
  }
}
