package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class OperationDescription
  implements IDLEntity
{
  public String name = null;
  public String id = null;
  public String defined_in = null;
  public String version = null;
  public TypeCode result = null;
  public OperationMode mode = null;
  public String[] contexts = null;
  public ParameterDescription[] parameters = null;
  public ExceptionDescription[] exceptions = null;
  
  public OperationDescription() {}
  
  public OperationDescription(String paramString1, String paramString2, String paramString3, String paramString4, TypeCode paramTypeCode, OperationMode paramOperationMode, String[] paramArrayOfString, ParameterDescription[] paramArrayOfParameterDescription, ExceptionDescription[] paramArrayOfExceptionDescription)
  {
    this.name = paramString1;
    this.id = paramString2;
    this.defined_in = paramString3;
    this.version = paramString4;
    this.result = paramTypeCode;
    this.mode = paramOperationMode;
    this.contexts = paramArrayOfString;
    this.parameters = paramArrayOfParameterDescription;
    this.exceptions = paramArrayOfExceptionDescription;
  }
}
