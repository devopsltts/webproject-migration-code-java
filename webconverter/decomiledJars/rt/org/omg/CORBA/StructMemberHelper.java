package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class StructMemberHelper
{
  private static String _id = "IDL:omg.org/CORBA/StructMember:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public StructMemberHelper() {}
  
  public static void insert(Any paramAny, StructMember paramStructMember)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramStructMember);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static StructMember extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[3];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", localTypeCode);
          arrayOfStructMember[0] = new StructMember("name", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[1] = new StructMember("type", localTypeCode, null);
          localTypeCode = IDLTypeHelper.type();
          arrayOfStructMember[2] = new StructMember("type_def", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "StructMember", arrayOfStructMember);
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
  
  public static StructMember read(InputStream paramInputStream)
  {
    StructMember localStructMember = new StructMember();
    localStructMember.name = paramInputStream.read_string();
    localStructMember.type = paramInputStream.read_TypeCode();
    localStructMember.type_def = IDLTypeHelper.read(paramInputStream);
    return localStructMember;
  }
  
  public static void write(OutputStream paramOutputStream, StructMember paramStructMember)
  {
    paramOutputStream.write_string(paramStructMember.name);
    paramOutputStream.write_TypeCode(paramStructMember.type);
    IDLTypeHelper.write(paramOutputStream, paramStructMember.type_def);
  }
}
