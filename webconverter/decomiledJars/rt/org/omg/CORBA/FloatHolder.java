package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class FloatHolder
  implements Streamable
{
  public float value;
  
  public FloatHolder() {}
  
  public FloatHolder(float paramFloat)
  {
    this.value = paramFloat;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = paramInputStream.read_float();
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_float(this.value);
  }
  
  public TypeCode _type()
  {
    return ORB.init().get_primitive_tc(TCKind.tk_float);
  }
}
