package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.util.Vector;

public abstract class SnmpMsg
  implements SnmpDefinitions
{
  public int version = 0;
  public byte[] data = null;
  public int dataLength = 0;
  public InetAddress address = null;
  public int port = 0;
  public SnmpSecurityParameters securityParameters = null;
  
  public SnmpMsg() {}
  
  public static int getProtocolVersion(byte[] paramArrayOfByte)
    throws SnmpStatusException
  {
    int i = 0;
    BerDecoder localBerDecoder = null;
    try
    {
      localBerDecoder = new BerDecoder(paramArrayOfByte);
      localBerDecoder.openSequence();
      i = localBerDecoder.fetchInteger();
    }
    catch (BerException localBerException1)
    {
      throw new SnmpStatusException("Invalid encoding");
    }
    try
    {
      localBerDecoder.closeSequence();
    }
    catch (BerException localBerException2) {}
    return i;
  }
  
  public abstract int getRequestId(byte[] paramArrayOfByte)
    throws SnmpStatusException;
  
  public abstract int encodeMessage(byte[] paramArrayOfByte)
    throws SnmpTooBigException;
  
  public abstract void decodeMessage(byte[] paramArrayOfByte, int paramInt)
    throws SnmpStatusException;
  
  public abstract void encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException;
  
  public abstract SnmpPdu decodeSnmpPdu()
    throws SnmpStatusException;
  
  public static String dumpHexBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramInt2 << 1);
    int i = 1;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++)
    {
      int m = paramArrayOfByte[k] & 0xFF;
      localStringBuffer.append(Character.forDigit(m >>> 4, 16));
      localStringBuffer.append(Character.forDigit(m & 0xF, 16));
      i++;
      if (i % 16 == 0)
      {
        localStringBuffer.append('\n');
        i = 1;
      }
      else
      {
        localStringBuffer.append(' ');
      }
    }
    return localStringBuffer.toString();
  }
  
  public String printMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("Version: ");
    localStringBuffer.append(this.version);
    localStringBuffer.append("\n");
    if (this.data == null)
    {
      localStringBuffer.append("Data: null");
    }
    else
    {
      localStringBuffer.append("Data: {\n");
      localStringBuffer.append(dumpHexBuffer(this.data, 0, this.dataLength));
      localStringBuffer.append("\n}\n");
    }
    return localStringBuffer.toString();
  }
  
  public void encodeVarBindList(BerEncoder paramBerEncoder, SnmpVarBind[] paramArrayOfSnmpVarBind)
    throws SnmpStatusException, SnmpTooBigException
  {
    int i = 0;
    try
    {
      paramBerEncoder.openSequence();
      if (paramArrayOfSnmpVarBind != null) {
        for (int j = paramArrayOfSnmpVarBind.length - 1; j >= 0; j--)
        {
          SnmpVarBind localSnmpVarBind = paramArrayOfSnmpVarBind[j];
          if (localSnmpVarBind != null)
          {
            paramBerEncoder.openSequence();
            encodeVarBindValue(paramBerEncoder, localSnmpVarBind.value);
            paramBerEncoder.putOid(localSnmpVarBind.oid.longValue());
            paramBerEncoder.closeSequence();
            i++;
          }
        }
      }
      paramBerEncoder.closeSequence();
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new SnmpTooBigException(i);
    }
  }
  
  void encodeVarBindValue(BerEncoder paramBerEncoder, SnmpValue paramSnmpValue)
    throws SnmpStatusException
  {
    if (paramSnmpValue == null)
    {
      paramBerEncoder.putNull();
    }
    else if ((paramSnmpValue instanceof SnmpIpAddress))
    {
      paramBerEncoder.putOctetString(((SnmpIpAddress)paramSnmpValue).byteValue(), 64);
    }
    else if ((paramSnmpValue instanceof SnmpCounter))
    {
      paramBerEncoder.putInteger(((SnmpCounter)paramSnmpValue).longValue(), 65);
    }
    else if ((paramSnmpValue instanceof SnmpGauge))
    {
      paramBerEncoder.putInteger(((SnmpGauge)paramSnmpValue).longValue(), 66);
    }
    else if ((paramSnmpValue instanceof SnmpTimeticks))
    {
      paramBerEncoder.putInteger(((SnmpTimeticks)paramSnmpValue).longValue(), 67);
    }
    else if ((paramSnmpValue instanceof SnmpOpaque))
    {
      paramBerEncoder.putOctetString(((SnmpOpaque)paramSnmpValue).byteValue(), 68);
    }
    else if ((paramSnmpValue instanceof SnmpInt))
    {
      paramBerEncoder.putInteger(((SnmpInt)paramSnmpValue).intValue());
    }
    else if ((paramSnmpValue instanceof SnmpString))
    {
      paramBerEncoder.putOctetString(((SnmpString)paramSnmpValue).byteValue());
    }
    else if ((paramSnmpValue instanceof SnmpOid))
    {
      paramBerEncoder.putOid(((SnmpOid)paramSnmpValue).longValue());
    }
    else if ((paramSnmpValue instanceof SnmpCounter64))
    {
      if (this.version == 0) {
        throw new SnmpStatusException("Invalid value for SNMP v1 : " + paramSnmpValue);
      }
      paramBerEncoder.putInteger(((SnmpCounter64)paramSnmpValue).longValue(), 70);
    }
    else if ((paramSnmpValue instanceof SnmpNull))
    {
      int i = ((SnmpNull)paramSnmpValue).getTag();
      if ((this.version == 0) && (i != 5)) {
        throw new SnmpStatusException("Invalid value for SNMP v1 : " + paramSnmpValue);
      }
      if ((this.version == 1) && (i != 5) && (i != 128) && (i != 129) && (i != 130)) {
        throw new SnmpStatusException("Invalid value " + paramSnmpValue);
      }
      paramBerEncoder.putNull(i);
    }
    else
    {
      throw new SnmpStatusException("Invalid value " + paramSnmpValue);
    }
  }
  
  public SnmpVarBind[] decodeVarBindList(BerDecoder paramBerDecoder)
    throws BerException
  {
    paramBerDecoder.openSequence();
    Vector localVector = new Vector();
    while (paramBerDecoder.cannotCloseSequence())
    {
      localObject = new SnmpVarBind();
      paramBerDecoder.openSequence();
      ((SnmpVarBind)localObject).oid = new SnmpOid(paramBerDecoder.fetchOid());
      ((SnmpVarBind)localObject).setSnmpValue(decodeVarBindValue(paramBerDecoder));
      paramBerDecoder.closeSequence();
      localVector.addElement(localObject);
    }
    paramBerDecoder.closeSequence();
    Object localObject = new SnmpVarBind[localVector.size()];
    localVector.copyInto((Object[])localObject);
    return localObject;
  }
  
  SnmpValue decodeVarBindValue(BerDecoder paramBerDecoder)
    throws BerException
  {
    Object localObject = null;
    int i = paramBerDecoder.getTag();
    switch (i)
    {
    case 2: 
      try
      {
        localObject = new SnmpInt(paramBerDecoder.fetchInteger());
      }
      catch (RuntimeException localRuntimeException1)
      {
        throw new BerException();
      }
    case 4: 
      try
      {
        localObject = new SnmpString(paramBerDecoder.fetchOctetString());
      }
      catch (RuntimeException localRuntimeException2)
      {
        throw new BerException();
      }
    case 6: 
      try
      {
        localObject = new SnmpOid(paramBerDecoder.fetchOid());
      }
      catch (RuntimeException localRuntimeException3)
      {
        throw new BerException();
      }
    case 5: 
      paramBerDecoder.fetchNull();
      try
      {
        localObject = new SnmpNull();
      }
      catch (RuntimeException localRuntimeException4)
      {
        throw new BerException();
      }
    case 64: 
      try
      {
        localObject = new SnmpIpAddress(paramBerDecoder.fetchOctetString(i));
      }
      catch (RuntimeException localRuntimeException5)
      {
        throw new BerException();
      }
    case 65: 
      try
      {
        localObject = new SnmpCounter(paramBerDecoder.fetchIntegerAsLong(i));
      }
      catch (RuntimeException localRuntimeException6)
      {
        throw new BerException();
      }
    case 66: 
      try
      {
        localObject = new SnmpGauge(paramBerDecoder.fetchIntegerAsLong(i));
      }
      catch (RuntimeException localRuntimeException7)
      {
        throw new BerException();
      }
    case 67: 
      try
      {
        localObject = new SnmpTimeticks(paramBerDecoder.fetchIntegerAsLong(i));
      }
      catch (RuntimeException localRuntimeException8)
      {
        throw new BerException();
      }
    case 68: 
      try
      {
        localObject = new SnmpOpaque(paramBerDecoder.fetchOctetString(i));
      }
      catch (RuntimeException localRuntimeException9)
      {
        throw new BerException();
      }
    case 70: 
      if (this.version == 0) {
        throw new BerException(1);
      }
      try
      {
        localObject = new SnmpCounter64(paramBerDecoder.fetchIntegerAsLong(i));
      }
      catch (RuntimeException localRuntimeException10)
      {
        throw new BerException();
      }
    case 128: 
      if (this.version == 0) {
        throw new BerException(1);
      }
      paramBerDecoder.fetchNull(i);
      localObject = SnmpVarBind.noSuchObject;
      break;
    case 129: 
      if (this.version == 0) {
        throw new BerException(1);
      }
      paramBerDecoder.fetchNull(i);
      localObject = SnmpVarBind.noSuchInstance;
      break;
    case 130: 
      if (this.version == 0) {
        throw new BerException(1);
      }
      paramBerDecoder.fetchNull(i);
      localObject = SnmpVarBind.endOfMibView;
      break;
    default: 
      throw new BerException();
    }
    return localObject;
  }
}
