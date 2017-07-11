package com.sun.org.apache.xerces.internal.impl.msg;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class XMLMessageFormatter_pt_BR
  implements MessageFormatter
{
  public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
  public static final String XMLNS_DOMAIN = "http://www.w3.org/TR/1999/REC-xml-names-19990114";
  private Locale fLocale = null;
  private ResourceBundle fResourceBundle = null;
  
  public XMLMessageFormatter_pt_BR() {}
  
  public String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject)
    throws MissingResourceException
  {
    if ((this.fResourceBundle == null) || (paramLocale != this.fLocale))
    {
      if (paramLocale != null)
      {
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", paramLocale);
        this.fLocale = paramLocale;
      }
      if (this.fResourceBundle == null) {
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
      }
    }
    String str;
    try
    {
      str = this.fResourceBundle.getString(paramString);
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
    }
    catch (MissingResourceException localMissingResourceException)
    {
      str = this.fResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(paramString, str, paramString);
    }
    if (str == null)
    {
      str = paramString;
      if (paramArrayOfObject.length > 0)
      {
        StringBuffer localStringBuffer = new StringBuffer(str);
        localStringBuffer.append('?');
        for (int i = 0; i < paramArrayOfObject.length; i++)
        {
          if (i > 0) {
            localStringBuffer.append('&');
          }
          localStringBuffer.append(String.valueOf(paramArrayOfObject[i]));
        }
      }
    }
    return str;
  }
}
