package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.CORBA.UserException;

public final class ForwardRequest
  extends UserException
{
  public Object forward = null;
  
  public ForwardRequest()
  {
    super(ForwardRequestHelper.id());
  }
  
  public ForwardRequest(Object paramObject)
  {
    super(ForwardRequestHelper.id());
    this.forward = paramObject;
  }
  
  public ForwardRequest(String paramString, Object paramObject)
  {
    super(ForwardRequestHelper.id() + "  " + paramString);
    this.forward = paramObject;
  }
}
