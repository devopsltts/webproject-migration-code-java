package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream;

public abstract interface WriteContents
{
  public abstract void writeContents(OutputStream paramOutputStream);
}
