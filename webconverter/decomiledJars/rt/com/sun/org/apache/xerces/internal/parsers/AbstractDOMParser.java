package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentTypeImpl;
import com.sun.org.apache.xerces.internal.dom.ElementDefinitionImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.EntityImpl;
import com.sun.org.apache.xerces.internal.dom.EntityReferenceImpl;
import com.sun.org.apache.xerces.internal.dom.NodeImpl;
import com.sun.org.apache.xerces.internal.dom.NotationImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Locale;
import java.util.Stack;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.ls.LSParserFilter;

public class AbstractDOMParser
  extends AbstractXMLDocumentParser
{
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  protected static final String CREATE_ENTITY_REF_NODES = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
  protected static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
  protected static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
  protected static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
  protected static final String DEFER_NODE_EXPANSION = "http://apache.org/xml/features/dom/defer-node-expansion";
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/dom/create-entity-ref-nodes", "http://apache.org/xml/features/include-comments", "http://apache.org/xml/features/create-cdata-nodes", "http://apache.org/xml/features/dom/include-ignorable-whitespace", "http://apache.org/xml/features/dom/defer-node-expansion" };
  protected static final String DOCUMENT_CLASS_NAME = "http://apache.org/xml/properties/dom/document-class-name";
  protected static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/dom/document-class-name", "http://apache.org/xml/properties/dom/current-element-node" };
  protected static final String DEFAULT_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.DocumentImpl";
  protected static final String CORE_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl";
  protected static final String PSVI_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl";
  private static final boolean DEBUG_EVENTS = false;
  private static final boolean DEBUG_BASEURI = false;
  protected DOMErrorHandlerWrapper fErrorHandler = null;
  protected boolean fInDTD;
  protected boolean fCreateEntityRefNodes;
  protected boolean fIncludeIgnorableWhitespace;
  protected boolean fIncludeComments;
  protected boolean fCreateCDATANodes;
  protected Document fDocument;
  protected CoreDocumentImpl fDocumentImpl;
  protected boolean fStorePSVI;
  protected String fDocumentClassName;
  protected DocumentType fDocumentType;
  protected Node fCurrentNode;
  protected CDATASection fCurrentCDATASection;
  protected EntityImpl fCurrentEntityDecl;
  protected int fDeferredEntityDecl;
  protected final StringBuilder fStringBuilder = new StringBuilder(50);
  protected StringBuilder fInternalSubset;
  protected boolean fDeferNodeExpansion;
  protected boolean fNamespaceAware;
  protected DeferredDocumentImpl fDeferredDocumentImpl;
  protected int fDocumentIndex;
  protected int fDocumentTypeIndex;
  protected int fCurrentNodeIndex;
  protected int fCurrentCDATASectionIndex;
  protected boolean fInDTDExternalSubset;
  protected Node fRoot;
  protected boolean fInCDATASection;
  protected boolean fFirstChunk = false;
  protected boolean fFilterReject = false;
  protected final Stack fBaseURIStack = new Stack();
  protected int fRejectedElementDepth = 0;
  protected Stack fSkippedElemStack = null;
  protected boolean fInEntityRef = false;
  private final QName fAttrQName = new QName();
  private XMLLocator fLocator;
  protected LSParserFilter fDOMFilter = null;
  
  protected AbstractDOMParser(XMLParserConfiguration paramXMLParserConfiguration)
  {
    super(paramXMLParserConfiguration);
    this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", true);
    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
    this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
    this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", true);
    this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", "com.sun.org.apache.xerces.internal.dom.DocumentImpl");
  }
  
  protected String getDocumentClassName()
  {
    return this.fDocumentClassName;
  }
  
  protected void setDocumentClassName(String paramString)
  {
    if (paramString == null) {
      paramString = "com.sun.org.apache.xerces.internal.dom.DocumentImpl";
    }
    if ((!paramString.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl")) && (!paramString.equals("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl"))) {
      try
      {
        Class localClass = ObjectFactory.findProviderClass(paramString, true);
        if (!Document.class.isAssignableFrom(localClass)) {
          throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidDocumentClassName", new Object[] { paramString }));
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "MissingDocumentClassName", new Object[] { paramString }));
      }
    }
    this.fDocumentClassName = paramString;
    if (!paramString.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl")) {
      this.fDeferNodeExpansion = false;
    }
  }
  
  public Document getDocument()
  {
    return this.fDocument;
  }
  
  public final void dropDocumentReferences()
  {
    this.fDocument = null;
    this.fDocumentImpl = null;
    this.fDeferredDocumentImpl = null;
    this.fDocumentType = null;
    this.fCurrentNode = null;
    this.fCurrentCDATASection = null;
    this.fCurrentEntityDecl = null;
    this.fRoot = null;
  }
  
  public void reset()
    throws XNIException
  {
    super.reset();
    this.fCreateEntityRefNodes = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes");
    this.fIncludeIgnorableWhitespace = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace");
    this.fDeferNodeExpansion = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/defer-node-expansion");
    this.fNamespaceAware = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces");
    this.fIncludeComments = this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments");
    this.fCreateCDATANodes = this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes");
    setDocumentClassName((String)this.fConfiguration.getProperty("http://apache.org/xml/properties/dom/document-class-name"));
    this.fDocument = null;
    this.fDocumentImpl = null;
    this.fStorePSVI = false;
    this.fDocumentType = null;
    this.fDocumentTypeIndex = -1;
    this.fDeferredDocumentImpl = null;
    this.fCurrentNode = null;
    this.fStringBuilder.setLength(0);
    this.fRoot = null;
    this.fInDTD = false;
    this.fInDTDExternalSubset = false;
    this.fInCDATASection = false;
    this.fFirstChunk = false;
    this.fCurrentCDATASection = null;
    this.fCurrentCDATASectionIndex = -1;
    this.fBaseURIStack.removeAllElements();
  }
  
  public void setLocale(Locale paramLocale)
  {
    this.fConfiguration.setLocale(paramLocale);
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!this.fDeferNodeExpansion)
    {
      if (this.fFilterReject) {
        return;
      }
      setCharacterData(true);
      EntityReference localEntityReference = this.fDocument.createEntityReference(paramString1);
      if (this.fDocumentImpl != null)
      {
        EntityReferenceImpl localEntityReferenceImpl = (EntityReferenceImpl)localEntityReference;
        localEntityReferenceImpl.setBaseURI(paramXMLResourceIdentifier.getExpandedSystemId());
        if (this.fDocumentType != null)
        {
          NamedNodeMap localNamedNodeMap = this.fDocumentType.getEntities();
          this.fCurrentEntityDecl = ((EntityImpl)localNamedNodeMap.getNamedItem(paramString1));
          if (this.fCurrentEntityDecl != null) {
            this.fCurrentEntityDecl.setInputEncoding(paramString2);
          }
        }
        localEntityReferenceImpl.needsSyncChildren(false);
      }
      this.fInEntityRef = true;
      this.fCurrentNode.appendChild(localEntityReference);
      this.fCurrentNode = localEntityReference;
    }
    else
    {
      int i = this.fDeferredDocumentImpl.createDeferredEntityReference(paramString1, paramXMLResourceIdentifier.getExpandedSystemId());
      if (this.fDocumentTypeIndex != -1) {
        for (int j = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); j != -1; j = this.fDeferredDocumentImpl.getRealPrevSibling(j, false))
        {
          int k = this.fDeferredDocumentImpl.getNodeType(j, false);
          if (k == 6)
          {
            String str = this.fDeferredDocumentImpl.getNodeName(j, false);
            if (str.equals(paramString1))
            {
              this.fDeferredEntityDecl = j;
              this.fDeferredDocumentImpl.setInputEncoding(j, paramString2);
              break;
            }
          }
        }
      }
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
      this.fCurrentNodeIndex = i;
    }
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fInDTD) {
      return;
    }
    if (!this.fDeferNodeExpansion)
    {
      if ((this.fCurrentEntityDecl != null) && (!this.fFilterReject))
      {
        this.fCurrentEntityDecl.setXmlEncoding(paramString2);
        if (paramString1 != null) {
          this.fCurrentEntityDecl.setXmlVersion(paramString1);
        }
      }
    }
    else if (this.fDeferredEntityDecl != -1) {
      this.fDeferredDocumentImpl.setEntityInfo(this.fDeferredEntityDecl, paramString1, paramString2);
    }
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fInDTD)
    {
      if ((this.fInternalSubset != null) && (!this.fInDTDExternalSubset))
      {
        this.fInternalSubset.append("<!--");
        if (paramXMLString.length > 0) {
          this.fInternalSubset.append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
        }
        this.fInternalSubset.append("-->");
      }
      return;
    }
    if ((!this.fIncludeComments) || (this.fFilterReject)) {
      return;
    }
    if (!this.fDeferNodeExpansion)
    {
      Comment localComment = this.fDocument.createComment(paramXMLString.toString());
      setCharacterData(false);
      this.fCurrentNode.appendChild(localComment);
      if ((this.fDOMFilter != null) && (!this.fInEntityRef) && ((this.fDOMFilter.getWhatToShow() & 0x80) != 0))
      {
        int j = this.fDOMFilter.acceptNode(localComment);
        switch (j)
        {
        case 4: 
          throw Abort.INSTANCE;
        case 2: 
        case 3: 
          this.fCurrentNode.removeChild(localComment);
          this.fFirstChunk = true;
          return;
        }
      }
    }
    else
    {
      int i = this.fDeferredDocumentImpl.createDeferredComment(paramXMLString.toString());
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
    }
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fInDTD)
    {
      if ((this.fInternalSubset != null) && (!this.fInDTDExternalSubset))
      {
        this.fInternalSubset.append("<?");
        this.fInternalSubset.append(paramString);
        if (paramXMLString.length > 0) {
          this.fInternalSubset.append(' ').append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
        }
        this.fInternalSubset.append("?>");
      }
      return;
    }
    if (!this.fDeferNodeExpansion)
    {
      if (this.fFilterReject) {
        return;
      }
      ProcessingInstruction localProcessingInstruction = this.fDocument.createProcessingInstruction(paramString, paramXMLString.toString());
      setCharacterData(false);
      this.fCurrentNode.appendChild(localProcessingInstruction);
      if ((this.fDOMFilter != null) && (!this.fInEntityRef) && ((this.fDOMFilter.getWhatToShow() & 0x40) != 0))
      {
        int j = this.fDOMFilter.acceptNode(localProcessingInstruction);
        switch (j)
        {
        case 4: 
          throw Abort.INSTANCE;
        case 2: 
        case 3: 
          this.fCurrentNode.removeChild(localProcessingInstruction);
          this.fFirstChunk = true;
          return;
        }
      }
    }
    else
    {
      int i = this.fDeferredDocumentImpl.createDeferredProcessingInstruction(paramString, paramXMLString.toString());
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
    }
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fLocator = paramXMLLocator;
    if (!this.fDeferNodeExpansion)
    {
      if (this.fDocumentClassName.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl"))
      {
        this.fDocument = new DocumentImpl();
        this.fDocumentImpl = ((CoreDocumentImpl)this.fDocument);
        this.fDocumentImpl.setStrictErrorChecking(false);
        this.fDocumentImpl.setInputEncoding(paramString);
        this.fDocumentImpl.setDocumentURI(paramXMLLocator.getExpandedSystemId());
      }
      else if (this.fDocumentClassName.equals("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl"))
      {
        this.fDocument = new PSVIDocumentImpl();
        this.fDocumentImpl = ((CoreDocumentImpl)this.fDocument);
        this.fStorePSVI = true;
        this.fDocumentImpl.setStrictErrorChecking(false);
        this.fDocumentImpl.setInputEncoding(paramString);
        this.fDocumentImpl.setDocumentURI(paramXMLLocator.getExpandedSystemId());
      }
      else
      {
        try
        {
          Class localClass1 = ObjectFactory.findProviderClass(this.fDocumentClassName, true);
          this.fDocument = ((Document)localClass1.newInstance());
          Class localClass2 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl", true);
          if (localClass2.isAssignableFrom(localClass1))
          {
            this.fDocumentImpl = ((CoreDocumentImpl)this.fDocument);
            Class localClass3 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl", true);
            if (localClass3.isAssignableFrom(localClass1)) {
              this.fStorePSVI = true;
            }
            this.fDocumentImpl.setStrictErrorChecking(false);
            this.fDocumentImpl.setInputEncoding(paramString);
            if (paramXMLLocator != null) {
              this.fDocumentImpl.setDocumentURI(paramXMLLocator.getExpandedSystemId());
            }
          }
        }
        catch (ClassNotFoundException localClassNotFoundException) {}catch (Exception localException)
        {
          throw new RuntimeException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "CannotCreateDocumentClass", new Object[] { this.fDocumentClassName }));
        }
      }
      this.fCurrentNode = this.fDocument;
    }
    else
    {
      this.fDeferredDocumentImpl = new DeferredDocumentImpl(this.fNamespaceAware);
      this.fDocument = this.fDeferredDocumentImpl;
      this.fDocumentIndex = this.fDeferredDocumentImpl.createDeferredDocument();
      this.fDeferredDocumentImpl.setInputEncoding(paramString);
      this.fDeferredDocumentImpl.setDocumentURI(paramXMLLocator.getExpandedSystemId());
      this.fCurrentNodeIndex = this.fDocumentIndex;
    }
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!this.fDeferNodeExpansion)
    {
      if (this.fDocumentImpl != null)
      {
        if (paramString1 != null) {
          this.fDocumentImpl.setXmlVersion(paramString1);
        }
        this.fDocumentImpl.setXmlEncoding(paramString2);
        this.fDocumentImpl.setXmlStandalone("yes".equals(paramString3));
      }
    }
    else
    {
      if (paramString1 != null) {
        this.fDeferredDocumentImpl.setXmlVersion(paramString1);
      }
      this.fDeferredDocumentImpl.setXmlEncoding(paramString2);
      this.fDeferredDocumentImpl.setXmlStandalone("yes".equals(paramString3));
    }
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!this.fDeferNodeExpansion)
    {
      if (this.fDocumentImpl != null)
      {
        this.fDocumentType = this.fDocumentImpl.createDocumentType(paramString1, paramString2, paramString3);
        this.fCurrentNode.appendChild(this.fDocumentType);
      }
    }
    else
    {
      this.fDocumentTypeIndex = this.fDeferredDocumentImpl.createDeferredDocumentType(paramString1, paramString2, paramString3);
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDocumentTypeIndex);
    }
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    int k;
    Object localObject2;
    int n;
    if (!this.fDeferNodeExpansion)
    {
      if (this.fFilterReject)
      {
        this.fRejectedElementDepth += 1;
        return;
      }
      Element localElement = createElementNode(paramQName);
      int j = paramXMLAttributes.getLength();
      k = 0;
      for (int m = 0; m < j; m++)
      {
        paramXMLAttributes.getName(m, this.fAttrQName);
        localObject2 = createAttrNode(this.fAttrQName);
        String str = paramXMLAttributes.getValue(m);
        AttributePSVI localAttributePSVI = (AttributePSVI)paramXMLAttributes.getAugmentations(m).getItem("ATTRIBUTE_PSVI");
        if ((this.fStorePSVI) && (localAttributePSVI != null)) {
          ((PSVIAttrNSImpl)localObject2).setPSVI(localAttributePSVI);
        }
        ((Attr)localObject2).setValue(str);
        boolean bool3 = paramXMLAttributes.isSpecified(m);
        if ((!bool3) && ((k != 0) || ((this.fAttrQName.uri != null) && (this.fAttrQName.uri != NamespaceContext.XMLNS_URI) && (this.fAttrQName.prefix == null))))
        {
          localElement.setAttributeNodeNS((Attr)localObject2);
          k = 1;
        }
        else
        {
          localElement.setAttributeNode((Attr)localObject2);
        }
        if (this.fDocumentImpl != null)
        {
          AttrImpl localAttrImpl = (AttrImpl)localObject2;
          Object localObject3 = null;
          boolean bool4 = false;
          if ((localAttributePSVI != null) && (this.fNamespaceAware))
          {
            localObject3 = localAttributePSVI.getMemberTypeDefinition();
            if (localObject3 == null)
            {
              localObject3 = localAttributePSVI.getTypeDefinition();
              if (localObject3 != null)
              {
                bool4 = ((XSSimpleType)localObject3).isIDType();
                localAttrImpl.setType(localObject3);
              }
            }
            else
            {
              bool4 = ((XSSimpleType)localObject3).isIDType();
              localAttrImpl.setType(localObject3);
            }
          }
          else
          {
            boolean bool5 = Boolean.TRUE.equals(paramXMLAttributes.getAugmentations(m).getItem("ATTRIBUTE_DECLARED"));
            if (bool5)
            {
              localObject3 = paramXMLAttributes.getType(m);
              bool4 = "ID".equals(localObject3);
            }
            localAttrImpl.setType(localObject3);
          }
          if (bool4) {
            ((ElementImpl)localElement).setIdAttributeNode((Attr)localObject2, true);
          }
          localAttrImpl.setSpecified(bool3);
        }
      }
      setCharacterData(false);
      if (paramAugmentations != null)
      {
        ElementPSVI localElementPSVI = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
        if ((localElementPSVI != null) && (this.fNamespaceAware))
        {
          localObject2 = localElementPSVI.getMemberTypeDefinition();
          if (localObject2 == null) {
            localObject2 = localElementPSVI.getTypeDefinition();
          }
          ((ElementNSImpl)localElement).setType((XSTypeDefinition)localObject2);
        }
      }
      if ((this.fDOMFilter != null) && (!this.fInEntityRef)) {
        if (this.fRoot == null)
        {
          this.fRoot = localElement;
        }
        else
        {
          n = this.fDOMFilter.startElement(localElement);
          switch (n)
          {
          case 4: 
            throw Abort.INSTANCE;
          case 2: 
            this.fFilterReject = true;
            this.fRejectedElementDepth = 0;
            return;
          case 3: 
            this.fFirstChunk = true;
            this.fSkippedElemStack.push(Boolean.TRUE);
            return;
          }
          if (!this.fSkippedElemStack.isEmpty()) {
            this.fSkippedElemStack.push(Boolean.FALSE);
          }
        }
      }
      this.fCurrentNode.appendChild(localElement);
      this.fCurrentNode = localElement;
    }
    else
    {
      int i = this.fDeferredDocumentImpl.createDeferredElement(this.fNamespaceAware ? paramQName.uri : null, paramQName.rawname);
      Object localObject1 = null;
      k = paramXMLAttributes.getLength();
      for (n = k - 1; n >= 0; n--)
      {
        localObject2 = (AttributePSVI)paramXMLAttributes.getAugmentations(n).getItem("ATTRIBUTE_PSVI");
        boolean bool1 = false;
        if ((localObject2 != null) && (this.fNamespaceAware))
        {
          localObject1 = ((AttributePSVI)localObject2).getMemberTypeDefinition();
          if (localObject1 == null)
          {
            localObject1 = ((AttributePSVI)localObject2).getTypeDefinition();
            if (localObject1 != null) {
              bool1 = ((XSSimpleType)localObject1).isIDType();
            }
          }
          else
          {
            bool1 = ((XSSimpleType)localObject1).isIDType();
          }
        }
        else
        {
          boolean bool2 = Boolean.TRUE.equals(paramXMLAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
          if (bool2)
          {
            localObject1 = paramXMLAttributes.getType(n);
            bool1 = "ID".equals(localObject1);
          }
        }
        this.fDeferredDocumentImpl.setDeferredAttribute(i, paramXMLAttributes.getQName(n), paramXMLAttributes.getURI(n), paramXMLAttributes.getValue(n), paramXMLAttributes.isSpecified(n), bool1, localObject1);
      }
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
      this.fCurrentNodeIndex = i;
    }
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!this.fDeferNodeExpansion)
    {
      if (this.fFilterReject) {
        return;
      }
      if ((this.fInCDATASection) && (this.fCreateCDATANodes))
      {
        if (this.fCurrentCDATASection == null)
        {
          this.fCurrentCDATASection = this.fDocument.createCDATASection(paramXMLString.toString());
          this.fCurrentNode.appendChild(this.fCurrentCDATASection);
          this.fCurrentNode = this.fCurrentCDATASection;
        }
        else
        {
          this.fCurrentCDATASection.appendData(paramXMLString.toString());
        }
      }
      else if (!this.fInDTD)
      {
        if (paramXMLString.length == 0) {
          return;
        }
        Node localNode = this.fCurrentNode.getLastChild();
        if ((localNode != null) && (localNode.getNodeType() == 3))
        {
          if (this.fFirstChunk)
          {
            if (this.fDocumentImpl != null)
            {
              this.fStringBuilder.append(((TextImpl)localNode).removeData());
            }
            else
            {
              this.fStringBuilder.append(((Text)localNode).getData());
              ((Text)localNode).setNodeValue(null);
            }
            this.fFirstChunk = false;
          }
          if (paramXMLString.length > 0) {
            this.fStringBuilder.append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
          }
        }
        else
        {
          this.fFirstChunk = true;
          Text localText = this.fDocument.createTextNode(paramXMLString.toString());
          this.fCurrentNode.appendChild(localText);
        }
      }
    }
    else if ((this.fInCDATASection) && (this.fCreateCDATANodes))
    {
      int i;
      if (this.fCurrentCDATASectionIndex == -1)
      {
        i = this.fDeferredDocumentImpl.createDeferredCDATASection(paramXMLString.toString());
        this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
        this.fCurrentCDATASectionIndex = i;
        this.fCurrentNodeIndex = i;
      }
      else
      {
        i = this.fDeferredDocumentImpl.createDeferredTextNode(paramXMLString.toString(), false);
        this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
      }
    }
    else if (!this.fInDTD)
    {
      if (paramXMLString.length == 0) {
        return;
      }
      String str = paramXMLString.toString();
      int j = this.fDeferredDocumentImpl.createDeferredTextNode(str, false);
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, j);
    }
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((!this.fIncludeIgnorableWhitespace) || (this.fFilterReject)) {
      return;
    }
    if (!this.fDeferNodeExpansion)
    {
      Node localNode = this.fCurrentNode.getLastChild();
      Text localText;
      if ((localNode != null) && (localNode.getNodeType() == 3))
      {
        localText = (Text)localNode;
        localText.appendData(paramXMLString.toString());
      }
      else
      {
        localText = this.fDocument.createTextNode(paramXMLString.toString());
        if (this.fDocumentImpl != null)
        {
          TextImpl localTextImpl = (TextImpl)localText;
          localTextImpl.setIgnorableWhitespace(true);
        }
        this.fCurrentNode.appendChild(localText);
      }
    }
    else
    {
      int i = this.fDeferredDocumentImpl.createDeferredTextNode(paramXMLString.toString(), true);
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
    }
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException
  {
    Object localObject;
    if (!this.fDeferNodeExpansion)
    {
      if ((paramAugmentations != null) && (this.fDocumentImpl != null) && ((this.fNamespaceAware) || (this.fStorePSVI)))
      {
        ElementPSVI localElementPSVI1 = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
        if (localElementPSVI1 != null)
        {
          if (this.fNamespaceAware)
          {
            localObject = localElementPSVI1.getMemberTypeDefinition();
            if (localObject == null) {
              localObject = localElementPSVI1.getTypeDefinition();
            }
            ((ElementNSImpl)this.fCurrentNode).setType((XSTypeDefinition)localObject);
          }
          if (this.fStorePSVI) {
            ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(localElementPSVI1);
          }
        }
      }
      if (this.fDOMFilter != null)
      {
        if (this.fFilterReject)
        {
          if (this.fRejectedElementDepth-- == 0) {
            this.fFilterReject = false;
          }
          return;
        }
        if ((!this.fSkippedElemStack.isEmpty()) && (this.fSkippedElemStack.pop() == Boolean.TRUE)) {
          return;
        }
        setCharacterData(false);
        if ((this.fCurrentNode != this.fRoot) && (!this.fInEntityRef) && ((this.fDOMFilter.getWhatToShow() & 0x1) != 0))
        {
          int i = this.fDOMFilter.acceptNode(this.fCurrentNode);
          switch (i)
          {
          case 4: 
            throw Abort.INSTANCE;
          case 2: 
            localObject = this.fCurrentNode.getParentNode();
            ((Node)localObject).removeChild(this.fCurrentNode);
            this.fCurrentNode = ((Node)localObject);
            return;
          case 3: 
            this.fFirstChunk = true;
            localObject = this.fCurrentNode.getParentNode();
            NodeList localNodeList = this.fCurrentNode.getChildNodes();
            int j = localNodeList.getLength();
            for (int k = 0; k < j; k++) {
              ((Node)localObject).appendChild(localNodeList.item(0));
            }
            ((Node)localObject).removeChild(this.fCurrentNode);
            this.fCurrentNode = ((Node)localObject);
            return;
          }
        }
        this.fCurrentNode = this.fCurrentNode.getParentNode();
      }
      else
      {
        setCharacterData(false);
        this.fCurrentNode = this.fCurrentNode.getParentNode();
      }
    }
    else
    {
      if (paramAugmentations != null)
      {
        ElementPSVI localElementPSVI2 = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
        if (localElementPSVI2 != null)
        {
          localObject = localElementPSVI2.getMemberTypeDefinition();
          if (localObject == null) {
            localObject = localElementPSVI2.getTypeDefinition();
          }
          this.fDeferredDocumentImpl.setTypeInfo(this.fCurrentNodeIndex, localObject);
        }
      }
      this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
    }
  }
  
  public void startCDATA(Augmentations paramAugmentations)
    throws XNIException
  {
    this.fInCDATASection = true;
    if (!this.fDeferNodeExpansion)
    {
      if (this.fFilterReject) {
        return;
      }
      if (this.fCreateCDATANodes) {
        setCharacterData(false);
      }
    }
  }
  
  public void endCDATA(Augmentations paramAugmentations)
    throws XNIException
  {
    this.fInCDATASection = false;
    if (!this.fDeferNodeExpansion)
    {
      if (this.fFilterReject) {
        return;
      }
      if (this.fCurrentCDATASection != null)
      {
        if ((this.fDOMFilter != null) && (!this.fInEntityRef) && ((this.fDOMFilter.getWhatToShow() & 0x8) != 0))
        {
          int i = this.fDOMFilter.acceptNode(this.fCurrentCDATASection);
          switch (i)
          {
          case 4: 
            throw Abort.INSTANCE;
          case 2: 
          case 3: 
            Node localNode = this.fCurrentNode.getParentNode();
            localNode.removeChild(this.fCurrentCDATASection);
            this.fCurrentNode = localNode;
            return;
          }
        }
        this.fCurrentNode = this.fCurrentNode.getParentNode();
        this.fCurrentCDATASection = null;
      }
    }
    else if (this.fCurrentCDATASectionIndex != -1)
    {
      this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
      this.fCurrentCDATASectionIndex = -1;
    }
  }
  
  public void endDocument(Augmentations paramAugmentations)
    throws XNIException
  {
    if (!this.fDeferNodeExpansion)
    {
      if (this.fDocumentImpl != null)
      {
        if ((this.fLocator != null) && (this.fLocator.getEncoding() != null)) {
          this.fDocumentImpl.setInputEncoding(this.fLocator.getEncoding());
        }
        this.fDocumentImpl.setStrictErrorChecking(true);
      }
      this.fCurrentNode = null;
    }
    else
    {
      if ((this.fLocator != null) && (this.fLocator.getEncoding() != null)) {
        this.fDeferredDocumentImpl.setInputEncoding(this.fLocator.getEncoding());
      }
      this.fCurrentNodeIndex = -1;
    }
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    Object localObject;
    int i;
    int n;
    if (!this.fDeferNodeExpansion)
    {
      if (this.fFilterReject) {
        return;
      }
      setCharacterData(true);
      if (this.fDocumentType != null)
      {
        NamedNodeMap localNamedNodeMap = this.fDocumentType.getEntities();
        this.fCurrentEntityDecl = ((EntityImpl)localNamedNodeMap.getNamedItem(paramString));
        if (this.fCurrentEntityDecl != null)
        {
          if ((this.fCurrentEntityDecl != null) && (this.fCurrentEntityDecl.getFirstChild() == null))
          {
            this.fCurrentEntityDecl.setReadOnly(false, true);
            for (Node localNode1 = this.fCurrentNode.getFirstChild(); localNode1 != null; localNode1 = localNode1.getNextSibling())
            {
              localObject = localNode1.cloneNode(true);
              this.fCurrentEntityDecl.appendChild((Node)localObject);
            }
            this.fCurrentEntityDecl.setReadOnly(true, true);
          }
          this.fCurrentEntityDecl = null;
        }
      }
      this.fInEntityRef = false;
      i = 0;
      if (this.fCreateEntityRefNodes)
      {
        if (this.fDocumentImpl != null) {
          ((NodeImpl)this.fCurrentNode).setReadOnly(true, true);
        }
        if ((this.fDOMFilter != null) && ((this.fDOMFilter.getWhatToShow() & 0x10) != 0))
        {
          int j = this.fDOMFilter.acceptNode(this.fCurrentNode);
          switch (j)
          {
          case 4: 
            throw Abort.INSTANCE;
          case 2: 
            localObject = this.fCurrentNode.getParentNode();
            ((Node)localObject).removeChild(this.fCurrentNode);
            this.fCurrentNode = ((Node)localObject);
            return;
          case 3: 
            this.fFirstChunk = true;
            i = 1;
            break;
          default: 
            this.fCurrentNode = this.fCurrentNode.getParentNode();
          }
        }
        else
        {
          this.fCurrentNode = this.fCurrentNode.getParentNode();
        }
      }
      if ((!this.fCreateEntityRefNodes) || (i != 0))
      {
        NodeList localNodeList = this.fCurrentNode.getChildNodes();
        localObject = this.fCurrentNode.getParentNode();
        n = localNodeList.getLength();
        if (n > 0)
        {
          Node localNode2 = this.fCurrentNode.getPreviousSibling();
          Node localNode3 = localNodeList.item(0);
          if ((localNode2 != null) && (localNode2.getNodeType() == 3) && (localNode3.getNodeType() == 3))
          {
            ((Text)localNode2).appendData(localNode3.getNodeValue());
            this.fCurrentNode.removeChild(localNode3);
          }
          else
          {
            localNode2 = ((Node)localObject).insertBefore(localNode3, this.fCurrentNode);
            handleBaseURI(localNode2);
          }
          for (int i2 = 1; i2 < n; i2++)
          {
            localNode2 = ((Node)localObject).insertBefore(localNodeList.item(0), this.fCurrentNode);
            handleBaseURI(localNode2);
          }
        }
        ((Node)localObject).removeChild(this.fCurrentNode);
        this.fCurrentNode = ((Node)localObject);
      }
    }
    else
    {
      int k;
      if (this.fDocumentTypeIndex != -1) {
        for (i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false))
        {
          k = this.fDeferredDocumentImpl.getNodeType(i, false);
          if (k == 6)
          {
            localObject = this.fDeferredDocumentImpl.getNodeName(i, false);
            if (((String)localObject).equals(paramString))
            {
              this.fDeferredEntityDecl = i;
              break;
            }
          }
        }
      }
      int m;
      if ((this.fDeferredEntityDecl != -1) && (this.fDeferredDocumentImpl.getLastChild(this.fDeferredEntityDecl, false) == -1))
      {
        i = -1;
        for (k = this.fDeferredDocumentImpl.getLastChild(this.fCurrentNodeIndex, false); k != -1; k = this.fDeferredDocumentImpl.getRealPrevSibling(k, false))
        {
          m = this.fDeferredDocumentImpl.cloneNode(k, true);
          this.fDeferredDocumentImpl.insertBefore(this.fDeferredEntityDecl, m, i);
          i = m;
        }
      }
      if (this.fCreateEntityRefNodes)
      {
        this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
      }
      else
      {
        i = this.fDeferredDocumentImpl.getLastChild(this.fCurrentNodeIndex, false);
        k = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
        m = this.fCurrentNodeIndex;
        n = i;
        int i1 = -1;
        while (i != -1)
        {
          handleBaseURI(i);
          i1 = this.fDeferredDocumentImpl.getRealPrevSibling(i, false);
          this.fDeferredDocumentImpl.insertBefore(k, i, m);
          m = i;
          i = i1;
        }
        if (n != -1)
        {
          this.fDeferredDocumentImpl.setAsLastChild(k, n);
        }
        else
        {
          i1 = this.fDeferredDocumentImpl.getRealPrevSibling(m, false);
          this.fDeferredDocumentImpl.setAsLastChild(k, i1);
        }
        this.fCurrentNodeIndex = k;
      }
      this.fDeferredEntityDecl = -1;
    }
  }
  
  protected final void handleBaseURI(Node paramNode)
  {
    if (this.fDocumentImpl != null)
    {
      String str = null;
      int i = paramNode.getNodeType();
      if (i == 1)
      {
        if (this.fNamespaceAware)
        {
          if (((Element)paramNode).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "base") == null) {}
        }
        else if (((Element)paramNode).getAttributeNode("xml:base") != null) {
          return;
        }
        str = ((EntityReferenceImpl)this.fCurrentNode).getBaseURI();
        if ((str != null) && (!str.equals(this.fDocumentImpl.getDocumentURI()))) {
          if (this.fNamespaceAware) {
            ((Element)paramNode).setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", str);
          } else {
            ((Element)paramNode).setAttribute("xml:base", str);
          }
        }
      }
      else if (i == 7)
      {
        str = ((EntityReferenceImpl)this.fCurrentNode).getBaseURI();
        if ((str != null) && (this.fErrorHandler != null))
        {
          DOMErrorImpl localDOMErrorImpl = new DOMErrorImpl();
          localDOMErrorImpl.fType = "pi-base-uri-not-preserved";
          localDOMErrorImpl.fRelatedData = str;
          localDOMErrorImpl.fSeverity = 1;
          this.fErrorHandler.getErrorHandler().handleError(localDOMErrorImpl);
        }
      }
    }
  }
  
  protected final void handleBaseURI(int paramInt)
  {
    int i = this.fDeferredDocumentImpl.getNodeType(paramInt, false);
    String str;
    if (i == 1)
    {
      str = this.fDeferredDocumentImpl.getNodeValueString(this.fCurrentNodeIndex, false);
      if (str == null) {
        str = this.fDeferredDocumentImpl.getDeferredEntityBaseURI(this.fDeferredEntityDecl);
      }
      if ((str != null) && (!str.equals(this.fDeferredDocumentImpl.getDocumentURI()))) {
        this.fDeferredDocumentImpl.setDeferredAttribute(paramInt, "xml:base", "http://www.w3.org/XML/1998/namespace", str, true);
      }
    }
    else if (i == 7)
    {
      str = this.fDeferredDocumentImpl.getNodeValueString(this.fCurrentNodeIndex, false);
      if (str == null) {
        str = this.fDeferredDocumentImpl.getDeferredEntityBaseURI(this.fDeferredEntityDecl);
      }
      if ((str != null) && (this.fErrorHandler != null))
      {
        DOMErrorImpl localDOMErrorImpl = new DOMErrorImpl();
        localDOMErrorImpl.fType = "pi-base-uri-not-preserved";
        localDOMErrorImpl.fRelatedData = str;
        localDOMErrorImpl.fSeverity = 1;
        this.fErrorHandler.getErrorHandler().handleError(localDOMErrorImpl);
      }
    }
  }
  
  public void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fInDTD = true;
    if (paramXMLLocator != null) {
      this.fBaseURIStack.push(paramXMLLocator.getBaseSystemId());
    }
    if ((this.fDeferNodeExpansion) || (this.fDocumentImpl != null)) {
      this.fInternalSubset = new StringBuilder(1024);
    }
  }
  
  public void endDTD(Augmentations paramAugmentations)
    throws XNIException
  {
    this.fInDTD = false;
    if (!this.fBaseURIStack.isEmpty()) {
      this.fBaseURIStack.pop();
    }
    String str = (this.fInternalSubset != null) && (this.fInternalSubset.length() > 0) ? this.fInternalSubset.toString() : null;
    if (this.fDeferNodeExpansion)
    {
      if (str != null) {
        this.fDeferredDocumentImpl.setInternalSubset(this.fDocumentTypeIndex, str);
      }
    }
    else if ((this.fDocumentImpl != null) && (str != null)) {
      ((DocumentTypeImpl)this.fDocumentType).setInternalSubset(str);
    }
  }
  
  public void startConditional(short paramShort, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endConditional(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void startExternalSubset(XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fBaseURIStack.push(paramXMLResourceIdentifier.getBaseSystemId());
    this.fInDTDExternalSubset = true;
  }
  
  public void endExternalSubset(Augmentations paramAugmentations)
    throws XNIException
  {
    this.fInDTDExternalSubset = false;
    this.fBaseURIStack.pop();
  }
  
  public void internalEntityDecl(String paramString, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations)
    throws XNIException
  {
    Object localObject;
    if ((this.fInternalSubset != null) && (!this.fInDTDExternalSubset))
    {
      this.fInternalSubset.append("<!ENTITY ");
      if (paramString.startsWith("%"))
      {
        this.fInternalSubset.append("% ");
        this.fInternalSubset.append(paramString.substring(1));
      }
      else
      {
        this.fInternalSubset.append(paramString);
      }
      this.fInternalSubset.append(' ');
      localObject = paramXMLString2.toString();
      int j = ((String)localObject).indexOf('\'') == -1 ? 1 : 0;
      this.fInternalSubset.append(j != 0 ? '\'' : '"');
      this.fInternalSubset.append((String)localObject);
      this.fInternalSubset.append(j != 0 ? '\'' : '"');
      this.fInternalSubset.append(">\n");
    }
    if (paramString.startsWith("%")) {
      return;
    }
    if (this.fDocumentType != null)
    {
      localObject = this.fDocumentType.getEntities();
      EntityImpl localEntityImpl = (EntityImpl)((NamedNodeMap)localObject).getNamedItem(paramString);
      if (localEntityImpl == null)
      {
        localEntityImpl = (EntityImpl)this.fDocumentImpl.createEntity(paramString);
        localEntityImpl.setBaseURI((String)this.fBaseURIStack.peek());
        ((NamedNodeMap)localObject).setNamedItem(localEntityImpl);
      }
    }
    if (this.fDocumentTypeIndex != -1)
    {
      int i = 0;
      int m;
      for (int k = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); k != -1; k = this.fDeferredDocumentImpl.getRealPrevSibling(k, false))
      {
        m = this.fDeferredDocumentImpl.getNodeType(k, false);
        if (m == 6)
        {
          String str = this.fDeferredDocumentImpl.getNodeName(k, false);
          if (str.equals(paramString))
          {
            i = 1;
            break;
          }
        }
      }
      if (i == 0)
      {
        m = this.fDeferredDocumentImpl.createDeferredEntity(paramString, null, null, null, (String)this.fBaseURIStack.peek());
        this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, m);
      }
    }
  }
  
  public void externalEntityDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations)
    throws XNIException
  {
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    if ((this.fInternalSubset != null) && (!this.fInDTDExternalSubset))
    {
      this.fInternalSubset.append("<!ENTITY ");
      if (paramString.startsWith("%"))
      {
        this.fInternalSubset.append("% ");
        this.fInternalSubset.append(paramString.substring(1));
      }
      else
      {
        this.fInternalSubset.append(paramString);
      }
      this.fInternalSubset.append(' ');
      if (str1 != null)
      {
        this.fInternalSubset.append("PUBLIC '");
        this.fInternalSubset.append(str1);
        this.fInternalSubset.append("' '");
      }
      else
      {
        this.fInternalSubset.append("SYSTEM '");
      }
      this.fInternalSubset.append(str2);
      this.fInternalSubset.append("'>\n");
    }
    if (paramString.startsWith("%")) {
      return;
    }
    if (this.fDocumentType != null)
    {
      NamedNodeMap localNamedNodeMap = this.fDocumentType.getEntities();
      EntityImpl localEntityImpl = (EntityImpl)localNamedNodeMap.getNamedItem(paramString);
      if (localEntityImpl == null)
      {
        localEntityImpl = (EntityImpl)this.fDocumentImpl.createEntity(paramString);
        localEntityImpl.setPublicId(str1);
        localEntityImpl.setSystemId(str2);
        localEntityImpl.setBaseURI(paramXMLResourceIdentifier.getBaseSystemId());
        localNamedNodeMap.setNamedItem(localEntityImpl);
      }
    }
    if (this.fDocumentTypeIndex != -1)
    {
      int i = 0;
      int k;
      for (int j = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); j != -1; j = this.fDeferredDocumentImpl.getRealPrevSibling(j, false))
      {
        k = this.fDeferredDocumentImpl.getNodeType(j, false);
        if (k == 6)
        {
          String str3 = this.fDeferredDocumentImpl.getNodeName(j, false);
          if (str3.equals(paramString))
          {
            i = 1;
            break;
          }
        }
      }
      if (i == 0)
      {
        k = this.fDeferredDocumentImpl.createDeferredEntity(paramString, str1, str2, null, paramXMLResourceIdentifier.getBaseSystemId());
        this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, k);
      }
    }
  }
  
  public void startParameterEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((paramAugmentations != null) && (this.fInternalSubset != null) && (!this.fInDTDExternalSubset) && (Boolean.TRUE.equals(paramAugmentations.getItem("ENTITY_SKIPPED")))) {
      this.fInternalSubset.append(paramString1).append(";\n");
    }
    this.fBaseURIStack.push(paramXMLResourceIdentifier.getExpandedSystemId());
  }
  
  public void endParameterEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fBaseURIStack.pop();
  }
  
  public void unparsedEntityDecl(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    if ((this.fInternalSubset != null) && (!this.fInDTDExternalSubset))
    {
      this.fInternalSubset.append("<!ENTITY ");
      this.fInternalSubset.append(paramString1);
      this.fInternalSubset.append(' ');
      if (str1 != null)
      {
        this.fInternalSubset.append("PUBLIC '");
        this.fInternalSubset.append(str1);
        if (str2 != null)
        {
          this.fInternalSubset.append("' '");
          this.fInternalSubset.append(str2);
        }
      }
      else
      {
        this.fInternalSubset.append("SYSTEM '");
        this.fInternalSubset.append(str2);
      }
      this.fInternalSubset.append("' NDATA ");
      this.fInternalSubset.append(paramString2);
      this.fInternalSubset.append(">\n");
    }
    if (this.fDocumentType != null)
    {
      NamedNodeMap localNamedNodeMap = this.fDocumentType.getEntities();
      EntityImpl localEntityImpl = (EntityImpl)localNamedNodeMap.getNamedItem(paramString1);
      if (localEntityImpl == null)
      {
        localEntityImpl = (EntityImpl)this.fDocumentImpl.createEntity(paramString1);
        localEntityImpl.setPublicId(str1);
        localEntityImpl.setSystemId(str2);
        localEntityImpl.setNotationName(paramString2);
        localEntityImpl.setBaseURI(paramXMLResourceIdentifier.getBaseSystemId());
        localNamedNodeMap.setNamedItem(localEntityImpl);
      }
    }
    if (this.fDocumentTypeIndex != -1)
    {
      int i = 0;
      int k;
      for (int j = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); j != -1; j = this.fDeferredDocumentImpl.getRealPrevSibling(j, false))
      {
        k = this.fDeferredDocumentImpl.getNodeType(j, false);
        if (k == 6)
        {
          String str3 = this.fDeferredDocumentImpl.getNodeName(j, false);
          if (str3.equals(paramString1))
          {
            i = 1;
            break;
          }
        }
      }
      if (i == 0)
      {
        k = this.fDeferredDocumentImpl.createDeferredEntity(paramString1, str1, str2, paramString2, paramXMLResourceIdentifier.getBaseSystemId());
        this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, k);
      }
    }
  }
  
  public void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations)
    throws XNIException
  {
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    if ((this.fInternalSubset != null) && (!this.fInDTDExternalSubset))
    {
      this.fInternalSubset.append("<!NOTATION ");
      this.fInternalSubset.append(paramString);
      if (str1 != null)
      {
        this.fInternalSubset.append(" PUBLIC '");
        this.fInternalSubset.append(str1);
        if (str2 != null)
        {
          this.fInternalSubset.append("' '");
          this.fInternalSubset.append(str2);
        }
      }
      else
      {
        this.fInternalSubset.append(" SYSTEM '");
        this.fInternalSubset.append(str2);
      }
      this.fInternalSubset.append("'>\n");
    }
    if ((this.fDocumentImpl != null) && (this.fDocumentType != null))
    {
      NamedNodeMap localNamedNodeMap = this.fDocumentType.getNotations();
      if (localNamedNodeMap.getNamedItem(paramString) == null)
      {
        NotationImpl localNotationImpl = (NotationImpl)this.fDocumentImpl.createNotation(paramString);
        localNotationImpl.setPublicId(str1);
        localNotationImpl.setSystemId(str2);
        localNotationImpl.setBaseURI(paramXMLResourceIdentifier.getBaseSystemId());
        localNamedNodeMap.setNamedItem(localNotationImpl);
      }
    }
    if (this.fDocumentTypeIndex != -1)
    {
      int i = 0;
      int k;
      for (int j = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); j != -1; j = this.fDeferredDocumentImpl.getPrevSibling(j, false))
      {
        k = this.fDeferredDocumentImpl.getNodeType(j, false);
        if (k == 12)
        {
          String str3 = this.fDeferredDocumentImpl.getNodeName(j, false);
          if (str3.equals(paramString))
          {
            i = 1;
            break;
          }
        }
      }
      if (i == 0)
      {
        k = this.fDeferredDocumentImpl.createDeferredNotation(paramString, str1, str2, paramXMLResourceIdentifier.getBaseSystemId());
        this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, k);
      }
    }
  }
  
  public void ignoredCharacters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void elementDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((this.fInternalSubset != null) && (!this.fInDTDExternalSubset))
    {
      this.fInternalSubset.append("<!ELEMENT ");
      this.fInternalSubset.append(paramString1);
      this.fInternalSubset.append(' ');
      this.fInternalSubset.append(paramString2);
      this.fInternalSubset.append(">\n");
    }
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations)
    throws XNIException
  {
    int i;
    if ((this.fInternalSubset != null) && (!this.fInDTDExternalSubset))
    {
      this.fInternalSubset.append("<!ATTLIST ");
      this.fInternalSubset.append(paramString1);
      this.fInternalSubset.append(' ');
      this.fInternalSubset.append(paramString2);
      this.fInternalSubset.append(' ');
      if (paramString3.equals("ENUMERATION"))
      {
        this.fInternalSubset.append('(');
        for (i = 0; i < paramArrayOfString.length; i++)
        {
          if (i > 0) {
            this.fInternalSubset.append('|');
          }
          this.fInternalSubset.append(paramArrayOfString[i]);
        }
        this.fInternalSubset.append(')');
      }
      else
      {
        this.fInternalSubset.append(paramString3);
      }
      if (paramString4 != null)
      {
        this.fInternalSubset.append(' ');
        this.fInternalSubset.append(paramString4);
      }
      if (paramXMLString1 != null)
      {
        this.fInternalSubset.append(" '");
        for (i = 0; i < paramXMLString1.length; i++)
        {
          char c = paramXMLString1.ch[(paramXMLString1.offset + i)];
          if (c == '\'') {
            this.fInternalSubset.append("&apos;");
          } else {
            this.fInternalSubset.append(c);
          }
        }
        this.fInternalSubset.append('\'');
      }
      this.fInternalSubset.append(">\n");
    }
    if (this.fDeferredDocumentImpl != null)
    {
      if (paramXMLString1 != null)
      {
        i = this.fDeferredDocumentImpl.lookupElementDefinition(paramString1);
        if (i == -1)
        {
          i = this.fDeferredDocumentImpl.createDeferredElementDefinition(paramString1);
          this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, i);
        }
        boolean bool1 = this.fNamespaceAware;
        String str1 = null;
        if (bool1) {
          if ((paramString2.startsWith("xmlns:")) || (paramString2.equals("xmlns"))) {
            str1 = NamespaceContext.XMLNS_URI;
          } else if (paramString2.startsWith("xml:")) {
            str1 = NamespaceContext.XML_URI;
          }
        }
        int j = this.fDeferredDocumentImpl.createDeferredAttribute(paramString2, str1, paramXMLString1.toString(), false);
        if ("ID".equals(paramString3)) {
          this.fDeferredDocumentImpl.setIdAttribute(j);
        }
        this.fDeferredDocumentImpl.appendChild(i, j);
      }
    }
    else if ((this.fDocumentImpl != null) && (paramXMLString1 != null))
    {
      NamedNodeMap localNamedNodeMap = ((DocumentTypeImpl)this.fDocumentType).getElements();
      ElementDefinitionImpl localElementDefinitionImpl = (ElementDefinitionImpl)localNamedNodeMap.getNamedItem(paramString1);
      if (localElementDefinitionImpl == null)
      {
        localElementDefinitionImpl = this.fDocumentImpl.createElementDefinition(paramString1);
        ((DocumentTypeImpl)this.fDocumentType).getElements().setNamedItem(localElementDefinitionImpl);
      }
      boolean bool2 = this.fNamespaceAware;
      AttrImpl localAttrImpl;
      if (bool2)
      {
        String str2 = null;
        if ((paramString2.startsWith("xmlns:")) || (paramString2.equals("xmlns"))) {
          str2 = NamespaceContext.XMLNS_URI;
        } else if (paramString2.startsWith("xml:")) {
          str2 = NamespaceContext.XML_URI;
        }
        localAttrImpl = (AttrImpl)this.fDocumentImpl.createAttributeNS(str2, paramString2);
      }
      else
      {
        localAttrImpl = (AttrImpl)this.fDocumentImpl.createAttribute(paramString2);
      }
      localAttrImpl.setValue(paramXMLString1.toString());
      localAttrImpl.setSpecified(false);
      localAttrImpl.setIdAttribute("ID".equals(paramString3));
      if (bool2) {
        localElementDefinitionImpl.getAttributes().setNamedItemNS(localAttrImpl);
      } else {
        localElementDefinitionImpl.getAttributes().setNamedItem(localAttrImpl);
      }
    }
  }
  
  public void startAttlist(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endAttlist(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  protected Element createElementNode(QName paramQName)
  {
    Element localElement = null;
    if (this.fNamespaceAware)
    {
      if (this.fDocumentImpl != null) {
        localElement = this.fDocumentImpl.createElementNS(paramQName.uri, paramQName.rawname, paramQName.localpart);
      } else {
        localElement = this.fDocument.createElementNS(paramQName.uri, paramQName.rawname);
      }
    }
    else {
      localElement = this.fDocument.createElement(paramQName.rawname);
    }
    return localElement;
  }
  
  protected Attr createAttrNode(QName paramQName)
  {
    Attr localAttr = null;
    if (this.fNamespaceAware)
    {
      if (this.fDocumentImpl != null) {
        localAttr = this.fDocumentImpl.createAttributeNS(paramQName.uri, paramQName.rawname, paramQName.localpart);
      } else {
        localAttr = this.fDocument.createAttributeNS(paramQName.uri, paramQName.rawname);
      }
    }
    else {
      localAttr = this.fDocument.createAttribute(paramQName.rawname);
    }
    return localAttr;
  }
  
  protected void setCharacterData(boolean paramBoolean)
  {
    this.fFirstChunk = paramBoolean;
    Node localNode = this.fCurrentNode.getLastChild();
    if (localNode != null)
    {
      if (this.fStringBuilder.length() > 0)
      {
        if (localNode.getNodeType() == 3) {
          if (this.fDocumentImpl != null) {
            ((TextImpl)localNode).replaceData(this.fStringBuilder.toString());
          } else {
            ((Text)localNode).setData(this.fStringBuilder.toString());
          }
        }
        this.fStringBuilder.setLength(0);
      }
      if ((this.fDOMFilter != null) && (!this.fInEntityRef) && (localNode.getNodeType() == 3) && ((this.fDOMFilter.getWhatToShow() & 0x4) != 0))
      {
        int i = this.fDOMFilter.acceptNode(localNode);
        switch (i)
        {
        case 4: 
          throw Abort.INSTANCE;
        case 2: 
        case 3: 
          this.fCurrentNode.removeChild(localNode);
          return;
        }
      }
    }
  }
  
  public void abort()
  {
    throw Abort.INSTANCE;
  }
  
  static final class Abort
    extends RuntimeException
  {
    private static final long serialVersionUID = 1687848994976808490L;
    static final Abort INSTANCE = new Abort();
    
    private Abort() {}
    
    public Throwable fillInStackTrace()
    {
      return this;
    }
  }
}
