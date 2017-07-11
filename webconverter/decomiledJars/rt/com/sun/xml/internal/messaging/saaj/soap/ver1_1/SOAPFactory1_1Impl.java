package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

public class SOAPFactory1_1Impl
  extends SOAPFactoryImpl
{
  public SOAPFactory1_1Impl() {}
  
  protected SOAPDocumentImpl createDocument()
  {
    return new SOAPPart1_1Impl().getDocument();
  }
  
  public Detail createDetail()
    throws SOAPException
  {
    return new Detail1_1Impl(createDocument());
  }
  
  public SOAPFault createFault(String paramString, QName paramQName)
    throws SOAPException
  {
    if (paramQName == null) {
      throw new IllegalArgumentException("faultCode argument for createFault was passed NULL");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("reasonText argument for createFault was passed NULL");
    }
    Fault1_1Impl localFault1_1Impl = new Fault1_1Impl(createDocument(), null);
    localFault1_1Impl.setFaultCode(paramQName);
    localFault1_1Impl.setFaultString(paramString);
    return localFault1_1Impl;
  }
  
  public SOAPFault createFault()
    throws SOAPException
  {
    Fault1_1Impl localFault1_1Impl = new Fault1_1Impl(createDocument(), null);
    localFault1_1Impl.setFaultCode(localFault1_1Impl.getDefaultFaultCode());
    localFault1_1Impl.setFaultString("Fault string, and possibly fault code, not set");
    return localFault1_1Impl;
  }
}
