package com.sun.org.apache.bcel.internal.generic;

public class L2D
  extends ConversionInstruction
{
  public L2D()
  {
    super((short)138);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitL2D(this);
  }
}
