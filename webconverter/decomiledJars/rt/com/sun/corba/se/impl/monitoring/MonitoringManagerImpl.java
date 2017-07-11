package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;

public class MonitoringManagerImpl
  implements MonitoringManager
{
  private final MonitoredObject rootMonitoredObject;
  
  MonitoringManagerImpl(String paramString1, String paramString2)
  {
    MonitoredObjectFactory localMonitoredObjectFactory = MonitoringFactories.getMonitoredObjectFactory();
    this.rootMonitoredObject = localMonitoredObjectFactory.createMonitoredObject(paramString1, paramString2);
  }
  
  public void clearState()
  {
    this.rootMonitoredObject.clearState();
  }
  
  public MonitoredObject getRootMonitoredObject()
  {
    return this.rootMonitoredObject;
  }
  
  public void close()
  {
    MonitoringManagerFactory localMonitoringManagerFactory = MonitoringFactories.getMonitoringManagerFactory();
    localMonitoringManagerFactory.remove(this.rootMonitoredObject.getName());
  }
}
