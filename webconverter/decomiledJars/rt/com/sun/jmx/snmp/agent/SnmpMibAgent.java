package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.ServiceNotFoundException;

public abstract class SnmpMibAgent
  implements SnmpMibAgentMBean, MBeanRegistration, Serializable
{
  protected String mibName;
  protected MBeanServer server;
  private ObjectName adaptorName;
  private transient SnmpMibHandler adaptor;
  
  public SnmpMibAgent() {}
  
  public abstract void init()
    throws IllegalAccessException;
  
  public abstract ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception;
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister()
    throws Exception
  {}
  
  public void postDeregister() {}
  
  public abstract void get(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException;
  
  public abstract void getNext(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException;
  
  public abstract void getBulk(SnmpMibRequest paramSnmpMibRequest, int paramInt1, int paramInt2)
    throws SnmpStatusException;
  
  public abstract void set(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException;
  
  public abstract void check(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException;
  
  public abstract long[] getRootOid();
  
  public MBeanServer getMBeanServer()
  {
    return this.server;
  }
  
  public SnmpMibHandler getSnmpAdaptor()
  {
    return this.adaptor;
  }
  
  public void setSnmpAdaptor(SnmpMibHandler paramSnmpMibHandler)
  {
    if (this.adaptor != null) {
      this.adaptor.removeMib(this);
    }
    this.adaptor = paramSnmpMibHandler;
    if (this.adaptor != null) {
      this.adaptor.addMib(this);
    }
  }
  
  public void setSnmpAdaptor(SnmpMibHandler paramSnmpMibHandler, SnmpOid[] paramArrayOfSnmpOid)
  {
    if (this.adaptor != null) {
      this.adaptor.removeMib(this);
    }
    this.adaptor = paramSnmpMibHandler;
    if (this.adaptor != null) {
      this.adaptor.addMib(this, paramArrayOfSnmpOid);
    }
  }
  
  public void setSnmpAdaptor(SnmpMibHandler paramSnmpMibHandler, String paramString)
  {
    if (this.adaptor != null) {
      this.adaptor.removeMib(this, paramString);
    }
    this.adaptor = paramSnmpMibHandler;
    if (this.adaptor != null) {
      this.adaptor.addMib(this, paramString);
    }
  }
  
  public void setSnmpAdaptor(SnmpMibHandler paramSnmpMibHandler, String paramString, SnmpOid[] paramArrayOfSnmpOid)
  {
    if (this.adaptor != null) {
      this.adaptor.removeMib(this, paramString);
    }
    this.adaptor = paramSnmpMibHandler;
    if (this.adaptor != null) {
      this.adaptor.addMib(this, paramString, paramArrayOfSnmpOid);
    }
  }
  
  public ObjectName getSnmpAdaptorName()
  {
    return this.adaptorName;
  }
  
  public void setSnmpAdaptorName(ObjectName paramObjectName)
    throws InstanceNotFoundException, ServiceNotFoundException
  {
    if (this.server == null) {
      throw new ServiceNotFoundException(this.mibName + " is not registered in the MBean server");
    }
    if (this.adaptor != null) {
      this.adaptor.removeMib(this);
    }
    Object[] arrayOfObject = { this };
    String[] arrayOfString = { "com.sun.jmx.snmp.agent.SnmpMibAgent" };
    try
    {
      this.adaptor = ((SnmpMibHandler)this.server.invoke(paramObjectName, "addMib", arrayOfObject, arrayOfString));
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new InstanceNotFoundException(paramObjectName.toString());
    }
    catch (ReflectionException localReflectionException)
    {
      throw new ServiceNotFoundException(paramObjectName.toString());
    }
    catch (MBeanException localMBeanException) {}
    this.adaptorName = paramObjectName;
  }
  
  public void setSnmpAdaptorName(ObjectName paramObjectName, SnmpOid[] paramArrayOfSnmpOid)
    throws InstanceNotFoundException, ServiceNotFoundException
  {
    if (this.server == null) {
      throw new ServiceNotFoundException(this.mibName + " is not registered in the MBean server");
    }
    if (this.adaptor != null) {
      this.adaptor.removeMib(this);
    }
    Object[] arrayOfObject = { this, paramArrayOfSnmpOid };
    String[] arrayOfString = { "com.sun.jmx.snmp.agent.SnmpMibAgent", paramArrayOfSnmpOid.getClass().getName() };
    try
    {
      this.adaptor = ((SnmpMibHandler)this.server.invoke(paramObjectName, "addMib", arrayOfObject, arrayOfString));
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new InstanceNotFoundException(paramObjectName.toString());
    }
    catch (ReflectionException localReflectionException)
    {
      throw new ServiceNotFoundException(paramObjectName.toString());
    }
    catch (MBeanException localMBeanException) {}
    this.adaptorName = paramObjectName;
  }
  
  public void setSnmpAdaptorName(ObjectName paramObjectName, String paramString)
    throws InstanceNotFoundException, ServiceNotFoundException
  {
    if (this.server == null) {
      throw new ServiceNotFoundException(this.mibName + " is not registered in the MBean server");
    }
    if (this.adaptor != null) {
      this.adaptor.removeMib(this, paramString);
    }
    Object[] arrayOfObject = { this, paramString };
    String[] arrayOfString = { "com.sun.jmx.snmp.agent.SnmpMibAgent", "java.lang.String" };
    try
    {
      this.adaptor = ((SnmpMibHandler)this.server.invoke(paramObjectName, "addMib", arrayOfObject, arrayOfString));
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new InstanceNotFoundException(paramObjectName.toString());
    }
    catch (ReflectionException localReflectionException)
    {
      throw new ServiceNotFoundException(paramObjectName.toString());
    }
    catch (MBeanException localMBeanException) {}
    this.adaptorName = paramObjectName;
  }
  
  public void setSnmpAdaptorName(ObjectName paramObjectName, String paramString, SnmpOid[] paramArrayOfSnmpOid)
    throws InstanceNotFoundException, ServiceNotFoundException
  {
    if (this.server == null) {
      throw new ServiceNotFoundException(this.mibName + " is not registered in the MBean server");
    }
    if (this.adaptor != null) {
      this.adaptor.removeMib(this, paramString);
    }
    Object[] arrayOfObject = { this, paramString, paramArrayOfSnmpOid };
    String[] arrayOfString = { "com.sun.jmx.snmp.agent.SnmpMibAgent", "java.lang.String", paramArrayOfSnmpOid.getClass().getName() };
    try
    {
      this.adaptor = ((SnmpMibHandler)this.server.invoke(paramObjectName, "addMib", arrayOfObject, arrayOfString));
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new InstanceNotFoundException(paramObjectName.toString());
    }
    catch (ReflectionException localReflectionException)
    {
      throw new ServiceNotFoundException(paramObjectName.toString());
    }
    catch (MBeanException localMBeanException) {}
    this.adaptorName = paramObjectName;
  }
  
  public boolean getBindingState()
  {
    return this.adaptor != null;
  }
  
  public String getMibName()
  {
    return this.mibName;
  }
  
  public static SnmpMibRequest newMibRequest(SnmpPdu paramSnmpPdu, Vector<SnmpVarBind> paramVector, int paramInt, Object paramObject)
  {
    return new SnmpMibRequestImpl(null, paramSnmpPdu, paramVector, paramInt, paramObject, null, 0, getSecurityModel(paramInt), null, null);
  }
  
  public static SnmpMibRequest newMibRequest(SnmpEngine paramSnmpEngine, SnmpPdu paramSnmpPdu, Vector<SnmpVarBind> paramVector, int paramInt1, Object paramObject, String paramString, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    return new SnmpMibRequestImpl(paramSnmpEngine, paramSnmpPdu, paramVector, paramInt1, paramObject, paramString, paramInt2, paramInt3, paramArrayOfByte1, paramArrayOfByte2);
  }
  
  void getBulkWithGetNext(SnmpMibRequest paramSnmpMibRequest, int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    Vector localVector1 = paramSnmpMibRequest.getSubList();
    int i = localVector1.size();
    int j = Math.max(Math.min(paramInt1, i), 0);
    int k = Math.max(paramInt2, 0);
    int m = i - j;
    if (i != 0)
    {
      getNext(paramSnmpMibRequest);
      Vector localVector2 = splitFrom(localVector1, j);
      SnmpMibRequestImpl localSnmpMibRequestImpl = new SnmpMibRequestImpl(paramSnmpMibRequest.getEngine(), paramSnmpMibRequest.getPdu(), localVector2, 1, paramSnmpMibRequest.getUserData(), paramSnmpMibRequest.getPrincipal(), paramSnmpMibRequest.getSecurityLevel(), paramSnmpMibRequest.getSecurityModel(), paramSnmpMibRequest.getContextName(), paramSnmpMibRequest.getAccessContextName());
      for (int n = 2; n <= k; n++)
      {
        getNext(localSnmpMibRequestImpl);
        concatVector(paramSnmpMibRequest, localVector2);
      }
    }
  }
  
  private Vector<SnmpVarBind> splitFrom(Vector<SnmpVarBind> paramVector, int paramInt)
  {
    int i = paramVector.size();
    Vector localVector = new Vector(i - paramInt);
    int j = paramInt;
    Enumeration localEnumeration = paramVector.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      if (j <= 0) {
        localVector.addElement(new SnmpVarBind(localSnmpVarBind.oid, localSnmpVarBind.value));
      }
      j--;
    }
    return localVector;
  }
  
  private void concatVector(SnmpMibRequest paramSnmpMibRequest, Vector<SnmpVarBind> paramVector)
  {
    Enumeration localEnumeration = paramVector.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      paramSnmpMibRequest.addVarBind(new SnmpVarBind(localSnmpVarBind.oid, localSnmpVarBind.value));
    }
  }
  
  private static int getSecurityModel(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 1;
    }
    return 2;
  }
}
