package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyErrorHolder
  implements Streamable
{
  public PolicyError value = null;
  
  public PolicyErrorHolder() {}
  
  public PolicyErrorHolder(PolicyError paramPolicyError)
  {
    this.value = paramPolicyError;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = PolicyErrorHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    PolicyErrorHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return PolicyErrorHelper.type();
  }
}
