package com.sun.corba.se.spi.monitoring;

public class StatisticMonitoredAttribute
  extends MonitoredAttributeBase
{
  private StatisticsAccumulator statisticsAccumulator;
  private Object mutex;
  
  public StatisticMonitoredAttribute(String paramString1, String paramString2, StatisticsAccumulator paramStatisticsAccumulator, Object paramObject)
  {
    super(paramString1);
    MonitoredAttributeInfoFactory localMonitoredAttributeInfoFactory = MonitoringFactories.getMonitoredAttributeInfoFactory();
    MonitoredAttributeInfo localMonitoredAttributeInfo = localMonitoredAttributeInfoFactory.createMonitoredAttributeInfo(paramString2, String.class, false, true);
    setMonitoredAttributeInfo(localMonitoredAttributeInfo);
    this.statisticsAccumulator = paramStatisticsAccumulator;
    this.mutex = paramObject;
  }
  
  public Object getValue()
  {
    synchronized (this.mutex)
    {
      return this.statisticsAccumulator.getValue();
    }
  }
  
  public void clearState()
  {
    synchronized (this.mutex)
    {
      this.statisticsAccumulator.clearState();
    }
  }
  
  public StatisticsAccumulator getStatisticsAccumulator()
  {
    return this.statisticsAccumulator;
  }
}
