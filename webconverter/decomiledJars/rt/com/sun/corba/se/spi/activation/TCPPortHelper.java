package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TCPPortHelper
{
  private static String _id = "IDL:activation/TCPPort:1.0";
  private static TypeCode __typeCode = null;
  
  public TCPPortHelper() {}
  
  public static void insert(Any paramAny, int paramInt)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramInt);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static int extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
      __typeCode = ORB.init().create_alias_tc(id(), "TCPPort", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static int read(InputStream paramInputStream)
  {
    int i = 0;
    i = paramInputStream.read_long();
    return i;
  }
  
  public static void write(OutputStream paramOutputStream, int paramInt)
  {
    paramOutputStream.write_long(paramInt);
  }
}
