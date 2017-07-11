package java.util.spi;

import java.util.Locale;

public abstract class LocaleServiceProvider
{
  protected LocaleServiceProvider() {}
  
  public abstract Locale[] getAvailableLocales();
  
  public boolean isSupportedLocale(Locale paramLocale)
  {
    paramLocale = paramLocale.stripExtensions();
    for (Locale localLocale : getAvailableLocales()) {
      if (paramLocale.equals(localLocale.stripExtensions())) {
        return true;
      }
    }
    return false;
  }
}
