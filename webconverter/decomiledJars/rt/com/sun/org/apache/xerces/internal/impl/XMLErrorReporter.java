package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerProxy;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.xml.sax.ErrorHandler;

public class XMLErrorReporter
  implements XMLComponent
{
  public static final short SEVERITY_WARNING = 0;
  public static final short SEVERITY_ERROR = 1;
  public static final short SEVERITY_FATAL_ERROR = 2;
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/continue-after-fatal-error" };
  private static final Boolean[] FEATURE_DEFAULTS = { null };
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/error-handler" };
  private static final Object[] PROPERTY_DEFAULTS = { null };
  protected Locale fLocale;
  protected Map<String, MessageFormatter> fMessageFormatters = new HashMap();
  protected XMLErrorHandler fErrorHandler;
  protected XMLLocator fLocator;
  protected boolean fContinueAfterFatalError;
  protected XMLErrorHandler fDefaultErrorHandler;
  private ErrorHandler fSaxProxy = null;
  
  public XMLErrorReporter() {}
  
  public void setLocale(Locale paramLocale)
  {
    this.fLocale = paramLocale;
  }
  
  public Locale getLocale()
  {
    return this.fLocale;
  }
  
  public void setDocumentLocator(XMLLocator paramXMLLocator)
  {
    this.fLocator = paramXMLLocator;
  }
  
  public void putMessageFormatter(String paramString, MessageFormatter paramMessageFormatter)
  {
    this.fMessageFormatters.put(paramString, paramMessageFormatter);
  }
  
  public MessageFormatter getMessageFormatter(String paramString)
  {
    return (MessageFormatter)this.fMessageFormatters.get(paramString);
  }
  
  public MessageFormatter removeMessageFormatter(String paramString)
  {
    return (MessageFormatter)this.fMessageFormatters.remove(paramString);
  }
  
  public String reportError(String paramString1, String paramString2, Object[] paramArrayOfObject, short paramShort)
    throws XNIException
  {
    return reportError(this.fLocator, paramString1, paramString2, paramArrayOfObject, paramShort);
  }
  
  public String reportError(String paramString1, String paramString2, Object[] paramArrayOfObject, short paramShort, Exception paramException)
    throws XNIException
  {
    return reportError(this.fLocator, paramString1, paramString2, paramArrayOfObject, paramShort, paramException);
  }
  
  public String reportError(XMLLocator paramXMLLocator, String paramString1, String paramString2, Object[] paramArrayOfObject, short paramShort)
    throws XNIException
  {
    return reportError(paramXMLLocator, paramString1, paramString2, paramArrayOfObject, paramShort, null);
  }
  
  public String reportError(XMLLocator paramXMLLocator, String paramString1, String paramString2, Object[] paramArrayOfObject, short paramShort, Exception paramException)
    throws XNIException
  {
    MessageFormatter localMessageFormatter = getMessageFormatter(paramString1);
    String str;
    if (localMessageFormatter != null)
    {
      str = localMessageFormatter.formatMessage(this.fLocale, paramString2, paramArrayOfObject);
    }
    else
    {
      localObject = new StringBuffer();
      ((StringBuffer)localObject).append(paramString1);
      ((StringBuffer)localObject).append('#');
      ((StringBuffer)localObject).append(paramString2);
      int i = paramArrayOfObject != null ? paramArrayOfObject.length : 0;
      if (i > 0)
      {
        ((StringBuffer)localObject).append('?');
        for (int j = 0; j < i; j++)
        {
          ((StringBuffer)localObject).append(paramArrayOfObject[j]);
          if (j < i - 1) {
            ((StringBuffer)localObject).append('&');
          }
        }
      }
      str = ((StringBuffer)localObject).toString();
    }
    Object localObject = paramException != null ? new XMLParseException(paramXMLLocator, str, paramException) : new XMLParseException(paramXMLLocator, str);
    XMLErrorHandler localXMLErrorHandler = this.fErrorHandler;
    if (localXMLErrorHandler == null)
    {
      if (this.fDefaultErrorHandler == null) {
        this.fDefaultErrorHandler = new DefaultErrorHandler();
      }
      localXMLErrorHandler = this.fDefaultErrorHandler;
    }
    switch (paramShort)
    {
    case 0: 
      localXMLErrorHandler.warning(paramString1, paramString2, (XMLParseException)localObject);
      break;
    case 1: 
      localXMLErrorHandler.error(paramString1, paramString2, (XMLParseException)localObject);
      break;
    case 2: 
      localXMLErrorHandler.fatalError(paramString1, paramString2, (XMLParseException)localObject);
      if (!this.fContinueAfterFatalError) {
        throw ((Throwable)localObject);
      }
      break;
    }
    return str;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XNIException
  {
    this.fContinueAfterFatalError = paramXMLComponentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
    this.fErrorHandler = ((XMLErrorHandler)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
  }
  
  public String[] getRecognizedFeatures()
  {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/features/"))
    {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if ((i == "continue-after-fatal-error".length()) && (paramString.endsWith("continue-after-fatal-error"))) {
        this.fContinueAfterFatalError = paramBoolean;
      }
    }
  }
  
  public boolean getFeature(String paramString)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/features/"))
    {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if ((i == "continue-after-fatal-error".length()) && (paramString.endsWith("continue-after-fatal-error"))) {
        return this.fContinueAfterFatalError;
      }
    }
    return false;
  }
  
  public String[] getRecognizedProperties()
  {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if ((i == "internal/error-handler".length()) && (paramString.endsWith("internal/error-handler"))) {
        this.fErrorHandler = ((XMLErrorHandler)paramObject);
      }
    }
  }
  
  public Boolean getFeatureDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
      if (RECOGNIZED_FEATURES[i].equals(paramString)) {
        return FEATURE_DEFAULTS[i];
      }
    }
    return null;
  }
  
  public Object getPropertyDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
      if (RECOGNIZED_PROPERTIES[i].equals(paramString)) {
        return PROPERTY_DEFAULTS[i];
      }
    }
    return null;
  }
  
  public XMLErrorHandler getErrorHandler()
  {
    return this.fErrorHandler;
  }
  
  public ErrorHandler getSAXErrorHandler()
  {
    if (this.fSaxProxy == null) {
      this.fSaxProxy = new ErrorHandlerProxy()
      {
        protected XMLErrorHandler getErrorHandler()
        {
          return XMLErrorReporter.this.fErrorHandler;
        }
      };
    }
    return this.fSaxProxy;
  }
}
