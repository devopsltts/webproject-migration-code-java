package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpEngineParameters
  implements Serializable
{
  private static final long serialVersionUID = 3720556613478400808L;
  private UserAcl uacl = null;
  private String securityFile = null;
  private boolean encrypt = false;
  private SnmpEngineId engineId = null;
  
  public SnmpEngineParameters() {}
  
  public void setSecurityFile(String paramString)
  {
    this.securityFile = paramString;
  }
  
  public String getSecurityFile()
  {
    return this.securityFile;
  }
  
  public void setUserAcl(UserAcl paramUserAcl)
  {
    this.uacl = paramUserAcl;
  }
  
  public UserAcl getUserAcl()
  {
    return this.uacl;
  }
  
  public void activateEncryption()
  {
    this.encrypt = true;
  }
  
  public void deactivateEncryption()
  {
    this.encrypt = false;
  }
  
  public boolean isEncryptionEnabled()
  {
    return this.encrypt;
  }
  
  public void setEngineId(SnmpEngineId paramSnmpEngineId)
  {
    this.engineId = paramSnmpEngineId;
  }
  
  public SnmpEngineId getEngineId()
  {
    return this.engineId;
  }
}
