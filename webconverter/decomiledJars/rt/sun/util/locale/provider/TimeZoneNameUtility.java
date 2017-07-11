package sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.spi.TimeZoneNameProvider;
import sun.util.calendar.ZoneInfo;

public final class TimeZoneNameUtility
{
  private static ConcurrentHashMap<Locale, SoftReference<String[][]>> cachedZoneData = new ConcurrentHashMap();
  private static final Map<String, SoftReference<Map<Locale, String[]>>> cachedDisplayNames = new ConcurrentHashMap();
  
  public static String[][] getZoneStrings(Locale paramLocale)
  {
    SoftReference localSoftReference = (SoftReference)cachedZoneData.get(paramLocale);
    String[][] arrayOfString;
    if ((localSoftReference == null) || ((arrayOfString = (String[][])localSoftReference.get()) == null))
    {
      arrayOfString = loadZoneStrings(paramLocale);
      localSoftReference = new SoftReference(arrayOfString);
      cachedZoneData.put(paramLocale, localSoftReference);
    }
    return arrayOfString;
  }
  
  private static String[][] loadZoneStrings(Locale paramLocale)
  {
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(TimeZoneNameProvider.class, paramLocale);
    TimeZoneNameProvider localTimeZoneNameProvider = localLocaleProviderAdapter.getTimeZoneNameProvider();
    if ((localTimeZoneNameProvider instanceof TimeZoneNameProviderImpl)) {
      return ((TimeZoneNameProviderImpl)localTimeZoneNameProvider).getZoneStrings(paramLocale);
    }
    Set localSet = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale).getZoneIDs();
    LinkedList localLinkedList = new LinkedList();
    Object localObject = localSet.iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      String[] arrayOfString = retrieveDisplayNamesImpl(str, paramLocale);
      if (arrayOfString != null) {
        localLinkedList.add(arrayOfString);
      }
    }
    localObject = new String[localLinkedList.size()][];
    return (String[][])localLinkedList.toArray((Object[])localObject);
  }
  
  public static String[] retrieveDisplayNames(String paramString, Locale paramLocale)
  {
    Objects.requireNonNull(paramString);
    Objects.requireNonNull(paramLocale);
    return retrieveDisplayNamesImpl(paramString, paramLocale);
  }
  
  public static String retrieveGenericDisplayName(String paramString, int paramInt, Locale paramLocale)
  {
    String[] arrayOfString = retrieveDisplayNamesImpl(paramString, paramLocale);
    if (Objects.nonNull(arrayOfString)) {
      return arrayOfString[(6 - paramInt)];
    }
    return null;
  }
  
  public static String retrieveDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale)
  {
    String[] arrayOfString = retrieveDisplayNamesImpl(paramString, paramLocale);
    if (Objects.nonNull(arrayOfString)) {
      return arrayOfString[(2 - paramInt)];
    }
    return null;
  }
  
  private static String[] retrieveDisplayNamesImpl(String paramString, Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(TimeZoneNameProvider.class);
    Object localObject = null;
    SoftReference localSoftReference = (SoftReference)cachedDisplayNames.get(paramString);
    if (Objects.nonNull(localSoftReference))
    {
      localObject = (Map)localSoftReference.get();
      if (Objects.nonNull(localObject))
      {
        arrayOfString = (String[])((Map)localObject).get(paramLocale);
        if (Objects.nonNull(arrayOfString)) {
          return arrayOfString;
        }
      }
    }
    String[] arrayOfString = new String[7];
    arrayOfString[0] = paramString;
    for (int i = 1; i <= 6; i++) {
      arrayOfString[i] = ((String)localLocaleServiceProviderPool.getLocalizedObject(TimeZoneNameGetter.INSTANCE, paramLocale, i < 5 ? "dst" : i < 3 ? "std" : "generic", new Object[] { Integer.valueOf(i % 2), paramString }));
    }
    if (Objects.isNull(localObject)) {
      localObject = new ConcurrentHashMap();
    }
    ((Map)localObject).put(paramLocale, arrayOfString);
    localSoftReference = new SoftReference(localObject);
    cachedDisplayNames.put(paramString, localSoftReference);
    return arrayOfString;
  }
  
  private TimeZoneNameUtility() {}
  
  private static class TimeZoneNameGetter
    implements LocaleServiceProviderPool.LocalizedObjectGetter<TimeZoneNameProvider, String>
  {
    private static final TimeZoneNameGetter INSTANCE = new TimeZoneNameGetter();
    
    private TimeZoneNameGetter() {}
    
    public String getObject(TimeZoneNameProvider paramTimeZoneNameProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
    {
      assert (paramVarArgs.length == 2);
      int i = ((Integer)paramVarArgs[0]).intValue();
      String str1 = (String)paramVarArgs[1];
      String str2 = getName(paramTimeZoneNameProvider, paramLocale, paramString, i, str1);
      if (str2 == null)
      {
        Map localMap = ZoneInfo.getAliasTable();
        if (localMap != null)
        {
          String str3 = (String)localMap.get(str1);
          if (str3 != null) {
            str2 = getName(paramTimeZoneNameProvider, paramLocale, paramString, i, str3);
          }
          if (str2 == null) {
            str2 = examineAliases(paramTimeZoneNameProvider, paramLocale, paramString, str3 != null ? str3 : str1, i, localMap);
          }
        }
      }
      return str2;
    }
    
    private static String examineAliases(TimeZoneNameProvider paramTimeZoneNameProvider, Locale paramLocale, String paramString1, String paramString2, int paramInt, Map<String, String> paramMap)
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (((String)localEntry.getValue()).equals(paramString2))
        {
          String str1 = (String)localEntry.getKey();
          String str2 = getName(paramTimeZoneNameProvider, paramLocale, paramString1, paramInt, str1);
          if (str2 != null) {
            return str2;
          }
          str2 = examineAliases(paramTimeZoneNameProvider, paramLocale, paramString1, str1, paramInt, paramMap);
          if (str2 != null) {
            return str2;
          }
        }
      }
      return null;
    }
    
    private static String getName(TimeZoneNameProvider paramTimeZoneNameProvider, Locale paramLocale, String paramString1, int paramInt, String paramString2)
    {
      String str1 = null;
      switch (paramString1)
      {
      case "std": 
        str1 = paramTimeZoneNameProvider.getDisplayName(paramString2, false, paramInt, paramLocale);
        break;
      case "dst": 
        str1 = paramTimeZoneNameProvider.getDisplayName(paramString2, true, paramInt, paramLocale);
        break;
      case "generic": 
        str1 = paramTimeZoneNameProvider.getGenericDisplayName(paramString2, paramInt, paramLocale);
      }
      return str1;
    }
  }
}
