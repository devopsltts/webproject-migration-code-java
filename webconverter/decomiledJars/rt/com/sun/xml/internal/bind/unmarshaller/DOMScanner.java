package com.sun.xml.internal.bind.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import java.util.Enumeration;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

public class DOMScanner
  implements LocatorEx, InfosetScanner
{
  private Node currentNode = null;
  private final AttributesImpl atts = new AttributesImpl();
  private ContentHandler receiver = null;
  private Locator locator = this;
  
  public DOMScanner() {}
  
  public void setLocator(Locator paramLocator)
  {
    this.locator = paramLocator;
  }
  
  public void scan(Object paramObject)
    throws SAXException
  {
    if ((paramObject instanceof Document)) {
      scan((Document)paramObject);
    } else {
      scan((Element)paramObject);
    }
  }
  
  public void scan(Document paramDocument)
    throws SAXException
  {
    scan(paramDocument.getDocumentElement());
  }
  
  public void scan(Element paramElement)
    throws SAXException
  {
    setCurrentLocation(paramElement);
    this.receiver.setDocumentLocator(this.locator);
    this.receiver.startDocument();
    NamespaceSupport localNamespaceSupport = new NamespaceSupport();
    buildNamespaceSupport(localNamespaceSupport, paramElement.getParentNode());
    Enumeration localEnumeration = localNamespaceSupport.getPrefixes();
    String str;
    while (localEnumeration.hasMoreElements())
    {
      str = (String)localEnumeration.nextElement();
      this.receiver.startPrefixMapping(str, localNamespaceSupport.getURI(str));
    }
    visit(paramElement);
    localEnumeration = localNamespaceSupport.getPrefixes();
    while (localEnumeration.hasMoreElements())
    {
      str = (String)localEnumeration.nextElement();
      this.receiver.endPrefixMapping(str);
    }
    setCurrentLocation(paramElement);
    this.receiver.endDocument();
  }
  
  /**
   * @deprecated
   */
  public void parse(Element paramElement, ContentHandler paramContentHandler)
    throws SAXException
  {
    this.receiver = paramContentHandler;
    setCurrentLocation(paramElement);
    this.receiver.startDocument();
    this.receiver.setDocumentLocator(this.locator);
    visit(paramElement);
    setCurrentLocation(paramElement);
    this.receiver.endDocument();
  }
  
  /**
   * @deprecated
   */
  public void parseWithContext(Element paramElement, ContentHandler paramContentHandler)
    throws SAXException
  {
    setContentHandler(paramContentHandler);
    scan(paramElement);
  }
  
  private void buildNamespaceSupport(NamespaceSupport paramNamespaceSupport, Node paramNode)
  {
    if ((paramNode == null) || (paramNode.getNodeType() != 1)) {
      return;
    }
    buildNamespaceSupport(paramNamespaceSupport, paramNode.getParentNode());
    paramNamespaceSupport.pushContext();
    NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
    for (int i = 0; i < localNamedNodeMap.getLength(); i++)
    {
      Attr localAttr = (Attr)localNamedNodeMap.item(i);
      if ("xmlns".equals(localAttr.getPrefix())) {
        paramNamespaceSupport.declarePrefix(localAttr.getLocalName(), localAttr.getValue());
      } else if ("xmlns".equals(localAttr.getName())) {
        paramNamespaceSupport.declarePrefix("", localAttr.getValue());
      }
    }
  }
  
  public void visit(Element paramElement)
    throws SAXException
  {
    setCurrentLocation(paramElement);
    NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
    this.atts.clear();
    int i = localNamedNodeMap == null ? 0 : localNamedNodeMap.getLength();
    for (int j = i - 1; j >= 0; j--)
    {
      localObject1 = (Attr)localNamedNodeMap.item(j);
      str2 = ((Attr)localObject1).getName();
      if (str2.startsWith("xmlns"))
      {
        if (str2.length() == 5)
        {
          this.receiver.startPrefixMapping("", ((Attr)localObject1).getValue());
        }
        else
        {
          localObject2 = ((Attr)localObject1).getLocalName();
          if (localObject2 == null) {
            localObject2 = str2.substring(6);
          }
          this.receiver.startPrefixMapping((String)localObject2, ((Attr)localObject1).getValue());
        }
      }
      else
      {
        localObject2 = ((Attr)localObject1).getNamespaceURI();
        if (localObject2 == null) {
          localObject2 = "";
        }
        String str3 = ((Attr)localObject1).getLocalName();
        if (str3 == null) {
          str3 = ((Attr)localObject1).getName();
        }
        this.atts.addAttribute((String)localObject2, str3, ((Attr)localObject1).getName(), "CDATA", ((Attr)localObject1).getValue());
      }
    }
    String str1 = paramElement.getNamespaceURI();
    if (str1 == null) {
      str1 = "";
    }
    Object localObject1 = paramElement.getLocalName();
    String str2 = paramElement.getTagName();
    if (localObject1 == null) {
      localObject1 = str2;
    }
    this.receiver.startElement(str1, (String)localObject1, str2, this.atts);
    Object localObject2 = paramElement.getChildNodes();
    int k = ((NodeList)localObject2).getLength();
    for (int m = 0; m < k; m++) {
      visit(((NodeList)localObject2).item(m));
    }
    setCurrentLocation(paramElement);
    this.receiver.endElement(str1, (String)localObject1, str2);
    for (m = i - 1; m >= 0; m--)
    {
      Attr localAttr = (Attr)localNamedNodeMap.item(m);
      String str4 = localAttr.getName();
      if (str4.startsWith("xmlns")) {
        if (str4.length() == 5) {
          this.receiver.endPrefixMapping("");
        } else {
          this.receiver.endPrefixMapping(localAttr.getLocalName());
        }
      }
    }
  }
  
  private void visit(Node paramNode)
    throws SAXException
  {
    setCurrentLocation(paramNode);
    switch (paramNode.getNodeType())
    {
    case 3: 
    case 4: 
      String str = paramNode.getNodeValue();
      this.receiver.characters(str.toCharArray(), 0, str.length());
      break;
    case 1: 
      visit((Element)paramNode);
      break;
    case 5: 
      this.receiver.skippedEntity(paramNode.getNodeName());
      break;
    case 7: 
      ProcessingInstruction localProcessingInstruction = (ProcessingInstruction)paramNode;
      this.receiver.processingInstruction(localProcessingInstruction.getTarget(), localProcessingInstruction.getData());
    }
  }
  
  private void setCurrentLocation(Node paramNode)
  {
    this.currentNode = paramNode;
  }
  
  public Node getCurrentLocation()
  {
    return this.currentNode;
  }
  
  public Object getCurrentElement()
  {
    return this.currentNode;
  }
  
  public LocatorEx getLocator()
  {
    return this;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    this.receiver = paramContentHandler;
  }
  
  public ContentHandler getContentHandler()
  {
    return this.receiver;
  }
  
  public String getPublicId()
  {
    return null;
  }
  
  public String getSystemId()
  {
    return null;
  }
  
  public int getLineNumber()
  {
    return -1;
  }
  
  public int getColumnNumber()
  {
    return -1;
  }
  
  public ValidationEventLocator getLocation()
  {
    return new ValidationEventLocatorImpl(getCurrentLocation());
  }
}
