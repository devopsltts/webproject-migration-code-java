package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceContextHelper
{
  private static String _id = "IDL:omg.org/IOP/ServiceContext:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ServiceContextHelper() {}
  
  public static void insert(Any paramAny, ServiceContext paramServiceContext)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramServiceContext);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ServiceContext extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      synchronized (TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active) {
            return ORB.init().create_recursive_tc(_id);
          }
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
          localTypeCode = ORB.init().create_alias_tc(ServiceIdHelper.id(), "ServiceId", localTypeCode);
          arrayOfStructMember[0] = new StructMember("context_id", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          arrayOfStructMember[1] = new StructMember("context_data", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ServiceContext", arrayOfStructMember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static ServiceContext read(InputStream paramInputStream)
  {
    ServiceContext localServiceContext = new ServiceContext();
    localServiceContext.context_id = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    localServiceContext.context_data = new byte[i];
    paramInputStream.read_octet_array(localServiceContext.context_data, 0, i);
    return localServiceContext;
  }
  
  public static void write(OutputStream paramOutputStream, ServiceContext paramServiceContext)
  {
    paramOutputStream.write_ulong(paramServiceContext.context_id);
    paramOutputStream.write_long(paramServiceContext.context_data.length);
    paramOutputStream.write_octet_array(paramServiceContext.context_data, 0, paramServiceContext.context_data.length);
  }
}
