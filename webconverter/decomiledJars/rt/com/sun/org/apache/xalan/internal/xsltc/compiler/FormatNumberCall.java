package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class FormatNumberCall
  extends FunctionCall
{
  private Expression _value = argument(0);
  private Expression _format = argument(1);
  private Expression _name = argumentCount() == 3 ? argument(2) : null;
  private QName _resolvedQName = null;
  
  public FormatNumberCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    getStylesheet().numberFormattingUsed();
    Type localType1 = this._value.typeCheck(paramSymbolTable);
    if (!(localType1 instanceof RealType)) {
      this._value = new CastExpr(this._value, Type.Real);
    }
    Type localType2 = this._format.typeCheck(paramSymbolTable);
    if (!(localType2 instanceof StringType)) {
      this._format = new CastExpr(this._format, Type.String);
    }
    if (argumentCount() == 3)
    {
      Type localType3 = this._name.typeCheck(paramSymbolTable);
      if ((this._name instanceof LiteralExpr))
      {
        LiteralExpr localLiteralExpr = (LiteralExpr)this._name;
        this._resolvedQName = getParser().getQNameIgnoreDefaultNs(localLiteralExpr.getValue());
      }
      else if (!(localType3 instanceof StringType))
      {
        this._name = new CastExpr(this._name, Type.String);
      }
    }
    return this._type = Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    this._value.translate(paramClassGenerator, paramMethodGenerator);
    this._format.translate(paramClassGenerator, paramMethodGenerator);
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "formatNumber", "(DLjava/lang/String;Ljava/text/DecimalFormat;)Ljava/lang/String;");
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getDecimalFormat", "(Ljava/lang/String;)Ljava/text/DecimalFormat;");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    if (this._name == null) {
      localInstructionList.append(new PUSH(localConstantPoolGen, ""));
    } else if (this._resolvedQName != null) {
      localInstructionList.append(new PUSH(localConstantPoolGen, this._resolvedQName.toString()));
    } else {
      this._name.translate(paramClassGenerator, paramMethodGenerator);
    }
    localInstructionList.append(new INVOKEVIRTUAL(j));
    localInstructionList.append(new INVOKESTATIC(i));
  }
}
