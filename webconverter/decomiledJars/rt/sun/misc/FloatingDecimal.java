package sun.misc;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FloatingDecimal
{
  static final int EXP_SHIFT = 52;
  static final long FRACT_HOB = 4503599627370496L;
  static final long EXP_ONE = 4607182418800017408L;
  static final int MAX_SMALL_BIN_EXP = 62;
  static final int MIN_SMALL_BIN_EXP = -21;
  static final int MAX_DECIMAL_DIGITS = 15;
  static final int MAX_DECIMAL_EXPONENT = 308;
  static final int MIN_DECIMAL_EXPONENT = -324;
  static final int BIG_DECIMAL_EXPONENT = 324;
  static final int MAX_NDIGITS = 1100;
  static final int SINGLE_EXP_SHIFT = 23;
  static final int SINGLE_FRACT_HOB = 8388608;
  static final int SINGLE_MAX_DECIMAL_DIGITS = 7;
  static final int SINGLE_MAX_DECIMAL_EXPONENT = 38;
  static final int SINGLE_MIN_DECIMAL_EXPONENT = -45;
  static final int SINGLE_MAX_NDIGITS = 200;
  static final int INT_DECIMAL_DIGITS = 9;
  private static final String INFINITY_REP = "Infinity";
  private static final int INFINITY_LENGTH = "Infinity".length();
  private static final String NAN_REP = "NaN";
  private static final int NAN_LENGTH = "NaN".length();
  private static final BinaryToASCIIConverter B2AC_POSITIVE_INFINITY = new ExceptionalBinaryToASCIIBuffer("Infinity", false);
  private static final BinaryToASCIIConverter B2AC_NEGATIVE_INFINITY = new ExceptionalBinaryToASCIIBuffer("-Infinity", true);
  private static final BinaryToASCIIConverter B2AC_NOT_A_NUMBER = new ExceptionalBinaryToASCIIBuffer("NaN", false);
  private static final BinaryToASCIIConverter B2AC_POSITIVE_ZERO = new BinaryToASCIIBuffer(false, new char[] { '0' });
  private static final BinaryToASCIIConverter B2AC_NEGATIVE_ZERO = new BinaryToASCIIBuffer(true, new char[] { '0' });
  private static final ThreadLocal<BinaryToASCIIBuffer> threadLocalBinaryToASCIIBuffer = new ThreadLocal()
  {
    protected FloatingDecimal.BinaryToASCIIBuffer initialValue()
    {
      return new FloatingDecimal.BinaryToASCIIBuffer();
    }
  };
  static final ASCIIToBinaryConverter A2BC_POSITIVE_INFINITY = new PreparedASCIIToBinaryBuffer(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
  static final ASCIIToBinaryConverter A2BC_NEGATIVE_INFINITY = new PreparedASCIIToBinaryBuffer(Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
  static final ASCIIToBinaryConverter A2BC_NOT_A_NUMBER = new PreparedASCIIToBinaryBuffer(NaN.0D, NaN.0F);
  static final ASCIIToBinaryConverter A2BC_POSITIVE_ZERO = new PreparedASCIIToBinaryBuffer(0.0D, 0.0F);
  static final ASCIIToBinaryConverter A2BC_NEGATIVE_ZERO = new PreparedASCIIToBinaryBuffer(-0.0D, -0.0F);
  
  public FloatingDecimal() {}
  
  public static String toJavaFormatString(double paramDouble)
  {
    return getBinaryToASCIIConverter(paramDouble).toJavaFormatString();
  }
  
  public static String toJavaFormatString(float paramFloat)
  {
    return getBinaryToASCIIConverter(paramFloat).toJavaFormatString();
  }
  
  public static void appendTo(double paramDouble, Appendable paramAppendable)
  {
    getBinaryToASCIIConverter(paramDouble).appendTo(paramAppendable);
  }
  
  public static void appendTo(float paramFloat, Appendable paramAppendable)
  {
    getBinaryToASCIIConverter(paramFloat).appendTo(paramAppendable);
  }
  
  public static double parseDouble(String paramString)
    throws NumberFormatException
  {
    return readJavaFormatString(paramString).doubleValue();
  }
  
  public static float parseFloat(String paramString)
    throws NumberFormatException
  {
    return readJavaFormatString(paramString).floatValue();
  }
  
  private static BinaryToASCIIBuffer getBinaryToASCIIBuffer()
  {
    return (BinaryToASCIIBuffer)threadLocalBinaryToASCIIBuffer.get();
  }
  
  public static BinaryToASCIIConverter getBinaryToASCIIConverter(double paramDouble)
  {
    return getBinaryToASCIIConverter(paramDouble, true);
  }
  
  static BinaryToASCIIConverter getBinaryToASCIIConverter(double paramDouble, boolean paramBoolean)
  {
    long l1 = Double.doubleToRawLongBits(paramDouble);
    boolean bool = (l1 & 0x8000000000000000) != 0L;
    long l2 = l1 & 0xFFFFFFFFFFFFF;
    int i = (int)((l1 & 0x7FF0000000000000) >> 52);
    if (i == 2047)
    {
      if (l2 == 0L) {
        return bool ? B2AC_NEGATIVE_INFINITY : B2AC_POSITIVE_INFINITY;
      }
      return B2AC_NOT_A_NUMBER;
    }
    int j;
    if (i == 0)
    {
      if (l2 == 0L) {
        return bool ? B2AC_NEGATIVE_ZERO : B2AC_POSITIVE_ZERO;
      }
      int k = Long.numberOfLeadingZeros(l2);
      int m = k - 11;
      l2 <<= m;
      i = 1 - m;
      j = 64 - k;
    }
    else
    {
      l2 |= 0x10000000000000;
      j = 53;
    }
    i -= 1023;
    BinaryToASCIIBuffer localBinaryToASCIIBuffer = getBinaryToASCIIBuffer();
    localBinaryToASCIIBuffer.setSign(bool);
    localBinaryToASCIIBuffer.dtoa(i, l2, j, paramBoolean);
    return localBinaryToASCIIBuffer;
  }
  
  private static BinaryToASCIIConverter getBinaryToASCIIConverter(float paramFloat)
  {
    int i = Float.floatToRawIntBits(paramFloat);
    boolean bool = (i & 0x80000000) != 0;
    int j = i & 0x7FFFFF;
    int k = (i & 0x7F800000) >> 23;
    if (k == 255)
    {
      if (j == 0L) {
        return bool ? B2AC_NEGATIVE_INFINITY : B2AC_POSITIVE_INFINITY;
      }
      return B2AC_NOT_A_NUMBER;
    }
    int m;
    if (k == 0)
    {
      if (j == 0) {
        return bool ? B2AC_NEGATIVE_ZERO : B2AC_POSITIVE_ZERO;
      }
      int n = Integer.numberOfLeadingZeros(j);
      int i1 = n - 8;
      j <<= i1;
      k = 1 - i1;
      m = 32 - n;
    }
    else
    {
      j |= 0x800000;
      m = 24;
    }
    k -= 127;
    BinaryToASCIIBuffer localBinaryToASCIIBuffer = getBinaryToASCIIBuffer();
    localBinaryToASCIIBuffer.setSign(bool);
    localBinaryToASCIIBuffer.dtoa(k, j << 29, m, true);
    return localBinaryToASCIIBuffer;
  }
  
  static ASCIIToBinaryConverter readJavaFormatString(String paramString)
    throws NumberFormatException
  {
    boolean bool = false;
    int i = 0;
    try
    {
      paramString = paramString.trim();
      int m = paramString.length();
      if (m == 0) {
        throw new NumberFormatException("empty String");
      }
      int n = 0;
      switch (paramString.charAt(n))
      {
      case '-': 
        bool = true;
      case '+': 
        n++;
        i = 1;
      }
      int k = paramString.charAt(n);
      if (k == 78)
      {
        if ((m - n == NAN_LENGTH) && (paramString.indexOf("NaN", n) == n)) {
          return A2BC_NOT_A_NUMBER;
        }
      }
      else if (k == 73)
      {
        if ((m - n == INFINITY_LENGTH) && (paramString.indexOf("Infinity", n) == n)) {
          return bool ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_INFINITY;
        }
      }
      else
      {
        if ((k == 48) && (m > n + 1))
        {
          int i1 = paramString.charAt(n + 1);
          if ((i1 == 120) || (i1 == 88)) {
            return parseHexString(paramString);
          }
        }
        char[] arrayOfChar = new char[m];
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (n < m)
        {
          k = paramString.charAt(n);
          if (k == 48)
          {
            i5++;
          }
          else
          {
            if (k != 46) {
              break;
            }
            if (i3 != 0) {
              throw new NumberFormatException("multiple points");
            }
            i4 = n;
            if (i != 0) {
              i4--;
            }
            i3 = 1;
          }
          n++;
        }
        while (n < m)
        {
          k = paramString.charAt(n);
          if ((k >= 49) && (k <= 57))
          {
            arrayOfChar[(i2++)] = k;
            i6 = 0;
          }
          else if (k == 48)
          {
            arrayOfChar[(i2++)] = k;
            i6++;
          }
          else
          {
            if (k != 46) {
              break;
            }
            if (i3 != 0) {
              throw new NumberFormatException("multiple points");
            }
            i4 = n;
            if (i != 0) {
              i4--;
            }
            i3 = 1;
          }
          n++;
        }
        i2 -= i6;
        int i7 = i2 == 0 ? 1 : 0;
        if ((i7 == 0) || (i5 != 0))
        {
          int j;
          if (i3 != 0) {
            j = i4 - i5;
          } else {
            j = i2 + i6;
          }
          if ((n < m) && (((k = paramString.charAt(n)) == 'e') || (k == 69)))
          {
            int i8 = 1;
            int i9 = 0;
            int i10 = 214748364;
            int i11 = 0;
            switch (paramString.charAt(++n))
            {
            case '-': 
              i8 = -1;
            case '+': 
              n++;
            }
            int i12 = n;
            while (n < m)
            {
              if (i9 >= i10) {
                i11 = 1;
              }
              k = paramString.charAt(n++);
              if ((k >= 48) && (k <= 57)) {
                i9 = i9 * 10 + (k - 48);
              } else {
                n--;
              }
            }
            int i13 = 324 + i2 + i6;
            if ((i11 != 0) || (i9 > i13)) {
              j = i8 * i13;
            } else {
              j += i8 * i9;
            }
            if (n == i12) {}
          }
          else if ((n >= m) || ((n != m - 1) || ((paramString.charAt(n) == 'f') || (paramString.charAt(n) == 'F') || (paramString.charAt(n) == 'd') || (paramString.charAt(n) == 'D'))))
          {
            if (i7 != 0) {
              return bool ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
            }
            return new ASCIIToBinaryBuffer(bool, j, arrayOfChar, i2);
          }
        }
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new NumberFormatException("For input string: \"" + paramString + "\"");
    }
  }
  
  static ASCIIToBinaryConverter parseHexString(String paramString)
  {
    Matcher localMatcher = HexFloatPattern.VALUE.matcher(paramString);
    boolean bool = localMatcher.matches();
    if (!bool) {
      throw new NumberFormatException("For input string: \"" + paramString + "\"");
    }
    String str1 = localMatcher.group(1);
    int i = (str1 != null) && (str1.equals("-")) ? 1 : 0;
    String str2 = null;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    String str4;
    if ((str4 = localMatcher.group(4)) != null)
    {
      str2 = stripLeadingZeros(str4);
      m = str2.length();
    }
    else
    {
      String str5 = stripLeadingZeros(localMatcher.group(6));
      m = str5.length();
      String str6 = localMatcher.group(7);
      n = str6.length();
      str2 = (str5 == null ? "" : str5) + str6;
    }
    str2 = stripLeadingZeros(str2);
    j = str2.length();
    if (m >= 1) {
      k = 4 * (m - 1);
    } else {
      k = -4 * (n - j + 1);
    }
    if (j == 0) {
      return i != 0 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
    }
    String str3 = localMatcher.group(8);
    n = (str3 == null) || (str3.equals("+")) ? 1 : 0;
    long l1;
    try
    {
      l1 = Integer.parseInt(localMatcher.group(9));
    }
    catch (NumberFormatException localNumberFormatException)
    {
      return n != 0 ? A2BC_POSITIVE_INFINITY : i != 0 ? A2BC_NEGATIVE_ZERO : n != 0 ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_ZERO;
    }
    long l2 = (n != 0 ? 1L : -1L) * l1;
    long l3 = l2 + k;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    long l4 = 0L;
    long l5 = getHexDigit(str2, 0);
    if (l5 == 1L)
    {
      l4 |= l5 << 52;
      i3 = 48;
    }
    else if (l5 <= 3L)
    {
      l4 |= l5 << 51;
      i3 = 47;
      l3 += 1L;
    }
    else if (l5 <= 7L)
    {
      l4 |= l5 << 50;
      i3 = 46;
      l3 += 2L;
    }
    else if (l5 <= 15L)
    {
      l4 |= l5 << 49;
      i3 = 45;
      l3 += 3L;
    }
    else
    {
      throw new AssertionError("Result from digit conversion too large!");
    }
    int i4 = 0;
    long l6;
    for (i4 = 1; (i4 < j) && (i3 >= 0); i4++)
    {
      l6 = getHexDigit(str2, i4);
      l4 |= l6 << i3;
      i3 -= 4;
    }
    if (i4 < j)
    {
      l6 = getHexDigit(str2, i4);
      switch (i3)
      {
      case -1: 
        l4 |= (l6 & 0xE) >> 1;
        i1 = (l6 & 1L) != 0L ? 1 : 0;
        break;
      case -2: 
        l4 |= (l6 & 0xC) >> 2;
        i1 = (l6 & 0x2) != 0L ? 1 : 0;
        i2 = (l6 & 1L) != 0L ? 1 : 0;
        break;
      case -3: 
        l4 |= (l6 & 0x8) >> 3;
        i1 = (l6 & 0x4) != 0L ? 1 : 0;
        i2 = (l6 & 0x3) != 0L ? 1 : 0;
        break;
      case -4: 
        i1 = (l6 & 0x8) != 0L ? 1 : 0;
        i2 = (l6 & 0x7) != 0L ? 1 : 0;
        break;
      default: 
        throw new AssertionError("Unexpected shift distance remainder.");
      }
      i4++;
      while ((i4 < j) && (i2 == 0))
      {
        l6 = getHexDigit(str2, i4);
        i2 = (i2 != 0) || (l6 != 0L) ? 1 : 0;
        i4++;
      }
    }
    int i5 = i != 0 ? Integer.MIN_VALUE : 0;
    int i6;
    int i8;
    if (l3 >= -126L)
    {
      if (l3 > 127L)
      {
        i5 |= 0x7F800000;
      }
      else
      {
        i6 = 28;
        i7 = ((l4 & (1L << i6) - 1L) != 0L) || (i1 != 0) || (i2 != 0) ? 1 : 0;
        i8 = (int)(l4 >>> i6);
        if (((i8 & 0x3) != 1) || (i7 != 0)) {
          i8++;
        }
        i5 |= ((int)l3 + 126 << 23) + (i8 >> 1);
      }
    }
    else if (l3 >= -150L)
    {
      i6 = (int)(-98L - l3);
      assert (i6 >= 29);
      assert (i6 < 53);
      i7 = ((l4 & (1L << i6) - 1L) != 0L) || (i1 != 0) || (i2 != 0) ? 1 : 0;
      i8 = (int)(l4 >>> i6);
      if (((i8 & 0x3) != 1) || (i7 != 0)) {
        i8++;
      }
      i5 |= i8 >> 1;
    }
    float f = Float.intBitsToFloat(i5);
    if (l3 > 1023L) {
      return i != 0 ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_INFINITY;
    }
    if ((l3 <= 1023L) && (l3 >= -1022L))
    {
      l4 = l3 + 1023L << 52 & 0x7FF0000000000000 | 0xFFFFFFFFFFFFF & l4;
    }
    else
    {
      if (l3 < -1075L) {
        return i != 0 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
      }
      i2 = (i2 != 0) || (i1 != 0) ? 1 : 0;
      i1 = 0;
      i7 = 53 - ((int)l3 - 64462 + 1);
      assert ((i7 >= 1) && (i7 <= 53));
      i1 = (l4 & 1L << i7 - 1) != 0L ? 1 : 0;
      if (i7 > 1)
      {
        long l7 = -1L << i7 - 1 ^ 0xFFFFFFFFFFFFFFFF;
        i2 = (i2 != 0) || ((l4 & l7) != 0L) ? 1 : 0;
      }
      l4 >>= i7;
      l4 = 0L | 0xFFFFFFFFFFFFF & l4;
    }
    int i7 = (l4 & 1L) == 0L ? 1 : 0;
    if (((i7 != 0) && (i1 != 0) && (i2 != 0)) || ((i7 == 0) && (i1 != 0))) {
      l4 += 1L;
    }
    double d = i != 0 ? Double.longBitsToDouble(l4 | 0x8000000000000000) : Double.longBitsToDouble(l4);
    return new PreparedASCIIToBinaryBuffer(d, f);
  }
  
  static String stripLeadingZeros(String paramString)
  {
    if ((!paramString.isEmpty()) && (paramString.charAt(0) == '0'))
    {
      for (int i = 1; i < paramString.length(); i++) {
        if (paramString.charAt(i) != '0') {
          return paramString.substring(i);
        }
      }
      return "";
    }
    return paramString;
  }
  
  static int getHexDigit(String paramString, int paramInt)
  {
    int i = Character.digit(paramString.charAt(paramInt), 16);
    if ((i <= -1) || (i >= 16)) {
      throw new AssertionError("Unexpected failure of digit conversion of " + paramString.charAt(paramInt));
    }
    return i;
  }
  
  static class ASCIIToBinaryBuffer
    implements FloatingDecimal.ASCIIToBinaryConverter
  {
    boolean isNegative;
    int decExponent;
    char[] digits;
    int nDigits;
    private static final double[] SMALL_10_POW = { 1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D };
    private static final float[] SINGLE_SMALL_10_POW = { 1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F };
    private static final double[] BIG_10_POW = { 1.0E16D, 1.0E32D, 1.0E64D, 1.0E128D, 1.0E256D };
    private static final double[] TINY_10_POW = { 1.0E-16D, 1.0E-32D, 1.0E-64D, 1.0E-128D, 1.0E-256D };
    private static final int MAX_SMALL_TEN = SMALL_10_POW.length - 1;
    private static final int SINGLE_MAX_SMALL_TEN = SINGLE_SMALL_10_POW.length - 1;
    
    ASCIIToBinaryBuffer(boolean paramBoolean, int paramInt1, char[] paramArrayOfChar, int paramInt2)
    {
      this.isNegative = paramBoolean;
      this.decExponent = paramInt1;
      this.digits = paramArrayOfChar;
      this.nDigits = paramInt2;
    }
    
    public double doubleValue()
    {
      int i = Math.min(this.nDigits, 16);
      int j = this.digits[0] - '0';
      int k = Math.min(i, 9);
      for (int m = 1; m < k; m++) {
        j = j * 10 + this.digits[m] - 48;
      }
      long l1 = j;
      for (int n = k; n < i; n++) {
        l1 = l1 * 10L + (this.digits[n] - '0');
      }
      double d1 = l1;
      int i1 = this.decExponent - i;
      double d4;
      if (this.nDigits <= 15)
      {
        if ((i1 == 0) || (d1 == 0.0D)) {
          return this.isNegative ? -d1 : d1;
        }
        if (i1 >= 0)
        {
          if (i1 <= MAX_SMALL_TEN)
          {
            double d2 = d1 * SMALL_10_POW[i1];
            return this.isNegative ? -d2 : d2;
          }
          int i2 = 15 - i;
          if (i1 <= MAX_SMALL_TEN + i2)
          {
            d1 *= SMALL_10_POW[i2];
            d4 = d1 * SMALL_10_POW[(i1 - i2)];
            return this.isNegative ? -d4 : d4;
          }
        }
        else if (i1 >= -MAX_SMALL_TEN)
        {
          double d3 = d1 / SMALL_10_POW[(-i1)];
          return this.isNegative ? -d3 : d3;
        }
      }
      int i3;
      if (i1 > 0)
      {
        if (this.decExponent > 309) {
          return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
        if ((i1 & 0xF) != 0) {
          d1 *= SMALL_10_POW[(i1 & 0xF)];
        }
        if (i1 >>= 4 != 0)
        {
          i3 = 0;
          while (i1 > 1)
          {
            if ((i1 & 0x1) != 0) {
              d1 *= BIG_10_POW[i3];
            }
            i3++;
            i1 >>= 1;
          }
          d4 = d1 * BIG_10_POW[i3];
          if (Double.isInfinite(d4))
          {
            d4 = d1 / 2.0D;
            d4 *= BIG_10_POW[i3];
            if (Double.isInfinite(d4)) {
              return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            d4 = Double.MAX_VALUE;
          }
          d1 = d4;
        }
      }
      else if (i1 < 0)
      {
        i1 = -i1;
        if (this.decExponent < 65211) {
          return this.isNegative ? -0.0D : 0.0D;
        }
        if ((i1 & 0xF) != 0) {
          d1 /= SMALL_10_POW[(i1 & 0xF)];
        }
        if (i1 >>= 4 != 0)
        {
          i3 = 0;
          while (i1 > 1)
          {
            if ((i1 & 0x1) != 0) {
              d1 *= TINY_10_POW[i3];
            }
            i3++;
            i1 >>= 1;
          }
          d4 = d1 * TINY_10_POW[i3];
          if (d4 == 0.0D)
          {
            d4 = d1 * 2.0D;
            d4 *= TINY_10_POW[i3];
            if (d4 == 0.0D) {
              return this.isNegative ? -0.0D : 0.0D;
            }
            d4 = Double.MIN_VALUE;
          }
          d1 = d4;
        }
      }
      if (this.nDigits > 1100)
      {
        this.nDigits = 1101;
        this.digits['ь'] = '1';
      }
      FDBigInteger localFDBigInteger1 = new FDBigInteger(l1, this.digits, i, this.nDigits);
      i1 = this.decExponent - this.nDigits;
      long l2 = Double.doubleToRawLongBits(d1);
      int i4 = Math.max(0, -i1);
      int i5 = Math.max(0, i1);
      localFDBigInteger1 = localFDBigInteger1.multByPow52(i5, 0);
      localFDBigInteger1.makeImmutable();
      FDBigInteger localFDBigInteger2 = null;
      int i6 = 0;
      for (;;)
      {
        int i7 = (int)(l2 >>> 52);
        long l3 = l2 & 0xFFFFFFFFFFFFF;
        if (i7 > 0)
        {
          l3 |= 0x10000000000000;
        }
        else
        {
          assert (l3 != 0L) : l3;
          i8 = Long.numberOfLeadingZeros(l3);
          i9 = i8 - 11;
          l3 <<= i9;
          i7 = 1 - i9;
        }
        i7 -= 1023;
        int i8 = Long.numberOfTrailingZeros(l3);
        l3 >>>= i8;
        int i9 = i7 - 52 + i8;
        int i10 = 53 - i8;
        int i11 = i4;
        int i12 = i5;
        if (i9 >= 0) {
          i11 += i9;
        } else {
          i12 -= i9;
        }
        int i13 = i11;
        int i14;
        if (i7 <= 64513) {
          i14 = i7 + i8 + 1023;
        } else {
          i14 = 1 + i8;
        }
        i11 += i14;
        i12 += i14;
        int i15 = Math.min(i11, Math.min(i12, i13));
        i11 -= i15;
        i12 -= i15;
        i13 -= i15;
        FDBigInteger localFDBigInteger3 = FDBigInteger.valueOfMulPow52(l3, i4, i11);
        if ((localFDBigInteger2 == null) || (i6 != i12))
        {
          localFDBigInteger2 = localFDBigInteger1.leftShift(i12);
          i6 = i12;
        }
        int i17;
        FDBigInteger localFDBigInteger4;
        if ((i16 = localFDBigInteger3.cmp(localFDBigInteger2)) > 0)
        {
          i17 = 1;
          localFDBigInteger4 = localFDBigInteger3.leftInplaceSub(localFDBigInteger2);
          if ((i10 == 1) && (i9 > 64514))
          {
            i13--;
            if (i13 < 0)
            {
              i13 = 0;
              localFDBigInteger4 = localFDBigInteger4.leftShift(1);
            }
          }
        }
        else
        {
          if (i16 >= 0) {
            break;
          }
          i17 = 0;
          localFDBigInteger4 = localFDBigInteger2.rightInplaceSub(localFDBigInteger3);
        }
        int i16 = localFDBigInteger4.cmpPow52(i4, i13);
        if (i16 >= 0) {
          if (i16 == 0)
          {
            if ((l2 & 1L) != 0L) {
              l2 += (i17 != 0 ? -1L : 1L);
            }
          }
          else
          {
            l2 += (i17 != 0 ? -1L : 1L);
            if (l2 != 0L) {
              if (l2 == 9218868437227405312L) {
                break;
              }
            }
          }
        }
      }
      if (this.isNegative) {
        l2 |= 0x8000000000000000;
      }
      return Double.longBitsToDouble(l2);
    }
    
    public float floatValue()
    {
      int i = Math.min(this.nDigits, 8);
      int j = this.digits[0] - '0';
      for (int k = 1; k < i; k++) {
        j = j * 10 + this.digits[k] - 48;
      }
      float f = j;
      int m = this.decExponent - i;
      if (this.nDigits <= 7)
      {
        if ((m == 0) || (f == 0.0F)) {
          return this.isNegative ? -f : f;
        }
        if (m >= 0)
        {
          if (m <= SINGLE_MAX_SMALL_TEN)
          {
            f *= SINGLE_SMALL_10_POW[m];
            return this.isNegative ? -f : f;
          }
          int n = 7 - i;
          if (m <= SINGLE_MAX_SMALL_TEN + n)
          {
            f *= SINGLE_SMALL_10_POW[n];
            f *= SINGLE_SMALL_10_POW[(m - n)];
            return this.isNegative ? -f : f;
          }
        }
        else if (m >= -SINGLE_MAX_SMALL_TEN)
        {
          f /= SINGLE_SMALL_10_POW[(-m)];
          return this.isNegative ? -f : f;
        }
      }
      else if ((this.decExponent >= this.nDigits) && (this.nDigits + this.decExponent <= 15))
      {
        long l = j;
        for (int i1 = i; i1 < this.nDigits; i1++) {
          l = l * 10L + (this.digits[i1] - '0');
        }
        double d2 = l;
        m = this.decExponent - this.nDigits;
        d2 *= SMALL_10_POW[m];
        f = (float)d2;
        return this.isNegative ? -f : f;
      }
      double d1 = f;
      int i2;
      if (m > 0)
      {
        if (this.decExponent > 39) {
          return this.isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        }
        if ((m & 0xF) != 0) {
          d1 *= SMALL_10_POW[(m & 0xF)];
        }
        if (m >>= 4 != 0)
        {
          i2 = 0;
          while (m > 0)
          {
            if ((m & 0x1) != 0) {
              d1 *= BIG_10_POW[i2];
            }
            i2++;
            m >>= 1;
          }
        }
      }
      else if (m < 0)
      {
        m = -m;
        if (this.decExponent < -46) {
          return this.isNegative ? -0.0F : 0.0F;
        }
        if ((m & 0xF) != 0) {
          d1 /= SMALL_10_POW[(m & 0xF)];
        }
        if (m >>= 4 != 0)
        {
          i2 = 0;
          while (m > 0)
          {
            if ((m & 0x1) != 0) {
              d1 *= TINY_10_POW[i2];
            }
            i2++;
            m >>= 1;
          }
        }
      }
      f = Math.max(Float.MIN_VALUE, Math.min(Float.MAX_VALUE, (float)d1));
      if (this.nDigits > 200)
      {
        this.nDigits = 201;
        this.digits['È'] = '1';
      }
      FDBigInteger localFDBigInteger1 = new FDBigInteger(j, this.digits, i, this.nDigits);
      m = this.decExponent - this.nDigits;
      int i3 = Float.floatToRawIntBits(f);
      int i4 = Math.max(0, -m);
      int i5 = Math.max(0, m);
      localFDBigInteger1 = localFDBigInteger1.multByPow52(i5, 0);
      localFDBigInteger1.makeImmutable();
      FDBigInteger localFDBigInteger2 = null;
      int i6 = 0;
      for (;;)
      {
        int i7 = i3 >>> 23;
        int i8 = i3 & 0x7FFFFF;
        if (i7 > 0)
        {
          i8 |= 0x800000;
        }
        else
        {
          assert (i8 != 0) : i8;
          i9 = Integer.numberOfLeadingZeros(i8);
          i10 = i9 - 8;
          i8 <<= i10;
          i7 = 1 - i10;
        }
        i7 -= 127;
        int i9 = Integer.numberOfTrailingZeros(i8);
        i8 >>>= i9;
        int i10 = i7 - 23 + i9;
        int i11 = 24 - i9;
        int i12 = i4;
        int i13 = i5;
        if (i10 >= 0) {
          i12 += i10;
        } else {
          i13 -= i10;
        }
        int i14 = i12;
        int i15;
        if (i7 <= -127) {
          i15 = i7 + i9 + 127;
        } else {
          i15 = 1 + i9;
        }
        i12 += i15;
        i13 += i15;
        int i16 = Math.min(i12, Math.min(i13, i14));
        i12 -= i16;
        i13 -= i16;
        i14 -= i16;
        FDBigInteger localFDBigInteger3 = FDBigInteger.valueOfMulPow52(i8, i4, i12);
        if ((localFDBigInteger2 == null) || (i6 != i13))
        {
          localFDBigInteger2 = localFDBigInteger1.leftShift(i13);
          i6 = i13;
        }
        int i18;
        FDBigInteger localFDBigInteger4;
        if ((i17 = localFDBigInteger3.cmp(localFDBigInteger2)) > 0)
        {
          i18 = 1;
          localFDBigInteger4 = localFDBigInteger3.leftInplaceSub(localFDBigInteger2);
          if ((i11 == 1) && (i10 > -126))
          {
            i14--;
            if (i14 < 0)
            {
              i14 = 0;
              localFDBigInteger4 = localFDBigInteger4.leftShift(1);
            }
          }
        }
        else
        {
          if (i17 >= 0) {
            break;
          }
          i18 = 0;
          localFDBigInteger4 = localFDBigInteger2.rightInplaceSub(localFDBigInteger3);
        }
        int i17 = localFDBigInteger4.cmpPow52(i4, i14);
        if (i17 >= 0) {
          if (i17 == 0)
          {
            if ((i3 & 0x1) != 0) {
              i3 += (i18 != 0 ? -1 : 1);
            }
          }
          else
          {
            i3 += (i18 != 0 ? -1 : 1);
            if (i3 != 0) {
              if (i3 == 2139095040) {
                break;
              }
            }
          }
        }
      }
      if (this.isNegative) {
        i3 |= 0x80000000;
      }
      return Float.intBitsToFloat(i3);
    }
  }
  
  static abstract interface ASCIIToBinaryConverter
  {
    public abstract double doubleValue();
    
    public abstract float floatValue();
  }
  
  static class BinaryToASCIIBuffer
    implements FloatingDecimal.BinaryToASCIIConverter
  {
    private boolean isNegative;
    private int decExponent;
    private int firstDigitIndex;
    private int nDigits;
    private final char[] digits;
    private final char[] buffer = new char[26];
    private boolean exactDecimalConversion = false;
    private boolean decimalDigitsRoundedUp = false;
    private static int[] insignificantDigitsNumber = { 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 15, 15, 16, 16, 16, 17, 17, 17, 18, 18, 18, 19 };
    private static final int[] N_5_BITS = { 0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 47, 49, 52, 54, 56, 59, 61 };
    
    BinaryToASCIIBuffer()
    {
      this.digits = new char[20];
    }
    
    BinaryToASCIIBuffer(boolean paramBoolean, char[] paramArrayOfChar)
    {
      this.isNegative = paramBoolean;
      this.decExponent = 0;
      this.digits = paramArrayOfChar;
      this.firstDigitIndex = 0;
      this.nDigits = paramArrayOfChar.length;
    }
    
    public String toJavaFormatString()
    {
      int i = getChars(this.buffer);
      return new String(this.buffer, 0, i);
    }
    
    public void appendTo(Appendable paramAppendable)
    {
      int i = getChars(this.buffer);
      if ((paramAppendable instanceof StringBuilder)) {
        ((StringBuilder)paramAppendable).append(this.buffer, 0, i);
      } else if ((paramAppendable instanceof StringBuffer)) {
        ((StringBuffer)paramAppendable).append(this.buffer, 0, i);
      } else if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    
    public int getDecimalExponent()
    {
      return this.decExponent;
    }
    
    public int getDigits(char[] paramArrayOfChar)
    {
      System.arraycopy(this.digits, this.firstDigitIndex, paramArrayOfChar, 0, this.nDigits);
      return this.nDigits;
    }
    
    public boolean isNegative()
    {
      return this.isNegative;
    }
    
    public boolean isExceptional()
    {
      return false;
    }
    
    public boolean digitsRoundedUp()
    {
      return this.decimalDigitsRoundedUp;
    }
    
    public boolean decimalDigitsExact()
    {
      return this.exactDecimalConversion;
    }
    
    private void setSign(boolean paramBoolean)
    {
      this.isNegative = paramBoolean;
    }
    
    private void developLongDigits(int paramInt1, long paramLong, int paramInt2)
    {
      if (paramInt2 != 0)
      {
        long l1 = FDBigInteger.LONG_5_POW[paramInt2] << paramInt2;
        long l2 = paramLong % l1;
        paramLong /= l1;
        paramInt1 += paramInt2;
        if (l2 >= l1 >> 1) {
          paramLong += 1L;
        }
      }
      int i = this.digits.length - 1;
      int j;
      if (paramLong <= 2147483647L)
      {
        assert (paramLong > 0L) : paramLong;
        int k = (int)paramLong;
        j = k % 10;
        k /= 10;
        while (j == 0)
        {
          paramInt1++;
          j = k % 10;
          k /= 10;
        }
        while (k != 0)
        {
          this.digits[(i--)] = ((char)(j + 48));
          paramInt1++;
          j = k % 10;
          k /= 10;
        }
        this.digits[i] = ((char)(j + 48));
      }
      else
      {
        j = (int)(paramLong % 10L);
        for (paramLong /= 10L; j == 0; paramLong /= 10L)
        {
          paramInt1++;
          j = (int)(paramLong % 10L);
        }
        while (paramLong != 0L)
        {
          this.digits[(i--)] = ((char)(j + 48));
          paramInt1++;
          j = (int)(paramLong % 10L);
          paramLong /= 10L;
        }
        this.digits[i] = ((char)(j + 48));
      }
      this.decExponent = (paramInt1 + 1);
      this.firstDigitIndex = i;
      this.nDigits = (this.digits.length - i);
    }
    
    private void dtoa(int paramInt1, long paramLong, int paramInt2, boolean paramBoolean)
    {
      assert (paramLong > 0L);
      assert ((paramLong & 0x10000000000000) != 0L);
      int i = Long.numberOfTrailingZeros(paramLong);
      int j = 53 - i;
      this.decimalDigitsRoundedUp = false;
      this.exactDecimalConversion = false;
      int k = Math.max(0, j - paramInt1 - 1);
      if ((paramInt1 <= 62) && (paramInt1 >= -21) && (k < FDBigInteger.LONG_5_POW.length) && (j + N_5_BITS[k] < 64) && (k == 0))
      {
        if (paramInt1 > paramInt2) {
          m = insignificantDigitsForPow2(paramInt1 - paramInt2 - 1);
        } else {
          m = 0;
        }
        if (paramInt1 >= 52) {
          paramLong <<= paramInt1 - 52;
        } else {
          paramLong >>>= 52 - paramInt1;
        }
        developLongDigits(0, paramLong, m);
        return;
      }
      int m = estimateDecExp(paramLong, paramInt1);
      int i1 = Math.max(0, -m);
      int n = i1 + k + paramInt1;
      int i3 = Math.max(0, m);
      int i2 = i3 + k;
      int i5 = i1;
      int i4 = n - paramInt2;
      paramLong >>>= i;
      n -= j - 1;
      int i6 = Math.min(n, i2);
      n -= i6;
      i2 -= i6;
      i4 -= i6;
      if (j == 1) {
        i4--;
      }
      if (i4 < 0)
      {
        n -= i4;
        i2 -= i4;
        i4 = 0;
      }
      int i7 = 0;
      int i11 = j + n + (i1 < N_5_BITS.length ? N_5_BITS[i1] : i1 * 3);
      int i12 = i2 + 1 + (i3 + 1 < N_5_BITS.length ? N_5_BITS[(i3 + 1)] : (i3 + 1) * 3);
      int i14;
      int i10;
      int i8;
      int i9;
      long l1;
      if ((i11 < 64) && (i12 < 64))
      {
        if ((i11 < 32) && (i12 < 32))
        {
          int i13 = (int)paramLong * FDBigInteger.SMALL_5_POW[i1] << n;
          i14 = FDBigInteger.SMALL_5_POW[i3] << i2;
          int i15 = FDBigInteger.SMALL_5_POW[i5] << i4;
          int i16 = i14 * 10;
          i7 = 0;
          i10 = i13 / i14;
          i13 = 10 * (i13 % i14);
          i15 *= 10;
          i8 = i13 < i15 ? 1 : 0;
          i9 = i13 + i15 > i16 ? 1 : 0;
          assert (i10 < 10) : i10;
          if ((i10 == 0) && (i9 == 0)) {
            m--;
          } else {
            this.digits[(i7++)] = ((char)(48 + i10));
          }
          if ((!paramBoolean) || (m < -3) || (m >= 8)) {
            i9 = i8 = 0;
          }
          while ((i8 == 0) && (i9 == 0))
          {
            i10 = i13 / i14;
            i13 = 10 * (i13 % i14);
            i15 *= 10;
            assert (i10 < 10) : i10;
            if (i15 > 0L)
            {
              i8 = i13 < i15 ? 1 : 0;
              i9 = i13 + i15 > i16 ? 1 : 0;
            }
            else
            {
              i8 = 1;
              i9 = 1;
            }
            this.digits[(i7++)] = ((char)(48 + i10));
          }
          l1 = (i13 << 1) - i16;
          this.exactDecimalConversion = (i13 == 0);
        }
        else
        {
          long l2 = paramLong * FDBigInteger.LONG_5_POW[i1] << n;
          long l3 = FDBigInteger.LONG_5_POW[i3] << i2;
          long l4 = FDBigInteger.LONG_5_POW[i5] << i4;
          long l5 = l3 * 10L;
          i7 = 0;
          i10 = (int)(l2 / l3);
          l2 = 10L * (l2 % l3);
          l4 *= 10L;
          i8 = l2 < l4 ? 1 : 0;
          i9 = l2 + l4 > l5 ? 1 : 0;
          assert (i10 < 10) : i10;
          if ((i10 == 0) && (i9 == 0)) {
            m--;
          } else {
            this.digits[(i7++)] = ((char)(48 + i10));
          }
          if ((!paramBoolean) || (m < -3) || (m >= 8)) {
            i9 = i8 = 0;
          }
          while ((i8 == 0) && (i9 == 0))
          {
            i10 = (int)(l2 / l3);
            l2 = 10L * (l2 % l3);
            l4 *= 10L;
            assert (i10 < 10) : i10;
            if (l4 > 0L)
            {
              i8 = l2 < l4 ? 1 : 0;
              i9 = l2 + l4 > l5 ? 1 : 0;
            }
            else
            {
              i8 = 1;
              i9 = 1;
            }
            this.digits[(i7++)] = ((char)(48 + i10));
          }
          l1 = (l2 << 1) - l5;
          this.exactDecimalConversion = (l2 == 0L);
        }
      }
      else
      {
        FDBigInteger localFDBigInteger1 = FDBigInteger.valueOfPow52(i3, i2);
        i14 = localFDBigInteger1.getNormalizationBias();
        localFDBigInteger1 = localFDBigInteger1.leftShift(i14);
        FDBigInteger localFDBigInteger2 = FDBigInteger.valueOfMulPow52(paramLong, i1, n + i14);
        FDBigInteger localFDBigInteger3 = FDBigInteger.valueOfPow52(i5 + 1, i4 + i14 + 1);
        FDBigInteger localFDBigInteger4 = FDBigInteger.valueOfPow52(i3 + 1, i2 + i14 + 1);
        i7 = 0;
        i10 = localFDBigInteger2.quoRemIteration(localFDBigInteger1);
        i8 = localFDBigInteger2.cmp(localFDBigInteger3) < 0 ? 1 : 0;
        i9 = localFDBigInteger4.addAndCmp(localFDBigInteger2, localFDBigInteger3) <= 0 ? 1 : 0;
        assert (i10 < 10) : i10;
        if ((i10 == 0) && (i9 == 0)) {
          m--;
        } else {
          this.digits[(i7++)] = ((char)(48 + i10));
        }
        if ((!paramBoolean) || (m < -3) || (m >= 8)) {
          i9 = i8 = 0;
        }
        while ((i8 == 0) && (i9 == 0))
        {
          i10 = localFDBigInteger2.quoRemIteration(localFDBigInteger1);
          assert (i10 < 10) : i10;
          localFDBigInteger3 = localFDBigInteger3.multBy10();
          i8 = localFDBigInteger2.cmp(localFDBigInteger3) < 0 ? 1 : 0;
          i9 = localFDBigInteger4.addAndCmp(localFDBigInteger2, localFDBigInteger3) <= 0 ? 1 : 0;
          this.digits[(i7++)] = ((char)(48 + i10));
        }
        if ((i9 != 0) && (i8 != 0))
        {
          localFDBigInteger2 = localFDBigInteger2.leftShift(1);
          l1 = localFDBigInteger2.cmp(localFDBigInteger4);
        }
        else
        {
          l1 = 0L;
        }
        this.exactDecimalConversion = (localFDBigInteger2.cmp(FDBigInteger.ZERO) == 0);
      }
      this.decExponent = (m + 1);
      this.firstDigitIndex = 0;
      this.nDigits = i7;
      if (i9 != 0) {
        if (i8 != 0)
        {
          if (l1 == 0L)
          {
            if ((this.digits[(this.firstDigitIndex + this.nDigits - 1)] & 0x1) != 0) {
              roundup();
            }
          }
          else if (l1 > 0L) {
            roundup();
          }
        }
        else {
          roundup();
        }
      }
    }
    
    private void roundup()
    {
      int i = this.firstDigitIndex + this.nDigits - 1;
      int j = this.digits[i];
      if (j == 57)
      {
        while ((j == 57) && (i > this.firstDigitIndex))
        {
          this.digits[i] = '0';
          j = this.digits[(--i)];
        }
        if (j == 57)
        {
          this.decExponent += 1;
          this.digits[this.firstDigitIndex] = '1';
          return;
        }
      }
      this.digits[i] = ((char)(j + 1));
      this.decimalDigitsRoundedUp = true;
    }
    
    static int estimateDecExp(long paramLong, int paramInt)
    {
      double d1 = Double.longBitsToDouble(0x3FF0000000000000 | paramLong & 0xFFFFFFFFFFFFF);
      double d2 = (d1 - 1.5D) * 0.289529654D + 0.176091259D + paramInt * 0.301029995663981D;
      long l1 = Double.doubleToRawLongBits(d2);
      int i = (int)((l1 & 0x7FF0000000000000) >> 52) - 1023;
      int j = (l1 & 0x8000000000000000) != 0L ? 1 : 0;
      if ((i >= 0) && (i < 52))
      {
        long l2 = 4503599627370495L >> i;
        int k = (int)((l1 & 0xFFFFFFFFFFFFF | 0x10000000000000) >> 52 - i);
        return j != 0 ? -k - 1 : (l2 & l1) == 0L ? -k : k;
      }
      if (i < 0) {
        return j != 0 ? -1 : (l1 & 0x7FFFFFFFFFFFFFFF) == 0L ? 0 : 0;
      }
      return (int)d2;
    }
    
    private static int insignificantDigits(int paramInt)
    {
      for (int i = 0; paramInt >= 10L; i++) {
        paramInt = (int)(paramInt / 10L);
      }
      return i;
    }
    
    private static int insignificantDigitsForPow2(int paramInt)
    {
      if ((paramInt > 1) && (paramInt < insignificantDigitsNumber.length)) {
        return insignificantDigitsNumber[paramInt];
      }
      return 0;
    }
    
    private int getChars(char[] paramArrayOfChar)
    {
      assert (this.nDigits <= 19) : this.nDigits;
      int i = 0;
      if (this.isNegative)
      {
        paramArrayOfChar[0] = '-';
        i = 1;
      }
      int j;
      if ((this.decExponent > 0) && (this.decExponent < 8))
      {
        j = Math.min(this.nDigits, this.decExponent);
        System.arraycopy(this.digits, this.firstDigitIndex, paramArrayOfChar, i, j);
        i += j;
        if (j < this.decExponent)
        {
          j = this.decExponent - j;
          Arrays.fill(paramArrayOfChar, i, i + j, '0');
          i += j;
          paramArrayOfChar[(i++)] = '.';
          paramArrayOfChar[(i++)] = '0';
        }
        else
        {
          paramArrayOfChar[(i++)] = '.';
          if (j < this.nDigits)
          {
            int k = this.nDigits - j;
            System.arraycopy(this.digits, this.firstDigitIndex + j, paramArrayOfChar, i, k);
            i += k;
          }
          else
          {
            paramArrayOfChar[(i++)] = '0';
          }
        }
      }
      else if ((this.decExponent <= 0) && (this.decExponent > -3))
      {
        paramArrayOfChar[(i++)] = '0';
        paramArrayOfChar[(i++)] = '.';
        if (this.decExponent != 0)
        {
          Arrays.fill(paramArrayOfChar, i, i - this.decExponent, '0');
          i -= this.decExponent;
        }
        System.arraycopy(this.digits, this.firstDigitIndex, paramArrayOfChar, i, this.nDigits);
        i += this.nDigits;
      }
      else
      {
        paramArrayOfChar[(i++)] = this.digits[this.firstDigitIndex];
        paramArrayOfChar[(i++)] = '.';
        if (this.nDigits > 1)
        {
          System.arraycopy(this.digits, this.firstDigitIndex + 1, paramArrayOfChar, i, this.nDigits - 1);
          i += this.nDigits - 1;
        }
        else
        {
          paramArrayOfChar[(i++)] = '0';
        }
        paramArrayOfChar[(i++)] = 'E';
        if (this.decExponent <= 0)
        {
          paramArrayOfChar[(i++)] = '-';
          j = -this.decExponent + 1;
        }
        else
        {
          j = this.decExponent - 1;
        }
        if (j <= 9)
        {
          paramArrayOfChar[(i++)] = ((char)(j + 48));
        }
        else if (j <= 99)
        {
          paramArrayOfChar[(i++)] = ((char)(j / 10 + 48));
          paramArrayOfChar[(i++)] = ((char)(j % 10 + 48));
        }
        else
        {
          paramArrayOfChar[(i++)] = ((char)(j / 100 + 48));
          j %= 100;
          paramArrayOfChar[(i++)] = ((char)(j / 10 + 48));
          paramArrayOfChar[(i++)] = ((char)(j % 10 + 48));
        }
      }
      return i;
    }
  }
  
  public static abstract interface BinaryToASCIIConverter
  {
    public abstract String toJavaFormatString();
    
    public abstract void appendTo(Appendable paramAppendable);
    
    public abstract int getDecimalExponent();
    
    public abstract int getDigits(char[] paramArrayOfChar);
    
    public abstract boolean isNegative();
    
    public abstract boolean isExceptional();
    
    public abstract boolean digitsRoundedUp();
    
    public abstract boolean decimalDigitsExact();
  }
  
  private static class ExceptionalBinaryToASCIIBuffer
    implements FloatingDecimal.BinaryToASCIIConverter
  {
    private final String image;
    private boolean isNegative;
    
    public ExceptionalBinaryToASCIIBuffer(String paramString, boolean paramBoolean)
    {
      this.image = paramString;
      this.isNegative = paramBoolean;
    }
    
    public String toJavaFormatString()
    {
      return this.image;
    }
    
    public void appendTo(Appendable paramAppendable)
    {
      if ((paramAppendable instanceof StringBuilder)) {
        ((StringBuilder)paramAppendable).append(this.image);
      } else if ((paramAppendable instanceof StringBuffer)) {
        ((StringBuffer)paramAppendable).append(this.image);
      } else if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    
    public int getDecimalExponent()
    {
      throw new IllegalArgumentException("Exceptional value does not have an exponent");
    }
    
    public int getDigits(char[] paramArrayOfChar)
    {
      throw new IllegalArgumentException("Exceptional value does not have digits");
    }
    
    public boolean isNegative()
    {
      return this.isNegative;
    }
    
    public boolean isExceptional()
    {
      return true;
    }
    
    public boolean digitsRoundedUp()
    {
      throw new IllegalArgumentException("Exceptional value is not rounded");
    }
    
    public boolean decimalDigitsExact()
    {
      throw new IllegalArgumentException("Exceptional value is not exact");
    }
  }
  
  private static class HexFloatPattern
  {
    private static final Pattern VALUE = Pattern.compile("([-+])?0[xX](((\\p{XDigit}+)\\.?)|((\\p{XDigit}*)\\.(\\p{XDigit}+)))[pP]([-+])?(\\p{Digit}+)[fFdD]?");
    
    private HexFloatPattern() {}
  }
  
  static class PreparedASCIIToBinaryBuffer
    implements FloatingDecimal.ASCIIToBinaryConverter
  {
    private final double doubleVal;
    private final float floatVal;
    
    public PreparedASCIIToBinaryBuffer(double paramDouble, float paramFloat)
    {
      this.doubleVal = paramDouble;
      this.floatVal = paramFloat;
    }
    
    public double doubleValue()
    {
      return this.doubleVal;
    }
    
    public float floatValue()
    {
      return this.floatVal;
    }
  }
}
