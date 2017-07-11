package com.sun.jmx.snmp;

public class SnmpPduRequest
  extends SnmpPduPacket
  implements SnmpPduRequestType
{
  private static final long serialVersionUID = 2218754017025258979L;
  public int errorStatus = 0;
  public int errorIndex = 0;
  
  public SnmpPduRequest() {}
  
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
    SnmpPduRequest localSnmpPduRequest = new SnmpPduRequest();
    localSnmpPduRequest.address = this.address;
    localSnmpPduRequest.port = this.port;
    localSnmpPduRequest.version = this.version;
    localSnmpPduRequest.community = this.community;
    localSnmpPduRequest.type = 162;
    localSnmpPduRequest.requestId = this.requestId;
    localSnmpPduRequest.errorStatus = 0;
    localSnmpPduRequest.errorIndex = 0;
    return localSnmpPduRequest;
  }
}
