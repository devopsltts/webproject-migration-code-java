package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Lte
  extends Operation
{
  static final long serialVersionUID = 6945650810527140228L;
  
  public Lte() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return paramXObject1.lessThanOrEqual(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
