package com.sun.org.apache.bcel.internal.generic;

public class IASTORE
  extends ArrayInstruction
  implements StackConsumer
{
  public IASTORE()
  {
    super((short)79);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitIASTORE(this);
  }
}
