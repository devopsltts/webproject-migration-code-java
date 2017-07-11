package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectAlreadyActiveHelper
{
  private static String _id = "IDL:omg.org/PortableServer/POA/ObjectAlreadyActive:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ObjectAlreadyActiveHelper() {}
  
  public static void insert(Any paramAny, ObjectAlreadyActive paramObjectAlreadyActive)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramObjectAlreadyActive);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ObjectAlreadyActive extract(Any paramAny)
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
          __typeCode = ORB.init().create_exception_tc(id(), "ObjectAlreadyActive", arrayOfStructMember);
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
  
  public static ObjectAlreadyActive read(InputStream paramInputStream)
  {
    ObjectAlreadyActive localObjectAlreadyActive = new ObjectAlreadyActive();
    paramInputStream.read_string();
    return localObjectAlreadyActive;
  }
  
  public static void write(OutputStream paramOutputStream, ObjectAlreadyActive paramObjectAlreadyActive)
  {
    paramOutputStream.write_string(id());
  }
}
