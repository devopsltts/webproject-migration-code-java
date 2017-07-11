package com.sun.xml.internal.txw2;

final class Comment
  extends Content
{
  private final StringBuilder buffer = new StringBuilder();
  
  public Comment(Document paramDocument, NamespaceResolver paramNamespaceResolver, Object paramObject)
  {
    paramDocument.writeValue(paramObject, paramNamespaceResolver, this.buffer);
  }
  
  boolean concludesPendingStartTag()
  {
    return false;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onComment(this.buffer);
  }
}
