package org.omg.DynamicAny;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class _DynValueStub
  extends ObjectImpl
  implements DynValue
{
  public static final Class _opsClass = DynValueOperations.class;
  private static String[] __ids = { "IDL:omg.org/DynamicAny/DynValue:1.0", "IDL:omg.org/DynamicAny/DynValueCommon:1.0", "IDL:omg.org/DynamicAny/DynAny:1.0" };
  
  public _DynValueStub() {}
  
  public String current_member_name()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("current_member_name", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      String str = localDynValueOperations.current_member_name();
      return str;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public TCKind current_member_kind()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("current_member_kind", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      TCKind localTCKind = localDynValueOperations.current_member_kind();
      return localTCKind;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public NameValuePair[] get_members()
    throws InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_members", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      NameValuePair[] arrayOfNameValuePair = localDynValueOperations.get_members();
      return arrayOfNameValuePair;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void set_members(NameValuePair[] paramArrayOfNameValuePair)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("set_members", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.set_members(paramArrayOfNameValuePair);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public NameDynAnyPair[] get_members_as_dyn_any()
    throws InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_members_as_dyn_any", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      NameDynAnyPair[] arrayOfNameDynAnyPair = localDynValueOperations.get_members_as_dyn_any();
      return arrayOfNameDynAnyPair;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void set_members_as_dyn_any(NameDynAnyPair[] paramArrayOfNameDynAnyPair)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("set_members_as_dyn_any", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.set_members_as_dyn_any(paramArrayOfNameDynAnyPair);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean is_null()
  {
    ServantObject localServantObject = _servant_preinvoke("is_null", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynValueOperations.is_null();
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void set_to_null()
  {
    ServantObject localServantObject = _servant_preinvoke("set_to_null", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.set_to_null();
      _servant_postinvoke(localServantObject);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void set_to_value()
  {
    ServantObject localServantObject = _servant_preinvoke("set_to_value", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.set_to_value();
      _servant_postinvoke(localServantObject);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public TypeCode type()
  {
    ServantObject localServantObject = _servant_preinvoke("type", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      TypeCode localTypeCode = localDynValueOperations.type();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.assign(paramDynAny);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.from_any(paramAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Any to_any()
  {
    ServantObject localServantObject = _servant_preinvoke("to_any", _opsClass);
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      Any localAny = localDynValueOperations.to_any();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynValueOperations.equal(paramDynAny);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.destroy();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynValueOperations.copy();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_boolean(paramBoolean);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_octet(paramByte);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_char(paramChar);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_short(paramShort);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_ushort(paramShort);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_long(paramInt);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_ulong(paramInt);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_float(paramFloat);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_double(paramDouble);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_string(paramString);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_reference(paramObject);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_typecode(paramTypeCode);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_longlong(paramLong);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_ulonglong(paramLong);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_wchar(paramChar);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_wstring(paramString);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_any(paramAny);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_dyn_any(paramDynAny);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.insert_val(paramSerializable);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynValueOperations.get_boolean();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      byte b = localDynValueOperations.get_octet();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      char c = localDynValueOperations.get_char();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      short s = localDynValueOperations.get_short();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      short s = localDynValueOperations.get_ushort();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      int i = localDynValueOperations.get_long();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      int i = localDynValueOperations.get_ulong();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      float f = localDynValueOperations.get_float();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      double d = localDynValueOperations.get_double();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      String str = localDynValueOperations.get_string();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      org.omg.CORBA.Object localObject = localDynValueOperations.get_reference();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      TypeCode localTypeCode = localDynValueOperations.get_typecode();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      long l = localDynValueOperations.get_longlong();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      long l = localDynValueOperations.get_ulonglong();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      char c = localDynValueOperations.get_wchar();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      String str = localDynValueOperations.get_wstring();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      Any localAny = localDynValueOperations.get_any();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynValueOperations.get_dyn_any();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      Serializable localSerializable = localDynValueOperations.get_val();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynValueOperations.seek(paramInt);
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      localDynValueOperations.rewind();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynValueOperations.next();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      int i = localDynValueOperations.component_count();
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
    DynValueOperations localDynValueOperations = (DynValueOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynValueOperations.current_component();
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
