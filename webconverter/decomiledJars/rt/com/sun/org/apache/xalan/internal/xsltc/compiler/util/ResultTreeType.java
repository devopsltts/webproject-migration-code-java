package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class ResultTreeType
  extends Type
{
  private final String _methodName;
  
  protected ResultTreeType()
  {
    this._methodName = null;
  }
  
  public ResultTreeType(String paramString)
  {
    this._methodName = paramString;
  }
  
  public String toString()
  {
    return "result-tree";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return paramType instanceof ResultTreeType;
  }
  
  public String toSignature()
  {
    return "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;";
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return Util.getJCRefType(toSignature());
  }
  
  public String getMethodName()
  {
    return this._methodName;
  }
  
  public boolean implementedAsMethod()
  {
    return this._methodName != null;
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
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(POP);
    localInstructionList.append(ICONST_1);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (this._methodName == null)
    {
      int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValue", "()Ljava/lang/String;");
      localInstructionList.append(new INVOKEINTERFACE(i, 1));
    }
    else
    {
      String str = paramClassGenerator.getClassName();
      int j = paramMethodGenerator.getLocalIndex("current");
      localInstructionList.append(paramClassGenerator.loadTranslet());
      if (paramClassGenerator.isExternal()) {
        localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass(str)));
      }
      localInstructionList.append(DUP);
      localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref(str, "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
      int k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "<init>", "()V");
      localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler")));
      localInstructionList.append(DUP);
      localInstructionList.append(DUP);
      localInstructionList.append(new INVOKESPECIAL(k));
      LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable("rt_to_string_handler", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;"), null, null);
      localLocalVariableGen.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
      k = localConstantPoolGen.addMethodref(str, this._methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      localInstructionList.append(new INVOKEVIRTUAL(k));
      localLocalVariableGen.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen.getIndex())));
      k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;");
      localInstructionList.append(new INVOKEVIRTUAL(k));
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    Type.String.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (this._methodName == null)
    {
      localInstructionList.append(NOP);
    }
    else
    {
      String str = paramClassGenerator.getClassName();
      int i = paramMethodGenerator.getLocalIndex("current");
      localInstructionList.append(paramClassGenerator.loadTranslet());
      if (paramClassGenerator.isExternal()) {
        localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass(str)));
      }
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(paramMethodGenerator.loadDOM());
      int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getResultTreeFrag", "(IZ)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
      localInstructionList.append(new PUSH(localConstantPoolGen, 32));
      localInstructionList.append(new PUSH(localConstantPoolGen, false));
      localInstructionList.append(new INVOKEINTERFACE(j, 3));
      localInstructionList.append(DUP);
      LocalVariableGen localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("rt_to_reference_dom", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
      localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
      localLocalVariableGen2.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen2.getIndex())));
      j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getOutputDomBuilder", "()Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
      localInstructionList.append(new INVOKEINTERFACE(j, 1));
      localInstructionList.append(DUP);
      localInstructionList.append(DUP);
      LocalVariableGen localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("rt_to_reference_handler", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;"), null, null);
      localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
      j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V");
      localInstructionList.append(new INVOKEINTERFACE(j, 1));
      j = localConstantPoolGen.addMethodref(str, this._methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      localInstructionList.append(new INVOKEVIRTUAL(j));
      localLocalVariableGen1.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex())));
      j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V");
      localInstructionList.append(new INVOKEINTERFACE(j, 1));
      localLocalVariableGen2.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen2.getIndex())));
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeSetType paramNodeSetType)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(DUP);
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "setupMapping", "([Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
    localInstructionList.append(new INVOKEINTERFACE(i, 5));
    localInstructionList.append(DUP);
    int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localInstructionList.append(new INVOKEINTERFACE(j, 1));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ObjectType paramObjectType)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    translateTo(paramClassGenerator, paramMethodGenerator, Type.Boolean);
    return new FlowList(localInstructionList.append(new IFEQ(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    String str = paramClass.getName();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i;
    if (str.equals("org.w3c.dom.Node"))
    {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
    }
    else if (str.equals("org.w3c.dom.NodeList"))
    {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
    }
    else if (str.equals("java.lang.Object"))
    {
      localInstructionList.append(NOP);
    }
    else if (str.equals("java.lang.String"))
    {
      translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
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
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public String getClassName()
  {
    return "com.sun.org.apache.xalan.internal.xsltc.DOM";
  }
  
  public Instruction LOAD(int paramInt)
  {
    return new ALOAD(paramInt);
  }
  
  public Instruction STORE(int paramInt)
  {
    return new ASTORE(paramInt);
  }
}
