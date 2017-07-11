package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class BinOpExpr
  extends Expression
{
  public static final int PLUS = 0;
  public static final int MINUS = 1;
  public static final int TIMES = 2;
  public static final int DIV = 3;
  public static final int MOD = 4;
  private static final String[] Ops = { "+", "-", "*", "/", "%" };
  private int _op;
  private Expression _left;
  private Expression _right;
  
  public BinOpExpr(int paramInt, Expression paramExpression1, Expression paramExpression2)
  {
    this._op = paramInt;
    (this._left = paramExpression1).setParent(this);
    (this._right = paramExpression2).setParent(this);
  }
  
  public boolean hasPositionCall()
  {
    if (this._left.hasPositionCall()) {
      return true;
    }
    return this._right.hasPositionCall();
  }
  
  public boolean hasLastCall()
  {
    return (this._left.hasLastCall()) || (this._right.hasLastCall());
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
    this._right.setParser(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = this._left.typeCheck(paramSymbolTable);
    Type localType2 = this._right.typeCheck(paramSymbolTable);
    MethodType localMethodType = lookupPrimop(paramSymbolTable, Ops[this._op], new MethodType(Type.Void, localType1, localType2));
    if (localMethodType != null)
    {
      Type localType3 = (Type)localMethodType.argsType().elementAt(0);
      if (!localType3.identicalTo(localType1)) {
        this._left = new CastExpr(this._left, localType3);
      }
      Type localType4 = (Type)localMethodType.argsType().elementAt(1);
      if (!localType4.identicalTo(localType2)) {
        this._right = new CastExpr(this._right, localType3);
      }
      return this._type = localMethodType.resultType();
    }
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    this._left.translate(paramClassGenerator, paramMethodGenerator);
    this._right.translate(paramClassGenerator, paramMethodGenerator);
    switch (this._op)
    {
    case 0: 
      localInstructionList.append(this._type.ADD());
      break;
    case 1: 
      localInstructionList.append(this._type.SUB());
      break;
    case 2: 
      localInstructionList.append(this._type.MUL());
      break;
    case 3: 
      localInstructionList.append(this._type.DIV());
      break;
    case 4: 
      localInstructionList.append(this._type.REM());
      break;
    default: 
      ErrorMsg localErrorMsg = new ErrorMsg("ILLEGAL_BINARY_OP_ERR", this);
      getParser().reportError(3, localErrorMsg);
    }
  }
  
  public String toString()
  {
    return Ops[this._op] + '(' + this._left + ", " + this._right + ')';
  }
}
