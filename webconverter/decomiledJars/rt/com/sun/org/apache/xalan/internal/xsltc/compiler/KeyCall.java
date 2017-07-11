package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class KeyCall
  extends FunctionCall
{
  private Expression _name;
  private Expression _value;
  private Type _valueType;
  private QName _resolvedQName = null;
  
  public KeyCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
    switch (argumentCount())
    {
    case 1: 
      this._name = null;
      this._value = argument(0);
      break;
    case 2: 
      this._name = argument(0);
      this._value = argument(1);
      break;
    default: 
      this._name = (this._value = null);
    }
  }
  
  public void addParentDependency()
  {
    if (this._resolvedQName == null) {
      return;
    }
    for (Object localObject = this; (localObject != null) && (!(localObject instanceof TopLevelElement)); localObject = ((SyntaxTreeNode)localObject).getParent()) {}
    TopLevelElement localTopLevelElement = (TopLevelElement)localObject;
    if (localTopLevelElement != null) {
      localTopLevelElement.addDependency(getSymbolTable().getKey(this._resolvedQName));
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = super.typeCheck(paramSymbolTable);
    if (this._name != null)
    {
      Type localType2 = this._name.typeCheck(paramSymbolTable);
      if ((this._name instanceof LiteralExpr))
      {
        LiteralExpr localLiteralExpr = (LiteralExpr)this._name;
        this._resolvedQName = getParser().getQNameIgnoreDefaultNs(localLiteralExpr.getValue());
      }
      else if (!(localType2 instanceof StringType))
      {
        this._name = new CastExpr(this._name, Type.String);
      }
    }
    this._valueType = this._value.typeCheck(paramSymbolTable);
    if ((this._valueType != Type.NodeSet) && (this._valueType != Type.Reference) && (this._valueType != Type.String))
    {
      this._value = new CastExpr(this._value, Type.String);
      this._valueType = this._value.typeCheck(paramSymbolTable);
    }
    addParentDependency();
    return localType1;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
    int j = localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "setDom", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)V");
    int k = localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "getKeyIndexIterator", "(" + this._valueType.toSignature() + "Z)" + "Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex$KeyIndexIterator;");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    if (this._name == null) {
      localInstructionList.append(new PUSH(localConstantPoolGen, "##id"));
    } else if (this._resolvedQName != null) {
      localInstructionList.append(new PUSH(localConstantPoolGen, this._resolvedQName.toString()));
    } else {
      this._name.translate(paramClassGenerator, paramMethodGenerator);
    }
    localInstructionList.append(new INVOKEVIRTUAL(i));
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(new INVOKEVIRTUAL(j));
    this._value.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(this._name != null ? ICONST_1 : ICONST_0);
    localInstructionList.append(new INVOKEVIRTUAL(k));
  }
}
