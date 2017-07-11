package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public abstract class BodyElementImpl
  extends ElementImpl
  implements SOAPBodyElement
{
  public BodyElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName);
  }
  
  public BodyElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  public void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    if (!(paramSOAPElement instanceof SOAPBody))
    {
      log.severe("SAAJ0101.impl.parent.of.body.elem.mustbe.body");
      throw new SOAPException("Parent of a SOAPBodyElement has to be a SOAPBody");
    }
    super.setParentElement(paramSOAPElement);
  }
}
