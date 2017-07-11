package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPNE
  extends IfInstruction
{
  IF_ICMPNE() {}
  
  public IF_ICMPNE(InstructionHandle paramInstructionHandle)
  {
    super((short)160, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ICMPEQ(this.target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPNE(this);
  }
}
