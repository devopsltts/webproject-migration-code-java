package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPLT
  extends IfInstruction
{
  IF_ICMPLT() {}
  
  public IF_ICMPLT(InstructionHandle paramInstructionHandle)
  {
    super((short)161, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ICMPGE(this.target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPLT(this);
  }
}
