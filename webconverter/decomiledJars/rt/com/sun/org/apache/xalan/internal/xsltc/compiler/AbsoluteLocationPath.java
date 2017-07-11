package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class AbsoluteLocationPath
  extends Expression
{
  private Expression _path;
  
  public AbsoluteLocationPath()
  {
    this._path = null;
  }
  
  public AbsoluteLocationPath(Expression paramExpression)
  {
    this._path = paramExpression;
    if (paramExpression != null) {
      this._path.setParent(this);
    }
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    if (this._path != null) {
      this._path.setParser(paramParser);
    }
  }
  
  public Expression getPath()
  {
    return this._path;
  }
  
  public String toString()
  {
    return "AbsoluteLocationPath(" + (this._path != null ? this._path.toString() : "null") + ')';
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (this._path != null)
    {
      Type localType = this._path.typeCheck(paramSymbolTable);
      if ((localType instanceof NodeType)) {
        this._path = new CastExpr(this._path, Type.NodeSet);
      }
    }
    return this._type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i;
    if (this._path != null)
    {
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
      this._path.translate(paramClassGenerator, paramMethodGenerator);
      LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable("abs_location_path_tmp", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
      localLocalVariableGen.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
      localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator")));
      localInstructionList.append(DUP);
      localLocalVariableGen.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen.getIndex())));
      localInstructionList.append(new INVOKESPECIAL(i));
    }
    else
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(new INVOKEINTERFACE(i, 1));
    }
  }
}
