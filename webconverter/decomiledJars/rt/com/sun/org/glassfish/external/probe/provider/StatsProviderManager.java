package com.sun.org.glassfish.external.probe.provider;

import java.util.Iterator;
import java.util.Vector;

public class StatsProviderManager
{
  static StatsProviderManagerDelegate spmd;
  static Vector<StatsProviderInfo> toBeRegistered = new Vector();
  
  private StatsProviderManager() {}
  
  public static boolean register(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject)
  {
    return register(paramPluginPoint, paramString1, paramString2, paramObject, null);
  }
  
  public static boolean register(PluginPoint paramPluginPoint, String paramString1, String paramString2, Object paramObject, String paramString3)
  {
    StatsProviderInfo localStatsProviderInfo = new StatsProviderInfo(paramString1, paramPluginPoint, paramString2, paramObject, paramString3);
    return registerStatsProvider(localStatsProviderInfo);
  }
  
  public static boolean register(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject, String paramString3)
  {
    return register(paramString1, paramPluginPoint, paramString2, paramObject, paramString3, null);
  }
  
  public static boolean register(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject, String paramString3, String paramString4)
  {
    StatsProviderInfo localStatsProviderInfo = new StatsProviderInfo(paramString1, paramPluginPoint, paramString2, paramObject, paramString4);
    localStatsProviderInfo.setConfigLevel(paramString3);
    return registerStatsProvider(localStatsProviderInfo);
  }
  
  private static boolean registerStatsProvider(StatsProviderInfo paramStatsProviderInfo)
  {
    if (spmd == null)
    {
      toBeRegistered.add(paramStatsProviderInfo);
    }
    else
    {
      spmd.register(paramStatsProviderInfo);
      return true;
    }
    return false;
  }
  
  public static boolean unregister(Object paramObject)
  {
    if (spmd == null)
    {
      Iterator localIterator = toBeRegistered.iterator();
      while (localIterator.hasNext())
      {
        StatsProviderInfo localStatsProviderInfo = (StatsProviderInfo)localIterator.next();
        if (localStatsProviderInfo.getStatsProvider() == paramObject)
        {
          toBeRegistered.remove(localStatsProviderInfo);
          break;
        }
      }
    }
    else
    {
      spmd.unregister(paramObject);
      return true;
    }
    return false;
  }
  
  public static boolean hasListeners(String paramString)
  {
    if (spmd == null) {
      return false;
    }
    return spmd.hasListeners(paramString);
  }
  
  public static void setStatsProviderManagerDelegate(StatsProviderManagerDelegate paramStatsProviderManagerDelegate)
  {
    if (paramStatsProviderManagerDelegate == null) {
      return;
    }
    spmd = paramStatsProviderManagerDelegate;
    Iterator localIterator = toBeRegistered.iterator();
    while (localIterator.hasNext())
    {
      StatsProviderInfo localStatsProviderInfo = (StatsProviderInfo)localIterator.next();
      spmd.register(localStatsProviderInfo);
    }
    toBeRegistered.clear();
  }
}
