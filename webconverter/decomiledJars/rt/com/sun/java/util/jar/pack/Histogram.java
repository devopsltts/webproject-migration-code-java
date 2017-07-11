package com.sun.java.util.jar.pack;

import [I;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

final class Histogram
{
  protected final int[][] matrix;
  protected final int totalWeight;
  protected final int[] values;
  protected final int[] counts;
  private static final long LOW32 = 4294967295L;
  private static double log2 = Math.log(2.0D);
  private final BitMetric bitMetric = new BitMetric()
  {
    public double getBitLength(int paramAnonymousInt)
    {
      return Histogram.this.getBitLength(paramAnonymousInt);
    }
  };
  
  public Histogram(int[] paramArrayOfInt)
  {
    long[] arrayOfLong = computeHistogram2Col(maybeSort(paramArrayOfInt));
    int[][] arrayOfInt = makeTable(arrayOfLong);
    this.values = arrayOfInt[0];
    this.counts = arrayOfInt[1];
    this.matrix = makeMatrix(arrayOfLong);
    this.totalWeight = paramArrayOfInt.length;
    assert (assertWellFormed(paramArrayOfInt));
  }
  
  public Histogram(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    this(sortedSlice(paramArrayOfInt, paramInt1, paramInt2));
  }
  
  public Histogram(int[][] paramArrayOfInt)
  {
    paramArrayOfInt = normalizeMatrix(paramArrayOfInt);
    this.matrix = paramArrayOfInt;
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramArrayOfInt.length; k++)
    {
      m = paramArrayOfInt[k].length - 1;
      i += m;
      j += paramArrayOfInt[k][0] * m;
    }
    this.totalWeight = j;
    long[] arrayOfLong = new long[i];
    int m = 0;
    for (int n = 0; n < paramArrayOfInt.length; n++) {
      for (int i1 = 1; i1 < paramArrayOfInt[n].length; i1++) {
        arrayOfLong[(m++)] = (paramArrayOfInt[n][i1] << 32 | 0xFFFFFFFF & paramArrayOfInt[n][0]);
      }
    }
    assert (m == arrayOfLong.length);
    Arrays.sort(arrayOfLong);
    int[][] arrayOfInt = makeTable(arrayOfLong);
    this.values = arrayOfInt[1];
    this.counts = arrayOfInt[0];
    assert (assertWellFormed(null));
  }
  
  public int[][] getMatrix()
  {
    return this.matrix;
  }
  
  public int getRowCount()
  {
    return this.matrix.length;
  }
  
  public int getRowFrequency(int paramInt)
  {
    return this.matrix[paramInt][0];
  }
  
  public int getRowLength(int paramInt)
  {
    return this.matrix[paramInt].length - 1;
  }
  
  public int getRowValue(int paramInt1, int paramInt2)
  {
    return this.matrix[paramInt1][(paramInt2 + 1)];
  }
  
  public int getRowWeight(int paramInt)
  {
    return getRowFrequency(paramInt) * getRowLength(paramInt);
  }
  
  public int getTotalWeight()
  {
    return this.totalWeight;
  }
  
  public int getTotalLength()
  {
    return this.values.length;
  }
  
  public int[] getAllValues()
  {
    return this.values;
  }
  
  public int[] getAllFrequencies()
  {
    return this.counts;
  }
  
  public int getFrequency(int paramInt)
  {
    int i = Arrays.binarySearch(this.values, paramInt);
    if (i < 0) {
      return 0;
    }
    assert (this.values[i] == paramInt);
    return this.counts[i];
  }
  
  public double getBitLength(int paramInt)
  {
    double d = getFrequency(paramInt) / getTotalWeight();
    return -Math.log(d) / log2;
  }
  
  public double getRowBitLength(int paramInt)
  {
    double d = getRowFrequency(paramInt) / getTotalWeight();
    return -Math.log(d) / log2;
  }
  
  public BitMetric getBitMetric()
  {
    return this.bitMetric;
  }
  
  public double getBitLength()
  {
    double d = 0.0D;
    for (int i = 0; i < this.matrix.length; i++) {
      d += getRowBitLength(i) * getRowWeight(i);
    }
    assert (0.1D > Math.abs(d - getBitLength(this.bitMetric)));
    return d;
  }
  
  public double getBitLength(BitMetric paramBitMetric)
  {
    double d = 0.0D;
    for (int i = 0; i < this.matrix.length; i++) {
      for (int j = 1; j < this.matrix[i].length; j++) {
        d += this.matrix[i][0] * paramBitMetric.getBitLength(this.matrix[i][j]);
      }
    }
    return d;
  }
  
  private static double round(double paramDouble1, double paramDouble2)
  {
    return Math.round(paramDouble1 * paramDouble2) / paramDouble2;
  }
  
  public int[][] normalizeMatrix(int[][] paramArrayOfInt)
  {
    long[] arrayOfLong = new long[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      if (paramArrayOfInt[i].length > 1)
      {
        j = paramArrayOfInt[i][0];
        if (j > 0) {
          arrayOfLong[i] = (j << 32 | i);
        }
      }
    }
    Arrays.sort(arrayOfLong);
    int[][] arrayOfInt = new int[paramArrayOfInt.length][];
    int j = -1;
    int k = 0;
    int m = 0;
    for (int n = 0;; n++)
    {
      int[] arrayOfInt1;
      if (n < paramArrayOfInt.length)
      {
        long l = arrayOfLong[(arrayOfLong.length - n - 1)];
        if (l == 0L) {
          continue;
        }
        arrayOfInt1 = paramArrayOfInt[((int)l)];
        assert (l >>> 32 == arrayOfInt1[0]);
      }
      else
      {
        arrayOfInt1 = new int[] { -1 };
      }
      if ((arrayOfInt1[0] != j) && (m > k))
      {
        int i1 = 0;
        for (int i2 = k; i2 < m; i2++)
        {
          [I local[I1 = arrayOfInt[i2];
          assert (local[I1[0] == j);
          i1 += local[I1.length - 1;
        }
        Object localObject = new int[1 + i1];
        localObject[0] = j;
        int i3 = 1;
        for (int i4 = k; i4 < m; i4++)
        {
          [I local[I2 = arrayOfInt[i4];
          assert (local[I2[0] == j);
          System.arraycopy(local[I2, 1, localObject, i3, local[I2.length - 1);
          i3 += local[I2.length - 1;
        }
        if (!isSorted((int[])localObject, 1, true))
        {
          Arrays.sort((int[])localObject, 1, localObject.length);
          i4 = 2;
          for (int i5 = 2; i5 < localObject.length; i5++) {
            if (localObject[i5] != localObject[(i5 - 1)]) {
              localObject[(i4++)] = localObject[i5];
            }
          }
          if (i4 < localObject.length)
          {
            int[] arrayOfInt2 = new int[i4];
            System.arraycopy(localObject, 0, arrayOfInt2, 0, i4);
            localObject = arrayOfInt2;
          }
        }
        arrayOfInt[(k++)] = localObject;
        m = k;
      }
      if (n == paramArrayOfInt.length) {
        break;
      }
      j = arrayOfInt1[0];
      arrayOfInt[(m++)] = arrayOfInt1;
    }
    assert (k == m);
    paramArrayOfInt = arrayOfInt;
    if (k < paramArrayOfInt.length)
    {
      arrayOfInt = new int[k][];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, k);
      paramArrayOfInt = arrayOfInt;
    }
    return paramArrayOfInt;
  }
  
  public String[] getRowTitles(String paramString)
  {
    int i = getTotalLength();
    int j = getTotalWeight();
    String[] arrayOfString = new String[this.matrix.length];
    int k = 0;
    int m = 0;
    for (int n = 0; n < this.matrix.length; n++)
    {
      int i1 = getRowFrequency(n);
      int i2 = getRowLength(n);
      int i3 = getRowWeight(n);
      k += i3;
      m += i2;
      long l1 = (k * 100L + j / 2) / j;
      long l2 = (m * 100L + i / 2) / i;
      double d = getRowBitLength(n);
      assert (0.1D > Math.abs(d - getBitLength(this.matrix[n][1])));
      arrayOfString[n] = (paramString + "[" + n + "]" + " len=" + round(d, 10.0D) + " (" + i1 + "*[" + i2 + "])" + " (" + k + ":" + l1 + "%)" + " [" + m + ":" + l2 + "%]");
    }
    return arrayOfString;
  }
  
  public void print(PrintStream paramPrintStream)
  {
    print("hist", paramPrintStream);
  }
  
  public void print(String paramString, PrintStream paramPrintStream)
  {
    print(paramString, getRowTitles(paramString), paramPrintStream);
  }
  
  public void print(String paramString, String[] paramArrayOfString, PrintStream paramPrintStream)
  {
    int i = getTotalLength();
    int j = getTotalWeight();
    double d1 = getBitLength();
    double d2 = d1 / j;
    double d3 = j / i;
    String str = paramString + " len=" + round(d1, 10.0D) + " avgLen=" + round(d2, 10.0D) + " weight(" + j + ")" + " unique[" + i + "]" + " avgWeight(" + round(d3, 100.0D) + ")";
    if (paramArrayOfString == null)
    {
      paramPrintStream.println(str);
    }
    else
    {
      paramPrintStream.println(str + " {");
      StringBuffer localStringBuffer = new StringBuffer();
      for (int k = 0; k < this.matrix.length; k++)
      {
        localStringBuffer.setLength(0);
        localStringBuffer.append("  ").append(paramArrayOfString[k]).append(" {");
        for (int m = 1; m < this.matrix[k].length; m++) {
          localStringBuffer.append(" ").append(this.matrix[k][m]);
        }
        localStringBuffer.append(" }");
        paramPrintStream.println(localStringBuffer);
      }
      paramPrintStream.println("}");
    }
  }
  
  private static int[][] makeMatrix(long[] paramArrayOfLong)
  {
    Arrays.sort(paramArrayOfLong);
    int[] arrayOfInt1 = new int[paramArrayOfLong.length];
    for (int i = 0; i < arrayOfInt1.length; i++) {
      arrayOfInt1[i] = ((int)(paramArrayOfLong[i] >>> 32));
    }
    long[] arrayOfLong = computeHistogram2Col(arrayOfInt1);
    int[][] arrayOfInt = new int[arrayOfLong.length][];
    int j = 0;
    int k = 0;
    int m = arrayOfInt.length;
    for (;;)
    {
      m--;
      if (m < 0) {
        break;
      }
      long l1 = arrayOfLong[(k++)];
      int n = (int)l1;
      int i1 = (int)(l1 >>> 32);
      int[] arrayOfInt2 = new int[1 + i1];
      arrayOfInt2[0] = n;
      for (int i2 = 0; i2 < i1; i2++)
      {
        long l2 = paramArrayOfLong[(j++)];
        assert (l2 >>> 32 == n);
        arrayOfInt2[(1 + i2)] = ((int)l2);
      }
      arrayOfInt[m] = arrayOfInt2;
    }
    assert (j == paramArrayOfLong.length);
    return arrayOfInt;
  }
  
  private static int[][] makeTable(long[] paramArrayOfLong)
  {
    int[][] arrayOfInt = new int[2][paramArrayOfLong.length];
    for (int i = 0; i < paramArrayOfLong.length; i++)
    {
      arrayOfInt[0][i] = ((int)paramArrayOfLong[i]);
      arrayOfInt[1][i] = ((int)(paramArrayOfLong[i] >>> 32));
    }
    return arrayOfInt;
  }
  
  private static long[] computeHistogram2Col(int[] paramArrayOfInt)
  {
    switch (paramArrayOfInt.length)
    {
    case 0: 
      return new long[0];
    case 1: 
      return new long[] { 0x100000000 | 0xFFFFFFFF & paramArrayOfInt[0] };
    }
    long[] arrayOfLong = null;
    for (int i = 1;; i = 0)
    {
      int j = -1;
      int k = paramArrayOfInt[0] ^ 0xFFFFFFFF;
      int m = 0;
      for (int n = 0; n <= paramArrayOfInt.length; n++)
      {
        int i1;
        if (n < paramArrayOfInt.length) {
          i1 = paramArrayOfInt[n];
        } else {
          i1 = k ^ 0xFFFFFFFF;
        }
        if (i1 == k)
        {
          m++;
        }
        else
        {
          if ((i == 0) && (m != 0)) {
            arrayOfLong[j] = (m << 32 | 0xFFFFFFFF & k);
          }
          k = i1;
          m = 1;
          j++;
        }
      }
      if (i == 0) {
        break;
      }
      arrayOfLong = new long[j];
    }
    return arrayOfLong;
  }
  
  private static int[][] regroupHistogram(int[][] paramArrayOfInt, int[] paramArrayOfInt1)
  {
    long l1 = 0L;
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      l1 += paramArrayOfInt[i].length - 1;
    }
    long l2 = 0L;
    for (int j = 0; j < paramArrayOfInt1.length; j++) {
      l2 += paramArrayOfInt1[j];
    }
    if (l2 > l1)
    {
      j = paramArrayOfInt1.length;
      long l4 = l1;
      for (n = 0; n < paramArrayOfInt1.length; n++)
      {
        if (l4 < paramArrayOfInt1[n])
        {
          int[] arrayOfInt2 = new int[n + 1];
          System.arraycopy(paramArrayOfInt1, 0, arrayOfInt2, 0, n + 1);
          paramArrayOfInt1 = arrayOfInt2;
          paramArrayOfInt1[n] = ((int)l4);
          l4 = 0L;
          break;
        }
        l4 -= paramArrayOfInt1[n];
      }
    }
    else
    {
      long l3 = l1 - l2;
      int[] arrayOfInt1 = new int[paramArrayOfInt1.length + 1];
      System.arraycopy(paramArrayOfInt1, 0, arrayOfInt1, 0, paramArrayOfInt1.length);
      arrayOfInt1[paramArrayOfInt1.length] = ((int)l3);
      paramArrayOfInt1 = arrayOfInt1;
    }
    int[][] arrayOfInt = new int[paramArrayOfInt1.length][];
    int k = 0;
    int m = 1;
    int n = paramArrayOfInt[k].length;
    for (int i1 = 0; i1 < paramArrayOfInt1.length; i1++)
    {
      int i2 = paramArrayOfInt1[i1];
      int[] arrayOfInt3 = new int[1 + i2];
      long l5 = 0L;
      arrayOfInt[i1] = arrayOfInt3;
      int i3 = 1;
      while (i3 < arrayOfInt3.length)
      {
        int i4 = arrayOfInt3.length - i3;
        while (m == n)
        {
          m = 1;
          n = paramArrayOfInt[(++k)].length;
        }
        if (i4 > n - m) {
          i4 = n - m;
        }
        l5 += paramArrayOfInt[k][0] * i4;
        System.arraycopy(paramArrayOfInt[k], n - i4, arrayOfInt3, i3, i4);
        n -= i4;
        i3 += i4;
      }
      Arrays.sort(arrayOfInt3, 1, arrayOfInt3.length);
      arrayOfInt3[0] = ((int)((l5 + i2 / 2) / i2));
    }
    assert (m == n);
    assert (k == paramArrayOfInt.length - 1);
    return arrayOfInt;
  }
  
  public static Histogram makeByteHistogram(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['က'];
    int[] arrayOfInt = new int['Ā'];
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) > 0) {
      for (j = 0; j < i; j++) {
        arrayOfInt[(arrayOfByte[j] & 0xFF)] += 1;
      }
    }
    int[][] arrayOfInt1 = new int['Ā'][2];
    for (int j = 0; j < arrayOfInt.length; j++)
    {
      arrayOfInt1[j][0] = arrayOfInt[j];
      arrayOfInt1[j][1] = j;
    }
    return new Histogram(arrayOfInt1);
  }
  
  private static int[] sortedSlice(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramInt2 == paramArrayOfInt.length) && (isSorted(paramArrayOfInt, 0, false))) {
      return paramArrayOfInt;
    }
    int[] arrayOfInt = new int[paramInt2 - paramInt1];
    System.arraycopy(paramArrayOfInt, paramInt1, arrayOfInt, 0, arrayOfInt.length);
    Arrays.sort(arrayOfInt);
    return arrayOfInt;
  }
  
  private static boolean isSorted(int[] paramArrayOfInt, int paramInt, boolean paramBoolean)
  {
    for (int i = paramInt + 1; i < paramArrayOfInt.length; i++) {
      if (paramBoolean ? paramArrayOfInt[(i - 1)] >= paramArrayOfInt[i] : paramArrayOfInt[(i - 1)] > paramArrayOfInt[i]) {
        return false;
      }
    }
    return true;
  }
  
  private static int[] maybeSort(int[] paramArrayOfInt)
  {
    if (!isSorted(paramArrayOfInt, 0, false))
    {
      paramArrayOfInt = (int[])paramArrayOfInt.clone();
      Arrays.sort(paramArrayOfInt);
    }
    return paramArrayOfInt;
  }
  
  private boolean assertWellFormed(int[] paramArrayOfInt)
  {
    return true;
  }
  
  public static abstract interface BitMetric
  {
    public abstract double getBitLength(int paramInt);
  }
}
