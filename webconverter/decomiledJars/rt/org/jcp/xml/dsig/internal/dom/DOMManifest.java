package org.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMManifest
  extends DOMStructure
  implements Manifest
{
  private final List<Reference> references;
  private final String id;
  
  public DOMManifest(List<? extends Reference> paramList, String paramString)
  {
    if (paramList == null) {
      throw new NullPointerException("references cannot be null");
    }
    this.references = Collections.unmodifiableList(new ArrayList(paramList));
    if (this.references.isEmpty()) {
      throw new IllegalArgumentException("list of references must contain at least one entry");
    }
    int i = 0;
    int j = this.references.size();
    while (i < j)
    {
      if (!(this.references.get(i) instanceof Reference)) {
        throw new ClassCastException("references[" + i + "] is not a valid type");
      }
      i++;
    }
    this.id = paramString;
  }
  
  public DOMManifest(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
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
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    Element localElement = DOMUtils.getFirstChildElement(paramElement, "Reference");
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new DOMReference(localElement, paramXMLCryptoContext, paramProvider));
    for (localElement = DOMUtils.getNextSiblingElement(localElement); localElement != null; localElement = DOMUtils.getNextSiblingElement(localElement))
    {
      String str1 = localElement.getLocalName();
      if (!str1.equals("Reference")) {
        throw new MarshalException("Invalid element name: " + str1 + ", expected Reference");
      }
      localArrayList.add(new DOMReference(localElement, paramXMLCryptoContext, paramProvider));
      if ((bool) && (localArrayList.size() > 30))
      {
        String str2 = "A maxiumum of 30 references per Manifest are allowed with secure validation";
        throw new MarshalException(str2);
      }
    }
    this.references = Collections.unmodifiableList(localArrayList);
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public List getReferences()
  {
    return this.references;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "Manifest", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(localElement, "Id", this.id);
    Iterator localIterator = this.references.iterator();
    while (localIterator.hasNext())
    {
      Reference localReference = (Reference)localIterator.next();
      ((DOMReference)localReference).marshal(localElement, paramString, paramDOMCryptoContext);
    }
    paramNode.appendChild(localElement);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Manifest)) {
      return false;
    }
    Manifest localManifest = (Manifest)paramObject;
    boolean bool = this.id == null ? false : localManifest.getId() == null ? true : this.id.equals(localManifest.getId());
    return (bool) && (this.references.equals(localManifest.getReferences()));
  }
  
  public int hashCode()
  {
    int i = 17;
    if (this.id != null) {
      i = 31 * i + this.id.hashCode();
    }
    i = 31 * i + this.references.hashCode();
    return i;
  }
}
