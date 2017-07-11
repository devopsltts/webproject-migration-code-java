package java.time.temporal;

public abstract interface Temporal
  extends TemporalAccessor
{
  public abstract boolean isSupported(TemporalUnit paramTemporalUnit);
  
  public Temporal with(TemporalAdjuster paramTemporalAdjuster)
  {
    return paramTemporalAdjuster.adjustInto(this);
  }
  
  public abstract Temporal with(TemporalField paramTemporalField, long paramLong);
  
  public Temporal plus(TemporalAmount paramTemporalAmount)
  {
    return paramTemporalAmount.addTo(this);
  }
  
  public abstract Temporal plus(long paramLong, TemporalUnit paramTemporalUnit);
  
  public Temporal minus(TemporalAmount paramTemporalAmount)
  {
    return paramTemporalAmount.subtractFrom(this);
  }
  
  public Temporal minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return paramLong == Long.MIN_VALUE ? plus(Long.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit);
  }
  
  public abstract long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit);
}
