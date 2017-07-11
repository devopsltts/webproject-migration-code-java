package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class CastCall
  extends FunctionCall
{
  private String _className;
  private Expression _right;
  
  public CastCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (argumentCount() != 2) {
      throw new TypeCheckError(new ErrorMsg("ILLEGAL_ARG_ERR", getName(), this));
    }
    Expression localExpression = argument(0);
    if ((localExpression instanceof LiteralExpr))
    {
      this._className = ((LiteralExpr)localExpression).getValue();
      this._type = Type.newObjectType(this._className);
    }
    else
    {
      throw new TypeCheckError(new ErrorMsg("NEED_LITERAL_ERR", getName(), this));
    }
    this._right = argument(1);
    Type localType = this._right.typeCheck(paramSymbolTable);
    if ((localType != Type.Reference) && (!(localType instanceof ObjectType))) {
      throw new TypeCheckError(new ErrorMsg("DATA_CONVERSION_ERR", localType, this._type, this));
    }
    return this._type;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    this._right.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass(this._className)));
  }
}
