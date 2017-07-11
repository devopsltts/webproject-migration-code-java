package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UUEncoderStream
  extends FilterOutputStream
{
  private byte[] buffer;
  private int bufsize = 0;
  private boolean wrotePrefix = false;
  protected String name;
  protected int mode;
  
  public UUEncoderStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, "encoder.buf", 644);
  }
  
  public UUEncoderStream(OutputStream paramOutputStream, String paramString)
  {
    this(paramOutputStream, paramString, 644);
  }
  
  public UUEncoderStream(OutputStream paramOutputStream, String paramString, int paramInt)
  {
    super(paramOutputStream);
    this.name = paramString;
    this.mode = paramInt;
    this.buffer = new byte[45];
  }
  
  public void setNameMode(String paramString, int paramInt)
  {
    this.name = paramString;
    this.mode = paramInt;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfByte[(paramInt1 + i)]);
    }
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(int paramInt)
    throws IOException
  {
    this.buffer[(this.bufsize++)] = ((byte)paramInt);
    if (this.bufsize == 45)
    {
      writePrefix();
      encode();
      this.bufsize = 0;
    }
  }
  
  public void flush()
    throws IOException
  {
    if (this.bufsize > 0)
    {
      writePrefix();
      encode();
    }
    writeSuffix();
    this.out.flush();
  }
  
  public void close()
    throws IOException
  {
    flush();
    this.out.close();
  }
  
  private void writePrefix()
    throws IOException
  {
    if (!this.wrotePrefix)
    {
      PrintStream localPrintStream = new PrintStream(this.out);
      localPrintStream.println("begin " + this.mode + " " + this.name);
      localPrintStream.flush();
      this.wrotePrefix = true;
    }
  }
  
  private void writeSuffix()
    throws IOException
  {
    PrintStream localPrintStream = new PrintStream(this.out);
    localPrintStream.println(" \nend");
    localPrintStream.flush();
  }
  
  private void encode()
    throws IOException
  {
    int i3 = 0;
    this.out.write((this.bufsize & 0x3F) + 32);
    while (i3 < this.bufsize)
    {
      int i = this.buffer[(i3++)];
      int j;
      int k;
      if (i3 < this.bufsize)
      {
        j = this.buffer[(i3++)];
        if (i3 < this.bufsize) {
          k = this.buffer[(i3++)];
        } else {
          k = 1;
        }
      }
      else
      {
        j = 1;
        k = 1;
      }
      int m = i >>> 2 & 0x3F;
      int n = i << 4 & 0x30 | j >>> 4 & 0xF;
      int i1 = j << 2 & 0x3C | k >>> 6 & 0x3;
      int i2 = k & 0x3F;
      this.out.write(m + 32);
      this.out.write(n + 32);
      this.out.write(i1 + 32);
      this.out.write(i2 + 32);
    }
    this.out.write(10);
  }
}
