package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Deprecated
public abstract interface ElementChecker
{
  public abstract void guaranteeThatElementInCorrectSpace(ElementProxy paramElementProxy, Element paramElement)
    throws XMLSecurityException;
  
  public abstract boolean isNamespaceElement(Node paramNode, String paramString1, String paramString2);
}
