package com.sun.org.apache.bcel.internal.generic;

public class DUP2
  extends StackInstruction
  implements PushInstruction
{
  public DUP2()
  {
    super((short)92);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitDUP2(this);
  }
}
