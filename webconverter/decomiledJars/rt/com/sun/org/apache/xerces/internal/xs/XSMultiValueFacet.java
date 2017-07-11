package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSMultiValueFacet
  extends XSObject
{
  public abstract short getFacetKind();
  
  public abstract StringList getLexicalFacetValues();
  
  public abstract XSObjectList getAnnotations();
}
