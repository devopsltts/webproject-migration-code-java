package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ULongLongSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/ULongLongSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public ULongLongSeqHelper() {}
  
  public static void insert(Any paramAny, long[] paramArrayOfLong)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfLong);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static long[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulonglong);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ULongLongSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static long[] read(InputStream paramInputStream)
  {
    long[] arrayOfLong = null;
    int i = paramInputStream.read_long();
    arrayOfLong = new long[i];
    paramInputStream.read_ulonglong_array(arrayOfLong, 0, i);
    return arrayOfLong;
  }
  
  public static void write(OutputStream paramOutputStream, long[] paramArrayOfLong)
  {
    paramOutputStream.write_long(paramArrayOfLong.length);
    paramOutputStream.write_ulonglong_array(paramArrayOfLong, 0, paramArrayOfLong.length);
  }
}
