package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.BootstrapResolver;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;

public class SAXCatalogReader
  implements CatalogReader, ContentHandler, DocumentHandler
{
  protected SAXParserFactory parserFactory = null;
  protected String parserClass = null;
  protected Map<String, String> namespaceMap = new HashMap();
  private SAXCatalogParser saxParser = null;
  private boolean abandonHope = false;
  private Catalog catalog;
  protected Debug debug = CatalogManager.getStaticManager().debug;
  
  public void setParserFactory(SAXParserFactory paramSAXParserFactory)
  {
    this.parserFactory = paramSAXParserFactory;
  }
  
  public void setParserClass(String paramString)
  {
    this.parserClass = paramString;
  }
  
  public SAXParserFactory getParserFactory()
  {
    return this.parserFactory;
  }
  
  public String getParserClass()
  {
    return this.parserClass;
  }
  
  public SAXCatalogReader()
  {
    this.parserFactory = null;
    this.parserClass = null;
  }
  
  public SAXCatalogReader(SAXParserFactory paramSAXParserFactory)
  {
    this.parserFactory = paramSAXParserFactory;
  }
  
  public SAXCatalogReader(String paramString)
  {
    this.parserClass = paramString;
  }
  
  public void setCatalogParser(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1 == null) {
      this.namespaceMap.put(paramString2, paramString3);
    } else {
      this.namespaceMap.put("{" + paramString1 + "}" + paramString2, paramString3);
    }
  }
  
  public String getCatalogParser(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return (String)this.namespaceMap.get(paramString2);
    }
    return (String)this.namespaceMap.get("{" + paramString1 + "}" + paramString2);
  }
  
  public void readCatalog(Catalog paramCatalog, String paramString)
    throws MalformedURLException, IOException, CatalogException
  {
    URL localURL = null;
    try
    {
      localURL = new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localURL = new URL("file:///" + paramString);
    }
    this.debug = paramCatalog.getCatalogManager().debug;
    try
    {
      URLConnection localURLConnection = localURL.openConnection();
      readCatalog(paramCatalog, localURLConnection.getInputStream());
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      paramCatalog.getCatalogManager().debug.message(1, "Failed to load catalog, file not found", localURL.toString());
    }
  }
  
  public void readCatalog(Catalog paramCatalog, InputStream paramInputStream)
    throws IOException, CatalogException
  {
    if ((this.parserFactory == null) && (this.parserClass == null))
    {
      this.debug.message(1, "Cannot read SAX catalog without a parser");
      throw new CatalogException(6);
    }
    this.debug = paramCatalog.getCatalogManager().debug;
    BootstrapResolver localBootstrapResolver = paramCatalog.getCatalogManager().getBootstrapResolver();
    this.catalog = paramCatalog;
    try
    {
      Object localObject1;
      if (this.parserFactory != null)
      {
        localObject1 = this.parserFactory.newSAXParser();
        localObject2 = new SAXParserHandler();
        ((SAXParserHandler)localObject2).setContentHandler(this);
        if (localBootstrapResolver != null) {
          ((SAXParserHandler)localObject2).setEntityResolver(localBootstrapResolver);
        }
        ((SAXParser)localObject1).parse(new InputSource(paramInputStream), (DefaultHandler)localObject2);
      }
      else
      {
        localObject1 = (Parser)ReflectUtil.forName(this.parserClass).newInstance();
        ((Parser)localObject1).setDocumentHandler(this);
        if (localBootstrapResolver != null) {
          ((Parser)localObject1).setEntityResolver(localBootstrapResolver);
        }
        ((Parser)localObject1).parse(new InputSource(paramInputStream));
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new CatalogException(6);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new CatalogException(6);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new CatalogException(6);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new CatalogException(5);
    }
    catch (SAXException localSAXException)
    {
      Object localObject2 = localSAXException.getException();
      UnknownHostException localUnknownHostException = new UnknownHostException();
      FileNotFoundException localFileNotFoundException = new FileNotFoundException();
      if (localObject2 != null)
      {
        if (localObject2.getClass() == localUnknownHostException.getClass()) {
          throw new CatalogException(7, ((Exception)localObject2).toString());
        }
        if (localObject2.getClass() == localFileNotFoundException.getClass()) {
          throw new CatalogException(7, ((Exception)localObject2).toString());
        }
      }
      throw new CatalogException(localSAXException);
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    if (this.saxParser != null) {
      this.saxParser.setDocumentLocator(paramLocator);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    this.saxParser = null;
    this.abandonHope = false;
  }
  
  public void endDocument()
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.endDocument();
    }
  }
  
  public void startElement(String paramString, AttributeList paramAttributeList)
    throws SAXException
  {
    if (this.abandonHope) {
      return;
    }
    if (this.saxParser == null)
    {
      String str1 = "";
      if (paramString.indexOf(':') > 0) {
        str1 = paramString.substring(0, paramString.indexOf(':'));
      }
      String str2 = paramString;
      if (str2.indexOf(':') > 0) {
        str2 = str2.substring(str2.indexOf(':') + 1);
      }
      String str3 = null;
      if (str1.equals("")) {
        str3 = paramAttributeList.getValue("xmlns");
      } else {
        str3 = paramAttributeList.getValue("xmlns:" + str1);
      }
      String str4 = getCatalogParser(str3, str2);
      if (str4 == null)
      {
        this.abandonHope = true;
        if (str3 == null) {
          this.debug.message(2, "No Catalog parser for " + paramString);
        } else {
          this.debug.message(2, "No Catalog parser for {" + str3 + "}" + paramString);
        }
        return;
      }
      try
      {
        this.saxParser = ((SAXCatalogParser)ReflectUtil.forName(str4).newInstance());
        this.saxParser.setCatalog(this.catalog);
        this.saxParser.startDocument();
        this.saxParser.startElement(paramString, paramAttributeList);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        this.saxParser = null;
        this.abandonHope = true;
        this.debug.message(2, localClassNotFoundException.toString());
      }
      catch (InstantiationException localInstantiationException)
      {
        this.saxParser = null;
        this.abandonHope = true;
        this.debug.message(2, localInstantiationException.toString());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        this.saxParser = null;
        this.abandonHope = true;
        this.debug.message(2, localIllegalAccessException.toString());
      }
      catch (ClassCastException localClassCastException)
      {
        this.saxParser = null;
        this.abandonHope = true;
        this.debug.message(2, localClassCastException.toString());
      }
    }
    else
    {
      this.saxParser.startElement(paramString, paramAttributeList);
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (this.abandonHope) {
      return;
    }
    if (this.saxParser == null)
    {
      String str = getCatalogParser(paramString1, paramString2);
      if (str == null)
      {
        this.abandonHope = true;
        if (paramString1 == null) {
          this.debug.message(2, "No Catalog parser for " + paramString2);
        } else {
          this.debug.message(2, "No Catalog parser for {" + paramString1 + "}" + paramString2);
        }
        return;
      }
      try
      {
        this.saxParser = ((SAXCatalogParser)ReflectUtil.forName(str).newInstance());
        this.saxParser.setCatalog(this.catalog);
        this.saxParser.startDocument();
        this.saxParser.startElement(paramString1, paramString2, paramString3, paramAttributes);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        this.saxParser = null;
        this.abandonHope = true;
        this.debug.message(2, localClassNotFoundException.toString());
      }
      catch (InstantiationException localInstantiationException)
      {
        this.saxParser = null;
        this.abandonHope = true;
        this.debug.message(2, localInstantiationException.toString());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        this.saxParser = null;
        this.abandonHope = true;
        this.debug.message(2, localIllegalAccessException.toString());
      }
      catch (ClassCastException localClassCastException)
      {
        this.saxParser = null;
        this.abandonHope = true;
        this.debug.message(2, localClassCastException.toString());
      }
    }
    else
    {
      this.saxParser.startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.endElement(paramString);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.endElement(paramString1, paramString2, paramString3);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.processingInstruction(paramString1, paramString2);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.startPrefixMapping(paramString1, paramString2);
    }
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.endPrefixMapping(paramString);
    }
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    if (this.saxParser != null) {
      this.saxParser.skippedEntity(paramString);
    }
  }
}
