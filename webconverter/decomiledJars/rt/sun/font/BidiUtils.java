package sun.font;

import java.text.Bidi;

public final class BidiUtils
{
  static final char NUMLEVELS = '>';
  
  public BidiUtils() {}
  
  public static void getLevels(Bidi paramBidi, byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramInt + paramBidi.getLength();
    if ((paramInt < 0) || (i > paramArrayOfByte.length)) {
      throw new IndexOutOfBoundsException("levels.length = " + paramArrayOfByte.length + " start: " + paramInt + " limit: " + i);
    }
    int j = paramBidi.getRunCount();
    int k = paramInt;
    for (int m = 0; m < j; m++)
    {
      int n = paramInt + paramBidi.getRunLimit(m);
      int i1 = (byte)paramBidi.getRunLevel(m);
      while (k < n) {
        paramArrayOfByte[(k++)] = i1;
      }
    }
  }
  
  public static byte[] getLevels(Bidi paramBidi)
  {
    byte[] arrayOfByte = new byte[paramBidi.getLength()];
    getLevels(paramBidi, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static int[] createVisualToLogicalMap(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    int[] arrayOfInt = new int[i];
    int j = 63;
    int k = 0;
    int n;
    for (int m = 0; m < i; m++)
    {
      arrayOfInt[m] = m;
      n = paramArrayOfByte[m];
      if (n > k) {
        k = n;
      }
      if (((n & 0x1) != 0) && (n < j)) {
        j = n;
      }
    }
    while (k >= j)
    {
      m = 0;
      for (;;)
      {
        if ((m < i) && (paramArrayOfByte[m] < k))
        {
          m++;
        }
        else
        {
          n = m++;
          if (n == paramArrayOfByte.length) {
            break;
          }
          while ((m < i) && (paramArrayOfByte[m] >= k)) {
            m++;
          }
          for (int i1 = m - 1; n < i1; i1--)
          {
            int i2 = arrayOfInt[n];
            arrayOfInt[n] = arrayOfInt[i1];
            arrayOfInt[i1] = i2;
            n++;
          }
        }
      }
      k = (byte)(k - 1);
    }
    return arrayOfInt;
  }
  
  public static int[] createInverseMap(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      arrayOfInt[paramArrayOfInt[i]] = i;
    }
    return arrayOfInt;
  }
  
  public static int[] createContiguousOrder(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt != null) {
      return computeContiguousOrder(paramArrayOfInt, 0, paramArrayOfInt.length);
    }
    return null;
  }
  
  private static int[] computeContiguousOrder(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = new int[paramInt2 - paramInt1];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = (i + paramInt1);
    }
    for (i = 0; i < arrayOfInt.length - 1; i++)
    {
      int j = i;
      int k = paramArrayOfInt[arrayOfInt[j]];
      for (int m = i; m < arrayOfInt.length; m++) {
        if (paramArrayOfInt[arrayOfInt[m]] < k)
        {
          j = m;
          k = paramArrayOfInt[arrayOfInt[j]];
        }
      }
      m = arrayOfInt[i];
      arrayOfInt[i] = arrayOfInt[j];
      arrayOfInt[j] = m;
    }
    if (paramInt1 != 0) {
      for (i = 0; i < arrayOfInt.length; i++) {
        arrayOfInt[i] -= paramInt1;
      }
    }
    for (i = 0; (i < arrayOfInt.length) && (arrayOfInt[i] == i); i++) {}
    if (i == arrayOfInt.length) {
      return null;
    }
    return createInverseMap(arrayOfInt);
  }
  
  public static int[] createNormalizedMap(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfInt != null)
    {
      if ((paramInt1 != 0) || (paramInt2 != paramArrayOfInt.length))
      {
        int k;
        int i;
        int j;
        if (paramArrayOfByte == null)
        {
          k = 0;
          i = 1;
          j = 1;
        }
        else if (paramArrayOfByte[paramInt1] == paramArrayOfByte[(paramInt2 - 1)])
        {
          k = paramArrayOfByte[paramInt1];
          j = (k & 0x1) == 0 ? 1 : 0;
          for (int m = paramInt1; (m < paramInt2) && (paramArrayOfByte[m] >= k); m++) {
            if (j != 0) {
              j = paramArrayOfByte[m] == k ? 1 : 0;
            }
          }
          i = m == paramInt2 ? 1 : 0;
        }
        else
        {
          i = 0;
          k = 0;
          j = 0;
        }
        if (i != 0)
        {
          if (j != 0) {
            return null;
          }
          int[] arrayOfInt = new int[paramInt2 - paramInt1];
          int n;
          if ((k & 0x1) != 0) {
            n = paramArrayOfInt[(paramInt2 - 1)];
          } else {
            n = paramArrayOfInt[paramInt1];
          }
          if (n == 0) {
            System.arraycopy(paramArrayOfInt, paramInt1, arrayOfInt, 0, paramInt2 - paramInt1);
          } else {
            for (int i1 = 0; i1 < arrayOfInt.length; i1++) {
              arrayOfInt[i1] = (paramArrayOfInt[(i1 + paramInt1)] - n);
            }
          }
          return arrayOfInt;
        }
        return computeContiguousOrder(paramArrayOfInt, paramInt1, paramInt2);
      }
      return paramArrayOfInt;
    }
    return null;
  }
  
  public static void reorderVisually(byte[] paramArrayOfByte, Object[] paramArrayOfObject)
  {
    int i = paramArrayOfByte.length;
    int j = 63;
    int k = 0;
    int n;
    for (int m = 0; m < i; m++)
    {
      n = paramArrayOfByte[m];
      if (n > k) {
        k = n;
      }
      if (((n & 0x1) != 0) && (n < j)) {
        j = n;
      }
    }
    while (k >= j)
    {
      m = 0;
      for (;;)
      {
        if ((m < i) && (paramArrayOfByte[m] < k))
        {
          m++;
        }
        else
        {
          n = m++;
          if (n == paramArrayOfByte.length) {
            break;
          }
          while ((m < i) && (paramArrayOfByte[m] >= k)) {
            m++;
          }
          for (int i1 = m - 1; n < i1; i1--)
          {
            Object localObject = paramArrayOfObject[n];
            paramArrayOfObject[n] = paramArrayOfObject[i1];
            paramArrayOfObject[i1] = localObject;
            n++;
          }
        }
      }
      k = (byte)(k - 1);
    }
  }
}
