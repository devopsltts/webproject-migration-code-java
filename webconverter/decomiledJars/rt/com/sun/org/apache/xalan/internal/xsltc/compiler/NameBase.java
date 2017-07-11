package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

class NameBase
  extends FunctionCall
{
  private Expression _param = null;
  private Type _paramType = Type.Node;
  
  public NameBase(QName paramQName)
  {
    super(paramQName);
  }
  
  public NameBase(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
    this._param = argument(0);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    switch (argumentCount())
    {
    case 0: 
      this._paramType = Type.Node;
      break;
    case 1: 
      this._paramType = this._param.typeCheck(paramSymbolTable);
      break;
    default: 
      throw new TypeCheckError(this);
    }
    if ((this._paramType != Type.NodeSet) && (this._paramType != Type.Node) && (this._paramType != Type.Reference)) {
      throw new TypeCheckError(this);
    }
    return this._type = Type.String;
  }
  
  public Type getType()
  {
    return this._type;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramMethodGenerator.loadDOM());
    if (argumentCount() == 0)
    {
      localInstructionList.append(paramMethodGenerator.loadContextNode());
    }
    else if (this._paramType == Type.Node)
    {
      this._param.translate(paramClassGenerator, paramMethodGenerator);
    }
    else if (this._paramType == Type.Reference)
    {
      this._param.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeSet", "(Ljava/lang/Object;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;")));
      localInstructionList.append(paramMethodGenerator.nextNode());
    }
    else
    {
      this._param.translate(paramClassGenerator, paramMethodGenerator);
      this._param.startIterator(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.nextNode());
    }
  }
}
