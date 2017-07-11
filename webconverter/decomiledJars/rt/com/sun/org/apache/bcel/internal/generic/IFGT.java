package com.sun.org.apache.bcel.internal.generic;

public class IFGT
  extends IfInstruction
{
  IFGT() {}
  
  public IFGT(InstructionHandle paramInstructionHandle)
  {
    super((short)157, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IFLE(this.target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFGT(this);
  }
}
