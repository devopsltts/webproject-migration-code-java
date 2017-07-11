package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;

public class RequestProcessingPolicyImpl
  extends LocalObject
  implements RequestProcessingPolicy
{
  private RequestProcessingPolicyValue value;
  
  public RequestProcessingPolicyImpl(RequestProcessingPolicyValue paramRequestProcessingPolicyValue)
  {
    this.value = paramRequestProcessingPolicyValue;
  }
  
  public RequestProcessingPolicyValue value()
  {
    return this.value;
  }
  
  public int policy_type()
  {
    return 22;
  }
  
  public Policy copy()
  {
    return new RequestProcessingPolicyImpl(this.value);
  }
  
  public void destroy()
  {
    this.value = null;
  }
  
  public String toString()
  {
    String str = null;
    switch (this.value.value())
    {
    case 0: 
      str = "USE_ACTIVE_OBJECT_MAP_ONLY";
      break;
    case 1: 
      str = "USE_DEFAULT_SERVANT";
      break;
    case 2: 
      str = "USE_SERVANT_MANAGER";
    }
    return "RequestProcessingPolicy[" + str + "]";
  }
}
