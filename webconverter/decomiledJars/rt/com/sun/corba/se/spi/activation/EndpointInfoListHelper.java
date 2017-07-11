package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class EndpointInfoListHelper
{
  private static String _id = "IDL:activation/EndpointInfoList:1.0";
  private static TypeCode __typeCode = null;
  
  public EndpointInfoListHelper() {}
  
  public static void insert(Any paramAny, EndPointInfo[] paramArrayOfEndPointInfo)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfEndPointInfo);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static EndPointInfo[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = EndPointInfoHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "EndpointInfoList", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static EndPointInfo[] read(InputStream paramInputStream)
  {
    EndPointInfo[] arrayOfEndPointInfo = null;
    int i = paramInputStream.read_long();
    arrayOfEndPointInfo = new EndPointInfo[i];
    for (int j = 0; j < arrayOfEndPointInfo.length; j++) {
      arrayOfEndPointInfo[j] = EndPointInfoHelper.read(paramInputStream);
    }
    return arrayOfEndPointInfo;
  }
  
  public static void write(OutputStream paramOutputStream, EndPointInfo[] paramArrayOfEndPointInfo)
  {
    paramOutputStream.write_long(paramArrayOfEndPointInfo.length);
    for (int i = 0; i < paramArrayOfEndPointInfo.length; i++) {
      EndPointInfoHelper.write(paramOutputStream, paramArrayOfEndPointInfo[i]);
    }
  }
}
