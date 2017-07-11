package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncStartsWith
  extends Function2Args
{
  static final long serialVersionUID = 2194585774699567928L;
  
  public FuncStartsWith() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return this.m_arg0.execute(paramXPathContext).xstr().startsWith(this.m_arg1.execute(paramXPathContext).xstr()) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
