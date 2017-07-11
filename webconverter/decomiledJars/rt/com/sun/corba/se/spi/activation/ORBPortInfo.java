package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class ORBPortInfo
  implements IDLEntity
{
  public String orbId = null;
  public int port = 0;
  
  public ORBPortInfo() {}
  
  public ORBPortInfo(String paramString, int paramInt)
  {
    this.orbId = paramString;
    this.port = paramInt;
  }
}
