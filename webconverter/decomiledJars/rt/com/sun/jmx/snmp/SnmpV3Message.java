package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SnmpV3Message
  extends SnmpMsg
{
  public int msgId = 0;
  public int msgMaxSize = 0;
  public byte msgFlags = 0;
  public int msgSecurityModel = 0;
  public byte[] msgSecurityParameters = null;
  public byte[] contextEngineId = null;
  public byte[] contextName = null;
  public byte[] encryptedPdu = null;
  
  public SnmpV3Message() {}
  
  public int encodeMessage(byte[] paramArrayOfByte)
    throws SnmpTooBigException
  {
    int i = 0;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "encodeMessage", "Can't encode directly V3Message! Need a SecuritySubSystem");
    }
    throw new IllegalArgumentException("Can't encode");
  }
  
  public void decodeMessage(byte[] paramArrayOfByte, int paramInt)
    throws SnmpStatusException
  {
    try
    {
      BerDecoder localBerDecoder = new BerDecoder(paramArrayOfByte);
      localBerDecoder.openSequence();
      this.version = localBerDecoder.fetchInteger();
      localBerDecoder.openSequence();
      this.msgId = localBerDecoder.fetchInteger();
      this.msgMaxSize = localBerDecoder.fetchInteger();
      this.msgFlags = localBerDecoder.fetchOctetString()[0];
      this.msgSecurityModel = localBerDecoder.fetchInteger();
      localBerDecoder.closeSequence();
      this.msgSecurityParameters = localBerDecoder.fetchOctetString();
      if ((this.msgFlags & 0x2) == 0)
      {
        localBerDecoder.openSequence();
        this.contextEngineId = localBerDecoder.fetchOctetString();
        this.contextName = localBerDecoder.fetchOctetString();
        this.data = localBerDecoder.fetchAny();
        this.dataLength = this.data.length;
        localBerDecoder.closeSequence();
      }
      else
      {
        this.encryptedPdu = localBerDecoder.fetchOctetString();
      }
      localBerDecoder.closeSequence();
    }
    catch (BerException localBerException)
    {
      localBerException.printStackTrace();
      throw new SnmpStatusException("Invalid encoding");
    }
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Unmarshalled message : \n").append("version : ").append(this.version).append("\n").append("msgId : ").append(this.msgId).append("\n").append("msgMaxSize : ").append(this.msgMaxSize).append("\n").append("msgFlags : ").append(this.msgFlags).append("\n").append("msgSecurityModel : ").append(this.msgSecurityModel).append("\n").append("contextEngineId : ").append(this.contextEngineId == null ? null : SnmpEngineId.createEngineId(this.contextEngineId)).append("\n").append("contextName : ").append(this.contextName).append("\n").append("data : ").append(this.data).append("\n").append("dat len : ").append(this.data == null ? 0 : this.data.length).append("\n").append("encryptedPdu : ").append(this.encryptedPdu).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "decodeMessage", localStringBuilder.toString());
    }
  }
  
  public int getRequestId(byte[] paramArrayOfByte)
    throws SnmpStatusException
  {
    BerDecoder localBerDecoder = null;
    int i = 0;
    try
    {
      localBerDecoder = new BerDecoder(paramArrayOfByte);
      localBerDecoder.openSequence();
      localBerDecoder.fetchInteger();
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
  
  public void encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException
  {
    SnmpScopedPduPacket localSnmpScopedPduPacket = (SnmpScopedPduPacket)paramSnmpPdu;
    Object localObject;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
    {
      localObject = new StringBuilder().append("PDU to marshall: \n").append("security parameters : ").append(localSnmpScopedPduPacket.securityParameters).append("\n").append("type : ").append(localSnmpScopedPduPacket.type).append("\n").append("version : ").append(localSnmpScopedPduPacket.version).append("\n").append("requestId : ").append(localSnmpScopedPduPacket.requestId).append("\n").append("msgId : ").append(localSnmpScopedPduPacket.msgId).append("\n").append("msgMaxSize : ").append(localSnmpScopedPduPacket.msgMaxSize).append("\n").append("msgFlags : ").append(localSnmpScopedPduPacket.msgFlags).append("\n").append("msgSecurityModel : ").append(localSnmpScopedPduPacket.msgSecurityModel).append("\n").append("contextEngineId : ").append(localSnmpScopedPduPacket.contextEngineId).append("\n").append("contextName : ").append(localSnmpScopedPduPacket.contextName).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "encodeSnmpPdu", ((StringBuilder)localObject).toString());
    }
    this.version = localSnmpScopedPduPacket.version;
    this.address = localSnmpScopedPduPacket.address;
    this.port = localSnmpScopedPduPacket.port;
    this.msgId = localSnmpScopedPduPacket.msgId;
    this.msgMaxSize = localSnmpScopedPduPacket.msgMaxSize;
    this.msgFlags = localSnmpScopedPduPacket.msgFlags;
    this.msgSecurityModel = localSnmpScopedPduPacket.msgSecurityModel;
    this.contextEngineId = localSnmpScopedPduPacket.contextEngineId;
    this.contextName = localSnmpScopedPduPacket.contextName;
    this.securityParameters = localSnmpScopedPduPacket.securityParameters;
    this.data = new byte[paramInt];
    try
    {
      localObject = new BerEncoder(this.data);
      ((BerEncoder)localObject).openSequence();
      encodeVarBindList((BerEncoder)localObject, localSnmpScopedPduPacket.varBindList);
      switch (localSnmpScopedPduPacket.type)
      {
      case 160: 
      case 161: 
      case 162: 
      case 163: 
      case 166: 
      case 167: 
      case 168: 
        SnmpPduRequestType localSnmpPduRequestType = (SnmpPduRequestType)localSnmpScopedPduPacket;
        ((BerEncoder)localObject).putInteger(localSnmpPduRequestType.getErrorIndex());
        ((BerEncoder)localObject).putInteger(localSnmpPduRequestType.getErrorStatus());
        ((BerEncoder)localObject).putInteger(localSnmpScopedPduPacket.requestId);
        break;
      case 165: 
        SnmpPduBulkType localSnmpPduBulkType = (SnmpPduBulkType)localSnmpScopedPduPacket;
        ((BerEncoder)localObject).putInteger(localSnmpPduBulkType.getMaxRepetitions());
        ((BerEncoder)localObject).putInteger(localSnmpPduBulkType.getNonRepeaters());
        ((BerEncoder)localObject).putInteger(localSnmpScopedPduPacket.requestId);
        break;
      case 164: 
      default: 
        throw new SnmpStatusException("Invalid pdu type " + String.valueOf(localSnmpScopedPduPacket.type));
      }
      ((BerEncoder)localObject).closeSequence(localSnmpScopedPduPacket.type);
      this.dataLength = ((BerEncoder)localObject).trim();
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new SnmpTooBigException();
    }
  }
  
  public SnmpPdu decodeSnmpPdu()
    throws SnmpStatusException
  {
    Object localObject = null;
    BerDecoder localBerDecoder = new BerDecoder(this.data);
    try
    {
      int i = localBerDecoder.getTag();
      localBerDecoder.openSequence(i);
      switch (i)
      {
      case 160: 
      case 161: 
      case 162: 
      case 163: 
      case 166: 
      case 167: 
      case 168: 
        SnmpScopedPduRequest localSnmpScopedPduRequest = new SnmpScopedPduRequest();
        localSnmpScopedPduRequest.requestId = localBerDecoder.fetchInteger();
        localSnmpScopedPduRequest.setErrorStatus(localBerDecoder.fetchInteger());
        localSnmpScopedPduRequest.setErrorIndex(localBerDecoder.fetchInteger());
        localObject = localSnmpScopedPduRequest;
        break;
      case 165: 
        SnmpScopedPduBulk localSnmpScopedPduBulk = new SnmpScopedPduBulk();
        localSnmpScopedPduBulk.requestId = localBerDecoder.fetchInteger();
        localSnmpScopedPduBulk.setNonRepeaters(localBerDecoder.fetchInteger());
        localSnmpScopedPduBulk.setMaxRepetitions(localBerDecoder.fetchInteger());
        localObject = localSnmpScopedPduBulk;
        break;
      case 164: 
      default: 
        throw new SnmpStatusException(9);
      }
      localObject.type = i;
      localObject.varBindList = decodeVarBindList(localBerDecoder);
      localBerDecoder.closeSequence();
    }
    catch (BerException localBerException)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpV3Message.class.getName(), "decodeSnmpPdu", "BerException", localBerException);
      }
      throw new SnmpStatusException(9);
    }
    localObject.address = this.address;
    localObject.port = this.port;
    localObject.msgFlags = this.msgFlags;
    localObject.version = this.version;
    localObject.msgId = this.msgId;
    localObject.msgMaxSize = this.msgMaxSize;
    localObject.msgSecurityModel = this.msgSecurityModel;
    localObject.contextEngineId = this.contextEngineId;
    localObject.contextName = this.contextName;
    localObject.securityParameters = this.securityParameters;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Unmarshalled PDU : \n").append("type : ").append(localObject.type).append("\n").append("version : ").append(localObject.version).append("\n").append("requestId : ").append(localObject.requestId).append("\n").append("msgId : ").append(localObject.msgId).append("\n").append("msgMaxSize : ").append(localObject.msgMaxSize).append("\n").append("msgFlags : ").append(localObject.msgFlags).append("\n").append("msgSecurityModel : ").append(localObject.msgSecurityModel).append("\n").append("contextEngineId : ").append(localObject.contextEngineId).append("\n").append("contextName : ").append(localObject.contextName).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "decodeSnmpPdu", localStringBuilder.toString());
    }
    return localObject;
  }
  
  public String printMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("msgId : " + this.msgId + "\n");
    localStringBuffer.append("msgMaxSize : " + this.msgMaxSize + "\n");
    localStringBuffer.append("msgFlags : " + this.msgFlags + "\n");
    localStringBuffer.append("msgSecurityModel : " + this.msgSecurityModel + "\n");
    if (this.contextEngineId == null)
    {
      localStringBuffer.append("contextEngineId : null");
    }
    else
    {
      localStringBuffer.append("contextEngineId : {\n");
      localStringBuffer.append(dumpHexBuffer(this.contextEngineId, 0, this.contextEngineId.length));
      localStringBuffer.append("\n}\n");
    }
    if (this.contextName == null)
    {
      localStringBuffer.append("contextName : null");
    }
    else
    {
      localStringBuffer.append("contextName : {\n");
      localStringBuffer.append(dumpHexBuffer(this.contextName, 0, this.contextName.length));
      localStringBuffer.append("\n}\n");
    }
    return super.printMessage();
  }
}
