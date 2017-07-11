package com.sun.corba.se.spi.oa;

import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.corba.se.spi.orb.ORB;

public class OADefault
{
  public OADefault() {}
  
  public static ObjectAdapterFactory makePOAFactory(ORB paramORB)
  {
    POAFactory localPOAFactory = new POAFactory();
    localPOAFactory.init(paramORB);
    return localPOAFactory;
  }
  
  public static ObjectAdapterFactory makeTOAFactory(ORB paramORB)
  {
    TOAFactory localTOAFactory = new TOAFactory();
    localTOAFactory.init(paramORB);
    return localTOAFactory;
  }
}
