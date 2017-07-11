package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;

public class LDC_W
  extends LDC
{
  LDC_W() {}
  
  public LDC_W(int paramInt)
  {
    super(paramInt);
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    setIndex(paramByteSequence.readUnsignedShort());
    this.opcode = 19;
  }
}
