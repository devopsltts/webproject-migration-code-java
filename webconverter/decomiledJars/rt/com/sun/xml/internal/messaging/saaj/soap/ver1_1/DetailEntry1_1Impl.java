package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailEntryImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class DetailEntry1_1Impl
  extends DetailEntryImpl
{
  public DetailEntry1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName);
  }
  
  public DetailEntry1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    DetailEntry1_1Impl localDetailEntry1_1Impl = new DetailEntry1_1Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName);
    return replaceElementWithSOAPElement(this, localDetailEntry1_1Impl);
  }
}
