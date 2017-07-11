package sun.util.resources;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.Set;
import sun.util.locale.provider.JRELocaleProviderAdapter;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleProviderAdapter.Type;

public class LocaleData
{
  private final LocaleProviderAdapter.Type type;
  
  public LocaleData(LocaleProviderAdapter.Type paramType)
  {
    this.type = paramType;
  }
  
  public ResourceBundle getCalendarData(Locale paramLocale)
  {
    return getBundle(this.type.getUtilResourcesPackage() + ".CalendarData", paramLocale);
  }
  
  public OpenListResourceBundle getCurrencyNames(Locale paramLocale)
  {
    return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".CurrencyNames", paramLocale);
  }
  
  public OpenListResourceBundle getLocaleNames(Locale paramLocale)
  {
    return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".LocaleNames", paramLocale);
  }
  
  public TimeZoneNamesBundle getTimeZoneNames(Locale paramLocale)
  {
    return (TimeZoneNamesBundle)getBundle(this.type.getUtilResourcesPackage() + ".TimeZoneNames", paramLocale);
  }
  
  public ResourceBundle getBreakIteratorInfo(Locale paramLocale)
  {
    return getBundle(this.type.getTextResourcesPackage() + ".BreakIteratorInfo", paramLocale);
  }
  
  public ResourceBundle getCollationData(Locale paramLocale)
  {
    return getBundle(this.type.getTextResourcesPackage() + ".CollationData", paramLocale);
  }
  
  public ResourceBundle getDateFormatData(Locale paramLocale)
  {
    return getBundle(this.type.getTextResourcesPackage() + ".FormatData", paramLocale);
  }
  
  public void setSupplementary(ParallelListResourceBundle paramParallelListResourceBundle)
  {
    if (!paramParallelListResourceBundle.areParallelContentsComplete())
    {
      String str = this.type.getTextResourcesPackage() + ".JavaTimeSupplementary";
      setSupplementary(str, paramParallelListResourceBundle);
    }
  }
  
  private boolean setSupplementary(String paramString, ParallelListResourceBundle paramParallelListResourceBundle)
  {
    ParallelListResourceBundle localParallelListResourceBundle = (ParallelListResourceBundle)paramParallelListResourceBundle.getParent();
    boolean bool = false;
    if (localParallelListResourceBundle != null) {
      bool = setSupplementary(paramString, localParallelListResourceBundle);
    }
    OpenListResourceBundle localOpenListResourceBundle = getSupplementary(paramString, paramParallelListResourceBundle.getLocale());
    paramParallelListResourceBundle.setParallelContents(localOpenListResourceBundle);
    bool |= localOpenListResourceBundle != null;
    if (bool) {
      paramParallelListResourceBundle.resetKeySet();
    }
    return bool;
  }
  
  public ResourceBundle getNumberFormatData(Locale paramLocale)
  {
    return getBundle(this.type.getTextResourcesPackage() + ".FormatData", paramLocale);
  }
  
  public static ResourceBundle getBundle(String paramString, final Locale paramLocale)
  {
    (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ResourceBundle run()
      {
        return ResourceBundle.getBundle(this.val$baseName, paramLocale, LocaleData.LocaleDataResourceBundleControl.access$000());
      }
    });
  }
  
  private static OpenListResourceBundle getSupplementary(String paramString, final Locale paramLocale)
  {
    (OpenListResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
    {
      public OpenListResourceBundle run()
      {
        OpenListResourceBundle localOpenListResourceBundle = null;
        try
        {
          localOpenListResourceBundle = (OpenListResourceBundle)ResourceBundle.getBundle(this.val$baseName, paramLocale, LocaleData.SupplementaryResourceBundleControl.access$100());
        }
        catch (MissingResourceException localMissingResourceException) {}
        return localOpenListResourceBundle;
      }
    });
  }
  
  private static class LocaleDataResourceBundleControl
    extends ResourceBundle.Control
  {
    private static final LocaleDataResourceBundleControl INSTANCE = new LocaleDataResourceBundleControl();
    private static final String DOTCLDR = ".cldr";
    
    private LocaleDataResourceBundleControl() {}
    
    public List<Locale> getCandidateLocales(String paramString, Locale paramLocale)
    {
      List localList = super.getCandidateLocales(paramString, paramLocale);
      int i = paramString.lastIndexOf('.');
      String str = i >= 0 ? paramString.substring(i + 1) : paramString;
      LocaleProviderAdapter.Type localType = paramString.contains(".cldr") ? LocaleProviderAdapter.Type.CLDR : LocaleProviderAdapter.Type.JRE;
      LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.forType(localType);
      Set localSet = ((JRELocaleProviderAdapter)localLocaleProviderAdapter).getLanguageTagSet(str);
      if (!localSet.isEmpty())
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext()) {
          if (!LocaleProviderAdapter.isSupportedLocale((Locale)localIterator.next(), localType, localSet)) {
            localIterator.remove();
          }
        }
      }
      if ((paramLocale.getLanguage() != "en") && (localType == LocaleProviderAdapter.Type.CLDR) && (str.equals("TimeZoneNames"))) {
        localList.add(localList.size() - 1, Locale.ENGLISH);
      }
      return localList;
    }
    
    public Locale getFallbackLocale(String paramString, Locale paramLocale)
    {
      if ((paramString == null) || (paramLocale == null)) {
        throw new NullPointerException();
      }
      return null;
    }
    
    public String toBundleName(String paramString, Locale paramLocale)
    {
      String str1 = paramString;
      String str2 = paramLocale.getLanguage();
      if ((str2.length() > 0) && ((paramString.startsWith(LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage())) || (paramString.startsWith(LocaleProviderAdapter.Type.JRE.getTextResourcesPackage()))))
      {
        assert (LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length() == LocaleProviderAdapter.Type.JRE.getTextResourcesPackage().length());
        int i = LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length();
        if (paramString.indexOf(".cldr", i) > 0) {
          i += ".cldr".length();
        }
        str1 = paramString.substring(0, i + 1) + str2 + paramString.substring(i);
      }
      return super.toBundleName(str1, paramLocale);
    }
  }
  
  private static class SupplementaryResourceBundleControl
    extends LocaleData.LocaleDataResourceBundleControl
  {
    private static final SupplementaryResourceBundleControl INSTANCE = new SupplementaryResourceBundleControl();
    
    private SupplementaryResourceBundleControl()
    {
      super();
    }
    
    public List<Locale> getCandidateLocales(String paramString, Locale paramLocale)
    {
      return Arrays.asList(new Locale[] { paramLocale });
    }
    
    public long getTimeToLive(String paramString, Locale paramLocale)
    {
      assert (paramString.contains("JavaTimeSupplementary"));
      return -1L;
    }
  }
}
