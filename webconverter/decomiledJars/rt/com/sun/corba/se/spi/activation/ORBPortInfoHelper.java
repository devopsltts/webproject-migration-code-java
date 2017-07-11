package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ORBPortInfoHelper
{
  private static String _id = "IDL:activation/ORBPortInfo:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ORBPortInfoHelper() {}
  
  public static void insert(Any paramAny, ORBPortInfo paramORBPortInfo)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramORBPortInfo);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ORBPortInfo extract(Any paramAny)
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
          localTypeCode = ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", localTypeCode);
          arrayOfStructMember[0] = new StructMember("orbId", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
          localTypeCode = ORB.init().create_alias_tc(TCPPortHelper.id(), "TCPPort", localTypeCode);
          arrayOfStructMember[1] = new StructMember("port", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ORBPortInfo", arrayOfStructMember);
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
  
  public static ORBPortInfo read(InputStream paramInputStream)
  {
    ORBPortInfo localORBPortInfo = new ORBPortInfo();
    localORBPortInfo.orbId = paramInputStream.read_string();
    localORBPortInfo.port = paramInputStream.read_long();
    return localORBPortInfo;
  }
  
  public static void write(OutputStream paramOutputStream, ORBPortInfo paramORBPortInfo)
  {
    paramOutputStream.write_string(paramORBPortInfo.orbId);
    paramOutputStream.write_long(paramORBPortInfo.port);
  }
}
