package com.sun.org.apache.xerces.internal.impl.xs.util;

public final class XInt
{
  private int fValue;
  
  XInt(int paramInt)
  {
    this.fValue = paramInt;
  }
  
  public final int intValue()
  {
    return this.fValue;
  }
  
  public final short shortValue()
  {
    return (short)this.fValue;
  }
  
  public final boolean equals(XInt paramXInt)
  {
    return this.fValue == paramXInt.fValue;
  }
  
  public String toString()
  {
    return Integer.toString(this.fValue);
  }
}
