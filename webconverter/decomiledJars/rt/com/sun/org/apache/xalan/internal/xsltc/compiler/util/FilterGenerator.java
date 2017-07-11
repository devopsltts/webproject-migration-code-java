package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;

public final class FilterGenerator
  extends ClassGenerator
{
  private static int TRANSLET_INDEX = 5;
  private final Instruction _aloadTranslet = new ALOAD(TRANSLET_INDEX);
  
  public FilterGenerator(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString, Stylesheet paramStylesheet)
  {
    super(paramString1, paramString2, paramString3, paramInt, paramArrayOfString, paramStylesheet);
  }
  
  public final Instruction loadTranslet()
  {
    return this._aloadTranslet;
  }
  
  public boolean isExternal()
  {
    return true;
  }
}
