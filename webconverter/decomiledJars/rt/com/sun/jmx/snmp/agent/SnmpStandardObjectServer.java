package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;

public class SnmpStandardObjectServer
  implements Serializable
{
  private static final long serialVersionUID = -4641068116505308488L;
  
  public SnmpStandardObjectServer() {}
  
  public void get(SnmpStandardMetaServer paramSnmpStandardMetaServer, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    Object localObject = paramSnmpMibSubRequest.getUserData();
    Enumeration localEnumeration = paramSnmpMibSubRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      try
      {
        long l = localSnmpVarBind.oid.getOidArc(paramInt);
        localSnmpVarBind.value = paramSnmpStandardMetaServer.get(l, localObject);
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        paramSnmpMibSubRequest.registerGetException(localSnmpVarBind, localSnmpStatusException);
      }
    }
  }
  
  public void set(SnmpStandardMetaServer paramSnmpStandardMetaServer, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    Object localObject = paramSnmpMibSubRequest.getUserData();
    Enumeration localEnumeration = paramSnmpMibSubRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      try
      {
        long l = localSnmpVarBind.oid.getOidArc(paramInt);
        localSnmpVarBind.value = paramSnmpStandardMetaServer.set(localSnmpVarBind.value, l, localObject);
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        paramSnmpMibSubRequest.registerSetException(localSnmpVarBind, localSnmpStatusException);
      }
    }
  }
  
  public void check(SnmpStandardMetaServer paramSnmpStandardMetaServer, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    Object localObject = paramSnmpMibSubRequest.getUserData();
    Enumeration localEnumeration = paramSnmpMibSubRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      try
      {
        long l = localSnmpVarBind.oid.getOidArc(paramInt);
        paramSnmpStandardMetaServer.check(localSnmpVarBind.value, l, localObject);
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        paramSnmpMibSubRequest.registerCheckException(localSnmpVarBind, localSnmpStatusException);
      }
    }
  }
}
