package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.ByteArrayInputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMX509Data
  extends DOMStructure
  implements X509Data
{
  private final List<Object> content;
  private CertificateFactory cf;
  
  public DOMX509Data(List<?> paramList)
  {
    if (paramList == null) {
      throw new NullPointerException("content cannot be null");
    }
    ArrayList localArrayList = new ArrayList(paramList);
    if (localArrayList.isEmpty()) {
      throw new IllegalArgumentException("content cannot be empty");
    }
    int i = 0;
    int j = localArrayList.size();
    while (i < j)
    {
      Object localObject = localArrayList.get(i);
      if ((localObject instanceof String)) {
        new X500Principal((String)localObject);
      } else if ((!(localObject instanceof byte[])) && (!(localObject instanceof X509Certificate)) && (!(localObject instanceof X509CRL)) && (!(localObject instanceof XMLStructure))) {
        throw new ClassCastException("content[" + i + "] is not a valid X509Data type");
      }
      i++;
    }
    this.content = Collections.unmodifiableList(localArrayList);
  }
  
  public DOMX509Data(Element paramElement)
    throws MarshalException
  {
    NodeList localNodeList = paramElement.getChildNodes();
    int i = localNodeList.getLength();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      Node localNode = localNodeList.item(j);
      if (localNode.getNodeType() == 1)
      {
        Element localElement = (Element)localNode;
        String str = localElement.getLocalName();
        if (str.equals("X509Certificate")) {
          localArrayList.add(unmarshalX509Certificate(localElement));
        } else if (str.equals("X509IssuerSerial")) {
          localArrayList.add(new DOMX509IssuerSerial(localElement));
        } else if (str.equals("X509SubjectName")) {
          localArrayList.add(localElement.getFirstChild().getNodeValue());
        } else if (str.equals("X509SKI")) {
          try
          {
            localArrayList.add(Base64.decode(localElement));
          }
          catch (Base64DecodingException localBase64DecodingException)
          {
            throw new MarshalException("cannot decode X509SKI", localBase64DecodingException);
          }
        } else if (str.equals("X509CRL")) {
          localArrayList.add(unmarshalX509CRL(localElement));
        } else {
          localArrayList.add(new javax.xml.crypto.dom.DOMStructure(localElement));
        }
      }
    }
    this.content = Collections.unmodifiableList(localArrayList);
  }
  
  public List getContent()
  {
    return this.content;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "X509Data", "http://www.w3.org/2000/09/xmldsig#", paramString);
    int i = 0;
    int j = this.content.size();
    while (i < j)
    {
      Object localObject = this.content.get(i);
      if ((localObject instanceof X509Certificate)) {
        marshalCert((X509Certificate)localObject, localElement, localDocument, paramString);
      } else if ((localObject instanceof XMLStructure))
      {
        if ((localObject instanceof X509IssuerSerial))
        {
          ((DOMX509IssuerSerial)localObject).marshal(localElement, paramString, paramDOMCryptoContext);
        }
        else
        {
          javax.xml.crypto.dom.DOMStructure localDOMStructure = (javax.xml.crypto.dom.DOMStructure)localObject;
          DOMUtils.appendChild(localElement, localDOMStructure.getNode());
        }
      }
      else if ((localObject instanceof byte[])) {
        marshalSKI((byte[])localObject, localElement, localDocument, paramString);
      } else if ((localObject instanceof String)) {
        marshalSubjectName((String)localObject, localElement, localDocument, paramString);
      } else if ((localObject instanceof X509CRL)) {
        marshalCRL((X509CRL)localObject, localElement, localDocument, paramString);
      }
      i++;
    }
    paramNode.appendChild(localElement);
  }
  
  private void marshalSKI(byte[] paramArrayOfByte, Node paramNode, Document paramDocument, String paramString)
  {
    Element localElement = DOMUtils.createElement(paramDocument, "X509SKI", "http://www.w3.org/2000/09/xmldsig#", paramString);
    localElement.appendChild(paramDocument.createTextNode(Base64.encode(paramArrayOfByte)));
    paramNode.appendChild(localElement);
  }
  
  private void marshalSubjectName(String paramString1, Node paramNode, Document paramDocument, String paramString2)
  {
    Element localElement = DOMUtils.createElement(paramDocument, "X509SubjectName", "http://www.w3.org/2000/09/xmldsig#", paramString2);
    localElement.appendChild(paramDocument.createTextNode(paramString1));
    paramNode.appendChild(localElement);
  }
  
  private void marshalCert(X509Certificate paramX509Certificate, Node paramNode, Document paramDocument, String paramString)
    throws MarshalException
  {
    Element localElement = DOMUtils.createElement(paramDocument, "X509Certificate", "http://www.w3.org/2000/09/xmldsig#", paramString);
    try
    {
      localElement.appendChild(paramDocument.createTextNode(Base64.encode(paramX509Certificate.getEncoded())));
    }
    catch (CertificateEncodingException localCertificateEncodingException)
    {
      throw new MarshalException("Error encoding X509Certificate", localCertificateEncodingException);
    }
    paramNode.appendChild(localElement);
  }
  
  private void marshalCRL(X509CRL paramX509CRL, Node paramNode, Document paramDocument, String paramString)
    throws MarshalException
  {
    Element localElement = DOMUtils.createElement(paramDocument, "X509CRL", "http://www.w3.org/2000/09/xmldsig#", paramString);
    try
    {
      localElement.appendChild(paramDocument.createTextNode(Base64.encode(paramX509CRL.getEncoded())));
    }
    catch (CRLException localCRLException)
    {
      throw new MarshalException("Error encoding X509CRL", localCRLException);
    }
    paramNode.appendChild(localElement);
  }
  
  private X509Certificate unmarshalX509Certificate(Element paramElement)
    throws MarshalException
  {
    try
    {
      ByteArrayInputStream localByteArrayInputStream = unmarshalBase64Binary(paramElement);
      return (X509Certificate)this.cf.generateCertificate(localByteArrayInputStream);
    }
    catch (CertificateException localCertificateException)
    {
      throw new MarshalException("Cannot create X509Certificate", localCertificateException);
    }
  }
  
  private X509CRL unmarshalX509CRL(Element paramElement)
    throws MarshalException
  {
    try
    {
      ByteArrayInputStream localByteArrayInputStream = unmarshalBase64Binary(paramElement);
      return (X509CRL)this.cf.generateCRL(localByteArrayInputStream);
    }
    catch (CRLException localCRLException)
    {
      throw new MarshalException("Cannot create X509CRL", localCRLException);
    }
  }
  
  private ByteArrayInputStream unmarshalBase64Binary(Element paramElement)
    throws MarshalException
  {
    try
    {
      if (this.cf == null) {
        this.cf = CertificateFactory.getInstance("X.509");
      }
      return new ByteArrayInputStream(Base64.decode(paramElement));
    }
    catch (CertificateException localCertificateException)
    {
      throw new MarshalException("Cannot create CertificateFactory", localCertificateException);
    }
    catch (Base64DecodingException localBase64DecodingException)
    {
      throw new MarshalException("Cannot decode Base64-encoded val", localBase64DecodingException);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof X509Data)) {
      return false;
    }
    X509Data localX509Data = (X509Data)paramObject;
    List localList = localX509Data.getContent();
    int i = this.content.size();
    if (i != localList.size()) {
      return false;
    }
    for (int j = 0; j < i; j++)
    {
      Object localObject1 = this.content.get(j);
      Object localObject2 = localList.get(j);
      if ((localObject1 instanceof byte[]))
      {
        if ((!(localObject2 instanceof byte[])) || (!Arrays.equals((byte[])localObject1, (byte[])localObject2))) {
          return false;
        }
      }
      else if (!localObject1.equals(localObject2)) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + this.content.hashCode();
    return i;
  }
}
