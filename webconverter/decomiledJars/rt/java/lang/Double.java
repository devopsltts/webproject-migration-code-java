package java.lang;

import sun.misc.FloatingDecimal;

public final class Double
  extends Number
  implements Comparable<Double>
{
  public static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
  public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
  public static final double NaN = NaN.0D;
  public static final double MAX_VALUE = Double.MAX_VALUE;
  public static final double MIN_NORMAL = 2.2250738585072014E-308D;
  public static final double MIN_VALUE = Double.MIN_VALUE;
  public static final int MAX_EXPONENT = 1023;
  public static final int MIN_EXPONENT = -1022;
  public static final int SIZE = 64;
  public static final int BYTES = 8;
  public static final Class<Double> TYPE = Class.getPrimitiveClass("double");
  private final double value;
  private static final long serialVersionUID = -9172774392245257468L;
  
  public static String toString(double paramDouble)
  {
    return FloatingDecimal.toJavaFormatString(paramDouble);
  }
  
  public static String toHexString(double paramDouble)
  {
    if (!isFinite(paramDouble)) {
      return toString(paramDouble);
    }
    StringBuilder localStringBuilder = new StringBuilder(24);
    if (Math.copySign(1.0D, paramDouble) == -1.0D) {
      localStringBuilder.append("-");
    }
    localStringBuilder.append("0x");
    paramDouble = Math.abs(paramDouble);
    if (paramDouble == 0.0D)
    {
      localStringBuilder.append("0.0p0");
    }
    else
    {
      int i = paramDouble < 2.2250738585072014E-308D ? 1 : 0;
      long l = doubleToLongBits(paramDouble) & 0xFFFFFFFFFFFFF | 0x1000000000000000;
      localStringBuilder.append(i != 0 ? "0." : "1.");
      String str = Long.toHexString(l).substring(3, 16);
      localStringBuilder.append(str.equals("0000000000000") ? "0" : str.replaceFirst("0{1,12}$", ""));
      localStringBuilder.append('p');
      localStringBuilder.append(i != 0 ? 64514 : Math.getExponent(paramDouble));
    }
    return localStringBuilder.toString();
  }
  
  public static Double valueOf(String paramString)
    throws NumberFormatException
  {
    return new Double(parseDouble(paramString));
  }
  
  public static Double valueOf(double paramDouble)
  {
    return new Double(paramDouble);
  }
  
  public static double parseDouble(String paramString)
    throws NumberFormatException
  {
    return FloatingDecimal.parseDouble(paramString);
  }
  
  public static boolean isNaN(double paramDouble)
  {
    return paramDouble != paramDouble;
  }
  
  public static boolean isInfinite(double paramDouble)
  {
    return (paramDouble == Double.POSITIVE_INFINITY) || (paramDouble == Double.NEGATIVE_INFINITY);
  }
  
  public static boolean isFinite(double paramDouble)
  {
    return Math.abs(paramDouble) <= Double.MAX_VALUE;
  }
  
  public Double(double paramDouble)
  {
    this.value = paramDouble;
  }
  
  public Double(String paramString)
    throws NumberFormatException
  {
    this.value = parseDouble(paramString);
  }
  
  public boolean isNaN()
  {
    return isNaN(this.value);
  }
  
  public boolean isInfinite()
  {
    return isInfinite(this.value);
  }
  
  public String toString()
  {
    return toString(this.value);
  }
  
  public byte byteValue()
  {
    return (byte)(int)this.value;
  }
  
  public short shortValue()
  {
    return (short)(int)this.value;
  }
  
  public int intValue()
  {
    return (int)this.value;
  }
  
  public long longValue()
  {
    return this.value;
  }
  
  public float floatValue()
  {
    return (float)this.value;
  }
  
  public double doubleValue()
  {
    return this.value;
  }
  
  public int hashCode()
  {
    return hashCode(this.value);
  }
  
  public static int hashCode(double paramDouble)
  {
    long l = doubleToLongBits(paramDouble);
    return (int)(l ^ l >>> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Double)) && (doubleToLongBits(((Double)paramObject).value) == doubleToLongBits(this.value));
  }
  
  public static long doubleToLongBits(double paramDouble)
  {
    long l = doubleToRawLongBits(paramDouble);
    if (((l & 0x7FF0000000000000) == 9218868437227405312L) && ((l & 0xFFFFFFFFFFFFF) != 0L)) {
      l = 9221120237041090560L;
    }
    return l;
  }
  
  public static native long doubleToRawLongBits(double paramDouble);
  
  public static native double longBitsToDouble(long paramLong);
  
  public int compareTo(Double paramDouble)
  {
    return compare(this.value, paramDouble.value);
  }
  
  public static int compare(double paramDouble1, double paramDouble2)
  {
    if (paramDouble1 < paramDouble2) {
      return -1;
    }
    if (paramDouble1 > paramDouble2) {
      return 1;
    }
    long l1 = doubleToLongBits(paramDouble1);
    long l2 = doubleToLongBits(paramDouble2);
    return l1 < l2 ? -1 : l1 == l2 ? 0 : 1;
  }
  
  public static double sum(double paramDouble1, double paramDouble2)
  {
    return paramDouble1 + paramDouble2;
  }
  
  public static double max(double paramDouble1, double paramDouble2)
  {
    return Math.max(paramDouble1, paramDouble2);
  }
  
  public static double min(double paramDouble1, double paramDouble2)
  {
    return Math.min(paramDouble1, paramDouble2);
  }
}
