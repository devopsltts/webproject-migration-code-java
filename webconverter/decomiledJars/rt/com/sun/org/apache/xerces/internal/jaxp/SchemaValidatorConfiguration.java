package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

final class SchemaValidatorConfiguration
  implements XMLComponentManager
{
  private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  private static final String VALIDATION = "http://xml.org/sax/features/validation";
  private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
  private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  private final XMLComponentManager fParentComponentManager;
  private final XMLGrammarPool fGrammarPool;
  private final boolean fUseGrammarPoolOnly;
  private final ValidationManager fValidationManager;
  
  public SchemaValidatorConfiguration(XMLComponentManager paramXMLComponentManager, XSGrammarPoolContainer paramXSGrammarPoolContainer, ValidationManager paramValidationManager)
  {
    this.fParentComponentManager = paramXMLComponentManager;
    this.fGrammarPool = paramXSGrammarPoolContainer.getGrammarPool();
    this.fUseGrammarPoolOnly = paramXSGrammarPoolContainer.isFullyComposed();
    this.fValidationManager = paramValidationManager;
    try
    {
      XMLErrorReporter localXMLErrorReporter = (XMLErrorReporter)this.fParentComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      if (localXMLErrorReporter != null) {
        localXMLErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException) {}
  }
  
  public boolean getFeature(String paramString)
    throws XMLConfigurationException
  {
    FeatureState localFeatureState = getFeatureState(paramString);
    if (localFeatureState.isExceptional()) {
      throw new XMLConfigurationException(localFeatureState.status, paramString);
    }
    return localFeatureState.state;
  }
  
  public FeatureState getFeatureState(String paramString)
  {
    if ("http://apache.org/xml/features/internal/parser-settings".equals(paramString)) {
      return this.fParentComponentManager.getFeatureState(paramString);
    }
    if (("http://xml.org/sax/features/validation".equals(paramString)) || ("http://apache.org/xml/features/validation/schema".equals(paramString))) {
      return FeatureState.is(true);
    }
    if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(paramString)) {
      return FeatureState.is(this.fUseGrammarPoolOnly);
    }
    return this.fParentComponentManager.getFeatureState(paramString);
  }
  
  public PropertyState getPropertyState(String paramString)
  {
    if ("http://apache.org/xml/properties/internal/grammar-pool".equals(paramString)) {
      return PropertyState.is(this.fGrammarPool);
    }
    if ("http://apache.org/xml/properties/internal/validation-manager".equals(paramString)) {
      return PropertyState.is(this.fValidationManager);
    }
    return this.fParentComponentManager.getPropertyState(paramString);
  }
  
  public Object getProperty(String paramString)
    throws XMLConfigurationException
  {
    PropertyState localPropertyState = getPropertyState(paramString);
    if (localPropertyState.isExceptional()) {
      throw new XMLConfigurationException(localPropertyState.status, paramString);
    }
    return localPropertyState.state;
  }
  
  public boolean getFeature(String paramString, boolean paramBoolean)
  {
    FeatureState localFeatureState = getFeatureState(paramString);
    if (localFeatureState.isExceptional()) {
      return paramBoolean;
    }
    return localFeatureState.state;
  }
  
  public Object getProperty(String paramString, Object paramObject)
  {
    PropertyState localPropertyState = getPropertyState(paramString);
    if (localPropertyState.isExceptional()) {
      return paramObject;
    }
    return localPropertyState.state;
  }
}
