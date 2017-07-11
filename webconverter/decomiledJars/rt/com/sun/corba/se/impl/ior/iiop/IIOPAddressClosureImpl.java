package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public final class IIOPAddressClosureImpl
  extends IIOPAddressBase
{
  private Closure host;
  private Closure port;
  
  public IIOPAddressClosureImpl(Closure paramClosure1, Closure paramClosure2)
  {
    this.host = paramClosure1;
    this.port = paramClosure2;
  }
  
  public String getHost()
  {
    return (String)this.host.evaluate();
  }
  
  public int getPort()
  {
    Integer localInteger = (Integer)this.port.evaluate();
    return localInteger.intValue();
  }
}
