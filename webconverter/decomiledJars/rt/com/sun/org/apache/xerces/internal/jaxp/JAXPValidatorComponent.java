package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultXMLDocumentHandler;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerProxy;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.LocatorProxy;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

final class JAXPValidatorComponent
  extends TeeXMLDocumentFilterImpl
  implements XMLComponent
{
  private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  private final ValidatorHandler validator;
  private final XNI2SAX xni2sax = new XNI2SAX(null);
  private final SAX2XNI sax2xni = new SAX2XNI(null);
  private final TypeInfoProvider typeInfoProvider;
  private Augmentations fCurrentAug;
  private XMLAttributes fCurrentAttributes;
  private SymbolTable fSymbolTable;
  private XMLErrorReporter fErrorReporter;
  private XMLEntityResolver fEntityResolver;
  private static final TypeInfoProvider noInfoProvider = new TypeInfoProvider()
  {
    public TypeInfo getElementTypeInfo()
    {
      return null;
    }
    
    public TypeInfo getAttributeTypeInfo(int paramAnonymousInt)
    {
      return null;
    }
    
    public TypeInfo getAttributeTypeInfo(String paramAnonymousString)
    {
      return null;
    }
    
    public TypeInfo getAttributeTypeInfo(String paramAnonymousString1, String paramAnonymousString2)
    {
      return null;
    }
    
    public boolean isIdAttribute(int paramAnonymousInt)
    {
      return false;
    }
    
    public boolean isSpecified(int paramAnonymousInt)
    {
      return false;
    }
  };
  
  public JAXPValidatorComponent(ValidatorHandler paramValidatorHandler)
  {
    this.validator = paramValidatorHandler;
    TypeInfoProvider localTypeInfoProvider = paramValidatorHandler.getTypeInfoProvider();
    if (localTypeInfoProvider == null) {
      localTypeInfoProvider = noInfoProvider;
    }
    this.typeInfoProvider = localTypeInfoProvider;
    this.xni2sax.setContentHandler(this.validator);
    this.validator.setContentHandler(this.sax2xni);
    setSide(this.xni2sax);
    this.validator.setErrorHandler(new ErrorHandlerProxy()
    {
      protected XMLErrorHandler getErrorHandler()
      {
        XMLErrorHandler localXMLErrorHandler = JAXPValidatorComponent.this.fErrorReporter.getErrorHandler();
        if (localXMLErrorHandler != null) {
          return localXMLErrorHandler;
        }
        return new ErrorHandlerWrapper(JAXPValidatorComponent.DraconianErrorHandler.getInstance());
      }
    });
    this.validator.setResourceResolver(new LSResourceResolver()
    {
      public LSInput resolveResource(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, String paramAnonymousString4, String paramAnonymousString5)
      {
        if (JAXPValidatorComponent.this.fEntityResolver == null) {
          return null;
        }
        try
        {
          XMLInputSource localXMLInputSource = JAXPValidatorComponent.this.fEntityResolver.resolveEntity(new XMLResourceIdentifierImpl(paramAnonymousString3, paramAnonymousString4, paramAnonymousString5, null));
          if (localXMLInputSource == null) {
            return null;
          }
          DOMInputImpl localDOMInputImpl = new DOMInputImpl();
          localDOMInputImpl.setBaseURI(localXMLInputSource.getBaseSystemId());
          localDOMInputImpl.setByteStream(localXMLInputSource.getByteStream());
          localDOMInputImpl.setCharacterStream(localXMLInputSource.getCharacterStream());
          localDOMInputImpl.setEncoding(localXMLInputSource.getEncoding());
          localDOMInputImpl.setPublicId(localXMLInputSource.getPublicId());
          localDOMInputImpl.setSystemId(localXMLInputSource.getSystemId());
          return localDOMInputImpl;
        }
        catch (IOException localIOException)
        {
          throw new XNIException(localIOException);
        }
      }
    });
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fCurrentAttributes = paramXMLAttributes;
    this.fCurrentAug = paramAugmentations;
    this.xni2sax.startElement(paramQName, paramXMLAttributes, null);
    this.fCurrentAttributes = null;
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fCurrentAug = paramAugmentations;
    this.xni2sax.endElement(paramQName, null);
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
    this.fCurrentAug = paramAugmentations;
    this.xni2sax.characters(paramXMLString, null);
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fCurrentAug = paramAugmentations;
    this.xni2sax.ignorableWhitespace(paramXMLString, null);
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    this.fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    this.fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    try
    {
      this.fEntityResolver = ((XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      this.fEntityResolver = null;
    }
  }
  
  private void updateAttributes(Attributes paramAttributes)
  {
    int i = paramAttributes.getLength();
    for (int j = 0; j < i; j++)
    {
      String str1 = paramAttributes.getQName(j);
      int k = this.fCurrentAttributes.getIndex(str1);
      String str2 = paramAttributes.getValue(j);
      if (k == -1)
      {
        int m = str1.indexOf(':');
        String str3;
        if (m < 0) {
          str3 = null;
        } else {
          str3 = symbolize(str1.substring(0, m));
        }
        k = this.fCurrentAttributes.addAttribute(new QName(str3, symbolize(paramAttributes.getLocalName(j)), symbolize(str1), symbolize(paramAttributes.getURI(j))), paramAttributes.getType(j), str2);
      }
      else if (!str2.equals(this.fCurrentAttributes.getValue(k)))
      {
        this.fCurrentAttributes.setValue(k, str2);
      }
    }
  }
  
  private String symbolize(String paramString)
  {
    return this.fSymbolTable.addSymbol(paramString);
  }
  
  public String[] getRecognizedFeatures()
  {
    return null;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {}
  
  public String[] getRecognizedProperties()
  {
    return new String[] { "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/symbol-table" };
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {}
  
  public Boolean getFeatureDefault(String paramString)
  {
    return null;
  }
  
  public Object getPropertyDefault(String paramString)
  {
    return null;
  }
  
  private static final class DraconianErrorHandler
    implements ErrorHandler
  {
    private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();
    
    private DraconianErrorHandler() {}
    
    public static DraconianErrorHandler getInstance()
    {
      return ERROR_HANDLER_INSTANCE;
    }
    
    public void warning(SAXParseException paramSAXParseException)
      throws SAXException
    {}
    
    public void error(SAXParseException paramSAXParseException)
      throws SAXException
    {
      throw paramSAXParseException;
    }
    
    public void fatalError(SAXParseException paramSAXParseException)
      throws SAXException
    {
      throw paramSAXParseException;
    }
  }
  
  private final class SAX2XNI
    extends DefaultHandler
  {
    private final Augmentations fAugmentations = new AugmentationsImpl();
    private final QName fQName = new QName();
    
    private SAX2XNI() {}
    
    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws SAXException
    {
      try
      {
        handler().characters(new XMLString(paramArrayOfChar, paramInt1, paramInt2), aug());
      }
      catch (XNIException localXNIException)
      {
        throw toSAXException(localXNIException);
      }
    }
    
    public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws SAXException
    {
      try
      {
        handler().ignorableWhitespace(new XMLString(paramArrayOfChar, paramInt1, paramInt2), aug());
      }
      catch (XNIException localXNIException)
      {
        throw toSAXException(localXNIException);
      }
    }
    
    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
      throws SAXException
    {
      try
      {
        JAXPValidatorComponent.this.updateAttributes(paramAttributes);
        handler().startElement(toQName(paramString1, paramString2, paramString3), JAXPValidatorComponent.this.fCurrentAttributes, elementAug());
      }
      catch (XNIException localXNIException)
      {
        throw toSAXException(localXNIException);
      }
    }
    
    public void endElement(String paramString1, String paramString2, String paramString3)
      throws SAXException
    {
      try
      {
        handler().endElement(toQName(paramString1, paramString2, paramString3), aug());
      }
      catch (XNIException localXNIException)
      {
        throw toSAXException(localXNIException);
      }
    }
    
    private Augmentations elementAug()
    {
      Augmentations localAugmentations = aug();
      return localAugmentations;
    }
    
    private Augmentations aug()
    {
      if (JAXPValidatorComponent.this.fCurrentAug != null)
      {
        Augmentations localAugmentations = JAXPValidatorComponent.this.fCurrentAug;
        JAXPValidatorComponent.this.fCurrentAug = null;
        return localAugmentations;
      }
      this.fAugmentations.removeAllItems();
      return this.fAugmentations;
    }
    
    private XMLDocumentHandler handler()
    {
      return JAXPValidatorComponent.this.getDocumentHandler();
    }
    
    private SAXException toSAXException(XNIException paramXNIException)
    {
      Object localObject = paramXNIException.getException();
      if (localObject == null) {
        localObject = paramXNIException;
      }
      if ((localObject instanceof SAXException)) {
        return (SAXException)localObject;
      }
      return new SAXException((Exception)localObject);
    }
    
    private QName toQName(String paramString1, String paramString2, String paramString3)
    {
      String str = null;
      int i = paramString3.indexOf(':');
      if (i > 0) {
        str = JAXPValidatorComponent.this.symbolize(paramString3.substring(0, i));
      }
      paramString2 = JAXPValidatorComponent.this.symbolize(paramString2);
      paramString3 = JAXPValidatorComponent.this.symbolize(paramString3);
      paramString1 = JAXPValidatorComponent.this.symbolize(paramString1);
      this.fQName.setValues(str, paramString2, paramString3, paramString1);
      return this.fQName;
    }
  }
  
  private final class XNI2SAX
    extends DefaultXMLDocumentHandler
  {
    private ContentHandler fContentHandler;
    private String fVersion;
    protected NamespaceContext fNamespaceContext;
    private final AttributesProxy fAttributesProxy = new AttributesProxy(null);
    
    private XNI2SAX() {}
    
    public void setContentHandler(ContentHandler paramContentHandler)
    {
      this.fContentHandler = paramContentHandler;
    }
    
    public ContentHandler getContentHandler()
    {
      return this.fContentHandler;
    }
    
    public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
      throws XNIException
    {
      this.fVersion = paramString1;
    }
    
    public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
      throws XNIException
    {
      this.fNamespaceContext = paramNamespaceContext;
      this.fContentHandler.setDocumentLocator(new LocatorProxy(paramXMLLocator));
      try
      {
        this.fContentHandler.startDocument();
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
    
    public void endDocument(Augmentations paramAugmentations)
      throws XNIException
    {
      try
      {
        this.fContentHandler.endDocument();
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
    
    public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
      throws XNIException
    {
      try
      {
        this.fContentHandler.processingInstruction(paramString, paramXMLString.toString());
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
    
    public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
      throws XNIException
    {
      try
      {
        int i = this.fNamespaceContext.getDeclaredPrefixCount();
        if (i > 0)
        {
          str1 = null;
          str2 = null;
          for (int j = 0; j < i; j++)
          {
            str1 = this.fNamespaceContext.getDeclaredPrefixAt(j);
            str2 = this.fNamespaceContext.getURI(str1);
            this.fContentHandler.startPrefixMapping(str1, str2 == null ? "" : str2);
          }
        }
        String str1 = paramQName.uri != null ? paramQName.uri : "";
        String str2 = paramQName.localpart;
        this.fAttributesProxy.setAttributes(paramXMLAttributes);
        this.fContentHandler.startElement(str1, str2, paramQName.rawname, this.fAttributesProxy);
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
    
    public void endElement(QName paramQName, Augmentations paramAugmentations)
      throws XNIException
    {
      try
      {
        String str1 = paramQName.uri != null ? paramQName.uri : "";
        String str2 = paramQName.localpart;
        this.fContentHandler.endElement(str1, str2, paramQName.rawname);
        int i = this.fNamespaceContext.getDeclaredPrefixCount();
        if (i > 0) {
          for (int j = 0; j < i; j++) {
            this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(j));
          }
        }
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
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
      try
      {
        this.fContentHandler.characters(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
    
    public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
      throws XNIException
    {
      try
      {
        this.fContentHandler.ignorableWhitespace(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
  }
}
