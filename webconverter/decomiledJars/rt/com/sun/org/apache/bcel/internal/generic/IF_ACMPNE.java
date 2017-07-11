package com.sun.org.apache.bcel.internal.generic;

public class IF_ACMPNE
  extends IfInstruction
{
  IF_ACMPNE() {}
  
  public IF_ACMPNE(InstructionHandle paramInstructionHandle)
  {
    super((short)166, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ACMPEQ(this.target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ACMPNE(this);
  }
}
