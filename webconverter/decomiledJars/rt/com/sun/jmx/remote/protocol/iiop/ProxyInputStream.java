package com.sun.jmx.remote.protocol.iiop;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;

public class ProxyInputStream
  extends org.omg.CORBA_2_3.portable.InputStream
{
  protected final org.omg.CORBA.portable.InputStream in;
  
  public ProxyInputStream(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    this.in = paramInputStream;
  }
  
  public boolean read_boolean()
  {
    return this.in.read_boolean();
  }
  
  public char read_char()
  {
    return this.in.read_char();
  }
  
  public char read_wchar()
  {
    return this.in.read_wchar();
  }
  
  public byte read_octet()
  {
    return this.in.read_octet();
  }
  
  public short read_short()
  {
    return this.in.read_short();
  }
  
  public short read_ushort()
  {
    return this.in.read_ushort();
  }
  
  public int read_long()
  {
    return this.in.read_long();
  }
  
  public int read_ulong()
  {
    return this.in.read_ulong();
  }
  
  public long read_longlong()
  {
    return this.in.read_longlong();
  }
  
  public long read_ulonglong()
  {
    return this.in.read_ulonglong();
  }
  
  public float read_float()
  {
    return this.in.read_float();
  }
  
  public double read_double()
  {
    return this.in.read_double();
  }
  
  public String read_string()
  {
    return this.in.read_string();
  }
  
  public String read_wstring()
  {
    return this.in.read_wstring();
  }
  
  public void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    this.in.read_boolean_array(paramArrayOfBoolean, paramInt1, paramInt2);
  }
  
  public void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    this.in.read_char_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    this.in.read_wchar_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.in.read_octet_array(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    this.in.read_short_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    this.in.read_ushort_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    this.in.read_long_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    this.in.read_ulong_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    this.in.read_longlong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    this.in.read_ulonglong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    this.in.read_float_array(paramArrayOfFloat, paramInt1, paramInt2);
  }
  
  public void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    this.in.read_double_array(paramArrayOfDouble, paramInt1, paramInt2);
  }
  
  public org.omg.CORBA.Object read_Object()
  {
    return this.in.read_Object();
  }
  
  public TypeCode read_TypeCode()
  {
    return this.in.read_TypeCode();
  }
  
  public Any read_any()
  {
    return this.in.read_any();
  }
  
  @Deprecated
  public Principal read_Principal()
  {
    return this.in.read_Principal();
  }
  
  public int read()
    throws IOException
  {
    return this.in.read();
  }
  
  public BigDecimal read_fixed()
  {
    return this.in.read_fixed();
  }
  
  public Context read_Context()
  {
    return this.in.read_Context();
  }
  
  public org.omg.CORBA.Object read_Object(Class paramClass)
  {
    return this.in.read_Object(paramClass);
  }
  
  public ORB orb()
  {
    return this.in.orb();
  }
  
  public Serializable read_value()
  {
    return narrow().read_value();
  }
  
  public Serializable read_value(Class paramClass)
  {
    return narrow().read_value(paramClass);
  }
  
  public Serializable read_value(BoxedValueHelper paramBoxedValueHelper)
  {
    return narrow().read_value(paramBoxedValueHelper);
  }
  
  public Serializable read_value(String paramString)
  {
    return narrow().read_value(paramString);
  }
  
  public Serializable read_value(Serializable paramSerializable)
  {
    return narrow().read_value(paramSerializable);
  }
  
  public Object read_abstract_interface()
  {
    return narrow().read_abstract_interface();
  }
  
  public Object read_abstract_interface(Class paramClass)
  {
    return narrow().read_abstract_interface(paramClass);
  }
  
  protected org.omg.CORBA_2_3.portable.InputStream narrow()
  {
    if ((this.in instanceof org.omg.CORBA_2_3.portable.InputStream)) {
      return (org.omg.CORBA_2_3.portable.InputStream)this.in;
    }
    throw new NO_IMPLEMENT();
  }
  
  public org.omg.CORBA.portable.InputStream getProxiedInputStream()
  {
    return this.in;
  }
}
