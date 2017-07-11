package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;
import java.util.Vector;

final class RelationalExpr
  extends Expression
{
  private int _op;
  private Expression _left;
  private Expression _right;
  
  public RelationalExpr(int paramInt, Expression paramExpression1, Expression paramExpression2)
  {
    this._op = paramInt;
    (this._left = paramExpression1).setParent(this);
    (this._right = paramExpression2).setParent(this);
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    this._left.setParser(paramParser);
    this._right.setParser(paramParser);
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
  
  public boolean hasReferenceArgs()
  {
    return ((this._left.getType() instanceof ReferenceType)) || ((this._right.getType() instanceof ReferenceType));
  }
  
  public boolean hasNodeArgs()
  {
    return ((this._left.getType() instanceof NodeType)) || ((this._right.getType() instanceof NodeType));
  }
  
  public boolean hasNodeSetArgs()
  {
    return ((this._left.getType() instanceof NodeSetType)) || ((this._right.getType() instanceof NodeSetType));
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = this._left.typeCheck(paramSymbolTable);
    Type localType2 = this._right.typeCheck(paramSymbolTable);
    if (((localType1 instanceof ResultTreeType)) && ((localType2 instanceof ResultTreeType)))
    {
      this._right = new CastExpr(this._right, Type.Real);
      this._left = new CastExpr(this._left, Type.Real);
      return this._type = Type.Boolean;
    }
    Type localType3;
    Type localType4;
    if (hasReferenceArgs())
    {
      localObject = null;
      localType3 = null;
      localType4 = null;
      VariableRefBase localVariableRefBase;
      VariableBase localVariableBase;
      if (((localType1 instanceof ReferenceType)) && ((this._left instanceof VariableRefBase)))
      {
        localVariableRefBase = (VariableRefBase)this._left;
        localVariableBase = localVariableRefBase.getVariable();
        localType3 = localVariableBase.getType();
      }
      if (((localType2 instanceof ReferenceType)) && ((this._right instanceof VariableRefBase)))
      {
        localVariableRefBase = (VariableRefBase)this._right;
        localVariableBase = localVariableRefBase.getVariable();
        localType4 = localVariableBase.getType();
      }
      if (localType3 == null) {
        localObject = localType4;
      } else if (localType4 == null) {
        localObject = localType3;
      } else {
        localObject = Type.Real;
      }
      if (localObject == null) {
        localObject = Type.Real;
      }
      this._right = new CastExpr(this._right, (Type)localObject);
      this._left = new CastExpr(this._left, (Type)localObject);
      return this._type = Type.Boolean;
    }
    if (hasNodeSetArgs())
    {
      if ((localType2 instanceof NodeSetType))
      {
        localObject = this._right;
        this._right = this._left;
        this._left = ((Expression)localObject);
        this._op = (this._op == 4 ? 5 : this._op == 3 ? 2 : this._op == 2 ? 3 : 4);
        localType2 = this._right.getType();
      }
      if ((localType2 instanceof NodeType)) {
        this._right = new CastExpr(this._right, Type.NodeSet);
      }
      if ((localType2 instanceof IntType)) {
        this._right = new CastExpr(this._right, Type.Real);
      }
      if ((localType2 instanceof ResultTreeType)) {
        this._right = new CastExpr(this._right, Type.String);
      }
      return this._type = Type.Boolean;
    }
    if (hasNodeArgs())
    {
      if ((localType1 instanceof BooleanType))
      {
        this._right = new CastExpr(this._right, Type.Boolean);
        localType2 = Type.Boolean;
      }
      if ((localType2 instanceof BooleanType))
      {
        this._left = new CastExpr(this._left, Type.Boolean);
        localType1 = Type.Boolean;
      }
    }
    Object localObject = lookupPrimop(paramSymbolTable, Operators.getOpNames(this._op), new MethodType(Type.Void, localType1, localType2));
    if (localObject != null)
    {
      localType3 = (Type)((MethodType)localObject).argsType().elementAt(0);
      if (!localType3.identicalTo(localType1)) {
        this._left = new CastExpr(this._left, localType3);
      }
      localType4 = (Type)((MethodType)localObject).argsType().elementAt(1);
      if (!localType4.identicalTo(localType2)) {
        this._right = new CastExpr(this._right, localType3);
      }
      return this._type = ((MethodType)localObject).resultType();
    }
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if ((hasNodeSetArgs()) || (hasReferenceArgs()))
    {
      ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._left.startIterator(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      this._right.startIterator(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new PUSH(localConstantPoolGen, this._op));
      localInstructionList.append(paramMethodGenerator.loadDOM());
      int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + this._left.getType().toSignature() + this._right.getType().toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
      localInstructionList.append(new INVOKESTATIC(i));
    }
    else
    {
      translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      synthesize(paramClassGenerator, paramMethodGenerator);
    }
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if ((hasNodeSetArgs()) || (hasReferenceArgs()))
    {
      translate(paramClassGenerator, paramMethodGenerator);
      desynthesize(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      BranchInstruction localBranchInstruction = null;
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      boolean bool = false;
      Type localType = this._left.getType();
      if ((localType instanceof RealType))
      {
        localInstructionList.append(localType.CMP((this._op == 3) || (this._op == 5)));
        localType = Type.Int;
        bool = true;
      }
      switch (this._op)
      {
      case 3: 
        localBranchInstruction = localType.GE(bool);
        break;
      case 2: 
        localBranchInstruction = localType.LE(bool);
        break;
      case 5: 
        localBranchInstruction = localType.GT(bool);
        break;
      case 4: 
        localBranchInstruction = localType.LT(bool);
        break;
      default: 
        ErrorMsg localErrorMsg = new ErrorMsg("ILLEGAL_RELAT_OP_ERR", this);
        getParser().reportError(2, localErrorMsg);
      }
      this._falseList.add(localInstructionList.append(localBranchInstruction));
    }
  }
  
  public String toString()
  {
    return Operators.getOpNames(this._op) + '(' + this._left + ", " + this._right + ')';
  }
}
