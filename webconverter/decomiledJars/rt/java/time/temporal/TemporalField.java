package java.time.temporal;

import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public abstract interface TemporalField
{
  public String getDisplayName(Locale paramLocale)
  {
    Objects.requireNonNull(paramLocale, "locale");
    return toString();
  }
  
  public abstract TemporalUnit getBaseUnit();
  
  public abstract TemporalUnit getRangeUnit();
  
  public abstract ValueRange range();
  
  public abstract boolean isDateBased();
  
  public abstract boolean isTimeBased();
  
  public abstract boolean isSupportedBy(TemporalAccessor paramTemporalAccessor);
  
  public abstract ValueRange rangeRefinedBy(TemporalAccessor paramTemporalAccessor);
  
  public abstract long getFrom(TemporalAccessor paramTemporalAccessor);
  
  public abstract <R extends Temporal> R adjustInto(R paramR, long paramLong);
  
  public TemporalAccessor resolve(Map<TemporalField, Long> paramMap, TemporalAccessor paramTemporalAccessor, ResolverStyle paramResolverStyle)
  {
    return null;
  }
  
  public abstract String toString();
}
