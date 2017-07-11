package java.lang;

class CharacterData0E
  extends CharacterData
{
  static final CharacterData instance;
  static final char[] X;
  static final char[] Y;
  static final int[] A;
  static final String A_DATA = "砀\000䠀တ砀\000砀\000䠀တ䠀တ䀀〆䀀〆";
  static final char[] B;
  
  int getProperties(int paramInt)
  {
    int i = (char)paramInt;
    int j = A[(Y[(X[(i >> 5)] | i >> 1 & 0xF)] | i & 0x1)];
    return j;
  }
  
  int getPropertiesEx(int paramInt)
  {
    int i = (char)paramInt;
    int j = B[(Y[(X[(i >> 5)] | i >> 1 & 0xF)] | i & 0x1)];
    return j;
  }
  
  boolean isOtherLowercase(int paramInt)
  {
    int i = getPropertiesEx(paramInt);
    return (i & 0x1) != 0;
  }
  
  boolean isOtherUppercase(int paramInt)
  {
    int i = getPropertiesEx(paramInt);
    return (i & 0x2) != 0;
  }
  
  boolean isOtherAlphabetic(int paramInt)
  {
    int i = getPropertiesEx(paramInt);
    return (i & 0x4) != 0;
  }
  
  boolean isIdeographic(int paramInt)
  {
    int i = getPropertiesEx(paramInt);
    return (i & 0x10) != 0;
  }
  
  int getType(int paramInt)
  {
    int i = getProperties(paramInt);
    return i & 0x1F;
  }
  
  boolean isJavaIdentifierStart(int paramInt)
  {
    int i = getProperties(paramInt);
    return (i & 0x7000) >= 20480;
  }
  
  boolean isJavaIdentifierPart(int paramInt)
  {
    int i = getProperties(paramInt);
    return (i & 0x3000) != 0;
  }
  
  boolean isUnicodeIdentifierStart(int paramInt)
  {
    int i = getProperties(paramInt);
    return (i & 0x7000) == 28672;
  }
  
  boolean isUnicodeIdentifierPart(int paramInt)
  {
    int i = getProperties(paramInt);
    return (i & 0x1000) != 0;
  }
  
  boolean isIdentifierIgnorable(int paramInt)
  {
    int i = getProperties(paramInt);
    return (i & 0x7000) == 4096;
  }
  
  int toLowerCase(int paramInt)
  {
    int i = paramInt;
    int j = getProperties(paramInt);
    if ((j & 0x20000) != 0)
    {
      int k = j << 5 >> 23;
      i = paramInt + k;
    }
    return i;
  }
  
  int toUpperCase(int paramInt)
  {
    int i = paramInt;
    int j = getProperties(paramInt);
    if ((j & 0x10000) != 0)
    {
      int k = j << 5 >> 23;
      i = paramInt - k;
    }
    return i;
  }
  
  int toTitleCase(int paramInt)
  {
    int i = paramInt;
    int j = getProperties(paramInt);
    if ((j & 0x8000) != 0)
    {
      if ((j & 0x10000) == 0) {
        i = paramInt + 1;
      } else if ((j & 0x20000) == 0) {
        i = paramInt - 1;
      }
    }
    else if ((j & 0x10000) != 0) {
      i = toUpperCase(paramInt);
    }
    return i;
  }
  
  int digit(int paramInt1, int paramInt2)
  {
    int i = -1;
    if ((paramInt2 >= 2) && (paramInt2 <= 36))
    {
      int j = getProperties(paramInt1);
      int k = j & 0x1F;
      if (k == 9) {
        i = paramInt1 + ((j & 0x3E0) >> 5) & 0x1F;
      } else if ((j & 0xC00) == 3072) {
        i = (paramInt1 + ((j & 0x3E0) >> 5) & 0x1F) + 10;
      }
    }
    return i < paramInt2 ? i : -1;
  }
  
  int getNumericValue(int paramInt)
  {
    int i = getProperties(paramInt);
    int j = -1;
    switch (i & 0xC00)
    {
    case 0: 
    default: 
      j = -1;
      break;
    case 1024: 
      j = paramInt + ((i & 0x3E0) >> 5) & 0x1F;
      break;
    case 2048: 
      j = -2;
      break;
    case 3072: 
      j = (paramInt + ((i & 0x3E0) >> 5) & 0x1F) + 10;
    }
    return j;
  }
  
  boolean isWhitespace(int paramInt)
  {
    int i = getProperties(paramInt);
    return (i & 0x7000) == 16384;
  }
  
  byte getDirectionality(int paramInt)
  {
    int i = getProperties(paramInt);
    byte b = (byte)((i & 0x78000000) >> 27);
    if (b == 15) {
      b = -1;
    }
    return b;
  }
  
  boolean isMirrored(int paramInt)
  {
    int i = getProperties(paramInt);
    return (i & 0x80000000) != 0;
  }
  
  private CharacterData0E() {}
  
  static
  {
    instance = new CharacterData0E();
    X = "\000\020\020\020    0000000@                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ".toCharArray();
    Y = "\000\002\002\002\002\002\002\002\002\002\002\002\002\002\002\002\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\002\002\002\002\002\002\002\002\002\002\002\002\002\002\002\002\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\006\002\002\002\002\002\002\002\002".toCharArray();
    A = new int[8];
    B = "\000\000\000\000\000\000\000\000".toCharArray();
    char[] arrayOfChar = "砀\000䠀တ砀\000砀\000䠀တ䠀တ䀀〆䀀〆".toCharArray();
    assert (arrayOfChar.length == 16);
    int i = 0;
    int j = 0;
    while (i < 16)
    {
      int k = arrayOfChar[(i++)] << '\020';
      A[(j++)] = (k | arrayOfChar[(i++)]);
    }
  }
}
