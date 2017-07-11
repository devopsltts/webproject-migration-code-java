package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncExtElementAvailable
  extends FunctionOneArg
{
  static final long serialVersionUID = -472533699257968546L;
  
  public FuncExtElementAvailable() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    String str4 = this.m_arg0.execute(paramXPathContext).str();
    int i = str4.indexOf(':');
    String str1;
    String str2;
    String str3;
    if (i < 0)
    {
      str1 = "";
      str2 = "http://www.w3.org/1999/XSL/Transform";
      str3 = str4;
    }
    else
    {
      str1 = str4.substring(0, i);
      str2 = paramXPathContext.getNamespaceContext().getNamespaceForPrefix(str1);
      if (null == str2) {
        return XBoolean.S_FALSE;
      }
      str3 = str4.substring(i + 1);
    }
    if ((str2.equals("http://www.w3.org/1999/XSL/Transform")) || (str2.equals("http://xml.apache.org/xalan"))) {
      return XBoolean.S_FALSE;
    }
    ExtensionsProvider localExtensionsProvider = (ExtensionsProvider)paramXPathContext.getOwnerObject();
    return localExtensionsProvider.elementAvailable(str2, str3) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
