package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class NotEquals
  extends Operation
{
  static final long serialVersionUID = -7869072863070586900L;
  
  public NotEquals() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return paramXObject1.notEquals(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
