package com.sun.beans.decoder;

final class FloatElementHandler
  extends StringElementHandler
{
  FloatElementHandler() {}
  
  public Object getValue(String paramString)
  {
    return Float.valueOf(paramString);
  }
}
