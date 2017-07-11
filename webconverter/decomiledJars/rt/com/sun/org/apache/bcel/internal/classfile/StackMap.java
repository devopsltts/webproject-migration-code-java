package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StackMap
  extends Attribute
  implements Node
{
  private int map_length;
  private StackMapEntry[] map;
  
  public StackMap(int paramInt1, int paramInt2, StackMapEntry[] paramArrayOfStackMapEntry, ConstantPool paramConstantPool)
  {
    super((byte)11, paramInt1, paramInt2, paramConstantPool);
    setStackMap(paramArrayOfStackMapEntry);
  }
  
  StackMap(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (StackMapEntry[])null, paramConstantPool);
    this.map_length = paramDataInputStream.readUnsignedShort();
    this.map = new StackMapEntry[this.map_length];
    for (int i = 0; i < this.map_length; i++) {
      this.map[i] = new StackMapEntry(paramDataInputStream, paramConstantPool);
    }
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.map_length);
    for (int i = 0; i < this.map_length; i++) {
      this.map[i].dump(paramDataOutputStream);
    }
  }
  
  public final StackMapEntry[] getStackMap()
  {
    return this.map;
  }
  
  public final void setStackMap(StackMapEntry[] paramArrayOfStackMapEntry)
  {
    this.map = paramArrayOfStackMapEntry;
    this.map_length = (paramArrayOfStackMapEntry == null ? 0 : paramArrayOfStackMapEntry.length);
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("StackMap(");
    for (int i = 0; i < this.map_length; i++)
    {
      localStringBuffer.append(this.map[i].toString());
      if (i < this.map_length - 1) {
        localStringBuffer.append(", ");
      }
    }
    localStringBuffer.append(')');
    return localStringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    StackMap localStackMap = (StackMap)clone();
    localStackMap.map = new StackMapEntry[this.map_length];
    for (int i = 0; i < this.map_length; i++) {
      localStackMap.map[i] = this.map[i].copy();
    }
    localStackMap.constant_pool = paramConstantPool;
    return localStackMap;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackMap(this);
  }
  
  public final int getMapLength()
  {
    return this.map_length;
  }
}
