package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceIdHelper
{
  private static String _id = "IDL:omg.org/IOP/ServiceId:1.0";
  private static TypeCode __typeCode = null;
  
  public ServiceIdHelper() {}
  
  public static void insert(Any paramAny, int paramInt)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramInt);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static int extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
      __typeCode = ORB.init().create_alias_tc(id(), "ServiceId", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static int read(InputStream paramInputStream)
  {
    int i = 0;
    i = paramInputStream.read_ulong();
    return i;
  }
  
  public static void write(OutputStream paramOutputStream, int paramInt)
  {
    paramOutputStream.write_ulong(paramInt);
  }
}
