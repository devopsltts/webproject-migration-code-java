package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceInformation
  implements IDLEntity
{
  public int[] service_options;
  public ServiceDetail[] service_details;
  
  public ServiceInformation() {}
  
  public ServiceInformation(int[] paramArrayOfInt, ServiceDetail[] paramArrayOfServiceDetail)
  {
    this.service_options = paramArrayOfInt;
    this.service_details = paramArrayOfServiceDetail;
  }
}
