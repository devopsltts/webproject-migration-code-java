package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class ResolvingXMLFilter
  extends XMLFilterImpl
{
  public static boolean suppressExplanation = false;
  CatalogManager catalogManager = CatalogManager.getStaticManager();
  private CatalogResolver catalogResolver = null;
  private CatalogResolver piCatalogResolver = null;
  private boolean allowXMLCatalogPI = false;
  private boolean oasisXMLCatalogPI = false;
  private URL baseURL = null;
  
  public ResolvingXMLFilter()
  {
    this.catalogResolver = new CatalogResolver(this.catalogManager);
  }
  
  public ResolvingXMLFilter(XMLReader paramXMLReader)
  {
    super(paramXMLReader);
    this.catalogResolver = new CatalogResolver(this.catalogManager);
  }
  
  public ResolvingXMLFilter(CatalogManager paramCatalogManager)
  {
    this.catalogManager = paramCatalogManager;
    this.catalogResolver = new CatalogResolver(this.catalogManager);
  }
  
  public ResolvingXMLFilter(XMLReader paramXMLReader, CatalogManager paramCatalogManager)
  {
    super(paramXMLReader);
    this.catalogManager = paramCatalogManager;
    this.catalogResolver = new CatalogResolver(this.catalogManager);
  }
  
  public Catalog getCatalog()
  {
    return this.catalogResolver.getCatalog();
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    this.allowXMLCatalogPI = true;
    setupBaseURI(paramInputSource.getSystemId());
    try
    {
      super.parse(paramInputSource);
    }
    catch (InternalError localInternalError)
    {
      explain(paramInputSource.getSystemId());
      throw localInternalError;
    }
  }
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    this.allowXMLCatalogPI = true;
    setupBaseURI(paramString);
    try
    {
      super.parse(paramString);
    }
    catch (InternalError localInternalError)
    {
      explain(paramString);
      throw localInternalError;
    }
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
  {
    this.allowXMLCatalogPI = false;
    String str = this.catalogResolver.getResolvedEntity(paramString1, paramString2);
    if ((str == null) && (this.piCatalogResolver != null)) {
      str = this.piCatalogResolver.getResolvedEntity(paramString1, paramString2);
    }
    if (str != null) {
      try
      {
        InputSource localInputSource = new InputSource(str);
        localInputSource.setPublicId(paramString1);
        URL localURL = new URL(str);
        InputStream localInputStream = localURL.openStream();
        localInputSource.setByteStream(localInputStream);
        return localInputSource;
      }
      catch (Exception localException)
      {
        this.catalogManager.debug.message(1, "Failed to create InputSource", str);
        return null;
      }
    }
    return null;
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    this.allowXMLCatalogPI = false;
    super.notationDecl(paramString1, paramString2, paramString3);
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    this.allowXMLCatalogPI = false;
    super.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    this.allowXMLCatalogPI = false;
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (paramString1.equals("oasis-xml-catalog"))
    {
      URL localURL = null;
      String str1 = paramString2;
      int i = str1.indexOf("catalog=");
      if (i >= 0)
      {
        str1 = str1.substring(i + 8);
        if (str1.length() > 1)
        {
          String str2 = str1.substring(0, 1);
          str1 = str1.substring(1);
          i = str1.indexOf(str2);
          if (i >= 0)
          {
            str1 = str1.substring(0, i);
            try
            {
              if (this.baseURL != null) {
                localURL = new URL(this.baseURL, str1);
              } else {
                localURL = new URL(str1);
              }
            }
            catch (MalformedURLException localMalformedURLException) {}
          }
        }
      }
      if (this.allowXMLCatalogPI)
      {
        if (this.catalogManager.getAllowOasisXMLCatalogPI())
        {
          this.catalogManager.debug.message(4, "oasis-xml-catalog PI", paramString2);
          if (localURL != null) {
            try
            {
              this.catalogManager.debug.message(4, "oasis-xml-catalog", localURL.toString());
              this.oasisXMLCatalogPI = true;
              if (this.piCatalogResolver == null) {
                this.piCatalogResolver = new CatalogResolver(true);
              }
              this.piCatalogResolver.getCatalog().parseCatalog(localURL.toString());
            }
            catch (Exception localException)
            {
              this.catalogManager.debug.message(3, "Exception parsing oasis-xml-catalog: " + localURL.toString());
            }
          } else {
            this.catalogManager.debug.message(3, "PI oasis-xml-catalog unparseable: " + paramString2);
          }
        }
        else
        {
          this.catalogManager.debug.message(4, "PI oasis-xml-catalog ignored: " + paramString2);
        }
      }
      else {
        this.catalogManager.debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: " + paramString2);
      }
    }
    else
    {
      super.processingInstruction(paramString1, paramString2);
    }
  }
  
  private void setupBaseURI(String paramString)
  {
    URL localURL = null;
    try
    {
      localURL = FileURL.makeURL("basename");
    }
    catch (MalformedURLException localMalformedURLException1)
    {
      localURL = null;
    }
    try
    {
      this.baseURL = new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException2)
    {
      if (localURL != null) {
        try
        {
          this.baseURL = new URL(localURL, paramString);
        }
        catch (MalformedURLException localMalformedURLException3)
        {
          this.baseURL = null;
        }
      } else {
        this.baseURL = null;
      }
    }
  }
  
  private void explain(String paramString)
  {
    if (!suppressExplanation)
    {
      System.out.println("XMLReader probably encountered bad URI in " + paramString);
      System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
    }
    suppressExplanation = true;
  }
}
