package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AnySeqHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/AnySeq:1.0";
  private static TypeCode __typeCode = null;
  
  public AnySeqHelper() {}
  
  public static void insert(Any paramAny, Any[] paramArrayOfAny)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfAny);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Any[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "AnySeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Any[] read(InputStream paramInputStream)
  {
    Any[] arrayOfAny = null;
    int i = paramInputStream.read_long();
    arrayOfAny = new Any[i];
    for (int j = 0; j < arrayOfAny.length; j++) {
      arrayOfAny[j] = paramInputStream.read_any();
    }
    return arrayOfAny;
  }
  
  public static void write(OutputStream paramOutputStream, Any[] paramArrayOfAny)
  {
    paramOutputStream.write_long(paramArrayOfAny.length);
    for (int i = 0; i < paramArrayOfAny.length; i++) {
      paramOutputStream.write_any(paramArrayOfAny[i]);
    }
  }
}
