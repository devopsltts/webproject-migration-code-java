package com.sun.org.apache.xpath.internal.res;

import com.sun.org.apache.bcel.internal.util.SecuritySupport;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.text.MessageFormat;
import java.util.ListResourceBundle;

public class XPATHMessages
  extends XMLMessages
{
  private static ListResourceBundle XPATHBundle = null;
  private static final String XPATH_ERROR_RESOURCES = "com.sun.org.apache.xpath.internal.res.XPATHErrorResources";
  
  public XPATHMessages() {}
  
  public static final String createXPATHMessage(String paramString, Object[] paramArrayOfObject)
  {
    if (XPATHBundle == null) {
      XPATHBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xpath.internal.res.XPATHErrorResources");
    }
    if (XPATHBundle != null) {
      return createXPATHMsg(XPATHBundle, paramString, paramArrayOfObject);
    }
    return "Could not load any resource bundles.";
  }
  
  public static final String createXPATHWarning(String paramString, Object[] paramArrayOfObject)
  {
    if (XPATHBundle == null) {
      XPATHBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xpath.internal.res.XPATHErrorResources");
    }
    if (XPATHBundle != null) {
      return createXPATHMsg(XPATHBundle, paramString, paramArrayOfObject);
    }
    return "Could not load any resource bundles.";
  }
  
  public static final String createXPATHMsg(ListResourceBundle paramListResourceBundle, String paramString, Object[] paramArrayOfObject)
  {
    Object localObject = null;
    int i = 0;
    String str = null;
    if (paramString != null) {
      str = paramListResourceBundle.getString(paramString);
    }
    if (str == null)
    {
      str = paramListResourceBundle.getString("BAD_CODE");
      i = 1;
    }
    if (paramArrayOfObject != null) {
      try
      {
        int j = paramArrayOfObject.length;
        for (int k = 0; k < j; k++) {
          if (null == paramArrayOfObject[k]) {
            paramArrayOfObject[k] = "";
          }
        }
        localObject = MessageFormat.format(str, paramArrayOfObject);
      }
      catch (Exception localException)
      {
        localObject = paramListResourceBundle.getString("FORMAT_FAILED");
        localObject = (String)localObject + " " + str;
      }
    } else {
      localObject = str;
    }
    if (i != 0) {
      throw new RuntimeException((String)localObject);
    }
    return localObject;
  }
}
