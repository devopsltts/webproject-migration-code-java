package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class LOOKUPSWITCH
  extends Select
{
  LOOKUPSWITCH() {}
  
  public LOOKUPSWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle)
  {
    super((short)171, paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle);
    this.length = ((short)(9 + this.match_length * 8));
    this.fixed_length = this.length;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeInt(this.match_length);
    for (int i = 0; i < this.match_length; i++)
    {
      paramDataOutputStream.writeInt(this.match[i]);
      paramDataOutputStream.writeInt(this.indices[i] = getTargetOffset(this.targets[i]));
    }
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {
    super.initFromFile(paramByteSequence, paramBoolean);
    this.match_length = paramByteSequence.readInt();
    this.fixed_length = ((short)(9 + this.match_length * 8));
    this.length = ((short)(this.fixed_length + this.padding));
    this.match = new int[this.match_length];
    this.indices = new int[this.match_length];
    this.targets = new InstructionHandle[this.match_length];
    for (int i = 0; i < this.match_length; i++)
    {
      this.match[i] = paramByteSequence.readInt();
      this.indices[i] = paramByteSequence.readInt();
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitVariableLengthInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitSelect(this);
    paramVisitor.visitLOOKUPSWITCH(this);
  }
}
