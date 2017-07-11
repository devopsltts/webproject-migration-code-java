package sun.text.normalizer;

public final class Utility
{
  private static final char[] UNESCAPE_MAP = { 'a', '\007', 'b', '\b', 'e', '\033', 'f', '\f', 'n', '\n', 'r', '\r', 't', '\t', 'v', '\013' };
  static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
  
  public Utility() {}
  
  public static final boolean arrayRegionMatches(char[] paramArrayOfChar1, int paramInt1, char[] paramArrayOfChar2, int paramInt2, int paramInt3)
  {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 - paramInt1;
    for (int k = paramInt1; k < i; k++) {
      if (paramArrayOfChar1[k] != paramArrayOfChar2[(k + j)]) {
        return false;
      }
    }
    return true;
  }
  
  public static final String escape(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while (i < paramString.length())
    {
      int j = UTF16.charAt(paramString, i);
      i += UTF16.getCharCount(j);
      if ((j >= 32) && (j <= 127))
      {
        if (j == 92) {
          localStringBuffer.append("\\\\");
        } else {
          localStringBuffer.append((char)j);
        }
      }
      else
      {
        int k = j <= 65535 ? 1 : 0;
        localStringBuffer.append(k != 0 ? "\\u" : "\\U");
        hex(j, k != 0 ? 4 : 8, localStringBuffer);
      }
    }
    return localStringBuffer.toString();
  }
  
  public static int unescapeAt(String paramString, int[] paramArrayOfInt)
  {
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 4;
    int i4 = 0;
    int i5 = paramArrayOfInt[0];
    int i6 = paramString.length();
    if ((i5 < 0) || (i5 >= i6)) {
      return -1;
    }
    int i = UTF16.charAt(paramString, i5);
    i5 += UTF16.getCharCount(i);
    int i2;
    switch (i)
    {
    case 117: 
      m = n = 4;
      break;
    case 85: 
      m = n = 8;
      break;
    case 120: 
      m = 1;
      if ((i5 < i6) && (UTF16.charAt(paramString, i5) == 123))
      {
        i5++;
        i4 = 1;
        n = 8;
      }
      else
      {
        n = 2;
      }
      break;
    default: 
      i2 = UCharacter.digit(i, 8);
      if (i2 >= 0)
      {
        m = 1;
        n = 3;
        k = 1;
        i1 = 3;
        j = i2;
      }
      break;
    }
    if (m != 0)
    {
      while ((i5 < i6) && (k < n))
      {
        i = UTF16.charAt(paramString, i5);
        i2 = UCharacter.digit(i, i1 == 3 ? 8 : 16);
        if (i2 < 0) {
          break;
        }
        j = j << i1 | i2;
        i5 += UTF16.getCharCount(i);
        k++;
      }
      if (k < m) {
        return -1;
      }
      if (i4 != 0)
      {
        if (i != 125) {
          return -1;
        }
        i5++;
      }
      if ((j < 0) || (j >= 1114112)) {
        return -1;
      }
      if ((i5 < i6) && (UTF16.isLeadSurrogate((char)j)))
      {
        int i7 = i5 + 1;
        i = paramString.charAt(i5);
        if ((i == 92) && (i7 < i6))
        {
          int[] arrayOfInt = { i7 };
          i = unescapeAt(paramString, arrayOfInt);
          i7 = arrayOfInt[0];
        }
        if (UTF16.isTrailSurrogate((char)i))
        {
          i5 = i7;
          j = UCharacterProperty.getRawSupplementary((char)j, (char)i);
        }
      }
      paramArrayOfInt[0] = i5;
      return j;
    }
    for (int i3 = 0; i3 < UNESCAPE_MAP.length; i3 += 2)
    {
      if (i == UNESCAPE_MAP[i3])
      {
        paramArrayOfInt[0] = i5;
        return UNESCAPE_MAP[(i3 + 1)];
      }
      if (i < UNESCAPE_MAP[i3]) {
        break;
      }
    }
    if ((i == 99) && (i5 < i6))
    {
      i = UTF16.charAt(paramString, i5);
      paramArrayOfInt[0] = (i5 + UTF16.getCharCount(i));
      return 0x1F & i;
    }
    paramArrayOfInt[0] = i5;
    return i;
  }
  
  public static StringBuffer hex(int paramInt1, int paramInt2, StringBuffer paramStringBuffer)
  {
    return appendNumber(paramStringBuffer, paramInt1, 16, paramInt2);
  }
  
  public static String hex(int paramInt1, int paramInt2)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    return appendNumber(localStringBuffer, paramInt1, 16, paramInt2).toString();
  }
  
  public static int skipWhitespace(String paramString, int paramInt)
  {
    while (paramInt < paramString.length())
    {
      int i = UTF16.charAt(paramString, paramInt);
      if (!UCharacterProperty.isRuleWhiteSpace(i)) {
        break;
      }
      paramInt += UTF16.getCharCount(i);
    }
    return paramInt;
  }
  
  private static void recursiveAppendNumber(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1 % paramInt2;
    if ((paramInt1 >= paramInt2) || (paramInt3 > 1)) {
      recursiveAppendNumber(paramStringBuffer, paramInt1 / paramInt2, paramInt2, paramInt3 - 1);
    }
    paramStringBuffer.append(DIGITS[i]);
  }
  
  public static StringBuffer appendNumber(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, int paramInt3)
    throws IllegalArgumentException
  {
    if ((paramInt2 < 2) || (paramInt2 > 36)) {
      throw new IllegalArgumentException("Illegal radix " + paramInt2);
    }
    int i = paramInt1;
    if (paramInt1 < 0)
    {
      i = -paramInt1;
      paramStringBuffer.append("-");
    }
    recursiveAppendNumber(paramStringBuffer, i, paramInt2, paramInt3);
    return paramStringBuffer;
  }
  
  public static boolean isUnprintable(int paramInt)
  {
    return (paramInt < 32) || (paramInt > 126);
  }
  
  public static boolean escapeUnprintable(StringBuffer paramStringBuffer, int paramInt)
  {
    if (isUnprintable(paramInt))
    {
      paramStringBuffer.append('\\');
      if ((paramInt & 0xFFFF0000) != 0)
      {
        paramStringBuffer.append('U');
        paramStringBuffer.append(DIGITS[(0xF & paramInt >> 28)]);
        paramStringBuffer.append(DIGITS[(0xF & paramInt >> 24)]);
        paramStringBuffer.append(DIGITS[(0xF & paramInt >> 20)]);
        paramStringBuffer.append(DIGITS[(0xF & paramInt >> 16)]);
      }
      else
      {
        paramStringBuffer.append('u');
      }
      paramStringBuffer.append(DIGITS[(0xF & paramInt >> 12)]);
      paramStringBuffer.append(DIGITS[(0xF & paramInt >> 8)]);
      paramStringBuffer.append(DIGITS[(0xF & paramInt >> 4)]);
      paramStringBuffer.append(DIGITS[(0xF & paramInt)]);
      return true;
    }
    return false;
  }
  
  public static void getChars(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    if (paramInt1 == paramInt2) {
      return;
    }
    paramStringBuffer.getChars(paramInt1, paramInt2, paramArrayOfChar, paramInt3);
  }
}
