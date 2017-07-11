package sun.util.locale.provider;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.spi.TimeZoneNameProvider;

public class TimeZoneNameProviderImpl
  extends TimeZoneNameProvider
{
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  
  TimeZoneNameProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
  {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales()
  {
    return LocaleProviderAdapter.toLocaleArray(this.langtags);
  }
  
  public boolean isSupportedLocale(Locale paramLocale)
  {
    return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags);
  }
  
  public String getDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale)
  {
    String[] arrayOfString = getDisplayNameArray(paramString, paramLocale);
    if (Objects.nonNull(arrayOfString))
    {
      assert (arrayOfString.length >= 7);
      int i = paramBoolean ? 3 : 1;
      if (paramInt == 0) {
        i++;
      }
      return arrayOfString[i];
    }
    return null;
  }
  
  public String getGenericDisplayName(String paramString, int paramInt, Locale paramLocale)
  {
    String[] arrayOfString = getDisplayNameArray(paramString, paramLocale);
    if (Objects.nonNull(arrayOfString))
    {
      assert (arrayOfString.length >= 7);
      return arrayOfString[6];
    }
    return null;
  }
  
  private String[] getDisplayNameArray(String paramString, Locale paramLocale)
  {
    Objects.requireNonNull(paramString);
    Objects.requireNonNull(paramLocale);
    return LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getTimeZoneNames(paramString);
  }
  
  String[][] getZoneStrings(Locale paramLocale)
  {
    return LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getZoneStrings();
  }
}
