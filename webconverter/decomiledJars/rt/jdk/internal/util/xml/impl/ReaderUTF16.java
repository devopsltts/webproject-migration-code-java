package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class ReaderUTF16
  extends Reader
{
  private InputStream is;
  private char bo;
  
  public ReaderUTF16(InputStream paramInputStream, char paramChar)
  {
    switch (paramChar)
    {
    case 'l': 
      break;
    case 'b': 
      break;
    default: 
      throw new IllegalArgumentException("");
    }
    this.bo = paramChar;
    this.is = paramInputStream;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    int j;
    if (this.bo == 'b') {
      while (i < paramInt2)
      {
        if ((j = this.is.read()) < 0) {
          return i != 0 ? i : -1;
        }
        paramArrayOfChar[(paramInt1++)] = ((char)(j << 8 | this.is.read() & 0xFF));
        i++;
      }
    }
    while (i < paramInt2)
    {
      if ((j = this.is.read()) < 0) {
        return i != 0 ? i : -1;
      }
      paramArrayOfChar[(paramInt1++)] = ((char)(this.is.read() << 8 | j & 0xFF));
      i++;
    }
    return i;
  }
  
  public int read()
    throws IOException
  {
    int i;
    if ((i = this.is.read()) < 0) {
      return -1;
    }
    if (this.bo == 'b') {
      i = (char)(i << 8 | this.is.read() & 0xFF);
    } else {
      i = (char)(this.is.read() << 8 | i & 0xFF);
    }
    return i;
  }
  
  public void close()
    throws IOException
  {
    this.is.close();
  }
}
