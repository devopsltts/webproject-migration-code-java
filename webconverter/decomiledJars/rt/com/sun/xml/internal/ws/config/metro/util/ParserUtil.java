package com.sun.xml.internal.ws.config.metro.util;

import com.sun.istack.internal.logging.Logger;
import javax.xml.ws.WebServiceException;

public class ParserUtil
{
  private static final Logger LOGGER = Logger.getLogger(ParserUtil.class);
  
  private ParserUtil() {}
  
  public static boolean parseBooleanValue(String paramString)
    throws WebServiceException
  {
    if (("true".equals(paramString)) || ("1".equals(paramString))) {
      return true;
    }
    if (("false".equals(paramString)) || ("0".equals(paramString))) {
      return false;
    }
    throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException("invalid boolean value")));
  }
}
