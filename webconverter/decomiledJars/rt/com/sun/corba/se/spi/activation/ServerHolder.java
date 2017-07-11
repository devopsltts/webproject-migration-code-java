package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerHolder
  implements Streamable
{
  public Server value = null;
  
  public ServerHolder() {}
  
  public ServerHolder(Server paramServer)
  {
    this.value = paramServer;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = ServerHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return ServerHelper.type();
  }
}
