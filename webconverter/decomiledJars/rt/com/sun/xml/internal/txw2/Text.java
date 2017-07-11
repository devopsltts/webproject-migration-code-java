package com.sun.xml.internal.txw2;

abstract class Text
  extends Content
{
  protected final StringBuilder buffer = new StringBuilder();
  
  protected Text(Document paramDocument, NamespaceResolver paramNamespaceResolver, Object paramObject)
  {
    paramDocument.writeValue(paramObject, paramNamespaceResolver, this.buffer);
  }
  
  boolean concludesPendingStartTag()
  {
    return false;
  }
}
