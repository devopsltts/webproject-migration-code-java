package com.sun.corba.se.spi.legacy.connection;

import java.net.Socket;

public abstract interface Connection
{
  public abstract Socket getSocket();
}
