package com.sun.xml.internal.txw2;

abstract class Content
{
  private Content next;
  
  Content() {}
  
  final Content getNext()
  {
    return this.next;
  }
  
  final void setNext(Document paramDocument, Content paramContent)
  {
    assert (paramContent != null);
    assert (this.next == null) : ("next of " + this + " is already set to " + this.next);
    this.next = paramContent;
    paramDocument.run();
  }
  
  boolean isReadyToCommit()
  {
    return true;
  }
  
  abstract boolean concludesPendingStartTag();
  
  abstract void accept(ContentVisitor paramContentVisitor);
  
  public void written() {}
}
