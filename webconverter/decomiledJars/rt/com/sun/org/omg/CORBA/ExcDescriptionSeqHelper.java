package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ExcDescriptionSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/ExcDescriptionSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public ExcDescriptionSeqHelper() {}
  
  public static void insert(Any paramAny, ExceptionDescription[] paramArrayOfExceptionDescription)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfExceptionDescription);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ExceptionDescription[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ExceptionDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ExcDescriptionSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static ExceptionDescription[] read(InputStream paramInputStream)
  {
    ExceptionDescription[] arrayOfExceptionDescription = null;
    int i = paramInputStream.read_long();
    arrayOfExceptionDescription = new ExceptionDescription[i];
    for (int j = 0; j < arrayOfExceptionDescription.length; j++) {
      arrayOfExceptionDescription[j] = ExceptionDescriptionHelper.read(paramInputStream);
    }
    return arrayOfExceptionDescription;
  }
  
  public static void write(OutputStream paramOutputStream, ExceptionDescription[] paramArrayOfExceptionDescription)
  {
    paramOutputStream.write_long(paramArrayOfExceptionDescription.length);
    for (int i = 0; i < paramArrayOfExceptionDescription.length; i++) {
      ExceptionDescriptionHelper.write(paramOutputStream, paramArrayOfExceptionDescription[i]);
    }
  }
}
