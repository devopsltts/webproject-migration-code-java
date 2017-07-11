package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerManagerHolder
  implements Streamable
{
  public ServerManager value = null;
  
  public ServerManagerHolder() {}
  
  public ServerManagerHolder(ServerManager paramServerManager)
  {
    this.value = paramServerManager;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = ServerManagerHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerManagerHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return ServerManagerHelper.type();
  }
}
