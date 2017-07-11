package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncNot
  extends FunctionOneArg
{
  static final long serialVersionUID = 7299699961076329790L;
  
  public FuncNot() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return this.m_arg0.execute(paramXPathContext).bool() ? XBoolean.S_FALSE : XBoolean.S_TRUE;
  }
}
