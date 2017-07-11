package com.sun.org.apache.bcel.internal.generic;

public class DNEG
  extends ArithmeticInstruction
{
  public DNEG()
  {
    super((short)119);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitDNEG(this);
  }
}
