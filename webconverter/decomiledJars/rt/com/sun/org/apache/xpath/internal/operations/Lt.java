package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Lt
  extends Operation
{
  static final long serialVersionUID = 3388420509289359422L;
  
  public Lt() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return paramXObject1.lessThan(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
