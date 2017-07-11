package com.sun.org.apache.bcel.internal.generic;

public class DADD
  extends ArithmeticInstruction
{
  public DADD()
  {
    super((short)99);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitDADD(this);
  }
}
