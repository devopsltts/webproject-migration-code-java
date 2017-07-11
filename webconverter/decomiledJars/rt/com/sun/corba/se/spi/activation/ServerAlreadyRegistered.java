package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyRegistered
  extends UserException
{
  public int serverId = 0;
  
  public ServerAlreadyRegistered()
  {
    super(ServerAlreadyRegisteredHelper.id());
  }
  
  public ServerAlreadyRegistered(int paramInt)
  {
    super(ServerAlreadyRegisteredHelper.id());
    this.serverId = paramInt;
  }
  
  public ServerAlreadyRegistered(String paramString, int paramInt)
  {
    super(ServerAlreadyRegisteredHelper.id() + "  " + paramString);
    this.serverId = paramInt;
  }
}
