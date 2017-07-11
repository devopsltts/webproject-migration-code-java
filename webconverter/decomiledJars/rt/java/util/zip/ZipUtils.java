package java.util.zip;

import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class ZipUtils
{
  private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
  
  ZipUtils() {}
  
  public static final FileTime winTimeToFileTime(long paramLong)
  {
    return FileTime.from(paramLong / 10L + -11644473600000000L, TimeUnit.MICROSECONDS);
  }
  
  public static final long fileTimeToWinTime(FileTime paramFileTime)
  {
    return (paramFileTime.to(TimeUnit.MICROSECONDS) - -11644473600000000L) * 10L;
  }
  
  public static final FileTime unixTimeToFileTime(long paramLong)
  {
    return FileTime.from(paramLong, TimeUnit.SECONDS);
  }
  
  public static final long fileTimeToUnixTime(FileTime paramFileTime)
  {
    return paramFileTime.to(TimeUnit.SECONDS);
  }
  
  private static long dosToJavaTime(long paramLong)
  {
    Date localDate = new Date((int)((paramLong >> 25 & 0x7F) + 80L), (int)((paramLong >> 21 & 0xF) - 1L), (int)(paramLong >> 16 & 0x1F), (int)(paramLong >> 11 & 0x1F), (int)(paramLong >> 5 & 0x3F), (int)(paramLong << 1 & 0x3E));
    return localDate.getTime();
  }
  
  public static long extendedDosToJavaTime(long paramLong)
  {
    long l = dosToJavaTime(paramLong);
    return l + (paramLong >> 32);
  }
  
  private static long javaToDosTime(long paramLong)
  {
    Date localDate = new Date(paramLong);
    int i = localDate.getYear() + 1900;
    if (i < 1980) {
      return 2162688L;
    }
    return i - 1980 << 25 | localDate.getMonth() + 1 << 21 | localDate.getDate() << 16 | localDate.getHours() << 11 | localDate.getMinutes() << 5 | localDate.getSeconds() >> 1;
  }
  
  public static long javaToExtendedDosTime(long paramLong)
  {
    if (paramLong < 0L) {
      return 2162688L;
    }
    long l = javaToDosTime(paramLong);
    return l != 2162688L ? l + (paramLong % 2000L << 32) : 2162688L;
  }
  
  public static final int get16(byte[] paramArrayOfByte, int paramInt)
  {
    return Byte.toUnsignedInt(paramArrayOfByte[paramInt]) | Byte.toUnsignedInt(paramArrayOfByte[(paramInt + 1)]) << 8;
  }
  
  public static final long get32(byte[] paramArrayOfByte, int paramInt)
  {
    return (get16(paramArrayOfByte, paramInt) | get16(paramArrayOfByte, paramInt + 2) << 16) & 0xFFFFFFFF;
  }
  
  public static final long get64(byte[] paramArrayOfByte, int paramInt)
  {
    return get32(paramArrayOfByte, paramInt) | get32(paramArrayOfByte, paramInt + 4) << 32;
  }
}
