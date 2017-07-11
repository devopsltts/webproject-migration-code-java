package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureProperties
  extends SignatureElementProxy
{
  public SignatureProperties(Document paramDocument)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public SignatureProperties(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
    Attr localAttr1 = paramElement.getAttributeNodeNS(null, "Id");
    if (localAttr1 != null) {
      paramElement.setIdAttributeNode(localAttr1, true);
    }
    int i = getLength();
    for (int j = 0; j < i; j++)
    {
      Element localElement = XMLUtils.selectDsNode(this.constructionElement, "SignatureProperty", j);
      Attr localAttr2 = localElement.getAttributeNodeNS(null, "Id");
      if (localAttr2 != null) {
        localElement.setIdAttributeNode(localAttr2, true);
      }
    }
  }
  
  public int getLength()
  {
    Element[] arrayOfElement = XMLUtils.selectDsNodes(this.constructionElement, "SignatureProperty");
    return arrayOfElement.length;
  }
  
  public SignatureProperty item(int paramInt)
    throws XMLSignatureException
  {
    try
    {
      Element localElement = XMLUtils.selectDsNode(this.constructionElement, "SignatureProperty", paramInt);
      if (localElement == null) {
        return null;
      }
      return new SignatureProperty(localElement, this.baseURI);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new XMLSignatureException("empty", localXMLSecurityException);
    }
  }
  
  public void setId(String paramString)
  {
    if (paramString != null)
    {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    }
  }
  
  public String getId()
  {
    return this.constructionElement.getAttributeNS(null, "Id");
  }
  
  public void addSignatureProperty(SignatureProperty paramSignatureProperty)
  {
    this.constructionElement.appendChild(paramSignatureProperty.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public String getBaseLocalName()
  {
    return "SignatureProperties";
  }
}
