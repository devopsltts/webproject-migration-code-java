package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class NamespaceAlias
  extends TopLevelElement
{
  private String sPrefix;
  private String rPrefix;
  
  NamespaceAlias() {}
  
  public void parseContents(Parser paramParser)
  {
    this.sPrefix = getAttribute("stylesheet-prefix");
    this.rPrefix = getAttribute("result-prefix");
    paramParser.getSymbolTable().addPrefixAlias(this.sPrefix, this.rPrefix);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
}
