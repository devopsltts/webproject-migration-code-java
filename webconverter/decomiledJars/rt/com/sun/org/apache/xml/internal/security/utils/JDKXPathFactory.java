package com.sun.org.apache.xml.internal.security.utils;

public class JDKXPathFactory
  extends XPathFactory
{
  public JDKXPathFactory() {}
  
  public XPathAPI newXPathAPI()
  {
    return new JDKXPathAPI();
  }
}
