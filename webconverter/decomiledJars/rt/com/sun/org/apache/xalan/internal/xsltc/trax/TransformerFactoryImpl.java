package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.XalanConstants;
import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import com.sun.org.apache.xalan.internal.utils.FeatureManager.Feature;
import com.sun.org.apache.xalan.internal.utils.FeaturePropertyBase.State;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.State;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xml.internal.utils.StopParseException;
import com.sun.org.apache.xml.internal.utils.StylesheetPIHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TransformerFactoryImpl
  extends SAXTransformerFactory
  implements SourceLoader, ErrorListener
{
  public static final String TRANSLET_NAME = "translet-name";
  public static final String DESTINATION_DIRECTORY = "destination-directory";
  public static final String PACKAGE_NAME = "package-name";
  public static final String JAR_NAME = "jar-name";
  public static final String GENERATE_TRANSLET = "generate-translet";
  public static final String AUTO_TRANSLET = "auto-translet";
  public static final String USE_CLASSPATH = "use-classpath";
  public static final String DEBUG = "debug";
  public static final String ENABLE_INLINING = "enable-inlining";
  public static final String INDENT_NUMBER = "indent-number";
  private ErrorListener _errorListener = this;
  private URIResolver _uriResolver = null;
  protected static final String DEFAULT_TRANSLET_NAME = "GregorSamsa";
  private String _transletName = "GregorSamsa";
  private String _destinationDirectory = null;
  private String _packageName = null;
  private String _jarFileName = null;
  private Map<Source, PIParamWrapper> _piParams = null;
  private boolean _debug = false;
  private boolean _enableInlining = false;
  private boolean _generateTranslet = false;
  private boolean _autoTranslet = false;
  private boolean _useClasspath = false;
  private int _indentNumber = -1;
  private boolean _isNotSecureProcessing = true;
  private boolean _isSecureMode = false;
  private boolean _useServicesMechanism;
  private String _accessExternalStylesheet = "all";
  private String _accessExternalDTD = "all";
  private XMLSecurityPropertyManager _xmlSecurityPropertyMgr;
  private XMLSecurityManager _xmlSecurityManager;
  private final FeatureManager _featureManager;
  private ClassLoader _extensionClassLoader = null;
  private Map<String, Class> _xsltcExtensionFunctions;
  
  public TransformerFactoryImpl()
  {
    this(true);
  }
  
  public static TransformerFactory newTransformerFactoryNoServiceLoader()
  {
    return new TransformerFactoryImpl(false);
  }
  
  private TransformerFactoryImpl(boolean paramBoolean)
  {
    this._useServicesMechanism = paramBoolean;
    this._featureManager = new FeatureManager();
    if (System.getSecurityManager() != null)
    {
      this._isSecureMode = true;
      this._isNotSecureProcessing = false;
      this._featureManager.setValue(FeatureManager.Feature.ORACLE_ENABLE_EXTENSION_FUNCTION, FeaturePropertyBase.State.FSP, "false");
    }
    this._xmlSecurityPropertyMgr = new XMLSecurityPropertyManager();
    this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
    this._xmlSecurityManager = new XMLSecurityManager(true);
    this._xsltcExtensionFunctions = null;
  }
  
  public Map<String, Class> getExternalExtensionsMap()
  {
    return this._xsltcExtensionFunctions;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener)
    throws IllegalArgumentException
  {
    if (paramErrorListener == null)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "TransformerFactory");
      throw new IllegalArgumentException(localErrorMsg.toString());
    }
    this._errorListener = paramErrorListener;
  }
  
  public ErrorListener getErrorListener()
  {
    return this._errorListener;
  }
  
  public Object getAttribute(String paramString)
    throws IllegalArgumentException
  {
    if (paramString.equals("translet-name")) {
      return this._transletName;
    }
    if (paramString.equals("generate-translet")) {
      return new Boolean(this._generateTranslet);
    }
    if (paramString.equals("auto-translet")) {
      return new Boolean(this._autoTranslet);
    }
    if (paramString.equals("enable-inlining"))
    {
      if (this._enableInlining) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      return this._xmlSecurityManager;
    }
    if (paramString.equals("jdk.xml.transform.extensionClassLoader")) {
      return this._extensionClassLoader;
    }
    Object localObject = this._xmlSecurityManager != null ? this._xmlSecurityManager.getLimitAsString(paramString) : null;
    if (localObject != null) {
      return localObject;
    }
    localObject = this._xmlSecurityPropertyMgr != null ? this._xmlSecurityPropertyMgr.getValue(paramString) : null;
    if (localObject != null) {
      return localObject;
    }
    ErrorMsg localErrorMsg = new ErrorMsg("JAXP_INVALID_ATTR_ERR", paramString);
    throw new IllegalArgumentException(localErrorMsg.toString());
  }
  
  public void setAttribute(String paramString, Object paramObject)
    throws IllegalArgumentException
  {
    if ((paramString.equals("translet-name")) && ((paramObject instanceof String)))
    {
      this._transletName = ((String)paramObject);
      return;
    }
    if ((paramString.equals("destination-directory")) && ((paramObject instanceof String)))
    {
      this._destinationDirectory = ((String)paramObject);
      return;
    }
    if ((paramString.equals("package-name")) && ((paramObject instanceof String)))
    {
      this._packageName = ((String)paramObject);
      return;
    }
    if ((paramString.equals("jar-name")) && ((paramObject instanceof String)))
    {
      this._jarFileName = ((String)paramObject);
      return;
    }
    if (paramString.equals("generate-translet"))
    {
      if ((paramObject instanceof Boolean))
      {
        this._generateTranslet = ((Boolean)paramObject).booleanValue();
        return;
      }
      if ((paramObject instanceof String)) {
        this._generateTranslet = ((String)paramObject).equalsIgnoreCase("true");
      }
    }
    else if (paramString.equals("auto-translet"))
    {
      if ((paramObject instanceof Boolean))
      {
        this._autoTranslet = ((Boolean)paramObject).booleanValue();
        return;
      }
      if ((paramObject instanceof String)) {
        this._autoTranslet = ((String)paramObject).equalsIgnoreCase("true");
      }
    }
    else if (paramString.equals("use-classpath"))
    {
      if ((paramObject instanceof Boolean))
      {
        this._useClasspath = ((Boolean)paramObject).booleanValue();
        return;
      }
      if ((paramObject instanceof String)) {
        this._useClasspath = ((String)paramObject).equalsIgnoreCase("true");
      }
    }
    else if (paramString.equals("debug"))
    {
      if ((paramObject instanceof Boolean))
      {
        this._debug = ((Boolean)paramObject).booleanValue();
        return;
      }
      if ((paramObject instanceof String)) {
        this._debug = ((String)paramObject).equalsIgnoreCase("true");
      }
    }
    else if (paramString.equals("enable-inlining"))
    {
      if ((paramObject instanceof Boolean))
      {
        this._enableInlining = ((Boolean)paramObject).booleanValue();
        return;
      }
      if ((paramObject instanceof String)) {
        this._enableInlining = ((String)paramObject).equalsIgnoreCase("true");
      }
    }
    else if (paramString.equals("indent-number"))
    {
      if ((paramObject instanceof String)) {
        try
        {
          this._indentNumber = Integer.parseInt((String)paramObject);
          return;
        }
        catch (NumberFormatException localNumberFormatException) {}
      } else if ((paramObject instanceof Integer)) {
        this._indentNumber = ((Integer)paramObject).intValue();
      }
    }
    else if (paramString.equals("jdk.xml.transform.extensionClassLoader"))
    {
      if ((paramObject instanceof ClassLoader))
      {
        this._extensionClassLoader = ((ClassLoader)paramObject);
        return;
      }
      localErrorMsg = new ErrorMsg("JAXP_INVALID_ATTR_VALUE_ERR", "Extension Functions ClassLoader");
      throw new IllegalArgumentException(localErrorMsg.toString());
    }
    if ((this._xmlSecurityManager != null) && (this._xmlSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject))) {
      return;
    }
    if ((this._xmlSecurityPropertyMgr != null) && (this._xmlSecurityPropertyMgr.setValue(paramString, FeaturePropertyBase.State.APIPROPERTY, paramObject)))
    {
      this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
      this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
      return;
    }
    ErrorMsg localErrorMsg = new ErrorMsg("JAXP_INVALID_ATTR_ERR", paramString);
    throw new IllegalArgumentException(localErrorMsg.toString());
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws TransformerConfigurationException
  {
    ErrorMsg localErrorMsg;
    if (paramString == null)
    {
      localErrorMsg = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
      throw new NullPointerException(localErrorMsg.toString());
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
    {
      if ((this._isSecureMode) && (!paramBoolean))
      {
        localErrorMsg = new ErrorMsg("JAXP_SECUREPROCESSING_FEATURE");
        throw new TransformerConfigurationException(localErrorMsg.toString());
      }
      this._isNotSecureProcessing = (!paramBoolean);
      this._xmlSecurityManager.setSecureProcessing(paramBoolean);
      if ((paramBoolean) && (XalanConstants.IS_JDK8_OR_ABOVE))
      {
        this._xmlSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, FeaturePropertyBase.State.FSP, "");
        this._xmlSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET, FeaturePropertyBase.State.FSP, "");
        this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
      }
      if ((paramBoolean) && (this._featureManager != null)) {
        this._featureManager.setValue(FeatureManager.Feature.ORACLE_ENABLE_EXTENSION_FUNCTION, FeaturePropertyBase.State.FSP, "false");
      }
      return;
    }
    if (paramString.equals("http://www.oracle.com/feature/use-service-mechanism"))
    {
      if (!this._isSecureMode) {
        this._useServicesMechanism = paramBoolean;
      }
    }
    else
    {
      if ((this._featureManager != null) && (this._featureManager.setValue(paramString, FeaturePropertyBase.State.APIPROPERTY, paramBoolean))) {
        return;
      }
      localErrorMsg = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", paramString);
      throw new TransformerConfigurationException(localErrorMsg.toString());
    }
  }
  
  public boolean getFeature(String paramString)
  {
    String[] arrayOfString = { "http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stax.StAXSource/feature", "http://javax.xml.transform.stax.StAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter", "http://www.oracle.com/feature/use-service-mechanism" };
    if (paramString == null)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
      throw new NullPointerException(localErrorMsg.toString());
    }
    for (int i = 0; i < arrayOfString.length; i++) {
      if (paramString.equals(arrayOfString[i])) {
        return true;
      }
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      return !this._isNotSecureProcessing;
    }
    String str = this._featureManager != null ? this._featureManager.getValueAsString(paramString) : null;
    if (str != null) {
      return Boolean.parseBoolean(str);
    }
    return false;
  }
  
  public boolean useServicesMechnism()
  {
    return this._useServicesMechanism;
  }
  
  public FeatureManager getFeatureManager()
  {
    return this._featureManager;
  }
  
  public URIResolver getURIResolver()
  {
    return this._uriResolver;
  }
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    this._uriResolver = paramURIResolver;
  }
  
  public Source getAssociatedStylesheet(Source paramSource, String paramString1, String paramString2, String paramString3)
    throws TransformerConfigurationException
  {
    StylesheetPIHandler localStylesheetPIHandler = new StylesheetPIHandler(null, paramString1, paramString2, paramString3);
    try
    {
      Object localObject;
      String str;
      if ((paramSource instanceof DOMSource))
      {
        localObject = (DOMSource)paramSource;
        str = ((DOMSource)localObject).getSystemId();
        Node localNode = ((DOMSource)localObject).getNode();
        DOM2SAX localDOM2SAX = new DOM2SAX(localNode);
        localStylesheetPIHandler.setBaseId(str);
        localDOM2SAX.setContentHandler(localStylesheetPIHandler);
        localDOM2SAX.parse();
      }
      else
      {
        InputSource localInputSource = SAXSource.sourceToInputSource(paramSource);
        str = localInputSource.getSystemId();
        localObject = FactoryImpl.getSAXFactory(this._useServicesMechanism);
        ((SAXParserFactory)localObject).setNamespaceAware(true);
        if (!this._isNotSecureProcessing) {
          try
          {
            ((SAXParserFactory)localObject).setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
          }
          catch (SAXException localSAXException2) {}
        }
        SAXParser localSAXParser = ((SAXParserFactory)localObject).newSAXParser();
        XMLReader localXMLReader = localSAXParser.getXMLReader();
        if (localXMLReader == null) {
          localXMLReader = XMLReaderFactory.createXMLReader();
        }
        localStylesheetPIHandler.setBaseId(str);
        localXMLReader.setContentHandler(localStylesheetPIHandler);
        localXMLReader.parse(localInputSource);
      }
      if (this._uriResolver != null) {
        localStylesheetPIHandler.setURIResolver(this._uriResolver);
      }
    }
    catch (StopParseException localStopParseException) {}catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new TransformerConfigurationException("getAssociatedStylesheets failed", localParserConfigurationException);
    }
    catch (SAXException localSAXException1)
    {
      throw new TransformerConfigurationException("getAssociatedStylesheets failed", localSAXException1);
    }
    catch (IOException localIOException)
    {
      throw new TransformerConfigurationException("getAssociatedStylesheets failed", localIOException);
    }
    return localStylesheetPIHandler.getAssociatedStylesheet();
  }
  
  public Transformer newTransformer()
    throws TransformerConfigurationException
  {
    TransformerImpl localTransformerImpl = new TransformerImpl(new Properties(), this._indentNumber, this);
    if (this._uriResolver != null) {
      localTransformerImpl.setURIResolver(this._uriResolver);
    }
    if (!this._isNotSecureProcessing) {
      localTransformerImpl.setSecureProcessing(true);
    }
    return localTransformerImpl;
  }
  
  public Transformer newTransformer(Source paramSource)
    throws TransformerConfigurationException
  {
    Templates localTemplates = newTemplates(paramSource);
    Transformer localTransformer = localTemplates.newTransformer();
    if (this._uriResolver != null) {
      localTransformer.setURIResolver(this._uriResolver);
    }
    return localTransformer;
  }
  
  private void passWarningsToListener(Vector paramVector)
    throws TransformerException
  {
    if ((this._errorListener == null) || (paramVector == null)) {
      return;
    }
    int i = paramVector.size();
    for (int j = 0; j < i; j++)
    {
      ErrorMsg localErrorMsg = (ErrorMsg)paramVector.elementAt(j);
      if (localErrorMsg.isWarningError()) {
        this._errorListener.error(new TransformerConfigurationException(localErrorMsg.toString()));
      } else {
        this._errorListener.warning(new TransformerConfigurationException(localErrorMsg.toString()));
      }
    }
  }
  
  private void passErrorsToListener(Vector paramVector)
  {
    try
    {
      if ((this._errorListener == null) || (paramVector == null)) {
        return;
      }
      int i = paramVector.size();
      for (int j = 0; j < i; j++)
      {
        String str = paramVector.elementAt(j).toString();
        this._errorListener.error(new TransformerException(str));
      }
    }
    catch (TransformerException localTransformerException) {}
  }
  
  public Templates newTemplates(Source paramSource)
    throws TransformerConfigurationException
  {
    if (this._useClasspath)
    {
      localObject1 = getTransletBaseName(paramSource);
      if (this._packageName != null) {
        localObject1 = this._packageName + "." + (String)localObject1;
      }
      try
      {
        Class localClass = ObjectFactory.findProviderClass((String)localObject1, true);
        resetTransientAttributes();
        return new TemplatesImpl(new Class[] { localClass }, (String)localObject1, null, this._indentNumber, this);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        localObject3 = new ErrorMsg("CLASS_NOT_FOUND_ERR", localObject1);
        throw new TransformerConfigurationException(((ErrorMsg)localObject3).toString());
      }
      catch (Exception localException)
      {
        localObject3 = new ErrorMsg(new ErrorMsg("RUNTIME_ERROR_KEY") + localException.getMessage());
        throw new TransformerConfigurationException(((ErrorMsg)localObject3).toString());
      }
    }
    Object localObject2;
    if (this._autoTranslet)
    {
      localObject2 = getTransletBaseName(paramSource);
      if (this._packageName != null) {
        localObject2 = this._packageName + "." + (String)localObject2;
      }
      if (this._jarFileName != null) {
        localObject1 = getBytecodesFromJar(paramSource, (String)localObject2);
      } else {
        localObject1 = getBytecodesFromClasses(paramSource, (String)localObject2);
      }
      if (localObject1 != null)
      {
        if (this._debug) {
          if (this._jarFileName != null) {
            System.err.println(new ErrorMsg("TRANSFORM_WITH_JAR_STR", localObject2, this._jarFileName));
          } else {
            System.err.println(new ErrorMsg("TRANSFORM_WITH_TRANSLET_STR", localObject2));
          }
        }
        resetTransientAttributes();
        return new TemplatesImpl((byte[][])localObject1, (String)localObject2, null, this._indentNumber, this);
      }
    }
    Object localObject1 = new XSLTC(this._useServicesMechanism, this._featureManager);
    if (this._debug) {
      ((XSLTC)localObject1).setDebug(true);
    }
    if (this._enableInlining) {
      ((XSLTC)localObject1).setTemplateInlining(true);
    } else {
      ((XSLTC)localObject1).setTemplateInlining(false);
    }
    if (!this._isNotSecureProcessing) {
      ((XSLTC)localObject1).setSecureProcessing(true);
    }
    ((XSLTC)localObject1).setProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet", this._accessExternalStylesheet);
    ((XSLTC)localObject1).setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
    ((XSLTC)localObject1).setProperty("http://apache.org/xml/properties/security-manager", this._xmlSecurityManager);
    ((XSLTC)localObject1).setProperty("jdk.xml.transform.extensionClassLoader", this._extensionClassLoader);
    ((XSLTC)localObject1).init();
    if (!this._isNotSecureProcessing) {
      this._xsltcExtensionFunctions = ((XSLTC)localObject1).getExternalExtensionFunctions();
    }
    if (this._uriResolver != null) {
      ((XSLTC)localObject1).setSourceLoader(this);
    }
    if ((this._piParams != null) && (this._piParams.get(paramSource) != null))
    {
      localObject2 = (PIParamWrapper)this._piParams.get(paramSource);
      if (localObject2 != null) {
        ((XSLTC)localObject1).setPIParameters(((PIParamWrapper)localObject2)._media, ((PIParamWrapper)localObject2)._title, ((PIParamWrapper)localObject2)._charset);
      }
    }
    int i = 2;
    if ((this._generateTranslet) || (this._autoTranslet))
    {
      ((XSLTC)localObject1).setClassName(getTransletBaseName(paramSource));
      if (this._destinationDirectory != null)
      {
        ((XSLTC)localObject1).setDestDirectory(this._destinationDirectory);
      }
      else
      {
        localObject3 = getStylesheetFileName(paramSource);
        if (localObject3 != null)
        {
          localObject4 = new File((String)localObject3);
          str = ((File)localObject4).getParent();
          if (str != null) {
            ((XSLTC)localObject1).setDestDirectory(str);
          }
        }
      }
      if (this._packageName != null) {
        ((XSLTC)localObject1).setPackageName(this._packageName);
      }
      if (this._jarFileName != null)
      {
        ((XSLTC)localObject1).setJarFileName(this._jarFileName);
        i = 5;
      }
      else
      {
        i = 4;
      }
    }
    Object localObject3 = Util.getInputSource((XSLTC)localObject1, paramSource);
    Object localObject4 = ((XSLTC)localObject1).compile(null, (InputSource)localObject3, i);
    String str = ((XSLTC)localObject1).getClassName();
    if (((this._generateTranslet) || (this._autoTranslet)) && (localObject4 != null) && (this._jarFileName != null)) {
      try
      {
        ((XSLTC)localObject1).outputToJar();
      }
      catch (IOException localIOException) {}
    }
    resetTransientAttributes();
    if (this._errorListener != this) {
      try
      {
        passWarningsToListener(((XSLTC)localObject1).getWarnings());
      }
      catch (TransformerException localTransformerException1)
      {
        throw new TransformerConfigurationException(localTransformerException1);
      }
    } else {
      ((XSLTC)localObject1).printWarnings();
    }
    if (localObject4 == null)
    {
      Vector localVector = ((XSLTC)localObject1).getErrors();
      ErrorMsg localErrorMsg;
      if (localVector != null) {
        localErrorMsg = (ErrorMsg)localVector.elementAt(localVector.size() - 1);
      } else {
        localErrorMsg = new ErrorMsg("JAXP_COMPILE_ERR");
      }
      Throwable localThrowable = localErrorMsg.getCause();
      TransformerConfigurationException localTransformerConfigurationException;
      if (localThrowable != null) {
        localTransformerConfigurationException = new TransformerConfigurationException(localThrowable.getMessage(), localThrowable);
      } else {
        localTransformerConfigurationException = new TransformerConfigurationException(localErrorMsg.toString());
      }
      if (this._errorListener != null)
      {
        passErrorsToListener(((XSLTC)localObject1).getErrors());
        try
        {
          this._errorListener.fatalError(localTransformerConfigurationException);
        }
        catch (TransformerException localTransformerException2) {}
      }
      else
      {
        ((XSLTC)localObject1).printErrors();
      }
      throw localTransformerConfigurationException;
    }
    return new TemplatesImpl((byte[][])localObject4, str, ((XSLTC)localObject1).getOutputProperties(), this._indentNumber, this);
  }
  
  public TemplatesHandler newTemplatesHandler()
    throws TransformerConfigurationException
  {
    TemplatesHandlerImpl localTemplatesHandlerImpl = new TemplatesHandlerImpl(this._indentNumber, this);
    if (this._uriResolver != null) {
      localTemplatesHandlerImpl.setURIResolver(this._uriResolver);
    }
    return localTemplatesHandlerImpl;
  }
  
  public TransformerHandler newTransformerHandler()
    throws TransformerConfigurationException
  {
    Transformer localTransformer = newTransformer();
    if (this._uriResolver != null) {
      localTransformer.setURIResolver(this._uriResolver);
    }
    return new TransformerHandlerImpl((TransformerImpl)localTransformer);
  }
  
  public TransformerHandler newTransformerHandler(Source paramSource)
    throws TransformerConfigurationException
  {
    Transformer localTransformer = newTransformer(paramSource);
    if (this._uriResolver != null) {
      localTransformer.setURIResolver(this._uriResolver);
    }
    return new TransformerHandlerImpl((TransformerImpl)localTransformer);
  }
  
  public TransformerHandler newTransformerHandler(Templates paramTemplates)
    throws TransformerConfigurationException
  {
    Transformer localTransformer = paramTemplates.newTransformer();
    TransformerImpl localTransformerImpl = (TransformerImpl)localTransformer;
    return new TransformerHandlerImpl(localTransformerImpl);
  }
  
  public XMLFilter newXMLFilter(Source paramSource)
    throws TransformerConfigurationException
  {
    Templates localTemplates = newTemplates(paramSource);
    if (localTemplates == null) {
      return null;
    }
    return newXMLFilter(localTemplates);
  }
  
  public XMLFilter newXMLFilter(Templates paramTemplates)
    throws TransformerConfigurationException
  {
    try
    {
      return new TrAXFilter(paramTemplates);
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      if (this._errorListener != null) {
        try
        {
          this._errorListener.fatalError(localTransformerConfigurationException);
          return null;
        }
        catch (TransformerException localTransformerException)
        {
          new TransformerConfigurationException(localTransformerException);
        }
      }
      throw localTransformerConfigurationException;
    }
  }
  
  public void error(TransformerException paramTransformerException)
    throws TransformerException
  {
    Throwable localThrowable = paramTransformerException.getException();
    if (localThrowable != null) {
      System.err.println(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), localThrowable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("ERROR_MSG", paramTransformerException.getMessageAndLocation()));
    }
    throw paramTransformerException;
  }
  
  public void fatalError(TransformerException paramTransformerException)
    throws TransformerException
  {
    Throwable localThrowable = paramTransformerException.getException();
    if (localThrowable != null) {
      System.err.println(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), localThrowable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("FATAL_ERR_MSG", paramTransformerException.getMessageAndLocation()));
    }
    throw paramTransformerException;
  }
  
  public void warning(TransformerException paramTransformerException)
    throws TransformerException
  {
    Throwable localThrowable = paramTransformerException.getException();
    if (localThrowable != null) {
      System.err.println(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", paramTransformerException.getMessageAndLocation(), localThrowable.getMessage()));
    } else {
      System.err.println(new ErrorMsg("WARNING_MSG", paramTransformerException.getMessageAndLocation()));
    }
  }
  
  public InputSource loadSource(String paramString1, String paramString2, XSLTC paramXSLTC)
  {
    try
    {
      if (this._uriResolver != null)
      {
        Source localSource = this._uriResolver.resolve(paramString1, paramString2);
        if (localSource != null) {
          return Util.getInputSource(paramXSLTC, localSource);
        }
      }
    }
    catch (TransformerException localTransformerException)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("INVALID_URI_ERR", paramString1 + "\n" + localTransformerException.getMessage(), this);
      paramXSLTC.getParser().reportError(2, localErrorMsg);
    }
    return null;
  }
  
  private void resetTransientAttributes()
  {
    this._transletName = "GregorSamsa";
    this._destinationDirectory = null;
    this._packageName = null;
    this._jarFileName = null;
  }
  
  private byte[][] getBytecodesFromClasses(Source paramSource, String paramString)
  {
    if (paramString == null) {
      return (byte[][])null;
    }
    String str1 = getStylesheetFileName(paramSource);
    File localFile1 = null;
    if (str1 != null) {
      localFile1 = new File(str1);
    }
    int i = paramString.lastIndexOf('.');
    String str2;
    if (i > 0) {
      str2 = paramString.substring(i + 1);
    } else {
      str2 = paramString;
    }
    String str3 = paramString.replace('.', '/');
    if (this._destinationDirectory != null) {
      str3 = this._destinationDirectory + "/" + str3 + ".class";
    } else if ((localFile1 != null) && (localFile1.getParent() != null)) {
      str3 = localFile1.getParent() + "/" + str3 + ".class";
    } else {
      str3 = str3 + ".class";
    }
    File localFile2 = new File(str3);
    if (!localFile2.exists()) {
      return (byte[][])null;
    }
    if ((localFile1 != null) && (localFile1.exists()))
    {
      long l1 = localFile1.lastModified();
      long l2 = localFile2.lastModified();
      if (l2 < l1) {
        return (byte[][])null;
      }
    }
    Vector localVector = new Vector();
    int j = (int)localFile2.length();
    if (j > 0)
    {
      try
      {
        localObject1 = new FileInputStream(localFile2);
      }
      catch (FileNotFoundException localFileNotFoundException1)
      {
        return (byte[][])null;
      }
      localObject2 = new byte[j];
      try
      {
        readFromInputStream((byte[])localObject2, (InputStream)localObject1, j);
        ((FileInputStream)localObject1).close();
      }
      catch (IOException localIOException1)
      {
        return (byte[][])null;
      }
      localVector.addElement(localObject2);
    }
    else
    {
      return (byte[][])null;
    }
    Object localObject1 = localFile2.getParent();
    if (localObject1 == null) {
      localObject1 = SecuritySupport.getSystemProperty("user.dir");
    }
    Object localObject2 = new File((String)localObject1);
    final String str4 = str2 + "$";
    File[] arrayOfFile = ((File)localObject2).listFiles(new FilenameFilter()
    {
      public boolean accept(File paramAnonymousFile, String paramAnonymousString)
      {
        return (paramAnonymousString.endsWith(".class")) && (paramAnonymousString.startsWith(str4));
      }
    });
    Object localObject3;
    int m;
    for (int k = 0; k < arrayOfFile.length; k++)
    {
      localObject3 = arrayOfFile[k];
      m = (int)((File)localObject3).length();
      if (m > 0)
      {
        FileInputStream localFileInputStream = null;
        try
        {
          localFileInputStream = new FileInputStream((File)localObject3);
        }
        catch (FileNotFoundException localFileNotFoundException2)
        {
          continue;
        }
        byte[] arrayOfByte = new byte[m];
        try
        {
          readFromInputStream(arrayOfByte, localFileInputStream, m);
          localFileInputStream.close();
        }
        catch (IOException localIOException2)
        {
          continue;
        }
        localVector.addElement(arrayOfByte);
      }
    }
    k = localVector.size();
    if (k > 0)
    {
      localObject3 = new byte[k][1];
      for (m = 0; m < k; m++) {
        localObject3[m] = ((byte[])(byte[])localVector.elementAt(m));
      }
      return localObject3;
    }
    return (byte[][])null;
  }
  
  private byte[][] getBytecodesFromJar(Source paramSource, String paramString)
  {
    String str1 = getStylesheetFileName(paramSource);
    File localFile1 = null;
    if (str1 != null) {
      localFile1 = new File(str1);
    }
    String str2;
    if (this._destinationDirectory != null) {
      str2 = this._destinationDirectory + "/" + this._jarFileName;
    } else if ((localFile1 != null) && (localFile1.getParent() != null)) {
      str2 = localFile1.getParent() + "/" + this._jarFileName;
    } else {
      str2 = this._jarFileName;
    }
    File localFile2 = new File(str2);
    if (!localFile2.exists()) {
      return (byte[][])null;
    }
    if ((localFile1 != null) && (localFile1.exists()))
    {
      long l1 = localFile1.lastModified();
      long l2 = localFile2.lastModified();
      if (l2 < l1) {
        return (byte[][])null;
      }
    }
    ZipFile localZipFile;
    try
    {
      localZipFile = new ZipFile(localFile2);
    }
    catch (IOException localIOException1)
    {
      return (byte[][])null;
    }
    String str3 = paramString.replace('.', '/');
    String str4 = str3 + "$";
    String str5 = str3 + ".class";
    Vector localVector = new Vector();
    Enumeration localEnumeration = localZipFile.entries();
    Object localObject;
    while (localEnumeration.hasMoreElements())
    {
      ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
      localObject = localZipEntry.getName();
      if ((localZipEntry.getSize() > 0L) && ((((String)localObject).equals(str5)) || ((((String)localObject).endsWith(".class")) && (((String)localObject).startsWith(str4))))) {
        try
        {
          InputStream localInputStream = localZipFile.getInputStream(localZipEntry);
          int k = (int)localZipEntry.getSize();
          byte[] arrayOfByte = new byte[k];
          readFromInputStream(arrayOfByte, localInputStream, k);
          localInputStream.close();
          localVector.addElement(arrayOfByte);
        }
        catch (IOException localIOException2)
        {
          return (byte[][])null;
        }
      }
    }
    int i = localVector.size();
    if (i > 0)
    {
      localObject = new byte[i][1];
      for (int j = 0; j < i; j++) {
        localObject[j] = ((byte[])(byte[])localVector.elementAt(j));
      }
      return localObject;
    }
    return (byte[][])null;
  }
  
  private void readFromInputStream(byte[] paramArrayOfByte, InputStream paramInputStream, int paramInt)
    throws IOException
  {
    int i = 0;
    int j = 0;
    int k = paramInt;
    while ((k > 0) && ((i = paramInputStream.read(paramArrayOfByte, j, k)) > 0))
    {
      j += i;
      k -= i;
    }
  }
  
  private String getTransletBaseName(Source paramSource)
  {
    String str1 = null;
    if (!this._transletName.equals("GregorSamsa")) {
      return this._transletName;
    }
    String str2 = paramSource.getSystemId();
    if (str2 != null)
    {
      String str3 = Util.baseName(str2);
      if (str3 != null)
      {
        str3 = Util.noExtName(str3);
        str1 = Util.toJavaName(str3);
      }
    }
    return str1 != null ? str1 : "GregorSamsa";
  }
  
  private String getStylesheetFileName(Source paramSource)
  {
    String str = paramSource.getSystemId();
    if (str != null)
    {
      File localFile = new File(str);
      if (localFile.exists()) {
        return str;
      }
      URL localURL;
      try
      {
        localURL = new URL(str);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        return null;
      }
      if ("file".equals(localURL.getProtocol())) {
        return localURL.getFile();
      }
      return null;
    }
    return null;
  }
  
  protected final XSLTCDTMManager createNewDTMManagerInstance()
  {
    return XSLTCDTMManager.createNewDTMManagerInstance();
  }
  
  private static class PIParamWrapper
  {
    public String _media = null;
    public String _title = null;
    public String _charset = null;
    
    public PIParamWrapper(String paramString1, String paramString2, String paramString3)
    {
      this._media = paramString1;
      this._title = paramString2;
      this._charset = paramString3;
    }
  }
}
