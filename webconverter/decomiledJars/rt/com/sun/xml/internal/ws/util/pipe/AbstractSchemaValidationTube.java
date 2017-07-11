package com.sun.xml.internal.ws.util.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocument.Schema;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.developer.ValidationErrorHandler;
import com.sun.xml.internal.ws.server.SDDocumentImpl;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.NamespaceSupport;

public abstract class AbstractSchemaValidationTube
  extends AbstractFilterTubeImpl
{
  private static final Logger LOGGER = Logger.getLogger(AbstractSchemaValidationTube.class.getName());
  protected final WSBinding binding;
  protected final SchemaValidationFeature feature;
  protected final DocumentAddressResolver resolver = new ValidationDocumentAddressResolver(null);
  protected final SchemaFactory sf;
  
  public AbstractSchemaValidationTube(WSBinding paramWSBinding, Tube paramTube)
  {
    super(paramTube);
    this.binding = paramWSBinding;
    this.feature = ((SchemaValidationFeature)paramWSBinding.getFeature(SchemaValidationFeature.class));
    this.sf = XmlUtil.allowExternalAccess(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"), "file", false);
  }
  
  protected AbstractSchemaValidationTube(AbstractSchemaValidationTube paramAbstractSchemaValidationTube, TubeCloner paramTubeCloner)
  {
    super(paramAbstractSchemaValidationTube, paramTubeCloner);
    this.binding = paramAbstractSchemaValidationTube.binding;
    this.feature = paramAbstractSchemaValidationTube.feature;
    this.sf = paramAbstractSchemaValidationTube.sf;
  }
  
  protected abstract Validator getValidator();
  
  protected abstract boolean isNoValidation();
  
  private Document createDOM(SDDocument paramSDDocument)
  {
    ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
    try
    {
      paramSDDocument.writeTo(null, this.resolver, localByteArrayBuffer);
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
    Transformer localTransformer = XmlUtil.newTransformer();
    StreamSource localStreamSource = new StreamSource(localByteArrayBuffer.newInputStream(), null);
    DOMResult localDOMResult = new DOMResult();
    try
    {
      localTransformer.transform(localStreamSource, localDOMResult);
    }
    catch (TransformerException localTransformerException)
    {
      throw new WebServiceException(localTransformerException);
    }
    return (Document)localDOMResult.getNode();
  }
  
  private void updateMultiSchemaForTns(String paramString1, String paramString2, Map<String, List<String>> paramMap)
  {
    Object localObject = (List)paramMap.get(paramString1);
    if (localObject == null)
    {
      localObject = new ArrayList();
      paramMap.put(paramString1, localObject);
    }
    ((List)localObject).add(paramString2);
  }
  
  protected Source[] getSchemaSources(Iterable<SDDocument> paramIterable, MetadataResolverImpl paramMetadataResolverImpl)
  {
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    Object localObject1 = paramIterable.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (SDDocument)((Iterator)localObject1).next();
      if (((SDDocument)localObject2).isWSDL())
      {
        localObject3 = createDOM((SDDocument)localObject2);
        addSchemaFragmentSource((Document)localObject3, ((SDDocument)localObject2).getURL().toExternalForm(), localHashMap1);
      }
      else if (((SDDocument)localObject2).isSchema())
      {
        updateMultiSchemaForTns(((SDDocument.Schema)localObject2).getTargetNamespace(), ((SDDocument)localObject2).getURL().toExternalForm(), localHashMap2);
      }
    }
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "WSDL inlined schema fragment documents(these are used to create a pseudo schema) = {0}", localHashMap1.keySet());
    }
    localObject1 = localHashMap1.values().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (DOMSource)((Iterator)localObject1).next();
      localObject3 = getTargetNamespace((DOMSource)localObject2);
      updateMultiSchemaForTns((String)localObject3, ((DOMSource)localObject2).getSystemId(), localHashMap2);
    }
    if (localHashMap2.isEmpty()) {
      return new Source[0];
    }
    if ((localHashMap2.size() == 1) && (((List)localHashMap2.values().iterator().next()).size() == 1))
    {
      localObject1 = (String)((List)localHashMap2.values().iterator().next()).get(0);
      return new Source[] { (Source)localHashMap1.get(localObject1) };
    }
    paramMetadataResolverImpl.addSchemas(localHashMap1.values());
    localObject1 = new HashMap();
    int i = 0;
    Object localObject3 = localHashMap2.entrySet().iterator();
    while (((Iterator)localObject3).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject3).next();
      List localList = (List)localEntry.getValue();
      String str;
      if (localList.size() > 1)
      {
        str = "file:x-jax-ws-include-" + i++;
        Source localSource = createSameTnsPseudoSchema((String)localEntry.getKey(), localList, str);
        paramMetadataResolverImpl.addSchema(localSource);
      }
      else
      {
        str = (String)localList.get(0);
      }
      ((Map)localObject1).put(localEntry.getKey(), str);
    }
    localObject3 = createMasterPseudoSchema((Map)localObject1);
    return new Source[] { localObject3 };
  }
  
  @Nullable
  private void addSchemaFragmentSource(Document paramDocument, String paramString, Map<String, DOMSource> paramMap)
  {
    Element localElement1 = paramDocument.getDocumentElement();
    assert (localElement1.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/"));
    assert (localElement1.getLocalName().equals("definitions"));
    NodeList localNodeList1 = localElement1.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "types");
    for (int i = 0; i < localNodeList1.getLength(); i++)
    {
      NodeList localNodeList2 = ((Element)localNodeList1.item(i)).getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");
      for (int j = 0; j < localNodeList2.getLength(); j++)
      {
        Element localElement2 = (Element)localNodeList2.item(j);
        NamespaceSupport localNamespaceSupport = new NamespaceSupport();
        buildNamespaceSupport(localNamespaceSupport, localElement2);
        patchDOMFragment(localNamespaceSupport, localElement2);
        String str = paramString + "#schema" + j;
        paramMap.put(str, new DOMSource(localElement2, str));
      }
    }
  }
  
  private void buildNamespaceSupport(NamespaceSupport paramNamespaceSupport, Node paramNode)
  {
    if ((paramNode == null) || (paramNode.getNodeType() != 1)) {
      return;
    }
    buildNamespaceSupport(paramNamespaceSupport, paramNode.getParentNode());
    paramNamespaceSupport.pushContext();
    NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
    for (int i = 0; i < localNamedNodeMap.getLength(); i++)
    {
      Attr localAttr = (Attr)localNamedNodeMap.item(i);
      if ("xmlns".equals(localAttr.getPrefix())) {
        paramNamespaceSupport.declarePrefix(localAttr.getLocalName(), localAttr.getValue());
      } else if ("xmlns".equals(localAttr.getName())) {
        paramNamespaceSupport.declarePrefix("", localAttr.getValue());
      }
    }
  }
  
  @Nullable
  private void patchDOMFragment(NamespaceSupport paramNamespaceSupport, Element paramElement)
  {
    NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
    Enumeration localEnumeration = paramNamespaceSupport.getPrefixes();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      for (int i = 0; i < localNamedNodeMap.getLength(); i++)
      {
        Attr localAttr = (Attr)localNamedNodeMap.item(i);
        if ((!"xmlns".equals(localAttr.getPrefix())) || (!localAttr.getLocalName().equals(str)))
        {
          if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Patching with xmlns:{0}={1}", new Object[] { str, paramNamespaceSupport.getURI(str) });
          }
          paramElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, paramNamespaceSupport.getURI(str));
        }
      }
    }
  }
  
  @Nullable
  private Source createSameTnsPseudoSchema(String paramString1, Collection<String> paramCollection, String paramString2)
  {
    assert (paramCollection.size() > 1);
    final StringBuilder localStringBuilder = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'");
    if (!paramString1.equals("")) {
      localStringBuilder.append(" targetNamespace='").append(paramString1).append("'");
    }
    localStringBuilder.append(">\n");
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localStringBuilder.append("<xsd:include schemaLocation='").append(str).append("'/>\n");
    }
    localStringBuilder.append("</xsd:schema>\n");
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "Pseudo Schema for the same tns={0}is {1}", new Object[] { paramString1, localStringBuilder });
    }
    new StreamSource(paramString2)
    {
      public Reader getReader()
      {
        return new StringReader(localStringBuilder.toString());
      }
    };
  }
  
  private Source createMasterPseudoSchema(Map<String, String> paramMap)
  {
    final StringBuilder localStringBuilder = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:x-jax-ws-master'>\n");
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getValue();
      String str2 = (String)localEntry.getKey();
      localStringBuilder.append("<xsd:import schemaLocation='").append(str1).append("'");
      if (!str2.equals("")) {
        localStringBuilder.append(" namespace='").append(str2).append("'");
      }
      localStringBuilder.append("/>\n");
    }
    localStringBuilder.append("</xsd:schema>");
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "Master Pseudo Schema = {0}", localStringBuilder);
    }
    new StreamSource("file:x-jax-ws-master-doc")
    {
      public Reader getReader()
      {
        return new StringReader(localStringBuilder.toString());
      }
    };
  }
  
  protected void doProcess(Packet paramPacket)
    throws SAXException
  {
    getValidator().reset();
    Class localClass = this.feature.getErrorHandler();
    ValidationErrorHandler localValidationErrorHandler;
    try
    {
      localValidationErrorHandler = (ValidationErrorHandler)localClass.newInstance();
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
    localValidationErrorHandler.setPacket(paramPacket);
    getValidator().setErrorHandler(localValidationErrorHandler);
    Message localMessage = paramPacket.getMessage().copy();
    Source localSource = localMessage.readPayloadAsSource();
    try
    {
      getValidator().validate(localSource);
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
  
  private String getTargetNamespace(DOMSource paramDOMSource)
  {
    Element localElement = (Element)paramDOMSource.getNode();
    return localElement.getAttribute("targetNamespace");
  }
  
  protected class MetadataResolverImpl
    implements SDDocumentResolver, LSResourceResolver
  {
    final Map<String, SDDocument> docs = new HashMap();
    final Map<String, SDDocument> nsMapping = new HashMap();
    
    public MetadataResolverImpl() {}
    
    public MetadataResolverImpl()
    {
      Object localObject;
      Iterator localIterator = localObject.iterator();
      while (localIterator.hasNext())
      {
        SDDocument localSDDocument = (SDDocument)localIterator.next();
        if (localSDDocument.isSchema())
        {
          this.docs.put(localSDDocument.getURL().toExternalForm(), localSDDocument);
          this.nsMapping.put(((SDDocument.Schema)localSDDocument).getTargetNamespace(), localSDDocument);
        }
      }
    }
    
    void addSchema(Source paramSource)
    {
      assert (paramSource.getSystemId() != null);
      String str = paramSource.getSystemId();
      try
      {
        XMLStreamBufferResult localXMLStreamBufferResult = (XMLStreamBufferResult)XmlUtil.identityTransform(paramSource, new XMLStreamBufferResult());
        SDDocumentSource localSDDocumentSource = SDDocumentSource.create(new URL(str), localXMLStreamBufferResult.getXMLStreamBuffer());
        SDDocumentImpl localSDDocumentImpl = SDDocumentImpl.create(localSDDocumentSource, new QName(""), new QName(""));
        this.docs.put(str, localSDDocumentImpl);
        this.nsMapping.put(((SDDocument.Schema)localSDDocumentImpl).getTargetNamespace(), localSDDocumentImpl);
      }
      catch (Exception localException)
      {
        AbstractSchemaValidationTube.LOGGER.log(Level.WARNING, "Exception in adding schemas to resolver", localException);
      }
    }
    
    void addSchemas(Collection<? extends Source> paramCollection)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Source localSource = (Source)localIterator.next();
        addSchema(localSource);
      }
    }
    
    public SDDocument resolve(String paramString)
    {
      Object localObject = (SDDocument)this.docs.get(paramString);
      if (localObject == null)
      {
        SDDocumentSource localSDDocumentSource;
        try
        {
          localSDDocumentSource = SDDocumentSource.create(new URL(paramString));
        }
        catch (MalformedURLException localMalformedURLException)
        {
          throw new WebServiceException(localMalformedURLException);
        }
        localObject = SDDocumentImpl.create(localSDDocumentSource, new QName(""), new QName(""));
        this.docs.put(paramString, localObject);
      }
      return localObject;
    }
    
    public LSInput resolveResource(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    {
      if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
        AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "type={0} namespaceURI={1} publicId={2} systemId={3} baseURI={4}", new Object[] { paramString1, paramString2, paramString3, paramString4, paramString5 });
      }
      try
      {
        final SDDocument localSDDocument;
        if (paramString4 == null)
        {
          localSDDocument = (SDDocument)this.nsMapping.get(paramString2);
        }
        else
        {
          URI localURI = paramString5 != null ? new URI(paramString5).resolve(paramString4) : new URI(paramString4);
          localSDDocument = (SDDocument)this.docs.get(localURI.toString());
        }
        if (localSDDocument != null) {
          new LSInput()
          {
            public Reader getCharacterStream()
            {
              return null;
            }
            
            public void setCharacterStream(Reader paramAnonymousReader)
            {
              throw new UnsupportedOperationException();
            }
            
            public InputStream getByteStream()
            {
              ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
              try
              {
                localSDDocument.writeTo(null, AbstractSchemaValidationTube.this.resolver, localByteArrayBuffer);
              }
              catch (IOException localIOException)
              {
                throw new WebServiceException(localIOException);
              }
              return localByteArrayBuffer.newInputStream();
            }
            
            public void setByteStream(InputStream paramAnonymousInputStream)
            {
              throw new UnsupportedOperationException();
            }
            
            public String getStringData()
            {
              return null;
            }
            
            public void setStringData(String paramAnonymousString)
            {
              throw new UnsupportedOperationException();
            }
            
            public String getSystemId()
            {
              return localSDDocument.getURL().toExternalForm();
            }
            
            public void setSystemId(String paramAnonymousString)
            {
              throw new UnsupportedOperationException();
            }
            
            public String getPublicId()
            {
              return null;
            }
            
            public void setPublicId(String paramAnonymousString)
            {
              throw new UnsupportedOperationException();
            }
            
            public String getBaseURI()
            {
              return localSDDocument.getURL().toExternalForm();
            }
            
            public void setBaseURI(String paramAnonymousString)
            {
              throw new UnsupportedOperationException();
            }
            
            public String getEncoding()
            {
              return null;
            }
            
            public void setEncoding(String paramAnonymousString)
            {
              throw new UnsupportedOperationException();
            }
            
            public boolean getCertifiedText()
            {
              return false;
            }
            
            public void setCertifiedText(boolean paramAnonymousBoolean)
            {
              throw new UnsupportedOperationException();
            }
          };
        }
      }
      catch (Exception localException)
      {
        AbstractSchemaValidationTube.LOGGER.log(Level.WARNING, "Exception in LSResourceResolver impl", localException);
      }
      if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
        AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Don''t know about systemId={0} baseURI={1}", new Object[] { paramString4, paramString5 });
      }
      return null;
    }
  }
  
  private static class ValidationDocumentAddressResolver
    implements DocumentAddressResolver
  {
    private ValidationDocumentAddressResolver() {}
    
    @Nullable
    public String getRelativeAddressFor(@NotNull SDDocument paramSDDocument1, @NotNull SDDocument paramSDDocument2)
    {
      AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Current = {0} resolved relative={1}", new Object[] { paramSDDocument1.getURL(), paramSDDocument2.getURL() });
      return paramSDDocument2.getURL().toExternalForm();
    }
  }
}
