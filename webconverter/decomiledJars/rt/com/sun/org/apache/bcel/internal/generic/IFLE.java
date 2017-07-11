package com.sun.org.apache.bcel.internal.generic;

public class IFLE
  extends IfInstruction
{
  IFLE() {}
  
  public IFLE(InstructionHandle paramInstructionHandle)
  {
    super((short)158, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IFGT(this.target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFLE(this);
  }
}
