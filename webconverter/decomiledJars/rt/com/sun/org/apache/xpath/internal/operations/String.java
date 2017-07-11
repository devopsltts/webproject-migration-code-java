package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class String
  extends UnaryOperation
{
  static final long serialVersionUID = 2973374377453022888L;
  
  public String() {}
  
  public XObject operate(XObject paramXObject)
    throws TransformerException
  {
    return (XString)paramXObject.xstr();
  }
}
