package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class POANameHolder
  implements Streamable
{
  public String[] value = null;
  
  public POANameHolder() {}
  
  public POANameHolder(String[] paramArrayOfString)
  {
    this.value = paramArrayOfString;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = POANameHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    POANameHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return POANameHelper.type();
  }
}
