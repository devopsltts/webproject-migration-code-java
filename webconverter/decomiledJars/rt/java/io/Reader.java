package java.io;

import java.nio.CharBuffer;

public abstract class Reader
  implements Readable, Closeable
{
  protected Object lock;
  private static final int maxSkipBufferSize = 8192;
  private char[] skipBuffer = null;
  
  protected Reader()
  {
    this.lock = this;
  }
  
  protected Reader(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    this.lock = paramObject;
  }
  
  public int read(CharBuffer paramCharBuffer)
    throws IOException
  {
    int i = paramCharBuffer.remaining();
    char[] arrayOfChar = new char[i];
    int j = read(arrayOfChar, 0, i);
    if (j > 0) {
      paramCharBuffer.put(arrayOfChar, 0, j);
    }
    return j;
  }
  
  public int read()
    throws IOException
  {
    char[] arrayOfChar = new char[1];
    if (read(arrayOfChar, 0, 1) == -1) {
      return -1;
    }
    return arrayOfChar[0];
  }
  
  public int read(char[] paramArrayOfChar)
    throws IOException
  {
    return read(paramArrayOfChar, 0, paramArrayOfChar.length);
  }
  
  public abstract int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException;
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("skip value is negative");
    }
    int i = (int)Math.min(paramLong, 8192L);
    synchronized (this.lock)
    {
      if ((this.skipBuffer == null) || (this.skipBuffer.length < i)) {
        this.skipBuffer = new char[i];
      }
      int j;
      for (long l = paramLong; l > 0L; l -= j)
      {
        j = read(this.skipBuffer, 0, (int)Math.min(l, i));
        if (j == -1) {
          break;
        }
      }
      return paramLong - l;
    }
  }
  
  public boolean ready()
    throws IOException
  {
    return false;
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    throw new IOException("mark() not supported");
  }
  
  public void reset()
    throws IOException
  {
    throw new IOException("reset() not supported");
  }
  
  public abstract void close()
    throws IOException;
}
