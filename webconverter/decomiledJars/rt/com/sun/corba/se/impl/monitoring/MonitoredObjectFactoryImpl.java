package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;

public class MonitoredObjectFactoryImpl
  implements MonitoredObjectFactory
{
  public MonitoredObjectFactoryImpl() {}
  
  public MonitoredObject createMonitoredObject(String paramString1, String paramString2)
  {
    return new MonitoredObjectImpl(paramString1, paramString2);
  }
}
