package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class CastExpr
  extends Expression
{
  private final Expression _left;
  private static final MultiHashtable<Type, Type> InternalTypeMap = new MultiHashtable();
  private boolean _typeTest = false;
  
  public CastExpr(Expression paramExpression, Type paramType)
    throws TypeCheckError
  {
    this._left = paramExpression;
    this._type = paramType;
    if (((this._left instanceof Step)) && (this._type == Type.Boolean))
    {
      Step localStep = (Step)this._left;
      if ((localStep.getAxis() == 13) && (localStep.getNodeType() != -1)) {
        this._typeTest = true;
      }
    }
    setParser(paramExpression.getParser());
    setParent(paramExpression.getParent());
    paramExpression.setParent(this);
    typeCheck(paramExpression.getParser().getSymbolTable());
  }
  
  public Expression getExpr()
  {
    return this._left;
  }
  
  public boolean hasPositionCall()
  {
    return this._left.hasPositionCall();
  }
  
  public boolean hasLastCall()
  {
    return this._left.hasLastCall();
  }
  
  public String toString()
  {
    return "cast(" + this._left + ", " + this._type + ")";
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType = this._left.getType();
    if (localType == null) {
      localType = this._left.typeCheck(paramSymbolTable);
    }
    if ((localType instanceof NodeType)) {
      localType = Type.Node;
    } else if ((localType instanceof ResultTreeType)) {
      localType = Type.ResultTree;
    }
    if (InternalTypeMap.maps(localType, this._type) != null) {
      return this._type;
    }
    throw new TypeCheckError(new ErrorMsg("DATA_CONVERSION_ERR", localType.toString(), this._type.toString()));
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Type localType = this._left.getType();
    if (this._typeTest)
    {
      ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
      localInstructionList.append(new SIPUSH((short)((Step)this._left).getNodeType()));
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      this._falseList.add(localInstructionList.append(new IF_ICMPNE(null)));
    }
    else
    {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      if (this._type != localType)
      {
        this._left.startIterator(paramClassGenerator, paramMethodGenerator);
        if ((this._type instanceof BooleanType))
        {
          FlowList localFlowList = localType.translateToDesynthesized(paramClassGenerator, paramMethodGenerator, this._type);
          if (localFlowList != null) {
            this._falseList.append(localFlowList);
          }
        }
        else
        {
          localType.translateTo(paramClassGenerator, paramMethodGenerator, this._type);
        }
      }
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Type localType = this._left.getType();
    this._left.translate(paramClassGenerator, paramMethodGenerator);
    if (!this._type.identicalTo(localType))
    {
      this._left.startIterator(paramClassGenerator, paramMethodGenerator);
      localType.translateTo(paramClassGenerator, paramMethodGenerator, this._type);
    }
  }
  
  static
  {
    InternalTypeMap.put(Type.Boolean, Type.Boolean);
    InternalTypeMap.put(Type.Boolean, Type.Real);
    InternalTypeMap.put(Type.Boolean, Type.String);
    InternalTypeMap.put(Type.Boolean, Type.Reference);
    InternalTypeMap.put(Type.Boolean, Type.Object);
    InternalTypeMap.put(Type.Real, Type.Real);
    InternalTypeMap.put(Type.Real, Type.Int);
    InternalTypeMap.put(Type.Real, Type.Boolean);
    InternalTypeMap.put(Type.Real, Type.String);
    InternalTypeMap.put(Type.Real, Type.Reference);
    InternalTypeMap.put(Type.Real, Type.Object);
    InternalTypeMap.put(Type.Int, Type.Int);
    InternalTypeMap.put(Type.Int, Type.Real);
    InternalTypeMap.put(Type.Int, Type.Boolean);
    InternalTypeMap.put(Type.Int, Type.String);
    InternalTypeMap.put(Type.Int, Type.Reference);
    InternalTypeMap.put(Type.Int, Type.Object);
    InternalTypeMap.put(Type.String, Type.String);
    InternalTypeMap.put(Type.String, Type.Boolean);
    InternalTypeMap.put(Type.String, Type.Real);
    InternalTypeMap.put(Type.String, Type.Reference);
    InternalTypeMap.put(Type.String, Type.Object);
    InternalTypeMap.put(Type.NodeSet, Type.NodeSet);
    InternalTypeMap.put(Type.NodeSet, Type.Boolean);
    InternalTypeMap.put(Type.NodeSet, Type.Real);
    InternalTypeMap.put(Type.NodeSet, Type.String);
    InternalTypeMap.put(Type.NodeSet, Type.Node);
    InternalTypeMap.put(Type.NodeSet, Type.Reference);
    InternalTypeMap.put(Type.NodeSet, Type.Object);
    InternalTypeMap.put(Type.Node, Type.Node);
    InternalTypeMap.put(Type.Node, Type.Boolean);
    InternalTypeMap.put(Type.Node, Type.Real);
    InternalTypeMap.put(Type.Node, Type.String);
    InternalTypeMap.put(Type.Node, Type.NodeSet);
    InternalTypeMap.put(Type.Node, Type.Reference);
    InternalTypeMap.put(Type.Node, Type.Object);
    InternalTypeMap.put(Type.ResultTree, Type.ResultTree);
    InternalTypeMap.put(Type.ResultTree, Type.Boolean);
    InternalTypeMap.put(Type.ResultTree, Type.Real);
    InternalTypeMap.put(Type.ResultTree, Type.String);
    InternalTypeMap.put(Type.ResultTree, Type.NodeSet);
    InternalTypeMap.put(Type.ResultTree, Type.Reference);
    InternalTypeMap.put(Type.ResultTree, Type.Object);
    InternalTypeMap.put(Type.Reference, Type.Reference);
    InternalTypeMap.put(Type.Reference, Type.Boolean);
    InternalTypeMap.put(Type.Reference, Type.Int);
    InternalTypeMap.put(Type.Reference, Type.Real);
    InternalTypeMap.put(Type.Reference, Type.String);
    InternalTypeMap.put(Type.Reference, Type.Node);
    InternalTypeMap.put(Type.Reference, Type.NodeSet);
    InternalTypeMap.put(Type.Reference, Type.ResultTree);
    InternalTypeMap.put(Type.Reference, Type.Object);
    InternalTypeMap.put(Type.Object, Type.String);
    InternalTypeMap.put(Type.Void, Type.String);
    InternalTypeMap.makeUnmodifiable();
  }
}
