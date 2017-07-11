package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class InvalidAddressHelper
{
  private static String _id = "IDL:omg.org/CosNaming/NamingContextExt/InvalidAddress:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public InvalidAddressHelper() {}
  
  public static void insert(Any paramAny, InvalidAddress paramInvalidAddress)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramInvalidAddress);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static InvalidAddress extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[0];
          Object localObject1 = null;
          __typeCode = ORB.init().create_exception_tc(id(), "InvalidAddress", arrayOfStructMember);
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
  
  public static InvalidAddress read(InputStream paramInputStream)
  {
    InvalidAddress localInvalidAddress = new InvalidAddress();
    paramInputStream.read_string();
    return localInvalidAddress;
  }
  
  public static void write(OutputStream paramOutputStream, InvalidAddress paramInvalidAddress)
  {
    paramOutputStream.write_string(id());
  }
}
