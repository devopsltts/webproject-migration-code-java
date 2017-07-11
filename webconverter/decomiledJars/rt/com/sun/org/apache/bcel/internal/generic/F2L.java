package com.sun.org.apache.bcel.internal.generic;

public class F2L
  extends ConversionInstruction
{
  public F2L()
  {
    super((short)140);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitF2L(this);
  }
}
