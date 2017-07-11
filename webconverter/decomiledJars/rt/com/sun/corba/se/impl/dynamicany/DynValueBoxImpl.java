package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynValueBox;

public class DynValueBoxImpl
  extends DynValueCommonImpl
  implements DynValueBox
{
  private DynValueBoxImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynValueBoxImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynValueBoxImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
  }
  
  public Any get_boxed_value()
    throws InvalidValue
  {
    if (this.isNull) {
      throw new InvalidValue();
    }
    checkInitAny();
    return this.any;
  }
  
  public void set_boxed_value(Any paramAny)
    throws TypeMismatch
  {
    if ((!this.isNull) && (!paramAny.type().equal(type()))) {
      throw new TypeMismatch();
    }
    clearData();
    this.any = paramAny;
    this.representations = 2;
    this.index = 0;
    this.isNull = false;
  }
  
  public DynAny get_boxed_value_as_dyn_any()
    throws InvalidValue
  {
    if (this.isNull) {
      throw new InvalidValue();
    }
    checkInitComponents();
    return this.components[0];
  }
  
  public void set_boxed_value_as_dyn_any(DynAny paramDynAny)
    throws TypeMismatch
  {
    if ((!this.isNull) && (!paramDynAny.type().equal(type()))) {
      throw new TypeMismatch();
    }
    clearData();
    this.components = new DynAny[] { paramDynAny };
    this.representations = 4;
    this.index = 0;
    this.isNull = false;
  }
  
  protected boolean initializeComponentsFromAny()
  {
    try
    {
      this.components = new DynAny[] { DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, false) };
    }
    catch (InconsistentTypeCode localInconsistentTypeCode)
    {
      return false;
    }
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode()
  {
    try
    {
      this.any = DynAnyUtil.createDefaultAnyOfType(this.any.type(), this.orb);
      this.components = new DynAny[] { DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, false) };
    }
    catch (InconsistentTypeCode localInconsistentTypeCode)
    {
      return false;
    }
    return true;
  }
  
  protected boolean initializeAnyFromComponents()
  {
    this.any = getAny(this.components[0]);
    return true;
  }
}
