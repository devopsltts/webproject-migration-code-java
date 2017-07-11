package org.omg.DynamicAny.DynAnyPackage;

import org.omg.CORBA.UserException;

public final class TypeMismatch
  extends UserException
{
  public TypeMismatch()
  {
    super(TypeMismatchHelper.id());
  }
  
  public TypeMismatch(String paramString)
  {
    super(TypeMismatchHelper.id() + "  " + paramString);
  }
}
