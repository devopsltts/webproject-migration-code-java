package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextExtHolder
  implements Streamable
{
  public NamingContextExt value = null;
  
  public NamingContextExtHolder() {}
  
  public NamingContextExtHolder(NamingContextExt paramNamingContextExt)
  {
    this.value = paramNamingContextExt;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = NamingContextExtHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NamingContextExtHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return NamingContextExtHelper.type();
  }
}
