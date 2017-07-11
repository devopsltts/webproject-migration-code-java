package com.sun.jmx.snmp;

import java.security.BasicPermission;

public class SnmpPermission
  extends BasicPermission
{
  public SnmpPermission(String paramString)
  {
    super(paramString);
  }
  
  public SnmpPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}
