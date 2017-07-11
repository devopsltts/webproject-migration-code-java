package com.sun.xml.internal.ws.util;

import java.util.StringTokenizer;

public final class VersionUtil
{
  public static final String JAXWS_VERSION_20 = "2.0";
  public static final String JAXWS_VERSION_DEFAULT = "2.0";
  
  public VersionUtil() {}
  
  public static boolean isVersion20(String paramString)
  {
    return "2.0".equals(paramString);
  }
  
  public static boolean isValidVersion(String paramString)
  {
    return isVersion20(paramString);
  }
  
  public static String getValidVersionString()
  {
    return "2.0";
  }
  
  public static int[] getCanonicalVersion(String paramString)
  {
    int[] arrayOfInt = new int[4];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 1;
    arrayOfInt[2] = 0;
    arrayOfInt[3] = 0;
    String str1 = "_";
    String str2 = ".";
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString, ".");
    String str3 = localStringTokenizer1.nextToken();
    arrayOfInt[0] = Integer.parseInt(str3);
    str3 = localStringTokenizer1.nextToken();
    StringTokenizer localStringTokenizer2;
    if (str3.indexOf("_") == -1)
    {
      arrayOfInt[1] = Integer.parseInt(str3);
    }
    else
    {
      localStringTokenizer2 = new StringTokenizer(str3, "_");
      arrayOfInt[1] = Integer.parseInt(localStringTokenizer2.nextToken());
      arrayOfInt[3] = Integer.parseInt(localStringTokenizer2.nextToken());
    }
    if (localStringTokenizer1.hasMoreTokens())
    {
      str3 = localStringTokenizer1.nextToken();
      if (str3.indexOf("_") == -1)
      {
        arrayOfInt[2] = Integer.parseInt(str3);
        if (localStringTokenizer1.hasMoreTokens()) {
          arrayOfInt[3] = Integer.parseInt(localStringTokenizer1.nextToken());
        }
      }
      else
      {
        localStringTokenizer2 = new StringTokenizer(str3, "_");
        arrayOfInt[2] = Integer.parseInt(localStringTokenizer2.nextToken());
        arrayOfInt[3] = Integer.parseInt(localStringTokenizer2.nextToken());
      }
    }
    return arrayOfInt;
  }
  
  public static int compare(String paramString1, String paramString2)
  {
    int[] arrayOfInt1 = getCanonicalVersion(paramString1);
    int[] arrayOfInt2 = getCanonicalVersion(paramString2);
    if (arrayOfInt1[0] < arrayOfInt2[0]) {
      return -1;
    }
    if (arrayOfInt1[0] > arrayOfInt2[0]) {
      return 1;
    }
    if (arrayOfInt1[1] < arrayOfInt2[1]) {
      return -1;
    }
    if (arrayOfInt1[1] > arrayOfInt2[1]) {
      return 1;
    }
    if (arrayOfInt1[2] < arrayOfInt2[2]) {
      return -1;
    }
    if (arrayOfInt1[2] > arrayOfInt2[2]) {
      return 1;
    }
    if (arrayOfInt1[3] < arrayOfInt2[3]) {
      return -1;
    }
    if (arrayOfInt1[3] > arrayOfInt2[3]) {
      return 1;
    }
    return 0;
  }
}
