package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerAlreadyRegisteredHelper
{
  private static String _id = "IDL:activation/ServerAlreadyRegistered:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ServerAlreadyRegisteredHelper() {}
  
  public static void insert(Any paramAny, ServerAlreadyRegistered paramServerAlreadyRegistered)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramServerAlreadyRegistered);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ServerAlreadyRegistered extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
          localTypeCode = ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", localTypeCode);
          arrayOfStructMember[0] = new StructMember("serverId", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "ServerAlreadyRegistered", arrayOfStructMember);
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
  
  public static ServerAlreadyRegistered read(InputStream paramInputStream)
  {
    ServerAlreadyRegistered localServerAlreadyRegistered = new ServerAlreadyRegistered();
    paramInputStream.read_string();
    localServerAlreadyRegistered.serverId = paramInputStream.read_long();
    return localServerAlreadyRegistered;
  }
  
  public static void write(OutputStream paramOutputStream, ServerAlreadyRegistered paramServerAlreadyRegistered)
  {
    paramOutputStream.write_string(id());
    paramOutputStream.write_long(paramServerAlreadyRegistered.serverId);
  }
}
