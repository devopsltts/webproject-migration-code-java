package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class INSTANCEOF
  extends CPInstruction
  implements LoadClass, ExceptionThrower, StackProducer, StackConsumer
{
  INSTANCEOF() {}
  
  public INSTANCEOF(int paramInt)
  {
    super((short)193, paramInt);
  }
  
  public Class[] getExceptions()
  {
    return ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION;
  }
  
  public ObjectType getLoadClassType(ConstantPoolGen paramConstantPoolGen)
  {
    Type localType = getType(paramConstantPoolGen);
    if ((localType instanceof ArrayType)) {
      localType = ((ArrayType)localType).getBasicType();
    }
    return (localType instanceof ObjectType) ? (ObjectType)localType : null;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitINSTANCEOF(this);
  }
}
