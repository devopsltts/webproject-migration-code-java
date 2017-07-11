package com.sun.corba.se.impl.protocol.giopmsgheaders;

public abstract interface CancelRequestMessage
  extends Message
{
  public static final int CANCEL_REQ_MSG_SIZE = 4;
  
  public abstract int getRequestId();
}
