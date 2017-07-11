package java.text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Arrays;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.locale.provider.TimeZoneNameUtility;
import sun.util.resources.LocaleData;

public class DateFormatSymbols
  implements Serializable, Cloneable
{
  String[] eras = null;
  String[] months = null;
  String[] shortMonths = null;
  String[] weekdays = null;
  String[] shortWeekdays = null;
  String[] ampms = null;
  String[][] zoneStrings = (String[][])null;
  transient boolean isZoneStringsSet = false;
  static final String patternChars = "GyMdkHmsSEDFwWahKzZYuXL";
  static final int PATTERN_ERA = 0;
  static final int PATTERN_YEAR = 1;
  static final int PATTERN_MONTH = 2;
  static final int PATTERN_DAY_OF_MONTH = 3;
  static final int PATTERN_HOUR_OF_DAY1 = 4;
  static final int PATTERN_HOUR_OF_DAY0 = 5;
  static final int PATTERN_MINUTE = 6;
  static final int PATTERN_SECOND = 7;
  static final int PATTERN_MILLISECOND = 8;
  static final int PATTERN_DAY_OF_WEEK = 9;
  static final int PATTERN_DAY_OF_YEAR = 10;
  static final int PATTERN_DAY_OF_WEEK_IN_MONTH = 11;
  static final int PATTERN_WEEK_OF_YEAR = 12;
  static final int PATTERN_WEEK_OF_MONTH = 13;
  static final int PATTERN_AM_PM = 14;
  static final int PATTERN_HOUR1 = 15;
  static final int PATTERN_HOUR0 = 16;
  static final int PATTERN_ZONE_NAME = 17;
  static final int PATTERN_ZONE_VALUE = 18;
  static final int PATTERN_WEEK_YEAR = 19;
  static final int PATTERN_ISO_DAY_OF_WEEK = 20;
  static final int PATTERN_ISO_ZONE = 21;
  static final int PATTERN_MONTH_STANDALONE = 22;
  String localPatternChars = null;
  Locale locale = null;
  static final long serialVersionUID = -5987973545549424702L;
  static final int millisPerHour = 3600000;
  private static final ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> cachedInstances = new ConcurrentHashMap(3);
  private transient int lastZoneIndex = 0;
  volatile transient int cachedHashCode = 0;
  
  public DateFormatSymbols()
  {
    initializeData(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public DateFormatSymbols(Locale paramLocale)
  {
    initializeData(paramLocale);
  }
  
  public static Locale[] getAvailableLocales()
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(DateFormatSymbolsProvider.class);
    return localLocaleServiceProviderPool.getAvailableLocales();
  }
  
  public static final DateFormatSymbols getInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public static final DateFormatSymbols getInstance(Locale paramLocale)
  {
    DateFormatSymbols localDateFormatSymbols = getProviderInstance(paramLocale);
    if (localDateFormatSymbols != null) {
      return localDateFormatSymbols;
    }
    throw new RuntimeException("DateFormatSymbols instance creation failed.");
  }
  
  static final DateFormatSymbols getInstanceRef(Locale paramLocale)
  {
    DateFormatSymbols localDateFormatSymbols = getProviderInstance(paramLocale);
    if (localDateFormatSymbols != null) {
      return localDateFormatSymbols;
    }
    throw new RuntimeException("DateFormatSymbols instance creation failed.");
  }
  
  private static DateFormatSymbols getProviderInstance(Locale paramLocale)
  {
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, paramLocale);
    DateFormatSymbolsProvider localDateFormatSymbolsProvider = localLocaleProviderAdapter.getDateFormatSymbolsProvider();
    DateFormatSymbols localDateFormatSymbols = localDateFormatSymbolsProvider.getInstance(paramLocale);
    if (localDateFormatSymbols == null)
    {
      localDateFormatSymbolsProvider = LocaleProviderAdapter.forJRE().getDateFormatSymbolsProvider();
      localDateFormatSymbols = localDateFormatSymbolsProvider.getInstance(paramLocale);
    }
    return localDateFormatSymbols;
  }
  
  public String[] getEras()
  {
    return (String[])Arrays.copyOf(this.eras, this.eras.length);
  }
  
  public void setEras(String[] paramArrayOfString)
  {
    this.eras = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    this.cachedHashCode = 0;
  }
  
  public String[] getMonths()
  {
    return (String[])Arrays.copyOf(this.months, this.months.length);
  }
  
  public void setMonths(String[] paramArrayOfString)
  {
    this.months = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    this.cachedHashCode = 0;
  }
  
  public String[] getShortMonths()
  {
    return (String[])Arrays.copyOf(this.shortMonths, this.shortMonths.length);
  }
  
  public void setShortMonths(String[] paramArrayOfString)
  {
    this.shortMonths = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    this.cachedHashCode = 0;
  }
  
  public String[] getWeekdays()
  {
    return (String[])Arrays.copyOf(this.weekdays, this.weekdays.length);
  }
  
  public void setWeekdays(String[] paramArrayOfString)
  {
    this.weekdays = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    this.cachedHashCode = 0;
  }
  
  public String[] getShortWeekdays()
  {
    return (String[])Arrays.copyOf(this.shortWeekdays, this.shortWeekdays.length);
  }
  
  public void setShortWeekdays(String[] paramArrayOfString)
  {
    this.shortWeekdays = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    this.cachedHashCode = 0;
  }
  
  public String[] getAmPmStrings()
  {
    return (String[])Arrays.copyOf(this.ampms, this.ampms.length);
  }
  
  public void setAmPmStrings(String[] paramArrayOfString)
  {
    this.ampms = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    this.cachedHashCode = 0;
  }
  
  public String[][] getZoneStrings()
  {
    return getZoneStringsImpl(true);
  }
  
  public void setZoneStrings(String[][] paramArrayOfString)
  {
    String[][] arrayOfString; = new String[paramArrayOfString.length][];
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      int j = paramArrayOfString[i].length;
      if (j < 5) {
        throw new IllegalArgumentException();
      }
      arrayOfString;[i] = ((String[])Arrays.copyOf(paramArrayOfString[i], j));
    }
    this.zoneStrings = arrayOfString;;
    this.isZoneStringsSet = true;
    this.cachedHashCode = 0;
  }
  
  public String getLocalPatternChars()
  {
    return this.localPatternChars;
  }
  
  public void setLocalPatternChars(String paramString)
  {
    this.localPatternChars = paramString.toString();
    this.cachedHashCode = 0;
  }
  
  public Object clone()
  {
    try
    {
      DateFormatSymbols localDateFormatSymbols = (DateFormatSymbols)super.clone();
      copyMembers(this, localDateFormatSymbols);
      return localDateFormatSymbols;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public int hashCode()
  {
    int i = this.cachedHashCode;
    if (i == 0)
    {
      i = 5;
      i = 11 * i + Arrays.hashCode(this.eras);
      i = 11 * i + Arrays.hashCode(this.months);
      i = 11 * i + Arrays.hashCode(this.shortMonths);
      i = 11 * i + Arrays.hashCode(this.weekdays);
      i = 11 * i + Arrays.hashCode(this.shortWeekdays);
      i = 11 * i + Arrays.hashCode(this.ampms);
      i = 11 * i + Arrays.deepHashCode(getZoneStringsWrapper());
      i = 11 * i + Objects.hashCode(this.localPatternChars);
      this.cachedHashCode = i;
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    DateFormatSymbols localDateFormatSymbols = (DateFormatSymbols)paramObject;
    return (Arrays.equals(this.eras, localDateFormatSymbols.eras)) && (Arrays.equals(this.months, localDateFormatSymbols.months)) && (Arrays.equals(this.shortMonths, localDateFormatSymbols.shortMonths)) && (Arrays.equals(this.weekdays, localDateFormatSymbols.weekdays)) && (Arrays.equals(this.shortWeekdays, localDateFormatSymbols.shortWeekdays)) && (Arrays.equals(this.ampms, localDateFormatSymbols.ampms)) && (Arrays.deepEquals(getZoneStringsWrapper(), localDateFormatSymbols.getZoneStringsWrapper())) && (((this.localPatternChars != null) && (this.localPatternChars.equals(localDateFormatSymbols.localPatternChars))) || ((this.localPatternChars == null) && (localDateFormatSymbols.localPatternChars == null)));
  }
  
  private void initializeData(Locale paramLocale)
  {
    this.locale = paramLocale;
    SoftReference localSoftReference1 = (SoftReference)cachedInstances.get(this.locale);
    DateFormatSymbols localDateFormatSymbols1;
    if ((localSoftReference1 != null) && ((localDateFormatSymbols1 = (DateFormatSymbols)localSoftReference1.get()) != null))
    {
      copyMembers(localDateFormatSymbols1, this);
      return;
    }
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, this.locale);
    if (!(localLocaleProviderAdapter instanceof ResourceBundleBasedAdapter)) {
      localLocaleProviderAdapter = LocaleProviderAdapter.getResourceBundleBased();
    }
    ResourceBundle localResourceBundle = ((ResourceBundleBasedAdapter)localLocaleProviderAdapter).getLocaleData().getDateFormatData(this.locale);
    if (localResourceBundle.containsKey("Eras")) {
      this.eras = localResourceBundle.getStringArray("Eras");
    } else if (localResourceBundle.containsKey("long.Eras")) {
      this.eras = localResourceBundle.getStringArray("long.Eras");
    } else if (localResourceBundle.containsKey("short.Eras")) {
      this.eras = localResourceBundle.getStringArray("short.Eras");
    }
    this.months = localResourceBundle.getStringArray("MonthNames");
    this.shortMonths = localResourceBundle.getStringArray("MonthAbbreviations");
    this.ampms = localResourceBundle.getStringArray("AmPmMarkers");
    this.localPatternChars = localResourceBundle.getString("DateTimePatternChars");
    this.weekdays = toOneBasedArray(localResourceBundle.getStringArray("DayNames"));
    this.shortWeekdays = toOneBasedArray(localResourceBundle.getStringArray("DayAbbreviations"));
    localSoftReference1 = new SoftReference((DateFormatSymbols)clone());
    SoftReference localSoftReference2 = (SoftReference)cachedInstances.putIfAbsent(this.locale, localSoftReference1);
    if (localSoftReference2 != null)
    {
      DateFormatSymbols localDateFormatSymbols2 = (DateFormatSymbols)localSoftReference2.get();
      if (localDateFormatSymbols2 == null) {
        cachedInstances.put(this.locale, localSoftReference1);
      }
    }
  }
  
  private static String[] toOneBasedArray(String[] paramArrayOfString)
  {
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i + 1];
    arrayOfString[0] = "";
    for (int j = 0; j < i; j++) {
      arrayOfString[(j + 1)] = paramArrayOfString[j];
    }
    return arrayOfString;
  }
  
  final int getZoneIndex(String paramString)
  {
    String[][] arrayOfString = getZoneStringsWrapper();
    if ((this.lastZoneIndex < arrayOfString.length) && (paramString.equals(arrayOfString[this.lastZoneIndex][0]))) {
      return this.lastZoneIndex;
    }
    for (int i = 0; i < arrayOfString.length; i++) {
      if (paramString.equals(arrayOfString[i][0]))
      {
        this.lastZoneIndex = i;
        return i;
      }
    }
    return -1;
  }
  
  final String[][] getZoneStringsWrapper()
  {
    if (isSubclassObject()) {
      return getZoneStrings();
    }
    return getZoneStringsImpl(false);
  }
  
  private String[][] getZoneStringsImpl(boolean paramBoolean)
  {
    if (this.zoneStrings == null) {
      this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale);
    }
    if (!paramBoolean) {
      return this.zoneStrings;
    }
    int i = this.zoneStrings.length;
    String[][] arrayOfString; = new String[i][];
    for (int j = 0; j < i; j++) {
      arrayOfString;[j] = ((String[])Arrays.copyOf(this.zoneStrings[j], this.zoneStrings[j].length));
    }
    return arrayOfString;;
  }
  
  private boolean isSubclassObject()
  {
    return !getClass().getName().equals("java.text.DateFormatSymbols");
  }
  
  private void copyMembers(DateFormatSymbols paramDateFormatSymbols1, DateFormatSymbols paramDateFormatSymbols2)
  {
    paramDateFormatSymbols2.eras = ((String[])Arrays.copyOf(paramDateFormatSymbols1.eras, paramDateFormatSymbols1.eras.length));
    paramDateFormatSymbols2.months = ((String[])Arrays.copyOf(paramDateFormatSymbols1.months, paramDateFormatSymbols1.months.length));
    paramDateFormatSymbols2.shortMonths = ((String[])Arrays.copyOf(paramDateFormatSymbols1.shortMonths, paramDateFormatSymbols1.shortMonths.length));
    paramDateFormatSymbols2.weekdays = ((String[])Arrays.copyOf(paramDateFormatSymbols1.weekdays, paramDateFormatSymbols1.weekdays.length));
    paramDateFormatSymbols2.shortWeekdays = ((String[])Arrays.copyOf(paramDateFormatSymbols1.shortWeekdays, paramDateFormatSymbols1.shortWeekdays.length));
    paramDateFormatSymbols2.ampms = ((String[])Arrays.copyOf(paramDateFormatSymbols1.ampms, paramDateFormatSymbols1.ampms.length));
    if (paramDateFormatSymbols1.zoneStrings != null) {
      paramDateFormatSymbols2.zoneStrings = paramDateFormatSymbols1.getZoneStringsImpl(true);
    } else {
      paramDateFormatSymbols2.zoneStrings = ((String[][])null);
    }
    paramDateFormatSymbols2.localPatternChars = paramDateFormatSymbols1.localPatternChars;
    paramDateFormatSymbols2.cachedHashCode = 0;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (this.zoneStrings == null) {
      this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale);
    }
    paramObjectOutputStream.defaultWriteObject();
  }
}
