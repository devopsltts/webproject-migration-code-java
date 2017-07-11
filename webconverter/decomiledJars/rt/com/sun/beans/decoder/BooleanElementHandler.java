package com.sun.beans.decoder;

final class BooleanElementHandler
  extends StringElementHandler
{
  BooleanElementHandler() {}
  
  public Object getValue(String paramString)
  {
    if (Boolean.TRUE.toString().equalsIgnoreCase(paramString)) {
      return Boolean.TRUE;
    }
    if (Boolean.FALSE.toString().equalsIgnoreCase(paramString)) {
      return Boolean.FALSE;
    }
    throw new IllegalArgumentException("Unsupported boolean argument: " + paramString);
  }
}
