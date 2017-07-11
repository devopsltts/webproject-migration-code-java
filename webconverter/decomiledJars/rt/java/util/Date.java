package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.time.Instant;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.BaseCalendar.Date;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.ZoneInfo;

public class Date
  implements Serializable, Cloneable, Comparable<Date>
{
  private static final BaseCalendar gcal = ;
  private static BaseCalendar jcal;
  private transient long fastTime;
  private transient BaseCalendar.Date cdate;
  private static int defaultCenturyStart;
  private static final long serialVersionUID = 7523967970034938905L;
  private static final String[] wtb = { "am", "pm", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december", "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt" };
  private static final int[] ttb = { 14, 1, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 10000, 10000, 10000, 10300, 10240, 10360, 10300, 10420, 10360, 10480, 10420 };
  
  public Date()
  {
    this(System.currentTimeMillis());
  }
  
  public Date(long paramLong)
  {
    this.fastTime = paramLong;
  }
  
  @Deprecated
  public Date(int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramInt1, paramInt2, paramInt3, 0, 0, 0);
  }
  
  @Deprecated
  public Date(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0);
  }
  
  @Deprecated
  public Date(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    int i = paramInt1 + 1900;
    if (paramInt2 >= 12)
    {
      i += paramInt2 / 12;
      paramInt2 %= 12;
    }
    else if (paramInt2 < 0)
    {
      i += CalendarUtils.floorDivide(paramInt2, 12);
      paramInt2 = CalendarUtils.mod(paramInt2, 12);
    }
    BaseCalendar localBaseCalendar = getCalendarSystem(i);
    this.cdate = ((BaseCalendar.Date)localBaseCalendar.newCalendarDate(TimeZone.getDefaultRef()));
    this.cdate.setNormalizedDate(i, paramInt2 + 1, paramInt3).setTimeOfDay(paramInt4, paramInt5, paramInt6, 0);
    getTimeImpl();
    this.cdate = null;
  }
  
  @Deprecated
  public Date(String paramString)
  {
    this(parse(paramString));
  }
  
  public Object clone()
  {
    Date localDate = null;
    try
    {
      localDate = (Date)super.clone();
      if (this.cdate != null) {
        localDate.cdate = ((BaseCalendar.Date)this.cdate.clone());
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return localDate;
  }
  
  @Deprecated
  public static long UTC(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    int i = paramInt1 + 1900;
    if (paramInt2 >= 12)
    {
      i += paramInt2 / 12;
      paramInt2 %= 12;
    }
    else if (paramInt2 < 0)
    {
      i += CalendarUtils.floorDivide(paramInt2, 12);
      paramInt2 = CalendarUtils.mod(paramInt2, 12);
    }
    int j = paramInt2 + 1;
    BaseCalendar localBaseCalendar = getCalendarSystem(i);
    BaseCalendar.Date localDate = (BaseCalendar.Date)localBaseCalendar.newCalendarDate(null);
    localDate.setNormalizedDate(i, j, paramInt3).setTimeOfDay(paramInt4, paramInt5, paramInt6, 0);
    Date localDate1 = new Date(0L);
    localDate1.normalize(localDate);
    return localDate1.fastTime;
  }
  
  @Deprecated
  public static long parse(String paramString)
  {
    int i = Integer.MIN_VALUE;
    int j = -1;
    int k = -1;
    int m = -1;
    int n = -1;
    int i1 = -1;
    int i2 = -1;
    int i3 = -1;
    int i4 = 0;
    int i5 = -1;
    int i6 = -1;
    int i7 = -1;
    int i8 = 0;
    if (paramString != null)
    {
      int i9 = paramString.length();
      for (;;)
      {
        if (i4 >= i9) {
          break label783;
        }
        i3 = paramString.charAt(i4);
        i4++;
        if ((i3 > 32) && (i3 != 44))
        {
          int i10;
          if (i3 == 40)
          {
            for (i10 = 1; i4 < i9; i10++)
            {
              i3 = paramString.charAt(i4);
              i4++;
              if (i3 != 40) {
                break label126;
              }
            }
            continue;
            label126:
            if (i3 != 41) {
              break;
            }
            i10--;
            if (i10 > 0) {
              break;
            }
            continue;
          }
          if ((48 <= i3) && (i3 <= 57))
          {
            i5 = i3 - 48;
            while ((i4 < i9) && ('0' <= (i3 = paramString.charAt(i4))) && (i3 <= 57))
            {
              i5 = i5 * 10 + i3 - 48;
              i4++;
            }
            if ((i8 == 43) || ((i8 == 45) && (i != Integer.MIN_VALUE)))
            {
              if (i5 < 24) {
                i5 *= 60;
              } else {
                i5 = i5 % 100 + i5 / 100 * 60;
              }
              if (i8 == 43) {
                i5 = -i5;
              }
              if ((i7 != 0) && (i7 != -1)) {
                break label1000;
              }
              i7 = i5;
            }
            else if (i5 >= 70)
            {
              if ((i != Integer.MIN_VALUE) || ((i3 > 32) && (i3 != 44) && (i3 != 47) && (i4 < i9))) {
                break label1000;
              }
              i = i5;
            }
            else if (i3 == 58)
            {
              if (m < 0)
              {
                m = (byte)i5;
              }
              else
              {
                if (n >= 0) {
                  break label1000;
                }
                n = (byte)i5;
              }
            }
            else if (i3 == 47)
            {
              if (j < 0)
              {
                j = (byte)(i5 - 1);
              }
              else
              {
                if (k >= 0) {
                  break label1000;
                }
                k = (byte)i5;
              }
            }
            else
            {
              if ((i4 < i9) && (i3 != 44) && (i3 > 32) && (i3 != 45)) {
                break label1000;
              }
              if ((m >= 0) && (n < 0))
              {
                n = (byte)i5;
              }
              else if ((n >= 0) && (i1 < 0))
              {
                i1 = (byte)i5;
              }
              else if (k < 0)
              {
                k = (byte)i5;
              }
              else
              {
                if ((i != Integer.MIN_VALUE) || (j < 0) || (k < 0)) {
                  break label1000;
                }
                i = i5;
              }
            }
            i8 = 0;
          }
          else if ((i3 == 47) || (i3 == 58) || (i3 == 43) || (i3 == 45))
          {
            i8 = i3;
          }
          else
          {
            i10 = i4 - 1;
            while (i4 < i9)
            {
              i3 = paramString.charAt(i4);
              if (((65 > i3) || (i3 > 90)) && ((97 > i3) || (i3 > 122))) {
                break;
              }
              i4++;
            }
            if (i4 <= i10 + 1) {
              break label1000;
            }
            int i11 = wtb.length;
            do
            {
              i11--;
              if (i11 < 0) {
                break;
              }
            } while (!wtb[i11].regionMatches(true, 0, paramString, i10, i4 - i10));
            int i12 = ttb[i11];
            if (i12 != 0) {
              if (i12 == 1)
              {
                if ((m > 12) || (m < 1)) {
                  break label1000;
                }
                if (m < 12) {
                  m += 12;
                }
              }
              else if (i12 == 14)
              {
                if ((m > 12) || (m < 1)) {
                  break label1000;
                }
                if (m == 12) {
                  m = 0;
                }
              }
              else if (i12 <= 13)
              {
                if (j >= 0) {
                  break label1000;
                }
                j = (byte)(i12 - 2);
              }
              else
              {
                i7 = i12 - 10000;
              }
            }
            if (i11 < 0) {
              break label1000;
            }
            i8 = 0;
          }
        }
      }
      label783:
      if ((i != Integer.MIN_VALUE) && (j >= 0) && (k >= 0))
      {
        if (i < 100)
        {
          synchronized (Date.class)
          {
            if (defaultCenturyStart == 0) {
              defaultCenturyStart = gcal.getCalendarDate().getYear() - 80;
            }
          }
          i += defaultCenturyStart / 100 * 100;
          if (i < defaultCenturyStart) {
            i += 100;
          }
        }
        if (i1 < 0) {
          i1 = 0;
        }
        if (n < 0) {
          n = 0;
        }
        if (m < 0) {
          m = 0;
        }
        ??? = getCalendarSystem(i);
        if (i7 == -1)
        {
          localDate = (BaseCalendar.Date)((BaseCalendar)???).newCalendarDate(TimeZone.getDefaultRef());
          localDate.setDate(i, j + 1, k);
          localDate.setTimeOfDay(m, n, i1, 0);
          return ((BaseCalendar)???).getTime(localDate);
        }
        BaseCalendar.Date localDate = (BaseCalendar.Date)((BaseCalendar)???).newCalendarDate(null);
        localDate.setDate(i, j + 1, k);
        localDate.setTimeOfDay(m, n, i1, 0);
        return ((BaseCalendar)???).getTime(localDate) + i7 * 60000;
      }
    }
    label1000:
    throw new IllegalArgumentException();
  }
  
  @Deprecated
  public int getYear()
  {
    return normalize().getYear() - 1900;
  }
  
  @Deprecated
  public void setYear(int paramInt)
  {
    getCalendarDate().setNormalizedYear(paramInt + 1900);
  }
  
  @Deprecated
  public int getMonth()
  {
    return normalize().getMonth() - 1;
  }
  
  @Deprecated
  public void setMonth(int paramInt)
  {
    int i = 0;
    if (paramInt >= 12)
    {
      i = paramInt / 12;
      paramInt %= 12;
    }
    else if (paramInt < 0)
    {
      i = CalendarUtils.floorDivide(paramInt, 12);
      paramInt = CalendarUtils.mod(paramInt, 12);
    }
    BaseCalendar.Date localDate = getCalendarDate();
    if (i != 0) {
      localDate.setNormalizedYear(localDate.getNormalizedYear() + i);
    }
    localDate.setMonth(paramInt + 1);
  }
  
  @Deprecated
  public int getDate()
  {
    return normalize().getDayOfMonth();
  }
  
  @Deprecated
  public void setDate(int paramInt)
  {
    getCalendarDate().setDayOfMonth(paramInt);
  }
  
  @Deprecated
  public int getDay()
  {
    return normalize().getDayOfWeek() - 1;
  }
  
  @Deprecated
  public int getHours()
  {
    return normalize().getHours();
  }
  
  @Deprecated
  public void setHours(int paramInt)
  {
    getCalendarDate().setHours(paramInt);
  }
  
  @Deprecated
  public int getMinutes()
  {
    return normalize().getMinutes();
  }
  
  @Deprecated
  public void setMinutes(int paramInt)
  {
    getCalendarDate().setMinutes(paramInt);
  }
  
  @Deprecated
  public int getSeconds()
  {
    return normalize().getSeconds();
  }
  
  @Deprecated
  public void setSeconds(int paramInt)
  {
    getCalendarDate().setSeconds(paramInt);
  }
  
  public long getTime()
  {
    return getTimeImpl();
  }
  
  private final long getTimeImpl()
  {
    if ((this.cdate != null) && (!this.cdate.isNormalized())) {
      normalize();
    }
    return this.fastTime;
  }
  
  public void setTime(long paramLong)
  {
    this.fastTime = paramLong;
    this.cdate = null;
  }
  
  public boolean before(Date paramDate)
  {
    return getMillisOf(this) < getMillisOf(paramDate);
  }
  
  public boolean after(Date paramDate)
  {
    return getMillisOf(this) > getMillisOf(paramDate);
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Date)) && (getTime() == ((Date)paramObject).getTime());
  }
  
  static final long getMillisOf(Date paramDate)
  {
    if ((paramDate.cdate == null) || (paramDate.cdate.isNormalized())) {
      return paramDate.fastTime;
    }
    BaseCalendar.Date localDate = (BaseCalendar.Date)paramDate.cdate.clone();
    return gcal.getTime(localDate);
  }
  
  public int compareTo(Date paramDate)
  {
    long l1 = getMillisOf(this);
    long l2 = getMillisOf(paramDate);
    return l1 == l2 ? 0 : l1 < l2 ? -1 : 1;
  }
  
  public int hashCode()
  {
    long l = getTime();
    return (int)l ^ (int)(l >> 32);
  }
  
  public String toString()
  {
    BaseCalendar.Date localDate = normalize();
    StringBuilder localStringBuilder = new StringBuilder(28);
    int i = localDate.getDayOfWeek();
    if (i == 1) {
      i = 8;
    }
    convertToAbbr(localStringBuilder, wtb[i]).append(' ');
    convertToAbbr(localStringBuilder, wtb[(localDate.getMonth() - 1 + 2 + 7)]).append(' ');
    CalendarUtils.sprintf0d(localStringBuilder, localDate.getDayOfMonth(), 2).append(' ');
    CalendarUtils.sprintf0d(localStringBuilder, localDate.getHours(), 2).append(':');
    CalendarUtils.sprintf0d(localStringBuilder, localDate.getMinutes(), 2).append(':');
    CalendarUtils.sprintf0d(localStringBuilder, localDate.getSeconds(), 2).append(' ');
    TimeZone localTimeZone = localDate.getZone();
    if (localTimeZone != null) {
      localStringBuilder.append(localTimeZone.getDisplayName(localDate.isDaylightTime(), 0, Locale.US));
    } else {
      localStringBuilder.append("GMT");
    }
    localStringBuilder.append(' ').append(localDate.getYear());
    return localStringBuilder.toString();
  }
  
  private static final StringBuilder convertToAbbr(StringBuilder paramStringBuilder, String paramString)
  {
    paramStringBuilder.append(Character.toUpperCase(paramString.charAt(0)));
    paramStringBuilder.append(paramString.charAt(1)).append(paramString.charAt(2));
    return paramStringBuilder;
  }
  
  @Deprecated
  public String toLocaleString()
  {
    DateFormat localDateFormat = DateFormat.getDateTimeInstance();
    return localDateFormat.format(this);
  }
  
  @Deprecated
  public String toGMTString()
  {
    long l = getTime();
    BaseCalendar localBaseCalendar = getCalendarSystem(l);
    BaseCalendar.Date localDate = (BaseCalendar.Date)localBaseCalendar.getCalendarDate(getTime(), (TimeZone)null);
    StringBuilder localStringBuilder = new StringBuilder(32);
    CalendarUtils.sprintf0d(localStringBuilder, localDate.getDayOfMonth(), 1).append(' ');
    convertToAbbr(localStringBuilder, wtb[(localDate.getMonth() - 1 + 2 + 7)]).append(' ');
    localStringBuilder.append(localDate.getYear()).append(' ');
    CalendarUtils.sprintf0d(localStringBuilder, localDate.getHours(), 2).append(':');
    CalendarUtils.sprintf0d(localStringBuilder, localDate.getMinutes(), 2).append(':');
    CalendarUtils.sprintf0d(localStringBuilder, localDate.getSeconds(), 2);
    localStringBuilder.append(" GMT");
    return localStringBuilder.toString();
  }
  
  @Deprecated
  public int getTimezoneOffset()
  {
    int i;
    if (this.cdate == null)
    {
      TimeZone localTimeZone = TimeZone.getDefaultRef();
      if ((localTimeZone instanceof ZoneInfo)) {
        i = ((ZoneInfo)localTimeZone).getOffsets(this.fastTime, null);
      } else {
        i = localTimeZone.getOffset(this.fastTime);
      }
    }
    else
    {
      normalize();
      i = this.cdate.getZoneOffset();
    }
    return -i / 60000;
  }
  
  private final BaseCalendar.Date getCalendarDate()
  {
    if (this.cdate == null)
    {
      BaseCalendar localBaseCalendar = getCalendarSystem(this.fastTime);
      this.cdate = ((BaseCalendar.Date)localBaseCalendar.getCalendarDate(this.fastTime, TimeZone.getDefaultRef()));
    }
    return this.cdate;
  }
  
  private final BaseCalendar.Date normalize()
  {
    if (this.cdate == null)
    {
      localObject = getCalendarSystem(this.fastTime);
      this.cdate = ((BaseCalendar.Date)((BaseCalendar)localObject).getCalendarDate(this.fastTime, TimeZone.getDefaultRef()));
      return this.cdate;
    }
    if (!this.cdate.isNormalized()) {
      this.cdate = normalize(this.cdate);
    }
    Object localObject = TimeZone.getDefaultRef();
    if (localObject != this.cdate.getZone())
    {
      this.cdate.setZone((TimeZone)localObject);
      BaseCalendar localBaseCalendar = getCalendarSystem(this.cdate);
      localBaseCalendar.getCalendarDate(this.fastTime, this.cdate);
    }
    return this.cdate;
  }
  
  private final BaseCalendar.Date normalize(BaseCalendar.Date paramDate)
  {
    int i = paramDate.getNormalizedYear();
    int j = paramDate.getMonth();
    int k = paramDate.getDayOfMonth();
    int m = paramDate.getHours();
    int n = paramDate.getMinutes();
    int i1 = paramDate.getSeconds();
    int i2 = paramDate.getMillis();
    TimeZone localTimeZone = paramDate.getZone();
    if ((i == 1582) || (i > 280000000) || (i < -280000000))
    {
      if (localTimeZone == null) {
        localTimeZone = TimeZone.getTimeZone("GMT");
      }
      localObject = new GregorianCalendar(localTimeZone);
      ((GregorianCalendar)localObject).clear();
      ((GregorianCalendar)localObject).set(14, i2);
      ((GregorianCalendar)localObject).set(i, j - 1, k, m, n, i1);
      this.fastTime = ((GregorianCalendar)localObject).getTimeInMillis();
      localBaseCalendar = getCalendarSystem(this.fastTime);
      paramDate = (BaseCalendar.Date)localBaseCalendar.getCalendarDate(this.fastTime, localTimeZone);
      return paramDate;
    }
    Object localObject = getCalendarSystem(i);
    if (localObject != getCalendarSystem(paramDate))
    {
      paramDate = (BaseCalendar.Date)((BaseCalendar)localObject).newCalendarDate(localTimeZone);
      paramDate.setNormalizedDate(i, j, k).setTimeOfDay(m, n, i1, i2);
    }
    this.fastTime = ((BaseCalendar)localObject).getTime(paramDate);
    BaseCalendar localBaseCalendar = getCalendarSystem(this.fastTime);
    if (localBaseCalendar != localObject)
    {
      paramDate = (BaseCalendar.Date)localBaseCalendar.newCalendarDate(localTimeZone);
      paramDate.setNormalizedDate(i, j, k).setTimeOfDay(m, n, i1, i2);
      this.fastTime = localBaseCalendar.getTime(paramDate);
    }
    return paramDate;
  }
  
  private static final BaseCalendar getCalendarSystem(int paramInt)
  {
    if (paramInt >= 1582) {
      return gcal;
    }
    return getJulianCalendar();
  }
  
  private static final BaseCalendar getCalendarSystem(long paramLong)
  {
    if ((paramLong >= 0L) || (paramLong >= -12219292800000L - TimeZone.getDefaultRef().getOffset(paramLong))) {
      return gcal;
    }
    return getJulianCalendar();
  }
  
  private static final BaseCalendar getCalendarSystem(BaseCalendar.Date paramDate)
  {
    if (jcal == null) {
      return gcal;
    }
    if (paramDate.getEra() != null) {
      return jcal;
    }
    return gcal;
  }
  
  private static final synchronized BaseCalendar getJulianCalendar()
  {
    if (jcal == null) {
      jcal = (BaseCalendar)CalendarSystem.forName("julian");
    }
    return jcal;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeLong(getTimeImpl());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.fastTime = paramObjectInputStream.readLong();
  }
  
  public static Date from(Instant paramInstant)
  {
    try
    {
      return new Date(paramInstant.toEpochMilli());
    }
    catch (ArithmeticException localArithmeticException)
    {
      throw new IllegalArgumentException(localArithmeticException);
    }
  }
  
  public Instant toInstant()
  {
    return Instant.ofEpochMilli(getTime());
  }
}
