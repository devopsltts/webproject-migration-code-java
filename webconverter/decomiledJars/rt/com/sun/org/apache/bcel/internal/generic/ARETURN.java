package com.sun.org.apache.bcel.internal.generic;

public class ARETURN
  extends ReturnInstruction
{
  public ARETURN()
  {
    super((short)176);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitReturnInstruction(this);
    paramVisitor.visitARETURN(this);
  }
}
