package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameComponentHolder
  implements Streamable
{
  public NameComponent value = null;
  
  public NameComponentHolder() {}
  
  public NameComponentHolder(NameComponent paramNameComponent)
  {
    this.value = paramNameComponent;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = NameComponentHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NameComponentHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return NameComponentHelper.type();
  }
}
