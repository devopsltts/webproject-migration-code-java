package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class StartsWithCall
  extends FunctionCall
{
  private Expression _base = null;
  private Expression _token = null;
  
  public StartsWithCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (argumentCount() != 2)
    {
      localObject = new ErrorMsg("ILLEGAL_ARG_ERR", getName(), this);
      throw new TypeCheckError((ErrorMsg)localObject);
    }
    this._base = argument(0);
    Object localObject = this._base.typeCheck(paramSymbolTable);
    if (localObject != Type.String) {
      this._base = new CastExpr(this._base, Type.String);
    }
    this._token = argument(1);
    Type localType = this._token.typeCheck(paramSymbolTable);
    if (localType != Type.String) {
      this._token = new CastExpr(this._token, Type.String);
    }
    return this._type = Type.Boolean;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    this._base.translate(paramClassGenerator, paramMethodGenerator);
    this._token.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("java.lang.String", "startsWith", "(Ljava/lang/String;)Z")));
  }
}
