package java.time.chrono;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import java.util.Objects;

public abstract interface ChronoLocalDateTime<D extends ChronoLocalDate>
  extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDateTime<?>>
{
  public static Comparator<ChronoLocalDateTime<?>> timeLineOrder()
  {
    return AbstractChronology.DATE_TIME_ORDER;
  }
  
  public static ChronoLocalDateTime<?> from(TemporalAccessor paramTemporalAccessor)
  {
    if ((paramTemporalAccessor instanceof ChronoLocalDateTime)) {
      return (ChronoLocalDateTime)paramTemporalAccessor;
    }
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology localChronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if (localChronology == null) {
      throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + paramTemporalAccessor.getClass());
    }
    return localChronology.localDateTime(paramTemporalAccessor);
  }
  
  public Chronology getChronology()
  {
    return toLocalDate().getChronology();
  }
  
  public abstract D toLocalDate();
  
  public abstract LocalTime toLocalTime();
  
  public abstract boolean isSupported(TemporalField paramTemporalField);
  
  public boolean isSupported(TemporalUnit paramTemporalUnit)
  {
    if ((paramTemporalUnit instanceof ChronoUnit)) {
      return paramTemporalUnit != ChronoUnit.FOREVER;
    }
    return (paramTemporalUnit != null) && (paramTemporalUnit.isSupportedBy(this));
  }
  
  public ChronoLocalDateTime<D> with(TemporalAdjuster paramTemporalAdjuster)
  {
    return ChronoLocalDateTimeImpl.ensureValid(getChronology(), super.with(paramTemporalAdjuster));
  }
  
  public abstract ChronoLocalDateTime<D> with(TemporalField paramTemporalField, long paramLong);
  
  public ChronoLocalDateTime<D> plus(TemporalAmount paramTemporalAmount)
  {
    return ChronoLocalDateTimeImpl.ensureValid(getChronology(), super.plus(paramTemporalAmount));
  }
  
  public abstract ChronoLocalDateTime<D> plus(long paramLong, TemporalUnit paramTemporalUnit);
  
  public ChronoLocalDateTime<D> minus(TemporalAmount paramTemporalAmount)
  {
    return ChronoLocalDateTimeImpl.ensureValid(getChronology(), super.minus(paramTemporalAmount));
  }
  
  public ChronoLocalDateTime<D> minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return ChronoLocalDateTimeImpl.ensureValid(getChronology(), super.minus(paramLong, paramTemporalUnit));
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery)
  {
    if ((paramTemporalQuery == TemporalQueries.zoneId()) || (paramTemporalQuery == TemporalQueries.zone()) || (paramTemporalQuery == TemporalQueries.offset())) {
      return null;
    }
    if (paramTemporalQuery == TemporalQueries.localTime()) {
      return toLocalTime();
    }
    if (paramTemporalQuery == TemporalQueries.chronology()) {
      return getChronology();
    }
    if (paramTemporalQuery == TemporalQueries.precision()) {
      return ChronoUnit.NANOS;
    }
    return paramTemporalQuery.queryFrom(this);
  }
  
  public Temporal adjustInto(Temporal paramTemporal)
  {
    return paramTemporal.with(ChronoField.EPOCH_DAY, toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, toLocalTime().toNanoOfDay());
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter)
  {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public abstract ChronoZonedDateTime<D> atZone(ZoneId paramZoneId);
  
  public Instant toInstant(ZoneOffset paramZoneOffset)
  {
    return Instant.ofEpochSecond(toEpochSecond(paramZoneOffset), toLocalTime().getNano());
  }
  
  public long toEpochSecond(ZoneOffset paramZoneOffset)
  {
    Objects.requireNonNull(paramZoneOffset, "offset");
    long l1 = toLocalDate().toEpochDay();
    long l2 = l1 * 86400L + toLocalTime().toSecondOfDay();
    l2 -= paramZoneOffset.getTotalSeconds();
    return l2;
  }
  
  public int compareTo(ChronoLocalDateTime<?> paramChronoLocalDateTime)
  {
    int i = toLocalDate().compareTo(paramChronoLocalDateTime.toLocalDate());
    if (i == 0)
    {
      i = toLocalTime().compareTo(paramChronoLocalDateTime.toLocalTime());
      if (i == 0) {
        i = getChronology().compareTo(paramChronoLocalDateTime.getChronology());
      }
    }
    return i;
  }
  
  public boolean isAfter(ChronoLocalDateTime<?> paramChronoLocalDateTime)
  {
    long l1 = toLocalDate().toEpochDay();
    long l2 = paramChronoLocalDateTime.toLocalDate().toEpochDay();
    return (l1 > l2) || ((l1 == l2) && (toLocalTime().toNanoOfDay() > paramChronoLocalDateTime.toLocalTime().toNanoOfDay()));
  }
  
  public boolean isBefore(ChronoLocalDateTime<?> paramChronoLocalDateTime)
  {
    long l1 = toLocalDate().toEpochDay();
    long l2 = paramChronoLocalDateTime.toLocalDate().toEpochDay();
    return (l1 < l2) || ((l1 == l2) && (toLocalTime().toNanoOfDay() < paramChronoLocalDateTime.toLocalTime().toNanoOfDay()));
  }
  
  public boolean isEqual(ChronoLocalDateTime<?> paramChronoLocalDateTime)
  {
    return (toLocalTime().toNanoOfDay() == paramChronoLocalDateTime.toLocalTime().toNanoOfDay()) && (toLocalDate().toEpochDay() == paramChronoLocalDateTime.toLocalDate().toEpochDay());
  }
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}
