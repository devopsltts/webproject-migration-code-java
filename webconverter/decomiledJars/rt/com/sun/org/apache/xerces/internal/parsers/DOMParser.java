package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.State;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.State;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.LocatorImpl;

public class DOMParser
  extends AbstractDOMParser
{
  protected static final String USE_ENTITY_RESOLVER2 = "http://xml.org/sax/features/use-entity-resolver2";
  protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  private static final String[] RECOGNIZED_FEATURES = { "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace" };
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool" };
  protected boolean fUseEntityResolver2 = true;
  
  public DOMParser(XMLParserConfiguration paramXMLParserConfiguration)
  {
    super(paramXMLParserConfiguration);
  }
  
  public DOMParser()
  {
    this(null, null);
  }
  
  public DOMParser(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null);
  }
  
  public DOMParser(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    super(new XIncludeAwareParserConfiguration());
    this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    if (paramSymbolTable != null) {
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable);
    }
    if (paramXMLGrammarPool != null) {
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", paramXMLGrammarPool);
    }
    this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
  }
  
  public void parse(String paramString)
    throws SAXException, IOException
  {
    XMLInputSource localXMLInputSource = new XMLInputSource(null, paramString, null);
    try
    {
      parse(localXMLInputSource);
    }
    catch (XMLParseException localXMLParseException)
    {
      localException = localXMLParseException.getException();
      if (localException == null)
      {
        LocatorImpl localLocatorImpl = new LocatorImpl();
        localLocatorImpl.setPublicId(localXMLParseException.getPublicId());
        localLocatorImpl.setSystemId(localXMLParseException.getExpandedSystemId());
        localLocatorImpl.setLineNumber(localXMLParseException.getLineNumber());
        localLocatorImpl.setColumnNumber(localXMLParseException.getColumnNumber());
        throw new SAXParseException(localXMLParseException.getMessage(), localLocatorImpl);
      }
      if ((localException instanceof SAXException)) {
        throw ((SAXException)localException);
      }
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      throw new SAXException(localException);
    }
    catch (XNIException localXNIException)
    {
      localXNIException.printStackTrace();
      Exception localException = localXNIException.getException();
      if (localException == null) {
        throw new SAXException(localXNIException.getMessage());
      }
      if ((localException instanceof SAXException)) {
        throw ((SAXException)localException);
      }
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      throw new SAXException(localException);
    }
  }
  
  public void parse(InputSource paramInputSource)
    throws SAXException, IOException
  {
    try
    {
      XMLInputSource localXMLInputSource = new XMLInputSource(paramInputSource.getPublicId(), paramInputSource.getSystemId(), null);
      localXMLInputSource.setByteStream(paramInputSource.getByteStream());
      localXMLInputSource.setCharacterStream(paramInputSource.getCharacterStream());
      localXMLInputSource.setEncoding(paramInputSource.getEncoding());
      parse(localXMLInputSource);
    }
    catch (XMLParseException localXMLParseException)
    {
      localException = localXMLParseException.getException();
      if (localException == null)
      {
        LocatorImpl localLocatorImpl = new LocatorImpl();
        localLocatorImpl.setPublicId(localXMLParseException.getPublicId());
        localLocatorImpl.setSystemId(localXMLParseException.getExpandedSystemId());
        localLocatorImpl.setLineNumber(localXMLParseException.getLineNumber());
        localLocatorImpl.setColumnNumber(localXMLParseException.getColumnNumber());
        throw new SAXParseException(localXMLParseException.getMessage(), localLocatorImpl);
      }
      if ((localException instanceof SAXException)) {
        throw ((SAXException)localException);
      }
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      throw new SAXException(localException);
    }
    catch (XNIException localXNIException)
    {
      Exception localException = localXNIException.getException();
      if (localException == null) {
        throw new SAXException(localXNIException.getMessage());
      }
      if ((localException instanceof SAXException)) {
        throw ((SAXException)localException);
      }
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      throw new SAXException(localException);
    }
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
  {
    try
    {
      XMLEntityResolver localXMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
      Object localObject;
      if ((this.fUseEntityResolver2) && ((paramEntityResolver instanceof EntityResolver2)))
      {
        if ((localXMLEntityResolver instanceof EntityResolver2Wrapper))
        {
          localObject = (EntityResolver2Wrapper)localXMLEntityResolver;
          ((EntityResolver2Wrapper)localObject).setEntityResolver((EntityResolver2)paramEntityResolver);
        }
        else
        {
          this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)paramEntityResolver));
        }
      }
      else if ((localXMLEntityResolver instanceof EntityResolverWrapper))
      {
        localObject = (EntityResolverWrapper)localXMLEntityResolver;
        ((EntityResolverWrapper)localObject).setEntityResolver(paramEntityResolver);
      }
      else
      {
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(paramEntityResolver));
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException) {}
  }
  
  public EntityResolver getEntityResolver()
  {
    Object localObject = null;
    try
    {
      XMLEntityResolver localXMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
      if (localXMLEntityResolver != null) {
        if ((localXMLEntityResolver instanceof EntityResolverWrapper)) {
          localObject = ((EntityResolverWrapper)localXMLEntityResolver).getEntityResolver();
        } else if ((localXMLEntityResolver instanceof EntityResolver2Wrapper)) {
          localObject = ((EntityResolver2Wrapper)localXMLEntityResolver).getEntityResolver();
        }
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException) {}
    return localObject;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    try
    {
      XMLErrorHandler localXMLErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
      if ((localXMLErrorHandler instanceof ErrorHandlerWrapper))
      {
        ErrorHandlerWrapper localErrorHandlerWrapper = (ErrorHandlerWrapper)localXMLErrorHandler;
        localErrorHandlerWrapper.setErrorHandler(paramErrorHandler);
      }
      else
      {
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(paramErrorHandler));
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException) {}
  }
  
  public ErrorHandler getErrorHandler()
  {
    ErrorHandler localErrorHandler = null;
    try
    {
      XMLErrorHandler localXMLErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
      if ((localXMLErrorHandler != null) && ((localXMLErrorHandler instanceof ErrorHandlerWrapper))) {
        localErrorHandler = ((ErrorHandlerWrapper)localXMLErrorHandler).getErrorHandler();
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException) {}
    return localErrorHandler;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    try
    {
      if (paramString.equals("http://xml.org/sax/features/use-entity-resolver2"))
      {
        if (paramBoolean != this.fUseEntityResolver2)
        {
          this.fUseEntityResolver2 = paramBoolean;
          setEntityResolver(getEntityResolver());
        }
        return;
      }
      this.fConfiguration.setFeature(paramString, paramBoolean);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { str }));
      }
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { str }));
    }
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    try
    {
      if (paramString.equals("http://xml.org/sax/features/use-entity-resolver2")) {
        return this.fUseEntityResolver2;
      }
      return this.fConfiguration.getFeature(paramString);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { str }));
      }
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { str }));
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://apache.org/xml/properties/security-manager"))
    {
      this.securityManager = XMLSecurityManager.convert(paramObject, this.securityManager);
      setProperty0("http://apache.org/xml/properties/security-manager", this.securityManager);
      return;
    }
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"))
    {
      if (paramObject == null) {
        this.securityPropertyManager = new XMLSecurityPropertyManager();
      } else {
        this.securityPropertyManager = ((XMLSecurityPropertyManager)paramObject);
      }
      setProperty0("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
      return;
    }
    if (this.securityManager == null)
    {
      this.securityManager = new XMLSecurityManager(true);
      setProperty0("http://apache.org/xml/properties/security-manager", this.securityManager);
    }
    if (this.securityPropertyManager == null)
    {
      this.securityPropertyManager = new XMLSecurityPropertyManager();
      setProperty0("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
    }
    int i = this.securityPropertyManager.getIndex(paramString);
    if (i > -1) {
      this.securityPropertyManager.setValue(i, XMLSecurityPropertyManager.State.APIPROPERTY, (String)paramObject);
    } else if (!this.securityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject)) {
      setProperty0(paramString, paramObject);
    }
  }
  
  public void setProperty0(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    try
    {
      this.fConfiguration.setProperty(paramString, paramObject);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      String str = localXMLConfigurationException.getIdentifier();
      if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { str }));
      }
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { str }));
    }
  }
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://apache.org/xml/properties/dom/current-element-node"))
    {
      boolean bool = false;
      try
      {
        bool = getFeature("http://apache.org/xml/features/dom/defer-node-expansion");
      }
      catch (XMLConfigurationException localXMLConfigurationException2) {}
      if (bool) {
        throw new SAXNotSupportedException("Current element node cannot be queried when node expansion is deferred.");
      }
      return (this.fCurrentNode != null) && (this.fCurrentNode.getNodeType() == 1) ? this.fCurrentNode : null;
    }
    try
    {
      XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)this.fConfiguration.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
      int i = localXMLSecurityPropertyManager.getIndex(paramString);
      if (i > -1) {
        return localXMLSecurityPropertyManager.getValueByIndex(i);
      }
      return this.fConfiguration.getProperty(paramString);
    }
    catch (XMLConfigurationException localXMLConfigurationException1)
    {
      String str = localXMLConfigurationException1.getIdentifier();
      if (localXMLConfigurationException1.getType() == Status.NOT_RECOGNIZED) {
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { str }));
      }
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { str }));
    }
  }
  
  public XMLParserConfiguration getXMLParserConfiguration()
  {
    return this.fConfiguration;
  }
}
