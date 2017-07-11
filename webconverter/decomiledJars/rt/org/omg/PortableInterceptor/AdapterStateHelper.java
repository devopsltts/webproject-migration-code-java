package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AdapterStateHelper
{
  private static String _id = "IDL:omg.org/PortableInterceptor/AdapterState:1.0";
  private static TypeCode __typeCode = null;
  
  public AdapterStateHelper() {}
  
  public static void insert(Any paramAny, short paramShort)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramShort);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static short extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
      __typeCode = ORB.init().create_alias_tc(id(), "AdapterState", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static short read(InputStream paramInputStream)
  {
    short s = 0;
    s = paramInputStream.read_short();
    return s;
  }
  
  public static void write(OutputStream paramOutputStream, short paramShort)
  {
    paramOutputStream.write_short(paramShort);
  }
}
