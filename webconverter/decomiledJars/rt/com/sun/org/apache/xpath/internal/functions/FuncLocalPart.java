package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncLocalPart
  extends FunctionDef1Arg
{
  static final long serialVersionUID = 7591798770325814746L;
  
  public FuncLocalPart() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = getArg0AsNode(paramXPathContext);
    if (-1 == i) {
      return XString.EMPTYSTRING;
    }
    DTM localDTM = paramXPathContext.getDTM(i);
    String str = i != -1 ? localDTM.getLocalName(i) : "";
    if ((str.startsWith("#")) || (str.equals("xmlns"))) {
      return XString.EMPTYSTRING;
    }
    return new XString(str);
  }
}
