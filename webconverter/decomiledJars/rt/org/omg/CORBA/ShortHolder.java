package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ShortHolder
  implements Streamable
{
  public short value;
  
  public ShortHolder() {}
  
  public ShortHolder(short paramShort)
  {
    this.value = paramShort;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = paramInputStream.read_short();
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_short(this.value);
  }
  
  public TypeCode _type()
  {
    return ORB.init().get_primitive_tc(TCKind.tk_short);
  }
}
