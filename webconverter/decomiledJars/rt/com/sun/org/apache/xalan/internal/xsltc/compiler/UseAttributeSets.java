package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.StringTokenizer;
import java.util.Vector;

final class UseAttributeSets
  extends Instruction
{
  private static final String ATTR_SET_NOT_FOUND = "";
  private final Vector _sets = new Vector(2);
  
  public UseAttributeSets(String paramString, Parser paramParser)
  {
    setParser(paramParser);
    addAttributeSets(paramString);
  }
  
  public void addAttributeSets(String paramString)
  {
    if ((paramString != null) && (!paramString.equals("")))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      while (localStringTokenizer.hasMoreTokens())
      {
        QName localQName = getParser().getQNameIgnoreDefaultNs(localStringTokenizer.nextToken());
        this._sets.add(localQName);
      }
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    SymbolTable localSymbolTable = getParser().getSymbolTable();
    for (int i = 0; i < this._sets.size(); i++)
    {
      QName localQName = (QName)this._sets.elementAt(i);
      AttributeSet localAttributeSet = localSymbolTable.lookupAttributeSet(localQName);
      Object localObject;
      if (localAttributeSet != null)
      {
        localObject = localAttributeSet.getMethodName();
        localInstructionList.append(paramClassGenerator.loadTranslet());
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(paramMethodGenerator.loadIterator());
        localInstructionList.append(paramMethodGenerator.loadHandler());
        localInstructionList.append(paramMethodGenerator.loadCurrentNode());
        int j = localConstantPoolGen.addMethodref(paramClassGenerator.getClassName(), (String)localObject, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V");
        localInstructionList.append(new INVOKESPECIAL(j));
      }
      else
      {
        localObject = getParser();
        String str = localQName.toString();
        reportError(this, (Parser)localObject, "ATTRIBSET_UNDEF_ERR", str);
      }
    }
  }
}
