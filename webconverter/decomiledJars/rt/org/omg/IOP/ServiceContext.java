package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceContext
  implements IDLEntity
{
  public int context_id = 0;
  public byte[] context_data = null;
  
  public ServiceContext() {}
  
  public ServiceContext(int paramInt, byte[] paramArrayOfByte)
  {
    this.context_id = paramInt;
    this.context_data = paramArrayOfByte;
  }
}
