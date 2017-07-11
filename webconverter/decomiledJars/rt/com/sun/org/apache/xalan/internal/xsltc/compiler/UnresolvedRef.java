package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class UnresolvedRef
  extends VariableRefBase
{
  private QName _variableName = null;
  private VariableRefBase _ref = null;
  
  public UnresolvedRef(QName paramQName)
  {
    this._variableName = paramQName;
  }
  
  public QName getName()
  {
    return this._variableName;
  }
  
  private ErrorMsg reportError()
  {
    ErrorMsg localErrorMsg = new ErrorMsg("VARIABLE_UNDEF_ERR", this._variableName, this);
    getParser().reportError(3, localErrorMsg);
    return localErrorMsg;
  }
  
  private VariableRefBase resolve(Parser paramParser, SymbolTable paramSymbolTable)
  {
    VariableBase localVariableBase = paramParser.lookupVariable(this._variableName);
    if (localVariableBase == null) {
      localVariableBase = (VariableBase)paramSymbolTable.lookupName(this._variableName);
    }
    if (localVariableBase == null)
    {
      reportError();
      return null;
    }
    this._variable = localVariableBase;
    addParentDependency();
    if ((localVariableBase instanceof Variable)) {
      return new VariableRef((Variable)localVariableBase);
    }
    if ((localVariableBase instanceof Param)) {
      return new ParameterRef((Param)localVariableBase);
    }
    return null;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (this._ref != null)
    {
      String str = this._variableName.toString();
      ErrorMsg localErrorMsg = new ErrorMsg("CIRCULAR_VARIABLE_ERR", str, this);
    }
    if ((this._ref = resolve(getParser(), paramSymbolTable)) != null) {
      return this._type = this._ref.typeCheck(paramSymbolTable);
    }
    throw new TypeCheckError(reportError());
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (this._ref != null) {
      this._ref.translate(paramClassGenerator, paramMethodGenerator);
    } else {
      reportError();
    }
  }
  
  public String toString()
  {
    return "unresolved-ref()";
  }
}
