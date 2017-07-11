package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xs.XSModel;

public abstract interface XSGrammar
  extends Grammar
{
  public abstract XSModel toXSModel();
  
  public abstract XSModel toXSModel(XSGrammar[] paramArrayOfXSGrammar);
}
