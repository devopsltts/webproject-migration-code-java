package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class RetrievalMethodResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(RetrievalMethodResolver.class.getName());
  
  public RetrievalMethodResolver() {}
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "RetrievalMethod")) {
      return null;
    }
    try
    {
      RetrievalMethod localRetrievalMethod = new RetrievalMethod(paramElement, paramString);
      String str = localRetrievalMethod.getType();
      XMLSignatureInput localXMLSignatureInput1 = resolveInput(localRetrievalMethod, paramString, this.secureValidation);
      if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(str))
      {
        localObject1 = getRawCertificate(localXMLSignatureInput1);
        if (localObject1 != null) {
          return ((X509Certificate)localObject1).getPublicKey();
        }
        return null;
      }
      Object localObject1 = obtainReferenceElement(localXMLSignatureInput1);
      if (XMLUtils.elementIsInSignatureSpace((Element)localObject1, "RetrievalMethod"))
      {
        if (this.secureValidation)
        {
          localObject2 = "Error: It is forbidden to have one RetrievalMethod point to another with secure validation";
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)localObject2);
          }
          return null;
        }
        Object localObject2 = new RetrievalMethod((Element)localObject1, paramString);
        XMLSignatureInput localXMLSignatureInput2 = resolveInput((RetrievalMethod)localObject2, paramString, this.secureValidation);
        Element localElement = obtainReferenceElement(localXMLSignatureInput2);
        if (localElement == paramElement)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Error: Can't have RetrievalMethods pointing to each other");
          }
          return null;
        }
      }
      return resolveKey((Element)localObject1, paramString, paramStorageResolver);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
      }
    }
    catch (CertificateException localCertificateException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "CertificateException", localCertificateException);
      }
    }
    catch (IOException localIOException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "IOException", localIOException);
      }
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "ParserConfigurationException", localParserConfigurationException);
      }
    }
    catch (SAXException localSAXException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "SAXException", localSAXException);
      }
    }
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "RetrievalMethod")) {
      return null;
    }
    try
    {
      RetrievalMethod localRetrievalMethod = new RetrievalMethod(paramElement, paramString);
      String str = localRetrievalMethod.getType();
      XMLSignatureInput localXMLSignatureInput1 = resolveInput(localRetrievalMethod, paramString, this.secureValidation);
      if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(str)) {
        return getRawCertificate(localXMLSignatureInput1);
      }
      Element localElement1 = obtainReferenceElement(localXMLSignatureInput1);
      if (XMLUtils.elementIsInSignatureSpace(localElement1, "RetrievalMethod"))
      {
        if (this.secureValidation)
        {
          localObject = "Error: It is forbidden to have one RetrievalMethod point to another with secure validation";
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)localObject);
          }
          return null;
        }
        Object localObject = new RetrievalMethod(localElement1, paramString);
        XMLSignatureInput localXMLSignatureInput2 = resolveInput((RetrievalMethod)localObject, paramString, this.secureValidation);
        Element localElement2 = obtainReferenceElement(localXMLSignatureInput2);
        if (localElement2 == paramElement)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Error: Can't have RetrievalMethods pointing to each other");
          }
          return null;
        }
      }
      return resolveCertificate(localElement1, paramString, paramStorageResolver);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
      }
    }
    catch (CertificateException localCertificateException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "CertificateException", localCertificateException);
      }
    }
    catch (IOException localIOException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "IOException", localIOException);
      }
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "ParserConfigurationException", localParserConfigurationException);
      }
    }
    catch (SAXException localSAXException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "SAXException", localSAXException);
      }
    }
    return null;
  }
  
  private static X509Certificate resolveCertificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Now we have a {" + paramElement.getNamespaceURI() + "}" + paramElement.getLocalName() + " Element");
    }
    if (paramElement != null) {
      return KeyResolver.getX509Certificate(paramElement, paramString, paramStorageResolver);
    }
    return null;
  }
  
  private static PublicKey resolveKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Now we have a {" + paramElement.getNamespaceURI() + "}" + paramElement.getLocalName() + " Element");
    }
    if (paramElement != null) {
      return KeyResolver.getPublicKey(paramElement, paramString, paramStorageResolver);
    }
    return null;
  }
  
  private static Element obtainReferenceElement(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException
  {
    Element localElement;
    if (paramXMLSignatureInput.isElement())
    {
      localElement = (Element)paramXMLSignatureInput.getSubNode();
    }
    else if (paramXMLSignatureInput.isNodeSet())
    {
      localElement = getDocumentElement(paramXMLSignatureInput.getNodeSet());
    }
    else
    {
      byte[] arrayOfByte = paramXMLSignatureInput.getBytes();
      localElement = getDocFromBytes(arrayOfByte);
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "we have to parse " + arrayOfByte.length + " bytes");
      }
    }
    return localElement;
  }
  
  private static X509Certificate getRawCertificate(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException, IOException, CertificateException
  {
    byte[] arrayOfByte = paramXMLSignatureInput.getBytes();
    CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
    X509Certificate localX509Certificate = (X509Certificate)localCertificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfByte));
    return localX509Certificate;
  }
  
  private static XMLSignatureInput resolveInput(RetrievalMethod paramRetrievalMethod, String paramString, boolean paramBoolean)
    throws XMLSecurityException
  {
    Attr localAttr = paramRetrievalMethod.getURIAttr();
    Transforms localTransforms = paramRetrievalMethod.getTransforms();
    ResourceResolver localResourceResolver = ResourceResolver.getInstance(localAttr, paramString, paramBoolean);
    XMLSignatureInput localXMLSignatureInput = localResourceResolver.resolve(localAttr, paramString, paramBoolean);
    if (localTransforms != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "We have Transforms");
      }
      localXMLSignatureInput = localTransforms.performTransforms(localXMLSignatureInput);
    }
    return localXMLSignatureInput;
  }
  
  private static Element getDocFromBytes(byte[] paramArrayOfByte)
    throws KeyResolverException
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      Document localDocument = localDocumentBuilder.parse(new ByteArrayInputStream(paramArrayOfByte));
      return localDocument.getDocumentElement();
    }
    catch (SAXException localSAXException)
    {
      throw new KeyResolverException("empty", localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new KeyResolverException("empty", localIOException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new KeyResolverException("empty", localParserConfigurationException);
    }
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
  
  private static Element getDocumentElement(Set<Node> paramSet)
  {
    Iterator localIterator = paramSet.iterator();
    Element localElement1 = null;
    while (localIterator.hasNext())
    {
      localObject1 = (Node)localIterator.next();
      if ((localObject1 != null) && (1 == ((Node)localObject1).getNodeType()))
      {
        localElement1 = (Element)localObject1;
        break;
      }
    }
    Object localObject1 = new ArrayList();
    while (localElement1 != null)
    {
      ((List)localObject1).add(localElement1);
      localObject2 = localElement1.getParentNode();
      if ((localObject2 == null) || (1 != ((Node)localObject2).getNodeType())) {
        break;
      }
      localElement1 = (Element)localObject2;
    }
    Object localObject2 = ((List)localObject1).listIterator(((List)localObject1).size() - 1);
    Element localElement2 = null;
    while (((ListIterator)localObject2).hasPrevious())
    {
      localElement2 = (Element)((ListIterator)localObject2).previous();
      if (paramSet.contains(localElement2)) {
        return localElement2;
      }
    }
    return null;
  }
}
