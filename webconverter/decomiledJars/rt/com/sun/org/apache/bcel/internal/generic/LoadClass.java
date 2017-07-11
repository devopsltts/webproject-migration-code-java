package com.sun.org.apache.bcel.internal.generic;

public abstract interface LoadClass
{
  public abstract ObjectType getLoadClassType(ConstantPoolGen paramConstantPoolGen);
  
  public abstract Type getType(ConstantPoolGen paramConstantPoolGen);
}
