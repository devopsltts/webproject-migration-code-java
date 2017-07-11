package java.time.chrono;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract interface Chronology
  extends Comparable<Chronology>
{
  public static Chronology from(TemporalAccessor paramTemporalAccessor)
  {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology localChronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    return localChronology != null ? localChronology : IsoChronology.INSTANCE;
  }
  
  public static Chronology ofLocale(Locale paramLocale)
  {
    return AbstractChronology.ofLocale(paramLocale);
  }
  
  public static Chronology of(String paramString)
  {
    return AbstractChronology.of(paramString);
  }
  
  public static Set<Chronology> getAvailableChronologies()
  {
    return AbstractChronology.getAvailableChronologies();
  }
  
  public abstract String getId();
  
  public abstract String getCalendarType();
  
  public ChronoLocalDate date(Era paramEra, int paramInt1, int paramInt2, int paramInt3)
  {
    return date(prolepticYear(paramEra, paramInt1), paramInt2, paramInt3);
  }
  
  public abstract ChronoLocalDate date(int paramInt1, int paramInt2, int paramInt3);
  
  public ChronoLocalDate dateYearDay(Era paramEra, int paramInt1, int paramInt2)
  {
    return dateYearDay(prolepticYear(paramEra, paramInt1), paramInt2);
  }
  
  public abstract ChronoLocalDate dateYearDay(int paramInt1, int paramInt2);
  
  public abstract ChronoLocalDate dateEpochDay(long paramLong);
  
  public ChronoLocalDate dateNow()
  {
    return dateNow(Clock.systemDefaultZone());
  }
  
  public ChronoLocalDate dateNow(ZoneId paramZoneId)
  {
    return dateNow(Clock.system(paramZoneId));
  }
  
  public ChronoLocalDate dateNow(Clock paramClock)
  {
    Objects.requireNonNull(paramClock, "clock");
    return date(LocalDate.now(paramClock));
  }
  
  public abstract ChronoLocalDate date(TemporalAccessor paramTemporalAccessor);
  
  public ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime(TemporalAccessor paramTemporalAccessor)
  {
    try
    {
      return date(paramTemporalAccessor).atTime(LocalTime.from(paramTemporalAccessor));
    }
    catch (DateTimeException localDateTimeException)
    {
      throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + paramTemporalAccessor.getClass(), localDateTimeException);
    }
  }
  
  /* Error */
  public ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(TemporalAccessor paramTemporalAccessor)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 206	java/time/ZoneId:from	(Ljava/time/temporal/TemporalAccessor;)Ljava/time/ZoneId;
    //   4: astore_2
    //   5: aload_1
    //   6: invokestatic 203	java/time/Instant:from	(Ljava/time/temporal/TemporalAccessor;)Ljava/time/Instant;
    //   9: astore_3
    //   10: aload_0
    //   11: aload_3
    //   12: aload_2
    //   13: invokeinterface 229 3 0
    //   18: areturn
    //   19: astore_3
    //   20: aload_0
    //   21: aload_0
    //   22: aload_1
    //   23: invokeinterface 228 2 0
    //   28: invokestatic 210	java/time/chrono/ChronoLocalDateTimeImpl:ensureValid	(Ljava/time/chrono/Chronology;Ljava/time/temporal/Temporal;)Ljava/time/chrono/ChronoLocalDateTimeImpl;
    //   31: astore 4
    //   33: aload 4
    //   35: aload_2
    //   36: aconst_null
    //   37: invokestatic 212	java/time/chrono/ChronoZonedDateTimeImpl:ofBest	(Ljava/time/chrono/ChronoLocalDateTimeImpl;Ljava/time/ZoneId;Ljava/time/ZoneOffset;)Ljava/time/chrono/ChronoZonedDateTime;
    //   40: areturn
    //   41: astore_2
    //   42: new 85	java/time/DateTimeException
    //   45: dup
    //   46: new 83	java/lang/StringBuilder
    //   49: dup
    //   50: invokespecial 196	java/lang/StringBuilder:<init>	()V
    //   53: ldc 2
    //   55: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: aload_1
    //   59: invokevirtual 195	java/lang/Object:getClass	()Ljava/lang/Class;
    //   62: invokevirtual 198	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   65: invokevirtual 197	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   68: aload_2
    //   69: invokespecial 202	java/time/DateTimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   72: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	Chronology
    //   0	73	1	paramTemporalAccessor	TemporalAccessor
    //   4	32	2	localZoneId	ZoneId
    //   41	28	2	localDateTimeException1	DateTimeException
    //   9	3	3	localInstant	Instant
    //   19	1	3	localDateTimeException2	DateTimeException
    //   31	3	4	localChronoLocalDateTimeImpl	ChronoLocalDateTimeImpl
    // Exception table:
    //   from	to	target	type
    //   5	18	19	java/time/DateTimeException
    //   0	18	41	java/time/DateTimeException
    //   19	40	41	java/time/DateTimeException
  }
  
  public ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(Instant paramInstant, ZoneId paramZoneId)
  {
    return ChronoZonedDateTimeImpl.ofInstant(this, paramInstant, paramZoneId);
  }
  
  public abstract boolean isLeapYear(long paramLong);
  
  public abstract int prolepticYear(Era paramEra, int paramInt);
  
  public abstract Era eraOf(int paramInt);
  
  public abstract List<Era> eras();
  
  public abstract ValueRange range(ChronoField paramChronoField);
  
  public String getDisplayName(TextStyle paramTextStyle, Locale paramLocale)
  {
    TemporalAccessor local1 = new TemporalAccessor()
    {
      public boolean isSupported(TemporalField paramAnonymousTemporalField)
      {
        return false;
      }
      
      public long getLong(TemporalField paramAnonymousTemporalField)
      {
        throw new UnsupportedTemporalTypeException("Unsupported field: " + paramAnonymousTemporalField);
      }
      
      public <R> R query(TemporalQuery<R> paramAnonymousTemporalQuery)
      {
        if (paramAnonymousTemporalQuery == TemporalQueries.chronology()) {
          return Chronology.this;
        }
        return super.query(paramAnonymousTemporalQuery);
      }
    };
    return new DateTimeFormatterBuilder().appendChronologyText(paramTextStyle).toFormatter(paramLocale).format(local1);
  }
  
  public abstract ChronoLocalDate resolveDate(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle);
  
  public ChronoPeriod period(int paramInt1, int paramInt2, int paramInt3)
  {
    return new ChronoPeriodImpl(this, paramInt1, paramInt2, paramInt3);
  }
  
  public abstract int compareTo(Chronology paramChronology);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}
