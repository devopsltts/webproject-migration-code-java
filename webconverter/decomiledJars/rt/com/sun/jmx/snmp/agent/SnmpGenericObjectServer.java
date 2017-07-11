package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

public class SnmpGenericObjectServer
{
  protected final MBeanServer server;
  
  public SnmpGenericObjectServer(MBeanServer paramMBeanServer)
  {
    this.server = paramMBeanServer;
  }
  
  public void get(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    int i = paramSnmpMibSubRequest.getSize();
    Object localObject1 = paramSnmpMibSubRequest.getUserData();
    String[] arrayOfString = new String[i];
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[i];
    long[] arrayOfLong = new long[i];
    int j = 0;
    Object localObject2 = paramSnmpMibSubRequest.getElements();
    while (((Enumeration)localObject2).hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)((Enumeration)localObject2).nextElement();
      try
      {
        long l = localSnmpVarBind.oid.getOidArc(paramInt);
        arrayOfString[j] = paramSnmpGenericMetaServer.getAttributeName(l);
        arrayOfSnmpVarBind[j] = localSnmpVarBind;
        arrayOfLong[j] = l;
        paramSnmpGenericMetaServer.checkGetAccess(l, localObject1);
        j++;
      }
      catch (SnmpStatusException localSnmpStatusException1)
      {
        paramSnmpMibSubRequest.registerGetException(localSnmpVarBind, localSnmpStatusException1);
      }
    }
    localObject2 = null;
    int k = 224;
    try
    {
      localObject2 = this.server.getAttributes(paramObjectName, arrayOfString);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      localObject2 = new AttributeList();
    }
    catch (ReflectionException localReflectionException)
    {
      localObject2 = new AttributeList();
    }
    catch (Exception localException)
    {
      localObject2 = new AttributeList();
    }
    Iterator localIterator = ((AttributeList)localObject2).iterator();
    for (int m = 0; m < j; m++)
    {
      Object localObject3;
      if (!localIterator.hasNext())
      {
        localObject3 = new SnmpStatusException(k);
        paramSnmpMibSubRequest.registerGetException(arrayOfSnmpVarBind[m], (SnmpStatusException)localObject3);
      }
      else
      {
        localObject3 = (Attribute)localIterator.next();
        while ((m < j) && (!arrayOfString[m].equals(((Attribute)localObject3).getName())))
        {
          SnmpStatusException localSnmpStatusException2 = new SnmpStatusException(k);
          paramSnmpMibSubRequest.registerGetException(arrayOfSnmpVarBind[m], localSnmpStatusException2);
          m++;
        }
        if (m == j) {
          break;
        }
        try
        {
          arrayOfSnmpVarBind[m].value = paramSnmpGenericMetaServer.buildSnmpValue(arrayOfLong[m], ((Attribute)localObject3).getValue());
        }
        catch (SnmpStatusException localSnmpStatusException3)
        {
          paramSnmpMibSubRequest.registerGetException(arrayOfSnmpVarBind[m], localSnmpStatusException3);
        }
      }
    }
  }
  
  public SnmpValue get(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    String str = paramSnmpGenericMetaServer.getAttributeName(paramLong);
    Object localObject = null;
    try
    {
      localObject = this.server.getAttribute(paramObjectName, str);
    }
    catch (MBeanException localMBeanException)
    {
      Exception localException2 = localMBeanException.getTargetException();
      if ((localException2 instanceof SnmpStatusException)) {
        throw ((SnmpStatusException)localException2);
      }
      throw new SnmpStatusException(224);
    }
    catch (Exception localException1)
    {
      throw new SnmpStatusException(224);
    }
    return paramSnmpGenericMetaServer.buildSnmpValue(paramLong, localObject);
  }
  
  public void set(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    int i = paramSnmpMibSubRequest.getSize();
    AttributeList localAttributeList = new AttributeList(i);
    String[] arrayOfString = new String[i];
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[i];
    long[] arrayOfLong = new long[i];
    int j = 0;
    Object localObject1 = paramSnmpMibSubRequest.getElements();
    Object localObject2;
    Object localObject3;
    while (((Enumeration)localObject1).hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)((Enumeration)localObject1).nextElement();
      try
      {
        long l = localSnmpVarBind.oid.getOidArc(paramInt);
        localObject2 = paramSnmpGenericMetaServer.getAttributeName(l);
        localObject3 = paramSnmpGenericMetaServer.buildAttributeValue(l, localSnmpVarBind.value);
        Attribute localAttribute = new Attribute((String)localObject2, localObject3);
        localAttributeList.add(localAttribute);
        arrayOfString[j] = localObject2;
        arrayOfSnmpVarBind[j] = localSnmpVarBind;
        arrayOfLong[j] = l;
        j++;
      }
      catch (SnmpStatusException localSnmpStatusException1)
      {
        paramSnmpMibSubRequest.registerSetException(localSnmpVarBind, localSnmpStatusException1);
      }
    }
    int k = 6;
    try
    {
      localObject1 = this.server.setAttributes(paramObjectName, localAttributeList);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      localObject1 = new AttributeList();
      k = 18;
    }
    catch (ReflectionException localReflectionException)
    {
      k = 18;
      localObject1 = new AttributeList();
    }
    catch (Exception localException)
    {
      localObject1 = new AttributeList();
    }
    Iterator localIterator = ((AttributeList)localObject1).iterator();
    for (int m = 0; m < j; m++) {
      if (!localIterator.hasNext())
      {
        localObject2 = new SnmpStatusException(k);
        paramSnmpMibSubRequest.registerSetException(arrayOfSnmpVarBind[m], (SnmpStatusException)localObject2);
      }
      else
      {
        localObject2 = (Attribute)localIterator.next();
        while ((m < j) && (!arrayOfString[m].equals(((Attribute)localObject2).getName())))
        {
          localObject3 = new SnmpStatusException(6);
          paramSnmpMibSubRequest.registerSetException(arrayOfSnmpVarBind[m], (SnmpStatusException)localObject3);
          m++;
        }
        if (m == j) {
          break;
        }
        try
        {
          arrayOfSnmpVarBind[m].value = paramSnmpGenericMetaServer.buildSnmpValue(arrayOfLong[m], ((Attribute)localObject2).getValue());
        }
        catch (SnmpStatusException localSnmpStatusException2)
        {
          paramSnmpMibSubRequest.registerSetException(arrayOfSnmpVarBind[m], localSnmpStatusException2);
        }
      }
    }
  }
  
  public SnmpValue set(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    String str = paramSnmpGenericMetaServer.getAttributeName(paramLong);
    Object localObject1 = paramSnmpGenericMetaServer.buildAttributeValue(paramLong, paramSnmpValue);
    Attribute localAttribute = new Attribute(str, localObject1);
    Object localObject2 = null;
    try
    {
      this.server.setAttribute(paramObjectName, localAttribute);
      localObject2 = this.server.getAttribute(paramObjectName, str);
    }
    catch (InvalidAttributeValueException localInvalidAttributeValueException)
    {
      throw new SnmpStatusException(10);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new SnmpStatusException(18);
    }
    catch (ReflectionException localReflectionException)
    {
      throw new SnmpStatusException(18);
    }
    catch (MBeanException localMBeanException)
    {
      Exception localException2 = localMBeanException.getTargetException();
      if ((localException2 instanceof SnmpStatusException)) {
        throw ((SnmpStatusException)localException2);
      }
      throw new SnmpStatusException(6);
    }
    catch (Exception localException1)
    {
      throw new SnmpStatusException(6);
    }
    return paramSnmpGenericMetaServer.buildSnmpValue(paramLong, localObject2);
  }
  
  public void check(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
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
        check(paramSnmpGenericMetaServer, paramObjectName, localSnmpVarBind.value, l, localObject);
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        paramSnmpMibSubRequest.registerCheckException(localSnmpVarBind, localSnmpStatusException);
      }
    }
  }
  
  public void check(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    paramSnmpGenericMetaServer.checkSetAccess(paramSnmpValue, paramLong, paramObject);
    try
    {
      String str = paramSnmpGenericMetaServer.getAttributeName(paramLong);
      localObject = paramSnmpGenericMetaServer.buildAttributeValue(paramLong, paramSnmpValue);
      Object[] arrayOfObject = new Object[1];
      String[] arrayOfString = new String[1];
      arrayOfObject[0] = localObject;
      arrayOfString[0] = localObject.getClass().getName();
      this.server.invoke(paramObjectName, "check" + str, arrayOfObject, arrayOfString);
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      throw localSnmpStatusException;
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new SnmpStatusException(18);
    }
    catch (ReflectionException localReflectionException) {}catch (MBeanException localMBeanException)
    {
      Object localObject = localMBeanException.getTargetException();
      if ((localObject instanceof SnmpStatusException)) {
        throw ((SnmpStatusException)localObject);
      }
      throw new SnmpStatusException(6);
    }
    catch (Exception localException)
    {
      throw new SnmpStatusException(6);
    }
  }
  
  public void registerTableEntry(SnmpMibTable paramSnmpMibTable, SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject)
    throws SnmpStatusException
  {
    if (paramObjectName == null) {
      throw new SnmpStatusException(18);
    }
    try
    {
      if ((paramObject != null) && (!this.server.isRegistered(paramObjectName))) {
        this.server.registerMBean(paramObject, paramObjectName);
      }
    }
    catch (InstanceAlreadyExistsException localInstanceAlreadyExistsException)
    {
      throw new SnmpStatusException(18);
    }
    catch (MBeanRegistrationException localMBeanRegistrationException)
    {
      throw new SnmpStatusException(6);
    }
    catch (NotCompliantMBeanException localNotCompliantMBeanException)
    {
      throw new SnmpStatusException(5);
    }
    catch (RuntimeOperationsException localRuntimeOperationsException)
    {
      throw new SnmpStatusException(5);
    }
    catch (Exception localException)
    {
      throw new SnmpStatusException(5);
    }
  }
}
