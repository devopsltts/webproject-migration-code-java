package com.sun.org.apache.bcel.internal.generic;

public class DDIV
  extends ArithmeticInstruction
{
  public DDIV()
  {
    super((short)111);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitDDIV(this);
  }
}
