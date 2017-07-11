package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import java.util.Collection;

public abstract interface CorbaTransportManager
  extends TransportManager
{
  public static final String SOCKET_OR_CHANNEL_CONNECTION_CACHE = "SocketOrChannelConnectionCache";
  
  public abstract Collection getAcceptors(String paramString, ObjectAdapterId paramObjectAdapterId);
  
  public abstract void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString1, String paramString2, ObjectAdapterId paramObjectAdapterId);
}
