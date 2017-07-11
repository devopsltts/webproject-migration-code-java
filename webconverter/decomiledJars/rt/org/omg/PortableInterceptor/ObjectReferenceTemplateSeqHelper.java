package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectReferenceTemplateSeqHelper
{
  private static String _id = "IDL:omg.org/PortableInterceptor/ObjectReferenceTemplateSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public ObjectReferenceTemplateSeqHelper() {}
  
  public static void insert(Any paramAny, ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfObjectReferenceTemplate);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ObjectReferenceTemplate[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ObjectReferenceTemplateHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ObjectReferenceTemplateSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static ObjectReferenceTemplate[] read(InputStream paramInputStream)
  {
    ObjectReferenceTemplate[] arrayOfObjectReferenceTemplate = null;
    int i = paramInputStream.read_long();
    arrayOfObjectReferenceTemplate = new ObjectReferenceTemplate[i];
    for (int j = 0; j < arrayOfObjectReferenceTemplate.length; j++) {
      arrayOfObjectReferenceTemplate[j] = ObjectReferenceTemplateHelper.read(paramInputStream);
    }
    return arrayOfObjectReferenceTemplate;
  }
  
  public static void write(OutputStream paramOutputStream, ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate)
  {
    paramOutputStream.write_long(paramArrayOfObjectReferenceTemplate.length);
    for (int i = 0; i < paramArrayOfObjectReferenceTemplate.length; i++) {
      ObjectReferenceTemplateHelper.write(paramOutputStream, paramArrayOfObjectReferenceTemplate[i]);
    }
  }
}
