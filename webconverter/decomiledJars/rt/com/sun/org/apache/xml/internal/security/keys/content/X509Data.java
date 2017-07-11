package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509CRL;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Digest;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class X509Data
  extends SignatureElementProxy
  implements KeyInfoContent
{
  private static Logger log = Logger.getLogger(X509Data.class.getName());
  
  public X509Data(Document paramDocument)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public X509Data(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
    Node localNode = this.constructionElement.getFirstChild();
    while (localNode != null) {
      if (localNode.getNodeType() != 1) {
        localNode = localNode.getNextSibling();
      } else {
        return;
      }
    }
    Object[] arrayOfObject = { "Elements", "X509Data" };
    throw new XMLSecurityException("xml.WrongContent", arrayOfObject);
  }
  
  public void addIssuerSerial(String paramString, BigInteger paramBigInteger)
  {
    add(new XMLX509IssuerSerial(this.doc, paramString, paramBigInteger));
  }
  
  public void addIssuerSerial(String paramString1, String paramString2)
  {
    add(new XMLX509IssuerSerial(this.doc, paramString1, paramString2));
  }
  
  public void addIssuerSerial(String paramString, int paramInt)
  {
    add(new XMLX509IssuerSerial(this.doc, paramString, paramInt));
  }
  
  public void add(XMLX509IssuerSerial paramXMLX509IssuerSerial)
  {
    this.constructionElement.appendChild(paramXMLX509IssuerSerial.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addSKI(byte[] paramArrayOfByte)
  {
    add(new XMLX509SKI(this.doc, paramArrayOfByte));
  }
  
  public void addSKI(X509Certificate paramX509Certificate)
    throws XMLSecurityException
  {
    add(new XMLX509SKI(this.doc, paramX509Certificate));
  }
  
  public void add(XMLX509SKI paramXMLX509SKI)
  {
    this.constructionElement.appendChild(paramXMLX509SKI.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addSubjectName(String paramString)
  {
    add(new XMLX509SubjectName(this.doc, paramString));
  }
  
  public void addSubjectName(X509Certificate paramX509Certificate)
  {
    add(new XMLX509SubjectName(this.doc, paramX509Certificate));
  }
  
  public void add(XMLX509SubjectName paramXMLX509SubjectName)
  {
    this.constructionElement.appendChild(paramXMLX509SubjectName.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addCertificate(X509Certificate paramX509Certificate)
    throws XMLSecurityException
  {
    add(new XMLX509Certificate(this.doc, paramX509Certificate));
  }
  
  public void addCertificate(byte[] paramArrayOfByte)
  {
    add(new XMLX509Certificate(this.doc, paramArrayOfByte));
  }
  
  public void add(XMLX509Certificate paramXMLX509Certificate)
  {
    this.constructionElement.appendChild(paramXMLX509Certificate.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addCRL(byte[] paramArrayOfByte)
  {
    add(new XMLX509CRL(this.doc, paramArrayOfByte));
  }
  
  public void add(XMLX509CRL paramXMLX509CRL)
  {
    this.constructionElement.appendChild(paramXMLX509CRL.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addDigest(X509Certificate paramX509Certificate, String paramString)
    throws XMLSecurityException
  {
    add(new XMLX509Digest(this.doc, paramX509Certificate, paramString));
  }
  
  public void addDigest(byte[] paramArrayOfByte, String paramString)
  {
    add(new XMLX509Digest(this.doc, paramArrayOfByte, paramString));
  }
  
  public void add(XMLX509Digest paramXMLX509Digest)
  {
    this.constructionElement.appendChild(paramXMLX509Digest.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void addUnknownElement(Element paramElement)
  {
    this.constructionElement.appendChild(paramElement);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public int lengthIssuerSerial()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "X509IssuerSerial");
  }
  
  public int lengthSKI()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "X509SKI");
  }
  
  public int lengthSubjectName()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "X509SubjectName");
  }
  
  public int lengthCertificate()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
  }
  
  public int lengthCRL()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "X509CRL");
  }
  
  public int lengthDigest()
  {
    return length("http://www.w3.org/2009/xmldsig11#", "X509Digest");
  }
  
  public int lengthUnknownElement()
  {
    int i = 0;
    for (Node localNode = this.constructionElement.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      if ((localNode.getNodeType() == 1) && (!localNode.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#"))) {
        i++;
      }
    }
    return i;
  }
  
  public XMLX509IssuerSerial itemIssuerSerial(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509IssuerSerial", paramInt);
    if (localElement != null) {
      return new XMLX509IssuerSerial(localElement, this.baseURI);
    }
    return null;
  }
  
  public XMLX509SKI itemSKI(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509SKI", paramInt);
    if (localElement != null) {
      return new XMLX509SKI(localElement, this.baseURI);
    }
    return null;
  }
  
  public XMLX509SubjectName itemSubjectName(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509SubjectName", paramInt);
    if (localElement != null) {
      return new XMLX509SubjectName(localElement, this.baseURI);
    }
    return null;
  }
  
  public XMLX509Certificate itemCertificate(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509Certificate", paramInt);
    if (localElement != null) {
      return new XMLX509Certificate(localElement, this.baseURI);
    }
    return null;
  }
  
  public XMLX509CRL itemCRL(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509CRL", paramInt);
    if (localElement != null) {
      return new XMLX509CRL(localElement, this.baseURI);
    }
    return null;
  }
  
  public XMLX509Digest itemDigest(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDs11Node(this.constructionElement.getFirstChild(), "X509Digest", paramInt);
    if (localElement != null) {
      return new XMLX509Digest(localElement, this.baseURI);
    }
    return null;
  }
  
  public Element itemUnknownElement(int paramInt)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "itemUnknownElement not implemented:" + paramInt);
    }
    return null;
  }
  
  public boolean containsIssuerSerial()
  {
    return lengthIssuerSerial() > 0;
  }
  
  public boolean containsSKI()
  {
    return lengthSKI() > 0;
  }
  
  public boolean containsSubjectName()
  {
    return lengthSubjectName() > 0;
  }
  
  public boolean containsCertificate()
  {
    return lengthCertificate() > 0;
  }
  
  public boolean containsDigest()
  {
    return lengthDigest() > 0;
  }
  
  public boolean containsCRL()
  {
    return lengthCRL() > 0;
  }
  
  public boolean containsUnknownElement()
  {
    return lengthUnknownElement() > 0;
  }
  
  public String getBaseLocalName()
  {
    return "X509Data";
  }
}
