package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Attribute
  extends Instruction
{
  private QName _name;
  
  Attribute() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Attribute " + this._name);
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser)
  {
    this._name = paramParser.getQName(getAttribute("name"));
    parseChildren(paramParser);
  }
}
