package com.sun.corba.se.pept.transport;

public abstract interface ConnectionCache
{
  public abstract String getCacheType();
  
  public abstract void stampTime(Connection paramConnection);
  
  public abstract long numberOfConnections();
  
  public abstract long numberOfIdleConnections();
  
  public abstract long numberOfBusyConnections();
  
  public abstract boolean reclaim();
  
  public abstract void close();
}
