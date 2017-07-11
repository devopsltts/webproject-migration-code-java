package com.sun.xml.internal.txw2;

final class EndTag
  extends Content
{
  EndTag() {}
  
  boolean concludesPendingStartTag()
  {
    return true;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onEndTag();
  }
}
