package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.transport.SocketOrChannelContactInfoImpl;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketFactoryContactInfoImpl
  extends SocketOrChannelContactInfoImpl
{
  protected ORBUtilSystemException wrapper;
  protected SocketInfo socketInfo;
  
  public SocketFactoryContactInfoImpl() {}
  
  public SocketFactoryContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, IOR paramIOR, short paramShort, SocketInfo paramSocketInfo)
  {
    super(paramORB, paramCorbaContactInfoList);
    this.effectiveTargetIOR = paramIOR;
    this.addressingDisposition = paramShort;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    this.socketInfo = paramORB.getORBData().getLegacySocketFactory().getEndPointInfo(paramORB, paramIOR, paramSocketInfo);
    this.socketType = this.socketInfo.getType();
    this.hostname = this.socketInfo.getHost();
    this.port = this.socketInfo.getPort();
  }
  
  public Connection createConnection()
  {
    SocketFactoryConnectionImpl localSocketFactoryConnectionImpl = new SocketFactoryConnectionImpl(this.orb, this, this.orb.getORBData().connectionSocketUseSelectThreadToWait(), this.orb.getORBData().connectionSocketUseWorkerThreadForEvent());
    return localSocketFactoryConnectionImpl;
  }
  
  public String toString()
  {
    return "SocketFactoryContactInfoImpl[" + this.socketType + " " + this.hostname + " " + this.port + "]";
  }
}
