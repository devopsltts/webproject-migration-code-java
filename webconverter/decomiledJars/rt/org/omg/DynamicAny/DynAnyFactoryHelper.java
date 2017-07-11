package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class DynAnyFactoryHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/DynAnyFactory:1.0";
  private static TypeCode __typeCode = null;
  
  public DynAnyFactoryHelper() {}
  
  public static void insert(Any paramAny, DynAnyFactory paramDynAnyFactory)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramDynAnyFactory);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static DynAnyFactory extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "DynAnyFactory");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static DynAnyFactory read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, DynAnyFactory paramDynAnyFactory)
  {
    throw new MARSHAL();
  }
  
  public static DynAnyFactory narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynAnyFactory)) {
      return (DynAnyFactory)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynAnyFactoryStub local_DynAnyFactoryStub = new _DynAnyFactoryStub();
    local_DynAnyFactoryStub._set_delegate(localDelegate);
    return local_DynAnyFactoryStub;
  }
  
  public static DynAnyFactory unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynAnyFactory)) {
      return (DynAnyFactory)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynAnyFactoryStub local_DynAnyFactoryStub = new _DynAnyFactoryStub();
    local_DynAnyFactoryStub._set_delegate(localDelegate);
    return local_DynAnyFactoryStub;
  }
}
