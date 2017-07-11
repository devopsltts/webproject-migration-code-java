package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FunctionDef1Arg
  extends FunctionOneArg
{
  static final long serialVersionUID = 2325189412814149264L;
  
  public FunctionDef1Arg() {}
  
  protected int getArg0AsNode(XPathContext paramXPathContext)
    throws TransformerException
  {
    return null == this.m_arg0 ? paramXPathContext.getCurrentNode() : this.m_arg0.asNode(paramXPathContext);
  }
  
  public boolean Arg0IsNodesetExpr()
  {
    return null == this.m_arg0 ? true : this.m_arg0.isNodesetExpr();
  }
  
  protected XMLString getArg0AsString(XPathContext paramXPathContext)
    throws TransformerException
  {
    if (null == this.m_arg0)
    {
      int i = paramXPathContext.getCurrentNode();
      if (-1 == i) {
        return XString.EMPTYSTRING;
      }
      DTM localDTM = paramXPathContext.getDTM(i);
      return localDTM.getStringValue(i);
    }
    return this.m_arg0.execute(paramXPathContext).xstr();
  }
  
  protected double getArg0AsNumber(XPathContext paramXPathContext)
    throws TransformerException
  {
    if (null == this.m_arg0)
    {
      int i = paramXPathContext.getCurrentNode();
      if (-1 == i) {
        return 0.0D;
      }
      DTM localDTM = paramXPathContext.getDTM(i);
      XMLString localXMLString = localDTM.getStringValue(i);
      return localXMLString.toDouble();
    }
    return this.m_arg0.execute(paramXPathContext).num();
  }
  
  public void checkNumberArgs(int paramInt)
    throws WrongNumberArgsException
  {
    if (paramInt > 1) {
      reportWrongNumberArgs();
    }
  }
  
  protected void reportWrongNumberArgs()
    throws WrongNumberArgsException
  {
    throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("ER_ZERO_OR_ONE", null));
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    return null == this.m_arg0 ? false : super.canTraverseOutsideSubtree();
  }
}
