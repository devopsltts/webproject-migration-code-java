package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ParDescriptionSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/ParDescriptionSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public ParDescriptionSeqHelper() {}
  
  public static void insert(Any paramAny, ParameterDescription[] paramArrayOfParameterDescription)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfParameterDescription);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ParameterDescription[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ParameterDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ParDescriptionSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static ParameterDescription[] read(InputStream paramInputStream)
  {
    ParameterDescription[] arrayOfParameterDescription = null;
    int i = paramInputStream.read_long();
    arrayOfParameterDescription = new ParameterDescription[i];
    for (int j = 0; j < arrayOfParameterDescription.length; j++) {
      arrayOfParameterDescription[j] = ParameterDescriptionHelper.read(paramInputStream);
    }
    return arrayOfParameterDescription;
  }
  
  public static void write(OutputStream paramOutputStream, ParameterDescription[] paramArrayOfParameterDescription)
  {
    paramOutputStream.write_long(paramArrayOfParameterDescription.length);
    for (int i = 0; i < paramArrayOfParameterDescription.length; i++) {
      ParameterDescriptionHelper.write(paramOutputStream, paramArrayOfParameterDescription[i]);
    }
  }
}
