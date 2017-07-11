package org.omg.CORBA;

@Deprecated
public abstract interface DynEnum
  extends Object, DynAny
{
  public abstract String value_as_string();
  
  public abstract void value_as_string(String paramString);
  
  public abstract int value_as_ulong();
  
  public abstract void value_as_ulong(int paramInt);
}
