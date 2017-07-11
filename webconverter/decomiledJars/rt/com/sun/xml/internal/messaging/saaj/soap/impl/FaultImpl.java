package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPFaultElement;
import org.w3c.dom.Element;

public abstract class FaultImpl
  extends ElementImpl
  implements SOAPFault
{
  protected SOAPFaultElement faultStringElement;
  protected SOAPFaultElement faultActorElement;
  protected SOAPFaultElement faultCodeElement;
  protected Detail detail;
  
  protected FaultImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl)
  {
    super(paramSOAPDocumentImpl, paramNameImpl);
  }
  
  protected abstract NameImpl getDetailName();
  
  protected abstract NameImpl getFaultCodeName();
  
  protected abstract NameImpl getFaultStringName();
  
  protected abstract NameImpl getFaultActorName();
  
  protected abstract DetailImpl createDetail();
  
  protected abstract FaultElementImpl createSOAPFaultElement(String paramString);
  
  protected abstract FaultElementImpl createSOAPFaultElement(QName paramQName);
  
  protected abstract FaultElementImpl createSOAPFaultElement(Name paramName);
  
  protected abstract void checkIfStandardFaultCode(String paramString1, String paramString2)
    throws SOAPException;
  
  protected abstract void finallySetFaultCode(String paramString)
    throws SOAPException;
  
  protected abstract boolean isStandardFaultElement(String paramString);
  
  protected abstract QName getDefaultFaultCode();
  
  protected void findFaultCodeElement()
  {
    this.faultCodeElement = ((SOAPFaultElement)findChild(getFaultCodeName()));
  }
  
  protected void findFaultActorElement()
  {
    this.faultActorElement = ((SOAPFaultElement)findChild(getFaultActorName()));
  }
  
  protected void findFaultStringElement()
  {
    this.faultStringElement = ((SOAPFaultElement)findChild(getFaultStringName()));
  }
  
  public void setFaultCode(String paramString)
    throws SOAPException
  {
    setFaultCode(NameImpl.getLocalNameFromTagName(paramString), NameImpl.getPrefixFromTagName(paramString), null);
  }
  
  public void setFaultCode(String paramString1, String paramString2, String paramString3)
    throws SOAPException
  {
    if (((paramString2 == null) || ("".equals(paramString2))) && (paramString3 != null) && (!"".equals(paramString3)))
    {
      paramString2 = getNamespacePrefix(paramString3);
      if ((paramString2 == null) || ("".equals(paramString2))) {
        paramString2 = "ns0";
      }
    }
    if (this.faultCodeElement == null) {
      findFaultCodeElement();
    }
    if (this.faultCodeElement == null) {
      this.faultCodeElement = addFaultCodeElement();
    } else {
      this.faultCodeElement.removeContents();
    }
    if ((paramString3 == null) || ("".equals(paramString3))) {
      paramString3 = this.faultCodeElement.getNamespaceURI(paramString2);
    }
    if ((paramString3 == null) || ("".equals(paramString3)))
    {
      if ((paramString2 != null) && (!"".equals(paramString2)))
      {
        log.log(Level.SEVERE, "SAAJ0140.impl.no.ns.URI", new Object[] { paramString2 + ":" + paramString1 });
        throw new SOAPExceptionImpl("Empty/Null NamespaceURI specified for faultCode \"" + paramString2 + ":" + paramString1 + "\"");
      }
      paramString3 = "";
    }
    checkIfStandardFaultCode(paramString1, paramString3);
    ((FaultElementImpl)this.faultCodeElement).ensureNamespaceIsDeclared(paramString2, paramString3);
    if ((paramString2 == null) || ("".equals(paramString2))) {
      finallySetFaultCode(paramString1);
    } else {
      finallySetFaultCode(paramString2 + ":" + paramString1);
    }
  }
  
  public void setFaultCode(Name paramName)
    throws SOAPException
  {
    setFaultCode(paramName.getLocalName(), paramName.getPrefix(), paramName.getURI());
  }
  
  public void setFaultCode(QName paramQName)
    throws SOAPException
  {
    setFaultCode(paramQName.getLocalPart(), paramQName.getPrefix(), paramQName.getNamespaceURI());
  }
  
  protected static QName convertCodeToQName(String paramString, SOAPElement paramSOAPElement)
  {
    int i = paramString.indexOf(':');
    if (i == -1) {
      return new QName(paramString);
    }
    String str1 = paramString.substring(0, i);
    String str2 = ((ElementImpl)paramSOAPElement).lookupNamespaceURI(str1);
    return new QName(str2, getLocalPart(paramString), str1);
  }
  
  protected void initializeDetail()
  {
    NameImpl localNameImpl = getDetailName();
    this.detail = ((Detail)findChild(localNameImpl));
  }
  
  public Detail getDetail()
  {
    if (this.detail == null) {
      initializeDetail();
    }
    if ((this.detail != null) && (this.detail.getParentNode() == null)) {
      this.detail = null;
    }
    return this.detail;
  }
  
  public Detail addDetail()
    throws SOAPException
  {
    if (this.detail == null) {
      initializeDetail();
    }
    if (this.detail == null)
    {
      this.detail = createDetail();
      addNode(this.detail);
      return this.detail;
    }
    throw new SOAPExceptionImpl("Error: Detail already exists");
  }
  
  public boolean hasDetail()
  {
    return getDetail() != null;
  }
  
  public abstract void setFaultActor(String paramString)
    throws SOAPException;
  
  public String getFaultActor()
  {
    if (this.faultActorElement == null) {
      findFaultActorElement();
    }
    if (this.faultActorElement != null) {
      return this.faultActorElement.getValue();
    }
    return null;
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), paramQName.getLocalPart() });
    throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + paramQName.getLocalPart());
  }
  
  protected SOAPElement convertToSoapElement(Element paramElement)
  {
    if ((paramElement instanceof SOAPFaultElement)) {
      return (SOAPElement)paramElement;
    }
    Object localObject2;
    if ((paramElement instanceof SOAPElement))
    {
      localObject1 = (SOAPElement)paramElement;
      if (getDetailName().equals(((SOAPElement)localObject1).getElementName())) {
        return replaceElementWithSOAPElement(paramElement, createDetail());
      }
      localObject2 = ((SOAPElement)localObject1).getElementName().getLocalName();
      if (isStandardFaultElement((String)localObject2)) {
        return replaceElementWithSOAPElement(paramElement, createSOAPFaultElement(((SOAPElement)localObject1).getElementQName()));
      }
      return localObject1;
    }
    Object localObject1 = NameImpl.copyElementName(paramElement);
    if (getDetailName().equals(localObject1))
    {
      localObject2 = createDetail();
    }
    else
    {
      String str = ((Name)localObject1).getLocalName();
      if (isStandardFaultElement(str)) {
        localObject2 = createSOAPFaultElement((Name)localObject1);
      } else {
        localObject2 = (ElementImpl)createElement((Name)localObject1);
      }
    }
    return replaceElementWithSOAPElement(paramElement, (ElementImpl)localObject2);
  }
  
  protected SOAPFaultElement addFaultCodeElement()
    throws SOAPException
  {
    if (this.faultCodeElement == null) {
      findFaultCodeElement();
    }
    if (this.faultCodeElement == null)
    {
      this.faultCodeElement = addSOAPFaultElement(getFaultCodeName().getLocalName());
      return this.faultCodeElement;
    }
    throw new SOAPExceptionImpl("Error: Faultcode already exists");
  }
  
  private SOAPFaultElement addFaultStringElement()
    throws SOAPException
  {
    if (this.faultStringElement == null) {
      findFaultStringElement();
    }
    if (this.faultStringElement == null)
    {
      this.faultStringElement = addSOAPFaultElement(getFaultStringName().getLocalName());
      return this.faultStringElement;
    }
    throw new SOAPExceptionImpl("Error: Faultstring already exists");
  }
  
  private SOAPFaultElement addFaultActorElement()
    throws SOAPException
  {
    if (this.faultActorElement == null) {
      findFaultActorElement();
    }
    if (this.faultActorElement == null)
    {
      this.faultActorElement = addSOAPFaultElement(getFaultActorName().getLocalName());
      return this.faultActorElement;
    }
    throw new SOAPExceptionImpl("Error: Faultactor already exists");
  }
  
  protected SOAPElement addElement(Name paramName)
    throws SOAPException
  {
    if (getDetailName().equals(paramName)) {
      return addDetail();
    }
    if (getFaultCodeName().equals(paramName)) {
      return addFaultCodeElement();
    }
    if (getFaultStringName().equals(paramName)) {
      return addFaultStringElement();
    }
    if (getFaultActorName().equals(paramName)) {
      return addFaultActorElement();
    }
    return super.addElement(paramName);
  }
  
  protected SOAPElement addElement(QName paramQName)
    throws SOAPException
  {
    return addElement(NameImpl.convertToName(paramQName));
  }
  
  protected FaultElementImpl addSOAPFaultElement(String paramString)
    throws SOAPException
  {
    FaultElementImpl localFaultElementImpl = createSOAPFaultElement(paramString);
    addNode(localFaultElementImpl);
    return localFaultElementImpl;
  }
  
  protected static Locale xmlLangToLocale(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.indexOf("-");
    if (i == -1) {
      i = paramString.indexOf("_");
    }
    if (i == -1) {
      return new Locale(paramString, "");
    }
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1);
    return new Locale(str1, str2);
  }
  
  protected static String localeToXmlLang(Locale paramLocale)
  {
    String str1 = paramLocale.getLanguage();
    String str2 = paramLocale.getCountry();
    if (!"".equals(str2)) {
      str1 = str1 + "-" + str2;
    }
    return str1;
  }
}
