package com.sun.jmx.snmp;

public abstract class SnmpParams
  implements SnmpDefinitions
{
  private int protocolVersion = 0;
  
  SnmpParams(int paramInt)
  {
    this.protocolVersion = paramInt;
  }
  
  SnmpParams() {}
  
  public abstract boolean allowSnmpSets();
  
  public int getProtocolVersion()
  {
    return this.protocolVersion;
  }
  
  public void setProtocolVersion(int paramInt)
  {
    this.protocolVersion = paramInt;
  }
}
