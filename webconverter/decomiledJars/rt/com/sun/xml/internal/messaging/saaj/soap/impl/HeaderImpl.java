package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class HeaderImpl
  extends ElementImpl
  implements SOAPHeader
{
  protected static final boolean MUST_UNDERSTAND_ONLY = false;
  
  protected HeaderImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl)
  {
    super(paramSOAPDocumentImpl, paramNameImpl);
  }
  
  protected abstract SOAPHeaderElement createHeaderElement(Name paramName)
    throws SOAPException;
  
  protected abstract SOAPHeaderElement createHeaderElement(QName paramQName)
    throws SOAPException;
  
  protected abstract NameImpl getNotUnderstoodName();
  
  protected abstract NameImpl getUpgradeName();
  
  protected abstract NameImpl getSupportedEnvelopeName();
  
  public SOAPHeaderElement addHeaderElement(Name paramName)
    throws SOAPException
  {
    Object localObject = ElementFactory.createNamedElement(((SOAPDocument)getOwnerDocument()).getDocument(), paramName.getLocalName(), paramName.getPrefix(), paramName.getURI());
    if ((localObject == null) || (!(localObject instanceof SOAPHeaderElement))) {
      localObject = createHeaderElement(paramName);
    }
    String str = ((SOAPElement)localObject).getElementQName().getNamespaceURI();
    if ((str == null) || ("".equals(str)))
    {
      log.severe("SAAJ0131.impl.header.elems.ns.qualified");
      throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
    }
    addNode((Node)localObject);
    return (SOAPHeaderElement)localObject;
  }
  
  public SOAPHeaderElement addHeaderElement(QName paramQName)
    throws SOAPException
  {
    Object localObject = ElementFactory.createNamedElement(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName.getLocalPart(), paramQName.getPrefix(), paramQName.getNamespaceURI());
    if ((localObject == null) || (!(localObject instanceof SOAPHeaderElement))) {
      localObject = createHeaderElement(paramQName);
    }
    String str = ((SOAPElement)localObject).getElementQName().getNamespaceURI();
    if ((str == null) || ("".equals(str)))
    {
      log.severe("SAAJ0131.impl.header.elems.ns.qualified");
      throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
    }
    addNode((Node)localObject);
    return (SOAPHeaderElement)localObject;
  }
  
  protected SOAPElement addElement(Name paramName)
    throws SOAPException
  {
    return addHeaderElement(paramName);
  }
  
  protected SOAPElement addElement(QName paramQName)
    throws SOAPException
  {
    return addHeaderElement(paramQName);
  }
  
  public Iterator examineHeaderElements(String paramString)
  {
    return getHeaderElementsForActor(paramString, false, false);
  }
  
  public Iterator extractHeaderElements(String paramString)
  {
    return getHeaderElementsForActor(paramString, true, false);
  }
  
  protected Iterator getHeaderElementsForActor(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramString == null) || (paramString.equals("")))
    {
      log.severe("SAAJ0132.impl.invalid.value.for.actor.or.role");
      throw new IllegalArgumentException("Invalid value for actor or role");
    }
    return getHeaderElements(paramString, paramBoolean1, paramBoolean2);
  }
  
  protected Iterator getHeaderElements(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = getChildElements();
    Object localObject = iterate(localIterator);
    while (localObject != null) {
      if (!(localObject instanceof SOAPHeaderElement))
      {
        localObject = iterate(localIterator);
      }
      else
      {
        HeaderElementImpl localHeaderElementImpl = (HeaderElementImpl)localObject;
        localObject = iterate(localIterator);
        int i = (!paramBoolean2) || (localHeaderElementImpl.getMustUnderstand()) ? 1 : 0;
        int j = 0;
        if ((paramString == null) && (i != 0))
        {
          j = 1;
        }
        else
        {
          String str = localHeaderElementImpl.getActorOrRole();
          if (str == null) {
            str = "";
          }
          if ((str.equalsIgnoreCase(paramString)) && (i != 0)) {
            j = 1;
          }
        }
        if (j != 0)
        {
          localArrayList.add(localHeaderElementImpl);
          if (paramBoolean1) {
            localHeaderElementImpl.detachNode();
          }
        }
      }
    }
    return localArrayList.listIterator();
  }
  
  private Object iterate(Iterator paramIterator)
  {
    return paramIterator.hasNext() ? paramIterator.next() : null;
  }
  
  public void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    if (!(paramSOAPElement instanceof SOAPEnvelope))
    {
      log.severe("SAAJ0133.impl.header.parent.mustbe.envelope");
      throw new SOAPException("Parent of SOAPHeader has to be a SOAPEnvelope");
    }
    super.setParentElement(paramSOAPElement);
  }
  
  public SOAPElement addChildElement(String paramString)
    throws SOAPException
  {
    SOAPElement localSOAPElement = super.addChildElement(paramString);
    String str = localSOAPElement.getElementName().getURI();
    if ((str == null) || ("".equals(str)))
    {
      log.severe("SAAJ0134.impl.header.elems.ns.qualified");
      throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
    }
    return localSOAPElement;
  }
  
  public Iterator examineAllHeaderElements()
  {
    return getHeaderElements(null, false, false);
  }
  
  public Iterator examineMustUnderstandHeaderElements(String paramString)
  {
    return getHeaderElements(paramString, false, true);
  }
  
  public Iterator extractAllHeaderElements()
  {
    return getHeaderElements(null, true, false);
  }
  
  public SOAPHeaderElement addUpgradeHeaderElement(Iterator paramIterator)
    throws SOAPException
  {
    if (paramIterator == null)
    {
      log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
      throw new SOAPException("Argument cannot be null; iterator of supportedURIs cannot be null");
    }
    if (!paramIterator.hasNext())
    {
      log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
      throw new SOAPException("List of supported URIs cannot be empty");
    }
    NameImpl localNameImpl1 = getUpgradeName();
    SOAPHeaderElement localSOAPHeaderElement = (SOAPHeaderElement)addChildElement(localNameImpl1);
    NameImpl localNameImpl2 = getSupportedEnvelopeName();
    for (int i = 0; paramIterator.hasNext(); i++)
    {
      SOAPElement localSOAPElement = localSOAPHeaderElement.addChildElement(localNameImpl2);
      String str = "ns" + Integer.toString(i);
      localSOAPElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), str + ":Envelope");
      localSOAPElement.addNamespaceDeclaration(str, (String)paramIterator.next());
    }
    return localSOAPHeaderElement;
  }
  
  public SOAPHeaderElement addUpgradeHeaderElement(String paramString)
    throws SOAPException
  {
    return addUpgradeHeaderElement(new String[] { paramString });
  }
  
  public SOAPHeaderElement addUpgradeHeaderElement(String[] paramArrayOfString)
    throws SOAPException
  {
    if (paramArrayOfString == null)
    {
      log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
      throw new SOAPException("Argument cannot be null; array of supportedURIs cannot be null");
    }
    if (paramArrayOfString.length == 0)
    {
      log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
      throw new SOAPException("List of supported URIs cannot be empty");
    }
    NameImpl localNameImpl1 = getUpgradeName();
    SOAPHeaderElement localSOAPHeaderElement = (SOAPHeaderElement)addChildElement(localNameImpl1);
    NameImpl localNameImpl2 = getSupportedEnvelopeName();
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      SOAPElement localSOAPElement = localSOAPHeaderElement.addChildElement(localNameImpl2);
      String str = "ns" + Integer.toString(i);
      localSOAPElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), str + ":Envelope");
      localSOAPElement.addNamespaceDeclaration(str, paramArrayOfString[i]);
    }
    return localSOAPHeaderElement;
  }
  
  protected SOAPElement convertToSoapElement(Element paramElement)
  {
    if ((paramElement instanceof SOAPHeaderElement)) {
      return (SOAPElement)paramElement;
    }
    SOAPHeaderElement localSOAPHeaderElement;
    try
    {
      localSOAPHeaderElement = createHeaderElement(NameImpl.copyElementName(paramElement));
    }
    catch (SOAPException localSOAPException)
    {
      throw new ClassCastException("Could not convert Element to SOAPHeaderElement: " + localSOAPException.getMessage());
    }
    return replaceElementWithSOAPElement(paramElement, (ElementImpl)localSOAPHeaderElement);
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), paramQName.getLocalPart() });
    throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + paramQName.getLocalPart());
  }
}
