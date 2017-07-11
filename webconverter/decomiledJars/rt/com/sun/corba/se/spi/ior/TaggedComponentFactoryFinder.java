package com.sun.corba.se.spi.ior;

import org.omg.CORBA.ORB;

public abstract interface TaggedComponentFactoryFinder
  extends IdentifiableFactoryFinder
{
  public abstract TaggedComponent create(ORB paramORB, org.omg.IOP.TaggedComponent paramTaggedComponent);
}
