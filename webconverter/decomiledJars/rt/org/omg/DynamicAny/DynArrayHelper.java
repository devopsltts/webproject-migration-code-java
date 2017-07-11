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

public abstract class DynArrayHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/DynArray:1.0";
  private static TypeCode __typeCode = null;
  
  public DynArrayHelper() {}
  
  public static void insert(Any paramAny, DynArray paramDynArray)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramDynArray);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static DynArray extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "DynArray");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static DynArray read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, DynArray paramDynArray)
  {
    throw new MARSHAL();
  }
  
  public static DynArray narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynArray)) {
      return (DynArray)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynArrayStub local_DynArrayStub = new _DynArrayStub();
    local_DynArrayStub._set_delegate(localDelegate);
    return local_DynArrayStub;
  }
  
  public static DynArray unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynArray)) {
      return (DynArray)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynArrayStub local_DynArrayStub = new _DynArrayStub();
    local_DynArrayStub._set_delegate(localDelegate);
    return local_DynArrayStub;
  }
}
