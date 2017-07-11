package com.sun.corba.se.impl.io;

import java.io.IOException;

public class OptionalDataException
  extends IOException
{
  public int length;
  public boolean eof;
  
  OptionalDataException(int paramInt)
  {
    this.eof = false;
    this.length = paramInt;
  }
  
  OptionalDataException(boolean paramBoolean)
  {
    this.length = 0;
    this.eof = paramBoolean;
  }
}
