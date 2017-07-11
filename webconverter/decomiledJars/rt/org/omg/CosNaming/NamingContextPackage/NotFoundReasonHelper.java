package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NotFoundReasonHelper
{
  private static String _id = "IDL:omg.org/CosNaming/NamingContext/NotFoundReason:1.0";
  private static TypeCode __typeCode = null;
  
  public NotFoundReasonHelper() {}
  
  public static void insert(Any paramAny, NotFoundReason paramNotFoundReason)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramNotFoundReason);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NotFoundReason extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_enum_tc(id(), "NotFoundReason", new String[] { "missing_node", "not_context", "not_object" });
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static NotFoundReason read(InputStream paramInputStream)
  {
    return NotFoundReason.from_int(paramInputStream.read_long());
  }
  
  public static void write(OutputStream paramOutputStream, NotFoundReason paramNotFoundReason)
  {
    paramOutputStream.write_long(paramNotFoundReason.value());
  }
}
