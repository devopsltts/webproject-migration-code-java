package com.sun.org.apache.xerces.internal.impl.dv;

public abstract interface DatatypeValidator
{
  public abstract void validate(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException;
}
