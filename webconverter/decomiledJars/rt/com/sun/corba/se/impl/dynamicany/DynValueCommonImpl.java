package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynValueCommon;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

abstract class DynValueCommonImpl
  extends DynAnyComplexImpl
  implements DynValueCommon
{
  protected boolean isNull;
  
  private DynValueCommonImpl()
  {
    this(null, (Any)null, false);
    this.isNull = true;
  }
  
  protected DynValueCommonImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
    this.isNull = checkInitComponents();
  }
  
  protected DynValueCommonImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
    this.isNull = true;
  }
  
  public boolean is_null()
  {
    return this.isNull;
  }
  
  public void set_to_null()
  {
    this.isNull = true;
    clearData();
  }
  
  public void set_to_value()
  {
    if (this.isNull) {
      this.isNull = false;
    }
  }
  
  public NameValuePair[] get_members()
    throws InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.isNull) {
      throw new InvalidValue();
    }
    checkInitComponents();
    return this.nameValuePairs;
  }
  
  public NameDynAnyPair[] get_members_as_dyn_any()
    throws InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.isNull) {
      throw new InvalidValue();
    }
    checkInitComponents();
    return this.nameDynAnyPairs;
  }
  
  public void set_members(NameValuePair[] paramArrayOfNameValuePair)
    throws TypeMismatch, InvalidValue
  {
    super.set_members(paramArrayOfNameValuePair);
    this.isNull = false;
  }
  
  public void set_members_as_dyn_any(NameDynAnyPair[] paramArrayOfNameDynAnyPair)
    throws TypeMismatch, InvalidValue
  {
    super.set_members_as_dyn_any(paramArrayOfNameDynAnyPair);
    this.isNull = false;
  }
}
