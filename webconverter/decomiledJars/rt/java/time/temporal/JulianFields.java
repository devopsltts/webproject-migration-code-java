package java.time.temporal;

import java.time.DateTimeException;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.util.Map;

public final class JulianFields
{
  private static final long JULIAN_DAY_OFFSET = 2440588L;
  public static final TemporalField JULIAN_DAY = Field.JULIAN_DAY;
  public static final TemporalField MODIFIED_JULIAN_DAY = Field.MODIFIED_JULIAN_DAY;
  public static final TemporalField RATA_DIE = Field.RATA_DIE;
  
  private JulianFields()
  {
    throw new AssertionError("Not instantiable");
  }
  
  private static enum Field
    implements TemporalField
  {
    JULIAN_DAY("JulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 2440588L),  MODIFIED_JULIAN_DAY("ModifiedJulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 40587L),  RATA_DIE("RataDie", ChronoUnit.DAYS, ChronoUnit.FOREVER, 719163L);
    
    private static final long serialVersionUID = -7501623920830201812L;
    private final transient String name;
    private final transient TemporalUnit baseUnit;
    private final transient TemporalUnit rangeUnit;
    private final transient ValueRange range;
    private final transient long offset;
    
    private Field(String paramString, TemporalUnit paramTemporalUnit1, TemporalUnit paramTemporalUnit2, long paramLong)
    {
      this.name = paramString;
      this.baseUnit = paramTemporalUnit1;
      this.rangeUnit = paramTemporalUnit2;
      this.range = ValueRange.of(-365243219162L + paramLong, 365241780471L + paramLong);
      this.offset = paramLong;
    }
    
    public TemporalUnit getBaseUnit()
    {
      return this.baseUnit;
    }
    
    public TemporalUnit getRangeUnit()
    {
      return this.rangeUnit;
    }
    
    public boolean isDateBased()
    {
      return true;
    }
    
    public boolean isTimeBased()
    {
      return false;
    }
    
    public ValueRange range()
    {
      return this.range;
    }
    
    public boolean isSupportedBy(TemporalAccessor paramTemporalAccessor)
    {
      return paramTemporalAccessor.isSupported(ChronoField.EPOCH_DAY);
    }
    
    public ValueRange rangeRefinedBy(TemporalAccessor paramTemporalAccessor)
    {
      if (!isSupportedBy(paramTemporalAccessor)) {
        throw new DateTimeException("Unsupported field: " + this);
      }
      return range();
    }
    
    public long getFrom(TemporalAccessor paramTemporalAccessor)
    {
      return paramTemporalAccessor.getLong(ChronoField.EPOCH_DAY) + this.offset;
    }
    
    public <R extends Temporal> R adjustInto(R paramR, long paramLong)
    {
      if (!range().isValidValue(paramLong)) {
        throw new DateTimeException("Invalid value: " + this.name + " " + paramLong);
      }
      return paramR.with(ChronoField.EPOCH_DAY, Math.subtractExact(paramLong, this.offset));
    }
    
    public ChronoLocalDate resolve(Map<TemporalField, Long> paramMap, TemporalAccessor paramTemporalAccessor, ResolverStyle paramResolverStyle)
    {
      long l = ((Long)paramMap.remove(this)).longValue();
      Chronology localChronology = Chronology.from(paramTemporalAccessor);
      if (paramResolverStyle == ResolverStyle.LENIENT) {
        return localChronology.dateEpochDay(Math.subtractExact(l, this.offset));
      }
      range().checkValidValue(l, this);
      return localChronology.dateEpochDay(l - this.offset);
    }
    
    public String toString()
    {
      return this.name;
    }
  }
}
