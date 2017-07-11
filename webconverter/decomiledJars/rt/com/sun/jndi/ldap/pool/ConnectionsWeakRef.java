package com.sun.jndi.ldap.pool;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class ConnectionsWeakRef
  extends WeakReference<ConnectionsRef>
{
  private final Connections conns;
  
  ConnectionsWeakRef(ConnectionsRef paramConnectionsRef, ReferenceQueue<? super ConnectionsRef> paramReferenceQueue)
  {
    super(paramConnectionsRef, paramReferenceQueue);
    this.conns = paramConnectionsRef.getConnections();
  }
  
  Connections getConnections()
  {
    return this.conns;
  }
}
