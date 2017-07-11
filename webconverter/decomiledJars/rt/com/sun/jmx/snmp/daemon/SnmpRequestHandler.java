package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPduBulk;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class SnmpRequestHandler
  extends ClientHandler
  implements SnmpDefinitions
{
  private transient DatagramSocket socket = null;
  private transient DatagramPacket packet = null;
  private transient Vector<SnmpMibAgent> mibs = null;
  private transient Hashtable<SnmpMibAgent, SnmpSubRequestHandler> subs = null;
  private transient SnmpMibTree root;
  private transient InetAddressAcl ipacl = null;
  private transient SnmpPduFactory pduFactory = null;
  private transient SnmpUserDataFactory userDataFactory = null;
  private transient SnmpAdaptorServer adaptor = null;
  private static final String InterruptSysCallMsg = "Interrupted system call";
  
  public SnmpRequestHandler(SnmpAdaptorServer paramSnmpAdaptorServer, int paramInt, DatagramSocket paramDatagramSocket, DatagramPacket paramDatagramPacket, SnmpMibTree paramSnmpMibTree, Vector<SnmpMibAgent> paramVector, InetAddressAcl paramInetAddressAcl, SnmpPduFactory paramSnmpPduFactory, SnmpUserDataFactory paramSnmpUserDataFactory, MBeanServer paramMBeanServer, ObjectName paramObjectName)
  {
    super(paramSnmpAdaptorServer, paramInt, paramMBeanServer, paramObjectName);
    this.adaptor = paramSnmpAdaptorServer;
    this.socket = paramDatagramSocket;
    this.packet = paramDatagramPacket;
    this.root = paramSnmpMibTree;
    this.mibs = new Vector(paramVector);
    this.subs = new Hashtable(this.mibs.size());
    this.ipacl = paramInetAddressAcl;
    this.pduFactory = paramSnmpPduFactory;
    this.userDataFactory = paramSnmpUserDataFactory;
  }
  
  public void doRun()
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doRun", "Packet received:\n" + SnmpMessage.dumpHexBuffer(this.packet.getData(), 0, this.packet.getLength()));
    }
    DatagramPacket localDatagramPacket = makeResponsePacket(this.packet);
    if ((JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) && (localDatagramPacket != null)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doRun", "Packet to be sent:\n" + SnmpMessage.dumpHexBuffer(localDatagramPacket.getData(), 0, localDatagramPacket.getLength()));
    }
    if (localDatagramPacket != null) {
      try
      {
        this.socket.send(localDatagramPacket);
      }
      catch (SocketException localSocketException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          if (localSocketException.getMessage().equals("Interrupted system call")) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "interrupted");
          } else {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "I/O exception", localSocketException);
          }
        }
      }
      catch (InterruptedIOException localInterruptedIOException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "interrupted");
        }
      }
      catch (Exception localException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "failure when sending response", localException);
        }
      }
    }
  }
  
  private DatagramPacket makeResponsePacket(DatagramPacket paramDatagramPacket)
  {
    DatagramPacket localDatagramPacket = null;
    SnmpMessage localSnmpMessage1 = new SnmpMessage();
    try
    {
      localSnmpMessage1.decodeMessage(paramDatagramPacket.getData(), paramDatagramPacket.getLength());
      localSnmpMessage1.address = paramDatagramPacket.getAddress();
      localSnmpMessage1.port = paramDatagramPacket.getPort();
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", "packet decoding failed", localSnmpStatusException);
      }
      localSnmpMessage1 = null;
      ((SnmpAdaptorServer)this.adaptorServer).incSnmpInASNParseErrs(1);
    }
    SnmpMessage localSnmpMessage2 = null;
    if (localSnmpMessage1 != null) {
      localSnmpMessage2 = makeResponseMessage(localSnmpMessage1);
    }
    if (localSnmpMessage2 != null) {
      try
      {
        paramDatagramPacket.setLength(localSnmpMessage2.encodeMessage(paramDatagramPacket.getData()));
        localDatagramPacket = paramDatagramPacket;
      }
      catch (SnmpTooBigException localSnmpTooBigException1)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", "response message is too big");
        }
        try
        {
          localSnmpMessage2 = newTooBigMessage(localSnmpMessage1);
          paramDatagramPacket.setLength(localSnmpMessage2.encodeMessage(paramDatagramPacket.getData()));
          localDatagramPacket = paramDatagramPacket;
        }
        catch (SnmpTooBigException localSnmpTooBigException2)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", "'too big' is 'too big' !!!");
          }
          this.adaptor.incSnmpSilentDrops(1);
        }
      }
    }
    return localDatagramPacket;
  }
  
  private SnmpMessage makeResponseMessage(SnmpMessage paramSnmpMessage)
  {
    SnmpMessage localSnmpMessage = null;
    Object localObject = null;
    SnmpPduPacket localSnmpPduPacket1;
    try
    {
      localSnmpPduPacket1 = (SnmpPduPacket)this.pduFactory.decodeSnmpPdu(paramSnmpMessage);
      if ((localSnmpPduPacket1 != null) && (this.userDataFactory != null)) {
        localObject = this.userDataFactory.allocateUserData(localSnmpPduPacket1);
      }
    }
    catch (SnmpStatusException localSnmpStatusException1)
    {
      localSnmpPduPacket1 = null;
      SnmpAdaptorServer localSnmpAdaptorServer = (SnmpAdaptorServer)this.adaptorServer;
      localSnmpAdaptorServer.incSnmpInASNParseErrs(1);
      if (localSnmpStatusException1.getStatus() == 243) {
        localSnmpAdaptorServer.incSnmpInBadVersions(1);
      }
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "message decoding failed", localSnmpStatusException1);
      }
    }
    SnmpPduPacket localSnmpPduPacket2 = null;
    if (localSnmpPduPacket1 != null)
    {
      localSnmpPduPacket2 = makeResponsePdu(localSnmpPduPacket1, localObject);
      try
      {
        if (this.userDataFactory != null) {
          this.userDataFactory.releaseUserData(localObject, localSnmpPduPacket2);
        }
      }
      catch (SnmpStatusException localSnmpStatusException2)
      {
        localSnmpPduPacket2 = null;
      }
    }
    if (localSnmpPduPacket2 != null) {
      try
      {
        localSnmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(localSnmpPduPacket2, this.packet.getData().length);
      }
      catch (SnmpStatusException localSnmpStatusException3)
      {
        localSnmpMessage = null;
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "failure when encoding the response message", localSnmpStatusException3);
        }
      }
      catch (SnmpTooBigException localSnmpTooBigException1)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "response message is too big");
        }
        try
        {
          if (this.packet.getData().length <= 32) {
            throw localSnmpTooBigException1;
          }
          int i = localSnmpTooBigException1.getVarBindCount();
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "fail on element" + i);
          }
          for (;;)
          {
            try
            {
              localSnmpPduPacket2 = reduceResponsePdu(localSnmpPduPacket1, localSnmpPduPacket2, i);
              localSnmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(localSnmpPduPacket2, this.packet.getData().length - 32);
            }
            catch (SnmpTooBigException localSnmpTooBigException4)
            {
              if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "response message is still too big");
              }
              int j = i;
              i = localSnmpTooBigException4.getVarBindCount();
              if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "fail on element" + i);
              }
              if (i == j) {
                throw localSnmpTooBigException4;
              }
            }
          }
        }
        catch (SnmpStatusException localSnmpStatusException4)
        {
          localSnmpMessage = null;
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "failure when encoding the response message", localSnmpStatusException4);
          }
        }
        catch (SnmpTooBigException localSnmpTooBigException2)
        {
          try
          {
            localSnmpPduPacket2 = newTooBigPdu(localSnmpPduPacket1);
            localSnmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(localSnmpPduPacket2, this.packet.getData().length);
          }
          catch (SnmpTooBigException localSnmpTooBigException3)
          {
            localSnmpMessage = null;
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "'too big' is 'too big' !!!");
            }
            this.adaptor.incSnmpSilentDrops(1);
          }
          catch (Exception localException2)
          {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "Got unexpected exception", localException2);
            }
            localSnmpMessage = null;
          }
        }
        catch (Exception localException1)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "Got unexpected exception", localException1);
          }
          localSnmpMessage = null;
        }
      }
    }
    return localSnmpMessage;
  }
  
  private SnmpPduPacket makeResponsePdu(SnmpPduPacket paramSnmpPduPacket, Object paramObject)
  {
    SnmpAdaptorServer localSnmpAdaptorServer = (SnmpAdaptorServer)this.adaptorServer;
    SnmpPduPacket localSnmpPduPacket = null;
    localSnmpAdaptorServer.updateRequestCounters(paramSnmpPduPacket.type);
    if (paramSnmpPduPacket.varBindList != null) {
      localSnmpAdaptorServer.updateVarCounters(paramSnmpPduPacket.type, paramSnmpPduPacket.varBindList.length);
    }
    if (checkPduType(paramSnmpPduPacket))
    {
      localSnmpPduPacket = checkAcl(paramSnmpPduPacket);
      if (localSnmpPduPacket == null)
      {
        if (this.mibs.size() < 1)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "makeResponsePdu", "Request " + paramSnmpPduPacket.requestId + " received but no MIB registered.");
          }
          return makeNoMibErrorPdu((SnmpPduRequest)paramSnmpPduPacket, paramObject);
        }
        switch (paramSnmpPduPacket.type)
        {
        case 160: 
        case 161: 
        case 163: 
          localSnmpPduPacket = makeGetSetResponsePdu((SnmpPduRequest)paramSnmpPduPacket, paramObject);
          break;
        case 165: 
          localSnmpPduPacket = makeGetBulkResponsePdu((SnmpPduBulk)paramSnmpPduPacket, paramObject);
        }
      }
      else
      {
        if (!localSnmpAdaptorServer.getAuthRespEnabled()) {
          localSnmpPduPacket = null;
        }
        if (localSnmpAdaptorServer.getAuthTrapEnabled()) {
          try
          {
            localSnmpAdaptorServer.snmpV1Trap(4, 0, new SnmpVarBindList());
          }
          catch (Exception localException)
          {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePdu", "Failure when sending authentication trap", localException);
            }
          }
        }
      }
    }
    return localSnmpPduPacket;
  }
  
  SnmpPduPacket makeErrorVarbindPdu(SnmpPduPacket paramSnmpPduPacket, int paramInt)
  {
    SnmpVarBind[] arrayOfSnmpVarBind = paramSnmpPduPacket.varBindList;
    int i = arrayOfSnmpVarBind.length;
    int j;
    switch (paramInt)
    {
    case 130: 
      for (j = 0; j < i; j++) {
        arrayOfSnmpVarBind[j].value = SnmpVarBind.endOfMibView;
      }
      break;
    case 128: 
      for (j = 0; j < i; j++) {
        arrayOfSnmpVarBind[j].value = SnmpVarBind.noSuchObject;
      }
      break;
    case 129: 
      for (j = 0; j < i; j++) {
        arrayOfSnmpVarBind[j].value = SnmpVarBind.noSuchInstance;
      }
      break;
    default: 
      return newErrorResponsePdu(paramSnmpPduPacket, 5, 1);
    }
    return newValidResponsePdu(paramSnmpPduPacket, arrayOfSnmpVarBind);
  }
  
  SnmpPduPacket makeNoMibErrorPdu(SnmpPduRequest paramSnmpPduRequest, Object paramObject)
  {
    if (paramSnmpPduRequest.version == 0) {
      return newErrorResponsePdu(paramSnmpPduRequest, 2, 1);
    }
    if (paramSnmpPduRequest.version == 1) {
      switch (paramSnmpPduRequest.type)
      {
      case 163: 
      case 253: 
        return newErrorResponsePdu(paramSnmpPduRequest, 6, 1);
      case 160: 
        return makeErrorVarbindPdu(paramSnmpPduRequest, 128);
      case 161: 
      case 165: 
        return makeErrorVarbindPdu(paramSnmpPduRequest, 130);
      }
    }
    return newErrorResponsePdu(paramSnmpPduRequest, 5, 1);
  }
  
  private SnmpPduPacket makeGetSetResponsePdu(SnmpPduRequest paramSnmpPduRequest, Object paramObject)
  {
    if (paramSnmpPduRequest.varBindList == null) {
      return newValidResponsePdu(paramSnmpPduRequest, null);
    }
    splitRequest(paramSnmpPduRequest);
    int i = this.subs.size();
    if (i == 1) {
      return turboProcessingGetSet(paramSnmpPduRequest, paramObject);
    }
    SnmpPduPacket localSnmpPduPacket = executeSubRequest(paramSnmpPduRequest, paramObject);
    if (localSnmpPduPacket != null) {
      return localSnmpPduPacket;
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "makeGetSetResponsePdu", "Build the unified response for request " + paramSnmpPduRequest.requestId);
    }
    return mergeResponses(paramSnmpPduRequest);
  }
  
  private SnmpPduPacket executeSubRequest(SnmpPduPacket paramSnmpPduPacket, Object paramObject)
  {
    int i = 0;
    SnmpSubRequestHandler localSnmpSubRequestHandler;
    if (paramSnmpPduPacket.type == 163)
    {
      j = 0;
      localEnumeration = this.subs.elements();
      while (localEnumeration.hasMoreElements())
      {
        localSnmpSubRequestHandler = (SnmpSubRequestHandler)localEnumeration.nextElement();
        localSnmpSubRequestHandler.setUserData(paramObject);
        localSnmpSubRequestHandler.type = 253;
        localSnmpSubRequestHandler.run();
        localSnmpSubRequestHandler.type = 163;
        if (localSnmpSubRequestHandler.getErrorStatus() != 0)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "executeSubRequest", "an error occurs");
          }
          return newErrorResponsePdu(paramSnmpPduPacket, i, localSnmpSubRequestHandler.getErrorIndex() + 1);
        }
        j++;
      }
    }
    int j = 0;
    Enumeration localEnumeration = this.subs.elements();
    while (localEnumeration.hasMoreElements())
    {
      localSnmpSubRequestHandler = (SnmpSubRequestHandler)localEnumeration.nextElement();
      localSnmpSubRequestHandler.setUserData(paramObject);
      localSnmpSubRequestHandler.run();
      if (localSnmpSubRequestHandler.getErrorStatus() != 0)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "executeSubRequest", "an error occurs");
        }
        return newErrorResponsePdu(paramSnmpPduPacket, i, localSnmpSubRequestHandler.getErrorIndex() + 1);
      }
      j++;
    }
    return null;
  }
  
  private SnmpPduPacket turboProcessingGetSet(SnmpPduRequest paramSnmpPduRequest, Object paramObject)
  {
    SnmpSubRequestHandler localSnmpSubRequestHandler = (SnmpSubRequestHandler)this.subs.elements().nextElement();
    localSnmpSubRequestHandler.setUserData(paramObject);
    if (paramSnmpPduRequest.type == 163)
    {
      localSnmpSubRequestHandler.type = 253;
      localSnmpSubRequestHandler.run();
      localSnmpSubRequestHandler.type = 163;
      i = localSnmpSubRequestHandler.getErrorStatus();
      if (i != 0) {
        return newErrorResponsePdu(paramSnmpPduRequest, i, localSnmpSubRequestHandler.getErrorIndex() + 1);
      }
    }
    localSnmpSubRequestHandler.run();
    int i = localSnmpSubRequestHandler.getErrorStatus();
    if (i != 0)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "turboProcessingGetSet", "an error occurs");
      }
      int j = localSnmpSubRequestHandler.getErrorIndex() + 1;
      return newErrorResponsePdu(paramSnmpPduRequest, i, j);
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "turboProcessingGetSet", "build the unified response for request " + paramSnmpPduRequest.requestId);
    }
    return mergeResponses(paramSnmpPduRequest);
  }
  
  private SnmpPduPacket makeGetBulkResponsePdu(SnmpPduBulk paramSnmpPduBulk, Object paramObject)
  {
    int i = paramSnmpPduBulk.varBindList.length;
    int j = Math.max(Math.min(paramSnmpPduBulk.nonRepeaters, i), 0);
    int k = Math.max(paramSnmpPduBulk.maxRepetitions, 0);
    int m = i - j;
    if (paramSnmpPduBulk.varBindList == null) {
      return newValidResponsePdu(paramSnmpPduBulk, null);
    }
    splitBulkRequest(paramSnmpPduBulk, j, k, m);
    SnmpPduPacket localSnmpPduPacket = executeSubRequest(paramSnmpPduBulk, paramObject);
    if (localSnmpPduPacket != null) {
      return localSnmpPduPacket;
    }
    Object localObject = mergeBulkResponses(j + k * m);
    for (int i1 = localObject.length; (i1 > j) && (localObject[(i1 - 1)].value.equals(SnmpVarBind.endOfMibView)); i1--) {}
    int n;
    if (i1 == j) {
      n = j + m;
    } else {
      n = j + ((i1 - 1 - j) / m + 2) * m;
    }
    if (n < localObject.length)
    {
      SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[n];
      for (int i2 = 0; i2 < n; i2++) {
        arrayOfSnmpVarBind[i2] = localObject[i2];
      }
      localObject = arrayOfSnmpVarBind;
    }
    return newValidResponsePdu(paramSnmpPduBulk, (SnmpVarBind[])localObject);
  }
  
  private boolean checkPduType(SnmpPduPacket paramSnmpPduPacket)
  {
    boolean bool;
    switch (paramSnmpPduPacket.type)
    {
    case 160: 
    case 161: 
    case 163: 
    case 165: 
      bool = true;
      break;
    case 162: 
    case 164: 
    default: 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "checkPduType", "cannot respond to this kind of PDU");
      }
      bool = false;
    }
    return bool;
  }
  
  private SnmpPduPacket checkAcl(SnmpPduPacket paramSnmpPduPacket)
  {
    SnmpPduRequest localSnmpPduRequest = null;
    String str = new String(paramSnmpPduPacket.community);
    if (this.ipacl != null)
    {
      int i;
      if (paramSnmpPduPacket.type == 163)
      {
        if (!this.ipacl.checkWritePermission(paramSnmpPduPacket.address, str))
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + paramSnmpPduPacket.address + " with " + str + ". Sender has no write permission");
          }
          i = SnmpSubRequestHandler.mapErrorStatus(16, paramSnmpPduPacket.version, paramSnmpPduPacket.type);
          localSnmpPduRequest = newErrorResponsePdu(paramSnmpPduPacket, i, 0);
        }
        else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + paramSnmpPduPacket.address + " with " + str + ". Sender has write permission");
        }
      }
      else if (!this.ipacl.checkReadPermission(paramSnmpPduPacket.address, str))
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + paramSnmpPduPacket.address + " with " + str + ". Sender has no read permission");
        }
        i = SnmpSubRequestHandler.mapErrorStatus(16, paramSnmpPduPacket.version, paramSnmpPduPacket.type);
        localSnmpPduRequest = newErrorResponsePdu(paramSnmpPduPacket, i, 0);
        SnmpAdaptorServer localSnmpAdaptorServer2 = (SnmpAdaptorServer)this.adaptorServer;
        localSnmpAdaptorServer2.updateErrorCounters(2);
      }
      else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + paramSnmpPduPacket.address + " with " + str + ". Sender has read permission");
      }
    }
    if (localSnmpPduRequest != null)
    {
      SnmpAdaptorServer localSnmpAdaptorServer1 = (SnmpAdaptorServer)this.adaptorServer;
      localSnmpAdaptorServer1.incSnmpInBadCommunityUses(1);
      if (!this.ipacl.checkCommunity(str)) {
        localSnmpAdaptorServer1.incSnmpInBadCommunityNames(1);
      }
    }
    return localSnmpPduRequest;
  }
  
  private SnmpPduRequest newValidResponsePdu(SnmpPduPacket paramSnmpPduPacket, SnmpVarBind[] paramArrayOfSnmpVarBind)
  {
    SnmpPduRequest localSnmpPduRequest = new SnmpPduRequest();
    localSnmpPduRequest.address = paramSnmpPduPacket.address;
    localSnmpPduRequest.port = paramSnmpPduPacket.port;
    localSnmpPduRequest.version = paramSnmpPduPacket.version;
    localSnmpPduRequest.community = paramSnmpPduPacket.community;
    localSnmpPduRequest.type = 162;
    localSnmpPduRequest.requestId = paramSnmpPduPacket.requestId;
    localSnmpPduRequest.errorStatus = 0;
    localSnmpPduRequest.errorIndex = 0;
    localSnmpPduRequest.varBindList = paramArrayOfSnmpVarBind;
    ((SnmpAdaptorServer)this.adaptorServer).updateErrorCounters(localSnmpPduRequest.errorStatus);
    return localSnmpPduRequest;
  }
  
  private SnmpPduRequest newErrorResponsePdu(SnmpPduPacket paramSnmpPduPacket, int paramInt1, int paramInt2)
  {
    SnmpPduRequest localSnmpPduRequest = newValidResponsePdu(paramSnmpPduPacket, null);
    localSnmpPduRequest.errorStatus = paramInt1;
    localSnmpPduRequest.errorIndex = paramInt2;
    localSnmpPduRequest.varBindList = paramSnmpPduPacket.varBindList;
    ((SnmpAdaptorServer)this.adaptorServer).updateErrorCounters(localSnmpPduRequest.errorStatus);
    return localSnmpPduRequest;
  }
  
  private SnmpMessage newTooBigMessage(SnmpMessage paramSnmpMessage)
    throws SnmpTooBigException
  {
    SnmpMessage localSnmpMessage = null;
    try
    {
      SnmpPduPacket localSnmpPduPacket1 = (SnmpPduPacket)this.pduFactory.decodeSnmpPdu(paramSnmpMessage);
      if (localSnmpPduPacket1 != null)
      {
        SnmpPduPacket localSnmpPduPacket2 = newTooBigPdu(localSnmpPduPacket1);
        localSnmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(localSnmpPduPacket2, this.packet.getData().length);
      }
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "newTooBigMessage", "Internal error", localSnmpStatusException);
      }
      throw new InternalError(localSnmpStatusException);
    }
    return localSnmpMessage;
  }
  
  private SnmpPduPacket newTooBigPdu(SnmpPduPacket paramSnmpPduPacket)
  {
    SnmpPduRequest localSnmpPduRequest = newErrorResponsePdu(paramSnmpPduPacket, 1, 0);
    localSnmpPduRequest.varBindList = null;
    return localSnmpPduRequest;
  }
  
  private SnmpPduPacket reduceResponsePdu(SnmpPduPacket paramSnmpPduPacket1, SnmpPduPacket paramSnmpPduPacket2, int paramInt)
    throws SnmpTooBigException
  {
    if (paramSnmpPduPacket1.type != 165)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", "cannot remove anything");
      }
      throw new SnmpTooBigException(paramInt);
    }
    int i;
    if (paramInt >= 3) {
      i = Math.min(paramInt - 1, paramSnmpPduPacket2.varBindList.length);
    } else if (paramInt == 1) {
      i = 1;
    } else {
      i = paramSnmpPduPacket2.varBindList.length / 2;
    }
    if (i < 1)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", "cannot remove anything");
      }
      throw new SnmpTooBigException(paramInt);
    }
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[i];
    for (int j = 0; j < i; j++) {
      arrayOfSnmpVarBind[j] = paramSnmpPduPacket2.varBindList[j];
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", paramSnmpPduPacket2.varBindList.length - arrayOfSnmpVarBind.length + " items have been removed");
    }
    paramSnmpPduPacket2.varBindList = arrayOfSnmpVarBind;
    return paramSnmpPduPacket2;
  }
  
  private void splitRequest(SnmpPduRequest paramSnmpPduRequest)
  {
    int i = this.mibs.size();
    SnmpMibAgent localSnmpMibAgent = (SnmpMibAgent)this.mibs.firstElement();
    if (i == 1)
    {
      this.subs.put(localSnmpMibAgent, new SnmpSubRequestHandler(localSnmpMibAgent, paramSnmpPduRequest, true));
      return;
    }
    if (paramSnmpPduRequest.type == 161)
    {
      Enumeration localEnumeration = this.mibs.elements();
      while (localEnumeration.hasMoreElements())
      {
        localObject = (SnmpMibAgent)localEnumeration.nextElement();
        this.subs.put(localObject, new SnmpSubNextRequestHandler(this.adaptor, (SnmpMibAgent)localObject, paramSnmpPduRequest));
      }
      return;
    }
    int j = paramSnmpPduRequest.varBindList.length;
    Object localObject = paramSnmpPduRequest.varBindList;
    for (int k = 0; k < j; k++)
    {
      localSnmpMibAgent = this.root.getAgentMib(localObject[k].oid);
      SnmpSubRequestHandler localSnmpSubRequestHandler = (SnmpSubRequestHandler)this.subs.get(localSnmpMibAgent);
      if (localSnmpSubRequestHandler == null)
      {
        localSnmpSubRequestHandler = new SnmpSubRequestHandler(localSnmpMibAgent, paramSnmpPduRequest);
        this.subs.put(localSnmpMibAgent, localSnmpSubRequestHandler);
      }
      localSnmpSubRequestHandler.updateRequest(localObject[k], k);
    }
  }
  
  private void splitBulkRequest(SnmpPduBulk paramSnmpPduBulk, int paramInt1, int paramInt2, int paramInt3)
  {
    Enumeration localEnumeration = this.mibs.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpMibAgent localSnmpMibAgent = (SnmpMibAgent)localEnumeration.nextElement();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "splitBulkRequest", "Create a sub with : " + localSnmpMibAgent + " " + paramInt1 + " " + paramInt2 + " " + paramInt3);
      }
      this.subs.put(localSnmpMibAgent, new SnmpSubBulkRequestHandler(this.adaptor, localSnmpMibAgent, paramSnmpPduBulk, paramInt1, paramInt2, paramInt3));
    }
  }
  
  private SnmpPduPacket mergeResponses(SnmpPduRequest paramSnmpPduRequest)
  {
    if (paramSnmpPduRequest.type == 161) {
      return mergeNextResponses(paramSnmpPduRequest);
    }
    SnmpVarBind[] arrayOfSnmpVarBind = paramSnmpPduRequest.varBindList;
    Enumeration localEnumeration = this.subs.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpSubRequestHandler localSnmpSubRequestHandler = (SnmpSubRequestHandler)localEnumeration.nextElement();
      localSnmpSubRequestHandler.updateResult(arrayOfSnmpVarBind);
    }
    return newValidResponsePdu(paramSnmpPduRequest, arrayOfSnmpVarBind);
  }
  
  private SnmpPduPacket mergeNextResponses(SnmpPduRequest paramSnmpPduRequest)
  {
    int i = paramSnmpPduRequest.varBindList.length;
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[i];
    Enumeration localEnumeration = this.subs.elements();
    Object localObject;
    while (localEnumeration.hasMoreElements())
    {
      localObject = (SnmpSubRequestHandler)localEnumeration.nextElement();
      ((SnmpSubRequestHandler)localObject).updateResult(arrayOfSnmpVarBind);
    }
    if (paramSnmpPduRequest.version == 1) {
      return newValidResponsePdu(paramSnmpPduRequest, arrayOfSnmpVarBind);
    }
    for (int j = 0; j < i; j++)
    {
      localObject = arrayOfSnmpVarBind[j].value;
      if (localObject == SnmpVarBind.endOfMibView) {
        return newErrorResponsePdu(paramSnmpPduRequest, 2, j + 1);
      }
    }
    return newValidResponsePdu(paramSnmpPduRequest, arrayOfSnmpVarBind);
  }
  
  private SnmpVarBind[] mergeBulkResponses(int paramInt)
  {
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[paramInt];
    for (int i = paramInt - 1; i >= 0; i--)
    {
      arrayOfSnmpVarBind[i] = new SnmpVarBind();
      arrayOfSnmpVarBind[i].value = SnmpVarBind.endOfMibView;
    }
    Enumeration localEnumeration = this.subs.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpSubRequestHandler localSnmpSubRequestHandler = (SnmpSubRequestHandler)localEnumeration.nextElement();
      localSnmpSubRequestHandler.updateResult(arrayOfSnmpVarBind);
    }
    return arrayOfSnmpVarBind;
  }
  
  protected String makeDebugTag()
  {
    return "SnmpRequestHandler[" + this.adaptorServer.getProtocol() + ":" + this.adaptorServer.getPort() + "]";
  }
  
  Thread createThread(Runnable paramRunnable)
  {
    return null;
  }
}
