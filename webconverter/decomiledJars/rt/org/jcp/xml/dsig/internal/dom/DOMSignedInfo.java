package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMSignedInfo
  extends DOMStructure
  implements SignedInfo
{
  public static final int MAXIMUM_REFERENCE_COUNT = 30;
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  private static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
  private static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
  private List<Reference> references;
  private CanonicalizationMethod canonicalizationMethod;
  private SignatureMethod signatureMethod;
  private String id;
  private Document ownerDoc;
  private Element localSiElem;
  private InputStream canonData;
  
  public DOMSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List<? extends Reference> paramList)
  {
    if ((paramCanonicalizationMethod == null) || (paramSignatureMethod == null) || (paramList == null)) {
      throw new NullPointerException();
    }
    this.canonicalizationMethod = paramCanonicalizationMethod;
    this.signatureMethod = paramSignatureMethod;
    this.references = Collections.unmodifiableList(new ArrayList(paramList));
    if (this.references.isEmpty()) {
      throw new IllegalArgumentException("list of references must contain at least one entry");
    }
    int i = 0;
    int j = this.references.size();
    while (i < j)
    {
      Object localObject = this.references.get(i);
      if (!(localObject instanceof Reference)) {
        throw new ClassCastException("list of references contains an illegal type");
      }
      i++;
    }
  }
  
  public DOMSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List<? extends Reference> paramList, String paramString)
  {
    this(paramCanonicalizationMethod, paramSignatureMethod, paramList);
    this.id = paramString;
  }
  
  public DOMSignedInfo(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    this.localSiElem = paramElement;
    this.ownerDoc = paramElement.getOwnerDocument();
    this.id = DOMUtils.getAttributeValue(paramElement, "Id");
    Element localElement1 = DOMUtils.getFirstChildElement(paramElement, "CanonicalizationMethod");
    this.canonicalizationMethod = new DOMCanonicalizationMethod(localElement1, paramXMLCryptoContext, paramProvider);
    Element localElement2 = DOMUtils.getNextSiblingElement(localElement1, "SignatureMethod");
    this.signatureMethod = DOMSignatureMethod.unmarshal(localElement2);
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    String str1 = this.signatureMethod.getAlgorithm();
    if ((bool) && (("http://www.w3.org/2001/04/xmldsig-more#hmac-md5".equals(str1)) || ("http://www.w3.org/2001/04/xmldsig-more#rsa-md5".equals(str1)))) {
      throw new MarshalException("It is forbidden to use algorithm " + this.signatureMethod + " when secure validation is enabled");
    }
    ArrayList localArrayList = new ArrayList(5);
    Element localElement3 = DOMUtils.getNextSiblingElement(localElement2, "Reference");
    localArrayList.add(new DOMReference(localElement3, paramXMLCryptoContext, paramProvider));
    for (localElement3 = DOMUtils.getNextSiblingElement(localElement3); localElement3 != null; localElement3 = DOMUtils.getNextSiblingElement(localElement3))
    {
      String str2 = localElement3.getLocalName();
      if (!str2.equals("Reference")) {
        throw new MarshalException("Invalid element name: " + str2 + ", expected Reference");
      }
      localArrayList.add(new DOMReference(localElement3, paramXMLCryptoContext, paramProvider));
      if ((bool) && (localArrayList.size() > 30))
      {
        String str3 = "A maxiumum of 30 references per Manifest are allowed with secure validation";
        throw new MarshalException(str3);
      }
    }
    this.references = Collections.unmodifiableList(localArrayList);
  }
  
  public CanonicalizationMethod getCanonicalizationMethod()
  {
    return this.canonicalizationMethod;
  }
  
  public SignatureMethod getSignatureMethod()
  {
    return this.signatureMethod;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public List getReferences()
  {
    return this.references;
  }
  
  public InputStream getCanonicalizedData()
  {
    return this.canonData;
  }
  
  public void canonicalize(XMLCryptoContext paramXMLCryptoContext, ByteArrayOutputStream paramByteArrayOutputStream)
    throws XMLSignatureException
  {
    if (paramXMLCryptoContext == null) {
      throw new NullPointerException("context cannot be null");
    }
    UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(paramByteArrayOutputStream);
    DOMSubTreeData localDOMSubTreeData = new DOMSubTreeData(this.localSiElem, true);
    try
    {
      ((DOMCanonicalizationMethod)this.canonicalizationMethod).canonicalize(localDOMSubTreeData, paramXMLCryptoContext, localUnsyncBufferedOutputStream);
    }
    catch (TransformException localTransformException)
    {
      throw new XMLSignatureException(localTransformException);
    }
    try
    {
      localUnsyncBufferedOutputStream.flush();
    }
    catch (IOException localIOException1)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localIOException1.getMessage(), localIOException1);
      }
    }
    byte[] arrayOfByte = paramByteArrayOutputStream.toByteArray();
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Canonicalized SignedInfo:");
      StringBuilder localStringBuilder = new StringBuilder(arrayOfByte.length);
      for (int i = 0; i < arrayOfByte.length; i++) {
        localStringBuilder.append((char)arrayOfByte[i]);
      }
      log.log(Level.FINE, localStringBuilder.toString());
      log.log(Level.FINE, "Data to be signed/verified:" + Base64.encode(arrayOfByte));
    }
    this.canonData = new ByteArrayInputStream(arrayOfByte);
    try
    {
      localUnsyncBufferedOutputStream.close();
    }
    catch (IOException localIOException2)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localIOException2.getMessage(), localIOException2);
      }
    }
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    this.ownerDoc = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(this.ownerDoc, "SignedInfo", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMCanonicalizationMethod localDOMCanonicalizationMethod = (DOMCanonicalizationMethod)this.canonicalizationMethod;
    localDOMCanonicalizationMethod.marshal(localElement, paramString, paramDOMCryptoContext);
    ((DOMStructure)this.signatureMethod).marshal(localElement, paramString, paramDOMCryptoContext);
    Iterator localIterator = this.references.iterator();
    while (localIterator.hasNext())
    {
      Reference localReference = (Reference)localIterator.next();
      ((DOMReference)localReference).marshal(localElement, paramString, paramDOMCryptoContext);
    }
    DOMUtils.setAttributeID(localElement, "Id", this.id);
    paramNode.appendChild(localElement);
    this.localSiElem = localElement;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SignedInfo)) {
      return false;
    }
    SignedInfo localSignedInfo = (SignedInfo)paramObject;
    boolean bool = this.id == null ? false : localSignedInfo.getId() == null ? true : this.id.equals(localSignedInfo.getId());
    return (this.canonicalizationMethod.equals(localSignedInfo.getCanonicalizationMethod())) && (this.signatureMethod.equals(localSignedInfo.getSignatureMethod())) && (this.references.equals(localSignedInfo.getReferences())) && (bool);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (this.id != null) {
      i = 31 * i + this.id.hashCode();
    }
    i = 31 * i + this.canonicalizationMethod.hashCode();
    i = 31 * i + this.signatureMethod.hashCode();
    i = 31 * i + this.references.hashCode();
    return i;
  }
}
