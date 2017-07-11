package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class BooleanType
  extends Type
{
  protected BooleanType() {}
  
  public String toString()
  {
    return "boolean";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return this == paramType;
  }
  
  public String toSignature()
  {
    return "Z";
  }
  
  public boolean isSimple()
  {
    return true;
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    if (paramType == Type.String)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    }
    else if (paramType == Type.Real)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    }
    else if (paramType == Type.Reference)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    BranchHandle localBranchHandle1 = localInstructionList.append(new IFEQ(null));
    localInstructionList.append(new PUSH(localConstantPoolGen, "true"));
    BranchHandle localBranchHandle2 = localInstructionList.append(new GOTO(null));
    localBranchHandle1.setTarget(localInstructionList.append(new PUSH(localConstantPoolGen, "false")));
    localBranchHandle2.setTarget(localInstructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType)
  {
    paramMethodGenerator.getInstructionList().append(I2D);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("java.lang.Boolean")));
    localInstructionList.append(DUP_X1);
    localInstructionList.append(SWAP);
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("java.lang.Boolean", "<init>", "(Z)V")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    if (paramClass == Boolean.TYPE)
    {
      paramMethodGenerator.getInstructionList().append(NOP);
    }
    else if (paramClass.isAssignableFrom(Boolean.class))
    {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, paramClass);
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
  }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("java.lang.Boolean")));
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("java.lang.Boolean", "booleanValue", "()Z")));
  }
  
  public Instruction LOAD(int paramInt)
  {
    return new ILOAD(paramInt);
  }
  
  public Instruction STORE(int paramInt)
  {
    return new ISTORE(paramInt);
  }
  
  public BranchInstruction GT(boolean paramBoolean)
  {
    return paramBoolean ? new IFGT(null) : new IF_ICMPGT(null);
  }
  
  public BranchInstruction GE(boolean paramBoolean)
  {
    return paramBoolean ? new IFGE(null) : new IF_ICMPGE(null);
  }
  
  public BranchInstruction LT(boolean paramBoolean)
  {
    return paramBoolean ? new IFLT(null) : new IF_ICMPLT(null);
  }
  
  public BranchInstruction LE(boolean paramBoolean)
  {
    return paramBoolean ? new IFLE(null) : new IF_ICMPLE(null);
  }
}
