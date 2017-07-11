package com.sun.xml.internal.txw2;

final class StartDocument
  extends Content
{
  StartDocument() {}
  
  boolean concludesPendingStartTag()
  {
    return true;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onStartDocument();
  }
}
