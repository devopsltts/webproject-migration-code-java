package com.sun.org.apache.bcel.internal.generic;

public class I2F
  extends ConversionInstruction
{
  public I2F()
  {
    super((short)134);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitI2F(this);
  }
}
