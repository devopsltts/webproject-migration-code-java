package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.RangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class RangeStatisticImpl
  extends StatisticImpl
  implements RangeStatistic, InvocationHandler
{
  private long currentVal = 0L;
  private long highWaterMark = Long.MIN_VALUE;
  private long lowWaterMark = Long.MAX_VALUE;
  private final long initCurrentVal;
  private final long initHighWaterMark;
  private final long initLowWaterMark;
  private final RangeStatistic rs = (RangeStatistic)Proxy.newProxyInstance(RangeStatistic.class.getClassLoader(), new Class[] { RangeStatistic.class }, this);
  
  public RangeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, String paramString1, String paramString2, String paramString3, long paramLong4, long paramLong5)
  {
    super(paramString1, paramString2, paramString3, paramLong4, paramLong5);
    this.currentVal = paramLong1;
    this.initCurrentVal = paramLong1;
    this.highWaterMark = paramLong2;
    this.initHighWaterMark = paramLong2;
    this.lowWaterMark = paramLong3;
    this.initLowWaterMark = paramLong3;
  }
  
  public synchronized RangeStatistic getStatistic()
  {
    return this.rs;
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    localMap.put("current", Long.valueOf(getCurrent()));
    localMap.put("lowwatermark", Long.valueOf(getLowWaterMark()));
    localMap.put("highwatermark", Long.valueOf(getHighWaterMark()));
    return localMap;
  }
  
  public synchronized long getCurrent()
  {
    return this.currentVal;
  }
  
  public synchronized void setCurrent(long paramLong)
  {
    this.currentVal = paramLong;
    this.lowWaterMark = (paramLong >= this.lowWaterMark ? this.lowWaterMark : paramLong);
    this.highWaterMark = (paramLong >= this.highWaterMark ? paramLong : this.highWaterMark);
    this.sampleTime = System.currentTimeMillis();
  }
  
  public synchronized long getHighWaterMark()
  {
    return this.highWaterMark;
  }
  
  public synchronized void setHighWaterMark(long paramLong)
  {
    this.highWaterMark = paramLong;
  }
  
  public synchronized long getLowWaterMark()
  {
    return this.lowWaterMark;
  }
  
  public synchronized void setLowWaterMark(long paramLong)
  {
    this.lowWaterMark = paramLong;
  }
  
  public synchronized void reset()
  {
    super.reset();
    this.currentVal = this.initCurrentVal;
    this.highWaterMark = this.initHighWaterMark;
    this.lowWaterMark = this.initLowWaterMark;
    this.sampleTime = -1L;
  }
  
  public synchronized String toString()
  {
    return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark();
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
