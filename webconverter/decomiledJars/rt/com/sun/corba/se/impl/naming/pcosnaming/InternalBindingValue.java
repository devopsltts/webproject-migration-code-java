package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.Serializable;
import org.omg.CosNaming.BindingType;

public class InternalBindingValue
  implements Serializable
{
  public BindingType theBindingType;
  public String strObjectRef;
  private transient org.omg.CORBA.Object theObjectRef;
  
  public InternalBindingValue() {}
  
  public InternalBindingValue(BindingType paramBindingType, String paramString)
  {
    this.theBindingType = paramBindingType;
    this.strObjectRef = paramString;
  }
  
  public org.omg.CORBA.Object getObjectRef()
  {
    return this.theObjectRef;
  }
  
  public void setObjectRef(org.omg.CORBA.Object paramObject)
  {
    this.theObjectRef = paramObject;
  }
}
