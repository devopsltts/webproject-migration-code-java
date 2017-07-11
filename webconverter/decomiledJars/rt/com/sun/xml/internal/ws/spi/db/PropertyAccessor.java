package com.sun.xml.internal.ws.spi.db;

public abstract interface PropertyAccessor<B, V>
{
  public abstract V get(B paramB)
    throws DatabindingException;
  
  public abstract void set(B paramB, V paramV)
    throws DatabindingException;
}
