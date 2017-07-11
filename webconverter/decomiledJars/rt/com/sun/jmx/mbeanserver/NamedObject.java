package com.sun.jmx.mbeanserver;

import javax.management.DynamicMBean;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;

public class NamedObject
{
  private final ObjectName name;
  private final DynamicMBean object;
  
  public NamedObject(ObjectName paramObjectName, DynamicMBean paramDynamicMBean)
  {
    if (paramObjectName.isPattern()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + paramObjectName.toString()));
    }
    this.name = paramObjectName;
    this.object = paramDynamicMBean;
  }
  
  public NamedObject(String paramString, DynamicMBean paramDynamicMBean)
    throws MalformedObjectNameException
  {
    ObjectName localObjectName = new ObjectName(paramString);
    if (localObjectName.isPattern()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + localObjectName.toString()));
    }
    this.name = localObjectName;
    this.object = paramDynamicMBean;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof NamedObject)) {
      return false;
    }
    NamedObject localNamedObject = (NamedObject)paramObject;
    return this.name.equals(localNamedObject.getName());
  }
  
  public int hashCode()
  {
    return this.name.hashCode();
  }
  
  public ObjectName getName()
  {
    return this.name;
  }
  
  public DynamicMBean getObject()
  {
    return this.object;
  }
}
