package java.time;

import java.io.Serializable;
import java.util.Objects;

public abstract class Clock
{
  public static Clock systemUTC()
  {
    return new SystemClock(ZoneOffset.UTC);
  }
  
  public static Clock systemDefaultZone()
  {
    return new SystemClock(ZoneId.systemDefault());
  }
  
  public static Clock system(ZoneId paramZoneId)
  {
    Objects.requireNonNull(paramZoneId, "zone");
    return new SystemClock(paramZoneId);
  }
  
  public static Clock tickSeconds(ZoneId paramZoneId)
  {
    return new TickClock(system(paramZoneId), 1000000000L);
  }
  
  public static Clock tickMinutes(ZoneId paramZoneId)
  {
    return new TickClock(system(paramZoneId), 60000000000L);
  }
  
  public static Clock tick(Clock paramClock, Duration paramDuration)
  {
    Objects.requireNonNull(paramClock, "baseClock");
    Objects.requireNonNull(paramDuration, "tickDuration");
    if (paramDuration.isNegative()) {
      throw new IllegalArgumentException("Tick duration must not be negative");
    }
    long l = paramDuration.toNanos();
    if ((l % 1000000L != 0L) && (1000000000L % l != 0L)) {
      throw new IllegalArgumentException("Invalid tick duration");
    }
    if (l <= 1L) {
      return paramClock;
    }
    return new TickClock(paramClock, l);
  }
  
  public static Clock fixed(Instant paramInstant, ZoneId paramZoneId)
  {
    Objects.requireNonNull(paramInstant, "fixedInstant");
    Objects.requireNonNull(paramZoneId, "zone");
    return new FixedClock(paramInstant, paramZoneId);
  }
  
  public static Clock offset(Clock paramClock, Duration paramDuration)
  {
    Objects.requireNonNull(paramClock, "baseClock");
    Objects.requireNonNull(paramDuration, "offsetDuration");
    if (paramDuration.equals(Duration.ZERO)) {
      return paramClock;
    }
    return new OffsetClock(paramClock, paramDuration);
  }
  
  protected Clock() {}
  
  public abstract ZoneId getZone();
  
  public abstract Clock withZone(ZoneId paramZoneId);
  
  public long millis()
  {
    return instant().toEpochMilli();
  }
  
  public abstract Instant instant();
  
  public boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  static final class FixedClock
    extends Clock
    implements Serializable
  {
    private static final long serialVersionUID = 7430389292664866958L;
    private final Instant instant;
    private final ZoneId zone;
    
    FixedClock(Instant paramInstant, ZoneId paramZoneId)
    {
      this.instant = paramInstant;
      this.zone = paramZoneId;
    }
    
    public ZoneId getZone()
    {
      return this.zone;
    }
    
    public Clock withZone(ZoneId paramZoneId)
    {
      if (paramZoneId.equals(this.zone)) {
        return this;
      }
      return new FixedClock(this.instant, paramZoneId);
    }
    
    public long millis()
    {
      return this.instant.toEpochMilli();
    }
    
    public Instant instant()
    {
      return this.instant;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof FixedClock))
      {
        FixedClock localFixedClock = (FixedClock)paramObject;
        return (this.instant.equals(localFixedClock.instant)) && (this.zone.equals(localFixedClock.zone));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.instant.hashCode() ^ this.zone.hashCode();
    }
    
    public String toString()
    {
      return "FixedClock[" + this.instant + "," + this.zone + "]";
    }
  }
  
  static final class OffsetClock
    extends Clock
    implements Serializable
  {
    private static final long serialVersionUID = 2007484719125426256L;
    private final Clock baseClock;
    private final Duration offset;
    
    OffsetClock(Clock paramClock, Duration paramDuration)
    {
      this.baseClock = paramClock;
      this.offset = paramDuration;
    }
    
    public ZoneId getZone()
    {
      return this.baseClock.getZone();
    }
    
    public Clock withZone(ZoneId paramZoneId)
    {
      if (paramZoneId.equals(this.baseClock.getZone())) {
        return this;
      }
      return new OffsetClock(this.baseClock.withZone(paramZoneId), this.offset);
    }
    
    public long millis()
    {
      return Math.addExact(this.baseClock.millis(), this.offset.toMillis());
    }
    
    public Instant instant()
    {
      return this.baseClock.instant().plus(this.offset);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof OffsetClock))
      {
        OffsetClock localOffsetClock = (OffsetClock)paramObject;
        return (this.baseClock.equals(localOffsetClock.baseClock)) && (this.offset.equals(localOffsetClock.offset));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.baseClock.hashCode() ^ this.offset.hashCode();
    }
    
    public String toString()
    {
      return "OffsetClock[" + this.baseClock + "," + this.offset + "]";
    }
  }
  
  static final class SystemClock
    extends Clock
    implements Serializable
  {
    private static final long serialVersionUID = 6740630888130243051L;
    private final ZoneId zone;
    
    SystemClock(ZoneId paramZoneId)
    {
      this.zone = paramZoneId;
    }
    
    public ZoneId getZone()
    {
      return this.zone;
    }
    
    public Clock withZone(ZoneId paramZoneId)
    {
      if (paramZoneId.equals(this.zone)) {
        return this;
      }
      return new SystemClock(paramZoneId);
    }
    
    public long millis()
    {
      return System.currentTimeMillis();
    }
    
    public Instant instant()
    {
      return Instant.ofEpochMilli(millis());
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof SystemClock)) {
        return this.zone.equals(((SystemClock)paramObject).zone);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.zone.hashCode() + 1;
    }
    
    public String toString()
    {
      return "SystemClock[" + this.zone + "]";
    }
  }
  
  static final class TickClock
    extends Clock
    implements Serializable
  {
    private static final long serialVersionUID = 6504659149906368850L;
    private final Clock baseClock;
    private final long tickNanos;
    
    TickClock(Clock paramClock, long paramLong)
    {
      this.baseClock = paramClock;
      this.tickNanos = paramLong;
    }
    
    public ZoneId getZone()
    {
      return this.baseClock.getZone();
    }
    
    public Clock withZone(ZoneId paramZoneId)
    {
      if (paramZoneId.equals(this.baseClock.getZone())) {
        return this;
      }
      return new TickClock(this.baseClock.withZone(paramZoneId), this.tickNanos);
    }
    
    public long millis()
    {
      long l = this.baseClock.millis();
      return l - Math.floorMod(l, this.tickNanos / 1000000L);
    }
    
    public Instant instant()
    {
      if (this.tickNanos % 1000000L == 0L)
      {
        long l1 = this.baseClock.millis();
        return Instant.ofEpochMilli(l1 - Math.floorMod(l1, this.tickNanos / 1000000L));
      }
      Instant localInstant = this.baseClock.instant();
      long l2 = localInstant.getNano();
      long l3 = Math.floorMod(l2, this.tickNanos);
      return localInstant.minusNanos(l3);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof TickClock))
      {
        TickClock localTickClock = (TickClock)paramObject;
        return (this.baseClock.equals(localTickClock.baseClock)) && (this.tickNanos == localTickClock.tickNanos);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.baseClock.hashCode() ^ (int)(this.tickNanos ^ this.tickNanos >>> 32);
    }
    
    public String toString()
    {
      return "TickClock[" + this.baseClock + "," + Duration.ofNanos(this.tickNanos) + "]";
    }
  }
}
