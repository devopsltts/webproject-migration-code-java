package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

class Coding
  implements Comparable<Coding>, CodingMethod, Histogram.BitMetric
{
  public static final int B_MAX = 5;
  public static final int H_MAX = 256;
  public static final int S_MAX = 2;
  private final int B;
  private final int H;
  private final int L;
  private final int S;
  private final int del;
  private final int min;
  private final int max;
  private final int umin;
  private final int umax;
  private final int[] byteMin;
  private final int[] byteMax;
  private static Map<Coding, Coding> codeMap;
  private static final byte[] byteBitWidths;
  static boolean verboseStringForDebug = false;
  
  private static int saturate32(long paramLong)
  {
    if (paramLong > 2147483647L) {
      return Integer.MAX_VALUE;
    }
    if (paramLong < -2147483648L) {
      return Integer.MIN_VALUE;
    }
    return (int)paramLong;
  }
  
  private static long codeRangeLong(int paramInt1, int paramInt2)
  {
    return codeRangeLong(paramInt1, paramInt2, paramInt1);
  }
  
  private static long codeRangeLong(int paramInt1, int paramInt2, int paramInt3)
  {
    assert ((paramInt3 >= 0) && (paramInt3 <= paramInt1));
    assert ((paramInt1 >= 1) && (paramInt1 <= 5));
    assert ((paramInt2 >= 1) && (paramInt2 <= 256));
    if (paramInt3 == 0) {
      return 0L;
    }
    if (paramInt1 == 1) {
      return paramInt2;
    }
    int i = 256 - paramInt2;
    long l1 = 0L;
    long l2 = 1L;
    for (int j = 1; j <= paramInt3; j++)
    {
      l1 += l2;
      l2 *= paramInt2;
    }
    l1 *= i;
    if (paramInt3 == paramInt1) {
      l1 += l2;
    }
    return l1;
  }
  
  public static int codeMax(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    long l1 = codeRangeLong(paramInt1, paramInt2, paramInt4);
    if (l1 == 0L) {
      return -1;
    }
    if ((paramInt3 == 0) || (l1 >= 4294967296L)) {
      return saturate32(l1 - 1L);
    }
    for (long l2 = l1 - 1L; isNegativeCode(l2, paramInt3); l2 -= 1L) {}
    if (l2 < 0L) {
      return -1;
    }
    int i = decodeSign32(l2, paramInt3);
    if (i < 0) {
      return Integer.MAX_VALUE;
    }
    return i;
  }
  
  public static int codeMin(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    long l1 = codeRangeLong(paramInt1, paramInt2, paramInt4);
    if ((l1 >= 4294967296L) && (paramInt4 == paramInt1)) {
      return Integer.MIN_VALUE;
    }
    if (paramInt3 == 0) {
      return 0;
    }
    for (long l2 = l1 - 1L; !isNegativeCode(l2, paramInt3); l2 -= 1L) {}
    if (l2 < 0L) {
      return 0;
    }
    return decodeSign32(l2, paramInt3);
  }
  
  private static long toUnsigned32(int paramInt)
  {
    return paramInt << 32 >>> 32;
  }
  
  private static boolean isNegativeCode(long paramLong, int paramInt)
  {
    assert (paramInt > 0);
    assert (paramLong >= -1L);
    int i = (1 << paramInt) - 1;
    return ((int)paramLong + 1 & i) == 0;
  }
  
  private static boolean hasNegativeCode(int paramInt1, int paramInt2)
  {
    assert (paramInt2 > 0);
    return (0 > paramInt1) && (paramInt1 >= (-1 >>> paramInt2 ^ 0xFFFFFFFF));
  }
  
  private static int decodeSign32(long paramLong, int paramInt)
  {
    assert (paramLong == toUnsigned32((int)paramLong)) : Long.toHexString(paramLong);
    if (paramInt == 0) {
      return (int)paramLong;
    }
    int i;
    if (isNegativeCode(paramLong, paramInt)) {
      i = (int)paramLong >>> paramInt ^ 0xFFFFFFFF;
    } else {
      i = (int)paramLong - ((int)paramLong >>> paramInt);
    }
    assert ((paramInt != 1) || (i == ((int)paramLong >>> 1 ^ -((int)paramLong & 0x1))));
    return i;
  }
  
  private static long encodeSign32(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return toUnsigned32(paramInt1);
    }
    int i = (1 << paramInt2) - 1;
    if (!hasNegativeCode(paramInt1, paramInt2)) {
      l = paramInt1 + toUnsigned32(paramInt1) / i;
    } else {
      l = (-paramInt1 << paramInt2) - 1;
    }
    long l = toUnsigned32((int)l);
    assert (paramInt1 == decodeSign32(l, paramInt2)) : (Long.toHexString(l) + " -> " + Integer.toHexString(paramInt1) + " != " + Integer.toHexString(decodeSign32(l, paramInt2)));
    return l;
  }
  
  public static void writeInt(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    long l1 = encodeSign32(paramInt1, paramInt4);
    assert (l1 == toUnsigned32((int)l1));
    assert (l1 < codeRangeLong(paramInt2, paramInt3)) : Long.toHexString(l1);
    int i = 256 - paramInt3;
    long l2 = l1;
    int j = paramArrayOfInt[0];
    for (int k = 0; (k < paramInt2 - 1) && (l2 >= i); k++)
    {
      l2 -= i;
      int m = (int)(i + l2 % paramInt3);
      l2 /= paramInt3;
      paramArrayOfByte[(j++)] = ((byte)m);
    }
    paramArrayOfByte[(j++)] = ((byte)(int)l2);
    paramArrayOfInt[0] = j;
  }
  
  public static int readInt(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 256 - paramInt2;
    long l1 = 0L;
    long l2 = 1L;
    int j = paramArrayOfInt[0];
    for (int k = 0; k < paramInt1; k++)
    {
      int m = paramArrayOfByte[(j++)] & 0xFF;
      l1 += m * l2;
      l2 *= paramInt2;
      if (m < i) {
        break;
      }
    }
    paramArrayOfInt[0] = j;
    return decodeSign32(l1, paramInt3);
  }
  
  public static int readIntFrom(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    int i = 256 - paramInt2;
    long l1 = 0L;
    long l2 = 1L;
    for (int j = 0; j < paramInt1; j++)
    {
      int k = paramInputStream.read();
      if (k < 0) {
        throw new RuntimeException("unexpected EOF");
      }
      l1 += k * l2;
      l2 *= paramInt2;
      if (k < i) {
        break;
      }
    }
    assert ((l1 >= 0L) && (l1 < codeRangeLong(paramInt1, paramInt2)));
    return decodeSign32(l1, paramInt3);
  }
  
  private Coding(int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramInt1, paramInt2, paramInt3, 0);
  }
  
  private Coding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.B = paramInt1;
    this.H = paramInt2;
    this.L = (256 - paramInt2);
    this.S = paramInt3;
    this.del = paramInt4;
    this.min = codeMin(paramInt1, paramInt2, paramInt3, paramInt1);
    this.max = codeMax(paramInt1, paramInt2, paramInt3, paramInt1);
    this.umin = codeMin(paramInt1, paramInt2, 0, paramInt1);
    this.umax = codeMax(paramInt1, paramInt2, 0, paramInt1);
    this.byteMin = new int[paramInt1];
    this.byteMax = new int[paramInt1];
    for (int i = 1; i <= paramInt1; i++)
    {
      this.byteMin[(i - 1)] = codeMin(paramInt1, paramInt2, paramInt3, i);
      this.byteMax[(i - 1)] = codeMax(paramInt1, paramInt2, paramInt3, i);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Coding)) {
      return false;
    }
    Coding localCoding = (Coding)paramObject;
    if (this.B != localCoding.B) {
      return false;
    }
    if (this.H != localCoding.H) {
      return false;
    }
    if (this.S != localCoding.S) {
      return false;
    }
    return this.del == localCoding.del;
  }
  
  public int hashCode()
  {
    return (this.del << 14) + (this.S << 11) + (this.B << 8) + (this.H << 0);
  }
  
  private static synchronized Coding of(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (codeMap == null) {
      codeMap = new HashMap();
    }
    Coding localCoding1 = new Coding(paramInt1, paramInt2, paramInt3, paramInt4);
    Coding localCoding2 = (Coding)codeMap.get(localCoding1);
    if (localCoding2 == null) {
      codeMap.put(localCoding1, localCoding2 = localCoding1);
    }
    return localCoding2;
  }
  
  public static Coding of(int paramInt1, int paramInt2)
  {
    return of(paramInt1, paramInt2, 0, 0);
  }
  
  public static Coding of(int paramInt1, int paramInt2, int paramInt3)
  {
    return of(paramInt1, paramInt2, paramInt3, 0);
  }
  
  public boolean canRepresentValue(int paramInt)
  {
    if (isSubrange()) {
      return canRepresentUnsigned(paramInt);
    }
    return canRepresentSigned(paramInt);
  }
  
  public boolean canRepresentSigned(int paramInt)
  {
    return (paramInt >= this.min) && (paramInt <= this.max);
  }
  
  public boolean canRepresentUnsigned(int paramInt)
  {
    return (paramInt >= this.umin) && (paramInt <= this.umax);
  }
  
  public int readFrom(byte[] paramArrayOfByte, int[] paramArrayOfInt)
  {
    return readInt(paramArrayOfByte, paramArrayOfInt, this.B, this.H, this.S);
  }
  
  public void writeTo(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt)
  {
    writeInt(paramArrayOfByte, paramArrayOfInt, paramInt, this.B, this.H, this.S);
  }
  
  public int readFrom(InputStream paramInputStream)
    throws IOException
  {
    return readIntFrom(paramInputStream, this.B, this.H, this.S);
  }
  
  public void writeTo(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[this.B];
    int[] arrayOfInt = new int[1];
    writeInt(arrayOfByte, arrayOfInt, paramInt, this.B, this.H, this.S);
    paramOutputStream.write(arrayOfByte, 0, arrayOfInt[0]);
  }
  
  public void readArrayFrom(InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      paramArrayOfInt[i] = readFrom(paramInputStream);
    }
    for (i = 0; i < this.del; i++)
    {
      long l = 0L;
      for (int j = paramInt1; j < paramInt2; j++)
      {
        l += paramArrayOfInt[j];
        if (isSubrange()) {
          l = reduceToUnsignedRange(l);
        }
        paramArrayOfInt[j] = ((int)l);
      }
    }
  }
  
  public void writeArrayTo(OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 <= paramInt1) {
      return;
    }
    for (int i = 0; i < this.del; i++)
    {
      int[] arrayOfInt1;
      if (!isSubrange()) {
        arrayOfInt1 = makeDeltas(paramArrayOfInt, paramInt1, paramInt2, 0, 0);
      } else {
        arrayOfInt1 = makeDeltas(paramArrayOfInt, paramInt1, paramInt2, this.min, this.max);
      }
      paramArrayOfInt = arrayOfInt1;
      paramInt1 = 0;
      paramInt2 = arrayOfInt1.length;
    }
    byte[] arrayOfByte = new byte['Ā'];
    int j = arrayOfByte.length - this.B;
    int[] arrayOfInt2 = { 0 };
    int k = paramInt1;
    while (k < paramInt2)
    {
      while (arrayOfInt2[0] <= j)
      {
        writeTo(arrayOfByte, arrayOfInt2, paramArrayOfInt[(k++)]);
        if (k >= paramInt2) {
          break;
        }
      }
      paramOutputStream.write(arrayOfByte, 0, arrayOfInt2[0]);
      arrayOfInt2[0] = 0;
    }
  }
  
  boolean isSubrange()
  {
    return (this.max < Integer.MAX_VALUE) && (this.max - this.min + 1L <= 2147483647L);
  }
  
  boolean isFullRange()
  {
    return (this.max == Integer.MAX_VALUE) && (this.min == Integer.MIN_VALUE);
  }
  
  int getRange()
  {
    assert (isSubrange());
    return this.max - this.min + 1;
  }
  
  Coding setB(int paramInt)
  {
    return of(paramInt, this.H, this.S, this.del);
  }
  
  Coding setH(int paramInt)
  {
    return of(this.B, paramInt, this.S, this.del);
  }
  
  Coding setS(int paramInt)
  {
    return of(this.B, this.H, paramInt, this.del);
  }
  
  Coding setL(int paramInt)
  {
    return setH(256 - paramInt);
  }
  
  Coding setD(int paramInt)
  {
    return of(this.B, this.H, this.S, paramInt);
  }
  
  Coding getDeltaCoding()
  {
    return setD(this.del + 1);
  }
  
  Coding getValueCoding()
  {
    if (isDelta()) {
      return of(this.B, this.H, 0, this.del - 1);
    }
    return this;
  }
  
  int reduceToUnsignedRange(long paramLong)
  {
    if ((paramLong == (int)paramLong) && (canRepresentUnsigned((int)paramLong))) {
      return (int)paramLong;
    }
    int i = getRange();
    assert (i > 0);
    paramLong %= i;
    if (paramLong < 0L) {
      paramLong += i;
    }
    assert (canRepresentUnsigned((int)paramLong));
    return (int)paramLong;
  }
  
  int reduceToSignedRange(int paramInt)
  {
    if (canRepresentSigned(paramInt)) {
      return paramInt;
    }
    return reduceToSignedRange(paramInt, this.min, this.max);
  }
  
  static int reduceToSignedRange(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt3 - paramInt2 + 1;
    assert (i > 0);
    int j = paramInt1;
    paramInt1 -= paramInt2;
    if ((paramInt1 < 0) && (j >= 0))
    {
      paramInt1 -= i;
      assert (paramInt1 >= 0);
    }
    paramInt1 %= i;
    if (paramInt1 < 0) {
      paramInt1 += i;
    }
    paramInt1 += paramInt2;
    assert ((paramInt2 <= paramInt1) && (paramInt1 <= paramInt3));
    return paramInt1;
  }
  
  boolean isSigned()
  {
    return this.min < 0;
  }
  
  boolean isDelta()
  {
    return this.del != 0;
  }
  
  public int B()
  {
    return this.B;
  }
  
  public int H()
  {
    return this.H;
  }
  
  public int L()
  {
    return this.L;
  }
  
  public int S()
  {
    return this.S;
  }
  
  public int del()
  {
    return this.del;
  }
  
  public int min()
  {
    return this.min;
  }
  
  public int max()
  {
    return this.max;
  }
  
  public int umin()
  {
    return this.umin;
  }
  
  public int umax()
  {
    return this.umax;
  }
  
  public int byteMin(int paramInt)
  {
    return this.byteMin[(paramInt - 1)];
  }
  
  public int byteMax(int paramInt)
  {
    return this.byteMax[(paramInt - 1)];
  }
  
  public int compareTo(Coding paramCoding)
  {
    int i = this.del - paramCoding.del;
    if (i == 0) {
      i = this.B - paramCoding.B;
    }
    if (i == 0) {
      i = this.H - paramCoding.H;
    }
    if (i == 0) {
      i = this.S - paramCoding.S;
    }
    return i;
  }
  
  public int distanceFrom(Coding paramCoding)
  {
    int i = this.del - paramCoding.del;
    if (i < 0) {
      i = -i;
    }
    int j = this.S - paramCoding.S;
    if (j < 0) {
      j = -j;
    }
    int k = this.B - paramCoding.B;
    if (k < 0) {
      k = -k;
    }
    int m;
    if (this.H == paramCoding.H)
    {
      m = 0;
    }
    else
    {
      n = getHL();
      int i1 = paramCoding.getHL();
      n *= n;
      i1 *= i1;
      if (n > i1) {
        m = ceil_lg2(1 + (n - 1) / i1);
      } else {
        m = ceil_lg2(1 + (i1 - 1) / n);
      }
    }
    int n = 5 * (i + j + k) + m;
    assert ((n != 0) || (compareTo(paramCoding) == 0));
    return n;
  }
  
  private int getHL()
  {
    if (this.H <= 128) {
      return this.H;
    }
    if (this.L >= 1) {
      return 16384 / this.L;
    }
    return 32768;
  }
  
  static int ceil_lg2(int paramInt)
  {
    assert (paramInt - 1 >= 0);
    paramInt--;
    int i = 0;
    while (paramInt != 0)
    {
      i++;
      paramInt >>= 1;
    }
    return i;
  }
  
  static int bitWidth(int paramInt)
  {
    if (paramInt < 0) {
      paramInt ^= 0xFFFFFFFF;
    }
    int i = 0;
    int j = paramInt;
    if (j < byteBitWidths.length) {
      return byteBitWidths[j];
    }
    int k = j >>> 16;
    if (k != 0)
    {
      j = k;
      i += 16;
    }
    k = j >>> 8;
    if (k != 0)
    {
      j = k;
      i += 8;
    }
    i += byteBitWidths[j];
    return i;
  }
  
  static int[] makeDeltas(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    assert (paramInt4 >= paramInt3);
    int i = paramInt2 - paramInt1;
    int[] arrayOfInt = new int[i];
    int j = 0;
    int k;
    int m;
    if (paramInt3 == paramInt4) {
      for (k = 0; k < i; k++)
      {
        m = paramArrayOfInt[(paramInt1 + k)];
        arrayOfInt[k] = (m - j);
        j = m;
      }
    } else {
      for (k = 0; k < i; k++)
      {
        m = paramArrayOfInt[(paramInt1 + k)];
        assert ((m >= 0) && (m + paramInt3 <= paramInt4));
        int n = m - j;
        assert (n == m - j);
        j = m;
        n = reduceToSignedRange(n, paramInt3, paramInt4);
        arrayOfInt[k] = n;
      }
    }
    return arrayOfInt;
  }
  
  boolean canRepresent(int paramInt1, int paramInt2)
  {
    assert (paramInt1 <= paramInt2);
    if (this.del > 0)
    {
      if (isSubrange()) {
        return (canRepresentUnsigned(paramInt2)) && (canRepresentUnsigned(paramInt1));
      }
      return isFullRange();
    }
    return (canRepresentSigned(paramInt2)) && (canRepresentSigned(paramInt1));
  }
  
  boolean canRepresent(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    if (i == 0) {
      return true;
    }
    if (isFullRange()) {
      return true;
    }
    int j = paramArrayOfInt[paramInt1];
    int k = j;
    for (int m = 1; m < i; m++)
    {
      int n = paramArrayOfInt[(paramInt1 + m)];
      if (j < n) {
        j = n;
      }
      if (k > n) {
        k = n;
      }
    }
    return canRepresent(k, j);
  }
  
  public double getBitLength(int paramInt)
  {
    return getLength(paramInt) * 8.0D;
  }
  
  public int getLength(int paramInt)
  {
    if ((isDelta()) && (isSubrange()))
    {
      if (!canRepresentUnsigned(paramInt)) {
        return Integer.MAX_VALUE;
      }
      paramInt = reduceToSignedRange(paramInt);
    }
    int i;
    if (paramInt >= 0) {
      for (i = 0; i < this.B; i++) {
        if (paramInt <= this.byteMax[i]) {
          return i + 1;
        }
      }
    } else {
      for (i = 0; i < this.B; i++) {
        if (paramInt >= this.byteMin[i]) {
          return i + 1;
        }
      }
    }
    return Integer.MAX_VALUE;
  }
  
  public int getLength(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt1 = paramInt2 - paramInt1;
    if (this.B == 1) {
      return arrayOfInt1;
    }
    if (this.L == 0) {
      return arrayOfInt1 * this.B;
    }
    if (isDelta())
    {
      if (!isSubrange()) {
        arrayOfInt2 = makeDeltas(paramArrayOfInt, paramInt1, paramInt2, 0, 0);
      } else {
        arrayOfInt2 = makeDeltas(paramArrayOfInt, paramInt1, paramInt2, this.min, this.max);
      }
      paramArrayOfInt = arrayOfInt2;
      paramInt1 = 0;
    }
    int[] arrayOfInt2 = arrayOfInt1;
    int i;
    for (int j = 1; j <= this.B; j++)
    {
      int k = this.byteMax[(j - 1)];
      int m = this.byteMin[(j - 1)];
      int[] arrayOfInt3 = 0;
      for (int n = 0; n < arrayOfInt1; n++)
      {
        int i1 = paramArrayOfInt[(paramInt1 + n)];
        if (i1 >= 0)
        {
          if (i1 > k) {
            arrayOfInt3++;
          }
        }
        else if (i1 < m) {
          arrayOfInt3++;
        }
      }
      if (arrayOfInt3 == 0) {
        break;
      }
      if (j == this.B) {
        return Integer.MAX_VALUE;
      }
      arrayOfInt2 += arrayOfInt3;
    }
    return i;
  }
  
  public byte[] getMetaCoding(Coding paramCoding)
  {
    if (paramCoding == this) {
      return new byte[] { 0 };
    }
    int i = BandStructure.indexOf(this);
    if (i > 0) {
      return new byte[] { (byte)i };
    }
    return new byte[] { 116, (byte)(this.del + 2 * this.S + 8 * (this.B - 1)), (byte)(this.H - 1) };
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod)
  {
    int i = paramArrayOfByte[(paramInt++)] & 0xFF;
    if ((1 <= i) && (i <= 115))
    {
      Coding localCoding = BandStructure.codingForIndex(i);
      assert (localCoding != null);
      paramArrayOfCodingMethod[0] = localCoding;
      return paramInt;
    }
    if (i == 116)
    {
      int j = paramArrayOfByte[(paramInt++)] & 0xFF;
      int k = paramArrayOfByte[(paramInt++)] & 0xFF;
      int m = j % 2;
      int n = j / 2 % 4;
      int i1 = j / 8 + 1;
      int i2 = k + 1;
      if ((1 > i1) || (i1 > 5) || (0 > n) || (n > 2) || (1 > i2) || (i2 > 256) || (0 > m) || (m > 1) || ((i1 == 1) && (i2 != 256)) || ((i1 == 5) && (i2 == 256))) {
        throw new RuntimeException("Bad arb. coding: (" + i1 + "," + i2 + "," + n + "," + m);
      }
      paramArrayOfCodingMethod[0] = of(i1, i2, n, m);
      return paramInt;
    }
    return paramInt - 1;
  }
  
  public String keyString()
  {
    return "(" + this.B + "," + this.H + "," + this.S + "," + this.del + ")";
  }
  
  public String toString()
  {
    String str = "Coding" + keyString();
    return str;
  }
  
  String stringForDebug()
  {
    String str1 = "" + this.min;
    String str2 = "" + this.max;
    String str3 = keyString() + " L=" + this.L + " r=[" + str1 + "," + str2 + "]";
    if (isSubrange()) {
      str3 = str3 + " subrange";
    } else if (!isFullRange()) {
      str3 = str3 + " MIDRANGE";
    }
    if (verboseStringForDebug)
    {
      str3 = str3 + " {";
      int i = 0;
      for (int j = 1; j <= this.B; j++)
      {
        int k = saturate32(this.byteMax[(j - 1)] - this.byteMin[(j - 1)] + 1L);
        assert (k == saturate32(codeRangeLong(this.B, this.H, j)));
        k -= i;
        i = k;
        String str4 = "" + k;
        str3 = str3 + " #" + j + "=" + str4;
      }
      str3 = str3 + " }";
    }
    return str3;
  }
  
  static
  {
    byteBitWidths = new byte['Ā'];
    for (int i = 0; i < byteBitWidths.length; i++) {
      byteBitWidths[i] = ((byte)ceil_lg2(i + 1));
    }
    for (i = 10; i >= 0; i = (i << 1) - (i >> 3)) {
      assert (bitWidth(i) == ceil_lg2(i + 1));
    }
  }
}
