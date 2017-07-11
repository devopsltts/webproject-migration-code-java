package com.sun.corba.se.spi.ior;

public abstract interface ObjectId
  extends Writeable
{
  public abstract byte[] getId();
}
