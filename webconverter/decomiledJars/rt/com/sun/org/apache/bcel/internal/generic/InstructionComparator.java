package com.sun.org.apache.bcel.internal.generic;

public abstract interface InstructionComparator
{
  public static final InstructionComparator DEFAULT = new InstructionComparator()
  {
    public boolean equals(Instruction paramAnonymousInstruction1, Instruction paramAnonymousInstruction2)
    {
      if (paramAnonymousInstruction1.opcode == paramAnonymousInstruction2.opcode) {
        if ((paramAnonymousInstruction1 instanceof Select))
        {
          InstructionHandle[] arrayOfInstructionHandle1 = ((Select)paramAnonymousInstruction1).getTargets();
          InstructionHandle[] arrayOfInstructionHandle2 = ((Select)paramAnonymousInstruction2).getTargets();
          if (arrayOfInstructionHandle1.length == arrayOfInstructionHandle2.length)
          {
            for (int i = 0; i < arrayOfInstructionHandle1.length; i++) {
              if (arrayOfInstructionHandle1[i] != arrayOfInstructionHandle2[i]) {
                return false;
              }
            }
            return true;
          }
        }
        else
        {
          if ((paramAnonymousInstruction1 instanceof BranchInstruction)) {
            return ((BranchInstruction)paramAnonymousInstruction1).target == ((BranchInstruction)paramAnonymousInstruction2).target;
          }
          if ((paramAnonymousInstruction1 instanceof ConstantPushInstruction)) {
            return ((ConstantPushInstruction)paramAnonymousInstruction1).getValue().equals(((ConstantPushInstruction)paramAnonymousInstruction2).getValue());
          }
          if ((paramAnonymousInstruction1 instanceof IndexedInstruction)) {
            return ((IndexedInstruction)paramAnonymousInstruction1).getIndex() == ((IndexedInstruction)paramAnonymousInstruction2).getIndex();
          }
          if ((paramAnonymousInstruction1 instanceof NEWARRAY)) {
            return ((NEWARRAY)paramAnonymousInstruction1).getTypecode() == ((NEWARRAY)paramAnonymousInstruction2).getTypecode();
          }
          return true;
        }
      }
      return false;
    }
  };
  
  public abstract boolean equals(Instruction paramInstruction1, Instruction paramInstruction2);
}
