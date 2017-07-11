package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class Constant
  implements Cloneable, Node, Serializable
{
  protected byte tag;
  
  Constant(byte paramByte)
  {
    this.tag = paramByte;
  }
  
  public abstract void accept(Visitor paramVisitor);
  
  public abstract void dump(DataOutputStream paramDataOutputStream)
    throws IOException;
  
  public final byte getTag()
  {
    return this.tag;
  }
  
  public String toString()
  {
    return com.sun.org.apache.bcel.internal.Constants.CONSTANT_NAMES[this.tag] + "[" + this.tag + "]";
  }
  
  public Constant copy()
  {
    try
    {
      return (Constant)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
  
  static final Constant readConstant(DataInputStream paramDataInputStream)
    throws IOException, ClassFormatException
  {
    int i = paramDataInputStream.readByte();
    switch (i)
    {
    case 7: 
      return new ConstantClass(paramDataInputStream);
    case 9: 
      return new ConstantFieldref(paramDataInputStream);
    case 10: 
      return new ConstantMethodref(paramDataInputStream);
    case 11: 
      return new ConstantInterfaceMethodref(paramDataInputStream);
    case 8: 
      return new ConstantString(paramDataInputStream);
    case 3: 
      return new ConstantInteger(paramDataInputStream);
    case 4: 
      return new ConstantFloat(paramDataInputStream);
    case 5: 
      return new ConstantLong(paramDataInputStream);
    case 6: 
      return new ConstantDouble(paramDataInputStream);
    case 12: 
      return new ConstantNameAndType(paramDataInputStream);
    case 1: 
      return new ConstantUtf8(paramDataInputStream);
    }
    throw new ClassFormatException("Invalid byte tag in constant pool: " + i);
  }
}
