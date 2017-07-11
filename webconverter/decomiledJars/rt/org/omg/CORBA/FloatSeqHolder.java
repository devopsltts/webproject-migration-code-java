package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class FloatSeqHolder
  implements Streamable
{
  public float[] value = null;
  
  public FloatSeqHolder() {}
  
  public FloatSeqHolder(float[] paramArrayOfFloat)
  {
    this.value = paramArrayOfFloat;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = FloatSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    FloatSeqHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return FloatSeqHelper.type();
  }
}
