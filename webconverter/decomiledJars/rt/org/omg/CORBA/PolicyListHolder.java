package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyListHolder
  implements Streamable
{
  public Policy[] value = null;
  
  public PolicyListHolder() {}
  
  public PolicyListHolder(Policy[] paramArrayOfPolicy)
  {
    this.value = paramArrayOfPolicy;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = PolicyListHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    PolicyListHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return PolicyListHelper.type();
  }
}
