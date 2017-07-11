package com.sun.org.omg.CORBA;

import org.omg.CORBA.IDLType;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class ParameterDescription
  implements IDLEntity
{
  public String name = null;
  public TypeCode type = null;
  public IDLType type_def = null;
  public ParameterMode mode = null;
  
  public ParameterDescription() {}
  
  public ParameterDescription(String paramString, TypeCode paramTypeCode, IDLType paramIDLType, ParameterMode paramParameterMode)
  {
    this.name = paramString;
    this.type = paramTypeCode;
    this.type_def = paramIDLType;
    this.mode = paramParameterMode;
  }
}
