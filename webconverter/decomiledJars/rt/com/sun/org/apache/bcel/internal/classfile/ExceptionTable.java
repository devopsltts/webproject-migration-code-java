package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ExceptionTable
  extends Attribute
{
  private int number_of_exceptions;
  private int[] exception_index_table;
  
  public ExceptionTable(ExceptionTable paramExceptionTable)
  {
    this(paramExceptionTable.getNameIndex(), paramExceptionTable.getLength(), paramExceptionTable.getExceptionIndexTable(), paramExceptionTable.getConstantPool());
  }
  
  public ExceptionTable(int paramInt1, int paramInt2, int[] paramArrayOfInt, ConstantPool paramConstantPool)
  {
    super((byte)3, paramInt1, paramInt2, paramConstantPool);
    setExceptionIndexTable(paramArrayOfInt);
  }
  
  ExceptionTable(int paramInt1, int paramInt2, DataInputStream paramDataInputStream, ConstantPool paramConstantPool)
    throws IOException
  {
    this(paramInt1, paramInt2, (int[])null, paramConstantPool);
    this.number_of_exceptions = paramDataInputStream.readUnsignedShort();
    this.exception_index_table = new int[this.number_of_exceptions];
    for (int i = 0; i < this.number_of_exceptions; i++) {
      this.exception_index_table[i] = paramDataInputStream.readUnsignedShort();
    }
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionTable(this);
  }
  
  public final void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    super.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.number_of_exceptions);
    for (int i = 0; i < this.number_of_exceptions; i++) {
      paramDataOutputStream.writeShort(this.exception_index_table[i]);
    }
  }
  
  public final int[] getExceptionIndexTable()
  {
    return this.exception_index_table;
  }
  
  public final int getNumberOfExceptions()
  {
    return this.number_of_exceptions;
  }
  
  public final String[] getExceptionNames()
  {
    String[] arrayOfString = new String[this.number_of_exceptions];
    for (int i = 0; i < this.number_of_exceptions; i++) {
      arrayOfString[i] = this.constant_pool.getConstantString(this.exception_index_table[i], 7).replace('/', '.');
    }
    return arrayOfString;
  }
  
  public final void setExceptionIndexTable(int[] paramArrayOfInt)
  {
    this.exception_index_table = paramArrayOfInt;
    this.number_of_exceptions = (paramArrayOfInt == null ? 0 : paramArrayOfInt.length);
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("");
    for (int i = 0; i < this.number_of_exceptions; i++)
    {
      String str = this.constant_pool.getConstantString(this.exception_index_table[i], (byte)7);
      localStringBuffer.append(Utility.compactClassName(str, false));
      if (i < this.number_of_exceptions - 1) {
        localStringBuffer.append(", ");
      }
    }
    return localStringBuffer.toString();
  }
  
  public Attribute copy(ConstantPool paramConstantPool)
  {
    ExceptionTable localExceptionTable = (ExceptionTable)clone();
    localExceptionTable.exception_index_table = ((int[])this.exception_index_table.clone());
    localExceptionTable.constant_pool = paramConstantPool;
    return localExceptionTable;
  }
}
