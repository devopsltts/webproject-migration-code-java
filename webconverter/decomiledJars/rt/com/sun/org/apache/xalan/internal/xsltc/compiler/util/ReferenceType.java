package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class ReferenceType
  extends Type
{
  protected ReferenceType() {}
  
  public String toString()
  {
    return "reference";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return this == paramType;
  }
  
  public String toSignature()
  {
    return "Ljava/lang/Object;";
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return com.sun.org.apache.bcel.internal.generic.Type.OBJECT;
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
    else if (paramType == Type.Boolean)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    }
    else if (paramType == Type.NodeSet)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (NodeSetType)paramType);
    }
    else if (paramType == Type.Node)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (NodeType)paramType);
    }
    else if (paramType == Type.ResultTree)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ResultTreeType)paramType);
    }
    else if (paramType == Type.Object)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ObjectType)paramType);
    }
    else if (paramType != Type.Reference)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("INTERNAL_ERR", paramType.toString());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType)
  {
    int i = paramMethodGenerator.getLocalIndex("current");
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (i < 0) {
      localInstructionList.append(new PUSH(localConstantPoolGen, 0));
    } else {
      localInstructionList.append(new ILOAD(i));
    }
    localInstructionList.append(paramMethodGenerator.loadDOM());
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "stringF", "(Ljava/lang/Object;ILcom/sun/org/apache/xalan/internal/xsltc/DOM;)Ljava/lang/String;");
    localInstructionList.append(new INVOKESTATIC(j));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramMethodGenerator.loadDOM());
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "numberF", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)D");
    localInstructionList.append(new INVOKESTATIC(i));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "booleanF", "(Ljava/lang/Object;)Z");
    localInstructionList.append(new INVOKESTATIC(i));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeSetType paramNodeSetType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeSet", "(Ljava/lang/Object;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localInstructionList.append(new INVOKESTATIC(i));
    i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "reset", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localInstructionList.append(new INVOKEINTERFACE(i, 1));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeType paramNodeType)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
    Type.NodeSet.translateTo(paramClassGenerator, paramMethodGenerator, paramNodeType);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ResultTreeType paramResultTreeType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToResultTree", "(Ljava/lang/Object;)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    localInstructionList.append(new INVOKESTATIC(i));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ObjectType paramObjectType)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToLong", "(Ljava/lang/Object;)J");
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToDouble", "(Ljava/lang/Object;)D");
    int k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToBoolean", "(Ljava/lang/Object;)Z");
    if (paramClass.getName().equals("java.lang.Object"))
    {
      localInstructionList.append(NOP);
    }
    else if (paramClass == Double.TYPE)
    {
      localInstructionList.append(new INVOKESTATIC(j));
    }
    else if (paramClass.getName().equals("java.lang.Double"))
    {
      localInstructionList.append(new INVOKESTATIC(j));
      Type.Real.translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
    }
    else if (paramClass == Float.TYPE)
    {
      localInstructionList.append(new INVOKESTATIC(j));
      localInstructionList.append(D2F);
    }
    else
    {
      int m;
      if (paramClass.getName().equals("java.lang.String"))
      {
        m = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToString", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Ljava/lang/String;");
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(new INVOKESTATIC(m));
      }
      else if (paramClass.getName().equals("org.w3c.dom.Node"))
      {
        m = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNode", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lorg/w3c/dom/Node;");
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(new INVOKESTATIC(m));
      }
      else if (paramClass.getName().equals("org.w3c.dom.NodeList"))
      {
        m = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeList", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lorg/w3c/dom/NodeList;");
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(new INVOKESTATIC(m));
      }
      else if (paramClass.getName().equals("com.sun.org.apache.xalan.internal.xsltc.DOM"))
      {
        translateTo(paramClassGenerator, paramMethodGenerator, Type.ResultTree);
      }
      else if (paramClass == Long.TYPE)
      {
        localInstructionList.append(new INVOKESTATIC(i));
      }
      else if (paramClass == Integer.TYPE)
      {
        localInstructionList.append(new INVOKESTATIC(i));
        localInstructionList.append(L2I);
      }
      else if (paramClass == Short.TYPE)
      {
        localInstructionList.append(new INVOKESTATIC(i));
        localInstructionList.append(L2I);
        localInstructionList.append(I2S);
      }
      else if (paramClass == Byte.TYPE)
      {
        localInstructionList.append(new INVOKESTATIC(i));
        localInstructionList.append(L2I);
        localInstructionList.append(I2B);
      }
      else if (paramClass == Character.TYPE)
      {
        localInstructionList.append(new INVOKESTATIC(i));
        localInstructionList.append(L2I);
        localInstructionList.append(I2C);
      }
      else if (paramClass == Boolean.TYPE)
      {
        localInstructionList.append(new INVOKESTATIC(k));
      }
      else if (paramClass.getName().equals("java.lang.Boolean"))
      {
        localInstructionList.append(new INVOKESTATIC(k));
        Type.Boolean.translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
      }
      else
      {
        ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
        paramClassGenerator.getParser().reportError(2, localErrorMsg);
      }
    }
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    if (paramClass.getName().equals("java.lang.Object"))
    {
      paramMethodGenerator.getInstructionList().append(NOP);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    translateTo(paramClassGenerator, paramMethodGenerator, paramBooleanType);
    return new FlowList(localInstructionList.append(new IFEQ(null)));
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  public Instruction LOAD(int paramInt)
  {
    return new ALOAD(paramInt);
  }
  
  public Instruction STORE(int paramInt)
  {
    return new ASTORE(paramInt);
  }
}
