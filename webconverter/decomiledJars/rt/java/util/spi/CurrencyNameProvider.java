package java.util.spi;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle.Control;

public abstract class CurrencyNameProvider
  extends LocaleServiceProvider
{
  protected CurrencyNameProvider() {}
  
  public abstract String getSymbol(String paramString, Locale paramLocale);
  
  public String getDisplayName(String paramString, Locale paramLocale)
  {
    if ((paramString == null) || (paramLocale == null)) {
      throw new NullPointerException();
    }
    char[] arrayOfChar = paramString.toCharArray();
    if (arrayOfChar.length != 3) {
      throw new IllegalArgumentException("The currencyCode is not in the form of three upper-case letters.");
    }
    int k;
    for (k : arrayOfChar) {
      if ((k < 65) || (k > 90)) {
        throw new IllegalArgumentException("The currencyCode is not in the form of three upper-case letters.");
      }
    }
    ??? = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
    for (Locale localLocale : getAvailableLocales()) {
      if (((ResourceBundle.Control)???).getCandidateLocales("", localLocale).contains(paramLocale)) {
        return null;
      }
    }
    throw new IllegalArgumentException("The locale is not available");
  }
}
