package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class HeaderElement1_2Impl
  extends HeaderElementImpl
{
  private static final Logger log = Logger.getLogger(HeaderElement1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
  
  public HeaderElement1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName);
  }
  
  public HeaderElement1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    HeaderElement1_2Impl localHeaderElement1_2Impl = new HeaderElement1_2Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName);
    return replaceElementWithSOAPElement(this, localHeaderElement1_2Impl);
  }
  
  protected NameImpl getRoleAttributeName()
  {
    return NameImpl.create("role", null, "http://www.w3.org/2003/05/soap-envelope");
  }
  
  protected NameImpl getActorAttributeName()
  {
    return getRoleAttributeName();
  }
  
  protected NameImpl getMustunderstandAttributeName()
  {
    return NameImpl.create("mustUnderstand", null, "http://www.w3.org/2003/05/soap-envelope");
  }
  
  protected String getMustunderstandLiteralValue(boolean paramBoolean)
  {
    return paramBoolean == true ? "true" : "false";
  }
  
  protected boolean getMustunderstandAttributeValue(String paramString)
  {
    return (paramString.equals("true")) || (paramString.equals("1"));
  }
  
  protected NameImpl getRelayAttributeName()
  {
    return NameImpl.create("relay", null, "http://www.w3.org/2003/05/soap-envelope");
  }
  
  protected String getRelayLiteralValue(boolean paramBoolean)
  {
    return paramBoolean == true ? "true" : "false";
  }
  
  protected boolean getRelayAttributeValue(String paramString)
  {
    return (paramString.equals("true")) || (paramString.equals("1"));
  }
  
  protected String getActorOrRole()
  {
    return getRole();
  }
}
