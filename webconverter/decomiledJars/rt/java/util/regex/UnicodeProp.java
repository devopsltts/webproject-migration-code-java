package java.util.regex;

import java.util.HashMap;
import java.util.Locale;

 enum UnicodeProp
{
  ALPHABETIC,  LETTER,  IDEOGRAPHIC,  LOWERCASE,  UPPERCASE,  TITLECASE,  WHITE_SPACE,  CONTROL,  PUNCTUATION,  HEX_DIGIT,  ASSIGNED,  NONCHARACTER_CODE_POINT,  DIGIT,  ALNUM,  BLANK,  GRAPH,  PRINT,  WORD,  JOIN_CONTROL;
  
  private static final HashMap<String, String> posix;
  private static final HashMap<String, String> aliases;
  
  private UnicodeProp() {}
  
  public static UnicodeProp forName(String paramString)
  {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    String str = (String)aliases.get(paramString);
    if (str != null) {
      paramString = str;
    }
    try
    {
      return valueOf(paramString);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return null;
  }
  
  public static UnicodeProp forPOSIXName(String paramString)
  {
    paramString = (String)posix.get(paramString.toUpperCase(Locale.ENGLISH));
    if (paramString == null) {
      return null;
    }
    return valueOf(paramString);
  }
  
  public abstract boolean is(int paramInt);
  
  static
  {
    posix = new HashMap();
    aliases = new HashMap();
    posix.put("ALPHA", "ALPHABETIC");
    posix.put("LOWER", "LOWERCASE");
    posix.put("UPPER", "UPPERCASE");
    posix.put("SPACE", "WHITE_SPACE");
    posix.put("PUNCT", "PUNCTUATION");
    posix.put("XDIGIT", "HEX_DIGIT");
    posix.put("ALNUM", "ALNUM");
    posix.put("CNTRL", "CONTROL");
    posix.put("DIGIT", "DIGIT");
    posix.put("BLANK", "BLANK");
    posix.put("GRAPH", "GRAPH");
    posix.put("PRINT", "PRINT");
    aliases.put("WHITESPACE", "WHITE_SPACE");
    aliases.put("HEXDIGIT", "HEX_DIGIT");
    aliases.put("NONCHARACTERCODEPOINT", "NONCHARACTER_CODE_POINT");
    aliases.put("JOINCONTROL", "JOIN_CONTROL");
  }
}
