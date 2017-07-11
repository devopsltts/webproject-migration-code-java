package com.sun.jndi.ldap.pool;

import com.sun.jndi.ldap.LdapPoolManager;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import javax.naming.NamingException;

public final class Pool
{
  static final boolean debug = LdapPoolManager.debug;
  private static final ReferenceQueue<ConnectionsRef> queue = new ReferenceQueue();
  private static final Collection<Reference<ConnectionsRef>> weakRefs = Collections.synchronizedList(new LinkedList());
  private final int maxSize;
  private final int prefSize;
  private final int initSize;
  private final Map<Object, ConnectionsRef> map = new WeakHashMap();
  
  public Pool(int paramInt1, int paramInt2, int paramInt3)
  {
    this.prefSize = paramInt2;
    this.maxSize = paramInt3;
    this.initSize = paramInt1;
  }
  
  public PooledConnection getPooledConnection(Object paramObject, long paramLong, PooledConnectionFactory paramPooledConnectionFactory)
    throws NamingException
  {
    d("get(): ", paramObject);
    d("size: ", this.map.size());
    expungeStaleConnections();
    Connections localConnections;
    synchronized (this.map)
    {
      localConnections = getConnections(paramObject);
      if (localConnections == null)
      {
        d("get(): creating new connections list for ", paramObject);
        localConnections = new Connections(paramObject, this.initSize, this.prefSize, this.maxSize, paramPooledConnectionFactory);
        ConnectionsRef localConnectionsRef = new ConnectionsRef(localConnections);
        this.map.put(paramObject, localConnectionsRef);
        ConnectionsWeakRef localConnectionsWeakRef = new ConnectionsWeakRef(localConnectionsRef, queue);
        weakRefs.add(localConnectionsWeakRef);
      }
    }
    d("get(): size after: ", this.map.size());
    return localConnections.get(paramLong, paramPooledConnectionFactory);
  }
  
  private Connections getConnections(Object paramObject)
  {
    ConnectionsRef localConnectionsRef = (ConnectionsRef)this.map.get(paramObject);
    return localConnectionsRef != null ? localConnectionsRef.getConnections() : null;
  }
  
  public void expire(long paramLong)
  {
    synchronized (this.map)
    {
      Iterator localIterator = this.map.values().iterator();
      while (localIterator.hasNext())
      {
        Connections localConnections = ((ConnectionsRef)localIterator.next()).getConnections();
        if (localConnections.expire(paramLong))
        {
          d("expire(): removing ", localConnections);
          localIterator.remove();
        }
      }
    }
    expungeStaleConnections();
  }
  
  private static void expungeStaleConnections()
  {
    ConnectionsWeakRef localConnectionsWeakRef = null;
    while ((localConnectionsWeakRef = (ConnectionsWeakRef)queue.poll()) != null)
    {
      Connections localConnections = localConnectionsWeakRef.getConnections();
      if (debug) {
        System.err.println("weak reference cleanup: Closing Connections:" + localConnections);
      }
      localConnections.close();
      weakRefs.remove(localConnectionsWeakRef);
      localConnectionsWeakRef.clear();
    }
  }
  
  public void showStats(PrintStream paramPrintStream)
  {
    paramPrintStream.println("===== Pool start ======================");
    paramPrintStream.println("maximum pool size: " + this.maxSize);
    paramPrintStream.println("preferred pool size: " + this.prefSize);
    paramPrintStream.println("initial pool size: " + this.initSize);
    paramPrintStream.println("current pool size: " + this.map.size());
    Iterator localIterator = this.map.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject = localEntry.getKey();
      Connections localConnections = ((ConnectionsRef)localEntry.getValue()).getConnections();
      paramPrintStream.println("   " + localObject + ":" + localConnections.getStats());
    }
    paramPrintStream.println("====== Pool end =====================");
  }
  
  public String toString()
  {
    return super.toString() + " " + this.map.toString();
  }
  
  private void d(String paramString, int paramInt)
  {
    if (debug) {
      System.err.println(this + "." + paramString + paramInt);
    }
  }
  
  private void d(String paramString, Object paramObject)
  {
    if (debug) {
      System.err.println(this + "." + paramString + paramObject);
    }
  }
}
