package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.Streamable;

public final class TCUtility
{
  public TCUtility() {}
  
  static void marshalIn(org.omg.CORBA.portable.OutputStream paramOutputStream, TypeCode paramTypeCode, long paramLong, Object paramObject)
  {
    switch (paramTypeCode.kind().value())
    {
    case 0: 
    case 1: 
    case 31: 
      break;
    case 2: 
      paramOutputStream.write_short((short)(int)(paramLong & 0xFFFF));
      break;
    case 4: 
      paramOutputStream.write_ushort((short)(int)(paramLong & 0xFFFF));
      break;
    case 3: 
    case 17: 
      paramOutputStream.write_long((int)(paramLong & 0xFFFFFFFF));
      break;
    case 5: 
      paramOutputStream.write_ulong((int)(paramLong & 0xFFFFFFFF));
      break;
    case 6: 
      paramOutputStream.write_float(Float.intBitsToFloat((int)(paramLong & 0xFFFFFFFF)));
      break;
    case 7: 
      paramOutputStream.write_double(Double.longBitsToDouble(paramLong));
      break;
    case 8: 
      if (paramLong == 0L) {
        paramOutputStream.write_boolean(false);
      } else {
        paramOutputStream.write_boolean(true);
      }
      break;
    case 9: 
      paramOutputStream.write_char((char)(int)(paramLong & 0xFFFF));
      break;
    case 10: 
      paramOutputStream.write_octet((byte)(int)(paramLong & 0xFF));
      break;
    case 11: 
      paramOutputStream.write_any((Any)paramObject);
      break;
    case 12: 
      paramOutputStream.write_TypeCode((TypeCode)paramObject);
      break;
    case 13: 
      paramOutputStream.write_Principal((Principal)paramObject);
      break;
    case 14: 
      paramOutputStream.write_Object((org.omg.CORBA.Object)paramObject);
      break;
    case 23: 
      paramOutputStream.write_longlong(paramLong);
      break;
    case 24: 
      paramOutputStream.write_ulonglong(paramLong);
      break;
    case 26: 
      paramOutputStream.write_wchar((char)(int)(paramLong & 0xFFFF));
      break;
    case 18: 
      paramOutputStream.write_string((String)paramObject);
      break;
    case 27: 
      paramOutputStream.write_wstring((String)paramObject);
      break;
    case 29: 
    case 30: 
      ((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream).write_value((Serializable)paramObject);
      break;
    case 28: 
      if ((paramOutputStream instanceof CDROutputStream)) {
        try
        {
          ((CDROutputStream)paramOutputStream).write_fixed((BigDecimal)paramObject, paramTypeCode.fixed_digits(), paramTypeCode.fixed_scale());
        }
        catch (BadKind localBadKind) {}
      } else {
        paramOutputStream.write_fixed((BigDecimal)paramObject);
      }
      break;
    case 15: 
    case 16: 
    case 19: 
    case 20: 
    case 21: 
    case 22: 
      ((Streamable)paramObject)._write(paramOutputStream);
      break;
    case 32: 
      ((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream).write_abstract_interface(paramObject);
      break;
    case 25: 
    default: 
      ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get((ORB)paramOutputStream.orb(), "rpc.presentation");
      throw localORBUtilSystemException.typecodeNotSupported();
    }
  }
  
  static void unmarshalIn(org.omg.CORBA.portable.InputStream paramInputStream, TypeCode paramTypeCode, long[] paramArrayOfLong, Object[] paramArrayOfObject)
  {
    int i = paramTypeCode.kind().value();
    long l = 0L;
    Object localObject = paramArrayOfObject[0];
    switch (i)
    {
    case 0: 
    case 1: 
    case 31: 
      break;
    case 2: 
      l = paramInputStream.read_short() & 0xFFFF;
      break;
    case 4: 
      l = paramInputStream.read_ushort() & 0xFFFF;
      break;
    case 3: 
    case 17: 
      l = paramInputStream.read_long() & 0xFFFFFFFF;
      break;
    case 5: 
      l = paramInputStream.read_ulong() & 0xFFFFFFFF;
      break;
    case 6: 
      l = Float.floatToIntBits(paramInputStream.read_float()) & 0xFFFFFFFF;
      break;
    case 7: 
      l = Double.doubleToLongBits(paramInputStream.read_double());
      break;
    case 9: 
      l = paramInputStream.read_char() & 0xFFFF;
      break;
    case 10: 
      l = paramInputStream.read_octet() & 0xFF;
      break;
    case 8: 
      if (paramInputStream.read_boolean()) {
        l = 1L;
      } else {
        l = 0L;
      }
      break;
    case 11: 
      localObject = paramInputStream.read_any();
      break;
    case 12: 
      localObject = paramInputStream.read_TypeCode();
      break;
    case 13: 
      localObject = paramInputStream.read_Principal();
      break;
    case 14: 
      if ((localObject instanceof Streamable)) {
        ((Streamable)localObject)._read(paramInputStream);
      } else {
        localObject = paramInputStream.read_Object();
      }
      break;
    case 23: 
      l = paramInputStream.read_longlong();
      break;
    case 24: 
      l = paramInputStream.read_ulonglong();
      break;
    case 26: 
      l = paramInputStream.read_wchar() & 0xFFFF;
      break;
    case 18: 
      localObject = paramInputStream.read_string();
      break;
    case 27: 
      localObject = paramInputStream.read_wstring();
      break;
    case 29: 
    case 30: 
      localObject = ((org.omg.CORBA_2_3.portable.InputStream)paramInputStream).read_value();
      break;
    case 28: 
      try
      {
        if ((paramInputStream instanceof CDRInputStream))
        {
          localObject = ((CDRInputStream)paramInputStream).read_fixed(paramTypeCode.fixed_digits(), paramTypeCode.fixed_scale());
        }
        else
        {
          BigDecimal localBigDecimal = paramInputStream.read_fixed();
          localObject = localBigDecimal.movePointLeft(paramTypeCode.fixed_scale());
        }
      }
      catch (BadKind localBadKind) {}
    case 15: 
    case 16: 
    case 19: 
    case 20: 
    case 21: 
    case 22: 
      ((Streamable)localObject)._read(paramInputStream);
      break;
    case 32: 
      localObject = ((org.omg.CORBA_2_3.portable.InputStream)paramInputStream).read_abstract_interface();
      break;
    case 25: 
    default: 
      ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get((ORB)paramInputStream.orb(), "rpc.presentation");
      throw localORBUtilSystemException.typecodeNotSupported();
    }
    paramArrayOfObject[0] = localObject;
    paramArrayOfLong[0] = l;
  }
}
