package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectHelper
{
  private static String _id = "";
  private static TypeCode __typeCode = null;
  
  public ObjectHelper() {}
  
  public static void insert(Any paramAny, Object paramObject)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramObject);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Object extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Object read(InputStream paramInputStream)
  {
    return paramInputStream.read_Object();
  }
  
  public static void write(OutputStream paramOutputStream, Object paramObject)
  {
    paramOutputStream.write_Object(paramObject);
  }
}
