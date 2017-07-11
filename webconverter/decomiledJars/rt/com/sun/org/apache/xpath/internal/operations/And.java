package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class And
  extends Operation
{
  static final long serialVersionUID = 392330077126534022L;
  
  public And() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XObject localXObject1 = this.m_left.execute(paramXPathContext);
    if (localXObject1.bool())
    {
      XObject localXObject2 = this.m_right.execute(paramXPathContext);
      return localXObject2.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    return XBoolean.S_FALSE;
  }
  
  public boolean bool(XPathContext paramXPathContext)
    throws TransformerException
  {
    return (this.m_left.bool(paramXPathContext)) && (this.m_right.bool(paramXPathContext));
  }
}
