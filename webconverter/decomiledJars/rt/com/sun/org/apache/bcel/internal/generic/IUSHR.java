package com.sun.org.apache.bcel.internal.generic;

public class IUSHR
  extends ArithmeticInstruction
{
  public IUSHR()
  {
    super((short)124);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitIUSHR(this);
  }
}
