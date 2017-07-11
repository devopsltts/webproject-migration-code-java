package com.sun.corba.se.spi.resolver;

import java.util.Set;

public abstract interface Resolver
{
  public abstract org.omg.CORBA.Object resolve(String paramString);
  
  public abstract Set list();
}
