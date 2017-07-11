package java.time.temporal;

import java.util.List;

public abstract interface TemporalAmount
{
  public abstract long get(TemporalUnit paramTemporalUnit);
  
  public abstract List<TemporalUnit> getUnits();
  
  public abstract Temporal addTo(Temporal paramTemporal);
  
  public abstract Temporal subtractFrom(Temporal paramTemporal);
}
