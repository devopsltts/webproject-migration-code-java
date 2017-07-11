package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class IORInterceptor_3_0Holder
  implements Streamable
{
  public IORInterceptor_3_0 value = null;
  
  public IORInterceptor_3_0Holder() {}
  
  public IORInterceptor_3_0Holder(IORInterceptor_3_0 paramIORInterceptor_3_0)
  {
    this.value = paramIORInterceptor_3_0;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = IORInterceptor_3_0Helper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    IORInterceptor_3_0Helper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return IORInterceptor_3_0Helper.type();
  }
}
