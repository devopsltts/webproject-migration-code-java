package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;

public abstract class ObjectReferenceFactoryHelper
{
  private static String _id = "IDL:omg.org/PortableInterceptor/ObjectReferenceFactory:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ObjectReferenceFactoryHelper() {}
  
  public static void insert(Any paramAny, ObjectReferenceFactory paramObjectReferenceFactory)
  {
    org.omg.CORBA.portable.OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramObjectReferenceFactory);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ObjectReferenceFactory extract(Any paramAny)
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
          ValueMember[] arrayOfValueMember = new ValueMember[0];
          Object localObject1 = null;
          __typeCode = ORB.init().create_value_tc(_id, "ObjectReferenceFactory", (short)2, null, arrayOfValueMember);
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
  
  public static ObjectReferenceFactory read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    return (ObjectReferenceFactory)((org.omg.CORBA_2_3.portable.InputStream)paramInputStream).read_value(id());
  }
  
  public static void write(org.omg.CORBA.portable.OutputStream paramOutputStream, ObjectReferenceFactory paramObjectReferenceFactory)
  {
    ((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream).write_value(paramObjectReferenceFactory, id());
  }
}
