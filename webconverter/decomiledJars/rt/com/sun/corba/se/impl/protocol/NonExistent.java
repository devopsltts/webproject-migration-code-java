package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import org.omg.CORBA.portable.OutputStream;

class NonExistent
  extends SpecialMethod
{
  NonExistent() {}
  
  public boolean isNonExistentMethod()
  {
    return true;
  }
  
  public String getName()
  {
    return "_non_existent";
  }
  
  public CorbaMessageMediator invoke(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter)
  {
    boolean bool = (paramObject == null) || ((paramObject instanceof NullServant));
    CorbaMessageMediator localCorbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createResponse(paramCorbaMessageMediator, null);
    ((OutputStream)localCorbaMessageMediator.getOutputObject()).write_boolean(bool);
    return localCorbaMessageMediator;
  }
}
