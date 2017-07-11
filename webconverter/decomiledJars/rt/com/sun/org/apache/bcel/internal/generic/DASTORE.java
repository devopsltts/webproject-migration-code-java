package com.sun.org.apache.bcel.internal.generic;

public class DASTORE
  extends ArrayInstruction
  implements StackConsumer
{
  public DASTORE()
  {
    super((short)82);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitDASTORE(this);
  }
}
