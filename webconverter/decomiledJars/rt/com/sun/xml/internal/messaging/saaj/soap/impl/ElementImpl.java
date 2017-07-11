package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.util.NamespaceContextIterator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;

public class ElementImpl
  extends ElementNSImpl
  implements SOAPElement, SOAPBodyElement
{
  public static final String DSIG_NS = "http://www.w3.org/2000/09/xmldsig#".intern();
  public static final String XENC_NS = "http://www.w3.org/2001/04/xmlenc#".intern();
  public static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".intern();
  private AttributeManager encodingStyleAttribute = new AttributeManager();
  protected QName elementQName;
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
  public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
  public static final String XML_URI = "http://www.w3.org/XML/1998/namespace".intern();
  
  public ElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName.getURI(), paramName.getQualifiedName(), paramName.getLocalName());
    this.elementQName = NameImpl.convertToQName(paramName);
  }
  
  public ElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName.getNamespaceURI(), getQualifiedName(paramQName), paramQName.getLocalPart());
    this.elementQName = paramQName;
  }
  
  public ElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString1, String paramString2)
  {
    super(paramSOAPDocumentImpl, paramString1, paramString2);
    this.elementQName = new QName(paramString1, getLocalPart(paramString2), getPrefix(paramString2));
  }
  
  public void ensureNamespaceIsDeclared(String paramString1, String paramString2)
  {
    String str = getNamespaceURI(paramString1);
    if ((str == null) || (!str.equals(paramString2))) {
      try
      {
        addNamespaceDeclaration(paramString1, paramString2);
      }
      catch (SOAPException localSOAPException) {}
    }
  }
  
  public Document getOwnerDocument()
  {
    Document localDocument = super.getOwnerDocument();
    if ((localDocument instanceof SOAPDocument)) {
      return ((SOAPDocument)localDocument).getDocument();
    }
    return localDocument;
  }
  
  public SOAPElement addChildElement(Name paramName)
    throws SOAPException
  {
    return addElement(paramName);
  }
  
  public SOAPElement addChildElement(QName paramQName)
    throws SOAPException
  {
    return addElement(paramQName);
  }
  
  public SOAPElement addChildElement(String paramString)
    throws SOAPException
  {
    return addChildElement(NameImpl.createFromUnqualifiedName(paramString));
  }
  
  public SOAPElement addChildElement(String paramString1, String paramString2)
    throws SOAPException
  {
    String str = getNamespaceURI(paramString2);
    if (str == null)
    {
      log.log(Level.SEVERE, "SAAJ0101.impl.parent.of.body.elem.mustbe.body", new String[] { paramString2 });
      throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + paramString2);
    }
    return addChildElement(paramString1, paramString2, str);
  }
  
  public String getNamespaceURI(String paramString)
  {
    if ("xmlns".equals(paramString)) {
      return XMLNS_URI;
    }
    if ("xml".equals(paramString)) {
      return XML_URI;
    }
    Object localObject;
    if ("".equals(paramString)) {
      for (localObject = this; (localObject != null) && (!(localObject instanceof Document)); localObject = ((org.w3c.dom.Node)localObject).getParentNode()) {
        if ((localObject instanceof ElementImpl))
        {
          QName localQName = ((ElementImpl)localObject).getElementQName();
          if (((Element)localObject).hasAttributeNS(XMLNS_URI, "xmlns"))
          {
            String str = ((Element)localObject).getAttributeNS(XMLNS_URI, "xmlns");
            if ("".equals(str)) {
              return null;
            }
            return str;
          }
        }
      }
    } else if (paramString != null) {
      for (localObject = this; (localObject != null) && (!(localObject instanceof Document)); localObject = ((org.w3c.dom.Node)localObject).getParentNode()) {
        if (((Element)localObject).hasAttributeNS(XMLNS_URI, paramString)) {
          return ((Element)localObject).getAttributeNS(XMLNS_URI, paramString);
        }
      }
    }
    return null;
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    ElementImpl localElementImpl = new ElementImpl((SOAPDocumentImpl)getOwnerDocument(), paramQName);
    return replaceElementWithSOAPElement(this, localElementImpl);
  }
  
  public QName createQName(String paramString1, String paramString2)
    throws SOAPException
  {
    String str = getNamespaceURI(paramString2);
    if (str == null)
    {
      log.log(Level.SEVERE, "SAAJ0102.impl.cannot.locate.ns", new Object[] { paramString2 });
      throw new SOAPException("Unable to locate namespace for prefix " + paramString2);
    }
    return new QName(str, paramString1, paramString2);
  }
  
  public String getNamespacePrefix(String paramString)
  {
    NamespaceContextIterator localNamespaceContextIterator = getNamespaceContextNodes();
    while (localNamespaceContextIterator.hasNext())
    {
      localObject = localNamespaceContextIterator.nextNamespaceAttr();
      if (((Attr)localObject).getNodeValue().equals(paramString))
      {
        String str = ((Attr)localObject).getLocalName();
        if ("xmlns".equals(str)) {
          return "";
        }
        return str;
      }
    }
    for (Object localObject = this; (localObject != null) && (!(localObject instanceof Document)); localObject = ((org.w3c.dom.Node)localObject).getParentNode()) {
      if (paramString.equals(((org.w3c.dom.Node)localObject).getNamespaceURI())) {
        return ((org.w3c.dom.Node)localObject).getPrefix();
      }
    }
    return null;
  }
  
  protected Attr getNamespaceAttr(String paramString)
  {
    NamespaceContextIterator localNamespaceContextIterator = getNamespaceContextNodes();
    if (!"".equals(paramString)) {
      paramString = ":" + paramString;
    }
    while (localNamespaceContextIterator.hasNext())
    {
      Attr localAttr = localNamespaceContextIterator.nextNamespaceAttr();
      if (!"".equals(paramString))
      {
        if (localAttr.getNodeName().endsWith(paramString)) {
          return localAttr;
        }
      }
      else if (localAttr.getNodeName().equals("xmlns")) {
        return localAttr;
      }
    }
    return null;
  }
  
  public NamespaceContextIterator getNamespaceContextNodes()
  {
    return getNamespaceContextNodes(true);
  }
  
  public NamespaceContextIterator getNamespaceContextNodes(boolean paramBoolean)
  {
    return new NamespaceContextIterator(this, paramBoolean);
  }
  
  public SOAPElement addChildElement(String paramString1, String paramString2, String paramString3)
    throws SOAPException
  {
    SOAPElement localSOAPElement = createElement(NameImpl.create(paramString1, paramString2, paramString3));
    addNode(localSOAPElement);
    return convertToSoapElement(localSOAPElement);
  }
  
  public SOAPElement addChildElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    String str1 = paramSOAPElement.getElementName().getURI();
    String str2 = paramSOAPElement.getLocalName();
    if (("http://schemas.xmlsoap.org/soap/envelope/".equals(str1)) || ("http://www.w3.org/2003/05/soap-envelope".equals(str1)))
    {
      if (("Envelope".equalsIgnoreCase(str2)) || ("Header".equalsIgnoreCase(str2)) || ("Body".equalsIgnoreCase(str2)))
      {
        log.severe("SAAJ0103.impl.cannot.add.fragements");
        throw new SOAPExceptionImpl("Cannot add fragments which contain elements which are in the SOAP namespace");
      }
      if (("Fault".equalsIgnoreCase(str2)) && (!"Body".equalsIgnoreCase(getLocalName())))
      {
        log.severe("SAAJ0154.impl.adding.fault.to.nonbody");
        throw new SOAPExceptionImpl("Cannot add a SOAPFault as a child of " + getLocalName());
      }
      if (("Detail".equalsIgnoreCase(str2)) && (!"Fault".equalsIgnoreCase(getLocalName())))
      {
        log.severe("SAAJ0155.impl.adding.detail.nonfault");
        throw new SOAPExceptionImpl("Cannot add a Detail as a child of " + getLocalName());
      }
      if ("Fault".equalsIgnoreCase(str2))
      {
        if (!str1.equals(getElementName().getURI()))
        {
          log.severe("SAAJ0158.impl.version.mismatch.fault");
          throw new SOAPExceptionImpl("SOAP Version mismatch encountered when trying to add SOAPFault to SOAPBody");
        }
        localObject = getChildElements();
        if (((Iterator)localObject).hasNext())
        {
          log.severe("SAAJ0156.impl.adding.fault.error");
          throw new SOAPExceptionImpl("Cannot add SOAPFault as a child of a non-Empty SOAPBody");
        }
      }
    }
    Object localObject = paramSOAPElement.getEncodingStyle();
    ElementImpl localElementImpl = (ElementImpl)importElement(paramSOAPElement);
    addNode(localElementImpl);
    if (localObject != null) {
      localElementImpl.setEncodingStyle((String)localObject);
    }
    return convertToSoapElement(localElementImpl);
  }
  
  protected Element importElement(Element paramElement)
  {
    Document localDocument1 = getOwnerDocument();
    Document localDocument2 = paramElement.getOwnerDocument();
    if (!localDocument2.equals(localDocument1)) {
      return (Element)localDocument1.importNode(paramElement, true);
    }
    return paramElement;
  }
  
  protected SOAPElement addElement(Name paramName)
    throws SOAPException
  {
    SOAPElement localSOAPElement = createElement(paramName);
    addNode(localSOAPElement);
    return circumventBug5034339(localSOAPElement);
  }
  
  protected SOAPElement addElement(QName paramQName)
    throws SOAPException
  {
    SOAPElement localSOAPElement = createElement(paramQName);
    addNode(localSOAPElement);
    return circumventBug5034339(localSOAPElement);
  }
  
  protected SOAPElement createElement(Name paramName)
  {
    if (isNamespaceQualified(paramName)) {
      return (SOAPElement)getOwnerDocument().createElementNS(paramName.getURI(), paramName.getQualifiedName());
    }
    return (SOAPElement)getOwnerDocument().createElement(paramName.getQualifiedName());
  }
  
  protected SOAPElement createElement(QName paramQName)
  {
    if (isNamespaceQualified(paramQName)) {
      return (SOAPElement)getOwnerDocument().createElementNS(paramQName.getNamespaceURI(), getQualifiedName(paramQName));
    }
    return (SOAPElement)getOwnerDocument().createElement(getQualifiedName(paramQName));
  }
  
  protected void addNode(org.w3c.dom.Node paramNode)
    throws SOAPException
  {
    insertBefore(paramNode, null);
    if ((getOwnerDocument() instanceof DocumentFragment)) {
      return;
    }
    if ((paramNode instanceof ElementImpl))
    {
      ElementImpl localElementImpl = (ElementImpl)paramNode;
      QName localQName = localElementImpl.getElementQName();
      if (!"".equals(localQName.getNamespaceURI())) {
        localElementImpl.ensureNamespaceIsDeclared(localQName.getPrefix(), localQName.getNamespaceURI());
      }
    }
  }
  
  protected SOAPElement findChild(NameImpl paramNameImpl)
  {
    Iterator localIterator = getChildElementNodes();
    while (localIterator.hasNext())
    {
      SOAPElement localSOAPElement = (SOAPElement)localIterator.next();
      if (localSOAPElement.getElementName().equals(paramNameImpl)) {
        return localSOAPElement;
      }
    }
    return null;
  }
  
  public SOAPElement addTextNode(String paramString)
    throws SOAPException
  {
    if ((paramString.startsWith("<![CDATA[")) || (paramString.startsWith("<![cdata["))) {
      return addCDATA(paramString.substring("<![CDATA[".length(), paramString.length() - 3));
    }
    return addText(paramString);
  }
  
  protected SOAPElement addCDATA(String paramString)
    throws SOAPException
  {
    CDATASection localCDATASection = getOwnerDocument().createCDATASection(paramString);
    addNode(localCDATASection);
    return this;
  }
  
  protected SOAPElement addText(String paramString)
    throws SOAPException
  {
    Text localText = getOwnerDocument().createTextNode(paramString);
    addNode(localText);
    return this;
  }
  
  public SOAPElement addAttribute(Name paramName, String paramString)
    throws SOAPException
  {
    addAttributeBare(paramName, paramString);
    if (!"".equals(paramName.getURI())) {
      ensureNamespaceIsDeclared(paramName.getPrefix(), paramName.getURI());
    }
    return this;
  }
  
  public SOAPElement addAttribute(QName paramQName, String paramString)
    throws SOAPException
  {
    addAttributeBare(paramQName, paramString);
    if (!"".equals(paramQName.getNamespaceURI())) {
      ensureNamespaceIsDeclared(paramQName.getPrefix(), paramQName.getNamespaceURI());
    }
    return this;
  }
  
  private void addAttributeBare(Name paramName, String paramString)
  {
    addAttributeBare(paramName.getURI(), paramName.getPrefix(), paramName.getQualifiedName(), paramString);
  }
  
  private void addAttributeBare(QName paramQName, String paramString)
  {
    addAttributeBare(paramQName.getNamespaceURI(), paramQName.getPrefix(), getQualifiedName(paramQName), paramString);
  }
  
  private void addAttributeBare(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    paramString1 = paramString1.length() == 0 ? null : paramString1;
    if (paramString3.equals("xmlns")) {
      paramString1 = XMLNS_URI;
    }
    if (paramString1 == null) {
      setAttribute(paramString3, paramString4);
    } else {
      setAttributeNS(paramString1, paramString3, paramString4);
    }
  }
  
  public SOAPElement addNamespaceDeclaration(String paramString1, String paramString2)
    throws SOAPException
  {
    if (paramString1.length() > 0) {
      setAttributeNS(XMLNS_URI, "xmlns:" + paramString1, paramString2);
    } else {
      setAttributeNS(XMLNS_URI, "xmlns", paramString2);
    }
    return this;
  }
  
  public String getAttributeValue(Name paramName)
  {
    return getAttributeValueFrom(this, paramName);
  }
  
  public String getAttributeValue(QName paramQName)
  {
    return getAttributeValueFrom(this, paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramQName.getPrefix(), getQualifiedName(paramQName));
  }
  
  public Iterator getAllAttributes()
  {
    Iterator localIterator = getAllAttributesFrom(this);
    ArrayList localArrayList = new ArrayList();
    while (localIterator.hasNext())
    {
      Name localName = (Name)localIterator.next();
      if (!"xmlns".equalsIgnoreCase(localName.getPrefix())) {
        localArrayList.add(localName);
      }
    }
    return localArrayList.iterator();
  }
  
  public Iterator getAllAttributesAsQNames()
  {
    Iterator localIterator = getAllAttributesFrom(this);
    ArrayList localArrayList = new ArrayList();
    while (localIterator.hasNext())
    {
      Name localName = (Name)localIterator.next();
      if (!"xmlns".equalsIgnoreCase(localName.getPrefix())) {
        localArrayList.add(NameImpl.convertToQName(localName));
      }
    }
    return localArrayList.iterator();
  }
  
  public Iterator getNamespacePrefixes()
  {
    return doGetNamespacePrefixes(false);
  }
  
  public Iterator getVisibleNamespacePrefixes()
  {
    return doGetNamespacePrefixes(true);
  }
  
  protected Iterator doGetNamespacePrefixes(final boolean paramBoolean)
  {
    new Iterator()
    {
      String next = null;
      String last = null;
      NamespaceContextIterator eachNamespace = ElementImpl.this.getNamespaceContextNodes(paramBoolean);
      
      void findNext()
      {
        while ((this.next == null) && (this.eachNamespace.hasNext()))
        {
          String str = this.eachNamespace.nextNamespaceAttr().getNodeName();
          if (str.startsWith("xmlns:")) {
            this.next = str.substring("xmlns:".length());
          }
        }
      }
      
      public boolean hasNext()
      {
        findNext();
        return this.next != null;
      }
      
      public Object next()
      {
        findNext();
        if (this.next == null) {
          throw new NoSuchElementException();
        }
        this.last = this.next;
        this.next = null;
        return this.last;
      }
      
      public void remove()
      {
        if (this.last == null) {
          throw new IllegalStateException();
        }
        this.eachNamespace.remove();
        this.next = null;
        this.last = null;
      }
    };
  }
  
  public Name getElementName()
  {
    return NameImpl.convertToName(this.elementQName);
  }
  
  public QName getElementQName()
  {
    return this.elementQName;
  }
  
  public boolean removeAttribute(Name paramName)
  {
    return removeAttribute(paramName.getURI(), paramName.getLocalName());
  }
  
  public boolean removeAttribute(QName paramQName)
  {
    return removeAttribute(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  private boolean removeAttribute(String paramString1, String paramString2)
  {
    String str = (paramString1 == null) || (paramString1.length() == 0) ? null : paramString1;
    Attr localAttr = getAttributeNodeNS(str, paramString2);
    if (localAttr == null) {
      return false;
    }
    removeAttributeNode(localAttr);
    return true;
  }
  
  public boolean removeNamespaceDeclaration(String paramString)
  {
    Attr localAttr = getNamespaceAttr(paramString);
    if (localAttr == null) {
      return false;
    }
    try
    {
      removeAttributeNode(localAttr);
    }
    catch (DOMException localDOMException) {}
    return true;
  }
  
  public Iterator getChildElements()
  {
    return getChildElementsFrom(this);
  }
  
  protected SOAPElement convertToSoapElement(Element paramElement)
  {
    if ((paramElement instanceof SOAPElement)) {
      return (SOAPElement)paramElement;
    }
    return replaceElementWithSOAPElement(paramElement, (ElementImpl)createElement(NameImpl.copyElementName(paramElement)));
  }
  
  protected static SOAPElement replaceElementWithSOAPElement(Element paramElement, ElementImpl paramElementImpl)
  {
    Iterator localIterator = getAllAttributesFrom(paramElement);
    while (localIterator.hasNext())
    {
      localObject = (Name)localIterator.next();
      paramElementImpl.addAttributeBare((Name)localObject, getAttributeValueFrom(paramElement, (Name)localObject));
    }
    Object localObject = getChildElementsFrom(paramElement);
    while (((Iterator)localObject).hasNext())
    {
      localNode = (org.w3c.dom.Node)((Iterator)localObject).next();
      paramElementImpl.insertBefore(localNode, null);
    }
    org.w3c.dom.Node localNode = paramElement.getParentNode();
    if (localNode != null) {
      localNode.replaceChild(paramElementImpl, paramElement);
    }
    return paramElementImpl;
  }
  
  protected Iterator getChildElementNodes()
  {
    new Iterator()
    {
      Iterator eachNode = ElementImpl.this.getChildElements();
      org.w3c.dom.Node next = null;
      org.w3c.dom.Node last = null;
      
      public boolean hasNext()
      {
        if (this.next == null) {
          while (this.eachNode.hasNext())
          {
            org.w3c.dom.Node localNode = (org.w3c.dom.Node)this.eachNode.next();
            if ((localNode instanceof SOAPElement))
            {
              this.next = localNode;
              break;
            }
          }
        }
        return this.next != null;
      }
      
      public Object next()
      {
        if (hasNext())
        {
          this.last = this.next;
          this.next = null;
          return this.last;
        }
        throw new NoSuchElementException();
      }
      
      public void remove()
      {
        if (this.last == null) {
          throw new IllegalStateException();
        }
        org.w3c.dom.Node localNode = this.last;
        this.last = null;
        ElementImpl.this.removeChild(localNode);
      }
    };
  }
  
  public Iterator getChildElements(Name paramName)
  {
    return getChildElements(paramName.getURI(), paramName.getLocalName());
  }
  
  public Iterator getChildElements(QName paramQName)
  {
    return getChildElements(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  private Iterator getChildElements(final String paramString1, final String paramString2)
  {
    new Iterator()
    {
      Iterator eachElement = ElementImpl.this.getChildElementNodes();
      org.w3c.dom.Node next = null;
      org.w3c.dom.Node last = null;
      
      public boolean hasNext()
      {
        if (this.next == null) {
          while (this.eachElement.hasNext())
          {
            org.w3c.dom.Node localNode = (org.w3c.dom.Node)this.eachElement.next();
            String str1 = localNode.getNamespaceURI();
            str1 = str1 == null ? "" : str1;
            String str2 = localNode.getLocalName();
            if ((str1.equals(paramString1)) && (str2.equals(paramString2)))
            {
              this.next = localNode;
              break;
            }
          }
        }
        return this.next != null;
      }
      
      public Object next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        this.last = this.next;
        this.next = null;
        return this.last;
      }
      
      public void remove()
      {
        if (this.last == null) {
          throw new IllegalStateException();
        }
        org.w3c.dom.Node localNode = this.last;
        this.last = null;
        ElementImpl.this.removeChild(localNode);
      }
    };
  }
  
  public void removeContents()
  {
    org.w3c.dom.Node localNode1;
    for (Object localObject = getFirstChild(); localObject != null; localObject = localNode1)
    {
      localNode1 = ((org.w3c.dom.Node)localObject).getNextSibling();
      if ((localObject instanceof javax.xml.soap.Node))
      {
        ((javax.xml.soap.Node)localObject).detachNode();
      }
      else
      {
        org.w3c.dom.Node localNode2 = ((org.w3c.dom.Node)localObject).getParentNode();
        if (localNode2 != null) {
          localNode2.removeChild((org.w3c.dom.Node)localObject);
        }
      }
    }
  }
  
  public void setEncodingStyle(String paramString)
    throws SOAPException
  {
    if (!"".equals(paramString)) {
      try
      {
        new URI(paramString);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        log.log(Level.SEVERE, "SAAJ0105.impl.encoding.style.mustbe.valid.URI", new String[] { paramString });
        throw new IllegalArgumentException("Encoding style (" + paramString + ") should be a valid URI");
      }
    }
    this.encodingStyleAttribute.setValue(paramString);
    tryToFindEncodingStyleAttributeName();
  }
  
  public String getEncodingStyle()
  {
    String str1 = this.encodingStyleAttribute.getValue();
    if (str1 != null) {
      return str1;
    }
    String str2 = getSOAPNamespace();
    if (str2 != null)
    {
      Attr localAttr = getAttributeNodeNS(str2, "encodingStyle");
      if (localAttr != null)
      {
        str1 = localAttr.getValue();
        try
        {
          setEncodingStyle(str1);
        }
        catch (SOAPException localSOAPException) {}
        return str1;
      }
    }
    return null;
  }
  
  public String getValue()
  {
    javax.xml.soap.Node localNode = getValueNode();
    return localNode == null ? null : localNode.getValue();
  }
  
  public void setValue(String paramString)
  {
    org.w3c.dom.Node localNode = getValueNodeStrict();
    if (localNode != null) {
      localNode.setNodeValue(paramString);
    } else {
      try
      {
        addTextNode(paramString);
      }
      catch (SOAPException localSOAPException)
      {
        throw new RuntimeException(localSOAPException.getMessage());
      }
    }
  }
  
  protected org.w3c.dom.Node getValueNodeStrict()
  {
    org.w3c.dom.Node localNode = getFirstChild();
    if (localNode != null)
    {
      if ((localNode.getNextSibling() == null) && (localNode.getNodeType() == 3)) {
        return localNode;
      }
      log.severe("SAAJ0107.impl.elem.child.not.single.text");
      throw new IllegalStateException();
    }
    return null;
  }
  
  protected javax.xml.soap.Node getValueNode()
  {
    Iterator localIterator = getChildElements();
    while (localIterator.hasNext())
    {
      javax.xml.soap.Node localNode = (javax.xml.soap.Node)localIterator.next();
      if ((localNode.getNodeType() == 3) || (localNode.getNodeType() == 4))
      {
        normalize();
        return localNode;
      }
    }
    return null;
  }
  
  public void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    if (paramSOAPElement == null)
    {
      log.severe("SAAJ0106.impl.no.null.to.parent.elem");
      throw new SOAPException("Cannot pass NULL to setParentElement");
    }
    paramSOAPElement.addChildElement(this);
    findEncodingStyleAttributeName();
  }
  
  protected void findEncodingStyleAttributeName()
    throws SOAPException
  {
    String str1 = getSOAPNamespace();
    if (str1 != null)
    {
      String str2 = getNamespacePrefix(str1);
      if (str2 != null) {
        setEncodingStyleNamespace(str1, str2);
      }
    }
  }
  
  protected void setEncodingStyleNamespace(String paramString1, String paramString2)
    throws SOAPException
  {
    NameImpl localNameImpl = NameImpl.create("encodingStyle", paramString2, paramString1);
    this.encodingStyleAttribute.setName(localNameImpl);
  }
  
  public SOAPElement getParentElement()
  {
    org.w3c.dom.Node localNode = getParentNode();
    if ((localNode instanceof SOAPDocument)) {
      return null;
    }
    return (SOAPElement)localNode;
  }
  
  protected String getSOAPNamespace()
  {
    Object localObject1 = null;
    for (Object localObject2 = this; localObject2 != null; localObject2 = ((SOAPElement)localObject2).getParentElement())
    {
      Name localName = ((SOAPElement)localObject2).getElementName();
      String str = localName.getURI();
      if (("http://schemas.xmlsoap.org/soap/envelope/".equals(str)) || ("http://www.w3.org/2003/05/soap-envelope".equals(str)))
      {
        localObject1 = str;
        break;
      }
    }
    return localObject1;
  }
  
  public void detachNode()
  {
    org.w3c.dom.Node localNode = getParentNode();
    if (localNode != null) {
      localNode.removeChild(this);
    }
    this.encodingStyleAttribute.clearNameAndValue();
  }
  
  public void tryToFindEncodingStyleAttributeName()
  {
    try
    {
      findEncodingStyleAttributeName();
    }
    catch (SOAPException localSOAPException) {}
  }
  
  public void recycleNode()
  {
    detachNode();
  }
  
  protected static Attr getNamespaceAttrFrom(Element paramElement, String paramString)
  {
    NamespaceContextIterator localNamespaceContextIterator = new NamespaceContextIterator(paramElement);
    while (localNamespaceContextIterator.hasNext())
    {
      Attr localAttr = localNamespaceContextIterator.nextNamespaceAttr();
      String str = NameImpl.getLocalNameFromTagName(localAttr.getNodeName());
      if (str.equals(paramString)) {
        return localAttr;
      }
    }
    return null;
  }
  
  protected static Iterator getAllAttributesFrom(Element paramElement)
  {
    NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
    new Iterator()
    {
      int attributesLength = this.val$attributes.getLength();
      int attributeIndex = 0;
      String currentName;
      
      public boolean hasNext()
      {
        return this.attributeIndex < this.attributesLength;
      }
      
      public Object next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        org.w3c.dom.Node localNode = this.val$attributes.item(this.attributeIndex++);
        this.currentName = localNode.getNodeName();
        String str = NameImpl.getPrefixFromTagName(this.currentName);
        if (str.length() == 0) {
          return NameImpl.createFromUnqualifiedName(this.currentName);
        }
        Name localName = NameImpl.createFromQualifiedName(this.currentName, localNode.getNamespaceURI());
        return localName;
      }
      
      public void remove()
      {
        if (this.currentName == null) {
          throw new IllegalStateException();
        }
        this.val$attributes.removeNamedItem(this.currentName);
      }
    };
  }
  
  protected static String getAttributeValueFrom(Element paramElement, Name paramName)
  {
    return getAttributeValueFrom(paramElement, paramName.getURI(), paramName.getLocalName(), paramName.getPrefix(), paramName.getQualifiedName());
  }
  
  private static String getAttributeValueFrom(Element paramElement, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    String str = (paramString1 == null) || (paramString1.length() == 0) ? null : paramString1;
    int i = str != null ? 1 : 0;
    if (i != 0)
    {
      if (!paramElement.hasAttributeNS(paramString1, paramString2)) {
        return null;
      }
      localObject = paramElement.getAttributeNS(str, paramString2);
      return localObject;
    }
    Object localObject = null;
    localObject = paramElement.getAttributeNode(paramString4);
    return localObject == null ? null : ((Attr)localObject).getValue();
  }
  
  protected static Iterator getChildElementsFrom(Element paramElement)
  {
    new Iterator()
    {
      org.w3c.dom.Node next = this.val$element.getFirstChild();
      org.w3c.dom.Node nextNext = null;
      org.w3c.dom.Node last = null;
      
      public boolean hasNext()
      {
        if (this.next != null) {
          return true;
        }
        if ((this.next == null) && (this.nextNext != null)) {
          this.next = this.nextNext;
        }
        return this.next != null;
      }
      
      public Object next()
      {
        if (hasNext())
        {
          this.last = this.next;
          this.next = null;
          if (((this.val$element instanceof ElementImpl)) && ((this.last instanceof Element))) {
            this.last = ((ElementImpl)this.val$element).convertToSoapElement((Element)this.last);
          }
          this.nextNext = this.last.getNextSibling();
          return this.last;
        }
        throw new NoSuchElementException();
      }
      
      public void remove()
      {
        if (this.last == null) {
          throw new IllegalStateException();
        }
        org.w3c.dom.Node localNode = this.last;
        this.last = null;
        this.val$element.removeChild(localNode);
      }
    };
  }
  
  public static String getQualifiedName(QName paramQName)
  {
    String str1 = paramQName.getPrefix();
    String str2 = paramQName.getLocalPart();
    String str3 = null;
    if ((str1 != null) && (str1.length() > 0)) {
      str3 = str1 + ":" + str2;
    } else {
      str3 = str2;
    }
    return str3;
  }
  
  public static String getLocalPart(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Cannot get local name for a \"null\" qualified name");
    }
    int i = paramString.indexOf(':');
    if (i < 0) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
  
  public static String getPrefix(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Cannot get prefix for a  \"null\" qualified name");
    }
    int i = paramString.indexOf(':');
    if (i < 0) {
      return "";
    }
    return paramString.substring(0, i);
  }
  
  protected boolean isNamespaceQualified(Name paramName)
  {
    return !"".equals(paramName.getURI());
  }
  
  protected boolean isNamespaceQualified(QName paramQName)
  {
    return !"".equals(paramQName.getNamespaceURI());
  }
  
  protected SOAPElement circumventBug5034339(SOAPElement paramSOAPElement)
  {
    Name localName = paramSOAPElement.getElementName();
    if (!isNamespaceQualified(localName))
    {
      String str1 = localName.getPrefix();
      String str2 = getNamespaceURI(str1);
      if (str2 != null)
      {
        NameImpl localNameImpl = NameImpl.create(localName.getLocalName(), localName.getPrefix(), str2);
        SOAPElement localSOAPElement = createElement(localNameImpl);
        replaceChild(localSOAPElement, paramSOAPElement);
        return localSOAPElement;
      }
    }
    return paramSOAPElement;
  }
  
  public void setAttributeNS(String paramString1, String paramString2, String paramString3)
  {
    int i = paramString2.indexOf(':');
    String str1;
    if (i < 0) {
      str1 = paramString2;
    } else {
      str1 = paramString2.substring(i + 1);
    }
    super.setAttributeNS(paramString1, paramString2, paramString3);
    String str2 = getNamespaceURI();
    int j = 0;
    if ((str2 != null) && ((str2.equals(DSIG_NS)) || (str2.equals(XENC_NS)))) {
      j = 1;
    }
    if (str1.equals("Id")) {
      if ((paramString1 == null) || (paramString1.equals(""))) {
        setIdAttribute(str1, true);
      } else if ((j != 0) || (WSU_NS.equals(paramString1))) {
        setIdAttributeNS(paramString1, str1, true);
      }
    }
  }
  
  class AttributeManager
  {
    Name attributeName = null;
    String attributeValue = null;
    
    AttributeManager() {}
    
    public void setName(Name paramName)
      throws SOAPException
    {
      clearAttribute();
      this.attributeName = paramName;
      reconcileAttribute();
    }
    
    public void clearName()
    {
      clearAttribute();
      this.attributeName = null;
    }
    
    public void setValue(String paramString)
      throws SOAPException
    {
      this.attributeValue = paramString;
      reconcileAttribute();
    }
    
    public Name getName()
    {
      return this.attributeName;
    }
    
    public String getValue()
    {
      return this.attributeValue;
    }
    
    public void clearNameAndValue()
    {
      this.attributeName = null;
      this.attributeValue = null;
    }
    
    private void reconcileAttribute()
      throws SOAPException
    {
      if (this.attributeName != null)
      {
        ElementImpl.this.removeAttribute(this.attributeName);
        if (this.attributeValue != null) {
          ElementImpl.this.addAttribute(this.attributeName, this.attributeValue);
        }
      }
    }
    
    private void clearAttribute()
    {
      if (this.attributeName != null) {
        ElementImpl.this.removeAttribute(this.attributeName);
      }
    }
  }
}
