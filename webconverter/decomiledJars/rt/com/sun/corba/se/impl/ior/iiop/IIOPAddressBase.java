package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import org.omg.CORBA_2_3.portable.OutputStream;

abstract class IIOPAddressBase
  implements IIOPAddress
{
  IIOPAddressBase() {}
  
  protected short intToShort(int paramInt)
  {
    if (paramInt > 32767) {
      return (short)(paramInt - 65536);
    }
    return (short)paramInt;
  }
  
  protected int shortToInt(short paramShort)
  {
    if (paramShort < 0) {
      return paramShort + 65536;
    }
    return paramShort;
  }
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_string(getHost());
    int i = getPort();
    paramOutputStream.write_short(intToShort(i));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof IIOPAddress)) {
      return false;
    }
    IIOPAddress localIIOPAddress = (IIOPAddress)paramObject;
    return (getHost().equals(localIIOPAddress.getHost())) && (getPort() == localIIOPAddress.getPort());
  }
  
  public int hashCode()
  {
    return getHost().hashCode() ^ getPort();
  }
  
  public String toString()
  {
    return "IIOPAddress[" + getHost() + "," + getPort() + "]";
  }
}
