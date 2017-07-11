package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

public abstract class Instruction
  implements Cloneable, Serializable
{
  protected short length = 1;
  protected short opcode = -1;
  private static InstructionComparator cmp = InstructionComparator.DEFAULT;
  
  Instruction() {}
  
  public Instruction(short paramShort1, short paramShort2)
  {
    this.length = paramShort2;
    this.opcode = paramShort1;
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(this.opcode);
  }
  
  public String getName()
  {
    return com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[this.opcode];
  }
  
  public String toString(boolean paramBoolean)
  {
    if (paramBoolean) {
      return getName() + "[" + this.opcode + "](" + this.length + ")";
    }
    return getName();
  }
  
  public String toString()
  {
    return toString(true);
  }
  
  public String toString(ConstantPool paramConstantPool)
  {
    return toString(false);
  }
  
  public Instruction copy()
  {
    Instruction localInstruction = null;
    if (InstructionConstants.INSTRUCTIONS[getOpcode()] != null) {
      localInstruction = this;
    } else {
      try
      {
        localInstruction = (Instruction)clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        System.err.println(localCloneNotSupportedException);
      }
    }
    return localInstruction;
  }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean)
    throws IOException
  {}
  
  public static final Instruction readInstruction(ByteSequence paramByteSequence)
    throws IOException
  {
    boolean bool = false;
    short s = (short)paramByteSequence.readUnsignedByte();
    Instruction localInstruction = null;
    if (s == 196)
    {
      bool = true;
      s = (short)paramByteSequence.readUnsignedByte();
    }
    if (InstructionConstants.INSTRUCTIONS[s] != null) {
      return InstructionConstants.INSTRUCTIONS[s];
    }
    Class localClass;
    try
    {
      localClass = Class.forName(className(s));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new ClassGenException("Illegal opcode detected.");
    }
    try
    {
      localInstruction = (Instruction)localClass.newInstance();
      if ((bool) && (!(localInstruction instanceof LocalVariableInstruction)) && (!(localInstruction instanceof IINC)) && (!(localInstruction instanceof RET))) {
        throw new Exception("Illegal opcode after wide: " + s);
      }
      localInstruction.setOpcode(s);
      localInstruction.initFromFile(paramByteSequence, bool);
    }
    catch (Exception localException)
    {
      throw new ClassGenException(localException.toString());
    }
    return localInstruction;
  }
  
  private static final String className(short paramShort)
  {
    String str = com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[paramShort].toUpperCase();
    try
    {
      int i = str.length();
      int j = str.charAt(i - 2);
      int k = str.charAt(i - 1);
      if ((j == 95) && (k >= 48) && (k <= 53)) {
        str = str.substring(0, i - 2);
      }
      if (str.equals("ICONST_M1")) {
        str = "ICONST";
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      System.err.println(localStringIndexOutOfBoundsException);
    }
    return "com.sun.org.apache.bcel.internal.generic." + str;
  }
  
  public int consumeStack(ConstantPoolGen paramConstantPoolGen)
  {
    return com.sun.org.apache.bcel.internal.Constants.CONSUME_STACK[this.opcode];
  }
  
  public int produceStack(ConstantPoolGen paramConstantPoolGen)
  {
    return com.sun.org.apache.bcel.internal.Constants.PRODUCE_STACK[this.opcode];
  }
  
  public short getOpcode()
  {
    return this.opcode;
  }
  
  public int getLength()
  {
    return this.length;
  }
  
  private void setOpcode(short paramShort)
  {
    this.opcode = paramShort;
  }
  
  void dispose() {}
  
  public abstract void accept(Visitor paramVisitor);
  
  public static InstructionComparator getComparator()
  {
    return cmp;
  }
  
  public static void setComparator(InstructionComparator paramInstructionComparator)
  {
    cmp = paramInstructionComparator;
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject instanceof Instruction) ? cmp.equals(this, (Instruction)paramObject) : false;
  }
}
