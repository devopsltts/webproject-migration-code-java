package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11DocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11NSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class XML11DTDConfiguration
  extends ParserConfigurationSettings
  implements XMLPullParserConfiguration, XML11Configurable
{
  protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  protected static final String DTD_PROCESSOR = "http://apache.org/xml/properties/internal/dtd-processor";
  protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
  protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
  protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
  protected SymbolTable fSymbolTable;
  protected XMLInputSource fInputSource;
  protected ValidationManager fValidationManager;
  protected XMLVersionDetector fVersionDetector;
  protected XMLLocator fLocator;
  protected Locale fLocale;
  protected ArrayList fComponents = new ArrayList();
  protected ArrayList fXML11Components = null;
  protected ArrayList fCommonComponents = null;
  protected XMLDocumentHandler fDocumentHandler;
  protected XMLDTDHandler fDTDHandler;
  protected XMLDTDContentModelHandler fDTDContentModelHandler;
  protected XMLDocumentSource fLastComponent;
  protected boolean fParseInProgress = false;
  protected boolean fConfigUpdated = false;
  protected DTDDVFactory fDatatypeValidatorFactory;
  protected XMLNSDocumentScannerImpl fNamespaceScanner;
  protected XMLDocumentScannerImpl fNonNSScanner;
  protected XMLDTDValidator fDTDValidator;
  protected XMLDTDValidator fNonNSDTDValidator;
  protected XMLDTDScanner fDTDScanner;
  protected XMLDTDProcessor fDTDProcessor;
  protected DTDDVFactory fXML11DatatypeFactory = null;
  protected XML11NSDocumentScannerImpl fXML11NSDocScanner = null;
  protected XML11DocumentScannerImpl fXML11DocScanner = null;
  protected XML11NSDTDValidator fXML11NSDTDValidator = null;
  protected XML11DTDValidator fXML11DTDValidator = null;
  protected XML11DTDScannerImpl fXML11DTDScanner = null;
  protected XML11DTDProcessor fXML11DTDProcessor = null;
  protected XMLGrammarPool fGrammarPool;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityManager fEntityManager;
  protected XMLDocumentScanner fCurrentScanner;
  protected DTDDVFactory fCurrentDVFactory;
  protected XMLDTDScanner fCurrentDTDScanner;
  private boolean f11Initialized = false;
  
  public XML11DTDConfiguration()
  {
    this(null, null, null);
  }
  
  public XML11DTDConfiguration(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null, null);
  }
  
  public XML11DTDConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    this(paramSymbolTable, paramXMLGrammarPool, null);
  }
  
  public XML11DTDConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager)
  {
    super(paramXMLComponentManager);
    this.fFeatures = new HashMap();
    this.fProperties = new HashMap();
    String[] arrayOfString1 = { "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/internal/parser-settings" };
    addRecognizedFeatures(arrayOfString1);
    this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
    String[] arrayOfString2 = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage" };
    addRecognizedProperties(arrayOfString2);
    if (paramSymbolTable == null) {
      paramSymbolTable = new SymbolTable();
    }
    this.fSymbolTable = paramSymbolTable;
    this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
    this.fGrammarPool = paramXMLGrammarPool;
    if (this.fGrammarPool != null) {
      this.fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
    }
    this.fEntityManager = new XMLEntityManager();
    this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
    addCommonComponent(this.fEntityManager);
    this.fErrorReporter = new XMLErrorReporter();
    this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
    addCommonComponent(this.fErrorReporter);
    this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
    this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
    addComponent(this.fNamespaceScanner);
    this.fDTDScanner = new XMLDTDScannerImpl();
    this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
    addComponent((XMLComponent)this.fDTDScanner);
    this.fDTDProcessor = new XMLDTDProcessor();
    this.fProperties.put("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
    addComponent(this.fDTDProcessor);
    this.fDTDValidator = new XMLNSDTDValidator();
    this.fProperties.put("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
    addComponent(this.fDTDValidator);
    this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
    this.fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
    this.fValidationManager = new ValidationManager();
    this.fProperties.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
    this.fVersionDetector = new XMLVersionDetector();
    if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null)
    {
      XMLMessageFormatter localXMLMessageFormatter = new XMLMessageFormatter();
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", localXMLMessageFormatter);
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", localXMLMessageFormatter);
    }
    try
    {
      setLocale(Locale.getDefault());
    }
    catch (XNIException localXNIException) {}
    this.fConfigUpdated = false;
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource)
    throws XMLConfigurationException, IOException
  {
    this.fInputSource = paramXMLInputSource;
  }
  
  public void setLocale(Locale paramLocale)
    throws XNIException
  {
    this.fLocale = paramLocale;
    this.fErrorReporter.setLocale(paramLocale);
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler)
  {
    this.fDocumentHandler = paramXMLDocumentHandler;
    if (this.fLastComponent != null)
    {
      this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null) {
        this.fDocumentHandler.setDocumentSource(this.fLastComponent);
      }
    }
  }
  
  public XMLDocumentHandler getDocumentHandler()
  {
    return this.fDocumentHandler;
  }
  
  public void setDTDHandler(XMLDTDHandler paramXMLDTDHandler)
  {
    this.fDTDHandler = paramXMLDTDHandler;
  }
  
  public XMLDTDHandler getDTDHandler()
  {
    return this.fDTDHandler;
  }
  
  public void setDTDContentModelHandler(XMLDTDContentModelHandler paramXMLDTDContentModelHandler)
  {
    this.fDTDContentModelHandler = paramXMLDTDContentModelHandler;
  }
  
  public XMLDTDContentModelHandler getDTDContentModelHandler()
  {
    return this.fDTDContentModelHandler;
  }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver)
  {
    this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver);
  }
  
  public XMLEntityResolver getEntityResolver()
  {
    return (XMLEntityResolver)this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
  }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler)
  {
    this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler);
  }
  
  public XMLErrorHandler getErrorHandler()
  {
    return (XMLErrorHandler)this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
  }
  
  public void cleanup()
  {
    this.fEntityManager.closeReaders();
  }
  
  public void parse(XMLInputSource paramXMLInputSource)
    throws XNIException, IOException
  {
    if (this.fParseInProgress) {
      throw new XNIException("FWK005 parse may not be called while parsing.");
    }
    this.fParseInProgress = true;
    try
    {
      setInputSource(paramXMLInputSource);
      parse(true);
    }
    catch (XNIException localXNIException)
    {
      throw localXNIException;
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      throw new XNIException(localException);
    }
    finally
    {
      this.fParseInProgress = false;
      cleanup();
    }
  }
  
  public boolean parse(boolean paramBoolean)
    throws XNIException, IOException
  {
    if (this.fInputSource != null) {
      try
      {
        this.fValidationManager.reset();
        this.fVersionDetector.reset(this);
        resetCommon();
        short s = this.fVersionDetector.determineDocVersion(this.fInputSource);
        if (s == 2)
        {
          initXML11Components();
          configureXML11Pipeline();
          resetXML11();
        }
        else
        {
          configurePipeline();
          reset();
        }
        this.fConfigUpdated = false;
        this.fVersionDetector.startDocumentParsing((XMLEntityHandler)this.fCurrentScanner, s);
        this.fInputSource = null;
      }
      catch (XNIException localXNIException1)
      {
        throw localXNIException1;
      }
      catch (IOException localIOException1)
      {
        throw localIOException1;
      }
      catch (RuntimeException localRuntimeException1)
      {
        throw localRuntimeException1;
      }
      catch (Exception localException1)
      {
        throw new XNIException(localException1);
      }
    }
    try
    {
      return this.fCurrentScanner.scanDocument(paramBoolean);
    }
    catch (XNIException localXNIException2)
    {
      throw localXNIException2;
    }
    catch (IOException localIOException2)
    {
      throw localIOException2;
    }
    catch (RuntimeException localRuntimeException2)
    {
      throw localRuntimeException2;
    }
    catch (Exception localException2)
    {
      throw new XNIException(localException2);
    }
  }
  
  public FeatureState getFeatureState(String paramString)
    throws XMLConfigurationException
  {
    if (paramString.equals("http://apache.org/xml/features/internal/parser-settings")) {
      return FeatureState.is(this.fConfigUpdated);
    }
    return super.getFeatureState(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    this.fConfigUpdated = true;
    int i = this.fComponents.size();
    XMLComponent localXMLComponent;
    for (int j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)this.fComponents.get(j);
      localXMLComponent.setFeature(paramString, paramBoolean);
    }
    i = this.fCommonComponents.size();
    for (j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)this.fCommonComponents.get(j);
      localXMLComponent.setFeature(paramString, paramBoolean);
    }
    i = this.fXML11Components.size();
    for (j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)this.fXML11Components.get(j);
      try
      {
        localXMLComponent.setFeature(paramString, paramBoolean);
      }
      catch (Exception localException) {}
    }
    super.setFeature(paramString, paramBoolean);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    this.fConfigUpdated = true;
    int i = this.fComponents.size();
    XMLComponent localXMLComponent;
    for (int j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)this.fComponents.get(j);
      localXMLComponent.setProperty(paramString, paramObject);
    }
    i = this.fCommonComponents.size();
    for (j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)this.fCommonComponents.get(j);
      localXMLComponent.setProperty(paramString, paramObject);
    }
    i = this.fXML11Components.size();
    for (j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)this.fXML11Components.get(j);
      try
      {
        localXMLComponent.setProperty(paramString, paramObject);
      }
      catch (Exception localException) {}
    }
    super.setProperty(paramString, paramObject);
  }
  
  public Locale getLocale()
  {
    return this.fLocale;
  }
  
  protected void reset()
    throws XNIException
  {
    int i = this.fComponents.size();
    for (int j = 0; j < i; j++)
    {
      XMLComponent localXMLComponent = (XMLComponent)this.fComponents.get(j);
      localXMLComponent.reset(this);
    }
  }
  
  protected void resetCommon()
    throws XNIException
  {
    int i = this.fCommonComponents.size();
    for (int j = 0; j < i; j++)
    {
      XMLComponent localXMLComponent = (XMLComponent)this.fCommonComponents.get(j);
      localXMLComponent.reset(this);
    }
  }
  
  protected void resetXML11()
    throws XNIException
  {
    int i = this.fXML11Components.size();
    for (int j = 0; j < i; j++)
    {
      XMLComponent localXMLComponent = (XMLComponent)this.fXML11Components.get(j);
      localXMLComponent.reset(this);
    }
  }
  
  protected void configureXML11Pipeline()
  {
    if (this.fCurrentDVFactory != this.fXML11DatatypeFactory)
    {
      this.fCurrentDVFactory = this.fXML11DatatypeFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
    }
    if (this.fCurrentDTDScanner != this.fXML11DTDScanner)
    {
      this.fCurrentDTDScanner = this.fXML11DTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
      setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fXML11DTDProcessor);
    }
    this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
    this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
    this.fXML11DTDProcessor.setDTDHandler(this.fDTDHandler);
    if (this.fDTDHandler != null) {
      this.fDTDHandler.setDTDSource(this.fXML11DTDProcessor);
    }
    this.fXML11DTDScanner.setDTDContentModelHandler(this.fXML11DTDProcessor);
    this.fXML11DTDProcessor.setDTDContentModelSource(this.fXML11DTDScanner);
    this.fXML11DTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
    if (this.fDTDContentModelHandler != null) {
      this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDProcessor);
    }
    if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE)
    {
      if (this.fCurrentScanner != this.fXML11NSDocScanner)
      {
        this.fCurrentScanner = this.fXML11NSDocScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11NSDocScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11NSDTDValidator);
      }
      this.fXML11NSDocScanner.setDTDValidator(this.fXML11NSDTDValidator);
      this.fXML11NSDocScanner.setDocumentHandler(this.fXML11NSDTDValidator);
      this.fXML11NSDTDValidator.setDocumentSource(this.fXML11NSDocScanner);
      this.fXML11NSDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null) {
        this.fDocumentHandler.setDocumentSource(this.fXML11NSDTDValidator);
      }
      this.fLastComponent = this.fXML11NSDTDValidator;
    }
    else
    {
      if (this.fXML11DocScanner == null)
      {
        this.fXML11DocScanner = new XML11DocumentScannerImpl();
        addXML11Component(this.fXML11DocScanner);
        this.fXML11DTDValidator = new XML11DTDValidator();
        addXML11Component(this.fXML11DTDValidator);
      }
      if (this.fCurrentScanner != this.fXML11DocScanner)
      {
        this.fCurrentScanner = this.fXML11DocScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11DocScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11DTDValidator);
      }
      this.fXML11DocScanner.setDocumentHandler(this.fXML11DTDValidator);
      this.fXML11DTDValidator.setDocumentSource(this.fXML11DocScanner);
      this.fXML11DTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null) {
        this.fDocumentHandler.setDocumentSource(this.fXML11DTDValidator);
      }
      this.fLastComponent = this.fXML11DTDValidator;
    }
  }
  
  protected void configurePipeline()
  {
    if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory)
    {
      this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
    }
    if (this.fCurrentDTDScanner != this.fDTDScanner)
    {
      this.fCurrentDTDScanner = this.fDTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
      setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
    }
    this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
    this.fDTDProcessor.setDTDSource(this.fDTDScanner);
    this.fDTDProcessor.setDTDHandler(this.fDTDHandler);
    if (this.fDTDHandler != null) {
      this.fDTDHandler.setDTDSource(this.fDTDProcessor);
    }
    this.fDTDScanner.setDTDContentModelHandler(this.fDTDProcessor);
    this.fDTDProcessor.setDTDContentModelSource(this.fDTDScanner);
    this.fDTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
    if (this.fDTDContentModelHandler != null) {
      this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDProcessor);
    }
    if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE)
    {
      if (this.fCurrentScanner != this.fNamespaceScanner)
      {
        this.fCurrentScanner = this.fNamespaceScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
      }
      this.fNamespaceScanner.setDTDValidator(this.fDTDValidator);
      this.fNamespaceScanner.setDocumentHandler(this.fDTDValidator);
      this.fDTDValidator.setDocumentSource(this.fNamespaceScanner);
      this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null) {
        this.fDocumentHandler.setDocumentSource(this.fDTDValidator);
      }
      this.fLastComponent = this.fDTDValidator;
    }
    else
    {
      if (this.fNonNSScanner == null)
      {
        this.fNonNSScanner = new XMLDocumentScannerImpl();
        this.fNonNSDTDValidator = new XMLDTDValidator();
        addComponent(this.fNonNSScanner);
        addComponent(this.fNonNSDTDValidator);
      }
      if (this.fCurrentScanner != this.fNonNSScanner)
      {
        this.fCurrentScanner = this.fNonNSScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fNonNSDTDValidator);
      }
      this.fNonNSScanner.setDocumentHandler(this.fNonNSDTDValidator);
      this.fNonNSDTDValidator.setDocumentSource(this.fNonNSScanner);
      this.fNonNSDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null) {
        this.fDocumentHandler.setDocumentSource(this.fNonNSDTDValidator);
      }
      this.fLastComponent = this.fNonNSDTDValidator;
    }
  }
  
  protected FeatureState checkFeature(String paramString)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/features/"))
    {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if ((i == "validation/dynamic".length()) && (paramString.endsWith("validation/dynamic"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/default-attribute-values".length()) && (paramString.endsWith("validation/default-attribute-values"))) {
        return FeatureState.NOT_SUPPORTED;
      }
      if ((i == "validation/validate-content-models".length()) && (paramString.endsWith("validation/validate-content-models"))) {
        return FeatureState.NOT_SUPPORTED;
      }
      if ((i == "nonvalidating/load-dtd-grammar".length()) && (paramString.endsWith("nonvalidating/load-dtd-grammar"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "nonvalidating/load-external-dtd".length()) && (paramString.endsWith("nonvalidating/load-external-dtd"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/validate-datatypes".length()) && (paramString.endsWith("validation/validate-datatypes"))) {
        return FeatureState.NOT_SUPPORTED;
      }
      if ((i == "internal/parser-settings".length()) && (paramString.endsWith("internal/parser-settings"))) {
        return FeatureState.NOT_SUPPORTED;
      }
    }
    return super.checkFeature(paramString);
  }
  
  protected PropertyState checkProperty(String paramString)
    throws XMLConfigurationException
  {
    int i;
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      i = paramString.length() - "http://apache.org/xml/properties/".length();
      if ((i == "internal/dtd-scanner".length()) && (paramString.endsWith("internal/dtd-scanner"))) {
        return PropertyState.RECOGNIZED;
      }
    }
    if (paramString.startsWith("http://xml.org/sax/properties/"))
    {
      i = paramString.length() - "http://xml.org/sax/properties/".length();
      if ((i == "xml-string".length()) && (paramString.endsWith("xml-string"))) {
        return PropertyState.NOT_SUPPORTED;
      }
    }
    return super.checkProperty(paramString);
  }
  
  protected void addComponent(XMLComponent paramXMLComponent)
  {
    if (this.fComponents.contains(paramXMLComponent)) {
      return;
    }
    this.fComponents.add(paramXMLComponent);
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addCommonComponent(XMLComponent paramXMLComponent)
  {
    if (this.fCommonComponents.contains(paramXMLComponent)) {
      return;
    }
    this.fCommonComponents.add(paramXMLComponent);
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addXML11Component(XMLComponent paramXMLComponent)
  {
    if (this.fXML11Components.contains(paramXMLComponent)) {
      return;
    }
    this.fXML11Components.add(paramXMLComponent);
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addRecognizedParamsAndSetDefaults(XMLComponent paramXMLComponent)
  {
    String[] arrayOfString1 = paramXMLComponent.getRecognizedFeatures();
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = paramXMLComponent.getRecognizedProperties();
    addRecognizedProperties(arrayOfString2);
    int i;
    String str;
    Object localObject;
    if (arrayOfString1 != null) {
      for (i = 0; i < arrayOfString1.length; i++)
      {
        str = arrayOfString1[i];
        localObject = paramXMLComponent.getFeatureDefault(str);
        if ((localObject != null) && (!this.fFeatures.containsKey(str)))
        {
          this.fFeatures.put(str, localObject);
          this.fConfigUpdated = true;
        }
      }
    }
    if (arrayOfString2 != null) {
      for (i = 0; i < arrayOfString2.length; i++)
      {
        str = arrayOfString2[i];
        localObject = paramXMLComponent.getPropertyDefault(str);
        if ((localObject != null) && (!this.fProperties.containsKey(str)))
        {
          this.fProperties.put(str, localObject);
          this.fConfigUpdated = true;
        }
      }
    }
  }
  
  private void initXML11Components()
  {
    if (!this.f11Initialized)
    {
      this.fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
      this.fXML11DTDScanner = new XML11DTDScannerImpl();
      addXML11Component(this.fXML11DTDScanner);
      this.fXML11DTDProcessor = new XML11DTDProcessor();
      addXML11Component(this.fXML11DTDProcessor);
      this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
      addXML11Component(this.fXML11NSDocScanner);
      this.fXML11NSDTDValidator = new XML11NSDTDValidator();
      addXML11Component(this.fXML11NSDTDValidator);
      this.f11Initialized = true;
    }
  }
}
