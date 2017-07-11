package com.sun.corba.se.spi.ior;

import java.util.Iterator;
import java.util.List;

public abstract interface IORTemplate
  extends List, IORFactory, MakeImmutable
{
  public abstract Iterator iteratorById(int paramInt);
  
  public abstract ObjectKeyTemplate getObjectKeyTemplate();
}
