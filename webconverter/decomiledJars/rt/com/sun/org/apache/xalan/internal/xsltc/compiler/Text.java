package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Text
  extends Instruction
{
  private String _text;
  private boolean _escaping = true;
  private boolean _ignore = false;
  private boolean _textElement = false;
  
  public Text()
  {
    this._textElement = true;
  }
  
  public Text(String paramString)
  {
    this._text = paramString;
  }
  
  protected String getText()
  {
    return this._text;
  }
  
  protected void setText(String paramString)
  {
    if (this._text == null) {
      this._text = paramString;
    } else {
      this._text += paramString;
    }
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Text");
    indent(paramInt + 4);
    Util.println(this._text);
  }
  
  public void parseContents(Parser paramParser)
  {
    String str1 = getAttribute("disable-output-escaping");
    if ((str1 != null) && (str1.equals("yes"))) {
      this._escaping = false;
    }
    parseChildren(paramParser);
    if (this._text == null)
    {
      if (this._textElement) {
        this._text = "";
      } else {
        this._ignore = true;
      }
    }
    else if (this._textElement)
    {
      if (this._text.length() == 0) {
        this._ignore = true;
      }
    }
    else
    {
      int k;
      if ((getParent() instanceof LiteralElement))
      {
        LiteralElement localLiteralElement = (LiteralElement)getParent();
        String str2 = localLiteralElement.getAttribute("xml:space");
        if ((str2 == null) || (!str2.equals("preserve")))
        {
          int m = this._text.length();
          for (k = 0; k < m; k++)
          {
            char c = this._text.charAt(k);
            if (!isWhitespace(c)) {
              break;
            }
          }
          if (k == m) {
            this._ignore = true;
          }
        }
      }
      else
      {
        int j = this._text.length();
        for (int i = 0; i < j; i++)
        {
          k = this._text.charAt(i);
          if (!isWhitespace(k)) {
            break;
          }
        }
        if (i == j) {
          this._ignore = true;
        }
      }
    }
  }
  
  public void ignore()
  {
    this._ignore = true;
  }
  
  public boolean isIgnore()
  {
    return this._ignore;
  }
  
  public boolean isTextElement()
  {
    return this._textElement;
  }
  
  protected boolean contextDependent()
  {
    return false;
  }
  
  private static boolean isWhitespace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\t') || (paramChar == '\n') || (paramChar == '\r');
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (!this._ignore)
    {
      int i = localConstantPoolGen.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "setEscaping", "(Z)Z");
      if (!this._escaping)
      {
        localInstructionList.append(paramMethodGenerator.loadHandler());
        localInstructionList.append(new PUSH(localConstantPoolGen, false));
        localInstructionList.append(new INVOKEINTERFACE(i, 2));
      }
      localInstructionList.append(paramMethodGenerator.loadHandler());
      int j;
      if (!canLoadAsArrayOffsetLength())
      {
        j = localConstantPoolGen.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "characters", "(Ljava/lang/String;)V");
        localInstructionList.append(new PUSH(localConstantPoolGen, this._text));
        localInstructionList.append(new INVOKEINTERFACE(j, 2));
      }
      else
      {
        j = localConstantPoolGen.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "characters", "([CII)V");
        loadAsArrayOffsetLength(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(new INVOKEINTERFACE(j, 4));
      }
      if (!this._escaping)
      {
        localInstructionList.append(paramMethodGenerator.loadHandler());
        localInstructionList.append(SWAP);
        localInstructionList.append(new INVOKEINTERFACE(i, 2));
        localInstructionList.append(POP);
      }
    }
    translateContents(paramClassGenerator, paramMethodGenerator);
  }
  
  public boolean canLoadAsArrayOffsetLength()
  {
    return this._text.length() <= 21845;
  }
  
  public void loadAsArrayOffsetLength(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    XSLTC localXSLTC = paramClassGenerator.getParser().getXSLTC();
    int i = localXSLTC.addCharacterData(this._text);
    int j = this._text.length();
    String str = "_scharData" + (localXSLTC.getCharacterDataCount() - 1);
    localInstructionList.append(new GETSTATIC(localConstantPoolGen.addFieldref(localXSLTC.getClassName(), str, "[C")));
    localInstructionList.append(new PUSH(localConstantPoolGen, i));
    localInstructionList.append(new PUSH(localConstantPoolGen, this._text.length()));
  }
}
