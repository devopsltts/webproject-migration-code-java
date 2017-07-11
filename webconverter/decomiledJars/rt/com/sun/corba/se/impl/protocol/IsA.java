package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

class IsA
  extends SpecialMethod
{
  IsA() {}
  
  public boolean isNonExistentMethod()
  {
    return false;
  }
  
  public String getName()
  {
    return "_is_a";
  }
  
  public CorbaMessageMediator invoke(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter)
  {
    if ((paramObject == null) || ((paramObject instanceof NullServant)))
    {
      localObject1 = (ORB)paramCorbaMessageMediator.getBroker();
      localObject2 = ORBUtilSystemException.get((ORB)localObject1, "oa.invocation");
      return paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, ((ORBUtilSystemException)localObject2).badSkeleton(), null);
    }
    Object localObject1 = paramObjectAdapter.getInterfaces(paramObject, paramArrayOfByte);
    Object localObject2 = ((InputStream)paramCorbaMessageMediator.getInputObject()).read_string();
    boolean bool = false;
    for (int i = 0; i < localObject1.length; i++) {
      if (localObject1[i].equals(localObject2))
      {
        bool = true;
        break;
      }
    }
    CorbaMessageMediator localCorbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createResponse(paramCorbaMessageMediator, null);
    ((OutputStream)localCorbaMessageMediator.getOutputObject()).write_boolean(bool);
    return localCorbaMessageMediator;
  }
}
