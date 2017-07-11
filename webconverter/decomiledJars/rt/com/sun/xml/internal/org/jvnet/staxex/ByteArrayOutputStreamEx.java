package com.sun.xml.internal.org.jvnet.staxex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class ByteArrayOutputStreamEx
  extends ByteArrayOutputStream
{
  public ByteArrayOutputStreamEx() {}
  
  public ByteArrayOutputStreamEx(int paramInt)
  {
    super(paramInt);
  }
  
  public void set(Base64Data paramBase64Data, String paramString)
  {
    paramBase64Data.set(this.buf, this.count, paramString);
  }
  
  public byte[] getBuffer()
  {
    return this.buf;
  }
  
  public void readFrom(InputStream paramInputStream)
    throws IOException
  {
    for (;;)
    {
      if (this.count == this.buf.length)
      {
        byte[] arrayOfByte = new byte[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfByte, 0, this.buf.length);
        this.buf = arrayOfByte;
      }
      int i = paramInputStream.read(this.buf, this.count, this.buf.length - this.count);
      if (i < 0) {
        return;
      }
      this.count += i;
    }
  }
}
