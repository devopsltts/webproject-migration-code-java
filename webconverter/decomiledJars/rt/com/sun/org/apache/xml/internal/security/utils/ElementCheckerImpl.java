package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Deprecated
public abstract class ElementCheckerImpl
  implements ElementChecker
{
  public ElementCheckerImpl() {}
  
  public boolean isNamespaceElement(Node paramNode, String paramString1, String paramString2)
  {
    return (paramNode != null) && (paramString2 == paramNode.getNamespaceURI()) && (paramNode.getLocalName().equals(paramString1));
  }
  
  public static class EmptyChecker
    extends ElementCheckerImpl
  {
    public EmptyChecker() {}
    
    public void guaranteeThatElementInCorrectSpace(ElementProxy paramElementProxy, Element paramElement)
      throws XMLSecurityException
    {}
  }
  
  public static class FullChecker
    extends ElementCheckerImpl
  {
    public FullChecker() {}
    
    public void guaranteeThatElementInCorrectSpace(ElementProxy paramElementProxy, Element paramElement)
      throws XMLSecurityException
    {
      String str1 = paramElementProxy.getBaseLocalName();
      String str2 = paramElementProxy.getBaseNamespace();
      String str3 = paramElement.getLocalName();
      String str4 = paramElement.getNamespaceURI();
      if ((!str2.equals(str4)) || (!str1.equals(str3)))
      {
        Object[] arrayOfObject = { str4 + ":" + str3, str2 + ":" + str1 };
        throw new XMLSecurityException("xml.WrongElement", arrayOfObject);
      }
    }
  }
  
  public static class InternedNsChecker
    extends ElementCheckerImpl
  {
    public InternedNsChecker() {}
    
    public void guaranteeThatElementInCorrectSpace(ElementProxy paramElementProxy, Element paramElement)
      throws XMLSecurityException
    {
      String str1 = paramElementProxy.getBaseLocalName();
      String str2 = paramElementProxy.getBaseNamespace();
      String str3 = paramElement.getLocalName();
      String str4 = paramElement.getNamespaceURI();
      if ((str2 != str4) || (!str1.equals(str3)))
      {
        Object[] arrayOfObject = { str4 + ":" + str3, str2 + ":" + str1 };
        throw new XMLSecurityException("xml.WrongElement", arrayOfObject);
      }
    }
  }
}
