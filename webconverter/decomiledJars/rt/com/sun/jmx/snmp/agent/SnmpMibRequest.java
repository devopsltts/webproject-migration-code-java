package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Vector;

public abstract interface SnmpMibRequest
{
  public abstract Enumeration<SnmpVarBind> getElements();
  
  public abstract Vector<SnmpVarBind> getSubList();
  
  public abstract int getVersion();
  
  public abstract int getRequestPduVersion();
  
  public abstract SnmpEngine getEngine();
  
  public abstract String getPrincipal();
  
  public abstract int getSecurityLevel();
  
  public abstract int getSecurityModel();
  
  public abstract byte[] getContextName();
  
  public abstract byte[] getAccessContextName();
  
  public abstract Object getUserData();
  
  public abstract int getVarIndex(SnmpVarBind paramSnmpVarBind);
  
  public abstract void addVarBind(SnmpVarBind paramSnmpVarBind);
  
  public abstract int getSize();
  
  public abstract SnmpPdu getPdu();
}
