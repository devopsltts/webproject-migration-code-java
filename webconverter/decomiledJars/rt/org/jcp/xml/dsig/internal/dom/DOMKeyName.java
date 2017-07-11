package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMKeyName
  extends DOMStructure
  implements KeyName
{
  private final String name;
  
  public DOMKeyName(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("name cannot be null");
    }
    this.name = paramString;
  }
  
  public DOMKeyName(Element paramElement)
  {
    this.name = paramElement.getFirstChild().getNodeValue();
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "KeyName", "http://www.w3.org/2000/09/xmldsig#", paramString);
    localElement.appendChild(localDocument.createTextNode(this.name));
    paramNode.appendChild(localElement);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof KeyName)) {
      return false;
    }
    KeyName localKeyName = (KeyName)paramObject;
    return this.name.equals(localKeyName.getName());
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + this.name.hashCode();
    return i;
  }
}
