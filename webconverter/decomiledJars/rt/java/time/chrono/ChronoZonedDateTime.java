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
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.Objects;

public abstract interface ChronoZonedDateTime<D extends ChronoLocalDate>
  extends Temporal, Comparable<ChronoZonedDateTime<?>>
{
  public static Comparator<ChronoZonedDateTime<?>> timeLineOrder()
  {
    return AbstractChronology.INSTANT_ORDER;
  }
  
  public static ChronoZonedDateTime<?> from(TemporalAccessor paramTemporalAccessor)
  {
    if ((paramTemporalAccessor instanceof ChronoZonedDateTime)) {
      return (ChronoZonedDateTime)paramTemporalAccessor;
    }
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology localChronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if (localChronology == null) {
      throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + paramTemporalAccessor.getClass());
    }
    return localChronology.zonedDateTime(paramTemporalAccessor);
  }
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      if ((paramTemporalField == ChronoField.INSTANT_SECONDS) || (paramTemporalField == ChronoField.OFFSET_SECONDS)) {
        return paramTemporalField.range();
      }
      return toLocalDateTime().range(paramTemporalField);
    }
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public int get(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      switch (1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField)paramTemporalField).ordinal()])
      {
      case 1: 
        throw new UnsupportedTemporalTypeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
      case 2: 
        return getOffset().getTotalSeconds();
      }
      return toLocalDateTime().get(paramTemporalField);
    }
    return super.get(paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      switch (1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField)paramTemporalField).ordinal()])
      {
      case 1: 
        return toEpochSecond();
      case 2: 
        return getOffset().getTotalSeconds();
      }
      return toLocalDateTime().getLong(paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  public D toLocalDate()
  {
    return toLocalDateTime().toLocalDate();
  }
  
  public LocalTime toLocalTime()
  {
    return toLocalDateTime().toLocalTime();
  }
  
  public abstract ChronoLocalDateTime<D> toLocalDateTime();
  
  public Chronology getChronology()
  {
    return toLocalDate().getChronology();
  }
  
  public abstract ZoneOffset getOffset();
  
  public abstract ZoneId getZone();
  
  public abstract ChronoZonedDateTime<D> withEarlierOffsetAtOverlap();
  
  public abstract ChronoZonedDateTime<D> withLaterOffsetAtOverlap();
  
  public abstract ChronoZonedDateTime<D> withZoneSameLocal(ZoneId paramZoneId);
  
  public abstract ChronoZonedDateTime<D> withZoneSameInstant(ZoneId paramZoneId);
  
  public abstract boolean isSupported(TemporalField paramTemporalField);
  
  public boolean isSupported(TemporalUnit paramTemporalUnit)
  {
    if ((paramTemporalUnit instanceof ChronoUnit)) {
      return paramTemporalUnit != ChronoUnit.FOREVER;
    }
    return (paramTemporalUnit != null) && (paramTemporalUnit.isSupportedBy(this));
  }
  
  public ChronoZonedDateTime<D> with(TemporalAdjuster paramTemporalAdjuster)
  {
    return ChronoZonedDateTimeImpl.ensureValid(getChronology(), super.with(paramTemporalAdjuster));
  }
  
  public abstract ChronoZonedDateTime<D> with(TemporalField paramTemporalField, long paramLong);
  
  public ChronoZonedDateTime<D> plus(TemporalAmount paramTemporalAmount)
  {
    return ChronoZonedDateTimeImpl.ensureValid(getChronology(), super.plus(paramTemporalAmount));
  }
  
  public abstract ChronoZonedDateTime<D> plus(long paramLong, TemporalUnit paramTemporalUnit);
  
  public ChronoZonedDateTime<D> minus(TemporalAmount paramTemporalAmount)
  {
    return ChronoZonedDateTimeImpl.ensureValid(getChronology(), super.minus(paramTemporalAmount));
  }
  
  public ChronoZonedDateTime<D> minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return ChronoZonedDateTimeImpl.ensureValid(getChronology(), super.minus(paramLong, paramTemporalUnit));
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery)
  {
    if ((paramTemporalQuery == TemporalQueries.zone()) || (paramTemporalQuery == TemporalQueries.zoneId())) {
      return getZone();
    }
    if (paramTemporalQuery == TemporalQueries.offset()) {
      return getOffset();
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
  
  public String format(DateTimeFormatter paramDateTimeFormatter)
  {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public Instant toInstant()
  {
    return Instant.ofEpochSecond(toEpochSecond(), toLocalTime().getNano());
  }
  
  public long toEpochSecond()
  {
    long l1 = toLocalDate().toEpochDay();
    long l2 = l1 * 86400L + toLocalTime().toSecondOfDay();
    l2 -= getOffset().getTotalSeconds();
    return l2;
  }
  
  public int compareTo(ChronoZonedDateTime<?> paramChronoZonedDateTime)
  {
    int i = Long.compare(toEpochSecond(), paramChronoZonedDateTime.toEpochSecond());
    if (i == 0)
    {
      i = toLocalTime().getNano() - paramChronoZonedDateTime.toLocalTime().getNano();
      if (i == 0)
      {
        i = toLocalDateTime().compareTo(paramChronoZonedDateTime.toLocalDateTime());
        if (i == 0)
        {
          i = getZone().getId().compareTo(paramChronoZonedDateTime.getZone().getId());
          if (i == 0) {
            i = getChronology().compareTo(paramChronoZonedDateTime.getChronology());
          }
        }
      }
    }
    return i;
  }
  
  public boolean isBefore(ChronoZonedDateTime<?> paramChronoZonedDateTime)
  {
    long l1 = toEpochSecond();
    long l2 = paramChronoZonedDateTime.toEpochSecond();
    return (l1 < l2) || ((l1 == l2) && (toLocalTime().getNano() < paramChronoZonedDateTime.toLocalTime().getNano()));
  }
  
  public boolean isAfter(ChronoZonedDateTime<?> paramChronoZonedDateTime)
  {
    long l1 = toEpochSecond();
    long l2 = paramChronoZonedDateTime.toEpochSecond();
    return (l1 > l2) || ((l1 == l2) && (toLocalTime().getNano() > paramChronoZonedDateTime.toLocalTime().getNano()));
  }
  
  public boolean isEqual(ChronoZonedDateTime<?> paramChronoZonedDateTime)
  {
    return (toEpochSecond() == paramChronoZonedDateTime.toEpochSecond()) && (toLocalTime().getNano() == paramChronoZonedDateTime.toLocalTime().getNano());
  }
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}
