package com.sun.org.apache.bcel.internal.generic;

public abstract interface NamedAndTyped
{
  public abstract String getName();
  
  public abstract Type getType();
  
  public abstract void setName(String paramString);
  
  public abstract void setType(Type paramType);
}
