package com.sun.org.apache.bcel.internal.generic;

public class F2I
  extends ConversionInstruction
{
  public F2I()
  {
    super((short)139);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitF2I(this);
  }
}
