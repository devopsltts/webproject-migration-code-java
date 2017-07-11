package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StackMapType
  implements Cloneable
{
  private byte type;
  private int index = -1;
  private ConstantPool constant_pool;
  
  StackMapType(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramDataInputStream.readByte(), -1, paramConstantPool);
    if (hasIndex()) {
      setIndex(paramDataInputStream.readShort());
    }
    setConstantPool(paramConstantPool);
  }
  
  public StackMapType(byte paramByte, int paramInt, ConstantPool paramConstantPool)
  {
    setType(paramByte);
    setIndex(paramInt);
    setConstantPool(paramConstantPool);
  }
  
  public void setType(byte paramByte)
  {
    if ((paramByte < 0) || (paramByte > 8)) {
      throw new RuntimeException("Illegal type for StackMapType: " + paramByte);
    }
    this.type = paramByte;
  }
  
  public byte getType()
  {
    return this.type;
  }
  
  public void setIndex(int paramInt)
  {
    this.index = paramInt;
  }
  
  public int getIndex()
  {
    return this.index;
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(this.type);
    if (hasIndex()) {
      paramDataOutputStream.writeShort(getIndex());
    }
  }
  
  public final boolean hasIndex()
  {
    return (this.type == 7) || (this.type == 8);
  }
  
  private String printIndex()
  {
    if (this.type == 7) {
      return ", class=" + this.constant_pool.constantToString(this.index, (byte)7);
    }
    if (this.type == 8) {
      return ", offset=" + this.index;
    }
    return "";
  }
  
  public final String toString()
  {
    return "(type=" + com.sun.org.apache.bcel.internal.Constants.ITEM_NAMES[this.type] + printIndex() + ")";
  }
  
  public StackMapType copy()
  {
    try
    {
      return (StackMapType)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public final ConstantPool getConstantPool()
  {
    return this.constant_pool;
  }
  
  public final void setConstantPool(ConstantPool paramConstantPool)
  {
    this.constant_pool = paramConstantPool;
  }
}
