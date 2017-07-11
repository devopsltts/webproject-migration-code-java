package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class XPointerMessageFormatter
  implements MessageFormatter
{
  public static final String XPOINTER_DOMAIN = "http://www.w3.org/TR/XPTR";
  private Locale fLocale = null;
  private ResourceBundle fResourceBundle = null;
  
  XPointerMessageFormatter() {}
  
  public String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject)
    throws MissingResourceException
  {
    if ((this.fResourceBundle == null) || (paramLocale != this.fLocale))
    {
      if (paramLocale != null)
      {
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", paramLocale);
        this.fLocale = paramLocale;
      }
      if (this.fResourceBundle == null) {
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages");
      }
    }
    String str = this.fResourceBundle.getString(paramString);
    if (paramArrayOfObject != null) {
      try
      {
        str = MessageFormat.format(str, paramArrayOfObject);
      }
      catch (Exception localException)
      {
        str = this.fResourceBundle.getString("FormatFailed");
        str = str + " " + this.fResourceBundle.getString(paramString);
      }
    }
    if (str == null)
    {
      str = this.fResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(str, "com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", paramString);
    }
    return str;
  }
}
