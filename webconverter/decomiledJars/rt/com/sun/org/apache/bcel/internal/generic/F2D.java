package com.sun.org.apache.bcel.internal.generic;

public class F2D
  extends ConversionInstruction
{
  public F2D()
  {
    super((short)141);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitF2D(this);
  }
}
