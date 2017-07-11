package com.sun.org.apache.bcel.internal.generic;

public class FADD
  extends ArithmeticInstruction
{
  public FADD()
  {
    super((short)98);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitFADD(this);
  }
}
