package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.resolver.LocalResolver;

public class INSServerRequestDispatcher
  implements CorbaServerRequestDispatcher
{
  private ORB orb = null;
  private ORBUtilSystemException wrapper;
  
  public INSServerRequestDispatcher(ORB paramORB)
  {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  public IOR locate(ObjectKey paramObjectKey)
  {
    String str = new String(paramObjectKey.getBytes(this.orb));
    return getINSReference(str);
  }
  
  public void dispatch(MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    String str = new String(localCorbaMessageMediator.getObjectKey().getBytes(this.orb));
    localCorbaMessageMediator.getProtocolHandler().createLocationForward(localCorbaMessageMediator, getINSReference(str), null);
  }
  
  private IOR getINSReference(String paramString)
  {
    IOR localIOR = ORBUtility.getIOR(this.orb.getLocalResolver().resolve(paramString));
    if (localIOR != null) {
      return localIOR;
    }
    throw this.wrapper.servantNotFound();
  }
}
