package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class AttributeDescription
  implements IDLEntity
{
  public String name = null;
  public String id = null;
  public String defined_in = null;
  public String version = null;
  public TypeCode type = null;
  public AttributeMode mode = null;
  
  public AttributeDescription() {}
  
  public AttributeDescription(String paramString1, String paramString2, String paramString3, String paramString4, TypeCode paramTypeCode, AttributeMode paramAttributeMode)
  {
    this.name = paramString1;
    this.id = paramString2;
    this.defined_in = paramString3;
    this.version = paramString4;
    this.type = paramTypeCode;
    this.mode = paramAttributeMode;
  }
}
