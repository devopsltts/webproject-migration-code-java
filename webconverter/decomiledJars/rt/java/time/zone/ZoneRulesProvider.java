package java.time.zone;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ZoneRulesProvider
{
  private static final CopyOnWriteArrayList<ZoneRulesProvider> PROVIDERS = new CopyOnWriteArrayList();
  private static final ConcurrentMap<String, ZoneRulesProvider> ZONES = new ConcurrentHashMap(512, 0.75F, 2);
  
  public static Set<String> getAvailableZoneIds()
  {
    return new HashSet(ZONES.keySet());
  }
  
  public static ZoneRules getRules(String paramString, boolean paramBoolean)
  {
    Objects.requireNonNull(paramString, "zoneId");
    return getProvider(paramString).provideRules(paramString, paramBoolean);
  }
  
  public static NavigableMap<String, ZoneRules> getVersions(String paramString)
  {
    Objects.requireNonNull(paramString, "zoneId");
    return getProvider(paramString).provideVersions(paramString);
  }
  
  private static ZoneRulesProvider getProvider(String paramString)
  {
    ZoneRulesProvider localZoneRulesProvider = (ZoneRulesProvider)ZONES.get(paramString);
    if (localZoneRulesProvider == null)
    {
      if (ZONES.isEmpty()) {
        throw new ZoneRulesException("No time-zone data files registered");
      }
      throw new ZoneRulesException("Unknown time-zone ID: " + paramString);
    }
    return localZoneRulesProvider;
  }
  
  public static void registerProvider(ZoneRulesProvider paramZoneRulesProvider)
  {
    Objects.requireNonNull(paramZoneRulesProvider, "provider");
    registerProvider0(paramZoneRulesProvider);
    PROVIDERS.add(paramZoneRulesProvider);
  }
  
  private static void registerProvider0(ZoneRulesProvider paramZoneRulesProvider)
  {
    Iterator localIterator = paramZoneRulesProvider.provideZoneIds().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Objects.requireNonNull(str, "zoneId");
      ZoneRulesProvider localZoneRulesProvider = (ZoneRulesProvider)ZONES.putIfAbsent(str, paramZoneRulesProvider);
      if (localZoneRulesProvider != null) {
        throw new ZoneRulesException("Unable to register zone as one already registered with that ID: " + str + ", currently loading from provider: " + paramZoneRulesProvider);
      }
    }
  }
  
  public static boolean refresh()
  {
    boolean bool = false;
    Iterator localIterator = PROVIDERS.iterator();
    while (localIterator.hasNext())
    {
      ZoneRulesProvider localZoneRulesProvider = (ZoneRulesProvider)localIterator.next();
      bool |= localZoneRulesProvider.provideRefresh();
    }
    return bool;
  }
  
  protected ZoneRulesProvider() {}
  
  protected abstract Set<String> provideZoneIds();
  
  protected abstract ZoneRules provideRules(String paramString, boolean paramBoolean);
  
  protected abstract NavigableMap<String, ZoneRules> provideVersions(String paramString);
  
  protected boolean provideRefresh()
  {
    return false;
  }
  
  static
  {
    ArrayList localArrayList = new ArrayList();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        String str = System.getProperty("java.time.zone.DefaultZoneRulesProvider");
        if (str != null) {
          try
          {
            Class localClass = Class.forName(str, true, ClassLoader.getSystemClassLoader());
            ZoneRulesProvider localZoneRulesProvider = (ZoneRulesProvider)ZoneRulesProvider.class.cast(localClass.newInstance());
            ZoneRulesProvider.registerProvider(localZoneRulesProvider);
            this.val$loaded.add(localZoneRulesProvider);
          }
          catch (Exception localException)
          {
            throw new Error(localException);
          }
        } else {
          ZoneRulesProvider.registerProvider(new TzdbZoneRulesProvider());
        }
        return null;
      }
    });
    ServiceLoader localServiceLoader = ServiceLoader.load(ZoneRulesProvider.class, ClassLoader.getSystemClassLoader());
    Iterator localIterator1 = localServiceLoader.iterator();
    while (localIterator1.hasNext())
    {
      ZoneRulesProvider localZoneRulesProvider1;
      try
      {
        localZoneRulesProvider1 = (ZoneRulesProvider)localIterator1.next();
      }
      catch (ServiceConfigurationError localServiceConfigurationError) {}
      if (!(localServiceConfigurationError.getCause() instanceof SecurityException))
      {
        throw localServiceConfigurationError;
        int i = 0;
        Iterator localIterator2 = localArrayList.iterator();
        while (localIterator2.hasNext())
        {
          ZoneRulesProvider localZoneRulesProvider2 = (ZoneRulesProvider)localIterator2.next();
          if (localZoneRulesProvider2.getClass() == localZoneRulesProvider1.getClass()) {
            i = 1;
          }
        }
        if (i == 0)
        {
          registerProvider0(localZoneRulesProvider1);
          localArrayList.add(localZoneRulesProvider1);
        }
      }
    }
    PROVIDERS.addAll(localArrayList);
  }
}
