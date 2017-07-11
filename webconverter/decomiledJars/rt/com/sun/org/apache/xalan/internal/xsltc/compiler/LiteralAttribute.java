package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;

final class LiteralAttribute
  extends Instruction
{
  private final String _name;
  private final AttributeValue _value;
  
  public LiteralAttribute(String paramString1, String paramString2, Parser paramParser, SyntaxTreeNode paramSyntaxTreeNode)
  {
    this._name = paramString1;
    setParent(paramSyntaxTreeNode);
    this._value = AttributeValue.create(this, paramString2, paramParser);
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("LiteralAttribute name=" + this._name + " value=" + this._value);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    this._value.typeCheck(paramSymbolTable);
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  protected boolean contextDependent()
  {
    return this._value.contextDependent();
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new PUSH(localConstantPoolGen, this._name));
    this._value.translate(paramClassGenerator, paramMethodGenerator);
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    if (((localSyntaxTreeNode instanceof LiteralElement)) && (((LiteralElement)localSyntaxTreeNode).allAttributesUnique()))
    {
      int i = 0;
      int j = 0;
      ElemDesc localElemDesc = ((LiteralElement)localSyntaxTreeNode).getElemDesc();
      if (localElemDesc != null) {
        if (localElemDesc.isAttrFlagSet(this._name, 4))
        {
          i |= 0x2;
          j = 1;
        }
        else if (localElemDesc.isAttrFlagSet(this._name, 2))
        {
          i |= 0x4;
        }
      }
      if ((this._value instanceof SimpleAttributeValue))
      {
        String str = ((SimpleAttributeValue)this._value).toString();
        if ((!hasBadChars(str)) && (j == 0)) {
          i |= 0x1;
        }
      }
      localInstructionList.append(new PUSH(localConstantPoolGen, i));
      localInstructionList.append(paramMethodGenerator.uniqueAttribute());
    }
    else
    {
      localInstructionList.append(paramMethodGenerator.attribute());
    }
  }
  
  private boolean hasBadChars(String paramString)
  {
    for (int k : paramString.toCharArray()) {
      if ((k < 32) || (126 < k) || (k == 60) || (k == 62) || (k == 38) || (k == 34)) {
        return true;
      }
    }
    return false;
  }
  
  public String getName()
  {
    return this._name;
  }
  
  public AttributeValue getValue()
  {
    return this._value;
  }
}
