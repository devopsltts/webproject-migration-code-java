package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class BindingIteratorHelper
{
  private static String _id = "IDL:omg.org/CosNaming/BindingIterator:1.0";
  private static TypeCode __typeCode = null;
  
  public BindingIteratorHelper() {}
  
  public static void insert(Any paramAny, BindingIterator paramBindingIterator)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramBindingIterator);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static BindingIterator extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "BindingIterator");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static BindingIterator read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_BindingIteratorStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, BindingIterator paramBindingIterator)
  {
    paramOutputStream.write_Object(paramBindingIterator);
  }
  
  public static BindingIterator narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof BindingIterator)) {
      return (BindingIterator)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _BindingIteratorStub local_BindingIteratorStub = new _BindingIteratorStub();
    local_BindingIteratorStub._set_delegate(localDelegate);
    return local_BindingIteratorStub;
  }
  
  public static BindingIterator unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof BindingIterator)) {
      return (BindingIterator)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _BindingIteratorStub local_BindingIteratorStub = new _BindingIteratorStub();
    local_BindingIteratorStub._set_delegate(localDelegate);
    return local_BindingIteratorStub;
  }
}
