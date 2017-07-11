package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Node;

public class TextImpl
  extends com.sun.org.apache.xerces.internal.dom.TextImpl
  implements javax.xml.soap.Text, org.w3c.dom.Text
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
  
  public TextImpl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString)
  {
    super(paramSOAPDocumentImpl, paramString);
  }
  
  public String getValue()
  {
    String str = getNodeValue();
    return str.equals("") ? null : str;
  }
  
  public void setValue(String paramString)
  {
    setNodeValue(paramString);
  }
  
  public void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    if (paramSOAPElement == null)
    {
      log.severe("SAAJ0126.impl.cannot.locate.ns");
      throw new SOAPException("Cannot pass NULL to setParentElement");
    }
    ((ElementImpl)paramSOAPElement).addNode(this);
  }
  
  public SOAPElement getParentElement()
  {
    return (SOAPElement)getParentNode();
  }
  
  public void detachNode()
  {
    Node localNode = getParentNode();
    if (localNode != null) {
      localNode.removeChild(this);
    }
  }
  
  public void recycleNode()
  {
    detachNode();
  }
  
  public boolean isComment()
  {
    String str = getNodeValue();
    if (str == null) {
      return false;
    }
    return (str.startsWith("<!--")) && (str.endsWith("-->"));
  }
}
