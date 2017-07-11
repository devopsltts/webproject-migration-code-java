package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncString
  extends FunctionDef1Arg
{
  static final long serialVersionUID = -2206677149497712883L;
  
  public FuncString() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return (XString)getArg0AsString(paramXPathContext);
  }
}
