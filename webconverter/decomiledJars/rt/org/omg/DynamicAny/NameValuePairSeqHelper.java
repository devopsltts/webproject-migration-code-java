package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameValuePairSeqHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/NameValuePairSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public NameValuePairSeqHelper() {}
  
  public static void insert(Any paramAny, NameValuePair[] paramArrayOfNameValuePair)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfNameValuePair);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NameValuePair[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = NameValuePairHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "NameValuePairSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static NameValuePair[] read(InputStream paramInputStream)
  {
    NameValuePair[] arrayOfNameValuePair = null;
    int i = paramInputStream.read_long();
    arrayOfNameValuePair = new NameValuePair[i];
    for (int j = 0; j < arrayOfNameValuePair.length; j++) {
      arrayOfNameValuePair[j] = NameValuePairHelper.read(paramInputStream);
    }
    return arrayOfNameValuePair;
  }
  
  public static void write(OutputStream paramOutputStream, NameValuePair[] paramArrayOfNameValuePair)
  {
    paramOutputStream.write_long(paramArrayOfNameValuePair.length);
    for (int i = 0; i < paramArrayOfNameValuePair.length; i++) {
      NameValuePairHelper.write(paramOutputStream, paramArrayOfNameValuePair[i]);
    }
  }
}
