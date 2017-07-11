package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;
import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfoFactory;

public class MonitoredAttributeInfoFactoryImpl
  implements MonitoredAttributeInfoFactory
{
  public MonitoredAttributeInfoFactoryImpl() {}
  
  public MonitoredAttributeInfo createMonitoredAttributeInfo(String paramString, Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new MonitoredAttributeInfoImpl(paramString, paramClass, paramBoolean1, paramBoolean2);
  }
}
