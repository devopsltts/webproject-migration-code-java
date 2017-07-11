package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ExsltDatetime
{
  static final String dt = "yyyy-MM-dd'T'HH:mm:ss";
  static final String d = "yyyy-MM-dd";
  static final String gym = "yyyy-MM";
  static final String gy = "yyyy";
  static final String gmd = "--MM-dd";
  static final String gm = "--MM--";
  static final String gd = "---dd";
  static final String t = "HH:mm:ss";
  static final String EMPTY_STR = "";
  
  public ExsltDatetime() {}
  
  public static String dateTime()
  {
    Calendar localCalendar = Calendar.getInstance();
    Date localDate = localCalendar.getTime();
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    StringBuffer localStringBuffer = new StringBuffer(localSimpleDateFormat.format(localDate));
    int i = localCalendar.get(15) + localCalendar.get(16);
    if (i == 0)
    {
      localStringBuffer.append("Z");
    }
    else
    {
      int j = i / 3600000;
      int k = i % 3600000;
      char c = j < 0 ? '-' : '+';
      localStringBuffer.append(c).append(formatDigits(j)).append(':').append(formatDigits(k));
    }
    return localStringBuffer.toString();
  }
  
  private static String formatDigits(int paramInt)
  {
    String str = String.valueOf(Math.abs(paramInt));
    return str.length() == 1 ? '0' + str : str;
  }
  
  public static String date(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[0];
    String str2 = arrayOfString1[1];
    String str3 = arrayOfString1[2];
    if ((str2 == null) || (str3 == null)) {
      return "";
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    String str4 = "yyyy-MM-dd";
    Date localDate = testFormats(str2, arrayOfString2);
    if (localDate == null) {
      return "";
    }
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(str4);
    localSimpleDateFormat.setLenient(false);
    String str5 = localSimpleDateFormat.format(localDate);
    if (str5.length() == 0) {
      return "";
    }
    return str1 + str5 + str3;
  }
  
  public static String date()
  {
    String str1 = dateTime().toString();
    String str2 = str1.substring(0, str1.indexOf("T"));
    String str3 = str1.substring(getZoneStart(str1));
    return str2 + str3;
  }
  
  public static String time(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    String str2 = arrayOfString1[2];
    if ((str1 == null) || (str2 == null)) {
      return "";
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss" };
    String str3 = "HH:mm:ss";
    Date localDate = testFormats(str1, arrayOfString2);
    if (localDate == null) {
      return "";
    }
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(str3);
    String str4 = localSimpleDateFormat.format(localDate);
    return str4 + str2;
  }
  
  public static String time()
  {
    String str1 = dateTime().toString();
    String str2 = str1.substring(str1.indexOf("T") + 1);
    return str2;
  }
  
  public static double year(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    int i = arrayOfString1[0].length() == 0 ? 1 : 0;
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "yyyy" };
    double d1 = getNumber(str, arrayOfString2, 1);
    if ((i != 0) || (d1 == NaN.0D)) {
      return d1;
    }
    return -d1;
  }
  
  public static double year()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(1);
  }
  
  public static double monthInYear(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--", "--MM-dd" };
    return getNumber(str, arrayOfString2, 2) + 1.0D;
  }
  
  public static double monthInYear()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(2) + 1;
  }
  
  public static double weekInYear(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    return getNumber(str, arrayOfString2, 3);
  }
  
  public static double weekInYear()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(3);
  }
  
  public static double dayInYear(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    return getNumber(str, arrayOfString2, 6);
  }
  
  public static double dayInYear()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(6);
  }
  
  public static double dayInMonth(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "--MM-dd", "---dd" };
    double d1 = getNumber(str, arrayOfString2, 5);
    return d1;
  }
  
  public static double dayInMonth()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(5);
  }
  
  public static double dayOfWeekInMonth(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    return getNumber(str, arrayOfString2, 8);
  }
  
  public static double dayOfWeekInMonth()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(8);
  }
  
  public static double dayInWeek(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    return getNumber(str, arrayOfString2, 7);
  }
  
  public static double dayInWeek()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(7);
  }
  
  public static double hourInDay(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
    return getNumber(str, arrayOfString2, 11);
  }
  
  public static double hourInDay()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(11);
  }
  
  public static double minuteInHour(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
    return getNumber(str, arrayOfString2, 12);
  }
  
  public static double minuteInHour()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(12);
  }
  
  public static double secondInMinute(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return NaN.0D;
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
    return getNumber(str, arrayOfString2, 13);
  }
  
  public static double secondInMinute()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(13);
  }
  
  public static XObject leapYear(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null) {
      return new XNumber(NaN.0D);
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "yyyy" };
    double d1 = getNumber(str, arrayOfString2, 1);
    if (d1 == NaN.0D) {
      return new XNumber(NaN.0D);
    }
    int i = (int)d1;
    return new XBoolean((i % 400 == 0) || ((i % 100 != 0) && (i % 4 == 0)));
  }
  
  public static boolean leapYear()
  {
    Calendar localCalendar = Calendar.getInstance();
    int i = localCalendar.get(1);
    return (i % 400 == 0) || ((i % 100 != 0) && (i % 4 == 0));
  }
  
  public static String monthName(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    if (str1 == null) {
      return "";
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--" };
    String str2 = "MMMM";
    return getNameOrAbbrev(paramString, arrayOfString2, str2);
  }
  
  public static String monthName()
  {
    Calendar localCalendar = Calendar.getInstance();
    String str = "MMMM";
    return getNameOrAbbrev(str);
  }
  
  public static String monthAbbreviation(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    if (str1 == null) {
      return "";
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--" };
    String str2 = "MMM";
    return getNameOrAbbrev(paramString, arrayOfString2, str2);
  }
  
  public static String monthAbbreviation()
  {
    String str = "MMM";
    return getNameOrAbbrev(str);
  }
  
  public static String dayName(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    if (str1 == null) {
      return "";
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    String str2 = "EEEE";
    return getNameOrAbbrev(paramString, arrayOfString2, str2);
  }
  
  public static String dayName()
  {
    String str = "EEEE";
    return getNameOrAbbrev(str);
  }
  
  public static String dayAbbreviation(String paramString)
    throws ParseException
  {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    if (str1 == null) {
      return "";
    }
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    String str2 = "EEE";
    return getNameOrAbbrev(paramString, arrayOfString2, str2);
  }
  
  public static String dayAbbreviation()
  {
    String str = "EEE";
    return getNameOrAbbrev(str);
  }
  
  private static String[] getEraDatetimeZone(String paramString)
  {
    String str1 = "";
    String str2 = paramString;
    String str3 = "";
    if ((paramString.charAt(0) == '-') && (!paramString.startsWith("--")))
    {
      str1 = "-";
      str2 = paramString.substring(1);
    }
    int i = getZoneStart(str2);
    if (i > 0)
    {
      str3 = str2.substring(i);
      str2 = str2.substring(0, i);
    }
    else if (i == -2)
    {
      str3 = null;
    }
    return new String[] { str1, str2, str3 };
  }
  
  private static int getZoneStart(String paramString)
  {
    if (paramString.indexOf("Z") == paramString.length() - 1) {
      return paramString.length() - 1;
    }
    if ((paramString.length() >= 6) && (paramString.charAt(paramString.length() - 3) == ':') && ((paramString.charAt(paramString.length() - 6) == '+') || (paramString.charAt(paramString.length() - 6) == '-'))) {
      try
      {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm");
        localSimpleDateFormat.setLenient(false);
        Date localDate = localSimpleDateFormat.parse(paramString.substring(paramString.length() - 5));
        return paramString.length() - 6;
      }
      catch (ParseException localParseException)
      {
        System.out.println("ParseException " + localParseException.getErrorOffset());
        return -2;
      }
    }
    return -1;
  }
  
  private static Date testFormats(String paramString, String[] paramArrayOfString)
    throws ParseException
  {
    int i = 0;
    while (i < paramArrayOfString.length) {
      try
      {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramArrayOfString[i]);
        localSimpleDateFormat.setLenient(false);
        return localSimpleDateFormat.parse(paramString);
      }
      catch (ParseException localParseException)
      {
        i++;
      }
    }
    return null;
  }
  
  private static double getNumber(String paramString, String[] paramArrayOfString, int paramInt)
    throws ParseException
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setLenient(false);
    Date localDate = testFormats(paramString, paramArrayOfString);
    if (localDate == null) {
      return NaN.0D;
    }
    localCalendar.setTime(localDate);
    return localCalendar.get(paramInt);
  }
  
  private static String getNameOrAbbrev(String paramString1, String[] paramArrayOfString, String paramString2)
    throws ParseException
  {
    int i = 0;
    while (i < paramArrayOfString.length) {
      try
      {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramArrayOfString[i], Locale.ENGLISH);
        localSimpleDateFormat.setLenient(false);
        Date localDate = localSimpleDateFormat.parse(paramString1);
        localSimpleDateFormat.applyPattern(paramString2);
        return localSimpleDateFormat.format(localDate);
      }
      catch (ParseException localParseException)
      {
        i++;
      }
    }
    return "";
  }
  
  private static String getNameOrAbbrev(String paramString)
  {
    Calendar localCalendar = Calendar.getInstance();
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString, Locale.ENGLISH);
    return localSimpleDateFormat.format(localCalendar.getTime());
  }
  
  public static String formatDate(String paramString1, String paramString2)
  {
    String str1 = "Gy";
    String str2 = "M";
    String str3 = "dDEFwW";
    TimeZone localTimeZone;
    String str4;
    if ((paramString1.endsWith("Z")) || (paramString1.endsWith("z")))
    {
      localTimeZone = TimeZone.getTimeZone("GMT");
      paramString1 = paramString1.substring(0, paramString1.length() - 1) + "GMT";
      str4 = "z";
    }
    else if ((paramString1.length() >= 6) && (paramString1.charAt(paramString1.length() - 3) == ':') && ((paramString1.charAt(paramString1.length() - 6) == '+') || (paramString1.charAt(paramString1.length() - 6) == '-')))
    {
      localObject1 = paramString1.substring(paramString1.length() - 6);
      if (("+00:00".equals(localObject1)) || ("-00:00".equals(localObject1))) {
        localTimeZone = TimeZone.getTimeZone("GMT");
      } else {
        localTimeZone = TimeZone.getTimeZone("GMT" + (String)localObject1);
      }
      str4 = "z";
      paramString1 = paramString1.substring(0, paramString1.length() - 6) + "GMT" + (String)localObject1;
    }
    else
    {
      localTimeZone = TimeZone.getDefault();
      str4 = "";
    }
    Object localObject1 = { "yyyy-MM-dd'T'HH:mm:ss" + str4, "yyyy-MM-dd", "yyyy-MM", "yyyy" };
    try
    {
      SimpleDateFormat localSimpleDateFormat1 = new SimpleDateFormat("HH:mm:ss" + str4);
      localSimpleDateFormat1.setLenient(false);
      localObject2 = localSimpleDateFormat1.parse(paramString1);
      localObject3 = new SimpleDateFormat(strip("GyMdDEFwW", paramString2));
      ((SimpleDateFormat)localObject3).setTimeZone(localTimeZone);
      return ((SimpleDateFormat)localObject3).format((Date)localObject2);
    }
    catch (ParseException localParseException1)
    {
      Object localObject2;
      Object localObject3;
      int i = 0;
      while (i < localObject1.length) {
        try
        {
          localObject2 = new SimpleDateFormat(localObject1[i]);
          ((SimpleDateFormat)localObject2).setLenient(false);
          localObject3 = ((SimpleDateFormat)localObject2).parse(paramString1);
          SimpleDateFormat localSimpleDateFormat5 = new SimpleDateFormat(paramString2);
          localSimpleDateFormat5.setTimeZone(localTimeZone);
          return localSimpleDateFormat5.format((Date)localObject3);
        }
        catch (ParseException localParseException5)
        {
          i++;
        }
      }
      try
      {
        SimpleDateFormat localSimpleDateFormat2 = new SimpleDateFormat("--MM-dd");
        localSimpleDateFormat2.setLenient(false);
        localDate = localSimpleDateFormat2.parse(paramString1);
        localObject3 = new SimpleDateFormat(strip("Gy", paramString2));
        ((SimpleDateFormat)localObject3).setTimeZone(localTimeZone);
        return ((SimpleDateFormat)localObject3).format(localDate);
      }
      catch (ParseException localParseException2)
      {
        try
        {
          SimpleDateFormat localSimpleDateFormat3 = new SimpleDateFormat("--MM--");
          localSimpleDateFormat3.setLenient(false);
          localDate = localSimpleDateFormat3.parse(paramString1);
          localObject3 = new SimpleDateFormat(strip("Gy", paramString2));
          ((SimpleDateFormat)localObject3).setTimeZone(localTimeZone);
          return ((SimpleDateFormat)localObject3).format(localDate);
        }
        catch (ParseException localParseException3)
        {
          try
          {
            SimpleDateFormat localSimpleDateFormat4 = new SimpleDateFormat("---dd");
            localSimpleDateFormat4.setLenient(false);
            Date localDate = localSimpleDateFormat4.parse(paramString1);
            localObject3 = new SimpleDateFormat(strip("GyM", paramString2));
            ((SimpleDateFormat)localObject3).setTimeZone(localTimeZone);
            return ((SimpleDateFormat)localObject3).format(localDate);
          }
          catch (ParseException localParseException4) {}
        }
      }
    }
    return "";
  }
  
  private static String strip(String paramString1, String paramString2)
  {
    int i = 0;
    int j = 0;
    StringBuffer localStringBuffer = new StringBuffer(paramString2.length());
    while (j < paramString2.length())
    {
      char c = paramString2.charAt(j);
      if (c == '\'')
      {
        int k = paramString2.indexOf('\'', j + 1);
        if (k == -1) {
          k = paramString2.length();
        }
        localStringBuffer.append(paramString2.substring(j, k));
        j = k++;
      }
      else if (paramString1.indexOf(c) > -1)
      {
        j++;
      }
      else
      {
        localStringBuffer.append(c);
        j++;
      }
    }
    return localStringBuffer.toString();
  }
}
