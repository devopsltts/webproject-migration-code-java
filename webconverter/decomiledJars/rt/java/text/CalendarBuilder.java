package java.text;

import java.util.Calendar;

class CalendarBuilder
{
  private static final int UNSET = 0;
  private static final int COMPUTED = 1;
  private static final int MINIMUM_USER_STAMP = 2;
  private static final int MAX_FIELD = 18;
  public static final int WEEK_YEAR = 17;
  public static final int ISO_DAY_OF_WEEK = 1000;
  private final int[] field = new int[36];
  private int nextStamp = 2;
  private int maxFieldIndex = -1;
  
  CalendarBuilder() {}
  
  CalendarBuilder set(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 1000)
    {
      paramInt1 = 7;
      paramInt2 = toCalendarDayOfWeek(paramInt2);
    }
    this.field[paramInt1] = (this.nextStamp++);
    this.field[(18 + paramInt1)] = paramInt2;
    if ((paramInt1 > this.maxFieldIndex) && (paramInt1 < 17)) {
      this.maxFieldIndex = paramInt1;
    }
    return this;
  }
  
  CalendarBuilder addYear(int paramInt)
  {
    this.field[19] += paramInt;
    this.field[35] += paramInt;
    return this;
  }
  
  boolean isSet(int paramInt)
  {
    if (paramInt == 1000) {
      paramInt = 7;
    }
    return this.field[paramInt] > 0;
  }
  
  CalendarBuilder clear(int paramInt)
  {
    if (paramInt == 1000) {
      paramInt = 7;
    }
    this.field[paramInt] = 0;
    this.field[(18 + paramInt)] = 0;
    return this;
  }
  
  Calendar establish(Calendar paramCalendar)
  {
    int i = (isSet(17)) && (this.field[17] > this.field[1]) ? 1 : 0;
    if ((i != 0) && (!paramCalendar.isWeekDateSupported()))
    {
      if (!isSet(1)) {
        set(1, this.field[35]);
      }
      i = 0;
    }
    paramCalendar.clear();
    int k;
    for (int j = 2; j < this.nextStamp; j++) {
      for (k = 0; k <= this.maxFieldIndex; k++) {
        if (this.field[k] == j)
        {
          paramCalendar.set(k, this.field[(18 + k)]);
          break;
        }
      }
    }
    if (i != 0)
    {
      j = isSet(3) ? this.field[21] : 1;
      k = isSet(7) ? this.field[25] : paramCalendar.getFirstDayOfWeek();
      if ((!isValidDayOfWeek(k)) && (paramCalendar.isLenient()))
      {
        if (k >= 8)
        {
          k--;
          j += k / 7;
          k = k % 7 + 1;
        }
        else
        {
          while (k <= 0)
          {
            k += 7;
            j--;
          }
        }
        k = toCalendarDayOfWeek(k);
      }
      paramCalendar.setWeekDate(this.field[35], j, k);
    }
    return paramCalendar;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("CalendarBuilder:[");
    for (int i = 0; i < this.field.length; i++) {
      if (isSet(i)) {
        localStringBuilder.append(i).append('=').append(this.field[(18 + i)]).append(',');
      }
    }
    i = localStringBuilder.length() - 1;
    if (localStringBuilder.charAt(i) == ',') {
      localStringBuilder.setLength(i);
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  static int toISODayOfWeek(int paramInt)
  {
    return paramInt == 1 ? 7 : paramInt - 1;
  }
  
  static int toCalendarDayOfWeek(int paramInt)
  {
    if (!isValidDayOfWeek(paramInt)) {
      return paramInt;
    }
    return paramInt == 7 ? 1 : paramInt + 1;
  }
  
  static boolean isValidDayOfWeek(int paramInt)
  {
    return (paramInt > 0) && (paramInt <= 7);
  }
}
