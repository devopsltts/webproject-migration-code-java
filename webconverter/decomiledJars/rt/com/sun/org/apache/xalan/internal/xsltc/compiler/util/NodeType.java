package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class NodeType
  extends Type
{
  private final int _type;
  
  protected NodeType()
  {
    this(-1);
  }
  
  protected NodeType(int paramInt)
  {
    this._type = paramInt;
  }
  
  public int getType()
  {
    return this._type;
  }
  
  public String toString()
  {
    return "node-type";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return paramType instanceof NodeType;
  }
  
  public int hashCode()
  {
    return this._type;
  }
  
  public String toSignature()
  {
    return "I";
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return com.sun.org.apache.bcel.internal.generic.Type.INT;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    if (paramType == Type.String)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    }
    else if (paramType == Type.Boolean)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    }
    else if (paramType == Type.Real)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    }
    else if (paramType == Type.NodeSet)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (NodeSetType)paramType);
    }
    else if (paramType == Type.Reference)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    }
    else if (paramType == Type.Object)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ObjectType)paramType);
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
    int i;
    switch (this._type)
    {
    case 1: 
    case 9: 
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getElementValue", "(I)Ljava/lang/String;");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      break;
    case -1: 
    case 2: 
    case 7: 
    case 8: 
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(SWAP);
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      break;
    case 0: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    default: 
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramStringType.toString());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    FlowList localFlowList = translateToDesynthesized(paramClassGenerator, paramMethodGenerator, paramBooleanType);
    localInstructionList.append(ICONST_1);
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    localFlowList.backPatch(localInstructionList.append(ICONST_0));
    localBranchHandle.setTarget(localInstructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    Type.String.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeSetType paramNodeSetType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator")));
    localInstructionList.append(DUP_X1);
    localInstructionList.append(SWAP);
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator", "<init>", "(I)V");
    localInstructionList.append(new INVOKESPECIAL(i));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ObjectType paramObjectType)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    return new FlowList(localInstructionList.append(new IFEQ(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.Node")));
    localInstructionList.append(DUP_X1);
    localInstructionList.append(SWAP);
    localInstructionList.append(new PUSH(localConstantPoolGen, this._type));
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.Node", "<init>", "(II)V")));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    String str = paramClass.getName();
    if (str.equals("java.lang.String"))
    {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      return;
    }
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(SWAP);
    int i;
    if ((str.equals("org.w3c.dom.Node")) || (str.equals("java.lang.Object")))
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(I)Lorg/w3c/dom/Node;");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
    }
    else if (str.equals("org.w3c.dom.NodeList"))
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(I)Lorg/w3c/dom/NodeList;");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), str);
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
  }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.Node")));
    localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.Node", "node", "I")));
  }
  
  public String getClassName()
  {
    return "com.sun.org.apache.xalan.internal.xsltc.runtime.Node";
  }
  
  public Instruction LOAD(int paramInt)
  {
    return new ILOAD(paramInt);
  }
  
  public Instruction STORE(int paramInt)
  {
    return new ISTORE(paramInt);
  }
}
