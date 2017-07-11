package com.sun.jmx.snmp;

public class SnmpScopedPduRequest
  extends SnmpScopedPduPacket
  implements SnmpPduRequestType
{
  private static final long serialVersionUID = 6463060973056773680L;
  int errorStatus = 0;
  int errorIndex = 0;
  
  public SnmpScopedPduRequest() {}
  
  public void setErrorIndex(int paramInt)
  {
    this.errorIndex = paramInt;
  }
  
  public void setErrorStatus(int paramInt)
  {
    this.errorStatus = paramInt;
  }
  
  public int getErrorIndex()
  {
    return this.errorIndex;
  }
  
  public int getErrorStatus()
  {
    return this.errorStatus;
  }
  
  public SnmpPdu getResponsePdu()
  {
    SnmpScopedPduRequest localSnmpScopedPduRequest = new SnmpScopedPduRequest();
    localSnmpScopedPduRequest.address = this.address;
    localSnmpScopedPduRequest.port = this.port;
    localSnmpScopedPduRequest.version = this.version;
    localSnmpScopedPduRequest.requestId = this.requestId;
    localSnmpScopedPduRequest.msgId = this.msgId;
    localSnmpScopedPduRequest.msgMaxSize = this.msgMaxSize;
    localSnmpScopedPduRequest.msgFlags = this.msgFlags;
    localSnmpScopedPduRequest.msgSecurityModel = this.msgSecurityModel;
    localSnmpScopedPduRequest.contextEngineId = this.contextEngineId;
    localSnmpScopedPduRequest.contextName = this.contextName;
    localSnmpScopedPduRequest.securityParameters = this.securityParameters;
    localSnmpScopedPduRequest.type = 162;
    localSnmpScopedPduRequest.errorStatus = 0;
    localSnmpScopedPduRequest.errorIndex = 0;
    return localSnmpScopedPduRequest;
  }
}
