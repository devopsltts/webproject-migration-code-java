package com.sun.org.glassfish.external.statistics;

public abstract interface RangeStatistic
  extends Statistic
{
  public abstract long getHighWaterMark();
  
  public abstract long getLowWaterMark();
  
  public abstract long getCurrent();
}
