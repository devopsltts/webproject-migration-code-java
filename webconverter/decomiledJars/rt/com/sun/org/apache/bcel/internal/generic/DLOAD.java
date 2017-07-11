package com.sun.org.apache.bcel.internal.generic;

public class DLOAD
  extends LoadInstruction
{
  DLOAD()
  {
    super((short)24, (short)38);
  }
  
  public DLOAD(int paramInt)
  {
    super((short)24, (short)38, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitDLOAD(this);
  }
}
