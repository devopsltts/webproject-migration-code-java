package com.sun.org.apache.regexp.internal;

import java.io.IOException;
import java.io.Reader;

public final class ReaderCharacterIterator
  implements CharacterIterator
{
  private final Reader reader;
  private final StringBuffer buff;
  private boolean closed;
  
  public ReaderCharacterIterator(Reader paramReader)
  {
    this.reader = paramReader;
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
    char[] arrayOfChar = new char[paramInt];
    int i = 0;
    int j = 0;
    do
    {
      j = this.reader.read(arrayOfChar);
      if (j < 0)
      {
        this.closed = true;
        break;
      }
      i += j;
      this.buff.append(arrayOfChar, 0, j);
    } while (i < paramInt);
    return i;
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
