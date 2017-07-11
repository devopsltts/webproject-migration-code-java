package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.TimeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class TimeStatisticImpl
  extends StatisticImpl
  implements TimeStatistic, InvocationHandler
{
  private long count = 0L;
  private long maxTime = 0L;
  private long minTime = 0L;
  private long totTime = 0L;
  private final long initCount;
  private final long initMaxTime;
  private final long initMinTime;
  private final long initTotTime;
  private final TimeStatistic ts = (TimeStatistic)Proxy.newProxyInstance(TimeStatistic.class.getClassLoader(), new Class[] { TimeStatistic.class }, this);
  
  public final synchronized String toString()
  {
    return super.toString() + NEWLINE + "Count: " + getCount() + NEWLINE + "MinTime: " + getMinTime() + NEWLINE + "MaxTime: " + getMaxTime() + NEWLINE + "TotalTime: " + getTotalTime();
  }
  
  public TimeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, long paramLong4, String paramString1, String paramString2, String paramString3, long paramLong5, long paramLong6)
  {
    super(paramString1, paramString2, paramString3, paramLong5, paramLong6);
    this.count = paramLong1;
    this.initCount = paramLong1;
    this.maxTime = paramLong2;
    this.initMaxTime = paramLong2;
    this.minTime = paramLong3;
    this.initMinTime = paramLong3;
    this.totTime = paramLong4;
    this.initTotTime = paramLong4;
  }
  
  public synchronized TimeStatistic getStatistic()
  {
    return this.ts;
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    localMap.put("count", Long.valueOf(getCount()));
    localMap.put("maxtime", Long.valueOf(getMaxTime()));
    localMap.put("mintime", Long.valueOf(getMinTime()));
    localMap.put("totaltime", Long.valueOf(getTotalTime()));
    return localMap;
  }
  
  public synchronized void incrementCount(long paramLong)
  {
    if (this.count == 0L)
    {
      this.totTime = paramLong;
      this.maxTime = paramLong;
      this.minTime = paramLong;
    }
    else
    {
      this.totTime += paramLong;
      this.maxTime = (paramLong >= this.maxTime ? paramLong : this.maxTime);
      this.minTime = (paramLong >= this.minTime ? this.minTime : paramLong);
    }
    this.count += 1L;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public synchronized long getCount()
  {
    return this.count;
  }
  
  public synchronized long getMaxTime()
  {
    return this.maxTime;
  }
  
  public synchronized long getMinTime()
  {
    return this.minTime;
  }
  
  public synchronized long getTotalTime()
  {
    return this.totTime;
  }
  
  public synchronized void reset()
  {
    super.reset();
    this.count = this.initCount;
    this.maxTime = this.initMaxTime;
    this.minTime = this.initMinTime;
    this.totTime = this.initTotTime;
    this.sampleTime = -1L;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    checkMethod(paramMethod);
    Object localObject;
    try
    {
      localObject = paramMethod.invoke(this, paramArrayOfObject);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw localInvocationTargetException.getTargetException();
    }
    catch (Exception localException)
    {
      throw new RuntimeException("unexpected invocation exception: " + localException.getMessage());
    }
    return localObject;
  }
}
