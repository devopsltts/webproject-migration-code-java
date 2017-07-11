package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;

final class PositionCall
  extends FunctionCall
{
  public PositionCall(QName paramQName)
  {
    super(paramQName);
  }
  
  public boolean hasPositionCall()
  {
    return true;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((paramMethodGenerator instanceof CompareGenerator))
    {
      localInstructionList.append(((CompareGenerator)paramMethodGenerator).loadCurrentNode());
    }
    else if ((paramMethodGenerator instanceof TestGenerator))
    {
      localInstructionList.append(new ILOAD(2));
    }
    else
    {
      ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
      int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "getPosition", "()I");
      localInstructionList.append(paramMethodGenerator.loadIterator());
      localInstructionList.append(new INVOKEINTERFACE(i, 1));
    }
  }
}
