package java.time.format;

import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

class DateTimeTextProvider
{
  private static final ConcurrentMap<Map.Entry<TemporalField, Locale>, Object> CACHE = new ConcurrentHashMap(16, 0.75F, 2);
  private static final Comparator<Map.Entry<String, Long>> COMPARATOR = new Comparator()
  {
    public int compare(Map.Entry<String, Long> paramAnonymousEntry1, Map.Entry<String, Long> paramAnonymousEntry2)
    {
      return ((String)paramAnonymousEntry2.getKey()).length() - ((String)paramAnonymousEntry1.getKey()).length();
    }
  };
  
  DateTimeTextProvider() {}
  
  static DateTimeTextProvider getInstance()
  {
    return new DateTimeTextProvider();
  }
  
  public String getText(TemporalField paramTemporalField, long paramLong, TextStyle paramTextStyle, Locale paramLocale)
  {
    Object localObject = findStore(paramTemporalField, paramLocale);
    if ((localObject instanceof LocaleStore)) {
      return ((LocaleStore)localObject).getText(paramLong, paramTextStyle);
    }
    return null;
  }
  
  public String getText(Chronology paramChronology, TemporalField paramTemporalField, long paramLong, TextStyle paramTextStyle, Locale paramLocale)
  {
    if ((paramChronology == IsoChronology.INSTANCE) || (!(paramTemporalField instanceof ChronoField))) {
      return getText(paramTemporalField, paramLong, paramTextStyle, paramLocale);
    }
    int i;
    int j;
    if (paramTemporalField == ChronoField.ERA)
    {
      i = 0;
      if (paramChronology == JapaneseChronology.INSTANCE)
      {
        if (paramLong == -999L) {
          j = 0;
        } else {
          j = (int)paramLong + 2;
        }
      }
      else {
        j = (int)paramLong;
      }
    }
    else if (paramTemporalField == ChronoField.MONTH_OF_YEAR)
    {
      i = 2;
      j = (int)paramLong - 1;
    }
    else if (paramTemporalField == ChronoField.DAY_OF_WEEK)
    {
      i = 7;
      j = (int)paramLong + 1;
      if (j > 7) {
        j = 1;
      }
    }
    else if (paramTemporalField == ChronoField.AMPM_OF_DAY)
    {
      i = 9;
      j = (int)paramLong;
    }
    else
    {
      return null;
    }
    return CalendarDataUtility.retrieveJavaTimeFieldValueName(paramChronology.getCalendarType(), i, j, paramTextStyle.toCalendarStyle(), paramLocale);
  }
  
  public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField paramTemporalField, TextStyle paramTextStyle, Locale paramLocale)
  {
    Object localObject = findStore(paramTemporalField, paramLocale);
    if ((localObject instanceof LocaleStore)) {
      return ((LocaleStore)localObject).getTextIterator(paramTextStyle);
    }
    return null;
  }
  
  public Iterator<Map.Entry<String, Long>> getTextIterator(Chronology paramChronology, TemporalField paramTemporalField, TextStyle paramTextStyle, Locale paramLocale)
  {
    if ((paramChronology == IsoChronology.INSTANCE) || (!(paramTemporalField instanceof ChronoField))) {
      return getTextIterator(paramTemporalField, paramTextStyle, paramLocale);
    }
    int i;
    switch (2.$SwitchMap$java$time$temporal$ChronoField[((ChronoField)paramTemporalField).ordinal()])
    {
    case 1: 
      i = 0;
      break;
    case 2: 
      i = 2;
      break;
    case 3: 
      i = 7;
      break;
    case 4: 
      i = 9;
      break;
    default: 
      return null;
    }
    int j = paramTextStyle == null ? 0 : paramTextStyle.toCalendarStyle();
    Map localMap = CalendarDataUtility.retrieveJavaTimeFieldValueNames(paramChronology.getCalendarType(), i, j, paramLocale);
    if (localMap == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(localMap.size());
    Iterator localIterator;
    Map.Entry localEntry;
    switch (i)
    {
    case 0: 
      localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        int k = ((Integer)localEntry.getValue()).intValue();
        if (paramChronology == JapaneseChronology.INSTANCE) {
          if (k == 0) {
            k = 64537;
          } else {
            k -= 2;
          }
        }
        localArrayList.add(createEntry(localEntry.getKey(), Long.valueOf(k)));
      }
      break;
    case 2: 
      localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        localArrayList.add(createEntry(localEntry.getKey(), Long.valueOf(((Integer)localEntry.getValue()).intValue() + 1)));
      }
      break;
    case 7: 
      localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        localArrayList.add(createEntry(localEntry.getKey(), Long.valueOf(toWeekDay(((Integer)localEntry.getValue()).intValue()))));
      }
      break;
    default: 
      localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        localArrayList.add(createEntry(localEntry.getKey(), Long.valueOf(((Integer)localEntry.getValue()).intValue())));
      }
    }
    return localArrayList.iterator();
  }
  
  private Object findStore(TemporalField paramTemporalField, Locale paramLocale)
  {
    Map.Entry localEntry = createEntry(paramTemporalField, paramLocale);
    Object localObject = CACHE.get(localEntry);
    if (localObject == null)
    {
      localObject = createStore(paramTemporalField, paramLocale);
      CACHE.putIfAbsent(localEntry, localObject);
      localObject = CACHE.get(localEntry);
    }
    return localObject;
  }
  
  private static int toWeekDay(int paramInt)
  {
    if (paramInt == 1) {
      return 7;
    }
    return paramInt - 1;
  }
  
  private Object createStore(TemporalField paramTemporalField, Locale paramLocale)
  {
    HashMap localHashMap1 = new HashMap();
    Object localObject2;
    Map localMap;
    HashMap localHashMap2;
    Iterator localIterator1;
    Object localObject3;
    if (paramTemporalField == ChronoField.ERA)
    {
      for (localObject2 : TextStyle.values()) {
        if (!((TextStyle)localObject2).isStandalone())
        {
          localMap = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 0, ((TextStyle)localObject2).toCalendarStyle(), paramLocale);
          if (localMap != null)
          {
            localHashMap2 = new HashMap();
            localIterator1 = localMap.entrySet().iterator();
            while (localIterator1.hasNext())
            {
              localObject3 = (Map.Entry)localIterator1.next();
              localHashMap2.put(Long.valueOf(((Integer)((Map.Entry)localObject3).getValue()).intValue()), ((Map.Entry)localObject3).getKey());
            }
            if (!localHashMap2.isEmpty()) {
              localHashMap1.put(localObject2, localHashMap2);
            }
          }
        }
      }
      return new LocaleStore(localHashMap1);
    }
    if (paramTemporalField == ChronoField.MONTH_OF_YEAR)
    {
      for (localObject2 : TextStyle.values())
      {
        localMap = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 2, ((TextStyle)localObject2).toCalendarStyle(), paramLocale);
        localHashMap2 = new HashMap();
        if (localMap != null)
        {
          localIterator1 = localMap.entrySet().iterator();
          while (localIterator1.hasNext())
          {
            localObject3 = (Map.Entry)localIterator1.next();
            localHashMap2.put(Long.valueOf(((Integer)((Map.Entry)localObject3).getValue()).intValue() + 1), ((Map.Entry)localObject3).getKey());
          }
        }
        else
        {
          for (int m = 0; m <= 11; m++)
          {
            localObject3 = CalendarDataUtility.retrieveJavaTimeFieldValueName("gregory", 2, m, ((TextStyle)localObject2).toCalendarStyle(), paramLocale);
            if (localObject3 == null) {
              break;
            }
            localHashMap2.put(Long.valueOf(m + 1), localObject3);
          }
        }
        if (!localHashMap2.isEmpty()) {
          localHashMap1.put(localObject2, localHashMap2);
        }
      }
      return new LocaleStore(localHashMap1);
    }
    if (paramTemporalField == ChronoField.DAY_OF_WEEK)
    {
      for (localObject2 : TextStyle.values())
      {
        localMap = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 7, ((TextStyle)localObject2).toCalendarStyle(), paramLocale);
        localHashMap2 = new HashMap();
        if (localMap != null)
        {
          Iterator localIterator2 = localMap.entrySet().iterator();
          while (localIterator2.hasNext())
          {
            localObject3 = (Map.Entry)localIterator2.next();
            localHashMap2.put(Long.valueOf(toWeekDay(((Integer)((Map.Entry)localObject3).getValue()).intValue())), ((Map.Entry)localObject3).getKey());
          }
        }
        else
        {
          for (int n = 1; n <= 7; n++)
          {
            localObject3 = CalendarDataUtility.retrieveJavaTimeFieldValueName("gregory", 7, n, ((TextStyle)localObject2).toCalendarStyle(), paramLocale);
            if (localObject3 == null) {
              break;
            }
            localHashMap2.put(Long.valueOf(toWeekDay(n)), localObject3);
          }
        }
        if (!localHashMap2.isEmpty()) {
          localHashMap1.put(localObject2, localHashMap2);
        }
      }
      return new LocaleStore(localHashMap1);
    }
    if (paramTemporalField == ChronoField.AMPM_OF_DAY)
    {
      for (localObject2 : TextStyle.values()) {
        if (!((TextStyle)localObject2).isStandalone())
        {
          localMap = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 9, ((TextStyle)localObject2).toCalendarStyle(), paramLocale);
          if (localMap != null)
          {
            localHashMap2 = new HashMap();
            Iterator localIterator3 = localMap.entrySet().iterator();
            while (localIterator3.hasNext())
            {
              localObject3 = (Map.Entry)localIterator3.next();
              localHashMap2.put(Long.valueOf(((Integer)((Map.Entry)localObject3).getValue()).intValue()), ((Map.Entry)localObject3).getKey());
            }
            if (!localHashMap2.isEmpty()) {
              localHashMap1.put(localObject2, localHashMap2);
            }
          }
        }
      }
      return new LocaleStore(localHashMap1);
    }
    if (paramTemporalField == IsoFields.QUARTER_OF_YEAR)
    {
      ??? = new String[] { "QuarterNames", "standalone.QuarterNames", "QuarterAbbreviations", "standalone.QuarterAbbreviations", "QuarterNarrows", "standalone.QuarterNarrows" };
      for (??? = 0; ??? < ???.length; ???++)
      {
        String[] arrayOfString = (String[])getLocalizedResource(???[???], paramLocale);
        if (arrayOfString != null)
        {
          localObject2 = new HashMap();
          for (int k = 0; k < arrayOfString.length; k++) {
            ((Map)localObject2).put(Long.valueOf(k + 1), arrayOfString[k]);
          }
          localHashMap1.put(TextStyle.values()[???], localObject2);
        }
      }
      return new LocaleStore(localHashMap1);
    }
    return "";
  }
  
  private static <A, B> Map.Entry<A, B> createEntry(A paramA, B paramB)
  {
    return new AbstractMap.SimpleImmutableEntry(paramA, paramB);
  }
  
  static <T> T getLocalizedResource(String paramString, Locale paramLocale)
  {
    LocaleResources localLocaleResources = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(paramLocale);
    ResourceBundle localResourceBundle = localLocaleResources.getJavaTimeFormatData();
    return localResourceBundle.containsKey(paramString) ? localResourceBundle.getObject(paramString) : null;
  }
  
  static final class LocaleStore
  {
    private final Map<TextStyle, Map<Long, String>> valueTextMap;
    private final Map<TextStyle, List<Map.Entry<String, Long>>> parsable;
    
    LocaleStore(Map<TextStyle, Map<Long, String>> paramMap)
    {
      this.valueTextMap = paramMap;
      HashMap localHashMap1 = new HashMap();
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry1 = (Map.Entry)localIterator.next();
        HashMap localHashMap2 = new HashMap();
        Object localObject = ((Map)localEntry1.getValue()).entrySet().iterator();
        while (((Iterator)localObject).hasNext())
        {
          Map.Entry localEntry2 = (Map.Entry)((Iterator)localObject).next();
          if (localHashMap2.put(localEntry2.getValue(), DateTimeTextProvider.createEntry(localEntry2.getValue(), localEntry2.getKey())) != null) {}
        }
        localObject = new ArrayList(localHashMap2.values());
        Collections.sort((List)localObject, DateTimeTextProvider.COMPARATOR);
        localHashMap1.put(localEntry1.getKey(), localObject);
        localArrayList.addAll((Collection)localObject);
        localHashMap1.put(null, localArrayList);
      }
      Collections.sort(localArrayList, DateTimeTextProvider.COMPARATOR);
      this.parsable = localHashMap1;
    }
    
    String getText(long paramLong, TextStyle paramTextStyle)
    {
      Map localMap = (Map)this.valueTextMap.get(paramTextStyle);
      return localMap != null ? (String)localMap.get(Long.valueOf(paramLong)) : null;
    }
    
    Iterator<Map.Entry<String, Long>> getTextIterator(TextStyle paramTextStyle)
    {
      List localList = (List)this.parsable.get(paramTextStyle);
      return localList != null ? localList.iterator() : null;
    }
  }
}
