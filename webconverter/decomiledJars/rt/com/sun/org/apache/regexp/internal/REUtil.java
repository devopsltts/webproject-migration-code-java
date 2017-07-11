package com.sun.org.apache.regexp.internal;

public class REUtil
{
  private static final String complexPrefix = "complex:";
  
  public REUtil() {}
  
  public static RE createRE(String paramString, int paramInt)
    throws RESyntaxException
  {
    if (paramString.startsWith("complex:")) {
      return new RE(paramString.substring("complex:".length()), paramInt);
    }
    return new RE(RE.simplePatternToFullRegularExpression(paramString), paramInt);
  }
  
  public static RE createRE(String paramString)
    throws RESyntaxException
  {
    return createRE(paramString, 0);
  }
}
