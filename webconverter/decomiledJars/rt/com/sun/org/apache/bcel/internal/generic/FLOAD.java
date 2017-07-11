package com.sun.org.apache.bcel.internal.generic;

public class FLOAD
  extends LoadInstruction
{
  FLOAD()
  {
    super((short)23, (short)34);
  }
  
  public FLOAD(int paramInt)
  {
    super((short)23, (short)34, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitFLOAD(this);
  }
}
