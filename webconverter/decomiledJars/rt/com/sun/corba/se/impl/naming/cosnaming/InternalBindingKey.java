package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.NameComponent;

public class InternalBindingKey
{
  public NameComponent name;
  private int idLen;
  private int kindLen;
  private int hashVal;
  
  public InternalBindingKey() {}
  
  public InternalBindingKey(NameComponent paramNameComponent)
  {
    this.idLen = 0;
    this.kindLen = 0;
    setup(paramNameComponent);
  }
  
  protected void setup(NameComponent paramNameComponent)
  {
    this.name = paramNameComponent;
    if (this.name.id != null) {
      this.idLen = this.name.id.length();
    }
    if (this.name.kind != null) {
      this.kindLen = this.name.kind.length();
    }
    this.hashVal = 0;
    if (this.idLen > 0) {
      this.hashVal += this.name.id.hashCode();
    }
    if (this.kindLen > 0) {
      this.hashVal += this.name.kind.hashCode();
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if ((paramObject instanceof InternalBindingKey))
    {
      InternalBindingKey localInternalBindingKey = (InternalBindingKey)paramObject;
      if ((this.idLen != localInternalBindingKey.idLen) || (this.kindLen != localInternalBindingKey.kindLen)) {
        return false;
      }
      if ((this.idLen > 0) && (!this.name.id.equals(localInternalBindingKey.name.id))) {
        return false;
      }
      return (this.kindLen <= 0) || (this.name.kind.equals(localInternalBindingKey.name.kind));
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.hashVal;
  }
}
