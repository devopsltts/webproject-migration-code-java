package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class StAX2SAXReader
{
  ContentHandler _handler;
  LexicalHandler _lexicalHandler;
  XMLStreamReader _reader;
  
  public StAX2SAXReader(XMLStreamReader paramXMLStreamReader, ContentHandler paramContentHandler)
  {
    this._handler = paramContentHandler;
    this._reader = paramXMLStreamReader;
  }
  
  public StAX2SAXReader(XMLStreamReader paramXMLStreamReader)
  {
    this._reader = paramXMLStreamReader;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    this._handler = paramContentHandler;
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    this._lexicalHandler = paramLexicalHandler;
  }
  
  public void adapt()
    throws XMLStreamException, SAXException
  {
    AttributesImpl localAttributesImpl = new AttributesImpl();
    this._handler.startDocument();
    try
    {
      while (this._reader.hasNext())
      {
        int k = this._reader.next();
        int i;
        int m;
        QName localQName1;
        String str1;
        String str2;
        switch (k)
        {
        case 1: 
          i = this._reader.getNamespaceCount();
          for (m = 0; m < i; m++) {
            this._handler.startPrefixMapping(this._reader.getNamespacePrefix(m), this._reader.getNamespaceURI(m));
          }
          localAttributesImpl.clear();
          int j = this._reader.getAttributeCount();
          for (m = 0; m < j; m++)
          {
            QName localQName2 = this._reader.getAttributeName(m);
            String str3 = this._reader.getAttributePrefix(m);
            if ((str3 == null) || (str3 == "")) {
              str3 = localQName2.getLocalPart();
            } else {
              str3 = str3 + ":" + localQName2.getLocalPart();
            }
            localAttributesImpl.addAttribute(this._reader.getAttributeNamespace(m), localQName2.getLocalPart(), str3, this._reader.getAttributeType(m), this._reader.getAttributeValue(m));
          }
          localQName1 = this._reader.getName();
          str1 = localQName1.getPrefix();
          str2 = localQName1.getLocalPart();
          this._handler.startElement(this._reader.getNamespaceURI(), str2, str1.length() > 0 ? str1 + ":" + str2 : str2, localAttributesImpl);
          break;
        case 2: 
          localQName1 = this._reader.getName();
          str1 = localQName1.getPrefix();
          str2 = localQName1.getLocalPart();
          this._handler.endElement(this._reader.getNamespaceURI(), str2, str1.length() > 0 ? str1 + ":" + str2 : str2);
          i = this._reader.getNamespaceCount();
          for (m = 0; m < i; m++) {
            this._handler.endPrefixMapping(this._reader.getNamespacePrefix(m));
          }
          break;
        case 4: 
          this._handler.characters(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
          break;
        case 5: 
          this._lexicalHandler.comment(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
          break;
        case 3: 
          this._handler.processingInstruction(this._reader.getPITarget(), this._reader.getPIData());
          break;
        case 8: 
          break;
        case 6: 
        case 7: 
        default: 
          throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.StAX2SAXReader", new Object[] { Integer.valueOf(k) }));
        }
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      this._handler.endDocument();
      throw localXMLStreamException;
    }
    this._handler.endDocument();
  }
}
