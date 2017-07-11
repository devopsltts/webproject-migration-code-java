package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameValuePairHelper
{
  private static String _id = "IDL:omg.org/CORBA/NameValuePair:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public NameValuePairHelper() {}
  
  public static void insert(Any paramAny, NameValuePair paramNameValuePair)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramNameValuePair);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NameValuePair extract(Any paramAny)
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
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(FieldNameHelper.id(), "FieldName", localTypeCode);
          arrayOfStructMember[0] = new StructMember("id", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
          arrayOfStructMember[1] = new StructMember("value", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "NameValuePair", arrayOfStructMember);
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
  
  public static NameValuePair read(InputStream paramInputStream)
  {
    NameValuePair localNameValuePair = new NameValuePair();
    localNameValuePair.id = paramInputStream.read_string();
    localNameValuePair.value = paramInputStream.read_any();
    return localNameValuePair;
  }
  
  public static void write(OutputStream paramOutputStream, NameValuePair paramNameValuePair)
  {
    paramOutputStream.write_string(paramNameValuePair.id);
    paramOutputStream.write_any(paramNameValuePair.value);
  }
}
