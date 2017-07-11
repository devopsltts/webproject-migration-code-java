package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SnmpErrorHandlerAgent
  extends SnmpMibAgent
  implements Serializable
{
  private static final long serialVersionUID = 7751082923508885650L;
  
  public SnmpErrorHandlerAgent() {}
  
  public void init()
    throws IllegalAccessException
  {}
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    return paramObjectName;
  }
  
  public long[] getRootOid()
  {
    return null;
  }
  
  public void get(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "get", "Get in Exception");
    if (paramSnmpMibRequest.getVersion() == 0) {
      throw new SnmpStatusException(2);
    }
    Enumeration localEnumeration = paramSnmpMibRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      localSnmpVarBind.setNoSuchObject();
    }
  }
  
  public void check(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "check", "Check in Exception");
    throw new SnmpStatusException(17);
  }
  
  public void set(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "set", "Set in Exception, CANNOT be called");
    throw new SnmpStatusException(17);
  }
  
  public void getNext(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "getNext", "GetNext in Exception");
    if (paramSnmpMibRequest.getVersion() == 0) {
      throw new SnmpStatusException(2);
    }
    Enumeration localEnumeration = paramSnmpMibRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      localSnmpVarBind.setEndOfMibView();
    }
  }
  
  public void getBulk(SnmpMibRequest paramSnmpMibRequest, int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "getBulk", "GetBulk in Exception");
    if (paramSnmpMibRequest.getVersion() == 0) {
      throw new SnmpStatusException(5, 0);
    }
    Enumeration localEnumeration = paramSnmpMibRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      localSnmpVarBind.setEndOfMibView();
    }
  }
}
