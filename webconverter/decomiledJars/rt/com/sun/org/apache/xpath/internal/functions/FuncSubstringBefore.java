package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncSubstringBefore
  extends Function2Args
{
  static final long serialVersionUID = 4110547161672431775L;
  
  public FuncSubstringBefore() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    String str1 = this.m_arg0.execute(paramXPathContext).str();
    String str2 = this.m_arg1.execute(paramXPathContext).str();
    int i = str1.indexOf(str2);
    return -1 == i ? XString.EMPTYSTRING : new XString(str1.substring(0, i));
  }
}
