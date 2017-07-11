package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyConstructedImpl
  extends DynAnyImpl
{
  protected static final byte REPRESENTATION_NONE = 0;
  protected static final byte REPRESENTATION_TYPECODE = 1;
  protected static final byte REPRESENTATION_ANY = 2;
  protected static final byte REPRESENTATION_COMPONENTS = 4;
  protected static final byte RECURSIVE_UNDEF = -1;
  protected static final byte RECURSIVE_NO = 0;
  protected static final byte RECURSIVE_YES = 1;
  protected static final DynAny[] emptyComponents = new DynAny[0];
  DynAny[] components = emptyComponents;
  byte representations = 0;
  byte isRecursive = -1;
  
  private DynAnyConstructedImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynAnyConstructedImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
    if (this.any != null) {
      this.representations = 2;
    }
    this.index = 0;
  }
  
  protected DynAnyConstructedImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
    if (paramTypeCode != null) {
      this.representations = 1;
    }
    this.index = -1;
  }
  
  protected boolean isRecursive()
  {
    if (this.isRecursive == -1)
    {
      TypeCode localTypeCode = this.any.type();
      if ((localTypeCode instanceof TypeCodeImpl))
      {
        if (((TypeCodeImpl)localTypeCode).is_recursive()) {
          this.isRecursive = 1;
        } else {
          this.isRecursive = 0;
        }
      }
      else {
        this.isRecursive = 0;
      }
    }
    return this.isRecursive == 1;
  }
  
  public DynAny current_component()
    throws TypeMismatch
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      return null;
    }
    return checkInitComponents() ? this.components[this.index] : null;
  }
  
  public int component_count()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    return checkInitComponents() ? this.components.length : 0;
  }
  
  public boolean next()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (!checkInitComponents()) {
      return false;
    }
    this.index += 1;
    if ((this.index >= 0) && (this.index < this.components.length)) {
      return true;
    }
    this.index = -1;
    return false;
  }
  
  public boolean seek(int paramInt)
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (paramInt < 0)
    {
      this.index = -1;
      return false;
    }
    if (!checkInitComponents()) {
      return false;
    }
    if (paramInt < this.components.length)
    {
      this.index = paramInt;
      return true;
    }
    return false;
  }
  
  public void rewind()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    seek(0);
  }
  
  protected void clearData()
  {
    super.clearData();
    this.components = emptyComponents;
    this.index = -1;
    this.representations = 0;
  }
  
  protected void writeAny(OutputStream paramOutputStream)
  {
    checkInitAny();
    super.writeAny(paramOutputStream);
  }
  
  protected boolean checkInitComponents()
  {
    if ((this.representations & 0x4) == 0) {
      if ((this.representations & 0x2) != 0)
      {
        if (initializeComponentsFromAny()) {
          this.representations = ((byte)(this.representations | 0x4));
        } else {
          return false;
        }
      }
      else if ((this.representations & 0x1) != 0) {
        if (initializeComponentsFromTypeCode()) {
          this.representations = ((byte)(this.representations | 0x4));
        } else {
          return false;
        }
      }
    }
    return true;
  }
  
  protected void checkInitAny()
  {
    if ((this.representations & 0x2) == 0) {
      if ((this.representations & 0x4) != 0)
      {
        if (initializeAnyFromComponents()) {
          this.representations = ((byte)(this.representations | 0x2));
        }
      }
      else if ((this.representations & 0x1) != 0)
      {
        if ((this.representations == 1) && (isRecursive())) {
          return;
        }
        if (initializeComponentsFromTypeCode()) {
          this.representations = ((byte)(this.representations | 0x4));
        }
        if (initializeAnyFromComponents()) {
          this.representations = ((byte)(this.representations | 0x2));
        }
      }
    }
  }
  
  protected abstract boolean initializeComponentsFromAny();
  
  protected abstract boolean initializeComponentsFromTypeCode();
  
  protected boolean initializeAnyFromComponents()
  {
    OutputStream localOutputStream = this.any.create_output_stream();
    for (int i = 0; i < this.components.length; i++) {
      if ((this.components[i] instanceof DynAnyImpl)) {
        ((DynAnyImpl)this.components[i]).writeAny(localOutputStream);
      } else {
        this.components[i].to_any().write_value(localOutputStream);
      }
    }
    this.any.read_value(localOutputStream.create_input_stream(), this.any.type());
    return true;
  }
  
  public void assign(DynAny paramDynAny)
    throws TypeMismatch
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    clearData();
    super.assign(paramDynAny);
    this.representations = 2;
    this.index = 0;
  }
  
  public void from_any(Any paramAny)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    clearData();
    super.from_any(paramAny);
    this.representations = 2;
    this.index = 0;
  }
  
  public Any to_any()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    checkInitAny();
    return DynAnyUtil.copy(this.any, this.orb);
  }
  
  public boolean equal(DynAny paramDynAny)
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (paramDynAny == this) {
      return true;
    }
    if (!this.any.type().equal(paramDynAny.type())) {
      return false;
    }
    if (!checkInitComponents()) {
      return false;
    }
    DynAny localDynAny = null;
    try
    {
      localDynAny = paramDynAny.current_component();
      for (int i = 0; i < this.components.length; i++)
      {
        boolean bool;
        if (!paramDynAny.seek(i))
        {
          bool = false;
          return bool;
        }
        if (!this.components[i].equal(paramDynAny.current_component()))
        {
          bool = false;
          return bool;
        }
      }
    }
    catch (TypeMismatch localTypeMismatch) {}finally
    {
      DynAnyUtil.set_current_component(paramDynAny, localDynAny);
    }
    return true;
  }
  
  public void destroy()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.status == 0)
    {
      this.status = 2;
      for (int i = 0; i < this.components.length; i++)
      {
        if ((this.components[i] instanceof DynAnyImpl)) {
          ((DynAnyImpl)this.components[i]).setStatus((byte)0);
        }
        this.components[i].destroy();
      }
    }
  }
  
  public DynAny copy()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    checkInitAny();
    try
    {
      return DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, true);
    }
    catch (InconsistentTypeCode localInconsistentTypeCode) {}
    return null;
  }
  
  public void insert_boolean(boolean paramBoolean)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_boolean(paramBoolean);
  }
  
  public void insert_octet(byte paramByte)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_octet(paramByte);
  }
  
  public void insert_char(char paramChar)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_char(paramChar);
  }
  
  public void insert_short(short paramShort)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_short(paramShort);
  }
  
  public void insert_ushort(short paramShort)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_ushort(paramShort);
  }
  
  public void insert_long(int paramInt)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_long(paramInt);
  }
  
  public void insert_ulong(int paramInt)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_ulong(paramInt);
  }
  
  public void insert_float(float paramFloat)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_float(paramFloat);
  }
  
  public void insert_double(double paramDouble)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_double(paramDouble);
  }
  
  public void insert_string(String paramString)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_string(paramString);
  }
  
  public void insert_reference(org.omg.CORBA.Object paramObject)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_reference(paramObject);
  }
  
  public void insert_typecode(TypeCode paramTypeCode)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_typecode(paramTypeCode);
  }
  
  public void insert_longlong(long paramLong)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_longlong(paramLong);
  }
  
  public void insert_ulonglong(long paramLong)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_ulonglong(paramLong);
  }
  
  public void insert_wchar(char paramChar)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_wchar(paramChar);
  }
  
  public void insert_wstring(String paramString)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_wstring(paramString);
  }
  
  public void insert_any(Any paramAny)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_any(paramAny);
  }
  
  public void insert_dyn_any(DynAny paramDynAny)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_dyn_any(paramDynAny);
  }
  
  public void insert_val(Serializable paramSerializable)
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    localDynAny.insert_val(paramSerializable);
  }
  
  public Serializable get_val()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_val();
  }
  
  public boolean get_boolean()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_boolean();
  }
  
  public byte get_octet()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_octet();
  }
  
  public char get_char()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_char();
  }
  
  public short get_short()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_short();
  }
  
  public short get_ushort()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_ushort();
  }
  
  public int get_long()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_long();
  }
  
  public int get_ulong()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_ulong();
  }
  
  public float get_float()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_float();
  }
  
  public double get_double()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_double();
  }
  
  public String get_string()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_string();
  }
  
  public org.omg.CORBA.Object get_reference()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_reference();
  }
  
  public TypeCode get_typecode()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_typecode();
  }
  
  public long get_longlong()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_longlong();
  }
  
  public long get_ulonglong()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_ulonglong();
  }
  
  public char get_wchar()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_wchar();
  }
  
  public String get_wstring()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_wstring();
  }
  
  public Any get_any()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_any();
  }
  
  public DynAny get_dyn_any()
    throws TypeMismatch, InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (this.index == -1) {
      throw new InvalidValue();
    }
    DynAny localDynAny = current_component();
    if (DynAnyUtil.isConstructedDynAny(localDynAny)) {
      throw new TypeMismatch();
    }
    return localDynAny.get_dyn_any();
  }
}
