package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class TransletOutput
  extends Instruction
{
  private Expression _filename;
  private boolean _append;
  
  TransletOutput() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("TransletOutput: " + this._filename);
  }
  
  public void parseContents(Parser paramParser)
  {
    String str1 = getAttribute("file");
    String str2 = getAttribute("append");
    if ((str1 == null) || (str1.equals(""))) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "file");
    }
    this._filename = AttributeValue.create(this, str1, paramParser);
    if ((str2 != null) && ((str2.toLowerCase().equals("yes")) || (str2.toLowerCase().equals("true")))) {
      this._append = true;
    } else {
      this._append = false;
    }
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType = this._filename.typeCheck(paramSymbolTable);
    if (!(localType instanceof StringType)) {
      this._filename = new CastExpr(this._filename, Type.String);
    }
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    boolean bool = paramClassGenerator.getParser().getXSLTC().isSecureProcessing();
    if (bool)
    {
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unallowed_extension_elementF", "(Ljava/lang/String;)V");
      localInstructionList.append(new PUSH(localConstantPoolGen, "redirect"));
      localInstructionList.append(new INVOKESTATIC(i));
      return;
    }
    localInstructionList.append(paramMethodGenerator.loadHandler());
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "openOutputHandler", "(Ljava/lang/String;Z)Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "closeOutputHandler", "(Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    this._filename.translate(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new PUSH(localConstantPoolGen, this._append));
    localInstructionList.append(new INVOKEVIRTUAL(i));
    localInstructionList.append(paramMethodGenerator.storeHandler());
    translateContents(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new INVOKEVIRTUAL(j));
    localInstructionList.append(paramMethodGenerator.storeHandler());
  }
}
