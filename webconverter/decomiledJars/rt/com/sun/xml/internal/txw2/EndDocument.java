package com.sun.xml.internal.txw2;

final class EndDocument
  extends Content
{
  EndDocument() {}
  
  boolean concludesPendingStartTag()
  {
    return true;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onEndDocument();
  }
}
