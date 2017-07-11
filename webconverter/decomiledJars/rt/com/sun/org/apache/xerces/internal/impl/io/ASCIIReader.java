package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class ASCIIReader
  extends Reader
{
  public static final int DEFAULT_BUFFER_SIZE = 2048;
  protected InputStream fInputStream;
  protected byte[] fBuffer;
  private MessageFormatter fFormatter = null;
  private Locale fLocale = null;
  
  public ASCIIReader(InputStream paramInputStream, MessageFormatter paramMessageFormatter, Locale paramLocale)
  {
    this(paramInputStream, 2048, paramMessageFormatter, paramLocale);
  }
  
  public ASCIIReader(InputStream paramInputStream, int paramInt, MessageFormatter paramMessageFormatter, Locale paramLocale)
  {
    this.fInputStream = paramInputStream;
    BufferAllocator localBufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    this.fBuffer = localBufferAllocator.getByteBuffer(paramInt);
    if (this.fBuffer == null) {
      this.fBuffer = new byte[paramInt];
    }
    this.fFormatter = paramMessageFormatter;
    this.fLocale = paramLocale;
  }
  
  public int read()
    throws IOException
  {
    int i = this.fInputStream.read();
    if (i >= 128) {
      throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(i) });
    }
    return i;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 > this.fBuffer.length) {
      paramInt2 = this.fBuffer.length;
    }
    int i = this.fInputStream.read(this.fBuffer, 0, paramInt2);
    for (int j = 0; j < i; j++)
    {
      int k = this.fBuffer[j];
      if (k < 0) {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(k & 0xFF) });
      }
      paramArrayOfChar[(paramInt1 + j)] = ((char)k);
    }
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return this.fInputStream.skip(paramLong);
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
