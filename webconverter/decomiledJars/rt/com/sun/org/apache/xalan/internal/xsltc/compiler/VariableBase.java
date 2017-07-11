package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.io.PrintStream;
import java.util.Vector;

class VariableBase
  extends TopLevelElement
{
  protected QName _name;
  protected String _escapedName;
  protected com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type _type;
  protected boolean _isLocal;
  protected LocalVariableGen _local;
  protected Instruction _loadInstruction;
  protected Instruction _storeInstruction;
  protected Expression _select;
  protected String select;
  protected Vector _refs = new Vector(2);
  protected Vector _dependencies = null;
  protected boolean _ignore = false;
  
  VariableBase() {}
  
  public void disable()
  {
    this._ignore = true;
  }
  
  public void addReference(VariableRefBase paramVariableRefBase)
  {
    this._refs.addElement(paramVariableRefBase);
  }
  
  public void copyReferences(VariableBase paramVariableBase)
  {
    int i = this._refs.size();
    for (int j = 0; j < i; j++) {
      paramVariableBase.addReference((VariableRefBase)this._refs.get(j));
    }
  }
  
  public void mapRegister(MethodGenerator paramMethodGenerator)
  {
    if (this._local == null)
    {
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      String str = getEscapedName();
      com.sun.org.apache.bcel.internal.generic.Type localType = this._type.toJCType();
      this._local = paramMethodGenerator.addLocalVariable2(str, localType, localInstructionList.getEnd());
    }
  }
  
  public void unmapRegister(MethodGenerator paramMethodGenerator)
  {
    if (this._local != null)
    {
      this._local.setEnd(paramMethodGenerator.getInstructionList().getEnd());
      paramMethodGenerator.removeLocalVariable(this._local);
      this._refs = null;
      this._local = null;
    }
  }
  
  public Instruction loadInstruction()
  {
    Instruction localInstruction = this._loadInstruction;
    if (this._loadInstruction == null) {
      this._loadInstruction = this._type.LOAD(this._local.getIndex());
    }
    return this._loadInstruction;
  }
  
  public Instruction storeInstruction()
  {
    Instruction localInstruction = this._storeInstruction;
    if (this._storeInstruction == null) {
      this._storeInstruction = this._type.STORE(this._local.getIndex());
    }
    return this._storeInstruction;
  }
  
  public Expression getExpression()
  {
    return this._select;
  }
  
  public String toString()
  {
    return "variable(" + this._name + ")";
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    System.out.println("Variable " + this._name);
    if (this._select != null)
    {
      indent(paramInt + 4);
      System.out.println("select " + this._select.toString());
    }
    displayContents(paramInt + 4);
  }
  
  public com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type getType()
  {
    return this._type;
  }
  
  public QName getName()
  {
    return this._name;
  }
  
  public String getEscapedName()
  {
    return this._escapedName;
  }
  
  public void setName(QName paramQName)
  {
    this._name = paramQName;
    this._escapedName = Util.escape(paramQName.getStringRep());
  }
  
  public boolean isLocal()
  {
    return this._isLocal;
  }
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("name");
    if (str.length() > 0)
    {
      if (!XML11Char.isXML11ValidQName(str))
      {
        localObject = new ErrorMsg("INVALID_QNAME_ERR", str, this);
        paramParser.reportError(3, (ErrorMsg)localObject);
      }
      setName(paramParser.getQNameIgnoreDefaultNs(str));
    }
    else
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
    }
    Object localObject = paramParser.lookupVariable(this._name);
    if ((localObject != null) && (((VariableBase)localObject).getParent() == getParent())) {
      reportError(this, paramParser, "VARIABLE_REDEF_ERR", str);
    }
    this.select = getAttribute("select");
    if (this.select.length() > 0)
    {
      this._select = getParser().parseExpression(this, "select", null);
      if (this._select.isDummy())
      {
        reportError(this, paramParser, "REQUIRED_ATTR_ERR", "select");
        return;
      }
    }
    parseChildren(paramParser);
  }
  
  public void translateValue(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen;
    InstructionList localInstructionList;
    if (this._select != null)
    {
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      if ((this._select.getType() instanceof NodeSetType))
      {
        localConstantPoolGen = paramClassGenerator.getConstantPool();
        localInstructionList = paramMethodGenerator.getInstructionList();
        int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CachedNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
        localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CachedNodeListIterator")));
        localInstructionList.append(DUP_X1);
        localInstructionList.append(SWAP);
        localInstructionList.append(new INVOKESPECIAL(i));
      }
      this._select.startIterator(paramClassGenerator, paramMethodGenerator);
    }
    else if (hasContents())
    {
      compileResultTree(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      localConstantPoolGen = paramClassGenerator.getConstantPool();
      localInstructionList = paramMethodGenerator.getInstructionList();
      localInstructionList.append(new PUSH(localConstantPoolGen, ""));
    }
  }
}
