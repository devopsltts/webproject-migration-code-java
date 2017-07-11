package com.sun.org.apache.xerces.internal.impl.validation;

public abstract interface EntityState
{
  public abstract boolean isEntityDeclared(String paramString);
  
  public abstract boolean isEntityUnparsed(String paramString);
}
