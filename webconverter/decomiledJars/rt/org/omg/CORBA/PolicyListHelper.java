package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyListHelper
{
  private static String _id = "IDL:omg.org/CORBA/PolicyList:1.0";
  private static TypeCode __typeCode = null;
  
  public PolicyListHelper() {}
  
  public static void insert(Any paramAny, Policy[] paramArrayOfPolicy)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfPolicy);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Policy[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = PolicyHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "PolicyList", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Policy[] read(InputStream paramInputStream)
  {
    Policy[] arrayOfPolicy = null;
    int i = paramInputStream.read_long();
    arrayOfPolicy = new Policy[i];
    for (int j = 0; j < arrayOfPolicy.length; j++) {
      arrayOfPolicy[j] = PolicyHelper.read(paramInputStream);
    }
    return arrayOfPolicy;
  }
  
  public static void write(OutputStream paramOutputStream, Policy[] paramArrayOfPolicy)
  {
    paramOutputStream.write_long(paramArrayOfPolicy.length);
    for (int i = 0; i < paramArrayOfPolicy.length; i++) {
      PolicyHelper.write(paramOutputStream, paramArrayOfPolicy[i]);
    }
  }
}
