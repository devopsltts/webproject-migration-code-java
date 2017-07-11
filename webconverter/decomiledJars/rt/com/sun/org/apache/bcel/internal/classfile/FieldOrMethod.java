package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class FieldOrMethod
  extends AccessFlags
  implements Cloneable, Node
{
  protected int name_index;
  protected int signature_index;
  protected int attributes_count;
  protected Attribute[] attributes;
  protected ConstantPool constant_pool;
  
  FieldOrMethod() {}
  
  protected FieldOrMethod(FieldOrMethod paramFieldOrMethod)
  {
    this(paramFieldOrMethod.getAccessFlags(), paramFieldOrMethod.getNameIndex(), paramFieldOrMethod.getSignatureIndex(), paramFieldOrMethod.getAttributes(), paramFieldOrMethod.getConstantPool());
  }
  
  protected FieldOrMethod(DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException, ClassFormatException
  {
    this(paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), paramDataInputStream.readUnsignedShort(), null, paramConstantPool);
    this.attributes_count = paramDataInputStream.readUnsignedShort();
    this.attributes = new Attribute[this.attributes_count];
    for (int i = 0; i < this.attributes_count; i++) {
      this.attributes[i] = Attribute.readAttribute(paramDataInputStream, paramConstantPool);
    }
  }
  
  protected FieldOrMethod(int paramInt1, int paramInt2, int paramInt3, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool)
  {
    this.access_flags = paramInt1;
    this.name_index = paramInt2;
    this.signature_index = paramInt3;
    this.constant_pool = paramConstantPool;
    setAttributes(paramArrayOfAttribute);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(this.access_flags);
    paramDataOutputStream.writeShort(this.name_index);
    paramDataOutputStream.writeShort(this.signature_index);
    paramDataOutputStream.writeShort(this.attributes_count);
    for (int i = 0; i < this.attributes_count; i++) {
      this.attributes[i].dump(paramDataOutputStream);
    }
  }
  
  public final Attribute[] getAttributes()
  {
    return this.attributes;
  }
  
  public final void setAttributes(Attribute[] paramArrayOfAttribute)
  {
    this.attributes = paramArrayOfAttribute;
    this.attributes_count = (paramArrayOfAttribute == null ? 0 : paramArrayOfAttribute.length);
  }
  
  public final ConstantPool getConstantPool()
  {
    return this.constant_pool;
  }
  
  public final void setConstantPool(ConstantPool paramConstantPool)
  {
    this.constant_pool = paramConstantPool;
  }
  
  public final int getNameIndex()
  {
    return this.name_index;
  }
  
  public final void setNameIndex(int paramInt)
  {
    this.name_index = paramInt;
  }
  
  public final int getSignatureIndex()
  {
    return this.signature_index;
  }
  
  public final void setSignatureIndex(int paramInt)
  {
    this.signature_index = paramInt;
  }
  
  public final String getName()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.name_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  public final String getSignature()
  {
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)this.constant_pool.getConstant(this.signature_index, (byte)1);
    return localConstantUtf8.getBytes();
  }
  
  protected FieldOrMethod copy_(ConstantPool paramConstantPool)
  {
    FieldOrMethod localFieldOrMethod = null;
    try
    {
      localFieldOrMethod = (FieldOrMethod)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    localFieldOrMethod.constant_pool = paramConstantPool;
    localFieldOrMethod.attributes = new Attribute[this.attributes_count];
    for (int i = 0; i < this.attributes_count; i++) {
      localFieldOrMethod.attributes[i] = this.attributes[i].copy(paramConstantPool);
    }
    return localFieldOrMethod;
  }
}
