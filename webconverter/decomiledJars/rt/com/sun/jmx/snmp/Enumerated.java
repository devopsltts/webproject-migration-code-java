package com.sun.jmx.snmp;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class Enumerated
  implements Serializable
{
  protected int value;
  
  public Enumerated()
    throws IllegalArgumentException
  {
    Enumeration localEnumeration = getIntTable().keys();
    if (localEnumeration.hasMoreElements()) {
      this.value = ((Integer)localEnumeration.nextElement()).intValue();
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  public Enumerated(int paramInt)
    throws IllegalArgumentException
  {
    if (getIntTable().get(new Integer(paramInt)) == null) {
      throw new IllegalArgumentException();
    }
    this.value = paramInt;
  }
  
  public Enumerated(Integer paramInteger)
    throws IllegalArgumentException
  {
    if (getIntTable().get(paramInteger) == null) {
      throw new IllegalArgumentException();
    }
    this.value = paramInteger.intValue();
  }
  
  public Enumerated(String paramString)
    throws IllegalArgumentException
  {
    Integer localInteger = (Integer)getStringTable().get(paramString);
    if (localInteger == null) {
      throw new IllegalArgumentException();
    }
    this.value = localInteger.intValue();
  }
  
  public int intValue()
  {
    return this.value;
  }
  
  public Enumeration<Integer> valueIndexes()
  {
    return getIntTable().keys();
  }
  
  public Enumeration<String> valueStrings()
  {
    return getStringTable().keys();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && (getClass() == paramObject.getClass()) && (this.value == ((Enumerated)paramObject).value);
  }
  
  public int hashCode()
  {
    String str = getClass().getName() + String.valueOf(this.value);
    return str.hashCode();
  }
  
  public String toString()
  {
    return (String)getIntTable().get(new Integer(this.value));
  }
  
  protected abstract Hashtable<Integer, String> getIntTable();
  
  protected abstract Hashtable<String, Integer> getStringTable();
}
