package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceDetail
  implements IDLEntity
{
  public int service_detail_type;
  public byte[] service_detail;
  
  public ServiceDetail() {}
  
  public ServiceDetail(int paramInt, byte[] paramArrayOfByte)
  {
    this.service_detail_type = paramInt;
    this.service_detail = paramArrayOfByte;
  }
}
