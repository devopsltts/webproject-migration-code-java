package org.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMSignatureProperty
  extends DOMStructure
  implements SignatureProperty
{
  private final String id;
  private final String target;
  private final List<XMLStructure> content;
  
  public DOMSignatureProperty(List<? extends XMLStructure> paramList, String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new NullPointerException("target cannot be null");
    }
    if (paramList == null) {
      throw new NullPointerException("content cannot be null");
    }
    if (paramList.isEmpty()) {
      throw new IllegalArgumentException("content cannot be empty");
    }
    this.content = Collections.unmodifiableList(new ArrayList(paramList));
    int i = 0;
    int j = this.content.size();
    while (i < j)
    {
      if (!(this.content.get(i) instanceof XMLStructure)) {
        throw new ClassCastException("content[" + i + "] is not a valid type");
      }
      i++;
    }
    this.target = paramString1;
    this.id = paramString2;
  }
  
  public DOMSignatureProperty(Element paramElement, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException
  {
    this.target = DOMUtils.getAttributeValue(paramElement, "Target");
    if (this.target == null) {
      throw new MarshalException("target cannot be null");
    }
    Attr localAttr = paramElement.getAttributeNodeNS(null, "Id");
    if (localAttr != null)
    {
      this.id = localAttr.getValue();
      paramElement.setIdAttributeNode(localAttr, true);
    }
    else
    {
      this.id = null;
    }
    NodeList localNodeList = paramElement.getChildNodes();
    int i = localNodeList.getLength();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++) {
      localArrayList.add(new javax.xml.crypto.dom.DOMStructure(localNodeList.item(j)));
    }
    if (localArrayList.isEmpty()) {
      throw new MarshalException("content cannot be empty");
    }
    this.content = Collections.unmodifiableList(localArrayList);
  }
  
  public List getContent()
  {
    return this.content;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public String getTarget()
  {
    return this.target;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "SignatureProperty", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(localElement, "Id", this.id);
    DOMUtils.setAttribute(localElement, "Target", this.target);
    Iterator localIterator = this.content.iterator();
    while (localIterator.hasNext())
    {
      XMLStructure localXMLStructure = (XMLStructure)localIterator.next();
      DOMUtils.appendChild(localElement, ((javax.xml.crypto.dom.DOMStructure)localXMLStructure).getNode());
    }
    paramNode.appendChild(localElement);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SignatureProperty)) {
      return false;
    }
    SignatureProperty localSignatureProperty = (SignatureProperty)paramObject;
    boolean bool = this.id == null ? false : localSignatureProperty.getId() == null ? true : this.id.equals(localSignatureProperty.getId());
    List localList = localSignatureProperty.getContent();
    return (equalsContent(localList)) && (this.target.equals(localSignatureProperty.getTarget())) && (bool);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (this.id != null) {
      i = 31 * i + this.id.hashCode();
    }
    i = 31 * i + this.target.hashCode();
    i = 31 * i + this.content.hashCode();
    return i;
  }
  
  private boolean equalsContent(List<XMLStructure> paramList)
  {
    int i = paramList.size();
    if (this.content.size() != i) {
      return false;
    }
    for (int j = 0; j < i; j++)
    {
      XMLStructure localXMLStructure1 = (XMLStructure)paramList.get(j);
      XMLStructure localXMLStructure2 = (XMLStructure)this.content.get(j);
      if ((localXMLStructure1 instanceof javax.xml.crypto.dom.DOMStructure))
      {
        if (!(localXMLStructure2 instanceof javax.xml.crypto.dom.DOMStructure)) {
          return false;
        }
        Node localNode1 = ((javax.xml.crypto.dom.DOMStructure)localXMLStructure1).getNode();
        Node localNode2 = ((javax.xml.crypto.dom.DOMStructure)localXMLStructure2).getNode();
        if (!DOMUtils.nodesEqual(localNode2, localNode1)) {
          return false;
        }
      }
      else if (!localXMLStructure2.equals(localXMLStructure1))
      {
        return false;
      }
    }
    return true;
  }
}
