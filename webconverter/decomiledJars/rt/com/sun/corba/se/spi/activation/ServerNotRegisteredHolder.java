package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerNotRegisteredHolder
  implements Streamable
{
  public ServerNotRegistered value = null;
  
  public ServerNotRegisteredHolder() {}
  
  public ServerNotRegisteredHolder(ServerNotRegistered paramServerNotRegistered)
  {
    this.value = paramServerNotRegistered;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = ServerNotRegisteredHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerNotRegisteredHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return ServerNotRegisteredHelper.type();
  }
}
