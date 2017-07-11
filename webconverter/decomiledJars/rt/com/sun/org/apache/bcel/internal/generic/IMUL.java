package com.sun.org.apache.bcel.internal.generic;

public class IMUL
  extends ArithmeticInstruction
{
  public IMUL()
  {
    super((short)104);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitIMUL(this);
  }
}
