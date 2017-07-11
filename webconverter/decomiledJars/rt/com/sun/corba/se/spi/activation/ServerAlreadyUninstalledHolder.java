package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyUninstalledHolder
  implements Streamable
{
  public ServerAlreadyUninstalled value = null;
  
  public ServerAlreadyUninstalledHolder() {}
  
  public ServerAlreadyUninstalledHolder(ServerAlreadyUninstalled paramServerAlreadyUninstalled)
  {
    this.value = paramServerAlreadyUninstalled;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = ServerAlreadyUninstalledHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerAlreadyUninstalledHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return ServerAlreadyUninstalledHelper.type();
  }
}
