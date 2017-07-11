package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingHolder
  implements Streamable
{
  public Binding value = null;
  
  public BindingHolder() {}
  
  public BindingHolder(Binding paramBinding)
  {
    this.value = paramBinding;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = BindingHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    BindingHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return BindingHelper.type();
  }
}
