package java.time.chrono;

import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;

public enum HijrahEra
  implements Era
{
  AH;
  
  private HijrahEra() {}
  
  public static HijrahEra of(int paramInt)
  {
    if (paramInt == 1) {
      return AH;
    }
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue()
  {
    return 1;
  }
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    if (paramTemporalField == ChronoField.ERA) {
      return ValueRange.of(1L, 1L);
    }
    return super.range(paramTemporalField);
  }
}
