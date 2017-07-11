package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;

final class CurrentCall
  extends FunctionCall
{
  public CurrentCall(QName paramQName)
  {
    super(paramQName);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    paramMethodGenerator.getInstructionList().append(paramMethodGenerator.loadCurrentNode());
  }
}
