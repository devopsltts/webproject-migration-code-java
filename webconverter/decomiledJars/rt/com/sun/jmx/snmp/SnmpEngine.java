package com.sun.jmx.snmp;

public abstract interface SnmpEngine
{
  public abstract int getEngineTime();
  
  public abstract SnmpEngineId getEngineId();
  
  public abstract int getEngineBoots();
  
  public abstract SnmpUsmKeyHandler getUsmKeyHandler();
}
