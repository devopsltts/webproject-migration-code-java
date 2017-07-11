package sun.util.locale.provider;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.resources.LocaleData;
import sun.util.spi.CalendarProvider;

public class JRELocaleProviderAdapter
  extends LocaleProviderAdapter
  implements ResourceBundleBasedAdapter
{
  private static final String LOCALE_DATA_JAR_NAME = "localedata.jar";
  private final ConcurrentMap<String, Set<String>> langtagSets = new ConcurrentHashMap();
  private final ConcurrentMap<Locale, LocaleResources> localeResourcesMap = new ConcurrentHashMap();
  private volatile LocaleData localeData;
  private volatile BreakIteratorProvider breakIteratorProvider = null;
  private volatile CollatorProvider collatorProvider = null;
  private volatile DateFormatProvider dateFormatProvider = null;
  private volatile DateFormatSymbolsProvider dateFormatSymbolsProvider = null;
  private volatile DecimalFormatSymbolsProvider decimalFormatSymbolsProvider = null;
  private volatile NumberFormatProvider numberFormatProvider = null;
  private volatile CurrencyNameProvider currencyNameProvider = null;
  private volatile LocaleNameProvider localeNameProvider = null;
  private volatile TimeZoneNameProvider timeZoneNameProvider = null;
  private volatile CalendarDataProvider calendarDataProvider = null;
  private volatile CalendarNameProvider calendarNameProvider = null;
  private volatile CalendarProvider calendarProvider = null;
  private static volatile Boolean isNonENSupported = null;
  
  public JRELocaleProviderAdapter() {}
  
  public LocaleProviderAdapter.Type getAdapterType()
  {
    return LocaleProviderAdapter.Type.JRE;
  }
  
  public <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass)
  {
    switch (paramClass.getSimpleName())
    {
    case "BreakIteratorProvider": 
      return getBreakIteratorProvider();
    case "CollatorProvider": 
      return getCollatorProvider();
    case "DateFormatProvider": 
      return getDateFormatProvider();
    case "DateFormatSymbolsProvider": 
      return getDateFormatSymbolsProvider();
    case "DecimalFormatSymbolsProvider": 
      return getDecimalFormatSymbolsProvider();
    case "NumberFormatProvider": 
      return getNumberFormatProvider();
    case "CurrencyNameProvider": 
      return getCurrencyNameProvider();
    case "LocaleNameProvider": 
      return getLocaleNameProvider();
    case "TimeZoneNameProvider": 
      return getTimeZoneNameProvider();
    case "CalendarDataProvider": 
      return getCalendarDataProvider();
    case "CalendarNameProvider": 
      return getCalendarNameProvider();
    case "CalendarProvider": 
      return getCalendarProvider();
    }
    throw new InternalError("should not come down here");
  }
  
  public BreakIteratorProvider getBreakIteratorProvider()
  {
    if (this.breakIteratorProvider == null)
    {
      BreakIteratorProviderImpl localBreakIteratorProviderImpl = new BreakIteratorProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (this.breakIteratorProvider == null) {
          this.breakIteratorProvider = localBreakIteratorProviderImpl;
        }
      }
    }
    return this.breakIteratorProvider;
  }
  
  public CollatorProvider getCollatorProvider()
  {
    if (this.collatorProvider == null)
    {
      CollatorProviderImpl localCollatorProviderImpl = new CollatorProviderImpl(getAdapterType(), getLanguageTagSet("CollationData"));
      synchronized (this)
      {
        if (this.collatorProvider == null) {
          this.collatorProvider = localCollatorProviderImpl;
        }
      }
    }
    return this.collatorProvider;
  }
  
  public DateFormatProvider getDateFormatProvider()
  {
    if (this.dateFormatProvider == null)
    {
      DateFormatProviderImpl localDateFormatProviderImpl = new DateFormatProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (this.dateFormatProvider == null) {
          this.dateFormatProvider = localDateFormatProviderImpl;
        }
      }
    }
    return this.dateFormatProvider;
  }
  
  public DateFormatSymbolsProvider getDateFormatSymbolsProvider()
  {
    if (this.dateFormatSymbolsProvider == null)
    {
      DateFormatSymbolsProviderImpl localDateFormatSymbolsProviderImpl = new DateFormatSymbolsProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (this.dateFormatSymbolsProvider == null) {
          this.dateFormatSymbolsProvider = localDateFormatSymbolsProviderImpl;
        }
      }
    }
    return this.dateFormatSymbolsProvider;
  }
  
  public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider()
  {
    if (this.decimalFormatSymbolsProvider == null)
    {
      DecimalFormatSymbolsProviderImpl localDecimalFormatSymbolsProviderImpl = new DecimalFormatSymbolsProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (this.decimalFormatSymbolsProvider == null) {
          this.decimalFormatSymbolsProvider = localDecimalFormatSymbolsProviderImpl;
        }
      }
    }
    return this.decimalFormatSymbolsProvider;
  }
  
  public NumberFormatProvider getNumberFormatProvider()
  {
    if (this.numberFormatProvider == null)
    {
      NumberFormatProviderImpl localNumberFormatProviderImpl = new NumberFormatProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (this.numberFormatProvider == null) {
          this.numberFormatProvider = localNumberFormatProviderImpl;
        }
      }
    }
    return this.numberFormatProvider;
  }
  
  public CurrencyNameProvider getCurrencyNameProvider()
  {
    if (this.currencyNameProvider == null)
    {
      CurrencyNameProviderImpl localCurrencyNameProviderImpl = new CurrencyNameProviderImpl(getAdapterType(), getLanguageTagSet("CurrencyNames"));
      synchronized (this)
      {
        if (this.currencyNameProvider == null) {
          this.currencyNameProvider = localCurrencyNameProviderImpl;
        }
      }
    }
    return this.currencyNameProvider;
  }
  
  public LocaleNameProvider getLocaleNameProvider()
  {
    if (this.localeNameProvider == null)
    {
      LocaleNameProviderImpl localLocaleNameProviderImpl = new LocaleNameProviderImpl(getAdapterType(), getLanguageTagSet("LocaleNames"));
      synchronized (this)
      {
        if (this.localeNameProvider == null) {
          this.localeNameProvider = localLocaleNameProviderImpl;
        }
      }
    }
    return this.localeNameProvider;
  }
  
  public TimeZoneNameProvider getTimeZoneNameProvider()
  {
    if (this.timeZoneNameProvider == null)
    {
      TimeZoneNameProviderImpl localTimeZoneNameProviderImpl = new TimeZoneNameProviderImpl(getAdapterType(), getLanguageTagSet("TimeZoneNames"));
      synchronized (this)
      {
        if (this.timeZoneNameProvider == null) {
          this.timeZoneNameProvider = localTimeZoneNameProviderImpl;
        }
      }
    }
    return this.timeZoneNameProvider;
  }
  
  public CalendarDataProvider getCalendarDataProvider()
  {
    if (this.calendarDataProvider == null)
    {
      CalendarDataProviderImpl localCalendarDataProviderImpl = new CalendarDataProviderImpl(getAdapterType(), getLanguageTagSet("CalendarData"));
      synchronized (this)
      {
        if (this.calendarDataProvider == null) {
          this.calendarDataProvider = localCalendarDataProviderImpl;
        }
      }
    }
    return this.calendarDataProvider;
  }
  
  public CalendarNameProvider getCalendarNameProvider()
  {
    if (this.calendarNameProvider == null)
    {
      CalendarNameProviderImpl localCalendarNameProviderImpl = new CalendarNameProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (this.calendarNameProvider == null) {
          this.calendarNameProvider = localCalendarNameProviderImpl;
        }
      }
    }
    return this.calendarNameProvider;
  }
  
  public CalendarProvider getCalendarProvider()
  {
    if (this.calendarProvider == null)
    {
      CalendarProviderImpl localCalendarProviderImpl = new CalendarProviderImpl(getAdapterType(), getLanguageTagSet("CalendarData"));
      synchronized (this)
      {
        if (this.calendarProvider == null) {
          this.calendarProvider = localCalendarProviderImpl;
        }
      }
    }
    return this.calendarProvider;
  }
  
  public LocaleResources getLocaleResources(Locale paramLocale)
  {
    Object localObject = (LocaleResources)this.localeResourcesMap.get(paramLocale);
    if (localObject == null)
    {
      localObject = new LocaleResources(this, paramLocale);
      LocaleResources localLocaleResources = (LocaleResources)this.localeResourcesMap.putIfAbsent(paramLocale, localObject);
      if (localLocaleResources != null) {
        localObject = localLocaleResources;
      }
    }
    return localObject;
  }
  
  public LocaleData getLocaleData()
  {
    if (this.localeData == null) {
      synchronized (this)
      {
        if (this.localeData == null) {
          this.localeData = new LocaleData(getAdapterType());
        }
      }
    }
    return this.localeData;
  }
  
  public Locale[] getAvailableLocales()
  {
    return (Locale[])AvailableJRELocales.localeList.clone();
  }
  
  public Set<String> getLanguageTagSet(String paramString)
  {
    Object localObject = (Set)this.langtagSets.get(paramString);
    if (localObject == null)
    {
      localObject = createLanguageTagSet(paramString);
      Set localSet = (Set)this.langtagSets.putIfAbsent(paramString, localObject);
      if (localSet != null) {
        localObject = localSet;
      }
    }
    return localObject;
  }
  
  protected Set<String> createLanguageTagSet(String paramString)
  {
    String str1 = LocaleDataMetaInfo.getSupportedLocaleString(paramString);
    if (str1 == null) {
      return Collections.emptySet();
    }
    HashSet localHashSet = new HashSet();
    StringTokenizer localStringTokenizer = new StringTokenizer(str1);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str2 = localStringTokenizer.nextToken();
      if (str2.equals("|")) {
        if (!isNonENLangSupported()) {
          break;
        }
      } else {
        localHashSet.add(str2);
      }
    }
    return localHashSet;
  }
  
  private static Locale[] createAvailableLocales()
  {
    String str1 = LocaleDataMetaInfo.getSupportedLocaleString("AvailableLocales");
    if (str1.length() == 0) {
      throw new InternalError("No available locales for JRE");
    }
    int i = str1.indexOf('|');
    StringTokenizer localStringTokenizer;
    if (isNonENLangSupported()) {
      localStringTokenizer = new StringTokenizer(str1.substring(0, i) + str1.substring(i + 1));
    } else {
      localStringTokenizer = new StringTokenizer(str1.substring(0, i));
    }
    int j = localStringTokenizer.countTokens();
    Locale[] arrayOfLocale = new Locale[j + 1];
    arrayOfLocale[0] = Locale.ROOT;
    for (int k = 1; k <= j; k++)
    {
      String str2 = localStringTokenizer.nextToken();
      switch (str2)
      {
      case "ja-JP-JP": 
        arrayOfLocale[k] = JRELocaleConstants.JA_JP_JP;
        break;
      case "no-NO-NY": 
        arrayOfLocale[k] = JRELocaleConstants.NO_NO_NY;
        break;
      case "th-TH-TH": 
        arrayOfLocale[k] = JRELocaleConstants.TH_TH_TH;
        break;
      default: 
        arrayOfLocale[k] = Locale.forLanguageTag(str2);
      }
    }
    return arrayOfLocale;
  }
  
  private static boolean isNonENLangSupported()
  {
    if (isNonENSupported == null) {
      synchronized (JRELocaleProviderAdapter.class)
      {
        if (isNonENSupported == null)
        {
          String str1 = File.separator;
          String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + str1 + "lib" + str1 + "ext" + str1 + "localedata.jar";
          File localFile = new File(str2);
          isNonENSupported = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
          {
            public Boolean run()
            {
              return Boolean.valueOf(this.val$f.exists());
            }
          });
        }
      }
    }
    return isNonENSupported.booleanValue();
  }
  
  private static class AvailableJRELocales
  {
    private static final Locale[] localeList = ;
    
    private AvailableJRELocales() {}
  }
}
