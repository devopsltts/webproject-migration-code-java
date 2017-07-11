package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public class DynAnyFactoryImpl
  extends LocalObject
  implements DynAnyFactory
{
  private ORB orb;
  private String[] __ids = { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };
  
  private DynAnyFactoryImpl()
  {
    this.orb = null;
  }
  
  public DynAnyFactoryImpl(ORB paramORB)
  {
    this.orb = paramORB;
  }
  
  public DynAny create_dyn_any(Any paramAny)
    throws InconsistentTypeCode
  {
    return DynAnyUtil.createMostDerivedDynAny(paramAny, this.orb, true);
  }
  
  public DynAny create_dyn_any_from_type_code(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    return DynAnyUtil.createMostDerivedDynAny(paramTypeCode, this.orb);
  }
  
  public String[] _ids()
  {
    return (String[])this.__ids.clone();
  }
}
