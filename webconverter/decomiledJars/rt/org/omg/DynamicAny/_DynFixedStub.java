package org.omg.DynamicAny;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class _DynFixedStub
  extends ObjectImpl
  implements DynFixed
{
  public static final Class _opsClass = DynFixedOperations.class;
  private static String[] __ids = { "IDL:omg.org/DynamicAny/DynFixed:1.0", "IDL:omg.org/DynamicAny/DynAny:1.0" };
  
  public _DynFixedStub() {}
  
  public String get_value()
  {
    ServantObject localServantObject = _servant_preinvoke("get_value", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      String str = localDynFixedOperations.get_value();
      return str;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean set_value(String paramString)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("set_value", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynFixedOperations.set_value(paramString);
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public TypeCode type()
  {
    ServantObject localServantObject = _servant_preinvoke("type", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      TypeCode localTypeCode = localDynFixedOperations.type();
      return localTypeCode;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void assign(DynAny paramDynAny)
    throws TypeMismatch
  {
    ServantObject localServantObject = _servant_preinvoke("assign", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.assign(paramDynAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void from_any(Any paramAny)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("from_any", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.from_any(paramAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Any to_any()
  {
    ServantObject localServantObject = _servant_preinvoke("to_any", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      Any localAny = localDynFixedOperations.to_any();
      return localAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean equal(DynAny paramDynAny)
  {
    ServantObject localServantObject = _servant_preinvoke("equal", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynFixedOperations.equal(paramDynAny);
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void destroy()
  {
    ServantObject localServantObject = _servant_preinvoke("destroy", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.destroy();
      _servant_postinvoke(localServantObject);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public DynAny copy()
  {
    ServantObject localServantObject = _servant_preinvoke("copy", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynFixedOperations.copy();
      return localDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_boolean(boolean paramBoolean)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_boolean", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_boolean(paramBoolean);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_octet(byte paramByte)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_octet", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_octet(paramByte);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_char(char paramChar)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_char", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_char(paramChar);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_short(short paramShort)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_short", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_short(paramShort);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_ushort(short paramShort)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_ushort", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_ushort(paramShort);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_long(int paramInt)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_long", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_long(paramInt);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_ulong(int paramInt)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_ulong", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_ulong(paramInt);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_float(float paramFloat)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_float", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_float(paramFloat);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_double(double paramDouble)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_double", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_double(paramDouble);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_string(String paramString)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_string", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_string(paramString);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_reference(org.omg.CORBA.Object paramObject)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_reference", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_reference(paramObject);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_typecode(TypeCode paramTypeCode)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_typecode", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_typecode(paramTypeCode);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_longlong(long paramLong)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_longlong", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_longlong(paramLong);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_ulonglong(long paramLong)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_ulonglong", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_ulonglong(paramLong);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_wchar(char paramChar)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_wchar", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_wchar(paramChar);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_wstring(String paramString)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_wstring", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_wstring(paramString);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_any(Any paramAny)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_any", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_any(paramAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_dyn_any(DynAny paramDynAny)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_dyn_any", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_dyn_any(paramDynAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_val(Serializable paramSerializable)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_val", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.insert_val(paramSerializable);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean get_boolean()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_boolean", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynFixedOperations.get_boolean();
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public byte get_octet()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_octet", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      byte b = localDynFixedOperations.get_octet();
      return b;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public char get_char()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_char", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      char c = localDynFixedOperations.get_char();
      return c;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public short get_short()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_short", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      short s = localDynFixedOperations.get_short();
      return s;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public short get_ushort()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_ushort", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      short s = localDynFixedOperations.get_ushort();
      return s;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public int get_long()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_long", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      int i = localDynFixedOperations.get_long();
      return i;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public int get_ulong()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_ulong", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      int i = localDynFixedOperations.get_ulong();
      return i;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public float get_float()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_float", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      float f = localDynFixedOperations.get_float();
      return f;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public double get_double()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_double", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      double d = localDynFixedOperations.get_double();
      return d;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public String get_string()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_string", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      String str = localDynFixedOperations.get_string();
      return str;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public org.omg.CORBA.Object get_reference()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_reference", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      org.omg.CORBA.Object localObject = localDynFixedOperations.get_reference();
      return localObject;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public TypeCode get_typecode()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_typecode", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      TypeCode localTypeCode = localDynFixedOperations.get_typecode();
      return localTypeCode;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public long get_longlong()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_longlong", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      long l = localDynFixedOperations.get_longlong();
      return l;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public long get_ulonglong()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_ulonglong", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      long l = localDynFixedOperations.get_ulonglong();
      return l;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public char get_wchar()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_wchar", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      char c = localDynFixedOperations.get_wchar();
      return c;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public String get_wstring()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_wstring", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      String str = localDynFixedOperations.get_wstring();
      return str;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Any get_any()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_any", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      Any localAny = localDynFixedOperations.get_any();
      return localAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public DynAny get_dyn_any()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_dyn_any", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynFixedOperations.get_dyn_any();
      return localDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Serializable get_val()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_val", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      Serializable localSerializable = localDynFixedOperations.get_val();
      return localSerializable;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean seek(int paramInt)
  {
    ServantObject localServantObject = _servant_preinvoke("seek", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynFixedOperations.seek(paramInt);
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void rewind()
  {
    ServantObject localServantObject = _servant_preinvoke("rewind", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      localDynFixedOperations.rewind();
      _servant_postinvoke(localServantObject);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean next()
  {
    ServantObject localServantObject = _servant_preinvoke("next", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynFixedOperations.next();
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public int component_count()
  {
    ServantObject localServantObject = _servant_preinvoke("component_count", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      int i = localDynFixedOperations.component_count();
      return i;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public DynAny current_component()
    throws TypeMismatch
  {
    ServantObject localServantObject = _servant_preinvoke("current_component", _opsClass);
    DynFixedOperations localDynFixedOperations = (DynFixedOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynFixedOperations.current_component();
      return localDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException
  {
    String str = paramObjectInputStream.readUTF();
    String[] arrayOfString = null;
    Properties localProperties = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    try
    {
      org.omg.CORBA.Object localObject = localORB.string_to_object(str);
      Delegate localDelegate = ((ObjectImpl)localObject)._get_delegate();
      _set_delegate(localDelegate);
    }
    finally
    {
      localORB.destroy();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    String[] arrayOfString = null;
    Properties localProperties = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    try
    {
      String str = localORB.object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    }
    finally
    {
      localORB.destroy();
    }
  }
}
