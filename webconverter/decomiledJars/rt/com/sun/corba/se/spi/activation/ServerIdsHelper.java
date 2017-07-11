package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerIdsHelper
{
  private static String _id = "IDL:activation/ServerIds:1.0";
  private static TypeCode __typeCode = null;
  
  public ServerIdsHelper() {}
  
  public static void insert(Any paramAny, int[] paramArrayOfInt)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfInt);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static int[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
      __typeCode = ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", __typeCode);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ServerIds", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static int[] read(InputStream paramInputStream)
  {
    int[] arrayOfInt = null;
    int i = paramInputStream.read_long();
    arrayOfInt = new int[i];
    for (int j = 0; j < arrayOfInt.length; j++) {
      arrayOfInt[j] = ServerIdHelper.read(paramInputStream);
    }
    return arrayOfInt;
  }
  
  public static void write(OutputStream paramOutputStream, int[] paramArrayOfInt)
  {
    paramOutputStream.write_long(paramArrayOfInt.length);
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      ServerIdHelper.write(paramOutputStream, paramArrayOfInt[i]);
    }
  }
}
