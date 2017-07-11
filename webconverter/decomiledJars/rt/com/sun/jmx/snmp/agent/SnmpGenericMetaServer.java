package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;

public abstract interface SnmpGenericMetaServer
{
  public abstract Object buildAttributeValue(long paramLong, SnmpValue paramSnmpValue)
    throws SnmpStatusException;
  
  public abstract SnmpValue buildSnmpValue(long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  public abstract String getAttributeName(long paramLong)
    throws SnmpStatusException;
  
  public abstract void checkSetAccess(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  public abstract void checkGetAccess(long paramLong, Object paramObject)
    throws SnmpStatusException;
}
