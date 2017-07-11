package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

public abstract interface DynEnumOperations
  extends DynAnyOperations
{
  public abstract String get_as_string();
  
  public abstract void set_as_string(String paramString)
    throws InvalidValue;
  
  public abstract int get_as_ulong();
  
  public abstract void set_as_ulong(int paramInt)
    throws InvalidValue;
}
