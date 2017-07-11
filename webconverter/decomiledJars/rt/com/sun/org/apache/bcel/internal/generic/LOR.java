package com.sun.org.apache.bcel.internal.generic;

public class LOR
  extends ArithmeticInstruction
{
  public LOR()
  {
    super((short)129);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitLOR(this);
  }
}
