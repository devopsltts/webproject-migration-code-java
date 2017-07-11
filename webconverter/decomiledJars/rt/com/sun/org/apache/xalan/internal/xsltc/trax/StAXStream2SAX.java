package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

public class StAXStream2SAX
  implements XMLReader, Locator
{
  private final XMLStreamReader staxStreamReader;
  private ContentHandler _sax = null;
  private LexicalHandler _lex = null;
  private SAXImpl _saxImpl = null;
  
  public StAXStream2SAX(XMLStreamReader paramXMLStreamReader)
  {
    this.staxStreamReader = paramXMLStreamReader;
  }
  
  public ContentHandler getContentHandler()
  {
    return this._sax;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
    throws NullPointerException
  {
    this._sax = paramContentHandler;
    if ((paramContentHandler instanceof LexicalHandler)) {
      this._lex = ((LexicalHandler)paramContentHandler);
    }
    if ((paramContentHandler instanceof SAXImpl)) {
      this._saxImpl = ((SAXImpl)paramContentHandler);
    }
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    try
    {
      bridge();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void parse()
    throws IOException, SAXException, XMLStreamException
  {
    bridge();
  }
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    throw new IOException("This method is not yet implemented.");
  }
  
  public void bridge()
    throws XMLStreamException
  {
    try
    {
      int i = 0;
      int j = this.staxStreamReader.getEventType();
      if (j == 7) {
        j = this.staxStreamReader.next();
      }
      if (j != 1)
      {
        j = this.staxStreamReader.nextTag();
        if (j != 1) {
          throw new IllegalStateException("The current event is not START_ELEMENT\n but" + j);
        }
      }
      handleStartDocument();
      do
      {
        switch (j)
        {
        case 1: 
          i++;
          handleStartElement();
          break;
        case 2: 
          handleEndElement();
          i--;
          break;
        case 4: 
          handleCharacters();
          break;
        case 9: 
          handleEntityReference();
          break;
        case 3: 
          handlePI();
          break;
        case 5: 
          handleComment();
          break;
        case 11: 
          handleDTD();
          break;
        case 10: 
          handleAttribute();
          break;
        case 13: 
          handleNamespace();
          break;
        case 12: 
          handleCDATA();
          break;
        case 15: 
          handleEntityDecl();
          break;
        case 14: 
          handleNotationDecl();
          break;
        case 6: 
          handleSpace();
          break;
        case 7: 
        case 8: 
        default: 
          throw new InternalError("processing event: " + j);
        }
        j = this.staxStreamReader.next();
      } while (i != 0);
      handleEndDocument();
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private void handleEndDocument()
    throws SAXException
  {
    this._sax.endDocument();
  }
  
  private void handleStartDocument()
    throws SAXException
  {
    this._sax.setDocumentLocator(new Locator2()
    {
      public int getColumnNumber()
      {
        return StAXStream2SAX.this.staxStreamReader.getLocation().getColumnNumber();
      }
      
      public int getLineNumber()
      {
        return StAXStream2SAX.this.staxStreamReader.getLocation().getLineNumber();
      }
      
      public String getPublicId()
      {
        return StAXStream2SAX.this.staxStreamReader.getLocation().getPublicId();
      }
      
      public String getSystemId()
      {
        return StAXStream2SAX.this.staxStreamReader.getLocation().getSystemId();
      }
      
      public String getXMLVersion()
      {
        return StAXStream2SAX.this.staxStreamReader.getVersion();
      }
      
      public String getEncoding()
      {
        return StAXStream2SAX.this.staxStreamReader.getEncoding();
      }
    });
    this._sax.startDocument();
  }
  
  private void handlePI()
    throws XMLStreamException
  {
    try
    {
      this._sax.processingInstruction(this.staxStreamReader.getPITarget(), this.staxStreamReader.getPIData());
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private void handleCharacters()
    throws XMLStreamException
  {
    int i = this.staxStreamReader.getTextLength();
    char[] arrayOfChar = new char[i];
    this.staxStreamReader.getTextCharacters(0, arrayOfChar, 0, i);
    try
    {
      this._sax.characters(arrayOfChar, 0, arrayOfChar.length);
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private void handleEndElement()
    throws XMLStreamException
  {
    QName localQName = this.staxStreamReader.getName();
    try
    {
      String str1 = "";
      if ((localQName.getPrefix() != null) && (localQName.getPrefix().trim().length() != 0)) {
        str1 = localQName.getPrefix() + ":";
      }
      str1 = str1 + localQName.getLocalPart();
      this._sax.endElement(localQName.getNamespaceURI(), localQName.getLocalPart(), str1);
      int i = this.staxStreamReader.getNamespaceCount();
      for (int j = i - 1; j >= 0; j--)
      {
        String str2 = this.staxStreamReader.getNamespacePrefix(j);
        if (str2 == null) {
          str2 = "";
        }
        this._sax.endPrefixMapping(str2);
      }
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private void handleStartElement()
    throws XMLStreamException
  {
    try
    {
      int i = this.staxStreamReader.getNamespaceCount();
      for (int j = 0; j < i; j++)
      {
        str1 = this.staxStreamReader.getNamespacePrefix(j);
        if (str1 == null) {
          str1 = "";
        }
        this._sax.startPrefixMapping(str1, this.staxStreamReader.getNamespaceURI(j));
      }
      QName localQName = this.staxStreamReader.getName();
      String str1 = localQName.getPrefix();
      String str2;
      if ((str1 == null) || (str1.length() == 0)) {
        str2 = localQName.getLocalPart();
      } else {
        str2 = str1 + ':' + localQName.getLocalPart();
      }
      Attributes localAttributes = getAttributes();
      this._sax.startElement(localQName.getNamespaceURI(), localQName.getLocalPart(), str2, localAttributes);
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private Attributes getAttributes()
  {
    AttributesImpl localAttributesImpl = new AttributesImpl();
    int i = this.staxStreamReader.getEventType();
    if ((i != 10) && (i != 1)) {
      throw new InternalError("getAttributes() attempting to process: " + i);
    }
    for (int j = 0; j < this.staxStreamReader.getAttributeCount(); j++)
    {
      String str1 = this.staxStreamReader.getAttributeNamespace(j);
      if (str1 == null) {
        str1 = "";
      }
      String str2 = this.staxStreamReader.getAttributeLocalName(j);
      String str3 = this.staxStreamReader.getAttributePrefix(j);
      String str4;
      if ((str3 == null) || (str3.length() == 0)) {
        str4 = str2;
      } else {
        str4 = str3 + ':' + str2;
      }
      String str5 = this.staxStreamReader.getAttributeType(j);
      String str6 = this.staxStreamReader.getAttributeValue(j);
      localAttributesImpl.addAttribute(str1, str2, str4, str5, str6);
    }
    return localAttributesImpl;
  }
  
  private void handleNamespace() {}
  
  private void handleAttribute() {}
  
  private void handleDTD() {}
  
  private void handleComment() {}
  
  private void handleEntityReference() {}
  
  private void handleSpace() {}
  
  private void handleNotationDecl() {}
  
  private void handleEntityDecl() {}
  
  private void handleCDATA() {}
  
  public DTDHandler getDTDHandler()
  {
    return null;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return null;
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return false;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {}
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
    throws NullPointerException
  {}
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
    throws NullPointerException
  {}
  
  public EntityResolver getEntityResolver()
  {
    return null;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
    throws NullPointerException
  {}
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {}
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return null;
  }
  
  public int getColumnNumber()
  {
    return 0;
  }
  
  public int getLineNumber()
  {
    return 0;
  }
  
  public String getPublicId()
  {
    return null;
  }
  
  public String getSystemId()
  {
    return null;
  }
}
