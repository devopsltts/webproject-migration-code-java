package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class BooleanCall
  extends FunctionCall
{
  private Expression _arg = null;
  
  public BooleanCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    this._arg.typeCheck(paramSymbolTable);
    return this._type = Type.Boolean;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    this._arg.translate(paramClassGenerator, paramMethodGenerator);
    Type localType = this._arg.getType();
    if (!localType.identicalTo(Type.Boolean))
    {
      this._arg.startIterator(paramClassGenerator, paramMethodGenerator);
      localType.translateTo(paramClassGenerator, paramMethodGenerator, Type.Boolean);
    }
  }
}
