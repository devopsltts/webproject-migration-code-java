package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.encoding.CorbaInputObject;
import com.sun.corba.se.spi.encoding.CorbaOutputObject;
import com.sun.corba.se.spi.ior.IOR;

public abstract interface IORTransformer
{
  public abstract IOR unmarshal(CorbaInputObject paramCorbaInputObject);
  
  public abstract void marshal(CorbaOutputObject paramCorbaOutputObject, IOR paramIOR);
}
