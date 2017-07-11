package org.xml.sax.helpers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class ParserAdapter
  implements XMLReader, DocumentHandler
{
  private static SecuritySupport ss = new SecuritySupport();
  private static final String FEATURES = "http://xml.org/sax/features/";
  private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
  private static final String XMLNS_URIs = "http://xml.org/sax/features/xmlns-uris";
  private NamespaceSupport nsSupport;
  private AttributeListAdapter attAdapter;
  private boolean parsing = false;
  private String[] nameParts = new String[3];
  private Parser parser = null;
  private AttributesImpl atts = null;
  private boolean namespaces = true;
  private boolean prefixes = false;
  private boolean uris = false;
  Locator locator;
  EntityResolver entityResolver = null;
  DTDHandler dtdHandler = null;
  ContentHandler contentHandler = null;
  ErrorHandler errorHandler = null;
  
  public ParserAdapter()
    throws SAXException
  {
    String str = ss.getSystemProperty("org.xml.sax.parser");
    try
    {
      setup(ParserFactory.makeParser());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new SAXException("Cannot find SAX1 driver class " + str, localClassNotFoundException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new SAXException("SAX1 driver class " + str + " found but cannot be loaded", localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new SAXException("SAX1 driver class " + str + " loaded but cannot be instantiated", localInstantiationException);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new SAXException("SAX1 driver class " + str + " does not implement org.xml.sax.Parser");
    }
    catch (NullPointerException localNullPointerException)
    {
      throw new SAXException("System property org.xml.sax.parser not specified");
    }
  }
  
  public ParserAdapter(Parser paramParser)
  {
    setup(paramParser);
  }
  
  private void setup(Parser paramParser)
  {
    if (paramParser == null) {
      throw new NullPointerException("Parser argument must not be null");
    }
    this.parser = paramParser;
    this.atts = new AttributesImpl();
    this.nsSupport = new NamespaceSupport();
    this.attAdapter = new AttributeListAdapter();
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://xml.org/sax/features/namespaces"))
    {
      checkNotParsing("feature", paramString);
      this.namespaces = paramBoolean;
      if ((!this.namespaces) && (!this.prefixes)) {
        this.prefixes = true;
      }
    }
    else if (paramString.equals("http://xml.org/sax/features/namespace-prefixes"))
    {
      checkNotParsing("feature", paramString);
      this.prefixes = paramBoolean;
      if ((!this.prefixes) && (!this.namespaces)) {
        this.namespaces = true;
      }
    }
    else if (paramString.equals("http://xml.org/sax/features/xmlns-uris"))
    {
      checkNotParsing("feature", paramString);
      this.uris = paramBoolean;
    }
    else
    {
      throw new SAXNotRecognizedException("Feature: " + paramString);
    }
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://xml.org/sax/features/namespaces")) {
      return this.namespaces;
    }
    if (paramString.equals("http://xml.org/sax/features/namespace-prefixes")) {
      return this.prefixes;
    }
    if (paramString.equals("http://xml.org/sax/features/xmlns-uris")) {
      return this.uris;
    }
    throw new SAXNotRecognizedException("Feature: " + paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    throw new SAXNotRecognizedException("Property: " + paramString);
  }
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    throw new SAXNotRecognizedException("Property: " + paramString);
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
  {
    this.entityResolver = paramEntityResolver;
  }
  
  public EntityResolver getEntityResolver()
  {
    return this.entityResolver;
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
  {
    this.dtdHandler = paramDTDHandler;
  }
  
  public DTDHandler getDTDHandler()
  {
    return this.dtdHandler;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    this.contentHandler = paramContentHandler;
  }
  
  public ContentHandler getContentHandler()
  {
    return this.contentHandler;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    this.errorHandler = paramErrorHandler;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return this.errorHandler;
  }
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    parse(new InputSource(paramString));
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    if (this.parsing) {
      throw new SAXException("Parser is already in use");
    }
    setupParser();
    this.parsing = true;
    try
    {
      this.parser.parse(paramInputSource);
      this.parsing = false;
    }
    finally
    {
      this.parsing = false;
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    this.locator = paramLocator;
    if (this.contentHandler != null) {
      this.contentHandler.setDocumentLocator(paramLocator);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    if (this.contentHandler != null) {
      this.contentHandler.startDocument();
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    if (this.contentHandler != null) {
      this.contentHandler.endDocument();
    }
  }
  
  public void startElement(String paramString, AttributeList paramAttributeList)
    throws SAXException
  {
    Vector localVector = null;
    if (!this.namespaces)
    {
      if (this.contentHandler != null)
      {
        this.attAdapter.setAttributeList(paramAttributeList);
        this.contentHandler.startElement("", "", paramString.intern(), this.attAdapter);
      }
      return;
    }
    this.nsSupport.pushContext();
    int i = paramAttributeList.getLength();
    String str1;
    String str2;
    Object localObject;
    for (int j = 0; j < i; j++)
    {
      str1 = paramAttributeList.getName(j);
      if (str1.startsWith("xmlns"))
      {
        int k = str1.indexOf(':');
        if ((k == -1) && (str1.length() == 5))
        {
          str2 = "";
        }
        else
        {
          if (k != 5) {
            continue;
          }
          str2 = str1.substring(k + 1);
        }
        localObject = paramAttributeList.getValue(j);
        if (!this.nsSupport.declarePrefix(str2, (String)localObject)) {
          reportError("Illegal Namespace prefix: " + str2);
        } else if (this.contentHandler != null) {
          this.contentHandler.startPrefixMapping(str2, (String)localObject);
        }
      }
    }
    this.atts.clear();
    for (j = 0; j < i; j++)
    {
      str1 = paramAttributeList.getName(j);
      str2 = paramAttributeList.getType(j);
      String str3 = paramAttributeList.getValue(j);
      if (str1.startsWith("xmlns"))
      {
        int m = str1.indexOf(':');
        if ((m == -1) && (str1.length() == 5)) {
          localObject = "";
        } else if (m != 5) {
          localObject = null;
        } else {
          localObject = str1.substring(6);
        }
        if (localObject != null)
        {
          if (!this.prefixes) {
            continue;
          }
          if (this.uris)
          {
            this.atts.addAttribute("http://www.w3.org/XML/1998/namespace", (String)localObject, str1.intern(), str2, str3);
            continue;
          }
          this.atts.addAttribute("", "", str1.intern(), str2, str3);
          continue;
        }
      }
      try
      {
        localObject = processName(str1, true, true);
        this.atts.addAttribute(localObject[0], localObject[1], localObject[2], str2, str3);
      }
      catch (SAXException localSAXException)
      {
        if (localVector == null) {
          localVector = new Vector();
        }
        localVector.addElement(localSAXException);
        this.atts.addAttribute("", str1, str1, str2, str3);
      }
    }
    if ((localVector != null) && (this.errorHandler != null)) {
      for (j = 0; j < localVector.size(); j++) {
        this.errorHandler.error((SAXParseException)localVector.elementAt(j));
      }
    }
    if (this.contentHandler != null)
    {
      String[] arrayOfString = processName(paramString, false, false);
      this.contentHandler.startElement(arrayOfString[0], arrayOfString[1], arrayOfString[2], this.atts);
    }
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    if (!this.namespaces)
    {
      if (this.contentHandler != null) {
        this.contentHandler.endElement("", "", paramString.intern());
      }
      return;
    }
    String[] arrayOfString = processName(paramString, false, false);
    if (this.contentHandler != null)
    {
      this.contentHandler.endElement(arrayOfString[0], arrayOfString[1], arrayOfString[2]);
      Enumeration localEnumeration = this.nsSupport.getDeclaredPrefixes();
      while (localEnumeration.hasMoreElements())
      {
        String str = (String)localEnumeration.nextElement();
        this.contentHandler.endPrefixMapping(str);
      }
    }
    this.nsSupport.popContext();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this.contentHandler != null) {
      this.contentHandler.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this.contentHandler != null) {
      this.contentHandler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (this.contentHandler != null) {
      this.contentHandler.processingInstruction(paramString1, paramString2);
    }
  }
  
  private void setupParser()
  {
    if ((!this.prefixes) && (!this.namespaces)) {
      throw new IllegalStateException();
    }
    this.nsSupport.reset();
    if (this.uris) {
      this.nsSupport.setNamespaceDeclUris(true);
    }
    if (this.entityResolver != null) {
      this.parser.setEntityResolver(this.entityResolver);
    }
    if (this.dtdHandler != null) {
      this.parser.setDTDHandler(this.dtdHandler);
    }
    if (this.errorHandler != null) {
      this.parser.setErrorHandler(this.errorHandler);
    }
    this.parser.setDocumentHandler(this);
    this.locator = null;
  }
  
  private String[] processName(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws SAXException
  {
    String[] arrayOfString = this.nsSupport.processName(paramString, this.nameParts, paramBoolean1);
    if (arrayOfString == null)
    {
      if (paramBoolean2) {
        throw makeException("Undeclared prefix: " + paramString);
      }
      reportError("Undeclared prefix: " + paramString);
      arrayOfString = new String[3];
      String tmp85_83 = "";
      arrayOfString[1] = tmp85_83;
      arrayOfString[0] = tmp85_83;
      arrayOfString[2] = paramString.intern();
    }
    return arrayOfString;
  }
  
  void reportError(String paramString)
    throws SAXException
  {
    if (this.errorHandler != null) {
      this.errorHandler.error(makeException(paramString));
    }
  }
  
  private SAXParseException makeException(String paramString)
  {
    if (this.locator != null) {
      return new SAXParseException(paramString, this.locator);
    }
    return new SAXParseException(paramString, null, null, -1, -1);
  }
  
  private void checkNotParsing(String paramString1, String paramString2)
    throws SAXNotSupportedException
  {
    if (this.parsing) {
      throw new SAXNotSupportedException("Cannot change " + paramString1 + ' ' + paramString2 + " while parsing");
    }
  }
  
  final class AttributeListAdapter
    implements Attributes
  {
    private AttributeList qAtts;
    
    AttributeListAdapter() {}
    
    void setAttributeList(AttributeList paramAttributeList)
    {
      this.qAtts = paramAttributeList;
    }
    
    public int getLength()
    {
      return this.qAtts.getLength();
    }
    
    public String getURI(int paramInt)
    {
      return "";
    }
    
    public String getLocalName(int paramInt)
    {
      return "";
    }
    
    public String getQName(int paramInt)
    {
      return this.qAtts.getName(paramInt).intern();
    }
    
    public String getType(int paramInt)
    {
      return this.qAtts.getType(paramInt).intern();
    }
    
    public String getValue(int paramInt)
    {
      return this.qAtts.getValue(paramInt);
    }
    
    public int getIndex(String paramString1, String paramString2)
    {
      return -1;
    }
    
    public int getIndex(String paramString)
    {
      int i = ParserAdapter.this.atts.getLength();
      for (int j = 0; j < i; j++) {
        if (this.qAtts.getName(j).equals(paramString)) {
          return j;
        }
      }
      return -1;
    }
    
    public String getType(String paramString1, String paramString2)
    {
      return null;
    }
    
    public String getType(String paramString)
    {
      return this.qAtts.getType(paramString).intern();
    }
    
    public String getValue(String paramString1, String paramString2)
    {
      return null;
    }
    
    public String getValue(String paramString)
    {
      return this.qAtts.getValue(paramString);
    }
  }
}
