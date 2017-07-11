package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class SendQ
  extends Vector<SnmpInformRequest>
{
  boolean isBeingDestroyed = false;
  
  SendQ(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  
  private synchronized void notifyClients()
  {
    notifyAll();
  }
  
  public synchronized void addRequest(SnmpInformRequest paramSnmpInformRequest)
  {
    long l = paramSnmpInformRequest.getAbsNextPollTime();
    for (int i = size(); (i > 0) && (l >= getRequestAt(i - 1).getAbsNextPollTime()); i--) {}
    if (i == size())
    {
      addElement(paramSnmpInformRequest);
      notifyClients();
    }
    else
    {
      insertElementAt(paramSnmpInformRequest, i);
    }
  }
  
  public synchronized boolean waitUntilReady()
  {
    for (;;)
    {
      if (this.isBeingDestroyed == true) {
        return false;
      }
      long l1 = 0L;
      if (!isEmpty())
      {
        long l2 = System.currentTimeMillis();
        SnmpInformRequest localSnmpInformRequest = (SnmpInformRequest)lastElement();
        l1 = localSnmpInformRequest.getAbsNextPollTime() - l2;
        if (l1 <= 0L) {
          return true;
        }
      }
      waitOnThisQueue(l1);
    }
  }
  
  public synchronized Vector<SnmpInformRequest> getAllOutstandingRequest(long paramLong)
  {
    Vector localVector = new Vector();
    while (waitUntilReady() == true)
    {
      long l = System.currentTimeMillis() + paramLong;
      for (int i = size(); i > 0; i--)
      {
        SnmpInformRequest localSnmpInformRequest = getRequestAt(i - 1);
        if (localSnmpInformRequest.getAbsNextPollTime() > l) {
          break;
        }
        localVector.addElement(localSnmpInformRequest);
      }
      if (!localVector.isEmpty())
      {
        this.elementCount -= localVector.size();
        return localVector;
      }
    }
    return null;
  }
  
  public synchronized void waitOnThisQueue(long paramLong)
  {
    if ((paramLong == 0L) && (!isEmpty()) && (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpQManager.class.getName(), "waitOnThisQueue", "[" + Thread.currentThread().toString() + "]:" + "Fatal BUG :: Blocking on newq permenantly. But size = " + size());
    }
    try
    {
      wait(paramLong);
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public SnmpInformRequest getRequestAt(int paramInt)
  {
    return (SnmpInformRequest)elementAt(paramInt);
  }
  
  public synchronized SnmpInformRequest removeRequest(long paramLong)
  {
    int i = size();
    for (int j = 0; j < i; j++)
    {
      SnmpInformRequest localSnmpInformRequest = getRequestAt(j);
      if (paramLong == localSnmpInformRequest.getRequestId())
      {
        removeElementAt(j);
        return localSnmpInformRequest;
      }
    }
    return null;
  }
}
