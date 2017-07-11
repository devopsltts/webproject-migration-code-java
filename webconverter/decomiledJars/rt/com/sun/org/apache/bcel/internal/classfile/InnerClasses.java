package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class InnerClasses
  extends Attribute
{
  private InnerClass[] inner_classes;
  private int number_of_classes;
  
  public InnerClasses(InnerClasses paramInnerClasses)
  {
    this(paramInnerClasses.getNameIndex(), paramInnerClasses.getLength(), paramInnerClasses.getInnerClasses(), paramInnerClasses.getConstantPool());
  }
  
  public InnerClasses(int paramInt1, int paramInt2, InnerClass[] paramArrayOfInnerClass, ConstantPool paramConstantPool)
  {
    super((byte)6, paramInt1, paramInt2, paramConstantPool);
    setInnerClasses(paramArrayOfInnerClass);
  }
  
  InnerClasses(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (InnerClass[])null, paramConstantPool);
    this.number_of_classes = paramDataInputStream.readUnsignedShort();
    this.inner_classes = new InnerClass[this.number_of_classes];
    for (int i = 0; i < this.number_of_classes; i++) {
      this.inner_classes[i] = new InnerClass(paramDataInputStream);
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitInnerClasses(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.number_of_classes);
    for (int i = 0; i < this.number_of_classes; i++) {
      this.inner_classes[i].dump(paramDataOutputStream);
    }
  }
  
  public final InnerClass[] getInnerClasses()
  {
    return this.inner_classes;
  }
  
  public final void setInnerClasses(InnerClass[] paramArrayOfInnerClass)
  {
    this.inner_classes = paramArrayOfInnerClass;
    this.number_of_classes = (paramArrayOfInnerClass == null ? 0 : paramArrayOfInnerClass.length);
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < this.number_of_classes; i++) {
      localStringBuffer.append(this.inner_classes[i].toString(this.constant_pool) + "\n");
    }
    return localStringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    InnerClasses localInnerClasses = (InnerClasses)clone();
    localInnerClasses.inner_classes = new InnerClass[this.number_of_classes];
    for (int i = 0; i < this.number_of_classes; i++) {
      localInnerClasses.inner_classes[i] = this.inner_classes[i].copy();
    }
    localInnerClasses.constant_pool = paramConstantPool;
    return localInnerClasses;
  }
}
