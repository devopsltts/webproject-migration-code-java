package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public final class Deprecated
  extends Attribute
{
  private byte[] bytes;
  
  public Deprecated(Deprecated paramDeprecated)
  {
    this(paramDeprecated.getNameIndex(), paramDeprecated.getLength(), paramDeprecated.getBytes(), paramDeprecated.getConstantPool());
  }
  
  public Deprecated(int paramInt1, int paramInt2, byte[] paramArrayOfByte, ConstantPool paramConstantPool)
  {
    super((byte)8, paramInt1, paramInt2, paramConstantPool);
    this.bytes = paramArrayOfByte;
  }
  
  Deprecated(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (byte[])null, paramConstantPool);
    if (paramInt2 > 0)
    {
      this.bytes = new byte[paramInt2];
      paramDataInputStream.readFully(this.bytes);
      System.err.println("Deprecated attribute with length > 0");
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitDeprecated(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    if (this.length > 0) {
      paramDataOutputStream.write(this.bytes, 0, this.length);
    }
  }
  
  public final byte[] getBytes()
  {
    return this.bytes;
  }
  
  public final void setBytes(byte[] paramArrayOfByte)
  {
    this.bytes = paramArrayOfByte;
  }
  
  public final String toString()
  {
    return com.sun.org.apache.bcel.internal.Constants.ATTRIBUTE_NAMES[8];
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    Deprecated localDeprecated = (Deprecated)clone();
    if (this.bytes != null) {
      localDeprecated.bytes = ((byte[])this.bytes.clone());
    }
    localDeprecated.constant_pool = paramConstantPool;
    return localDeprecated;
  }
}
