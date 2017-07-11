package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Vector;

class TopLevelElement
  extends SyntaxTreeNode
{
  protected Vector _dependencies = null;
  
  TopLevelElement() {}
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return typeCheckContents(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ErrorMsg localErrorMsg = new ErrorMsg("NOT_IMPLEMENTED_ERR", getClass(), this);
    getParser().reportError(2, localErrorMsg);
  }
  
  public InstructionList compile(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList2 = paramMethodGenerator.getInstructionList();
    InstructionList localInstructionList1;
    paramMethodGenerator.setInstructionList(localInstructionList1 = new InstructionList());
    translate(paramClassGenerator, paramMethodGenerator);
    paramMethodGenerator.setInstructionList(localInstructionList2);
    return localInstructionList1;
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("TopLevelElement");
    displayContents(paramInt + 4);
  }
  
  public void addDependency(TopLevelElement paramTopLevelElement)
  {
    if (this._dependencies == null) {
      this._dependencies = new Vector();
    }
    if (!this._dependencies.contains(paramTopLevelElement)) {
      this._dependencies.addElement(paramTopLevelElement);
    }
  }
  
  public Vector getDependencies()
  {
    return this._dependencies;
  }
}
