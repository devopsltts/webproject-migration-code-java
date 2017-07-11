package com.sun.org.apache.bcel.internal.generic;

public class POP
  extends StackInstruction
  implements PopInstruction
{
  public POP()
  {
    super((short)87);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitPopInstruction(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitPOP(this);
  }
}
