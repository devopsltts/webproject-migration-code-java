package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class GenerateIdCall
  extends FunctionCall
{
  public GenerateIdCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (argumentCount() == 0) {
      localInstructionList.append(paramMethodGenerator.loadContextNode());
    } else {
      argument().translate(paramClassGenerator, paramMethodGenerator);
    }
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "generate_idF", "(I)Ljava/lang/String;")));
  }
}
