package com.sun.org.apache.bcel.internal.generic;

public class FMUL
  extends ArithmeticInstruction
{
  public FMUL()
  {
    super((short)106);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitFMUL(this);
  }
}
