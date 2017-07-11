package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class ANEWARRAY
  extends CPInstruction
  implements LoadClass, AllocationInstruction, ExceptionThrower, StackProducer
{
  ANEWARRAY() {}
  
  public ANEWARRAY(int paramInt)
  {
    super((short)189, paramInt);
  }
  
  public Class[] getExceptions()
  {
    Class[] arrayOfClass = new Class[1 + ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length);
    arrayOfClass[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length] = ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION;
    return arrayOfClass;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitAllocationInstruction(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitANEWARRAY(this);
  }
  
  public ObjectType getLoadClassType(ConstantPoolGen paramConstantPoolGen)
  {
    Type localType = getType(paramConstantPoolGen);
    if ((localType instanceof ArrayType)) {
      localType = ((ArrayType)localType).getBasicType();
    }
    return (localType instanceof ObjectType) ? (ObjectType)localType : null;
  }
}
