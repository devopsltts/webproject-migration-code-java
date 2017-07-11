package com.sun.org.apache.regexp.internal;

import java.io.IOException;
import java.io.InputStream;

public final class StreamCharacterIterator
  implements CharacterIterator
{
  private final InputStream is;
  private final StringBuffer buff;
  private boolean closed;
  
  public StreamCharacterIterator(InputStream paramInputStream)
  {
    this.is = paramInputStream;
    this.buff = new StringBuffer(512);
    this.closed = false;
  }
  
  public String substring(int paramInt1, int paramInt2)
  {
    try
    {
      ensure(paramInt2);
      return this.buff.toString().substring(paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new StringIndexOutOfBoundsException(localIOException.getMessage());
    }
  }
  
  public String substring(int paramInt)
  {
    try
    {
      readAll();
      return this.buff.toString().substring(paramInt);
    }
    catch (IOException localIOException)
    {
      throw new StringIndexOutOfBoundsException(localIOException.getMessage());
    }
  }
  
  public char charAt(int paramInt)
  {
    try
    {
      ensure(paramInt);
      return this.buff.charAt(paramInt);
    }
    catch (IOException localIOException)
    {
      throw new StringIndexOutOfBoundsException(localIOException.getMessage());
    }
  }
  
  public boolean isEnd(int paramInt)
  {
    if (this.buff.length() > paramInt) {
      return false;
    }
    try
    {
      ensure(paramInt);
      return this.buff.length() <= paramInt;
    }
    catch (IOException localIOException)
    {
      throw new StringIndexOutOfBoundsException(localIOException.getMessage());
    }
  }
  
  private int read(int paramInt)
    throws IOException
  {
    if (this.closed) {
      return 0;
    }
    int j = paramInt;
    for (;;)
    {
      j--;
      if (j < 0) {
        break;
      }
      int i = this.is.read();
      if (i < 0)
      {
        this.closed = true;
        break;
      }
      this.buff.append((char)i);
    }
    return paramInt - j;
  }
  
  private void readAll()
    throws IOException
  {
    while (!this.closed) {
      read(1000);
    }
  }
  
  private void ensure(int paramInt)
    throws IOException
  {
    if (this.closed) {
      return;
    }
    if (paramInt < this.buff.length()) {
      return;
    }
    read(paramInt + 1 - this.buff.length());
  }
}
