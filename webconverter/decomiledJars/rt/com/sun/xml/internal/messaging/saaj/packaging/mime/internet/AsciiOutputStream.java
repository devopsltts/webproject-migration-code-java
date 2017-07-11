package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

class AsciiOutputStream
  extends OutputStream
{
  private boolean breakOnNonAscii;
  private int ascii = 0;
  private int non_ascii = 0;
  private int linelen = 0;
  private boolean longLine = false;
  private boolean badEOL = false;
  private boolean checkEOL = false;
  private int lastb = 0;
  private int ret = 0;
  
  public AsciiOutputStream(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.breakOnNonAscii = paramBoolean1;
    this.checkEOL = ((paramBoolean2) && (paramBoolean1));
  }
  
  public void write(int paramInt)
    throws IOException
  {
    check(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt2 += paramInt1;
    for (int i = paramInt1; i < paramInt2; i++) {
      check(paramArrayOfByte[i]);
    }
  }
  
  private final void check(int paramInt)
    throws IOException
  {
    paramInt &= 0xFF;
    if ((this.checkEOL) && (((this.lastb == 13) && (paramInt != 10)) || ((this.lastb != 13) && (paramInt == 10)))) {
      this.badEOL = true;
    }
    if ((paramInt == 13) || (paramInt == 10))
    {
      this.linelen = 0;
    }
    else
    {
      this.linelen += 1;
      if (this.linelen > 998) {
        this.longLine = true;
      }
    }
    if (MimeUtility.nonascii(paramInt))
    {
      this.non_ascii += 1;
      if (this.breakOnNonAscii)
      {
        this.ret = 3;
        throw new EOFException();
      }
    }
    else
    {
      this.ascii += 1;
    }
    this.lastb = paramInt;
  }
  
  public int getAscii()
  {
    if (this.ret != 0) {
      return this.ret;
    }
    if (this.badEOL) {
      return 3;
    }
    if (this.non_ascii == 0)
    {
      if (this.longLine) {
        return 2;
      }
      return 1;
    }
    if (this.ascii > this.non_ascii) {
      return 2;
    }
    return 3;
  }
}
