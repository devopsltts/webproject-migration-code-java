package com.sun.org.apache.bcel.internal.generic;

public class LSTORE
  extends StoreInstruction
{
  LSTORE()
  {
    super((short)55, (short)63);
  }
  
  public LSTORE(int paramInt)
  {
    super((short)55, (short)63, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitLSTORE(this);
  }
}
