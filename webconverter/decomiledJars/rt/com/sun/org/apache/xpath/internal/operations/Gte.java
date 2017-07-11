package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Gte
  extends Operation
{
  static final long serialVersionUID = 9142945909906680220L;
  
  public Gte() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return paramXObject1.greaterThanOrEqual(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
