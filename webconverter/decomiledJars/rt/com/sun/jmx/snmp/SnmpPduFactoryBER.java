package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpPduFactoryBER
  implements SnmpPduFactory, Serializable
{
  private static final long serialVersionUID = -3525318344000547635L;
  
  public SnmpPduFactoryBER() {}
  
  public SnmpPdu decodeSnmpPdu(SnmpMsg paramSnmpMsg)
    throws SnmpStatusException
  {
    return paramSnmpMsg.decodeSnmpPdu();
  }
  
  public SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException
  {
    Object localObject;
    switch (paramSnmpPdu.version)
    {
    case 0: 
    case 1: 
      localObject = new SnmpMessage();
      ((SnmpMessage)localObject).encodeSnmpPdu((SnmpPduPacket)paramSnmpPdu, paramInt);
      return localObject;
    case 3: 
      localObject = new SnmpV3Message();
      ((SnmpV3Message)localObject).encodeSnmpPdu(paramSnmpPdu, paramInt);
      return localObject;
    }
    return null;
  }
}
