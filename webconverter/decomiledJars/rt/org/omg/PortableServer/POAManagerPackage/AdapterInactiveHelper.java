package org.omg.PortableServer.POAManagerPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AdapterInactiveHelper
{
  private static String _id = "IDL:omg.org/PortableServer/POAManager/AdapterInactive:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public AdapterInactiveHelper() {}
  
  public static void insert(Any paramAny, AdapterInactive paramAdapterInactive)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramAdapterInactive);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static AdapterInactive extract(Any paramAny)
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
          __typeCode = ORB.init().create_exception_tc(id(), "AdapterInactive", arrayOfStructMember);
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
  
  public static AdapterInactive read(InputStream paramInputStream)
  {
    AdapterInactive localAdapterInactive = new AdapterInactive();
    paramInputStream.read_string();
    return localAdapterInactive;
  }
  
  public static void write(OutputStream paramOutputStream, AdapterInactive paramAdapterInactive)
  {
    paramOutputStream.write_string(id());
  }
}
