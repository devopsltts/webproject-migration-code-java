package java.nio.file.attribute;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class FileTime
  implements Comparable<FileTime>
{
  private final TimeUnit unit;
  private final long value;
  private Instant instant;
  private String valueAsString;
  private static final long HOURS_PER_DAY = 24L;
  private static final long MINUTES_PER_HOUR = 60L;
  private static final long SECONDS_PER_MINUTE = 60L;
  private static final long SECONDS_PER_HOUR = 3600L;
  private static final long SECONDS_PER_DAY = 86400L;
  private static final long MILLIS_PER_SECOND = 1000L;
  private static final long MICROS_PER_SECOND = 1000000L;
  private static final long NANOS_PER_SECOND = 1000000000L;
  private static final int NANOS_PER_MILLI = 1000000;
  private static final int NANOS_PER_MICRO = 1000;
  private static final long MIN_SECOND = -31557014167219200L;
  private static final long MAX_SECOND = 31556889864403199L;
  private static final long DAYS_PER_10000_YEARS = 3652425L;
  private static final long SECONDS_PER_10000_YEARS = 315569520000L;
  private static final long SECONDS_0000_TO_1970 = 62167219200L;
  
  private FileTime(long paramLong, TimeUnit paramTimeUnit, Instant paramInstant)
  {
    this.value = paramLong;
    this.unit = paramTimeUnit;
    this.instant = paramInstant;
  }
  
  public static FileTime from(long paramLong, TimeUnit paramTimeUnit)
  {
    Objects.requireNonNull(paramTimeUnit, "unit");
    return new FileTime(paramLong, paramTimeUnit, null);
  }
  
  public static FileTime fromMillis(long paramLong)
  {
    return new FileTime(paramLong, TimeUnit.MILLISECONDS, null);
  }
  
  public static FileTime from(Instant paramInstant)
  {
    Objects.requireNonNull(paramInstant, "instant");
    return new FileTime(0L, null, paramInstant);
  }
  
  public long to(TimeUnit paramTimeUnit)
  {
    Objects.requireNonNull(paramTimeUnit, "unit");
    if (this.unit != null) {
      return paramTimeUnit.convert(this.value, this.unit);
    }
    long l1 = paramTimeUnit.convert(this.instant.getEpochSecond(), TimeUnit.SECONDS);
    if ((l1 == Long.MIN_VALUE) || (l1 == Long.MAX_VALUE)) {
      return l1;
    }
    long l2 = paramTimeUnit.convert(this.instant.getNano(), TimeUnit.NANOSECONDS);
    long l3 = l1 + l2;
    if (((l1 ^ l3) & (l2 ^ l3)) < 0L) {
      return l1 < 0L ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    return l3;
  }
  
  public long toMillis()
  {
    if (this.unit != null) {
      return this.unit.toMillis(this.value);
    }
    long l1 = this.instant.getEpochSecond();
    int i = this.instant.getNano();
    long l2 = l1 * 1000L;
    long l3 = Math.abs(l1);
    if (((l3 | 0x3E8) >>> 31 != 0L) && (l2 / 1000L != l1)) {
      return l1 < 0L ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    return l2 + i / 1000000;
  }
  
  private static long scale(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 > paramLong3) {
      return Long.MAX_VALUE;
    }
    if (paramLong1 < -paramLong3) {
      return Long.MIN_VALUE;
    }
    return paramLong1 * paramLong2;
  }
  
  public Instant toInstant()
  {
    if (this.instant == null)
    {
      long l = 0L;
      int i = 0;
      switch (1.$SwitchMap$java$util$concurrent$TimeUnit[this.unit.ordinal()])
      {
      case 1: 
        l = scale(this.value, 86400L, 106751991167300L);
        break;
      case 2: 
        l = scale(this.value, 3600L, 2562047788015215L);
        break;
      case 3: 
        l = scale(this.value, 60L, 153722867280912930L);
        break;
      case 4: 
        l = this.value;
        break;
      case 5: 
        l = Math.floorDiv(this.value, 1000L);
        i = (int)Math.floorMod(this.value, 1000L) * 1000000;
        break;
      case 6: 
        l = Math.floorDiv(this.value, 1000000L);
        i = (int)Math.floorMod(this.value, 1000000L) * 1000;
        break;
      case 7: 
        l = Math.floorDiv(this.value, 1000000000L);
        i = (int)Math.floorMod(this.value, 1000000000L);
        break;
      default: 
        throw new AssertionError("Unit not handled");
      }
      if (l <= -31557014167219200L) {
        this.instant = Instant.MIN;
      } else if (l >= 31556889864403199L) {
        this.instant = Instant.MAX;
      } else {
        this.instant = Instant.ofEpochSecond(l, i);
      }
    }
    return this.instant;
  }
  
  public boolean equals(Object paramObject)
  {
    return compareTo((FileTime)paramObject) == 0;
  }
  
  public int hashCode()
  {
    return toInstant().hashCode();
  }
  
  private long toDays()
  {
    if (this.unit != null) {
      return this.unit.toDays(this.value);
    }
    return TimeUnit.SECONDS.toDays(toInstant().getEpochSecond());
  }
  
  private long toExcessNanos(long paramLong)
  {
    if (this.unit != null) {
      return this.unit.toNanos(this.value - this.unit.convert(paramLong, TimeUnit.DAYS));
    }
    return TimeUnit.SECONDS.toNanos(toInstant().getEpochSecond() - TimeUnit.DAYS.toSeconds(paramLong));
  }
  
  public int compareTo(FileTime paramFileTime)
  {
    if ((this.unit != null) && (this.unit == paramFileTime.unit)) {
      return Long.compare(this.value, paramFileTime.value);
    }
    long l1 = toInstant().getEpochSecond();
    long l2 = paramFileTime.toInstant().getEpochSecond();
    int i = Long.compare(l1, l2);
    if (i != 0) {
      return i;
    }
    i = Long.compare(toInstant().getNano(), paramFileTime.toInstant().getNano());
    if (i != 0) {
      return i;
    }
    if ((l1 != 31556889864403199L) && (l1 != -31557014167219200L)) {
      return 0;
    }
    long l3 = toDays();
    long l4 = paramFileTime.toDays();
    if (l3 == l4) {
      return Long.compare(toExcessNanos(l3), paramFileTime.toExcessNanos(l4));
    }
    return Long.compare(l3, l4);
  }
  
  private StringBuilder append(StringBuilder paramStringBuilder, int paramInt1, int paramInt2)
  {
    while (paramInt1 > 0)
    {
      paramStringBuilder.append((char)(paramInt2 / paramInt1 + 48));
      paramInt2 %= paramInt1;
      paramInt1 /= 10;
    }
    return paramStringBuilder;
  }
  
  public String toString()
  {
    if (this.valueAsString == null)
    {
      long l1 = 0L;
      int i = 0;
      if ((this.instant == null) && (this.unit.compareTo(TimeUnit.SECONDS) >= 0))
      {
        l1 = this.unit.toSeconds(this.value);
      }
      else
      {
        l1 = toInstant().getEpochSecond();
        i = toInstant().getNano();
      }
      int j = 0;
      long l2;
      long l3;
      long l4;
      LocalDateTime localLocalDateTime;
      if (l1 >= -62167219200L)
      {
        l2 = l1 - 315569520000L + 62167219200L;
        l3 = Math.floorDiv(l2, 315569520000L) + 1L;
        l4 = Math.floorMod(l2, 315569520000L);
        localLocalDateTime = LocalDateTime.ofEpochSecond(l4 - 62167219200L, i, ZoneOffset.UTC);
        j = localLocalDateTime.getYear() + (int)l3 * 10000;
      }
      else
      {
        l2 = l1 + 62167219200L;
        l3 = l2 / 315569520000L;
        l4 = l2 % 315569520000L;
        localLocalDateTime = LocalDateTime.ofEpochSecond(l4 - 62167219200L, i, ZoneOffset.UTC);
        j = localLocalDateTime.getYear() + (int)l3 * 10000;
      }
      if (j <= 0) {
        j -= 1;
      }
      int k = localLocalDateTime.getNano();
      StringBuilder localStringBuilder = new StringBuilder(64);
      localStringBuilder.append(j < 0 ? "-" : "");
      j = Math.abs(j);
      if (j < 10000) {
        append(localStringBuilder, 1000, Math.abs(j));
      } else {
        localStringBuilder.append(String.valueOf(j));
      }
      localStringBuilder.append('-');
      append(localStringBuilder, 10, localLocalDateTime.getMonthValue());
      localStringBuilder.append('-');
      append(localStringBuilder, 10, localLocalDateTime.getDayOfMonth());
      localStringBuilder.append('T');
      append(localStringBuilder, 10, localLocalDateTime.getHour());
      localStringBuilder.append(':');
      append(localStringBuilder, 10, localLocalDateTime.getMinute());
      localStringBuilder.append(':');
      append(localStringBuilder, 10, localLocalDateTime.getSecond());
      if (k != 0)
      {
        localStringBuilder.append('.');
        int m = 100000000;
        while (k % 10 == 0)
        {
          k /= 10;
          m /= 10;
        }
        append(localStringBuilder, m, k);
      }
      localStringBuilder.append('Z');
      this.valueAsString = localStringBuilder.toString();
    }
    return this.valueAsString;
  }
}
