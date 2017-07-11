package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public abstract interface DynAnyFactoryOperations
{
  public abstract DynAny create_dyn_any(Any paramAny)
    throws InconsistentTypeCode;
  
  public abstract DynAny create_dyn_any_from_type_code(TypeCode paramTypeCode)
    throws InconsistentTypeCode;
}
