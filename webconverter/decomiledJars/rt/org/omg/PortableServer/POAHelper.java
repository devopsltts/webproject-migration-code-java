package org.omg.PortableServer;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class POAHelper
{
  private static String _id = "IDL:omg.org/PortableServer/POA:2.3";
  private static TypeCode __typeCode = null;
  
  public POAHelper() {}
  
  public static void insert(Any paramAny, POA paramPOA)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramPOA);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static POA extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "POA");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static POA read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, POA paramPOA)
  {
    throw new MARSHAL();
  }
  
  public static POA narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof POA)) {
      return (POA)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    return null;
  }
}
