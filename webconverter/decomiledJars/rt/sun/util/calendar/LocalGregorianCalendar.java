package sun.util.calendar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class LocalGregorianCalendar
  extends BaseCalendar
{
  private String name;
  private Era[] eras;
  
  static LocalGregorianCalendar getLocalGregorianCalendar(String paramString)
  {
    Properties localProperties;
    try
    {
      localProperties = CalendarSystem.getCalendarProperties();
    }
    catch (IOException|IllegalArgumentException localIOException)
    {
      throw new InternalError(localIOException);
    }
    String str1 = localProperties.getProperty("calendar." + paramString + ".eras");
    if (str1 == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    StringTokenizer localStringTokenizer1 = new StringTokenizer(str1, ";");
    while (localStringTokenizer1.hasMoreTokens())
    {
      localObject1 = localStringTokenizer1.nextToken().trim();
      StringTokenizer localStringTokenizer2 = new StringTokenizer((String)localObject1, ",");
      Object localObject2 = null;
      boolean bool = true;
      long l = 0L;
      Object localObject3 = null;
      while (localStringTokenizer2.hasMoreTokens())
      {
        localObject4 = localStringTokenizer2.nextToken();
        int i = ((String)localObject4).indexOf('=');
        if (i == -1) {
          return null;
        }
        String str2 = ((String)localObject4).substring(0, i);
        String str3 = ((String)localObject4).substring(i + 1);
        if ("name".equals(str2)) {
          localObject2 = str3;
        } else if ("since".equals(str2))
        {
          if (str3.endsWith("u"))
          {
            bool = false;
            l = Long.parseLong(str3.substring(0, str3.length() - 1));
          }
          else
          {
            l = Long.parseLong(str3);
          }
        }
        else if ("abbr".equals(str2)) {
          localObject3 = str3;
        } else {
          throw new RuntimeException("Unknown key word: " + str2);
        }
      }
      Object localObject4 = new Era(localObject2, localObject3, l, bool);
      localArrayList.add(localObject4);
    }
    Object localObject1 = new Era[localArrayList.size()];
    localArrayList.toArray((Object[])localObject1);
    return new LocalGregorianCalendar(paramString, (Era[])localObject1);
  }
  
  private LocalGregorianCalendar(String paramString, Era[] paramArrayOfEra)
  {
    this.name = paramString;
    this.eras = paramArrayOfEra;
    setEras(paramArrayOfEra);
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Date getCalendarDate()
  {
    return getCalendarDate(System.currentTimeMillis(), newCalendarDate());
  }
  
  public Date getCalendarDate(long paramLong)
  {
    return getCalendarDate(paramLong, newCalendarDate());
  }
  
  public Date getCalendarDate(long paramLong, TimeZone paramTimeZone)
  {
    return getCalendarDate(paramLong, newCalendarDate(paramTimeZone));
  }
  
  public Date getCalendarDate(long paramLong, CalendarDate paramCalendarDate)
  {
    Date localDate = (Date)super.getCalendarDate(paramLong, paramCalendarDate);
    return adjustYear(localDate, paramLong, localDate.getZoneOffset());
  }
  
  private Date adjustYear(Date paramDate, long paramLong, int paramInt)
  {
    for (int i = this.eras.length - 1; i >= 0; i--)
    {
      Era localEra = this.eras[i];
      long l = localEra.getSince(null);
      if (localEra.isLocalTime()) {
        l -= paramInt;
      }
      if (paramLong >= l)
      {
        paramDate.setLocalEra(localEra);
        int j = paramDate.getNormalizedYear() - localEra.getSinceDate().getYear() + 1;
        paramDate.setLocalYear(j);
        break;
      }
    }
    if (i < 0)
    {
      paramDate.setLocalEra(null);
      paramDate.setLocalYear(paramDate.getNormalizedYear());
    }
    paramDate.setNormalized(true);
    return paramDate;
  }
  
  public Date newCalendarDate()
  {
    return new Date();
  }
  
  public Date newCalendarDate(TimeZone paramTimeZone)
  {
    return new Date(paramTimeZone);
  }
  
  public boolean validate(CalendarDate paramCalendarDate)
  {
    Date localDate1 = (Date)paramCalendarDate;
    Era localEra = localDate1.getEra();
    if (localEra != null)
    {
      if (!validateEra(localEra)) {
        return false;
      }
      localDate1.setNormalizedYear(localEra.getSinceDate().getYear() + localDate1.getYear() - 1);
      Date localDate2 = newCalendarDate(paramCalendarDate.getZone());
      localDate2.setEra(localEra).setDate(paramCalendarDate.getYear(), paramCalendarDate.getMonth(), paramCalendarDate.getDayOfMonth());
      normalize(localDate2);
      if (localDate2.getEra() != localEra) {
        return false;
      }
    }
    else
    {
      if (paramCalendarDate.getYear() >= this.eras[0].getSinceDate().getYear()) {
        return false;
      }
      localDate1.setNormalizedYear(localDate1.getYear());
    }
    return super.validate(localDate1);
  }
  
  private boolean validateEra(Era paramEra)
  {
    for (int i = 0; i < this.eras.length; i++) {
      if (paramEra == this.eras[i]) {
        return true;
      }
    }
    return false;
  }
  
  public boolean normalize(CalendarDate paramCalendarDate)
  {
    if (paramCalendarDate.isNormalized()) {
      return true;
    }
    normalizeYear(paramCalendarDate);
    Date localDate = (Date)paramCalendarDate;
    super.normalize(localDate);
    int i = 0;
    long l1 = 0L;
    int j = localDate.getNormalizedYear();
    Era localEra = null;
    for (int k = this.eras.length - 1; k >= 0; k--)
    {
      localEra = this.eras[k];
      if (localEra.isLocalTime())
      {
        CalendarDate localCalendarDate = localEra.getSinceDate();
        int n = localCalendarDate.getYear();
        if (j > n) {
          break;
        }
        if (j == n)
        {
          int i1 = localDate.getMonth();
          int i2 = localCalendarDate.getMonth();
          if (i1 > i2) {
            break;
          }
          if (i1 == i2)
          {
            int i3 = localDate.getDayOfMonth();
            int i4 = localCalendarDate.getDayOfMonth();
            if (i3 > i4) {
              break;
            }
            if (i3 == i4)
            {
              long l3 = localDate.getTimeOfDay();
              long l4 = localCalendarDate.getTimeOfDay();
              if (l3 >= l4) {
                break;
              }
              k--;
              break;
            }
          }
        }
      }
      else
      {
        if (i == 0)
        {
          l1 = super.getTime(paramCalendarDate);
          i = 1;
        }
        long l2 = localEra.getSince(paramCalendarDate.getZone());
        if (l1 >= l2) {
          break;
        }
      }
    }
    if (k >= 0)
    {
      localDate.setLocalEra(localEra);
      int m = localDate.getNormalizedYear() - localEra.getSinceDate().getYear() + 1;
      localDate.setLocalYear(m);
    }
    else
    {
      localDate.setEra(null);
      localDate.setLocalYear(j);
      localDate.setNormalizedYear(j);
    }
    localDate.setNormalized(true);
    return true;
  }
  
  void normalizeMonth(CalendarDate paramCalendarDate)
  {
    normalizeYear(paramCalendarDate);
    super.normalizeMonth(paramCalendarDate);
  }
  
  void normalizeYear(CalendarDate paramCalendarDate)
  {
    Date localDate = (Date)paramCalendarDate;
    Era localEra = localDate.getEra();
    if ((localEra == null) || (!validateEra(localEra))) {
      localDate.setNormalizedYear(localDate.getYear());
    } else {
      localDate.setNormalizedYear(localEra.getSinceDate().getYear() + localDate.getYear() - 1);
    }
  }
  
  public boolean isLeapYear(int paramInt)
  {
    return CalendarUtils.isGregorianLeapYear(paramInt);
  }
  
  public boolean isLeapYear(Era paramEra, int paramInt)
  {
    if (paramEra == null) {
      return isLeapYear(paramInt);
    }
    int i = paramEra.getSinceDate().getYear() + paramInt - 1;
    return isLeapYear(i);
  }
  
  public void getCalendarDateFromFixedDate(CalendarDate paramCalendarDate, long paramLong)
  {
    Date localDate = (Date)paramCalendarDate;
    super.getCalendarDateFromFixedDate(localDate, paramLong);
    adjustYear(localDate, (paramLong - 719163L) * 86400000L, 0);
  }
  
  public static class Date
    extends BaseCalendar.Date
  {
    private int gregorianYear = Integer.MIN_VALUE;
    
    protected Date() {}
    
    protected Date(TimeZone paramTimeZone)
    {
      super();
    }
    
    public Date setEra(Era paramEra)
    {
      if (getEra() != paramEra)
      {
        super.setEra(paramEra);
        this.gregorianYear = Integer.MIN_VALUE;
      }
      return this;
    }
    
    public Date addYear(int paramInt)
    {
      super.addYear(paramInt);
      this.gregorianYear += paramInt;
      return this;
    }
    
    public Date setYear(int paramInt)
    {
      if (getYear() != paramInt)
      {
        super.setYear(paramInt);
        this.gregorianYear = Integer.MIN_VALUE;
      }
      return this;
    }
    
    public int getNormalizedYear()
    {
      return this.gregorianYear;
    }
    
    public void setNormalizedYear(int paramInt)
    {
      this.gregorianYear = paramInt;
    }
    
    void setLocalEra(Era paramEra)
    {
      super.setEra(paramEra);
    }
    
    void setLocalYear(int paramInt)
    {
      super.setYear(paramInt);
    }
    
    public String toString()
    {
      String str1 = super.toString();
      str1 = str1.substring(str1.indexOf('T'));
      StringBuffer localStringBuffer = new StringBuffer();
      Era localEra = getEra();
      if (localEra != null)
      {
        String str2 = localEra.getAbbreviation();
        if (str2 != null) {
          localStringBuffer.append(str2);
        }
      }
      localStringBuffer.append(getYear()).append('.');
      CalendarUtils.sprintf0d(localStringBuffer, getMonth(), 2).append('.');
      CalendarUtils.sprintf0d(localStringBuffer, getDayOfMonth(), 2);
      localStringBuffer.append(str1);
      return localStringBuffer.toString();
    }
  }
}
