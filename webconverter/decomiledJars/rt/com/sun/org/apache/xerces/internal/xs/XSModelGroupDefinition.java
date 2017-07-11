package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSModelGroupDefinition
  extends XSObject
{
  public abstract XSModelGroup getModelGroup();
  
  public abstract XSAnnotation getAnnotation();
  
  public abstract XSObjectList getAnnotations();
}
