package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.Binding;

public class InternalBindingValue
{
  public Binding theBinding;
  public String strObjectRef;
  public org.omg.CORBA.Object theObjectRef;
  
  public InternalBindingValue() {}
  
  public InternalBindingValue(Binding paramBinding, String paramString)
  {
    this.theBinding = paramBinding;
    this.strObjectRef = paramString;
  }
}
