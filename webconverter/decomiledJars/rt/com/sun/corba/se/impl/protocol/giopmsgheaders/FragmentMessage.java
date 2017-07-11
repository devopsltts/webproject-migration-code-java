package com.sun.corba.se.impl.protocol.giopmsgheaders;

public abstract interface FragmentMessage
  extends Message
{
  public abstract int getRequestId();
  
  public abstract int getHeaderLength();
}
