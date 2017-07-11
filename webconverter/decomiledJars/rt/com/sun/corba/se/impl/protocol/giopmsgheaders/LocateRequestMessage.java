package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;

public abstract interface LocateRequestMessage
  extends Message
{
  public abstract int getRequestId();
  
  public abstract ObjectKey getObjectKey();
}
