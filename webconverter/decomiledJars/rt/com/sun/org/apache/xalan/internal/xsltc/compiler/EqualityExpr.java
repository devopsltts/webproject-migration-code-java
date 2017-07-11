package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;

final class EqualityExpr
  extends Expression
{
  private final int _op;
  private Expression _left;
  private Expression _right;
  
  public EqualityExpr(int paramInt, Expression paramExpression1, Expression paramExpression2)
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
  
  public String toString()
  {
    return Operators.getOpNames(this._op) + '(' + this._left + ", " + this._right + ')';
  }
  
  public Expression getLeft()
  {
    return this._left;
  }
  
  public Expression getRight()
  {
    return this._right;
  }
  
  public boolean getOp()
  {
    return this._op != 1;
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
    if (this._left.hasLastCall()) {
      return true;
    }
    return this._right.hasLastCall();
  }
  
  private void swapArguments()
  {
    Expression localExpression = this._left;
    this._left = this._right;
    this._right = localExpression;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = this._left.typeCheck(paramSymbolTable);
    Type localType2 = this._right.typeCheck(paramSymbolTable);
    if ((localType1.isSimple()) && (localType2.isSimple()))
    {
      if (localType1 != localType2) {
        if ((localType1 instanceof BooleanType))
        {
          this._right = new CastExpr(this._right, Type.Boolean);
        }
        else if ((localType2 instanceof BooleanType))
        {
          this._left = new CastExpr(this._left, Type.Boolean);
        }
        else if (((localType1 instanceof NumberType)) || ((localType2 instanceof NumberType)))
        {
          this._left = new CastExpr(this._left, Type.Real);
          this._right = new CastExpr(this._right, Type.Real);
        }
        else
        {
          this._left = new CastExpr(this._left, Type.String);
          this._right = new CastExpr(this._right, Type.String);
        }
      }
    }
    else if ((localType1 instanceof ReferenceType))
    {
      this._right = new CastExpr(this._right, Type.Reference);
    }
    else if ((localType2 instanceof ReferenceType))
    {
      this._left = new CastExpr(this._left, Type.Reference);
    }
    else if (((localType1 instanceof NodeType)) && (localType2 == Type.String))
    {
      this._left = new CastExpr(this._left, Type.String);
    }
    else if ((localType1 == Type.String) && ((localType2 instanceof NodeType)))
    {
      this._right = new CastExpr(this._right, Type.String);
    }
    else if (((localType1 instanceof NodeType)) && ((localType2 instanceof NodeType)))
    {
      this._left = new CastExpr(this._left, Type.String);
      this._right = new CastExpr(this._right, Type.String);
    }
    else if ((!(localType1 instanceof NodeType)) || (!(localType2 instanceof NodeSetType)))
    {
      if (((localType1 instanceof NodeSetType)) && ((localType2 instanceof NodeType)))
      {
        swapArguments();
      }
      else
      {
        if ((localType1 instanceof NodeType)) {
          this._left = new CastExpr(this._left, Type.NodeSet);
        }
        if ((localType2 instanceof NodeType)) {
          this._right = new CastExpr(this._right, Type.NodeSet);
        }
        if ((localType1.isSimple()) || (((localType1 instanceof ResultTreeType)) && ((localType2 instanceof NodeSetType)))) {
          swapArguments();
        }
        if ((this._right.getType() instanceof IntType)) {
          this._right = new CastExpr(this._right, Type.Real);
        }
      }
    }
    return this._type = Type.Boolean;
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Type localType = this._left.getType();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((localType instanceof BooleanType))
    {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      this._falseList.add(localInstructionList.append(this._op == 0 ? new IF_ICMPNE(null) : new IF_ICMPEQ(null)));
    }
    else if ((localType instanceof NumberType))
    {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      if ((localType instanceof RealType))
      {
        localInstructionList.append(DCMPG);
        this._falseList.add(localInstructionList.append(this._op == 0 ? new IFNE(null) : new IFEQ(null)));
      }
      else
      {
        this._falseList.add(localInstructionList.append(this._op == 0 ? new IF_ICMPNE(null) : new IF_ICMPEQ(null)));
      }
    }
    else
    {
      translate(paramClassGenerator, paramMethodGenerator);
      desynthesize(paramClassGenerator, paramMethodGenerator);
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    Type localType1 = this._left.getType();
    Type localType2 = this._right.getType();
    if (((localType1 instanceof BooleanType)) || ((localType1 instanceof NumberType)))
    {
      translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      synthesize(paramClassGenerator, paramMethodGenerator);
      return;
    }
    if ((localType1 instanceof StringType))
    {
      int i = localConstantPoolGen.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new INVOKEVIRTUAL(i));
      if (this._op == 1)
      {
        localInstructionList.append(ICONST_1);
        localInstructionList.append(IXOR);
      }
      return;
    }
    if ((localType1 instanceof ResultTreeType))
    {
      if ((localType2 instanceof BooleanType))
      {
        this._right.translate(paramClassGenerator, paramMethodGenerator);
        if (this._op == 1)
        {
          localInstructionList.append(ICONST_1);
          localInstructionList.append(IXOR);
        }
        return;
      }
      if ((localType2 instanceof RealType))
      {
        this._left.translate(paramClassGenerator, paramMethodGenerator);
        localType1.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
        this._right.translate(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(DCMPG);
        BranchHandle localBranchHandle2 = localInstructionList.append(this._op == 0 ? new IFNE(null) : new IFEQ(null));
        localInstructionList.append(ICONST_1);
        BranchHandle localBranchHandle1 = localInstructionList.append(new GOTO(null));
        localBranchHandle2.setTarget(localInstructionList.append(ICONST_0));
        localBranchHandle1.setTarget(localInstructionList.append(NOP));
        return;
      }
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      localType1.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      if ((localType2 instanceof ResultTreeType)) {
        localType2.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      }
      j = localConstantPoolGen.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
      localInstructionList.append(new INVOKEVIRTUAL(j));
      if (this._op == 1)
      {
        localInstructionList.append(ICONST_1);
        localInstructionList.append(IXOR);
      }
      return;
    }
    if (((localType1 instanceof NodeSetType)) && ((localType2 instanceof BooleanType)))
    {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._left.startIterator(paramClassGenerator, paramMethodGenerator);
      Type.NodeSet.translateTo(paramClassGenerator, paramMethodGenerator, Type.Boolean);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(IXOR);
      if (this._op == 0)
      {
        localInstructionList.append(ICONST_1);
        localInstructionList.append(IXOR);
      }
      return;
    }
    if (((localType1 instanceof NodeSetType)) && ((localType2 instanceof StringType)))
    {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      this._left.startIterator(paramClassGenerator, paramMethodGenerator);
      this._right.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new PUSH(localConstantPoolGen, this._op));
      localInstructionList.append(paramMethodGenerator.loadDOM());
      j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + localType1.toSignature() + localType2.toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
      localInstructionList.append(new INVOKESTATIC(j));
      return;
    }
    this._left.translate(paramClassGenerator, paramMethodGenerator);
    this._left.startIterator(paramClassGenerator, paramMethodGenerator);
    this._right.translate(paramClassGenerator, paramMethodGenerator);
    this._right.startIterator(paramClassGenerator, paramMethodGenerator);
    if ((localType2 instanceof ResultTreeType))
    {
      localType2.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      localType2 = Type.String;
    }
    localInstructionList.append(new PUSH(localConstantPoolGen, this._op));
    localInstructionList.append(paramMethodGenerator.loadDOM());
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + localType1.toSignature() + localType2.toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
    localInstructionList.append(new INVOKESTATIC(j));
  }
}
