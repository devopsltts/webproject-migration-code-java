package com.sun.org.apache.xerces.internal.impl.dv.util;

public final class HexBin
{
  private static final int BASELENGTH = 128;
  private static final int LOOKUPLENGTH = 16;
  private static final byte[] hexNumberTable = new byte[''];
  private static final char[] lookUpHexAlphabet = new char[16];
  
  public HexBin() {}
  
  public static String encode(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    int i = paramArrayOfByte.length;
    int j = i * 2;
    char[] arrayOfChar = new char[j];
    for (int m = 0; m < i; m++)
    {
      int k = paramArrayOfByte[m];
      if (k < 0) {
        k += 256;
      }
      arrayOfChar[(m * 2)] = lookUpHexAlphabet[(k >> 4)];
      arrayOfChar[(m * 2 + 1)] = lookUpHexAlphabet[(k & 0xF)];
    }
    return new String(arrayOfChar);
  }
  
  public static byte[] decode(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.length();
    if (i % 2 != 0) {
      return null;
    }
    char[] arrayOfChar = paramString.toCharArray();
    int j = i / 2;
    byte[] arrayOfByte = new byte[j];
    for (int i1 = 0; i1 < j; i1++)
    {
      int n = arrayOfChar[(i1 * 2)];
      int k = n < 128 ? hexNumberTable[n] : -1;
      if (k == -1) {
        return null;
      }
      n = arrayOfChar[(i1 * 2 + 1)];
      int m = n < 128 ? hexNumberTable[n] : -1;
      if (m == -1) {
        return null;
      }
      arrayOfByte[i1] = ((byte)(k << 4 | m));
    }
    return arrayOfByte;
  }
  
  static
  {
    for (int i = 0; i < 128; i++) {
      hexNumberTable[i] = -1;
    }
    for (i = 57; i >= 48; i--) {
      hexNumberTable[i] = ((byte)(i - 48));
    }
    for (i = 70; i >= 65; i--) {
      hexNumberTable[i] = ((byte)(i - 65 + 10));
    }
    for (i = 102; i >= 97; i--) {
      hexNumberTable[i] = ((byte)(i - 97 + 10));
    }
    for (i = 0; i < 10; i++) {
      lookUpHexAlphabet[i] = ((char)(48 + i));
    }
    for (i = 10; i <= 15; i++) {
      lookUpHexAlphabet[i] = ((char)(65 + i - 10));
    }
  }
}
