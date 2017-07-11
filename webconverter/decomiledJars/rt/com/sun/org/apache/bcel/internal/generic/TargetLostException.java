package com.sun.org.apache.bcel.internal.generic;

public final class TargetLostException
  extends Exception
{
  private InstructionHandle[] targets;
  
  TargetLostException(InstructionHandle[] paramArrayOfInstructionHandle, String paramString)
  {
    super(paramString);
    this.targets = paramArrayOfInstructionHandle;
  }
  
  public InstructionHandle[] getTargets()
  {
    return this.targets;
  }
}
