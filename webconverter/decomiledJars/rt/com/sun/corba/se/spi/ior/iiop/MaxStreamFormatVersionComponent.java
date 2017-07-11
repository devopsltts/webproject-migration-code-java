package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponent;

public abstract interface MaxStreamFormatVersionComponent
  extends TaggedComponent
{
  public abstract byte getMaxStreamFormatVersion();
}
