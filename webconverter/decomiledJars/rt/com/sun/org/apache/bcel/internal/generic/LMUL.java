package com.sun.org.apache.bcel.internal.generic;

public class LMUL
  extends ArithmeticInstruction
{
  public LMUL()
  {
    super((short)105);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitLMUL(this);
  }
}
