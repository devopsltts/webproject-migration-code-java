package com.sun.org.apache.bcel.internal.generic;

public class ISTORE
  extends StoreInstruction
{
  ISTORE()
  {
    super((short)54, (short)59);
  }
  
  public ISTORE(int paramInt)
  {
    super((short)54, (short)59, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitISTORE(this);
  }
}
