package java.sql;

import java.time.Instant;
import java.time.LocalDate;

public class Date
  extends java.util.Date
{
  static final long serialVersionUID = 1511598038487230103L;
  
  @Deprecated
  public Date(int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramInt1, paramInt2, paramInt3);
  }
  
  public Date(long paramLong)
  {
    super(paramLong);
  }
  
  public void setTime(long paramLong)
  {
    super.setTime(paramLong);
  }
  
  public static Date valueOf(String paramString)
  {
    Date localDate = null;
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    int i = paramString.indexOf('-');
    int j = paramString.indexOf('-', i + 1);
    if ((i > 0) && (j > 0) && (j < paramString.length() - 1))
    {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1, j);
      String str3 = paramString.substring(j + 1);
      if ((str1.length() == 4) && (str2.length() >= 1) && (str2.length() <= 2) && (str3.length() >= 1) && (str3.length() <= 2))
      {
        int k = Integer.parseInt(str1);
        int m = Integer.parseInt(str2);
        int n = Integer.parseInt(str3);
        if ((m >= 1) && (m <= 12) && (n >= 1) && (n <= 31)) {
          localDate = new Date(k - 1900, m - 1, n);
        }
      }
    }
    if (localDate == null) {
      throw new IllegalArgumentException();
    }
    return localDate;
  }
  
  public String toString()
  {
    int i = super.getYear() + 1900;
    int j = super.getMonth() + 1;
    int k = super.getDate();
    char[] arrayOfChar = "2000-00-00".toCharArray();
    arrayOfChar[0] = Character.forDigit(i / 1000, 10);
    arrayOfChar[1] = Character.forDigit(i / 100 % 10, 10);
    arrayOfChar[2] = Character.forDigit(i / 10 % 10, 10);
    arrayOfChar[3] = Character.forDigit(i % 10, 10);
    arrayOfChar[5] = Character.forDigit(j / 10, 10);
    arrayOfChar[6] = Character.forDigit(j % 10, 10);
    arrayOfChar[8] = Character.forDigit(k / 10, 10);
    arrayOfChar[9] = Character.forDigit(k % 10, 10);
    return new String(arrayOfChar);
  }
  
  @Deprecated
  public int getHours()
  {
    throw new IllegalArgumentException();
  }
  
  @Deprecated
  public int getMinutes()
  {
    throw new IllegalArgumentException();
  }
  
  @Deprecated
  public int getSeconds()
  {
    throw new IllegalArgumentException();
  }
  
  @Deprecated
  public void setHours(int paramInt)
  {
    throw new IllegalArgumentException();
  }
  
  @Deprecated
  public void setMinutes(int paramInt)
  {
    throw new IllegalArgumentException();
  }
  
  @Deprecated
  public void setSeconds(int paramInt)
  {
    throw new IllegalArgumentException();
  }
  
  public static Date valueOf(LocalDate paramLocalDate)
  {
    return new Date(paramLocalDate.getYear() - 1900, paramLocalDate.getMonthValue() - 1, paramLocalDate.getDayOfMonth());
  }
  
  public LocalDate toLocalDate()
  {
    return LocalDate.of(getYear() + 1900, getMonth() + 1, getDate());
  }
  
  public Instant toInstant()
  {
    throw new UnsupportedOperationException();
  }
}
