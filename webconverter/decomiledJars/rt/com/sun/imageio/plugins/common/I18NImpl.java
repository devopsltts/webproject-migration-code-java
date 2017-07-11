package com.sun.imageio.plugins.common;

import java.io.InputStream;
import java.util.PropertyResourceBundle;

public class I18NImpl
{
  public I18NImpl() {}
  
  protected static final String getString(String paramString1, String paramString2, String paramString3)
  {
    PropertyResourceBundle localPropertyResourceBundle = null;
    try
    {
      InputStream localInputStream = Class.forName(paramString1).getResourceAsStream(paramString2);
      localPropertyResourceBundle = new PropertyResourceBundle(localInputStream);
    }
    catch (Throwable localThrowable)
    {
      throw new RuntimeException(localThrowable);
    }
    return (String)localPropertyResourceBundle.handleGetObject(paramString3);
  }
}
