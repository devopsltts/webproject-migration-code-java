package com.sun.org.apache.bcel.internal.generic;

public class ILOAD
  extends LoadInstruction
{
  ILOAD()
  {
    super((short)21, (short)26);
  }
  
  public ILOAD(int paramInt)
  {
    super((short)21, (short)26, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitILOAD(this);
  }
}
