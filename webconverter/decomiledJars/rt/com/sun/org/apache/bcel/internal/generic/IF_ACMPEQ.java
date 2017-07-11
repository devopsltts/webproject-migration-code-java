package com.sun.org.apache.bcel.internal.generic;

public class IF_ACMPEQ
  extends IfInstruction
{
  IF_ACMPEQ() {}
  
  public IF_ACMPEQ(InstructionHandle paramInstructionHandle)
  {
    super((short)165, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ACMPNE(this.target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ACMPEQ(this);
  }
}
