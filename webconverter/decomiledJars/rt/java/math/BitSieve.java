package java.math;

import java.util.Random;

class BitSieve
{
  private long[] bits;
  private int length;
  private static BitSieve smallSieve = new BitSieve();
  
  private BitSieve()
  {
    this.length = 9600;
    this.bits = new long[unitIndex(this.length - 1) + 1];
    set(0);
    int i = 1;
    int j = 3;
    do
    {
      sieveSingle(this.length, i + j, j);
      i = sieveSearch(this.length, i + 1);
      j = 2 * i + 1;
    } while ((i > 0) && (j < this.length));
  }
  
  BitSieve(BigInteger paramBigInteger, int paramInt)
  {
    this.bits = new long[unitIndex(paramInt - 1) + 1];
    this.length = paramInt;
    int i = 0;
    int j = smallSieve.sieveSearch(smallSieve.length, i);
    int k = j * 2 + 1;
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(paramBigInteger);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger();
    do
    {
      i = localMutableBigInteger1.divideOneWord(k, localMutableBigInteger2);
      i = k - i;
      if (i % 2 == 0) {
        i += k;
      }
      sieveSingle(paramInt, (i - 1) / 2, k);
      j = smallSieve.sieveSearch(smallSieve.length, j + 1);
      k = j * 2 + 1;
    } while (j > 0);
  }
  
  private static int unitIndex(int paramInt)
  {
    return paramInt >>> 6;
  }
  
  private static long bit(int paramInt)
  {
    return 1L << (paramInt & 0x3F);
  }
  
  private boolean get(int paramInt)
  {
    int i = unitIndex(paramInt);
    return (this.bits[i] & bit(paramInt)) != 0L;
  }
  
  private void set(int paramInt)
  {
    int i = unitIndex(paramInt);
    this.bits[i] |= bit(paramInt);
  }
  
  private int sieveSearch(int paramInt1, int paramInt2)
  {
    if (paramInt2 >= paramInt1) {
      return -1;
    }
    int i = paramInt2;
    do
    {
      if (!get(i)) {
        return i;
      }
      i++;
    } while (i < paramInt1 - 1);
    return -1;
  }
  
  private void sieveSingle(int paramInt1, int paramInt2, int paramInt3)
  {
    while (paramInt2 < paramInt1)
    {
      set(paramInt2);
      paramInt2 += paramInt3;
    }
  }
  
  BigInteger retrieve(BigInteger paramBigInteger, int paramInt, Random paramRandom)
  {
    int i = 1;
    for (int j = 0; j < this.bits.length; j++)
    {
      long l = this.bits[j] ^ 0xFFFFFFFFFFFFFFFF;
      for (int k = 0; k < 64; k++)
      {
        if ((l & 1L) == 1L)
        {
          BigInteger localBigInteger = paramBigInteger.add(BigInteger.valueOf(i));
          if (localBigInteger.primeToCertainty(paramInt, paramRandom)) {
            return localBigInteger;
          }
        }
        l >>>= 1;
        i += 2;
      }
    }
    return null;
  }
}
