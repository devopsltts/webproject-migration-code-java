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

public class _DynSequenceStub
  extends ObjectImpl
  implements DynSequence
{
  public static final Class _opsClass = DynSequenceOperations.class;
  private static String[] __ids = { "IDL:omg.org/DynamicAny/DynSequence:1.0", "IDL:omg.org/DynamicAny/DynAny:1.0" };
  
  public _DynSequenceStub() {}
  
  public int get_length()
  {
    ServantObject localServantObject = _servant_preinvoke("get_length", _opsClass);
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      int i = localDynSequenceOperations.get_length();
      return i;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void set_length(int paramInt)
    throws InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("set_length", _opsClass);
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.set_length(paramInt);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Any[] get_elements()
  {
    ServantObject localServantObject = _servant_preinvoke("get_elements", _opsClass);
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      Any[] arrayOfAny = localDynSequenceOperations.get_elements();
      return arrayOfAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void set_elements(Any[] paramArrayOfAny)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("set_elements", _opsClass);
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.set_elements(paramArrayOfAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public DynAny[] get_elements_as_dyn_any()
  {
    ServantObject localServantObject = _servant_preinvoke("get_elements_as_dyn_any", _opsClass);
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      DynAny[] arrayOfDynAny = localDynSequenceOperations.get_elements_as_dyn_any();
      return arrayOfDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void set_elements_as_dyn_any(DynAny[] paramArrayOfDynAny)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("set_elements_as_dyn_any", _opsClass);
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.set_elements_as_dyn_any(paramArrayOfDynAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public TypeCode type()
  {
    ServantObject localServantObject = _servant_preinvoke("type", _opsClass);
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      TypeCode localTypeCode = localDynSequenceOperations.type();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.assign(paramDynAny);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.from_any(paramAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Any to_any()
  {
    ServantObject localServantObject = _servant_preinvoke("to_any", _opsClass);
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      Any localAny = localDynSequenceOperations.to_any();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynSequenceOperations.equal(paramDynAny);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.destroy();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynSequenceOperations.copy();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_boolean(paramBoolean);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_octet(paramByte);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_char(paramChar);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_short(paramShort);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_ushort(paramShort);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_long(paramInt);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_ulong(paramInt);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_float(paramFloat);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_double(paramDouble);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_string(paramString);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_reference(paramObject);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_typecode(paramTypeCode);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_longlong(paramLong);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_ulonglong(paramLong);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_wchar(paramChar);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_wstring(paramString);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_any(paramAny);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_dyn_any(paramDynAny);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.insert_val(paramSerializable);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynSequenceOperations.get_boolean();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      byte b = localDynSequenceOperations.get_octet();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      char c = localDynSequenceOperations.get_char();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      short s = localDynSequenceOperations.get_short();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      short s = localDynSequenceOperations.get_ushort();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      int i = localDynSequenceOperations.get_long();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      int i = localDynSequenceOperations.get_ulong();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      float f = localDynSequenceOperations.get_float();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      double d = localDynSequenceOperations.get_double();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      String str = localDynSequenceOperations.get_string();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      org.omg.CORBA.Object localObject = localDynSequenceOperations.get_reference();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      TypeCode localTypeCode = localDynSequenceOperations.get_typecode();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      long l = localDynSequenceOperations.get_longlong();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      long l = localDynSequenceOperations.get_ulonglong();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      char c = localDynSequenceOperations.get_wchar();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      String str = localDynSequenceOperations.get_wstring();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      Any localAny = localDynSequenceOperations.get_any();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynSequenceOperations.get_dyn_any();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      Serializable localSerializable = localDynSequenceOperations.get_val();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynSequenceOperations.seek(paramInt);
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      localDynSequenceOperations.rewind();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      boolean bool = localDynSequenceOperations.next();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      int i = localDynSequenceOperations.component_count();
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
    DynSequenceOperations localDynSequenceOperations = (DynSequenceOperations)localServantObject.servant;
    try
    {
      DynAny localDynAny = localDynSequenceOperations.current_component();
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
