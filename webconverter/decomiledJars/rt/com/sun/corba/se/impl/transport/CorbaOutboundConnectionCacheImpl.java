package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import java.util.Collection;
import java.util.Hashtable;

public class CorbaOutboundConnectionCacheImpl
  extends CorbaConnectionCacheBase
  implements OutboundConnectionCache
{
  protected Hashtable connectionCache = new Hashtable();
  
  public CorbaOutboundConnectionCacheImpl(ORB paramORB, ContactInfo paramContactInfo)
  {
    super(paramORB, paramContactInfo.getConnectionCacheType(), ((CorbaContactInfo)paramContactInfo).getMonitoringName());
  }
  
  public Connection get(ContactInfo paramContactInfo)
  {
    if (this.orb.transportDebugFlag) {
      dprint(".get: " + paramContactInfo + " " + paramContactInfo.hashCode());
    }
    synchronized (backingStore())
    {
      dprintStatistics();
      return (Connection)this.connectionCache.get(paramContactInfo);
    }
  }
  
  public void put(ContactInfo paramContactInfo, Connection paramConnection)
  {
    if (this.orb.transportDebugFlag) {
      dprint(".put: " + paramContactInfo + " " + paramContactInfo.hashCode() + " " + paramConnection);
    }
    synchronized (backingStore())
    {
      this.connectionCache.put(paramContactInfo, paramConnection);
      paramConnection.setConnectionCache(this);
      dprintStatistics();
    }
  }
  
  public void remove(ContactInfo paramContactInfo)
  {
    if (this.orb.transportDebugFlag) {
      dprint(".remove: " + paramContactInfo + " " + paramContactInfo.hashCode());
    }
    synchronized (backingStore())
    {
      if (paramContactInfo != null) {
        this.connectionCache.remove(paramContactInfo);
      }
      dprintStatistics();
    }
  }
  
  public Collection values()
  {
    return this.connectionCache.values();
  }
  
  protected Object backingStore()
  {
    return this.connectionCache;
  }
  
  protected void registerWithMonitoring()
  {
    MonitoredObject localMonitoredObject1 = this.orb.getMonitoringManager().getRootMonitoredObject();
    MonitoredObject localMonitoredObject2 = localMonitoredObject1.getChild("Connections");
    if (localMonitoredObject2 == null)
    {
      localMonitoredObject2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Connections", "Statistics on inbound/outbound connections");
      localMonitoredObject1.addChild(localMonitoredObject2);
    }
    MonitoredObject localMonitoredObject3 = localMonitoredObject2.getChild("Outbound");
    if (localMonitoredObject3 == null)
    {
      localMonitoredObject3 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Outbound", "Statistics on outbound connections");
      localMonitoredObject2.addChild(localMonitoredObject3);
    }
    MonitoredObject localMonitoredObject4 = localMonitoredObject3.getChild(getMonitoringName());
    if (localMonitoredObject4 == null)
    {
      localMonitoredObject4 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(getMonitoringName(), "Connection statistics");
      localMonitoredObject3.addChild(localMonitoredObject4);
    }
    Object localObject = new LongMonitoredAttributeBase("NumberOfConnections", "The total number of connections")
    {
      public Object getValue()
      {
        return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfConnections());
      }
    };
    localMonitoredObject4.addAttribute((MonitoredAttribute)localObject);
    localObject = new LongMonitoredAttributeBase("NumberOfIdleConnections", "The number of idle connections")
    {
      public Object getValue()
      {
        return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfIdleConnections());
      }
    };
    localMonitoredObject4.addAttribute((MonitoredAttribute)localObject);
    localObject = new LongMonitoredAttributeBase("NumberOfBusyConnections", "The number of busy connections")
    {
      public Object getValue()
      {
        return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfBusyConnections());
      }
    };
    localMonitoredObject4.addAttribute((MonitoredAttribute)localObject);
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaOutboundConnectionCacheImpl", paramString);
  }
}
