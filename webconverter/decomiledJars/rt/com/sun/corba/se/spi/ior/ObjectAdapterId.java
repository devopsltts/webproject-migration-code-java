package com.sun.corba.se.spi.ior;

import java.util.Iterator;

public abstract interface ObjectAdapterId
  extends Writeable
{
  public abstract int getNumLevels();
  
  public abstract Iterator iterator();
  
  public abstract String[] getAdapterName();
}
