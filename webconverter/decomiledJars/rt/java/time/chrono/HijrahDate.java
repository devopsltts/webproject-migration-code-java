package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

public final class HijrahDate
  extends ChronoLocalDateImpl<HijrahDate>
  implements ChronoLocalDate, Serializable
{
  private static final long serialVersionUID = -5207853542612002020L;
  private final transient HijrahChronology chrono;
  private final transient int prolepticYear;
  private final transient int monthOfYear;
  private final transient int dayOfMonth;
  
  static HijrahDate of(HijrahChronology paramHijrahChronology, int paramInt1, int paramInt2, int paramInt3)
  {
    return new HijrahDate(paramHijrahChronology, paramInt1, paramInt2, paramInt3);
  }
  
  static HijrahDate ofEpochDay(HijrahChronology paramHijrahChronology, long paramLong)
  {
    return new HijrahDate(paramHijrahChronology, paramLong);
  }
  
  public static HijrahDate now()
  {
    return now(Clock.systemDefaultZone());
  }
  
  public static HijrahDate now(ZoneId paramZoneId)
  {
    return now(Clock.system(paramZoneId));
  }
  
  public static HijrahDate now(Clock paramClock)
  {
    return ofEpochDay(HijrahChronology.INSTANCE, LocalDate.now(paramClock).toEpochDay());
  }
  
  public static HijrahDate of(int paramInt1, int paramInt2, int paramInt3)
  {
    return HijrahChronology.INSTANCE.date(paramInt1, paramInt2, paramInt3);
  }
  
  public static HijrahDate from(TemporalAccessor paramTemporalAccessor)
  {
    return HijrahChronology.INSTANCE.date(paramTemporalAccessor);
  }
  
  private HijrahDate(HijrahChronology paramHijrahChronology, int paramInt1, int paramInt2, int paramInt3)
  {
    paramHijrahChronology.getEpochDay(paramInt1, paramInt2, paramInt3);
    this.chrono = paramHijrahChronology;
    this.prolepticYear = paramInt1;
    this.monthOfYear = paramInt2;
    this.dayOfMonth = paramInt3;
  }
  
  private HijrahDate(HijrahChronology paramHijrahChronology, long paramLong)
  {
    int[] arrayOfInt = paramHijrahChronology.getHijrahDateInfo((int)paramLong);
    this.chrono = paramHijrahChronology;
    this.prolepticYear = arrayOfInt[0];
    this.monthOfYear = arrayOfInt[1];
    this.dayOfMonth = arrayOfInt[2];
  }
  
  public HijrahChronology getChronology()
  {
    return this.chrono;
  }
  
  public HijrahEra getEra()
  {
    return HijrahEra.AH;
  }
  
  public int lengthOfMonth()
  {
    return this.chrono.getMonthLength(this.prolepticYear, this.monthOfYear);
  }
  
  public int lengthOfYear()
  {
    return this.chrono.getYearLength(this.prolepticYear);
  }
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      if (isSupported(paramTemporalField))
      {
        ChronoField localChronoField = (ChronoField)paramTemporalField;
        switch (1.$SwitchMap$java$time$temporal$ChronoField[localChronoField.ordinal()])
        {
        case 1: 
          return ValueRange.of(1L, lengthOfMonth());
        case 2: 
          return ValueRange.of(1L, lengthOfYear());
        case 3: 
          return ValueRange.of(1L, 5L);
        }
        return getChronology().range(localChronoField);
      }
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public long getLong(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      switch (1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField)paramTemporalField).ordinal()])
      {
      case 4: 
        return getDayOfWeek();
      case 5: 
        return (getDayOfWeek() - 1) % 7 + 1;
      case 6: 
        return (getDayOfYear() - 1) % 7 + 1;
      case 1: 
        return this.dayOfMonth;
      case 2: 
        return getDayOfYear();
      case 7: 
        return toEpochDay();
      case 3: 
        return (this.dayOfMonth - 1) / 7 + 1;
      case 8: 
        return (getDayOfYear() - 1) / 7 + 1;
      case 9: 
        return this.monthOfYear;
      case 10: 
        return getProlepticMonth();
      case 11: 
        return this.prolepticYear;
      case 12: 
        return this.prolepticYear;
      case 13: 
        return getEraValue();
      }
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  private long getProlepticMonth()
  {
    return this.prolepticYear * 12L + this.monthOfYear - 1L;
  }
  
  public HijrahDate with(TemporalField paramTemporalField, long paramLong)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      this.chrono.range(localChronoField).checkValidValue(paramLong, localChronoField);
      int i = (int)paramLong;
      switch (1.$SwitchMap$java$time$temporal$ChronoField[localChronoField.ordinal()])
      {
      case 4: 
        return plusDays(paramLong - getDayOfWeek());
      case 5: 
        return plusDays(paramLong - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
      case 6: 
        return plusDays(paramLong - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
      case 1: 
        return resolvePreviousValid(this.prolepticYear, this.monthOfYear, i);
      case 2: 
        return plusDays(Math.min(i, lengthOfYear()) - getDayOfYear());
      case 7: 
        return new HijrahDate(this.chrono, paramLong);
      case 3: 
        return plusDays((paramLong - getLong(ChronoField.ALIGNED_WEEK_OF_MONTH)) * 7L);
      case 8: 
        return plusDays((paramLong - getLong(ChronoField.ALIGNED_WEEK_OF_YEAR)) * 7L);
      case 9: 
        return resolvePreviousValid(this.prolepticYear, i, this.dayOfMonth);
      case 10: 
        return plusMonths(paramLong - getProlepticMonth());
      case 11: 
        return resolvePreviousValid(this.prolepticYear >= 1 ? i : 1 - i, this.monthOfYear, this.dayOfMonth);
      case 12: 
        return resolvePreviousValid(i, this.monthOfYear, this.dayOfMonth);
      case 13: 
        return resolvePreviousValid(1 - this.prolepticYear, this.monthOfYear, this.dayOfMonth);
      }
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return (HijrahDate)super.with(paramTemporalField, paramLong);
  }
  
  private HijrahDate resolvePreviousValid(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = this.chrono.getMonthLength(paramInt1, paramInt2);
    if (paramInt3 > i) {
      paramInt3 = i;
    }
    return of(this.chrono, paramInt1, paramInt2, paramInt3);
  }
  
  public HijrahDate with(TemporalAdjuster paramTemporalAdjuster)
  {
    return (HijrahDate)super.with(paramTemporalAdjuster);
  }
  
  public HijrahDate withVariant(HijrahChronology paramHijrahChronology)
  {
    if (this.chrono == paramHijrahChronology) {
      return this;
    }
    int i = paramHijrahChronology.getDayOfYear(this.prolepticYear, this.monthOfYear);
    return of(paramHijrahChronology, this.prolepticYear, this.monthOfYear, this.dayOfMonth > i ? i : this.dayOfMonth);
  }
  
  public HijrahDate plus(TemporalAmount paramTemporalAmount)
  {
    return (HijrahDate)super.plus(paramTemporalAmount);
  }
  
  public HijrahDate minus(TemporalAmount paramTemporalAmount)
  {
    return (HijrahDate)super.minus(paramTemporalAmount);
  }
  
  public long toEpochDay()
  {
    return this.chrono.getEpochDay(this.prolepticYear, this.monthOfYear, this.dayOfMonth);
  }
  
  private int getDayOfYear()
  {
    return this.chrono.getDayOfYear(this.prolepticYear, this.monthOfYear) + this.dayOfMonth;
  }
  
  private int getDayOfWeek()
  {
    int i = (int)Math.floorMod(toEpochDay() + 3L, 7L);
    return i + 1;
  }
  
  private int getEraValue()
  {
    return this.prolepticYear > 1 ? 1 : 0;
  }
  
  public boolean isLeapYear()
  {
    return this.chrono.isLeapYear(this.prolepticYear);
  }
  
  HijrahDate plusYears(long paramLong)
  {
    if (paramLong == 0L) {
      return this;
    }
    int i = Math.addExact(this.prolepticYear, (int)paramLong);
    return resolvePreviousValid(i, this.monthOfYear, this.dayOfMonth);
  }
  
  HijrahDate plusMonths(long paramLong)
  {
    if (paramLong == 0L) {
      return this;
    }
    long l1 = this.prolepticYear * 12L + (this.monthOfYear - 1);
    long l2 = l1 + paramLong;
    int i = this.chrono.checkValidYear(Math.floorDiv(l2, 12L));
    int j = (int)Math.floorMod(l2, 12L) + 1;
    return resolvePreviousValid(i, j, this.dayOfMonth);
  }
  
  HijrahDate plusWeeks(long paramLong)
  {
    return (HijrahDate)super.plusWeeks(paramLong);
  }
  
  HijrahDate plusDays(long paramLong)
  {
    return new HijrahDate(this.chrono, toEpochDay() + paramLong);
  }
  
  public HijrahDate plus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return (HijrahDate)super.plus(paramLong, paramTemporalUnit);
  }
  
  public HijrahDate minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return (HijrahDate)super.minus(paramLong, paramTemporalUnit);
  }
  
  HijrahDate minusYears(long paramLong)
  {
    return (HijrahDate)super.minusYears(paramLong);
  }
  
  HijrahDate minusMonths(long paramLong)
  {
    return (HijrahDate)super.minusMonths(paramLong);
  }
  
  HijrahDate minusWeeks(long paramLong)
  {
    return (HijrahDate)super.minusWeeks(paramLong);
  }
  
  HijrahDate minusDays(long paramLong)
  {
    return (HijrahDate)super.minusDays(paramLong);
  }
  
  public final ChronoLocalDateTime<HijrahDate> atTime(LocalTime paramLocalTime)
  {
    return super.atTime(paramLocalTime);
  }
  
  public ChronoPeriod until(ChronoLocalDate paramChronoLocalDate)
  {
    HijrahDate localHijrahDate1 = getChronology().date(paramChronoLocalDate);
    long l1 = (localHijrahDate1.prolepticYear - this.prolepticYear) * 12 + (localHijrahDate1.monthOfYear - this.monthOfYear);
    int i = localHijrahDate1.dayOfMonth - this.dayOfMonth;
    if ((l1 > 0L) && (i < 0))
    {
      l1 -= 1L;
      HijrahDate localHijrahDate2 = plusMonths(l1);
      i = (int)(localHijrahDate1.toEpochDay() - localHijrahDate2.toEpochDay());
    }
    else if ((l1 < 0L) && (i > 0))
    {
      l1 += 1L;
      i -= localHijrahDate1.lengthOfMonth();
    }
    long l2 = l1 / 12L;
    int j = (int)(l1 % 12L);
    return getChronology().period(Math.toIntExact(l2), j, i);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof HijrahDate))
    {
      HijrahDate localHijrahDate = (HijrahDate)paramObject;
      return (this.prolepticYear == localHijrahDate.prolepticYear) && (this.monthOfYear == localHijrahDate.monthOfYear) && (this.dayOfMonth == localHijrahDate.dayOfMonth) && (getChronology().equals(localHijrahDate.getChronology()));
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = this.prolepticYear;
    int j = this.monthOfYear;
    int k = this.dayOfMonth;
    return getChronology().getId().hashCode() ^ i & 0xF800 ^ (i << 11) + (j << 6) + k;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)6, this);
  }
  
  void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeObject(getChronology());
    paramObjectOutput.writeInt(get(ChronoField.YEAR));
    paramObjectOutput.writeByte(get(ChronoField.MONTH_OF_YEAR));
    paramObjectOutput.writeByte(get(ChronoField.DAY_OF_MONTH));
  }
  
  static HijrahDate readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    HijrahChronology localHijrahChronology = (HijrahChronology)paramObjectInput.readObject();
    int i = paramObjectInput.readInt();
    int j = paramObjectInput.readByte();
    int k = paramObjectInput.readByte();
    return localHijrahChronology.date(i, j, k);
  }
}
