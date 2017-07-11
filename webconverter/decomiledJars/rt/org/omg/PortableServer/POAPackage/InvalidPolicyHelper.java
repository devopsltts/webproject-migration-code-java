package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class InvalidPolicyHelper
{
  private static String _id = "IDL:omg.org/PortableServer/POA/InvalidPolicy:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public InvalidPolicyHelper() {}
  
  public static void insert(Any paramAny, InvalidPolicy paramInvalidPolicy)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramInvalidPolicy);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static InvalidPolicy extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_ushort);
          arrayOfStructMember[0] = new StructMember("index", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "InvalidPolicy", arrayOfStructMember);
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
  
  public static InvalidPolicy read(InputStream paramInputStream)
  {
    InvalidPolicy localInvalidPolicy = new InvalidPolicy();
    paramInputStream.read_string();
    localInvalidPolicy.index = paramInputStream.read_ushort();
    return localInvalidPolicy;
  }
  
  public static void write(OutputStream paramOutputStream, InvalidPolicy paramInvalidPolicy)
  {
    paramOutputStream.write_string(id());
    paramOutputStream.write_ushort(paramInvalidPolicy.index);
  }
}
