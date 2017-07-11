package com.sun.beans.decoder;

final class ClassElementHandler
  extends StringElementHandler
{
  ClassElementHandler() {}
  
  public Object getValue(String paramString)
  {
    return getOwner().findClass(paramString);
  }
}
