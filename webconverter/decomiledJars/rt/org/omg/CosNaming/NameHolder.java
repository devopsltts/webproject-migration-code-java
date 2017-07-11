package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameHolder
  implements Streamable
{
  public NameComponent[] value = null;
  
  public NameHolder() {}
  
  public NameHolder(NameComponent[] paramArrayOfNameComponent)
  {
    this.value = paramArrayOfNameComponent;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = NameHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NameHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return NameHelper.type();
  }
}
