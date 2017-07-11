package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.ThreadContext;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibRequest;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class SnmpSubRequestHandler
  implements SnmpDefinitions, Runnable
{
  protected SnmpIncomingRequest incRequest = null;
  protected SnmpEngine engine = null;
  protected int version = 0;
  protected int type = 0;
  protected SnmpMibAgent agent;
  protected int errorStatus = 0;
  protected int errorIndex = -1;
  protected Vector<SnmpVarBind> varBind;
  protected int[] translation;
  protected Object data;
  private SnmpMibRequest mibRequest = null;
  private SnmpPdu reqPdu = null;
  
  protected SnmpSubRequestHandler(SnmpEngine paramSnmpEngine, SnmpIncomingRequest paramSnmpIncomingRequest, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu)
  {
    this(paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpEngine, paramSnmpIncomingRequest);
  }
  
  protected SnmpSubRequestHandler(SnmpEngine paramSnmpEngine, SnmpIncomingRequest paramSnmpIncomingRequest, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu, boolean paramBoolean)
  {
    this(paramSnmpMibAgent, paramSnmpPdu, paramBoolean);
    init(paramSnmpEngine, paramSnmpIncomingRequest);
  }
  
  protected SnmpSubRequestHandler(SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu)
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "constructor", "creating instance for request " + String.valueOf(paramSnmpPdu.requestId));
    }
    this.version = paramSnmpPdu.version;
    this.type = paramSnmpPdu.type;
    this.agent = paramSnmpMibAgent;
    this.reqPdu = paramSnmpPdu;
    int i = paramSnmpPdu.varBindList.length;
    this.translation = new int[i];
    this.varBind = new NonSyncVector(i);
  }
  
  protected SnmpSubRequestHandler(SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu, boolean paramBoolean)
  {
    this(paramSnmpMibAgent, paramSnmpPdu);
    int i = this.translation.length;
    SnmpVarBind[] arrayOfSnmpVarBind = paramSnmpPdu.varBindList;
    for (int j = 0; j < i; j++)
    {
      this.translation[j] = j;
      ((NonSyncVector)this.varBind).addNonSyncElement(arrayOfSnmpVarBind[j]);
    }
  }
  
  SnmpMibRequest createMibRequest(Vector<SnmpVarBind> paramVector, int paramInt, Object paramObject)
  {
    if ((this.type == 163) && (this.mibRequest != null)) {
      return this.mibRequest;
    }
    SnmpMibRequest localSnmpMibRequest = null;
    if (this.incRequest != null) {
      localSnmpMibRequest = SnmpMibAgent.newMibRequest(this.engine, this.reqPdu, paramVector, paramInt, paramObject, this.incRequest.getPrincipal(), this.incRequest.getSecurityLevel(), this.incRequest.getSecurityModel(), this.incRequest.getContextName(), this.incRequest.getAccessContext());
    } else {
      localSnmpMibRequest = SnmpMibAgent.newMibRequest(this.reqPdu, paramVector, paramInt, paramObject);
    }
    if (this.type == 253) {
      this.mibRequest = localSnmpMibRequest;
    }
    return localSnmpMibRequest;
  }
  
  void setUserData(Object paramObject)
  {
    this.data = paramObject;
  }
  
  public void run()
  {
    try
    {
      ThreadContext localThreadContext = ThreadContext.push("SnmpUserData", this.data);
      try
      {
        switch (this.type)
        {
        case 160: 
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:get operation on " + this.agent.getMibName());
          }
          this.agent.get(createMibRequest(this.varBind, this.version, this.data));
          break;
        case 161: 
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:getNext operation on " + this.agent.getMibName());
          }
          this.agent.getNext(createMibRequest(this.varBind, this.version, this.data));
          break;
        case 163: 
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:set operation on " + this.agent.getMibName());
          }
          this.agent.set(createMibRequest(this.varBind, this.version, this.data));
          break;
        case 253: 
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:check operation on " + this.agent.getMibName());
          }
          this.agent.check(createMibRequest(this.varBind, this.version, this.data));
          break;
        default: 
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:unknown operation (" + this.type + ") on " + this.agent.getMibName());
          }
          this.errorStatus = 5;
          this.errorIndex = 1;
        }
        ThreadContext.restore(localThreadContext);
      }
      finally
      {
        ThreadContext.restore(localThreadContext);
      }
      if (!JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        return;
      }
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      this.errorStatus = localSnmpStatusException.getStatus();
      this.errorIndex = localSnmpStatusException.getErrorIndex();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:an Snmp error occurred during the operation", localSnmpStatusException);
      }
    }
    catch (Exception localException)
    {
      this.errorStatus = 5;
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:a generic error occurred during the operation", localException);
      }
    }
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:operation completed");
  }
  
  static final int mapErrorStatusToV1(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0) {
      return 0;
    }
    if (paramInt1 == 5) {
      return 5;
    }
    if (paramInt1 == 2) {
      return 2;
    }
    if ((paramInt1 == 224) || (paramInt1 == 225) || (paramInt1 == 6) || (paramInt1 == 18) || (paramInt1 == 16)) {
      return 2;
    }
    if ((paramInt1 == 16) || (paramInt1 == 17))
    {
      if (paramInt2 == 253) {
        return 4;
      }
      return 2;
    }
    if (paramInt1 == 11) {
      return 2;
    }
    if ((paramInt1 == 7) || (paramInt1 == 8) || (paramInt1 == 9) || (paramInt1 == 10) || (paramInt1 == 8) || (paramInt1 == 12))
    {
      if ((paramInt2 == 163) || (paramInt2 == 253)) {
        return 3;
      }
      return 2;
    }
    if ((paramInt1 == 13) || (paramInt1 == 14) || (paramInt1 == 15)) {
      return 5;
    }
    if (paramInt1 == 1) {
      return 1;
    }
    if ((paramInt1 == 3) || (paramInt1 == 4))
    {
      if ((paramInt2 == 163) || (paramInt2 == 253)) {
        return paramInt1;
      }
      return 2;
    }
    return 5;
  }
  
  static final int mapErrorStatusToV2(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0) {
      return 0;
    }
    if (paramInt1 == 5) {
      return 5;
    }
    if (paramInt1 == 1) {
      return 1;
    }
    if ((paramInt2 != 163) && (paramInt2 != 253))
    {
      if (paramInt1 == 16) {
        return paramInt1;
      }
      return 5;
    }
    if (paramInt1 == 2) {
      return 6;
    }
    if (paramInt1 == 4) {
      return 17;
    }
    if (paramInt1 == 3) {
      return 10;
    }
    if ((paramInt1 == 6) || (paramInt1 == 18) || (paramInt1 == 16) || (paramInt1 == 17) || (paramInt1 == 11) || (paramInt1 == 7) || (paramInt1 == 8) || (paramInt1 == 9) || (paramInt1 == 10) || (paramInt1 == 8) || (paramInt1 == 12) || (paramInt1 == 13) || (paramInt1 == 14) || (paramInt1 == 15)) {
      return paramInt1;
    }
    return 5;
  }
  
  static final int mapErrorStatus(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 == 0) {
      return 0;
    }
    if (paramInt2 == 0) {
      return mapErrorStatusToV1(paramInt1, paramInt3);
    }
    if ((paramInt2 == 1) || (paramInt2 == 3)) {
      return mapErrorStatusToV2(paramInt1, paramInt3);
    }
    return 5;
  }
  
  protected int getErrorStatus()
  {
    if (this.errorStatus == 0) {
      return 0;
    }
    return mapErrorStatus(this.errorStatus, this.version, this.type);
  }
  
  protected int getErrorIndex()
  {
    if (this.errorStatus == 0) {
      return -1;
    }
    if ((this.errorIndex == 0) || (this.errorIndex == -1)) {
      this.errorIndex = 1;
    }
    return this.translation[(this.errorIndex - 1)];
  }
  
  protected void updateRequest(SnmpVarBind paramSnmpVarBind, int paramInt)
  {
    int i = this.varBind.size();
    this.translation[i] = paramInt;
    this.varBind.addElement(paramSnmpVarBind);
  }
  
  protected void updateResult(SnmpVarBind[] paramArrayOfSnmpVarBind)
  {
    if (paramArrayOfSnmpVarBind == null) {
      return;
    }
    int i = this.varBind.size();
    int j = paramArrayOfSnmpVarBind.length;
    for (int k = 0; k < i; k++)
    {
      int m = this.translation[k];
      if (m < j) {
        paramArrayOfSnmpVarBind[m] = ((SnmpVarBind)((NonSyncVector)this.varBind).elementAtNonSync(k));
      } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateResult", "Position `" + m + "' is out of bound...");
      }
    }
  }
  
  private void init(SnmpEngine paramSnmpEngine, SnmpIncomingRequest paramSnmpIncomingRequest)
  {
    this.incRequest = paramSnmpIncomingRequest;
    this.engine = paramSnmpEngine;
  }
  
  class NonSyncVector<E>
    extends Vector<E>
  {
    public NonSyncVector(int paramInt)
    {
      super();
    }
    
    final void addNonSyncElement(E paramE)
    {
      ensureCapacity(this.elementCount + 1);
      this.elementData[(this.elementCount++)] = paramE;
    }
    
    final E elementAtNonSync(int paramInt)
    {
      return this.elementData[paramInt];
    }
  }
}
