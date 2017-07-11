package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Fallback
  extends Instruction
{
  private boolean _active = false;
  
  Fallback() {}
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (this._active) {
      return typeCheckContents(paramSymbolTable);
    }
    return Type.Void;
  }
  
  public void activate()
  {
    this._active = true;
  }
  
  public String toString()
  {
    return "fallback";
  }
  
  public void parseContents(Parser paramParser)
  {
    if (this._active) {
      parseChildren(paramParser);
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (this._active) {
      translateContents(paramClassGenerator, paramMethodGenerator);
    }
  }
}
