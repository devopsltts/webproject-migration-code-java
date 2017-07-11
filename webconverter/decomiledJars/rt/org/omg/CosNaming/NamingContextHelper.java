package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class NamingContextHelper
{
  private static String _id = "IDL:omg.org/CosNaming/NamingContext:1.0";
  private static TypeCode __typeCode = null;
  
  public NamingContextHelper() {}
  
  public static void insert(Any paramAny, NamingContext paramNamingContext)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramNamingContext);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NamingContext extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "NamingContext");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static NamingContext read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_NamingContextStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, NamingContext paramNamingContext)
  {
    paramOutputStream.write_Object(paramNamingContext);
  }
  
  public static NamingContext narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof NamingContext)) {
      return (NamingContext)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _NamingContextStub local_NamingContextStub = new _NamingContextStub();
    local_NamingContextStub._set_delegate(localDelegate);
    return local_NamingContextStub;
  }
  
  public static NamingContext unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof NamingContext)) {
      return (NamingContext)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _NamingContextStub local_NamingContextStub = new _NamingContextStub();
    local_NamingContextStub._set_delegate(localDelegate);
    return local_NamingContextStub;
  }
}
