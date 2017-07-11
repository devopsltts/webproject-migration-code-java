package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;

final class Predicate
  extends Expression
  implements Closure
{
  private Expression _exp = null;
  private boolean _canOptimize = true;
  private boolean _nthPositionFilter = false;
  private boolean _nthDescendant = false;
  int _ptype = -1;
  private String _className = null;
  private ArrayList _closureVars = null;
  private Closure _parentClosure = null;
  private Expression _value = null;
  private Step _step = null;
  
  public Predicate(Expression paramExpression)
  {
    this._exp = paramExpression;
    this._exp.setParent(this);
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    this._exp.setParser(paramParser);
  }
  
  public boolean isNthPositionFilter()
  {
    return this._nthPositionFilter;
  }
  
  public boolean isNthDescendant()
  {
    return this._nthDescendant;
  }
  
  public void dontOptimize()
  {
    this._canOptimize = false;
  }
  
  public boolean hasPositionCall()
  {
    return this._exp.hasPositionCall();
  }
  
  public boolean hasLastCall()
  {
    return this._exp.hasLastCall();
  }
  
  public boolean inInnerClass()
  {
    return this._className != null;
  }
  
  public Closure getParentClosure()
  {
    if (this._parentClosure == null)
    {
      SyntaxTreeNode localSyntaxTreeNode = getParent();
      do
      {
        if ((localSyntaxTreeNode instanceof Closure))
        {
          this._parentClosure = ((Closure)localSyntaxTreeNode);
          break;
        }
        if ((localSyntaxTreeNode instanceof TopLevelElement)) {
          break;
        }
        localSyntaxTreeNode = localSyntaxTreeNode.getParent();
      } while (localSyntaxTreeNode != null);
    }
    return this._parentClosure;
  }
  
  public String getInnerClassName()
  {
    return this._className;
  }
  
  public void addVariable(VariableRefBase paramVariableRefBase)
  {
    if (this._closureVars == null) {
      this._closureVars = new ArrayList();
    }
    if (!this._closureVars.contains(paramVariableRefBase))
    {
      this._closureVars.add(paramVariableRefBase);
      Closure localClosure = getParentClosure();
      if (localClosure != null) {
        localClosure.addVariable(paramVariableRefBase);
      }
    }
  }
  
  public int getPosType()
  {
    if (this._ptype == -1)
    {
      SyntaxTreeNode localSyntaxTreeNode = getParent();
      if ((localSyntaxTreeNode instanceof StepPattern))
      {
        this._ptype = ((StepPattern)localSyntaxTreeNode).getNodeType();
      }
      else
      {
        Object localObject1;
        Object localObject2;
        if ((localSyntaxTreeNode instanceof AbsoluteLocationPath))
        {
          localObject1 = (AbsoluteLocationPath)localSyntaxTreeNode;
          localObject2 = ((AbsoluteLocationPath)localObject1).getPath();
          if ((localObject2 instanceof Step)) {
            this._ptype = ((Step)localObject2).getNodeType();
          }
        }
        else if ((localSyntaxTreeNode instanceof VariableRefBase))
        {
          localObject1 = (VariableRefBase)localSyntaxTreeNode;
          localObject2 = ((VariableRefBase)localObject1).getVariable();
          Expression localExpression = ((VariableBase)localObject2).getExpression();
          if ((localExpression instanceof Step)) {
            this._ptype = ((Step)localExpression).getNodeType();
          }
        }
        else if ((localSyntaxTreeNode instanceof Step))
        {
          this._ptype = ((Step)localSyntaxTreeNode).getNodeType();
        }
      }
    }
    return this._ptype;
  }
  
  public boolean parentIsPattern()
  {
    return getParent() instanceof Pattern;
  }
  
  public Expression getExpr()
  {
    return this._exp;
  }
  
  public String toString()
  {
    return "pred(" + this._exp + ')';
  }
  
  public com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = this._exp.typeCheck(paramSymbolTable);
    if ((localType instanceof ReferenceType)) {
      this._exp = new CastExpr(this._exp, localType = com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Real);
    }
    if ((localType instanceof ResultTreeType))
    {
      this._exp = new CastExpr(this._exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean);
      this._exp = new CastExpr(this._exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Real);
      localType = this._exp.typeCheck(paramSymbolTable);
    }
    if ((localType instanceof NumberType))
    {
      if (!(localType instanceof IntType)) {
        this._exp = new CastExpr(this._exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int);
      }
      if (this._canOptimize)
      {
        this._nthPositionFilter = ((!this._exp.hasLastCall()) && (!this._exp.hasPositionCall()));
        if (this._nthPositionFilter)
        {
          localObject = getParent();
          this._nthDescendant = (((localObject instanceof Step)) && ((((SyntaxTreeNode)localObject).getParent() instanceof AbsoluteLocationPath)));
          return this._type = com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.NodeSet;
        }
      }
      this._nthPositionFilter = (this._nthDescendant = 0);
      Object localObject = getParser().getQNameIgnoreDefaultNs("position");
      PositionCall localPositionCall = new PositionCall((QName)localObject);
      localPositionCall.setParser(getParser());
      localPositionCall.setParent(this);
      this._exp = new EqualityExpr(0, localPositionCall, this._exp);
      if (this._exp.typeCheck(paramSymbolTable) != com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean) {
        this._exp = new CastExpr(this._exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean);
      }
      return this._type = com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean;
    }
    if (!(localType instanceof BooleanType)) {
      this._exp = new CastExpr(this._exp, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean);
    }
    return this._type = com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean;
  }
  
  private void compileFilter(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    this._className = getXSLTC().getHelperClassName();
    FilterGenerator localFilterGenerator = new FilterGenerator(this._className, "java.lang.Object", toString(), 33, new String[] { "com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListFilter" }, paramClassGenerator.getStylesheet());
    ConstantPoolGen localConstantPoolGen = localFilterGenerator.getConstantPool();
    int i = this._closureVars == null ? 0 : this._closureVars.size();
    for (int j = 0; j < i; j++)
    {
      localObject = ((VariableRefBase)this._closureVars.get(j)).getVariable();
      localFilterGenerator.addField(new Field(1, localConstantPoolGen.addUtf8(((VariableBase)localObject).getEscapedName()), localConstantPoolGen.addUtf8(((VariableBase)localObject).getType().toSignature()), null, localConstantPoolGen.getConstantPool()));
    }
    InstructionList localInstructionList = new InstructionList();
    TestGenerator localTestGenerator = new TestGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[] { com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;") }, new String[] { "node", "position", "last", "current", "translet", "iterator" }, "test", this._className, localInstructionList, localConstantPoolGen);
    LocalVariableGen localLocalVariableGen = localTestGenerator.addLocalVariable("document", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
    Object localObject = paramClassGenerator.getClassName();
    localInstructionList.append(localFilterGenerator.loadTranslet());
    localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass((String)localObject)));
    localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref((String)localObject, "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
    localLocalVariableGen.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
    localTestGenerator.setDomIndex(localLocalVariableGen.getIndex());
    this._exp.translate(localFilterGenerator, localTestGenerator);
    localInstructionList.append(IRETURN);
    localFilterGenerator.addEmptyConstructor(1);
    localFilterGenerator.addMethod(localTestGenerator);
    getXSLTC().dumpClass(localFilterGenerator.getJavaClass());
  }
  
  public boolean isBooleanTest()
  {
    return this._exp instanceof BooleanExpr;
  }
  
  public boolean isNodeValueTest()
  {
    if (!this._canOptimize) {
      return false;
    }
    return (getStep() != null) && (getCompareValue() != null);
  }
  
  public Step getStep()
  {
    if (this._step != null) {
      return this._step;
    }
    if (this._exp == null) {
      return null;
    }
    if ((this._exp instanceof EqualityExpr))
    {
      EqualityExpr localEqualityExpr = (EqualityExpr)this._exp;
      Expression localExpression1 = localEqualityExpr.getLeft();
      Expression localExpression2 = localEqualityExpr.getRight();
      if ((localExpression1 instanceof CastExpr)) {
        localExpression1 = ((CastExpr)localExpression1).getExpr();
      }
      if ((localExpression1 instanceof Step)) {
        this._step = ((Step)localExpression1);
      }
      if ((localExpression2 instanceof CastExpr)) {
        localExpression2 = ((CastExpr)localExpression2).getExpr();
      }
      if ((localExpression2 instanceof Step)) {
        this._step = ((Step)localExpression2);
      }
    }
    return this._step;
  }
  
  public Expression getCompareValue()
  {
    if (this._value != null) {
      return this._value;
    }
    if (this._exp == null) {
      return null;
    }
    if ((this._exp instanceof EqualityExpr))
    {
      EqualityExpr localEqualityExpr = (EqualityExpr)this._exp;
      Expression localExpression1 = localEqualityExpr.getLeft();
      Expression localExpression2 = localEqualityExpr.getRight();
      if ((localExpression1 instanceof LiteralExpr))
      {
        this._value = localExpression1;
        return this._value;
      }
      if (((localExpression1 instanceof VariableRefBase)) && (localExpression1.getType() == com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String))
      {
        this._value = localExpression1;
        return this._value;
      }
      if ((localExpression2 instanceof LiteralExpr))
      {
        this._value = localExpression2;
        return this._value;
      }
      if (((localExpression2 instanceof VariableRefBase)) && (localExpression2.getType() == com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String))
      {
        this._value = localExpression2;
        return this._value;
      }
    }
    return null;
  }
  
  public void translateFilter(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    compileFilter(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new NEW(localConstantPoolGen.addClass(this._className)));
    localInstructionList.append(DUP);
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref(this._className, "<init>", "()V")));
    int i = this._closureVars == null ? 0 : this._closureVars.size();
    for (int j = 0; j < i; j++)
    {
      VariableRefBase localVariableRefBase = (VariableRefBase)this._closureVars.get(j);
      VariableBase localVariableBase = localVariableRefBase.getVariable();
      com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = localVariableBase.getType();
      localInstructionList.append(DUP);
      for (Closure localClosure = this._parentClosure; (localClosure != null) && (!localClosure.inInnerClass()); localClosure = localClosure.getParentClosure()) {}
      if (localClosure != null)
      {
        localInstructionList.append(ALOAD_0);
        localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref(localClosure.getInnerClassName(), localVariableBase.getEscapedName(), localType.toSignature())));
      }
      else
      {
        localInstructionList.append(localVariableBase.loadInstruction());
      }
      localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref(this._className, localVariableBase.getEscapedName(), localType.toSignature())));
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((this._nthPositionFilter) || (this._nthDescendant))
    {
      this._exp.translate(paramClassGenerator, paramMethodGenerator);
    }
    else if ((isNodeValueTest()) && ((getParent() instanceof Step)))
    {
      this._value.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("java.lang.String")));
      localInstructionList.append(new PUSH(localConstantPoolGen, ((EqualityExpr)this._exp).getOp()));
    }
    else
    {
      translateFilter(paramClassGenerator, paramMethodGenerator);
    }
  }
}
