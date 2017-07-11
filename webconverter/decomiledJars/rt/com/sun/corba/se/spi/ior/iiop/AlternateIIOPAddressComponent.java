package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponent;

public abstract interface AlternateIIOPAddressComponent
  extends TaggedComponent
{
  public abstract IIOPAddress getAddress();
}
