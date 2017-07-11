package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidValue;

@Deprecated
public abstract interface DynFixed
  extends Object, DynAny
{
  public abstract byte[] get_value();
  
  public abstract void set_value(byte[] paramArrayOfByte)
    throws InvalidValue;
}
