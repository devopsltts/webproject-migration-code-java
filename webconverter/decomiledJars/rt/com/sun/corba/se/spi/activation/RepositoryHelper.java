package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class RepositoryHelper
{
  private static String _id = "IDL:activation/Repository:1.0";
  private static TypeCode __typeCode = null;
  
  public RepositoryHelper() {}
  
  public static void insert(Any paramAny, Repository paramRepository)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramRepository);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Repository extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "Repository");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Repository read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_RepositoryStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, Repository paramRepository)
  {
    paramOutputStream.write_Object(paramRepository);
  }
  
  public static Repository narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Repository)) {
      return (Repository)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _RepositoryStub local_RepositoryStub = new _RepositoryStub();
    local_RepositoryStub._set_delegate(localDelegate);
    return local_RepositoryStub;
  }
  
  public static Repository unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Repository)) {
      return (Repository)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _RepositoryStub local_RepositoryStub = new _RepositoryStub();
    local_RepositoryStub._set_delegate(localDelegate);
    return local_RepositoryStub;
  }
}
