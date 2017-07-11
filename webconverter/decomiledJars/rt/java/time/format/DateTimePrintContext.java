package java.time.format;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;
import java.util.Locale;
import java.util.Objects;

final class DateTimePrintContext
{
  private TemporalAccessor temporal;
  private DateTimeFormatter formatter;
  private int optional;
  
  DateTimePrintContext(TemporalAccessor paramTemporalAccessor, DateTimeFormatter paramDateTimeFormatter)
  {
    this.temporal = adjust(paramTemporalAccessor, paramDateTimeFormatter);
    this.formatter = paramDateTimeFormatter;
  }
  
  private static TemporalAccessor adjust(final TemporalAccessor paramTemporalAccessor, DateTimeFormatter paramDateTimeFormatter)
  {
    Chronology localChronology1 = paramDateTimeFormatter.getChronology();
    ZoneId localZoneId1 = paramDateTimeFormatter.getZone();
    if ((localChronology1 == null) && (localZoneId1 == null)) {
      return paramTemporalAccessor;
    }
    Chronology localChronology2 = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    ZoneId localZoneId2 = (ZoneId)paramTemporalAccessor.query(TemporalQueries.zoneId());
    if (Objects.equals(localChronology1, localChronology2)) {
      localChronology1 = null;
    }
    if (Objects.equals(localZoneId1, localZoneId2)) {
      localZoneId1 = null;
    }
    if ((localChronology1 == null) && (localZoneId1 == null)) {
      return paramTemporalAccessor;
    }
    final Chronology localChronology3 = localChronology1 != null ? localChronology1 : localChronology2;
    if (localZoneId1 != null)
    {
      if (paramTemporalAccessor.isSupported(ChronoField.INSTANT_SECONDS))
      {
        localObject = localChronology3 != null ? localChronology3 : IsoChronology.INSTANCE;
        return ((Chronology)localObject).zonedDateTime(Instant.from(paramTemporalAccessor), localZoneId1);
      }
      if (((localZoneId1.normalized() instanceof ZoneOffset)) && (paramTemporalAccessor.isSupported(ChronoField.OFFSET_SECONDS)) && (paramTemporalAccessor.get(ChronoField.OFFSET_SECONDS) != localZoneId1.getRules().getOffset(Instant.EPOCH).getTotalSeconds())) {
        throw new DateTimeException("Unable to apply override zone '" + localZoneId1 + "' because the temporal object being formatted has a different offset but" + " does not represent an instant: " + paramTemporalAccessor);
      }
    }
    Object localObject = localZoneId1 != null ? localZoneId1 : localZoneId2;
    ChronoLocalDate localChronoLocalDate;
    if (localChronology1 != null)
    {
      if (paramTemporalAccessor.isSupported(ChronoField.EPOCH_DAY))
      {
        localChronoLocalDate = localChronology3.date(paramTemporalAccessor);
      }
      else
      {
        if ((localChronology1 != IsoChronology.INSTANCE) || (localChronology2 != null)) {
          for (ChronoField localChronoField : ChronoField.values()) {
            if ((localChronoField.isDateBased()) && (paramTemporalAccessor.isSupported(localChronoField))) {
              throw new DateTimeException("Unable to apply override chronology '" + localChronology1 + "' because the temporal object being formatted contains date fields but" + " does not represent a whole date: " + paramTemporalAccessor);
            }
          }
        }
        localChronoLocalDate = null;
      }
    }
    else {
      localChronoLocalDate = null;
    }
    new TemporalAccessor()
    {
      public boolean isSupported(TemporalField paramAnonymousTemporalField)
      {
        if ((this.val$effectiveDate != null) && (paramAnonymousTemporalField.isDateBased())) {
          return this.val$effectiveDate.isSupported(paramAnonymousTemporalField);
        }
        return paramTemporalAccessor.isSupported(paramAnonymousTemporalField);
      }
      
      public ValueRange range(TemporalField paramAnonymousTemporalField)
      {
        if ((this.val$effectiveDate != null) && (paramAnonymousTemporalField.isDateBased())) {
          return this.val$effectiveDate.range(paramAnonymousTemporalField);
        }
        return paramTemporalAccessor.range(paramAnonymousTemporalField);
      }
      
      public long getLong(TemporalField paramAnonymousTemporalField)
      {
        if ((this.val$effectiveDate != null) && (paramAnonymousTemporalField.isDateBased())) {
          return this.val$effectiveDate.getLong(paramAnonymousTemporalField);
        }
        return paramTemporalAccessor.getLong(paramAnonymousTemporalField);
      }
      
      public <R> R query(TemporalQuery<R> paramAnonymousTemporalQuery)
      {
        if (paramAnonymousTemporalQuery == TemporalQueries.chronology()) {
          return localChronology3;
        }
        if (paramAnonymousTemporalQuery == TemporalQueries.zoneId()) {
          return this.val$effectiveZone;
        }
        if (paramAnonymousTemporalQuery == TemporalQueries.precision()) {
          return paramTemporalAccessor.query(paramAnonymousTemporalQuery);
        }
        return paramAnonymousTemporalQuery.queryFrom(this);
      }
    };
  }
  
  TemporalAccessor getTemporal()
  {
    return this.temporal;
  }
  
  Locale getLocale()
  {
    return this.formatter.getLocale();
  }
  
  DecimalStyle getDecimalStyle()
  {
    return this.formatter.getDecimalStyle();
  }
  
  void startOptional()
  {
    this.optional += 1;
  }
  
  void endOptional()
  {
    this.optional -= 1;
  }
  
  <R> R getValue(TemporalQuery<R> paramTemporalQuery)
  {
    Object localObject = this.temporal.query(paramTemporalQuery);
    if ((localObject == null) && (this.optional == 0)) {
      throw new DateTimeException("Unable to extract value: " + this.temporal.getClass());
    }
    return localObject;
  }
  
  Long getValue(TemporalField paramTemporalField)
  {
    try
    {
      return Long.valueOf(this.temporal.getLong(paramTemporalField));
    }
    catch (DateTimeException localDateTimeException)
    {
      if (this.optional > 0) {
        return null;
      }
      throw localDateTimeException;
    }
  }
  
  public String toString()
  {
    return this.temporal.toString();
  }
}
