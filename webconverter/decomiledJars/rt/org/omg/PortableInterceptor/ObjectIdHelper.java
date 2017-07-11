package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.OctetSeqHelper;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectIdHelper
{
  private static String _id = "IDL:omg.org/PortableInterceptor/ObjectId:1.0";
  private static TypeCode __typeCode = null;
  
  public ObjectIdHelper() {}
  
  public static void insert(Any paramAny, byte[] paramArrayOfByte)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfByte);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static byte[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(OctetSeqHelper.id(), "OctetSeq", __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ObjectId", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static byte[] read(InputStream paramInputStream)
  {
    byte[] arrayOfByte = null;
    arrayOfByte = OctetSeqHelper.read(paramInputStream);
    return arrayOfByte;
  }
  
  public static void write(OutputStream paramOutputStream, byte[] paramArrayOfByte)
  {
    OctetSeqHelper.write(paramOutputStream, paramArrayOfByte);
  }
}
