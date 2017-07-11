package com.sun.xml.internal.messaging.saaj.util;

import java.io.CharArrayWriter;

public class CharWriter
  extends CharArrayWriter
{
  public CharWriter() {}
  
  public CharWriter(int paramInt)
  {
    super(paramInt);
  }
  
  public char[] getChars()
  {
    return this.buf;
  }
  
  public int getCount()
  {
    return this.count;
  }
}
