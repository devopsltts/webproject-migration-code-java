package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InitialNameServiceHolder
  implements Streamable
{
  public InitialNameService value = null;
  
  public InitialNameServiceHolder() {}
  
  public InitialNameServiceHolder(InitialNameService paramInitialNameService)
  {
    this.value = paramInitialNameService;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = InitialNameServiceHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    InitialNameServiceHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return InitialNameServiceHelper.type();
  }
}
