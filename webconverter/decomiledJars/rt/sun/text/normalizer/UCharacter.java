package sun.text.normalizer;

import java.io.IOException;
import java.util.MissingResourceException;

public final class UCharacter
{
  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 1114111;
  public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
  private static final UCharacterProperty PROPERTY_;
  private static final char[] PROPERTY_TRIE_INDEX_;
  private static final char[] PROPERTY_TRIE_DATA_;
  private static final int PROPERTY_INITIAL_VALUE_;
  private static final UBiDiProps gBdp;
  private static final int NUMERIC_TYPE_SHIFT_ = 5;
  private static final int NUMERIC_TYPE_MASK_ = 224;
  
  public UCharacter() {}
  
  public static int digit(int paramInt1, int paramInt2)
  {
    int i = getProperty(paramInt1);
    int j;
    if (getNumericType(i) == 1) {
      j = UCharacterProperty.getUnsignedValue(i);
    } else {
      j = getEuropeanDigit(paramInt1);
    }
    return (0 <= j) && (j < paramInt2) ? j : -1;
  }
  
  public static int getDirection(int paramInt)
  {
    return gBdp.getClass(paramInt);
  }
  
  public static int getCodePoint(char paramChar1, char paramChar2)
  {
    if ((UTF16.isLeadSurrogate(paramChar1)) && (UTF16.isTrailSurrogate(paramChar2))) {
      return UCharacterProperty.getRawSupplementary(paramChar1, paramChar2);
    }
    throw new IllegalArgumentException("Illegal surrogate characters");
  }
  
  public static VersionInfo getAge(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 1114111)) {
      throw new IllegalArgumentException("Codepoint out of bounds");
    }
    return PROPERTY_.getAge(paramInt);
  }
  
  private static int getEuropeanDigit(int paramInt)
  {
    if (((paramInt > 122) && (paramInt < 65313)) || (paramInt < 65) || ((paramInt > 90) && (paramInt < 97)) || (paramInt > 65370) || ((paramInt > 65338) && (paramInt < 65345))) {
      return -1;
    }
    if (paramInt <= 122) {
      return paramInt + 10 - (paramInt <= 90 ? 65 : 97);
    }
    if (paramInt <= 65338) {
      return paramInt + 10 - 65313;
    }
    return paramInt + 10 - 65345;
  }
  
  private static int getNumericType(int paramInt)
  {
    return (paramInt & 0xE0) >> 5;
  }
  
  private static final int getProperty(int paramInt)
  {
    if ((paramInt < 55296) || ((paramInt > 56319) && (paramInt < 65536))) {
      try
      {
        return PROPERTY_TRIE_DATA_[((PROPERTY_TRIE_INDEX_[(paramInt >> 5)] << '\002') + (paramInt & 0x1F))];
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        return PROPERTY_INITIAL_VALUE_;
      }
    }
    if (paramInt <= 56319) {
      return PROPERTY_TRIE_DATA_[((PROPERTY_TRIE_INDEX_[(320 + (paramInt >> 5))] << '\002') + (paramInt & 0x1F))];
    }
    if (paramInt <= 1114111) {
      return PROPERTY_.m_trie_.getSurrogateValue(UTF16.getLeadSurrogate(paramInt), (char)(paramInt & 0x3FF));
    }
    return PROPERTY_INITIAL_VALUE_;
  }
  
  static
  {
    try
    {
      PROPERTY_ = UCharacterProperty.getInstance();
      PROPERTY_TRIE_INDEX_ = PROPERTY_.m_trieIndex_;
      PROPERTY_TRIE_DATA_ = PROPERTY_.m_trieData_;
      PROPERTY_INITIAL_VALUE_ = PROPERTY_.m_trieInitialValue_;
    }
    catch (Exception localException)
    {
      throw new MissingResourceException(localException.getMessage(), "", "");
    }
    UBiDiProps localUBiDiProps;
    try
    {
      localUBiDiProps = UBiDiProps.getSingleton();
    }
    catch (IOException localIOException)
    {
      localUBiDiProps = UBiDiProps.getDummy();
    }
    gBdp = localUBiDiProps;
  }
  
  public static abstract interface NumericType
  {
    public static final int DECIMAL = 1;
  }
}
