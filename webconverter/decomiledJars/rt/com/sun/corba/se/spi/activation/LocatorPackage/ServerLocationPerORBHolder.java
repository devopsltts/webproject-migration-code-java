package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerLocationPerORBHolder
  implements Streamable
{
  public ServerLocationPerORB value = null;
  
  public ServerLocationPerORBHolder() {}
  
  public ServerLocationPerORBHolder(ServerLocationPerORB paramServerLocationPerORB)
  {
    this.value = paramServerLocationPerORB;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = ServerLocationPerORBHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerLocationPerORBHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return ServerLocationPerORBHelper.type();
  }
}
