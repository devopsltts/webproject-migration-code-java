package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.util.ByteListImpl;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class HexBinaryDV
  extends TypeValidator
{
  public HexBinaryDV() {}
  
  public short getAllowedFacets()
  {
    return 2079;
  }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    byte[] arrayOfByte = HexBin.decode(paramString);
    if (arrayOfByte == null) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "hexBinary" });
    }
    return new XHex(arrayOfByte);
  }
  
  public int getDataLength(Object paramObject)
  {
    return ((XHex)paramObject).getLength();
  }
  
  private static final class XHex
    extends ByteListImpl
  {
    public XHex(byte[] paramArrayOfByte)
    {
      super();
    }
    
    public synchronized String toString()
    {
      if (this.canonical == null) {
        this.canonical = HexBin.encode(this.data);
      }
      return this.canonical;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof XHex)) {
        return false;
      }
      byte[] arrayOfByte = ((XHex)paramObject).data;
      int i = this.data.length;
      if (i != arrayOfByte.length) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (this.data[j] != arrayOfByte[j]) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      int i = 0;
      for (int j = 0; j < this.data.length; j++) {
        i = i * 37 + (this.data[j] & 0xFF);
      }
      return i;
    }
  }
}
