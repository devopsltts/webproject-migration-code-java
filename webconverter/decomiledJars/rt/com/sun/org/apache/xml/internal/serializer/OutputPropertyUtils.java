package com.sun.org.apache.xml.internal.serializer;

import java.util.Properties;

public final class OutputPropertyUtils
{
  public OutputPropertyUtils() {}
  
  public static boolean getBooleanProperty(String paramString, Properties paramProperties)
  {
    String str = paramProperties.getProperty(paramString);
    return (null != str) && (str.equals("yes"));
  }
  
  public static int getIntProperty(String paramString, Properties paramProperties)
  {
    String str = paramProperties.getProperty(paramString);
    if (null == str) {
      return 0;
    }
    return Integer.parseInt(str);
  }
}
