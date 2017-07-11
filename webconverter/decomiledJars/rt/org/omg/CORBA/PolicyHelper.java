package org.omg.CORBA;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyHelper
{
  private static String _id = "IDL:omg.org/CORBA/Policy:1.0";
  private static TypeCode __typeCode = null;
  
  public PolicyHelper() {}
  
  public static void insert(Any paramAny, Policy paramPolicy)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramPolicy);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Policy extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "Policy");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Policy read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_PolicyStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, Policy paramPolicy)
  {
    paramOutputStream.write_Object(paramPolicy);
  }
  
  public static Policy narrow(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Policy)) {
      return (Policy)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    return new _PolicyStub(localDelegate);
  }
}
