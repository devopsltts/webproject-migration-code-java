package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.IOException;

public final class ConstantFieldref
  extends ConstantCP
{
  public ConstantFieldref(ConstantFieldref paramConstantFieldref)
  {
    super((byte)9, paramConstantFieldref.getClassIndex(), paramConstantFieldref.getNameAndTypeIndex());
  }
  
  ConstantFieldref(DataInputStream paramDataInputStream)
    throws IOException
  {
    super((byte)9, paramDataInputStream);
  }
  
  public ConstantFieldref(int paramInt1, int paramInt2)
  {
    super((byte)9, paramInt1, paramInt2);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitConstantFieldref(this);
  }
}
