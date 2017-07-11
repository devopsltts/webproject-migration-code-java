package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class UnionMember
  implements IDLEntity
{
  public String name;
  public Any label;
  public TypeCode type;
  public IDLType type_def;
  
  public UnionMember() {}
  
  public UnionMember(String paramString, Any paramAny, TypeCode paramTypeCode, IDLType paramIDLType)
  {
    this.name = paramString;
    this.label = paramAny;
    this.type = paramTypeCode;
    this.type_def = paramIDLType;
  }
}
