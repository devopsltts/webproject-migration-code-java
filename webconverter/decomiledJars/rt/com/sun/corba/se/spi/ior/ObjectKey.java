package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;

public abstract interface ObjectKey
  extends Writeable
{
  public abstract ObjectId getId();
  
  public abstract ObjectKeyTemplate getTemplate();
  
  public abstract byte[] getBytes(org.omg.CORBA.ORB paramORB);
  
  public abstract CorbaServerRequestDispatcher getServerRequestDispatcher(com.sun.corba.se.spi.orb.ORB paramORB);
}
