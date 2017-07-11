package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyElementImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class BodyElement1_1Impl
  extends BodyElementImpl
{
  public BodyElement1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName);
  }
  
  public BodyElement1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    BodyElement1_1Impl localBodyElement1_1Impl = new BodyElement1_1Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName);
    return replaceElementWithSOAPElement(this, localBodyElement1_1Impl);
  }
}
