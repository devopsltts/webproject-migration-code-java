package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class XMLGrammarPreparser
{
  private static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  private static final Map<String, String> KNOWN_LOADERS;
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool" };
  protected SymbolTable fSymbolTable;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityResolver fEntityResolver;
  protected XMLGrammarPool fGrammarPool;
  protected Locale fLocale;
  private Map<String, XMLGrammarLoader> fLoaders;
  
  public XMLGrammarPreparser()
  {
    this(new SymbolTable());
  }
  
  public XMLGrammarPreparser(SymbolTable paramSymbolTable)
  {
    this.fSymbolTable = paramSymbolTable;
    this.fLoaders = new HashMap();
    this.fErrorReporter = new XMLErrorReporter();
    setLocale(Locale.getDefault());
    this.fEntityResolver = new XMLEntityManager();
  }
  
  public boolean registerPreparser(String paramString, XMLGrammarLoader paramXMLGrammarLoader)
  {
    if (paramXMLGrammarLoader == null)
    {
      if (KNOWN_LOADERS.containsKey(paramString))
      {
        String str = (String)KNOWN_LOADERS.get(paramString);
        try
        {
          XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)ObjectFactory.newInstance(str, true);
          this.fLoaders.put(paramString, localXMLGrammarLoader);
        }
        catch (Exception localException)
        {
          return false;
        }
        return true;
      }
      return false;
    }
    this.fLoaders.put(paramString, paramXMLGrammarLoader);
    return true;
  }
  
  public Grammar preparseGrammar(String paramString, XMLInputSource paramXMLInputSource)
    throws XNIException, IOException
  {
    if (this.fLoaders.containsKey(paramString))
    {
      XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)this.fLoaders.get(paramString);
      localXMLGrammarLoader.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
      localXMLGrammarLoader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fEntityResolver);
      localXMLGrammarLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
      if (this.fGrammarPool != null) {
        try
        {
          localXMLGrammarLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
        }
        catch (Exception localException) {}
      }
      return localXMLGrammarLoader.loadGrammar(paramXMLInputSource);
    }
    return null;
  }
  
  public void setLocale(Locale paramLocale)
  {
    this.fLocale = paramLocale;
    this.fErrorReporter.setLocale(paramLocale);
  }
  
  public Locale getLocale()
  {
    return this.fLocale;
  }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler)
  {
    this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler);
  }
  
  public XMLErrorHandler getErrorHandler()
  {
    return this.fErrorReporter.getErrorHandler();
  }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver)
  {
    this.fEntityResolver = paramXMLEntityResolver;
  }
  
  public XMLEntityResolver getEntityResolver()
  {
    return this.fEntityResolver;
  }
  
  public void setGrammarPool(XMLGrammarPool paramXMLGrammarPool)
  {
    this.fGrammarPool = paramXMLGrammarPool;
  }
  
  public XMLGrammarPool getGrammarPool()
  {
    return this.fGrammarPool;
  }
  
  public XMLGrammarLoader getLoader(String paramString)
  {
    return (XMLGrammarLoader)this.fLoaders.get(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
  {
    Iterator localIterator = this.fLoaders.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      try
      {
        XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)localEntry.getValue();
        localXMLGrammarLoader.setFeature(paramString, paramBoolean);
      }
      catch (Exception localException) {}
    }
    if (paramString.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
      this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", paramBoolean);
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    Iterator localIterator = this.fLoaders.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      try
      {
        XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)localEntry.getValue();
        localXMLGrammarLoader.setProperty(paramString, paramObject);
      }
      catch (Exception localException) {}
    }
  }
  
  public boolean getFeature(String paramString1, String paramString2)
  {
    XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)this.fLoaders.get(paramString1);
    return localXMLGrammarLoader.getFeature(paramString2);
  }
  
  public Object getProperty(String paramString1, String paramString2)
  {
    XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)this.fLoaders.get(paramString1);
    return localXMLGrammarLoader.getProperty(paramString2);
  }
  
  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("http://www.w3.org/2001/XMLSchema", "com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader");
    localHashMap.put("http://www.w3.org/TR/REC-xml", "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader");
    KNOWN_LOADERS = Collections.unmodifiableMap(localHashMap);
  }
}
