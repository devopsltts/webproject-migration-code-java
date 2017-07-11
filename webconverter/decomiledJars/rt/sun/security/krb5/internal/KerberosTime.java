package sun.security.krb5.internal;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Config;
import sun.security.krb5.KrbException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KerberosTime
{
  private final long kerberosTime;
  private final int microSeconds;
  private static long initMilli = ;
  private static long initMicro = System.nanoTime() / 1000L;
  private static boolean DEBUG = Krb5.DEBUG;
  
  private KerberosTime(long paramLong, int paramInt)
  {
    this.kerberosTime = paramLong;
    this.microSeconds = paramInt;
  }
  
  public KerberosTime(long paramLong)
  {
    this(paramLong, 0);
  }
  
  public KerberosTime(String paramString)
    throws Asn1Exception
  {
    this(toKerberosTime(paramString), 0);
  }
  
  private static long toKerberosTime(String paramString)
    throws Asn1Exception
  {
    if (paramString.length() != 15) {
      throw new Asn1Exception(900);
    }
    if (paramString.charAt(14) != 'Z') {
      throw new Asn1Exception(900);
    }
    int i = Integer.parseInt(paramString.substring(0, 4));
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    localCalendar.clear();
    localCalendar.set(i, Integer.parseInt(paramString.substring(4, 6)) - 1, Integer.parseInt(paramString.substring(6, 8)), Integer.parseInt(paramString.substring(8, 10)), Integer.parseInt(paramString.substring(10, 12)), Integer.parseInt(paramString.substring(12, 14)));
    return localCalendar.getTimeInMillis();
  }
  
  public KerberosTime(Date paramDate)
  {
    this(paramDate.getTime(), 0);
  }
  
  public static KerberosTime now()
  {
    long l1 = System.currentTimeMillis();
    long l2 = System.nanoTime() / 1000L;
    long l3 = l2 - initMicro;
    long l4 = initMilli + l3 / 1000L;
    if ((l4 - l1 > 100L) || (l1 - l4 > 100L))
    {
      if (DEBUG) {
        System.out.println("System time adjusted");
      }
      initMilli = l1;
      initMicro = l2;
      return new KerberosTime(l1, 0);
    }
    return new KerberosTime(l4, (int)(l3 % 1000L));
  }
  
  public String toGeneralizedTimeString()
  {
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    localCalendar.clear();
    localCalendar.setTimeInMillis(this.kerberosTime);
    return String.format("%04d%02d%02d%02d%02d%02dZ", new Object[] { Integer.valueOf(localCalendar.get(1)), Integer.valueOf(localCalendar.get(2) + 1), Integer.valueOf(localCalendar.get(5)), Integer.valueOf(localCalendar.get(11)), Integer.valueOf(localCalendar.get(12)), Integer.valueOf(localCalendar.get(13)) });
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putGeneralizedTime(toDate());
    return localDerOutputStream.toByteArray();
  }
  
  public long getTime()
  {
    return this.kerberosTime;
  }
  
  public Date toDate()
  {
    return new Date(this.kerberosTime);
  }
  
  public int getMicroSeconds()
  {
    Long localLong = new Long(this.kerberosTime % 1000L * 1000L);
    return localLong.intValue() + this.microSeconds;
  }
  
  public KerberosTime withMicroSeconds(int paramInt)
  {
    return new KerberosTime(this.kerberosTime - this.kerberosTime % 1000L + paramInt / 1000L, paramInt % 1000);
  }
  
  private boolean inClockSkew(int paramInt)
  {
    return Math.abs(this.kerberosTime - System.currentTimeMillis()) <= paramInt * 1000L;
  }
  
  public boolean inClockSkew()
  {
    return inClockSkew(getDefaultSkew());
  }
  
  public boolean greaterThanWRTClockSkew(KerberosTime paramKerberosTime, int paramInt)
  {
    return this.kerberosTime - paramKerberosTime.kerberosTime > paramInt * 1000L;
  }
  
  public boolean greaterThanWRTClockSkew(KerberosTime paramKerberosTime)
  {
    return greaterThanWRTClockSkew(paramKerberosTime, getDefaultSkew());
  }
  
  public boolean greaterThan(KerberosTime paramKerberosTime)
  {
    return (this.kerberosTime > paramKerberosTime.kerberosTime) || ((this.kerberosTime == paramKerberosTime.kerberosTime) && (this.microSeconds > paramKerberosTime.microSeconds));
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof KerberosTime)) {
      return false;
    }
    return (this.kerberosTime == ((KerberosTime)paramObject).kerberosTime) && (this.microSeconds == ((KerberosTime)paramObject).microSeconds);
  }
  
  public int hashCode()
  {
    int i = 629 + (int)(this.kerberosTime ^ this.kerberosTime >>> 32);
    return i * 17 + this.microSeconds;
  }
  
  public boolean isZero()
  {
    return (this.kerberosTime == 0L) && (this.microSeconds == 0);
  }
  
  public int getSeconds()
  {
    Long localLong = new Long(this.kerberosTime / 1000L);
    return localLong.intValue();
  }
  
  public static KerberosTime parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    Date localDate = localDerValue2.getGeneralizedTime();
    return new KerberosTime(localDate.getTime(), 0);
  }
  
  public static int getDefaultSkew()
  {
    int i = 300;
    try
    {
      if ((i = Config.getInstance().getIntValue(new String[] { "libdefaults", "clockskew" })) == Integer.MIN_VALUE) {
        i = 300;
      }
    }
    catch (KrbException localKrbException)
    {
      if (DEBUG) {
        System.out.println("Exception in getting clockskew from Configuration using default value " + localKrbException.getMessage());
      }
    }
    return i;
  }
  
  public String toString()
  {
    return toGeneralizedTimeString();
  }
}
