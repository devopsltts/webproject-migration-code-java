package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.jcp.xml.dsig.internal.DigesterOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMReference
  extends DOMStructure
  implements Reference, DOMURIReference
{
  public static final int MAXIMUM_TRANSFORM_COUNT = 5;
  private static boolean useC14N11 = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11"));
    }
  })).booleanValue();
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  private final DigestMethod digestMethod;
  private final String id;
  private final List<Transform> transforms;
  private List<Transform> allTransforms;
  private final Data appliedTransformData;
  private Attr here;
  private final String uri;
  private final String type;
  private byte[] digestValue;
  private byte[] calcDigestValue;
  private Element refElem;
  private boolean digested = false;
  private boolean validated = false;
  private boolean validationStatus;
  private Data derefData;
  private InputStream dis;
  private MessageDigest md;
  private Provider provider;
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList, String paramString3, Provider paramProvider)
  {
    this(paramString1, paramString2, paramDigestMethod, null, null, paramList, paramString3, null, paramProvider);
  }
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList1, Data paramData, List<? extends Transform> paramList2, String paramString3, Provider paramProvider)
  {
    this(paramString1, paramString2, paramDigestMethod, paramList1, paramData, paramList2, paramString3, null, paramProvider);
  }
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList1, Data paramData, List<? extends Transform> paramList2, String paramString3, byte[] paramArrayOfByte, Provider paramProvider)
  {
    if (paramDigestMethod == null) {
      throw new NullPointerException("DigestMethod must be non-null");
    }
    int i;
    int j;
    if (paramList1 == null)
    {
      this.allTransforms = new ArrayList();
    }
    else
    {
      this.allTransforms = new ArrayList(paramList1);
      i = 0;
      j = this.allTransforms.size();
      while (i < j)
      {
        if (!(this.allTransforms.get(i) instanceof Transform)) {
          throw new ClassCastException("appliedTransforms[" + i + "] is not a valid type");
        }
        i++;
      }
    }
    if (paramList2 == null)
    {
      this.transforms = Collections.emptyList();
    }
    else
    {
      this.transforms = new ArrayList(paramList2);
      i = 0;
      j = this.transforms.size();
      while (i < j)
      {
        if (!(this.transforms.get(i) instanceof Transform)) {
          throw new ClassCastException("transforms[" + i + "] is not a valid type");
        }
        i++;
      }
      this.allTransforms.addAll(this.transforms);
    }
    this.digestMethod = paramDigestMethod;
    this.uri = paramString1;
    if ((paramString1 != null) && (!paramString1.equals(""))) {
      try
      {
        new URI(paramString1);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        throw new IllegalArgumentException(localURISyntaxException.getMessage());
      }
    }
    this.type = paramString2;
    this.id = paramString3;
    if (paramArrayOfByte != null)
    {
      this.digestValue = ((byte[])paramArrayOfByte.clone());
      this.digested = true;
    }
    this.appliedTransformData = paramData;
    this.provider = paramProvider;
  }
  
  public DOMReference(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    Element localElement1 = DOMUtils.getFirstChildElement(paramElement);
    ArrayList localArrayList = new ArrayList(5);
    if (localElement1.getLocalName().equals("Transforms"))
    {
      localElement2 = DOMUtils.getFirstChildElement(localElement1, "Transform");
      localArrayList.add(new DOMTransform(localElement2, paramXMLCryptoContext, paramProvider));
      for (localElement2 = DOMUtils.getNextSiblingElement(localElement2); localElement2 != null; localElement2 = DOMUtils.getNextSiblingElement(localElement2))
      {
        str = localElement2.getLocalName();
        if (!str.equals("Transform")) {
          throw new MarshalException("Invalid element name: " + str + ", expected Transform");
        }
        localArrayList.add(new DOMTransform(localElement2, paramXMLCryptoContext, paramProvider));
        if ((bool) && (localArrayList.size() > 5))
        {
          localObject = "A maxiumum of 5 transforms per Reference are allowed with secure validation";
          throw new MarshalException((String)localObject);
        }
      }
      localElement1 = DOMUtils.getNextSiblingElement(localElement1);
    }
    if (!localElement1.getLocalName().equals("DigestMethod")) {
      throw new MarshalException("Invalid element name: " + localElement1.getLocalName() + ", expected DigestMethod");
    }
    Element localElement2 = localElement1;
    this.digestMethod = DOMDigestMethod.unmarshal(localElement2);
    String str = this.digestMethod.getAlgorithm();
    if ((bool) && ("http://www.w3.org/2001/04/xmldsig-more#md5".equals(str))) {
      throw new MarshalException("It is forbidden to use algorithm " + this.digestMethod + " when secure validation is enabled");
    }
    Object localObject = DOMUtils.getNextSiblingElement(localElement2, "DigestValue");
    try
    {
      this.digestValue = Base64.decode((Element)localObject);
    }
    catch (Base64DecodingException localBase64DecodingException)
    {
      throw new MarshalException(localBase64DecodingException);
    }
    if (DOMUtils.getNextSiblingElement((Node)localObject) != null) {
      throw new MarshalException("Unexpected element after DigestValue element");
    }
    this.uri = DOMUtils.getAttributeValue(paramElement, "URI");
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
    this.type = DOMUtils.getAttributeValue(paramElement, "Type");
    this.here = paramElement.getAttributeNodeNS(null, "URI");
    this.refElem = paramElement;
    this.transforms = localArrayList;
    this.allTransforms = localArrayList;
    this.appliedTransformData = null;
    this.provider = paramProvider;
  }
  
  public DigestMethod getDigestMethod()
  {
    return this.digestMethod;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public String getURI()
  {
    return this.uri;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public List getTransforms()
  {
    return Collections.unmodifiableList(this.allTransforms);
  }
  
  public byte[] getDigestValue()
  {
    return this.digestValue == null ? null : (byte[])this.digestValue.clone();
  }
  
  public byte[] getCalculatedDigestValue()
  {
    return this.calcDigestValue == null ? null : (byte[])this.calcDigestValue.clone();
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Marshalling Reference");
    }
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    this.refElem = DOMUtils.createElement(localDocument, "Reference", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(this.refElem, "Id", this.id);
    DOMUtils.setAttribute(this.refElem, "URI", this.uri);
    DOMUtils.setAttribute(this.refElem, "Type", this.type);
    if (!this.allTransforms.isEmpty())
    {
      localElement = DOMUtils.createElement(localDocument, "Transforms", "http://www.w3.org/2000/09/xmldsig#", paramString);
      this.refElem.appendChild(localElement);
      Iterator localIterator = this.allTransforms.iterator();
      while (localIterator.hasNext())
      {
        Transform localTransform = (Transform)localIterator.next();
        ((DOMStructure)localTransform).marshal(localElement, paramString, paramDOMCryptoContext);
      }
    }
    ((DOMDigestMethod)this.digestMethod).marshal(this.refElem, paramString, paramDOMCryptoContext);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Adding digestValueElem");
    }
    Element localElement = DOMUtils.createElement(localDocument, "DigestValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
    if (this.digestValue != null) {
      localElement.appendChild(localDocument.createTextNode(Base64.encode(this.digestValue)));
    }
    this.refElem.appendChild(localElement);
    paramNode.appendChild(this.refElem);
    this.here = this.refElem.getAttributeNodeNS(null, "URI");
  }
  
  public void digest(XMLSignContext paramXMLSignContext)
    throws XMLSignatureException
  {
    Data localData = null;
    if (this.appliedTransformData == null) {
      localData = dereference(paramXMLSignContext);
    } else {
      localData = this.appliedTransformData;
    }
    this.digestValue = transform(localData, paramXMLSignContext);
    String str = Base64.encode(this.digestValue);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Reference object uri = " + this.uri);
    }
    Element localElement = DOMUtils.getLastChildElement(this.refElem);
    if (localElement == null) {
      throw new XMLSignatureException("DigestValue element expected");
    }
    DOMUtils.removeAllChildren(localElement);
    localElement.appendChild(this.refElem.getOwnerDocument().createTextNode(str));
    this.digested = true;
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Reference digesting completed");
    }
  }
  
  public boolean validate(XMLValidateContext paramXMLValidateContext)
    throws XMLSignatureException
  {
    if (paramXMLValidateContext == null) {
      throw new NullPointerException("validateContext cannot be null");
    }
    if (this.validated) {
      return this.validationStatus;
    }
    Data localData = dereference(paramXMLValidateContext);
    this.calcDigestValue = transform(localData, paramXMLValidateContext);
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Expected digest: " + Base64.encode(this.digestValue));
      log.log(Level.FINE, "Actual digest: " + Base64.encode(this.calcDigestValue));
    }
    this.validationStatus = Arrays.equals(this.digestValue, this.calcDigestValue);
    this.validated = true;
    return this.validationStatus;
  }
  
  public Data getDereferencedData()
  {
    return this.derefData;
  }
  
  public InputStream getDigestInputStream()
  {
    return this.dis;
  }
  
  private Data dereference(XMLCryptoContext paramXMLCryptoContext)
    throws XMLSignatureException
  {
    Data localData = null;
    URIDereferencer localURIDereferencer = paramXMLCryptoContext.getURIDereferencer();
    if (localURIDereferencer == null) {
      localURIDereferencer = DOMURIDereferencer.INSTANCE;
    }
    try
    {
      localData = localURIDereferencer.dereference(this, paramXMLCryptoContext);
      if (log.isLoggable(Level.FINE))
      {
        log.log(Level.FINE, "URIDereferencer class name: " + localURIDereferencer.getClass().getName());
        log.log(Level.FINE, "Data class name: " + localData.getClass().getName());
      }
    }
    catch (URIReferenceException localURIReferenceException)
    {
      throw new XMLSignatureException(localURIReferenceException);
    }
    return localData;
  }
  
  private byte[] transform(Data paramData, XMLCryptoContext paramXMLCryptoContext)
    throws XMLSignatureException
  {
    if (this.md == null) {
      try
      {
        this.md = MessageDigest.getInstance(((DOMDigestMethod)this.digestMethod).getMessageDigestAlgorithm());
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException1)
      {
        throw new XMLSignatureException(localNoSuchAlgorithmException1);
      }
    }
    this.md.reset();
    Boolean localBoolean = (Boolean)paramXMLCryptoContext.getProperty("javax.xml.crypto.dsig.cacheReference");
    DigesterOutputStream localDigesterOutputStream;
    if ((localBoolean != null) && (localBoolean.booleanValue()))
    {
      this.derefData = copyDerefData(paramData);
      localDigesterOutputStream = new DigesterOutputStream(this.md, true);
    }
    else
    {
      localDigesterOutputStream = new DigesterOutputStream(this.md);
    }
    UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = null;
    Data localData = paramData;
    try
    {
      localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(localDigesterOutputStream);
      int i = 0;
      int j = this.transforms.size();
      Object localObject2;
      while (i < j)
      {
        localObject2 = (DOMTransform)this.transforms.get(i);
        if (i < j - 1) {
          localData = ((DOMTransform)localObject2).transform(localData, paramXMLCryptoContext);
        } else {
          localData = ((DOMTransform)localObject2).transform(localData, paramXMLCryptoContext, localUnsyncBufferedOutputStream);
        }
        i++;
      }
      if (localData != null)
      {
        boolean bool = useC14N11;
        localObject2 = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
        Object localObject3;
        if ((paramXMLCryptoContext instanceof XMLSignContext)) {
          if (!bool)
          {
            localObject3 = (Boolean)paramXMLCryptoContext.getProperty("com.sun.org.apache.xml.internal.security.useC14N11");
            bool = (localObject3 != null) && (((Boolean)localObject3).booleanValue());
            if (bool) {
              localObject2 = "http://www.w3.org/2006/12/xml-c14n11";
            }
          }
          else
          {
            localObject2 = "http://www.w3.org/2006/12/xml-c14n11";
          }
        }
        if ((localData instanceof ApacheData))
        {
          localObject1 = ((ApacheData)localData).getXMLSignatureInput();
        }
        else if ((localData instanceof OctetStreamData))
        {
          localObject1 = new XMLSignatureInput(((OctetStreamData)localData).getOctetStream());
        }
        else if ((localData instanceof NodeSetData))
        {
          localObject3 = null;
          if (this.provider == null) {
            localObject3 = TransformService.getInstance((String)localObject2, "DOM");
          } else {
            try
            {
              localObject3 = TransformService.getInstance((String)localObject2, "DOM", this.provider);
            }
            catch (NoSuchAlgorithmException localNoSuchAlgorithmException3)
            {
              localObject3 = TransformService.getInstance((String)localObject2, "DOM");
            }
          }
          localData = ((TransformService)localObject3).transform(localData, paramXMLCryptoContext);
          localObject1 = new XMLSignatureInput(((OctetStreamData)localData).getOctetStream());
        }
        else
        {
          throw new XMLSignatureException("unrecognized Data type");
        }
        if (((paramXMLCryptoContext instanceof XMLSignContext)) && (bool) && (!((XMLSignatureInput)localObject1).isOctetStream()) && (!((XMLSignatureInput)localObject1).isOutputStreamSet()))
        {
          localObject3 = null;
          if (this.provider == null) {
            localObject3 = TransformService.getInstance((String)localObject2, "DOM");
          } else {
            try
            {
              localObject3 = TransformService.getInstance((String)localObject2, "DOM", this.provider);
            }
            catch (NoSuchAlgorithmException localNoSuchAlgorithmException4)
            {
              localObject3 = TransformService.getInstance((String)localObject2, "DOM");
            }
          }
          DOMTransform localDOMTransform = new DOMTransform((TransformService)localObject3);
          Element localElement = null;
          String str = DOMUtils.getSignaturePrefix(paramXMLCryptoContext);
          if (this.allTransforms.isEmpty())
          {
            localElement = DOMUtils.createElement(this.refElem.getOwnerDocument(), "Transforms", "http://www.w3.org/2000/09/xmldsig#", str);
            this.refElem.insertBefore(localElement, DOMUtils.getFirstChildElement(this.refElem));
          }
          else
          {
            localElement = DOMUtils.getFirstChildElement(this.refElem);
          }
          localDOMTransform.marshal(localElement, str, (DOMCryptoContext)paramXMLCryptoContext);
          this.allTransforms.add(localDOMTransform);
          ((XMLSignatureInput)localObject1).updateOutputStream(localUnsyncBufferedOutputStream, true);
        }
        else
        {
          ((XMLSignatureInput)localObject1).updateOutputStream(localUnsyncBufferedOutputStream);
        }
      }
      localUnsyncBufferedOutputStream.flush();
      if ((localBoolean != null) && (localBoolean.booleanValue())) {
        this.dis = localDigesterOutputStream.getInputStream();
      }
      Object localObject1 = localDigesterOutputStream.getDigestValue();
      return localObject1;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException2)
    {
      throw new XMLSignatureException(localNoSuchAlgorithmException2);
    }
    catch (TransformException localTransformException)
    {
      throw new XMLSignatureException(localTransformException);
    }
    catch (MarshalException localMarshalException)
    {
      throw new XMLSignatureException(localMarshalException);
    }
    catch (IOException localIOException1)
    {
      throw new XMLSignatureException(localIOException1);
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new XMLSignatureException(localCanonicalizationException);
    }
    finally
    {
      if (localUnsyncBufferedOutputStream != null) {
        try
        {
          localUnsyncBufferedOutputStream.close();
        }
        catch (IOException localIOException4)
        {
          throw new XMLSignatureException(localIOException4);
        }
      }
      if (localDigesterOutputStream != null) {
        try
        {
          localDigesterOutputStream.close();
        }
        catch (IOException localIOException5)
        {
          throw new XMLSignatureException(localIOException5);
        }
      }
    }
  }
  
  public Node getHere()
  {
    return this.here;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Reference)) {
      return false;
    }
    Reference localReference = (Reference)paramObject;
    boolean bool1 = this.id == null ? false : localReference.getId() == null ? true : this.id.equals(localReference.getId());
    boolean bool2 = this.uri == null ? false : localReference.getURI() == null ? true : this.uri.equals(localReference.getURI());
    boolean bool3 = this.type == null ? false : localReference.getType() == null ? true : this.type.equals(localReference.getType());
    boolean bool4 = Arrays.equals(this.digestValue, localReference.getDigestValue());
    return (this.digestMethod.equals(localReference.getDigestMethod())) && (bool1) && (bool2) && (bool3) && (this.allTransforms.equals(localReference.getTransforms())) && (bool4);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (this.id != null) {
      i = 31 * i + this.id.hashCode();
    }
    if (this.uri != null) {
      i = 31 * i + this.uri.hashCode();
    }
    if (this.type != null) {
      i = 31 * i + this.type.hashCode();
    }
    if (this.digestValue != null) {
      i = 31 * i + Arrays.hashCode(this.digestValue);
    }
    i = 31 * i + this.digestMethod.hashCode();
    i = 31 * i + this.allTransforms.hashCode();
    return i;
  }
  
  boolean isDigested()
  {
    return this.digested;
  }
  
  private static Data copyDerefData(Data paramData)
  {
    if ((paramData instanceof ApacheData))
    {
      ApacheData localApacheData = (ApacheData)paramData;
      XMLSignatureInput localXMLSignatureInput = localApacheData.getXMLSignatureInput();
      if (localXMLSignatureInput.isNodeSet()) {
        try
        {
          Set localSet = localXMLSignatureInput.getNodeSet();
          new NodeSetData()
          {
            public Iterator iterator()
            {
              return this.val$s.iterator();
            }
          };
        }
        catch (Exception localException)
        {
          log.log(Level.WARNING, "cannot cache dereferenced data: " + localException);
          return null;
        }
      }
      if (localXMLSignatureInput.isElement()) {
        return new DOMSubTreeData(localXMLSignatureInput.getSubNode(), localXMLSignatureInput.isExcludeComments());
      }
      if ((localXMLSignatureInput.isOctetStream()) || (localXMLSignatureInput.isByteArray())) {
        try
        {
          return new OctetStreamData(localXMLSignatureInput.getOctetStream(), localXMLSignatureInput.getSourceURI(), localXMLSignatureInput.getMIMEType());
        }
        catch (IOException localIOException)
        {
          log.log(Level.WARNING, "cannot cache dereferenced data: " + localIOException);
          return null;
        }
      }
    }
    return paramData;
  }
}
