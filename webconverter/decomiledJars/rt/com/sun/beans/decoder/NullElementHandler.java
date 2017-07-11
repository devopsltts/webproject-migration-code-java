package com.sun.beans.decoder;

class NullElementHandler
  extends ElementHandler
  implements ValueObject
{
  NullElementHandler() {}
  
  protected final ValueObject getValueObject()
  {
    return this;
  }
  
  public Object getValue()
  {
    return null;
  }
  
  public final boolean isVoid()
  {
    return false;
  }
}
