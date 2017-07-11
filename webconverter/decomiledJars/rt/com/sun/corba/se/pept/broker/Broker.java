package com.sun.corba.se.pept.broker;

import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.transport.TransportManager;

public abstract interface Broker
{
  public abstract ClientInvocationInfo createOrIncrementInvocationInfo();
  
  public abstract ClientInvocationInfo getInvocationInfo();
  
  public abstract void releaseOrDecrementInvocationInfo();
  
  public abstract TransportManager getTransportManager();
}
