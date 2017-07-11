package com.sun.corba.se.spi.activation.InitialNameServicePackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameAlreadyBoundHolder
  implements Streamable
{
  public NameAlreadyBound value = null;
  
  public NameAlreadyBoundHolder() {}
  
  public NameAlreadyBoundHolder(NameAlreadyBound paramNameAlreadyBound)
  {
    this.value = paramNameAlreadyBound;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = NameAlreadyBoundHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NameAlreadyBoundHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return NameAlreadyBoundHelper.type();
  }
}
