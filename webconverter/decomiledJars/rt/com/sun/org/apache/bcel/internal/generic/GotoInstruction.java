package com.sun.org.apache.bcel.internal.generic;

public abstract class GotoInstruction
  extends BranchInstruction
  implements UnconditionalBranch
{
  GotoInstruction(short paramShort, InstructionHandle paramInstructionHandle)
  {
    super(paramShort, paramInstructionHandle);
  }
  
  GotoInstruction() {}
}
