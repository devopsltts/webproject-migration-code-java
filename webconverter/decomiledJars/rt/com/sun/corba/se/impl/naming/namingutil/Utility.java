package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.io.StringWriter;
import org.omg.CORBA.DATA_CONVERSION;

class Utility
{
  private static NamingSystemException wrapper = NamingSystemException.get("naming");
  
  Utility() {}
  
  static String cleanEscapes(String paramString)
  {
    StringWriter localStringWriter = new StringWriter();
    for (int i = 0; i < paramString.length(); i++)
    {
      int j = paramString.charAt(i);
      if (j != 37)
      {
        localStringWriter.write(j);
      }
      else
      {
        i++;
        int k = hexOf(paramString.charAt(i));
        i++;
        int m = hexOf(paramString.charAt(i));
        int n = k * 16 + m;
        localStringWriter.write((char)n);
      }
    }
    return localStringWriter.toString();
  }
  
  static int hexOf(char paramChar)
  {
    int i = paramChar - '0';
    if ((i >= 0) && (i <= 9)) {
      return i;
    }
    i = paramChar - 'a' + 10;
    if ((i >= 10) && (i <= 15)) {
      return i;
    }
    i = paramChar - 'A' + 10;
    if ((i >= 10) && (i <= 15)) {
      return i;
    }
    throw new DATA_CONVERSION();
  }
  
  static void validateGIOPVersion(IIOPEndpointInfo paramIIOPEndpointInfo)
  {
    if ((paramIIOPEndpointInfo.getMajor() > 1) || (paramIIOPEndpointInfo.getMinor() > 2)) {
      throw wrapper.insBadAddress();
    }
  }
}
