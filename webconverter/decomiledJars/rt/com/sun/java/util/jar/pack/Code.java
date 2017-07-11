package com.sun.java.util.jar.pack;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

class Code
  extends Attribute.Holder
{
  Package.Class.Method m;
  private static final ConstantPool.Entry[] noRefs = ConstantPool.noRefs;
  int max_stack;
  int max_locals;
  ConstantPool.Entry[] handler_class = noRefs;
  int[] handler_start = Constants.noInts;
  int[] handler_end = Constants.noInts;
  int[] handler_catch = Constants.noInts;
  byte[] bytes;
  Fixups fixups;
  Object insnMap;
  static final boolean shrinkMaps = true;
  
  public Code(Package.Class.Method paramMethod)
  {
    this.m = paramMethod;
  }
  
  public Package.Class.Method getMethod()
  {
    return this.m;
  }
  
  public Package.Class thisClass()
  {
    return this.m.thisClass();
  }
  
  public Package getPackage()
  {
    return this.m.thisClass().getPackage();
  }
  
  public ConstantPool.Entry[] getCPMap()
  {
    return this.m.getCPMap();
  }
  
  int getLength()
  {
    return this.bytes.length;
  }
  
  int getMaxStack()
  {
    return this.max_stack;
  }
  
  void setMaxStack(int paramInt)
  {
    this.max_stack = paramInt;
  }
  
  int getMaxNALocals()
  {
    int i = this.m.getArgumentSize();
    return this.max_locals - i;
  }
  
  void setMaxNALocals(int paramInt)
  {
    int i = this.m.getArgumentSize();
    this.max_locals = (i + paramInt);
  }
  
  int getHandlerCount()
  {
    assert (this.handler_class.length == this.handler_start.length);
    assert (this.handler_class.length == this.handler_end.length);
    assert (this.handler_class.length == this.handler_catch.length);
    return this.handler_class.length;
  }
  
  void setHandlerCount(int paramInt)
  {
    if (paramInt > 0)
    {
      this.handler_class = new ConstantPool.Entry[paramInt];
      this.handler_start = new int[paramInt];
      this.handler_end = new int[paramInt];
      this.handler_catch = new int[paramInt];
    }
  }
  
  void setBytes(byte[] paramArrayOfByte)
  {
    this.bytes = paramArrayOfByte;
    if (this.fixups != null) {
      this.fixups.setBytes(paramArrayOfByte);
    }
  }
  
  void setInstructionMap(int[] paramArrayOfInt, int paramInt)
  {
    this.insnMap = allocateInstructionMap(paramArrayOfInt, paramInt);
  }
  
  void setInstructionMap(int[] paramArrayOfInt)
  {
    setInstructionMap(paramArrayOfInt, paramArrayOfInt.length);
  }
  
  int[] getInstructionMap()
  {
    return expandInstructionMap(getInsnMap());
  }
  
  void addFixups(Collection<Fixups.Fixup> paramCollection)
  {
    if (this.fixups == null) {
      this.fixups = new Fixups(this.bytes);
    }
    assert (this.fixups.getBytes() == this.bytes);
    this.fixups.addAll(paramCollection);
  }
  
  public void trimToSize()
  {
    if (this.fixups != null)
    {
      this.fixups.trimToSize();
      if (this.fixups.size() == 0) {
        this.fixups = null;
      }
    }
    super.trimToSize();
  }
  
  protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
  {
    int i = getPackage().verbose;
    if (i > 2) {
      System.out.println("Reference scan " + this);
    }
    paramCollection.addAll(Arrays.asList(this.handler_class));
    if (this.fixups != null)
    {
      this.fixups.visitRefs(paramCollection);
    }
    else
    {
      ConstantPool.Entry[] arrayOfEntry = getCPMap();
      for (Instruction localInstruction = instructionAt(0); localInstruction != null; localInstruction = localInstruction.next())
      {
        if (i > 4) {
          System.out.println(localInstruction);
        }
        int j = localInstruction.getCPIndex();
        if (j >= 0) {
          paramCollection.add(arrayOfEntry[j]);
        }
      }
    }
    super.visitRefs(paramInt, paramCollection);
  }
  
  private Object allocateInstructionMap(int[] paramArrayOfInt, int paramInt)
  {
    int i = getLength();
    int j;
    if (i <= 255)
    {
      localObject = new byte[paramInt + 1];
      for (j = 0; j < paramInt; j++) {
        localObject[j] = ((byte)(paramArrayOfInt[j] + -128));
      }
      localObject[paramInt] = ((byte)(i + -128));
      return localObject;
    }
    if (i < 65535)
    {
      localObject = new short[paramInt + 1];
      for (j = 0; j < paramInt; j++) {
        localObject[j] = ((short)(paramArrayOfInt[j] + 32768));
      }
      localObject[paramInt] = ((short)(i + 32768));
      return localObject;
    }
    Object localObject = Arrays.copyOf(paramArrayOfInt, paramInt + 1);
    localObject[paramInt] = i;
    return localObject;
  }
  
  private int[] expandInstructionMap(Object paramObject)
  {
    Object localObject;
    int[] arrayOfInt;
    int i;
    if ((paramObject instanceof byte[]))
    {
      localObject = (byte[])paramObject;
      arrayOfInt = new int[localObject.length - 1];
      for (i = 0; i < arrayOfInt.length; i++) {
        localObject[i] -= Byte.MIN_VALUE;
      }
    }
    else if ((paramObject instanceof short[]))
    {
      localObject = (short[])paramObject;
      arrayOfInt = new int[localObject.length - 1];
      for (i = 0; i < arrayOfInt.length; i++) {
        localObject[i] -= -128;
      }
    }
    else
    {
      localObject = (int[])paramObject;
      arrayOfInt = Arrays.copyOfRange((int[])localObject, 0, localObject.length - 1);
    }
    return arrayOfInt;
  }
  
  Object getInsnMap()
  {
    if (this.insnMap != null) {
      return this.insnMap;
    }
    int[] arrayOfInt = new int[getLength()];
    int i = 0;
    for (Instruction localInstruction = instructionAt(0); localInstruction != null; localInstruction = localInstruction.next()) {
      arrayOfInt[(i++)] = localInstruction.getPC();
    }
    this.insnMap = allocateInstructionMap(arrayOfInt, i);
    return this.insnMap;
  }
  
  public int encodeBCI(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt > getLength())) {
      return paramInt;
    }
    Object localObject1 = getInsnMap();
    Object localObject2;
    int j;
    int i;
    if ((localObject1 instanceof byte[]))
    {
      localObject2 = (byte[])localObject1;
      j = localObject2.length;
      i = Arrays.binarySearch((byte[])localObject2, (byte)(paramInt + -128));
    }
    else if ((localObject1 instanceof short[]))
    {
      localObject2 = (short[])localObject1;
      j = localObject2.length;
      i = Arrays.binarySearch((short[])localObject2, (short)(paramInt + 32768));
    }
    else
    {
      localObject2 = (int[])localObject1;
      j = localObject2.length;
      i = Arrays.binarySearch((int[])localObject2, paramInt);
    }
    assert (i != -1);
    assert (i != 0);
    assert (i != j);
    assert (i != -j - 1);
    return i >= 0 ? i : j + paramInt - (-i - 1);
  }
  
  public int decodeBCI(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt > getLength())) {
      return paramInt;
    }
    Object localObject1 = getInsnMap();
    Object localObject2;
    int j;
    int i;
    int k;
    if ((localObject1 instanceof byte[]))
    {
      localObject2 = (byte[])localObject1;
      j = localObject2.length;
      if (paramInt < j) {
        return localObject2[paramInt] - Byte.MIN_VALUE;
      }
      i = Arrays.binarySearch((byte[])localObject2, (byte)(paramInt + -128));
      if (i < 0) {
        i = -i - 1;
      }
      k = paramInt - j + -128;
      while (localObject2[(i - 1)] - (i - 1) > k) {
        i--;
      }
    }
    else if ((localObject1 instanceof short[]))
    {
      localObject2 = (short[])localObject1;
      j = localObject2.length;
      if (paramInt < j) {
        return localObject2[paramInt] - Short.MIN_VALUE;
      }
      i = Arrays.binarySearch((short[])localObject2, (short)(paramInt + 32768));
      if (i < 0) {
        i = -i - 1;
      }
      k = paramInt - j + 32768;
      while (localObject2[(i - 1)] - (i - 1) > k) {
        i--;
      }
    }
    else
    {
      localObject2 = (int[])localObject1;
      j = localObject2.length;
      if (paramInt < j) {
        return localObject2[paramInt];
      }
      i = Arrays.binarySearch((int[])localObject2, paramInt);
      if (i < 0) {
        i = -i - 1;
      }
      k = paramInt - j;
      while (localObject2[(i - 1)] - (i - 1) > k) {
        i--;
      }
    }
    return paramInt - j + i;
  }
  
  public void finishRefs(ConstantPool.Index paramIndex)
  {
    if (this.fixups != null)
    {
      this.fixups.finishRefs(paramIndex);
      this.fixups = null;
    }
  }
  
  Instruction instructionAt(int paramInt)
  {
    return Instruction.at(this.bytes, paramInt);
  }
  
  static boolean flagsRequireCode(int paramInt)
  {
    return (paramInt & 0x500) == 0;
  }
  
  public String toString()
  {
    return this.m + ".Code";
  }
  
  public int getInt(int paramInt)
  {
    return Instruction.getInt(this.bytes, paramInt);
  }
  
  public int getShort(int paramInt)
  {
    return Instruction.getShort(this.bytes, paramInt);
  }
  
  public int getByte(int paramInt)
  {
    return Instruction.getByte(this.bytes, paramInt);
  }
  
  void setInt(int paramInt1, int paramInt2)
  {
    Instruction.setInt(this.bytes, paramInt1, paramInt2);
  }
  
  void setShort(int paramInt1, int paramInt2)
  {
    Instruction.setShort(this.bytes, paramInt1, paramInt2);
  }
  
  void setByte(int paramInt1, int paramInt2)
  {
    Instruction.setByte(this.bytes, paramInt1, paramInt2);
  }
}
