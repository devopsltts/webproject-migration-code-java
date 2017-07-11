package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Message
  extends Instruction
{
  private boolean _terminate = false;
  
  Message() {}
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("terminate");
    if (str != null) {
      this._terminate = str.equals("yes");
    }
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramClassGenerator.loadTranslet());
    switch (elementCount())
    {
    case 0: 
      localInstructionList.append(new PUSH(localConstantPoolGen, ""));
      break;
    case 1: 
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)elementAt(0);
      if ((localSyntaxTreeNode instanceof Text)) {
        localInstructionList.append(new PUSH(localConstantPoolGen, ((Text)localSyntaxTreeNode).getText()));
      }
      break;
    }
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xml.internal.serializer.ToXMLStream")));
    localInstructionList.append(paramMethodGenerator.storeHandler());
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("java.io.StringWriter")));
    localInstructionList.append(DUP);
    localInstructionList.append(DUP);
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("java.io.StringWriter", "<init>", "()V")));
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xml.internal.serializer.ToXMLStream", "<init>", "()V")));
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(SWAP);
    localInstructionList.append(new INVOKEINTERFACE(localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setWriter", "(Ljava/io/Writer;)V"), 2));
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new PUSH(localConstantPoolGen, "UTF-8"));
    localInstructionList.append(new INVOKEINTERFACE(localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setEncoding", "(Ljava/lang/String;)V"), 2));
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(ICONST_1);
    localInstructionList.append(new INVOKEINTERFACE(localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setOmitXMLDeclaration", "(Z)V"), 2));
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new INVOKEINTERFACE(localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V"), 1));
    translateContents(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new INVOKEINTERFACE(localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V"), 1));
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("java.io.StringWriter", "toString", "()Ljava/lang/String;")));
    localInstructionList.append(SWAP);
    localInstructionList.append(paramMethodGenerator.storeHandler());
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "displayMessage", "(Ljava/lang/String;)V")));
    if (this._terminate == true)
    {
      int i = localConstantPoolGen.addMethodref("java.lang.RuntimeException", "<init>", "(Ljava/lang/String;)V");
      localInstructionList.append(new NEW(localConstantPoolGen.addClass("java.lang.RuntimeException")));
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, "Termination forced by an xsl:message instruction"));
      localInstructionList.append(new INVOKESPECIAL(i));
      localInstructionList.append(ATHROW);
    }
  }
}
