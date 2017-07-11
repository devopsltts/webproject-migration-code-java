package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class StructMember
  implements IDLEntity
{
  public String name;
  public TypeCode type;
  public IDLType type_def;
  
  public StructMember() {}
  
  public StructMember(String paramString, TypeCode paramTypeCode, IDLType paramIDLType)
  {
    this.name = paramString;
    this.type = paramTypeCode;
    this.type_def = paramIDLType;
  }
}
