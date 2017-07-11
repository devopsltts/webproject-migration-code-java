package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.ItemPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.EntityResolver2;

final class ValidatorHandlerImpl
  extends ValidatorHandler
  implements DTDHandler, EntityState, PSVIProvider, ValidatorHelper, XMLDocumentHandler
{
  private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
  protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  private XMLErrorReporter fErrorReporter;
  private NamespaceContext fNamespaceContext;
  private XMLSchemaValidator fSchemaValidator;
  private SymbolTable fSymbolTable;
  private ValidationManager fValidationManager;
  private XMLSchemaValidatorComponentManager fComponentManager;
  private final SAXLocatorWrapper fSAXLocatorWrapper = new SAXLocatorWrapper();
  private boolean fNeedPushNSContext = true;
  private HashMap fUnparsedEntities = null;
  private boolean fStringsInternalized = false;
  private final QName fElementQName = new QName();
  private final QName fAttributeQName = new QName();
  private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
  private final AttributesProxy fAttrAdapter = new AttributesProxy(this.fAttributes);
  private final XMLString fTempString = new XMLString();
  private ContentHandler fContentHandler = null;
  private final XMLSchemaTypeInfoProvider fTypeInfoProvider = new XMLSchemaTypeInfoProvider(null);
  private final ResolutionForwarder fResolutionForwarder = new ResolutionForwarder(null);
  
  public ValidatorHandlerImpl(XSGrammarPoolContainer paramXSGrammarPoolContainer)
  {
    this(new XMLSchemaValidatorComponentManager(paramXSGrammarPoolContainer));
    this.fComponentManager.addRecognizedFeatures(new String[] { "http://xml.org/sax/features/namespace-prefixes" });
    this.fComponentManager.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    setErrorHandler(null);
    setResourceResolver(null);
  }
  
  public ValidatorHandlerImpl(XMLSchemaValidatorComponentManager paramXMLSchemaValidatorComponentManager)
  {
    this.fComponentManager = paramXMLSchemaValidatorComponentManager;
    this.fErrorReporter = ((XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    this.fNamespaceContext = ((NamespaceContext)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context"));
    this.fSchemaValidator = ((XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema"));
    this.fSymbolTable = ((SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    this.fValidationManager = ((ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    this.fContentHandler = paramContentHandler;
  }
  
  public ContentHandler getContentHandler()
  {
    return this.fContentHandler;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    this.fComponentManager.setErrorHandler(paramErrorHandler);
  }
  
  public ErrorHandler getErrorHandler()
  {
    return this.fComponentManager.getErrorHandler();
  }
  
  public void setResourceResolver(LSResourceResolver paramLSResourceResolver)
  {
    this.fComponentManager.setResourceResolver(paramLSResourceResolver);
  }
  
  public LSResourceResolver getResourceResolver()
  {
    return this.fComponentManager.getResourceResolver();
  }
  
  public TypeInfoProvider getTypeInfoProvider()
  {
    return this.fTypeInfoProvider;
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    try
    {
      return this.fComponentManager.getFeature(paramString);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str1 = localXMLConfigurationException.getIdentifier();
      String str2 = localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED ? "feature-not-recognized" : "feature-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    }
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    try
    {
      this.fComponentManager.setFeature(paramString, paramBoolean);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str1 = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_ALLOWED) {
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "jaxp-secureprocessing-feature", null));
      }
      String str2;
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        str2 = "feature-not-recognized";
      } else {
        str2 = "feature-not-supported";
      }
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    }
  }
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    try
    {
      return this.fComponentManager.getProperty(paramString);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str1 = localXMLConfigurationException.getIdentifier();
      String str2 = localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    try
    {
      this.fComponentManager.setProperty(paramString, paramObject);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str1 = localXMLConfigurationException.getIdentifier();
      String str2 = localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    }
  }
  
  public boolean isEntityDeclared(String paramString)
  {
    return false;
  }
  
  public boolean isEntityUnparsed(String paramString)
  {
    if (this.fUnparsedEntities != null) {
      return this.fUnparsedEntities.containsKey(paramString);
    }
    return false;
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fContentHandler != null) {
      try
      {
        this.fContentHandler.startDocument();
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fContentHandler != null) {
      try
      {
        this.fContentHandler.processingInstruction(paramString, paramXMLString.toString());
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fContentHandler != null) {
      try
      {
        this.fTypeInfoProvider.beginStartElement(paramAugmentations, paramXMLAttributes);
        this.fContentHandler.startElement(paramQName.uri != null ? paramQName.uri : XMLSymbols.EMPTY_STRING, paramQName.localpart, paramQName.rawname, this.fAttrAdapter);
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
      finally
      {
        this.fTypeInfoProvider.finishStartElement();
      }
    }
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fContentHandler != null)
    {
      if (paramXMLString.length == 0) {
        return;
      }
      try
      {
        this.fContentHandler.characters(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fContentHandler != null) {
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
  
  public void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fContentHandler != null) {
      try
      {
        this.fTypeInfoProvider.beginEndElement(paramAugmentations);
        this.fContentHandler.endElement(paramQName.uri != null ? paramQName.uri : XMLSymbols.EMPTY_STRING, paramQName.localpart, paramQName.rawname);
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
      finally
      {
        this.fTypeInfoProvider.finishEndElement();
      }
    }
  }
  
  public void startCDATA(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endCDATA(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endDocument(Augmentations paramAugmentations)
    throws XNIException
  {
    if (this.fContentHandler != null) {
      try
      {
        this.fContentHandler.endDocument();
      }
      catch (SAXException localSAXException)
      {
        throw new XNIException(localSAXException);
      }
    }
  }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) {}
  
  public XMLDocumentSource getDocumentSource()
  {
    return this.fSchemaValidator;
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    this.fSAXLocatorWrapper.setLocator(paramLocator);
    if (this.fContentHandler != null) {
      this.fContentHandler.setDocumentLocator(paramLocator);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    this.fComponentManager.reset();
    this.fSchemaValidator.setDocumentHandler(this);
    this.fValidationManager.setEntityState(this);
    this.fTypeInfoProvider.finishStartElement();
    this.fNeedPushNSContext = true;
    if ((this.fUnparsedEntities != null) && (!this.fUnparsedEntities.isEmpty())) {
      this.fUnparsedEntities.clear();
    }
    this.fErrorReporter.setDocumentLocator(this.fSAXLocatorWrapper);
    try
    {
      this.fSchemaValidator.startDocument(this.fSAXLocatorWrapper, this.fSAXLocatorWrapper.getEncoding(), this.fNamespaceContext, null);
    }
    catch (XMLParseException localXMLParseException)
    {
      throw Util.toSAXParseException(localXMLParseException);
    }
    catch (XNIException localXNIException)
    {
      throw Util.toSAXException(localXNIException);
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    this.fSAXLocatorWrapper.setLocator(null);
    try
    {
      this.fSchemaValidator.endDocument(null);
    }
    catch (XMLParseException localXMLParseException)
    {
      throw Util.toSAXParseException(localXMLParseException);
    }
    catch (XNIException localXNIException)
    {
      throw Util.toSAXException(localXNIException);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    String str1;
    String str2;
    if (!this.fStringsInternalized)
    {
      str1 = paramString1 != null ? this.fSymbolTable.addSymbol(paramString1) : XMLSymbols.EMPTY_STRING;
      str2 = (paramString2 != null) && (paramString2.length() > 0) ? this.fSymbolTable.addSymbol(paramString2) : null;
    }
    else
    {
      str1 = paramString1 != null ? paramString1 : XMLSymbols.EMPTY_STRING;
      str2 = (paramString2 != null) && (paramString2.length() > 0) ? paramString2 : null;
    }
    if (this.fNeedPushNSContext)
    {
      this.fNeedPushNSContext = false;
      this.fNamespaceContext.pushContext();
    }
    this.fNamespaceContext.declarePrefix(str1, str2);
    if (this.fContentHandler != null) {
      this.fContentHandler.startPrefixMapping(paramString1, paramString2);
    }
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    if (this.fContentHandler != null) {
      this.fContentHandler.endPrefixMapping(paramString);
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (this.fNeedPushNSContext) {
      this.fNamespaceContext.pushContext();
    }
    this.fNeedPushNSContext = true;
    fillQName(this.fElementQName, paramString1, paramString2, paramString3);
    if ((paramAttributes instanceof Attributes2)) {
      fillXMLAttributes2((Attributes2)paramAttributes);
    } else {
      fillXMLAttributes(paramAttributes);
    }
    try
    {
      this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
    }
    catch (XMLParseException localXMLParseException)
    {
      throw Util.toSAXParseException(localXMLParseException);
    }
    catch (XNIException localXNIException)
    {
      throw Util.toSAXException(localXNIException);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    fillQName(this.fElementQName, paramString1, paramString2, paramString3);
    try
    {
      this.fSchemaValidator.endElement(this.fElementQName, null);
    }
    catch (XMLParseException localXMLParseException)
    {
      throw Util.toSAXParseException(localXMLParseException);
    }
    catch (XNIException localXNIException)
    {
      throw Util.toSAXException(localXNIException);
    }
    finally
    {
      this.fNamespaceContext.popContext();
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      this.fTempString.setValues(paramArrayOfChar, paramInt1, paramInt2);
      this.fSchemaValidator.characters(this.fTempString, null);
    }
    catch (XMLParseException localXMLParseException)
    {
      throw Util.toSAXParseException(localXMLParseException);
    }
    catch (XNIException localXNIException)
    {
      throw Util.toSAXException(localXNIException);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      this.fTempString.setValues(paramArrayOfChar, paramInt1, paramInt2);
      this.fSchemaValidator.ignorableWhitespace(this.fTempString, null);
    }
    catch (XMLParseException localXMLParseException)
    {
      throw Util.toSAXParseException(localXMLParseException);
    }
    catch (XNIException localXNIException)
    {
      throw Util.toSAXException(localXNIException);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (this.fContentHandler != null) {
      this.fContentHandler.processingInstruction(paramString1, paramString2);
    }
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    if (this.fContentHandler != null) {
      this.fContentHandler.skippedEntity(paramString);
    }
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    if (this.fUnparsedEntities == null) {
      this.fUnparsedEntities = new HashMap();
    }
    this.fUnparsedEntities.put(paramString1, paramString1);
  }
  
  public void validate(Source paramSource, Result paramResult)
    throws SAXException, IOException
  {
    if (((paramResult instanceof SAXResult)) || (paramResult == null))
    {
      SAXSource localSAXSource = (SAXSource)paramSource;
      SAXResult localSAXResult = (SAXResult)paramResult;
      if (paramResult != null) {
        setContentHandler(localSAXResult.getHandler());
      }
      try
      {
        XMLReader localXMLReader = localSAXSource.getXMLReader();
        if (localXMLReader == null)
        {
          SAXParserFactoryImpl localSAXParserFactoryImpl = this.fComponentManager.getFeature("http://www.oracle.com/feature/use-service-mechanism") ? SAXParserFactory.newInstance() : new SAXParserFactoryImpl();
          localSAXParserFactoryImpl.setNamespaceAware(true);
          try
          {
            localSAXParserFactoryImpl.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this.fComponentManager.getFeature("http://javax.xml.XMLConstants/feature/secure-processing"));
            localXMLReader = localSAXParserFactoryImpl.newSAXParser().getXMLReader();
            if ((localXMLReader instanceof com.sun.org.apache.xerces.internal.parsers.SAXParser))
            {
              XMLSecurityManager localXMLSecurityManager = (XMLSecurityManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
              if (localXMLSecurityManager != null) {
                try
                {
                  localXMLReader.setProperty("http://apache.org/xml/properties/security-manager", localXMLSecurityManager);
                }
                catch (SAXException localSAXException2) {}
              }
              try
              {
                XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)this.fComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
                localXMLReader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", localXMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD));
              }
              catch (SAXException localSAXException3)
              {
                System.err.println("Warning: " + localXMLReader.getClass().getName() + ": " + localSAXException3.getMessage());
              }
            }
          }
          catch (Exception localException)
          {
            throw new FactoryConfigurationError(localException);
          }
        }
        try
        {
          this.fStringsInternalized = localXMLReader.getFeature("http://xml.org/sax/features/string-interning");
        }
        catch (SAXException localSAXException1)
        {
          this.fStringsInternalized = false;
        }
        ErrorHandler localErrorHandler = this.fComponentManager.getErrorHandler();
        localXMLReader.setErrorHandler(localErrorHandler != null ? localErrorHandler : DraconianErrorHandler.getInstance());
        localXMLReader.setEntityResolver(this.fResolutionForwarder);
        this.fResolutionForwarder.setEntityResolver(this.fComponentManager.getResourceResolver());
        localXMLReader.setContentHandler(this);
        localXMLReader.setDTDHandler(this);
        InputSource localInputSource = localSAXSource.getInputSource();
        localXMLReader.parse(localInputSource);
      }
      finally
      {
        setContentHandler(null);
      }
      return;
    }
    throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { paramSource.getClass().getName(), paramResult.getClass().getName() }));
  }
  
  public ElementPSVI getElementPSVI()
  {
    return this.fTypeInfoProvider.getElementPSVI();
  }
  
  public AttributePSVI getAttributePSVI(int paramInt)
  {
    return this.fTypeInfoProvider.getAttributePSVI(paramInt);
  }
  
  public AttributePSVI getAttributePSVIByName(String paramString1, String paramString2)
  {
    return this.fTypeInfoProvider.getAttributePSVIByName(paramString1, paramString2);
  }
  
  private void fillQName(QName paramQName, String paramString1, String paramString2, String paramString3)
  {
    if (!this.fStringsInternalized)
    {
      paramString1 = (paramString1 != null) && (paramString1.length() > 0) ? this.fSymbolTable.addSymbol(paramString1) : null;
      paramString2 = paramString2 != null ? this.fSymbolTable.addSymbol(paramString2) : XMLSymbols.EMPTY_STRING;
      paramString3 = paramString3 != null ? this.fSymbolTable.addSymbol(paramString3) : XMLSymbols.EMPTY_STRING;
    }
    else
    {
      if ((paramString1 != null) && (paramString1.length() == 0)) {
        paramString1 = null;
      }
      if (paramString2 == null) {
        paramString2 = XMLSymbols.EMPTY_STRING;
      }
      if (paramString3 == null) {
        paramString3 = XMLSymbols.EMPTY_STRING;
      }
    }
    String str = XMLSymbols.EMPTY_STRING;
    int i = paramString3.indexOf(':');
    if (i != -1) {
      str = this.fSymbolTable.addSymbol(paramString3.substring(0, i));
    }
    paramQName.setValues(str, paramString2, paramString3, paramString1);
  }
  
  private void fillXMLAttributes(Attributes paramAttributes)
  {
    this.fAttributes.removeAllAttributes();
    int i = paramAttributes.getLength();
    for (int j = 0; j < i; j++)
    {
      fillXMLAttribute(paramAttributes, j);
      this.fAttributes.setSpecified(j, true);
    }
  }
  
  private void fillXMLAttributes2(Attributes2 paramAttributes2)
  {
    this.fAttributes.removeAllAttributes();
    int i = paramAttributes2.getLength();
    for (int j = 0; j < i; j++)
    {
      fillXMLAttribute(paramAttributes2, j);
      this.fAttributes.setSpecified(j, paramAttributes2.isSpecified(j));
      if (paramAttributes2.isDeclared(j)) {
        this.fAttributes.getAugmentations(j).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
      }
    }
  }
  
  private void fillXMLAttribute(Attributes paramAttributes, int paramInt)
  {
    fillQName(this.fAttributeQName, paramAttributes.getURI(paramInt), paramAttributes.getLocalName(paramInt), paramAttributes.getQName(paramInt));
    String str = paramAttributes.getType(paramInt);
    this.fAttributes.addAttributeNS(this.fAttributeQName, str != null ? str : XMLSymbols.fCDATASymbol, paramAttributes.getValue(paramInt));
  }
  
  static final class ResolutionForwarder
    implements EntityResolver2
  {
    private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
    protected LSResourceResolver fEntityResolver;
    
    public ResolutionForwarder() {}
    
    public ResolutionForwarder(LSResourceResolver paramLSResourceResolver)
    {
      setEntityResolver(paramLSResourceResolver);
    }
    
    public void setEntityResolver(LSResourceResolver paramLSResourceResolver)
    {
      this.fEntityResolver = paramLSResourceResolver;
    }
    
    public LSResourceResolver getEntityResolver()
    {
      return this.fEntityResolver;
    }
    
    public InputSource getExternalSubset(String paramString1, String paramString2)
      throws SAXException, IOException
    {
      return null;
    }
    
    public InputSource resolveEntity(String paramString1, String paramString2, String paramString3, String paramString4)
      throws SAXException, IOException
    {
      if (this.fEntityResolver != null)
      {
        LSInput localLSInput = this.fEntityResolver.resolveResource("http://www.w3.org/TR/REC-xml", null, paramString2, paramString4, paramString3);
        if (localLSInput != null)
        {
          String str1 = localLSInput.getPublicId();
          String str2 = localLSInput.getSystemId();
          String str3 = localLSInput.getBaseURI();
          Reader localReader = localLSInput.getCharacterStream();
          InputStream localInputStream = localLSInput.getByteStream();
          String str4 = localLSInput.getStringData();
          String str5 = localLSInput.getEncoding();
          InputSource localInputSource = new InputSource();
          localInputSource.setPublicId(str1);
          localInputSource.setSystemId(str3 != null ? resolveSystemId(paramString4, str3) : paramString4);
          if (localReader != null) {
            localInputSource.setCharacterStream(localReader);
          } else if (localInputStream != null) {
            localInputSource.setByteStream(localInputStream);
          } else if ((str4 != null) && (str4.length() != 0)) {
            localInputSource.setCharacterStream(new StringReader(str4));
          }
          localInputSource.setEncoding(str5);
          return localInputSource;
        }
      }
      return null;
    }
    
    public InputSource resolveEntity(String paramString1, String paramString2)
      throws SAXException, IOException
    {
      return resolveEntity(null, paramString1, null, paramString2);
    }
    
    private String resolveSystemId(String paramString1, String paramString2)
    {
      try
      {
        return XMLEntityManager.expandSystemId(paramString1, paramString2, false);
      }
      catch (URI.MalformedURIException localMalformedURIException) {}
      return paramString1;
    }
  }
  
  private class XMLSchemaTypeInfoProvider
    extends TypeInfoProvider
  {
    private Augmentations fElementAugs;
    private XMLAttributes fAttributes;
    private boolean fInStartElement = false;
    private boolean fInEndElement = false;
    
    private XMLSchemaTypeInfoProvider() {}
    
    void beginStartElement(Augmentations paramAugmentations, XMLAttributes paramXMLAttributes)
    {
      this.fInStartElement = true;
      this.fElementAugs = paramAugmentations;
      this.fAttributes = paramXMLAttributes;
    }
    
    void finishStartElement()
    {
      this.fInStartElement = false;
      this.fElementAugs = null;
      this.fAttributes = null;
    }
    
    void beginEndElement(Augmentations paramAugmentations)
    {
      this.fInEndElement = true;
      this.fElementAugs = paramAugmentations;
    }
    
    void finishEndElement()
    {
      this.fInEndElement = false;
      this.fElementAugs = null;
    }
    
    private void checkState(boolean paramBoolean)
    {
      if ((!this.fInStartElement) && ((!this.fInEndElement) || (!paramBoolean))) {
        throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalState", null));
      }
    }
    
    public TypeInfo getAttributeTypeInfo(int paramInt)
    {
      checkState(false);
      return getAttributeType(paramInt);
    }
    
    private TypeInfo getAttributeType(int paramInt)
    {
      checkState(false);
      if ((paramInt < 0) || (this.fAttributes.getLength() <= paramInt)) {
        throw new IndexOutOfBoundsException(Integer.toString(paramInt));
      }
      Augmentations localAugmentations = this.fAttributes.getAugmentations(paramInt);
      if (localAugmentations == null) {
        return null;
      }
      AttributePSVI localAttributePSVI = (AttributePSVI)localAugmentations.getItem("ATTRIBUTE_PSVI");
      return getTypeInfoFromPSVI(localAttributePSVI);
    }
    
    public TypeInfo getAttributeTypeInfo(String paramString1, String paramString2)
    {
      checkState(false);
      return getAttributeTypeInfo(this.fAttributes.getIndex(paramString1, paramString2));
    }
    
    public TypeInfo getAttributeTypeInfo(String paramString)
    {
      checkState(false);
      return getAttributeTypeInfo(this.fAttributes.getIndex(paramString));
    }
    
    public TypeInfo getElementTypeInfo()
    {
      checkState(true);
      if (this.fElementAugs == null) {
        return null;
      }
      ElementPSVI localElementPSVI = (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI");
      return getTypeInfoFromPSVI(localElementPSVI);
    }
    
    private TypeInfo getTypeInfoFromPSVI(ItemPSVI paramItemPSVI)
    {
      if (paramItemPSVI == null) {
        return null;
      }
      if (paramItemPSVI.getValidity() == 2)
      {
        localObject = paramItemPSVI.getMemberTypeDefinition();
        if (localObject != null) {
          return (localObject instanceof TypeInfo) ? (TypeInfo)localObject : null;
        }
      }
      Object localObject = paramItemPSVI.getTypeDefinition();
      if (localObject != null) {
        return (localObject instanceof TypeInfo) ? (TypeInfo)localObject : null;
      }
      return null;
    }
    
    public boolean isIdAttribute(int paramInt)
    {
      checkState(false);
      XSSimpleType localXSSimpleType = (XSSimpleType)getAttributeType(paramInt);
      if (localXSSimpleType == null) {
        return false;
      }
      return localXSSimpleType.isIDType();
    }
    
    public boolean isSpecified(int paramInt)
    {
      checkState(false);
      return this.fAttributes.isSpecified(paramInt);
    }
    
    ElementPSVI getElementPSVI()
    {
      return this.fElementAugs != null ? (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI") : null;
    }
    
    AttributePSVI getAttributePSVI(int paramInt)
    {
      if (this.fAttributes != null)
      {
        Augmentations localAugmentations = this.fAttributes.getAugmentations(paramInt);
        if (localAugmentations != null) {
          return (AttributePSVI)localAugmentations.getItem("ATTRIBUTE_PSVI");
        }
      }
      return null;
    }
    
    AttributePSVI getAttributePSVIByName(String paramString1, String paramString2)
    {
      if (this.fAttributes != null)
      {
        Augmentations localAugmentations = this.fAttributes.getAugmentations(paramString1, paramString2);
        if (localAugmentations != null) {
          return (AttributePSVI)localAugmentations.getItem("ATTRIBUTE_PSVI");
        }
      }
      return null;
    }
  }
}
