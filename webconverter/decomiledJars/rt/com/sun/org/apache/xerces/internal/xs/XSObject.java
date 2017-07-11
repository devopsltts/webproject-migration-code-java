package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSObject
{
  public abstract short getType();
  
  public abstract String getName();
  
  public abstract String getNamespace();
  
  public abstract XSNamespaceItem getNamespaceItem();
}
