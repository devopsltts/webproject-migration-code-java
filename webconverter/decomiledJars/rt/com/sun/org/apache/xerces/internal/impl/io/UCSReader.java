package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;

public class UCSReader
  extends Reader
{
  public static final int DEFAULT_BUFFER_SIZE = 8192;
  public static final short UCS2LE = 1;
  public static final short UCS2BE = 2;
  public static final short UCS4LE = 4;
  public static final short UCS4BE = 8;
  protected InputStream fInputStream;
  protected byte[] fBuffer;
  protected short fEncoding;
  
  public UCSReader(InputStream paramInputStream, short paramShort)
  {
    this(paramInputStream, 8192, paramShort);
  }
  
  public UCSReader(InputStream paramInputStream, int paramInt, short paramShort)
  {
    this.fInputStream = paramInputStream;
    BufferAllocator localBufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    this.fBuffer = localBufferAllocator.getByteBuffer(paramInt);
    if (this.fBuffer == null) {
      this.fBuffer = new byte[paramInt];
    }
    this.fEncoding = paramShort;
  }
  
  public int read()
    throws IOException
  {
    int i = this.fInputStream.read() & 0xFF;
    if (i == 255) {
      return -1;
    }
    int j = this.fInputStream.read() & 0xFF;
    if (j == 255) {
      return -1;
    }
    if (this.fEncoding >= 4)
    {
      int k = this.fInputStream.read() & 0xFF;
      if (k == 255) {
        return -1;
      }
      int m = this.fInputStream.read() & 0xFF;
      if (m == 255) {
        return -1;
      }
      System.err.println("b0 is " + (i & 0xFF) + " b1 " + (j & 0xFF) + " b2 " + (k & 0xFF) + " b3 " + (m & 0xFF));
      if (this.fEncoding == 8) {
        return (i << 24) + (j << 16) + (k << 8) + m;
      }
      return (m << 24) + (k << 16) + (j << 8) + i;
    }
    if (this.fEncoding == 2) {
      return (i << 8) + j;
    }
    return (j << 8) + i;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt2 << (this.fEncoding >= 4 ? 2 : 1);
    if (i > this.fBuffer.length) {
      i = this.fBuffer.length;
    }
    int j = this.fInputStream.read(this.fBuffer, 0, i);
    if (j == -1) {
      return -1;
    }
    int i1;
    if (this.fEncoding >= 4)
    {
      k = 4 - (j & 0x3) & 0x3;
      for (m = 0; m < k; m++)
      {
        n = this.fInputStream.read();
        if (n == -1)
        {
          for (i1 = m; i1 < k; i1++) {
            this.fBuffer[(j + i1)] = 0;
          }
          break;
        }
        this.fBuffer[(j + m)] = ((byte)n);
      }
      j += k;
    }
    else
    {
      k = j & 0x1;
      if (k != 0)
      {
        j++;
        m = this.fInputStream.read();
        if (m == -1) {
          this.fBuffer[j] = 0;
        } else {
          this.fBuffer[j] = ((byte)m);
        }
      }
    }
    int k = j >> (this.fEncoding >= 4 ? 2 : 1);
    int m = 0;
    for (int n = 0; n < k; n++)
    {
      i1 = this.fBuffer[(m++)] & 0xFF;
      int i2 = this.fBuffer[(m++)] & 0xFF;
      if (this.fEncoding >= 4)
      {
        int i3 = this.fBuffer[(m++)] & 0xFF;
        int i4 = this.fBuffer[(m++)] & 0xFF;
        if (this.fEncoding == 8) {
          paramArrayOfChar[(paramInt1 + n)] = ((char)((i1 << 24) + (i2 << 16) + (i3 << 8) + i4));
        } else {
          paramArrayOfChar[(paramInt1 + n)] = ((char)((i4 << 24) + (i3 << 16) + (i2 << 8) + i1));
        }
      }
      else if (this.fEncoding == 2)
      {
        paramArrayOfChar[(paramInt1 + n)] = ((char)((i1 << 8) + i2));
      }
      else
      {
        paramArrayOfChar[(paramInt1 + n)] = ((char)((i2 << 8) + i1));
      }
    }
    return k;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    int i = this.fEncoding >= 4 ? 2 : 1;
    long l = this.fInputStream.skip(paramLong << i);
    if ((l & (i | 0x1)) == 0L) {
      return l >> i;
    }
    return (l >> i) + 1L;
  }
  
  public boolean ready()
    throws IOException
  {
    return false;
  }
  
  public boolean markSupported()
  {
    return this.fInputStream.markSupported();
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    this.fInputStream.mark(paramInt);
  }
  
  public void reset()
    throws IOException
  {
    this.fInputStream.reset();
  }
  
  public void close()
    throws IOException
  {
    BufferAllocator localBufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    localBufferAllocator.returnByteBuffer(this.fBuffer);
    this.fBuffer = null;
    this.fInputStream.close();
  }
}
