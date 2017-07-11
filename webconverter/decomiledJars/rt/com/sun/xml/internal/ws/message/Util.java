package com.sun.xml.internal.ws.message;

public abstract class Util
{
  public Util() {}
  
  public static boolean parseBool(String paramString)
  {
    if (paramString.length() == 0) {
      return false;
    }
    int i = paramString.charAt(0);
    return (i == 116) || (i == 49);
  }
}
