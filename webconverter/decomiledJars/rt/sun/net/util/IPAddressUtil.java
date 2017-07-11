package sun.net.util;

public class IPAddressUtil
{
  private static final int INADDR4SZ = 4;
  private static final int INADDR16SZ = 16;
  private static final int INT16SZ = 2;
  
  public IPAddressUtil() {}
  
  public static byte[] textToNumericFormatV4(String paramString)
  {
    byte[] arrayOfByte = new byte[4];
    long l = 0L;
    int i = 0;
    int j = paramString.length();
    if ((j == 0) || (j > 15)) {
      return null;
    }
    for (int k = 0; k < j; k++)
    {
      char c = paramString.charAt(k);
      if (c == '.')
      {
        if ((l < 0L) || (l > 255L) || (i == 3)) {
          return null;
        }
        arrayOfByte[(i++)] = ((byte)(int)(l & 0xFF));
        l = 0L;
      }
      else
      {
        int m = Character.digit(c, 10);
        if (m < 0) {
          return null;
        }
        l *= 10L;
        l += m;
      }
    }
    if ((l < 0L) || (l >= 1L << (4 - i) * 8)) {
      return null;
    }
    switch (i)
    {
    case 0: 
      arrayOfByte[0] = ((byte)(int)(l >> 24 & 0xFF));
    case 1: 
      arrayOfByte[1] = ((byte)(int)(l >> 16 & 0xFF));
    case 2: 
      arrayOfByte[2] = ((byte)(int)(l >> 8 & 0xFF));
    case 3: 
      arrayOfByte[3] = ((byte)(int)(l >> 0 & 0xFF));
    }
    return arrayOfByte;
  }
  
  public static byte[] textToNumericFormatV6(String paramString)
  {
    if (paramString.length() < 2) {
      return null;
    }
    char[] arrayOfChar = paramString.toCharArray();
    byte[] arrayOfByte1 = new byte[16];
    int m = arrayOfChar.length;
    int n = paramString.indexOf("%");
    if (n == m - 1) {
      return null;
    }
    if (n != -1) {
      m = n;
    }
    int i = -1;
    int i1 = 0;
    int i2 = 0;
    if ((arrayOfChar[i1] == ':') && (arrayOfChar[(++i1)] != ':')) {
      return null;
    }
    int i3 = i1;
    int j = 0;
    int k = 0;
    int i4;
    while (i1 < m)
    {
      char c = arrayOfChar[(i1++)];
      i4 = Character.digit(c, 16);
      if (i4 != -1)
      {
        k <<= 4;
        k |= i4;
        if (k > 65535) {
          return null;
        }
        j = 1;
      }
      else if (c == ':')
      {
        i3 = i1;
        if (j == 0)
        {
          if (i != -1) {
            return null;
          }
          i = i2;
        }
        else
        {
          if (i1 == m) {
            return null;
          }
          if (i2 + 2 > 16) {
            return null;
          }
          arrayOfByte1[(i2++)] = ((byte)(k >> 8 & 0xFF));
          arrayOfByte1[(i2++)] = ((byte)(k & 0xFF));
          j = 0;
          k = 0;
        }
      }
      else if ((c == '.') && (i2 + 4 <= 16))
      {
        String str = paramString.substring(i3, m);
        int i5 = 0;
        for (int i6 = 0; (i6 = str.indexOf('.', i6)) != -1; i6++) {
          i5++;
        }
        if (i5 != 3) {
          return null;
        }
        byte[] arrayOfByte3 = textToNumericFormatV4(str);
        if (arrayOfByte3 == null) {
          return null;
        }
        for (int i7 = 0; i7 < 4; i7++) {
          arrayOfByte1[(i2++)] = arrayOfByte3[i7];
        }
        j = 0;
      }
      else
      {
        return null;
      }
    }
    if (j != 0)
    {
      if (i2 + 2 > 16) {
        return null;
      }
      arrayOfByte1[(i2++)] = ((byte)(k >> 8 & 0xFF));
      arrayOfByte1[(i2++)] = ((byte)(k & 0xFF));
    }
    if (i != -1)
    {
      i4 = i2 - i;
      if (i2 == 16) {
        return null;
      }
      for (i1 = 1; i1 <= i4; i1++)
      {
        arrayOfByte1[(16 - i1)] = arrayOfByte1[(i + i4 - i1)];
        arrayOfByte1[(i + i4 - i1)] = 0;
      }
      i2 = 16;
    }
    if (i2 != 16) {
      return null;
    }
    byte[] arrayOfByte2 = convertFromIPv4MappedAddress(arrayOfByte1);
    if (arrayOfByte2 != null) {
      return arrayOfByte2;
    }
    return arrayOfByte1;
  }
  
  public static boolean isIPv4LiteralAddress(String paramString)
  {
    return textToNumericFormatV4(paramString) != null;
  }
  
  public static boolean isIPv6LiteralAddress(String paramString)
  {
    return textToNumericFormatV6(paramString) != null;
  }
  
  public static byte[] convertFromIPv4MappedAddress(byte[] paramArrayOfByte)
  {
    if (isIPv4MappedAddress(paramArrayOfByte))
    {
      byte[] arrayOfByte = new byte[4];
      System.arraycopy(paramArrayOfByte, 12, arrayOfByte, 0, 4);
      return arrayOfByte;
    }
    return null;
  }
  
  private static boolean isIPv4MappedAddress(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length < 16) {
      return false;
    }
    return (paramArrayOfByte[0] == 0) && (paramArrayOfByte[1] == 0) && (paramArrayOfByte[2] == 0) && (paramArrayOfByte[3] == 0) && (paramArrayOfByte[4] == 0) && (paramArrayOfByte[5] == 0) && (paramArrayOfByte[6] == 0) && (paramArrayOfByte[7] == 0) && (paramArrayOfByte[8] == 0) && (paramArrayOfByte[9] == 0) && (paramArrayOfByte[10] == -1) && (paramArrayOfByte[11] == -1);
  }
}
