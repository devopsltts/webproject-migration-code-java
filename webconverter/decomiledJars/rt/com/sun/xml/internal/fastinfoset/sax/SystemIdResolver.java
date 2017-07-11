package com.sun.xml.internal.fastinfoset.sax;

import java.io.File;

public class SystemIdResolver
{
  public SystemIdResolver() {}
  
  public static String getAbsoluteURIFromRelative(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return "";
    }
    String str1 = paramString;
    if (!isAbsolutePath(paramString)) {
      try
      {
        str1 = getAbsolutePathFromRelativePath(paramString);
      }
      catch (SecurityException localSecurityException)
      {
        return "file:" + paramString;
      }
    }
    String str2;
    if (null != str1) {
      str2 = "file:///" + str1;
    } else {
      str2 = "file:" + paramString;
    }
    return replaceChars(str2);
  }
  
  private static String getAbsolutePathFromRelativePath(String paramString)
  {
    return new File(paramString).getAbsolutePath();
  }
  
  public static boolean isAbsoluteURI(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if (isWindowsAbsolutePath(paramString)) {
      return false;
    }
    int i = paramString.indexOf('#');
    int j = paramString.indexOf('?');
    int k = paramString.indexOf('/');
    int m = paramString.indexOf(':');
    int n = paramString.length() - 1;
    if (i > 0) {
      n = i;
    }
    if ((j > 0) && (j < n)) {
      n = j;
    }
    if ((k > 0) && (k < n)) {
      n = k;
    }
    return (m > 0) && (m < n);
  }
  
  public static boolean isAbsolutePath(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    File localFile = new File(paramString);
    return localFile.isAbsolute();
  }
  
  private static boolean isWindowsAbsolutePath(String paramString)
  {
    if (!isAbsolutePath(paramString)) {
      return false;
    }
    return (paramString.length() > 2) && (paramString.charAt(1) == ':') && (Character.isLetter(paramString.charAt(0))) && ((paramString.charAt(2) == '\\') || (paramString.charAt(2) == '/'));
  }
  
  private static String replaceChars(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString);
    int i = localStringBuffer.length();
    for (int j = 0; j < i; j++)
    {
      int k = localStringBuffer.charAt(j);
      if (k == 32)
      {
        localStringBuffer.setCharAt(j, '%');
        localStringBuffer.insert(j + 1, "20");
        i += 2;
        j += 2;
      }
      else if (k == 92)
      {
        localStringBuffer.setCharAt(j, '/');
      }
    }
    return localStringBuffer.toString();
  }
  
  public static String getAbsoluteURI(String paramString)
  {
    String str1 = paramString;
    if (isAbsoluteURI(paramString))
    {
      if (paramString.startsWith("file:"))
      {
        String str2 = paramString.substring(5);
        if ((str2 != null) && (str2.startsWith("/")))
        {
          if ((str2.startsWith("///")) || (!str2.startsWith("//")))
          {
            int i = paramString.indexOf(':', 5);
            if (i > 0)
            {
              String str3 = paramString.substring(i - 1);
              try
              {
                if (!isAbsolutePath(str3)) {
                  str1 = paramString.substring(0, i - 1) + getAbsolutePathFromRelativePath(str3);
                }
              }
              catch (SecurityException localSecurityException)
              {
                return paramString;
              }
            }
          }
        }
        else {
          return getAbsoluteURIFromRelative(paramString.substring(5));
        }
        return replaceChars(str1);
      }
      return paramString;
    }
    return getAbsoluteURIFromRelative(paramString);
  }
}
