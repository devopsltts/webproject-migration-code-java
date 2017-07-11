package com.sun.jndi.ldap.pool;

final class ConnectionsRef
{
  private final Connections conns;
  
  ConnectionsRef(Connections paramConnections)
  {
    this.conns = paramConnections;
  }
  
  Connections getConnections()
  {
    return this.conns;
  }
}
