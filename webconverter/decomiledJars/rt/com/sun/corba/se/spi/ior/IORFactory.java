package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;

public abstract interface IORFactory
  extends Writeable, MakeImmutable
{
  public abstract IOR makeIOR(ORB paramORB, String paramString, ObjectId paramObjectId);
  
  public abstract boolean isEquivalent(IORFactory paramIORFactory);
}
