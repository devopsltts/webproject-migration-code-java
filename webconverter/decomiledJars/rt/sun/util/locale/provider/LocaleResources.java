package sun.util.locale.provider;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.calendar.ZoneInfo;
import sun.util.resources.LocaleData;
import sun.util.resources.OpenListResourceBundle;
import sun.util.resources.ParallelListResourceBundle;
import sun.util.resources.TimeZoneNamesBundle;

public class LocaleResources
{
  private final Locale locale;
  private final LocaleData localeData;
  private final LocaleProviderAdapter.Type type;
  private ConcurrentMap<String, ResourceReference> cache = new ConcurrentHashMap();
  private ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
  private static final String BREAK_ITERATOR_INFO = "BII.";
  private static final String CALENDAR_DATA = "CALD.";
  private static final String COLLATION_DATA_CACHEKEY = "COLD";
  private static final String DECIMAL_FORMAT_SYMBOLS_DATA_CACHEKEY = "DFSD";
  private static final String CURRENCY_NAMES = "CN.";
  private static final String LOCALE_NAMES = "LN.";
  private static final String TIME_ZONE_NAMES = "TZN.";
  private static final String ZONE_IDS_CACHEKEY = "ZID";
  private static final String CALENDAR_NAMES = "CALN.";
  private static final String NUMBER_PATTERNS_CACHEKEY = "NP";
  private static final String DATE_TIME_PATTERN = "DTP.";
  private static final Object NULLOBJECT = new Object();
  
  LocaleResources(ResourceBundleBasedAdapter paramResourceBundleBasedAdapter, Locale paramLocale)
  {
    this.locale = paramLocale;
    this.localeData = paramResourceBundleBasedAdapter.getLocaleData();
    this.type = ((LocaleProviderAdapter)paramResourceBundleBasedAdapter).getAdapterType();
  }
  
  private void removeEmptyReferences()
  {
    Reference localReference;
    while ((localReference = this.referenceQueue.poll()) != null) {
      this.cache.remove(((ResourceReference)localReference).getCacheKey());
    }
  }
  
  Object getBreakIteratorInfo(String paramString)
  {
    String str = "BII." + paramString;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
    Object localObject;
    if ((localResourceReference == null) || ((localObject = localResourceReference.get()) == null))
    {
      localObject = this.localeData.getBreakIteratorInfo(this.locale).getObject(paramString);
      this.cache.put(str, new ResourceReference(str, localObject, this.referenceQueue));
    }
    return localObject;
  }
  
  int getCalendarData(String paramString)
  {
    String str = "CALD." + paramString;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
    Integer localInteger;
    if ((localResourceReference == null) || ((localInteger = (Integer)localResourceReference.get()) == null))
    {
      ResourceBundle localResourceBundle = this.localeData.getCalendarData(this.locale);
      if (localResourceBundle.containsKey(paramString)) {
        localInteger = Integer.valueOf(Integer.parseInt(localResourceBundle.getString(paramString)));
      } else {
        localInteger = Integer.valueOf(0);
      }
      this.cache.put(str, new ResourceReference(str, localInteger, this.referenceQueue));
    }
    return localInteger.intValue();
  }
  
  public String getCollationData()
  {
    String str1 = "Rule";
    String str2 = "";
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get("COLD");
    if ((localResourceReference == null) || ((str2 = (String)localResourceReference.get()) == null))
    {
      ResourceBundle localResourceBundle = this.localeData.getCollationData(this.locale);
      if (localResourceBundle.containsKey(str1)) {
        str2 = localResourceBundle.getString(str1);
      }
      this.cache.put("COLD", new ResourceReference("COLD", str2, this.referenceQueue));
    }
    return str2;
  }
  
  public Object[] getDecimalFormatSymbolsData()
  {
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get("DFSD");
    Object[] arrayOfObject;
    if ((localResourceReference == null) || ((arrayOfObject = (Object[])localResourceReference.get()) == null))
    {
      ResourceBundle localResourceBundle = this.localeData.getNumberFormatData(this.locale);
      arrayOfObject = new Object[3];
      String str2 = this.locale.getUnicodeLocaleType("nu");
      String str1;
      if (str2 != null)
      {
        str1 = str2 + ".NumberElements";
        if (localResourceBundle.containsKey(str1)) {
          arrayOfObject[0] = localResourceBundle.getStringArray(str1);
        }
      }
      if ((arrayOfObject[0] == null) && (localResourceBundle.containsKey("DefaultNumberingSystem")))
      {
        str1 = localResourceBundle.getString("DefaultNumberingSystem") + ".NumberElements";
        if (localResourceBundle.containsKey(str1)) {
          arrayOfObject[0] = localResourceBundle.getStringArray(str1);
        }
      }
      if (arrayOfObject[0] == null) {
        arrayOfObject[0] = localResourceBundle.getStringArray("NumberElements");
      }
      this.cache.put("DFSD", new ResourceReference("DFSD", arrayOfObject, this.referenceQueue));
    }
    return arrayOfObject;
  }
  
  public String getCurrencyName(String paramString)
  {
    Object localObject = null;
    String str = "CN." + paramString;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
    if ((localResourceReference != null) && ((localObject = localResourceReference.get()) != null))
    {
      if (localObject.equals(NULLOBJECT)) {
        localObject = null;
      }
      return (String)localObject;
    }
    OpenListResourceBundle localOpenListResourceBundle = this.localeData.getCurrencyNames(this.locale);
    if (localOpenListResourceBundle.containsKey(paramString))
    {
      localObject = localOpenListResourceBundle.getObject(paramString);
      this.cache.put(str, new ResourceReference(str, localObject, this.referenceQueue));
    }
    return (String)localObject;
  }
  
  public String getLocaleName(String paramString)
  {
    Object localObject = null;
    String str = "LN." + paramString;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
    if ((localResourceReference != null) && ((localObject = localResourceReference.get()) != null))
    {
      if (localObject.equals(NULLOBJECT)) {
        localObject = null;
      }
      return (String)localObject;
    }
    OpenListResourceBundle localOpenListResourceBundle = this.localeData.getLocaleNames(this.locale);
    if (localOpenListResourceBundle.containsKey(paramString))
    {
      localObject = localOpenListResourceBundle.getObject(paramString);
      this.cache.put(str, new ResourceReference(str, localObject, this.referenceQueue));
    }
    return (String)localObject;
  }
  
  String[] getTimeZoneNames(String paramString)
  {
    String[] arrayOfString = null;
    String str = "TZN.." + paramString;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
    if ((Objects.isNull(localResourceReference)) || (Objects.isNull(arrayOfString = (String[])localResourceReference.get())))
    {
      TimeZoneNamesBundle localTimeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
      if (localTimeZoneNamesBundle.containsKey(paramString))
      {
        arrayOfString = localTimeZoneNamesBundle.getStringArray(paramString);
        this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
      }
    }
    return arrayOfString;
  }
  
  Set<String> getZoneIDs()
  {
    Set localSet = null;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get("ZID");
    if ((localResourceReference == null) || ((localSet = (Set)localResourceReference.get()) == null))
    {
      TimeZoneNamesBundle localTimeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
      localSet = localTimeZoneNamesBundle.keySet();
      this.cache.put("ZID", new ResourceReference("ZID", localSet, this.referenceQueue));
    }
    return localSet;
  }
  
  String[][] getZoneStrings()
  {
    TimeZoneNamesBundle localTimeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
    Set localSet = getZoneIDs();
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    Object localObject1 = localSet.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      localLinkedHashSet.add(localTimeZoneNamesBundle.getStringArray((String)localObject2));
    }
    if (this.type == LocaleProviderAdapter.Type.CLDR)
    {
      localObject1 = ZoneInfo.getAliasTable();
      localObject2 = ((Map)localObject1).keySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        String str1 = (String)((Iterator)localObject2).next();
        if (!localSet.contains(str1))
        {
          String str2 = (String)((Map)localObject1).get(str1);
          if (localSet.contains(str2))
          {
            String[] arrayOfString = localTimeZoneNamesBundle.getStringArray(str2);
            arrayOfString[0] = str1;
            localLinkedHashSet.add(arrayOfString);
          }
        }
      }
    }
    return (String[][])localLinkedHashSet.toArray(new String[0][]);
  }
  
  String[] getCalendarNames(String paramString)
  {
    String[] arrayOfString = null;
    String str = "CALN." + paramString;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
    if ((localResourceReference == null) || ((arrayOfString = (String[])localResourceReference.get()) == null))
    {
      ResourceBundle localResourceBundle = this.localeData.getDateFormatData(this.locale);
      if (localResourceBundle.containsKey(paramString))
      {
        arrayOfString = localResourceBundle.getStringArray(paramString);
        this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
      }
    }
    return arrayOfString;
  }
  
  String[] getJavaTimeNames(String paramString)
  {
    String[] arrayOfString = null;
    String str = "CALN." + paramString;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
    if ((localResourceReference == null) || ((arrayOfString = (String[])localResourceReference.get()) == null))
    {
      ResourceBundle localResourceBundle = getJavaTimeFormatData();
      if (localResourceBundle.containsKey(paramString))
      {
        arrayOfString = localResourceBundle.getStringArray(paramString);
        this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
      }
    }
    return arrayOfString;
  }
  
  public String getDateTimePattern(int paramInt1, int paramInt2, Calendar paramCalendar)
  {
    if (paramCalendar == null) {
      paramCalendar = Calendar.getInstance(this.locale);
    }
    return getDateTimePattern(null, paramInt1, paramInt2, paramCalendar.getCalendarType());
  }
  
  public String getJavaTimeDateTimePattern(int paramInt1, int paramInt2, String paramString)
  {
    paramString = CalendarDataUtility.normalizeCalendarType(paramString);
    String str = getDateTimePattern("java.time.", paramInt1, paramInt2, paramString);
    if (str == null) {
      str = getDateTimePattern(null, paramInt1, paramInt2, paramString);
    }
    return str;
  }
  
  private String getDateTimePattern(String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    String str2 = null;
    String str3 = null;
    if (paramInt1 >= 0)
    {
      if (paramString1 != null) {
        str2 = getDateTimePattern(paramString1, "TimePatterns", paramInt1, paramString2);
      }
      if (str2 == null) {
        str2 = getDateTimePattern(null, "TimePatterns", paramInt1, paramString2);
      }
    }
    if (paramInt2 >= 0)
    {
      if (paramString1 != null) {
        str3 = getDateTimePattern(paramString1, "DatePatterns", paramInt2, paramString2);
      }
      if (str3 == null) {
        str3 = getDateTimePattern(null, "DatePatterns", paramInt2, paramString2);
      }
    }
    String str1;
    if (paramInt1 >= 0)
    {
      if (paramInt2 >= 0)
      {
        String str4 = null;
        if (paramString1 != null) {
          str4 = getDateTimePattern(paramString1, "DateTimePatterns", 0, paramString2);
        }
        if (str4 == null) {
          str4 = getDateTimePattern(null, "DateTimePatterns", 0, paramString2);
        }
        switch (str4)
        {
        case "{1} {0}": 
          str1 = str3 + " " + str2;
          break;
        case "{0} {1}": 
          str1 = str2 + " " + str3;
          break;
        default: 
          str1 = MessageFormat.format(str4, new Object[] { str2, str3 });
        }
      }
      else
      {
        str1 = str2;
      }
    }
    else if (paramInt2 >= 0) {
      str1 = str3;
    } else {
      throw new IllegalArgumentException("No date or time style specified");
    }
    return str1;
  }
  
  public String[] getNumberPatterns()
  {
    String[] arrayOfString = null;
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get("NP");
    if ((localResourceReference == null) || ((arrayOfString = (String[])localResourceReference.get()) == null))
    {
      ResourceBundle localResourceBundle = this.localeData.getNumberFormatData(this.locale);
      arrayOfString = localResourceBundle.getStringArray("NumberPatterns");
      this.cache.put("NP", new ResourceReference("NP", arrayOfString, this.referenceQueue));
    }
    return arrayOfString;
  }
  
  public ResourceBundle getJavaTimeFormatData()
  {
    ResourceBundle localResourceBundle = this.localeData.getDateFormatData(this.locale);
    if ((localResourceBundle instanceof ParallelListResourceBundle)) {
      this.localeData.setSupplementary((ParallelListResourceBundle)localResourceBundle);
    }
    return localResourceBundle;
  }
  
  private String getDateTimePattern(String paramString1, String paramString2, int paramInt, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramString1 != null) {
      localStringBuilder.append(paramString1);
    }
    if (!"gregory".equals(paramString3)) {
      localStringBuilder.append(paramString3).append('.');
    }
    localStringBuilder.append(paramString2);
    String str1 = localStringBuilder.toString();
    String str2 = localStringBuilder.insert(0, "DTP.").toString();
    removeEmptyReferences();
    ResourceReference localResourceReference = (ResourceReference)this.cache.get(str2);
    Object localObject = NULLOBJECT;
    if ((localResourceReference == null) || ((localObject = localResourceReference.get()) == null))
    {
      ResourceBundle localResourceBundle = paramString1 != null ? getJavaTimeFormatData() : this.localeData.getDateFormatData(this.locale);
      if (localResourceBundle.containsKey(str1))
      {
        localObject = localResourceBundle.getStringArray(str1);
      }
      else
      {
        assert (!str1.equals(paramString2));
        if (localResourceBundle.containsKey(paramString2)) {
          localObject = localResourceBundle.getStringArray(paramString2);
        }
      }
      this.cache.put(str2, new ResourceReference(str2, localObject, this.referenceQueue));
    }
    if (localObject == NULLOBJECT)
    {
      assert (paramString1 != null);
      return null;
    }
    return ((String[])(String[])localObject)[paramInt];
  }
  
  private static class ResourceReference
    extends SoftReference<Object>
  {
    private final String cacheKey;
    
    ResourceReference(String paramString, Object paramObject, ReferenceQueue<Object> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      this.cacheKey = paramString;
    }
    
    String getCacheKey()
    {
      return this.cacheKey;
    }
  }
}
