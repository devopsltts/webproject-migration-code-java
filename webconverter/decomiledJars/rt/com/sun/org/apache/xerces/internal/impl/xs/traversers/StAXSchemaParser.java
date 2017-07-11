package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
import com.sun.org.apache.xerces.internal.util.JAXPNamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.StAXLocationWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.w3c.dom.Document;

final class StAXSchemaParser
{
  private static final int CHUNK_SIZE = 1024;
  private static final int CHUNK_MASK = 1023;
  private final char[] fCharBuffer = new char['Ѐ'];
  private SymbolTable fSymbolTable;
  private SchemaDOMParser fSchemaDOMParser;
  private final StAXLocationWrapper fLocationWrapper = new StAXLocationWrapper();
  private final JAXPNamespaceContextWrapper fNamespaceContext = new JAXPNamespaceContextWrapper(this.fSymbolTable);
  private final com.sun.org.apache.xerces.internal.xni.QName fElementQName = new com.sun.org.apache.xerces.internal.xni.QName();
  private final com.sun.org.apache.xerces.internal.xni.QName fAttributeQName = new com.sun.org.apache.xerces.internal.xni.QName();
  private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
  private final XMLString fTempString = new XMLString();
  private final ArrayList fDeclaredPrefixes = new ArrayList();
  private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  private int fDepth;
  
  public StAXSchemaParser()
  {
    this.fNamespaceContext.setDeclaredPrefixes(this.fDeclaredPrefixes);
  }
  
  public void reset(SchemaDOMParser paramSchemaDOMParser, SymbolTable paramSymbolTable)
  {
    this.fSchemaDOMParser = paramSchemaDOMParser;
    this.fSymbolTable = paramSymbolTable;
    this.fNamespaceContext.setSymbolTable(this.fSymbolTable);
    this.fNamespaceContext.reset();
  }
  
  public Document getDocument()
  {
    return this.fSchemaDOMParser.getDocument();
  }
  
  public void parse(XMLEventReader paramXMLEventReader)
    throws XMLStreamException, XNIException
  {
    XMLEvent localXMLEvent = paramXMLEventReader.peek();
    if (localXMLEvent != null)
    {
      int i = localXMLEvent.getEventType();
      if ((i != 7) && (i != 1)) {
        throw new XMLStreamException();
      }
      this.fLocationWrapper.setLocation(localXMLEvent.getLocation());
      this.fSchemaDOMParser.startDocument(this.fLocationWrapper, null, this.fNamespaceContext, null);
      while (paramXMLEventReader.hasNext())
      {
        localXMLEvent = paramXMLEventReader.nextEvent();
        i = localXMLEvent.getEventType();
        switch (i)
        {
        case 1: 
          this.fDepth += 1;
          StartElement localStartElement = localXMLEvent.asStartElement();
          fillQName(this.fElementQName, localStartElement.getName());
          this.fLocationWrapper.setLocation(localStartElement.getLocation());
          this.fNamespaceContext.setNamespaceContext(localStartElement.getNamespaceContext());
          fillXMLAttributes(localStartElement);
          fillDeclaredPrefixes(localStartElement);
          addNamespaceDeclarations();
          this.fNamespaceContext.pushContext();
          this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
          break;
        case 2: 
          EndElement localEndElement = localXMLEvent.asEndElement();
          fillQName(this.fElementQName, localEndElement.getName());
          fillDeclaredPrefixes(localEndElement);
          this.fLocationWrapper.setLocation(localEndElement.getLocation());
          this.fSchemaDOMParser.endElement(this.fElementQName, null);
          this.fNamespaceContext.popContext();
          this.fDepth -= 1;
          if (this.fDepth > 0) {
            break;
          }
          break;
        case 4: 
          sendCharactersToSchemaParser(localXMLEvent.asCharacters().getData(), false);
          break;
        case 6: 
          sendCharactersToSchemaParser(localXMLEvent.asCharacters().getData(), true);
          break;
        case 12: 
          this.fSchemaDOMParser.startCDATA(null);
          sendCharactersToSchemaParser(localXMLEvent.asCharacters().getData(), false);
          this.fSchemaDOMParser.endCDATA(null);
          break;
        case 3: 
          ProcessingInstruction localProcessingInstruction = (ProcessingInstruction)localXMLEvent;
          fillProcessingInstruction(localProcessingInstruction.getData());
          this.fSchemaDOMParser.processingInstruction(localProcessingInstruction.getTarget(), this.fTempString, null);
          break;
        case 11: 
          break;
        case 9: 
          break;
        case 5: 
          break;
        case 7: 
          this.fDepth += 1;
        }
      }
      this.fLocationWrapper.setLocation(null);
      this.fNamespaceContext.setNamespaceContext(null);
      this.fSchemaDOMParser.endDocument(null);
    }
  }
  
  public void parse(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException, XNIException
  {
    if (paramXMLStreamReader.hasNext())
    {
      int i = paramXMLStreamReader.getEventType();
      if ((i != 7) && (i != 1)) {
        throw new XMLStreamException();
      }
      this.fLocationWrapper.setLocation(paramXMLStreamReader.getLocation());
      this.fSchemaDOMParser.startDocument(this.fLocationWrapper, null, this.fNamespaceContext, null);
      int j = 1;
      while (paramXMLStreamReader.hasNext())
      {
        if (j == 0) {
          i = paramXMLStreamReader.next();
        } else {
          j = 0;
        }
        switch (i)
        {
        case 1: 
          this.fDepth += 1;
          this.fLocationWrapper.setLocation(paramXMLStreamReader.getLocation());
          this.fNamespaceContext.setNamespaceContext(paramXMLStreamReader.getNamespaceContext());
          fillQName(this.fElementQName, paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName(), paramXMLStreamReader.getPrefix());
          fillXMLAttributes(paramXMLStreamReader);
          fillDeclaredPrefixes(paramXMLStreamReader);
          addNamespaceDeclarations();
          this.fNamespaceContext.pushContext();
          this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
          break;
        case 2: 
          this.fLocationWrapper.setLocation(paramXMLStreamReader.getLocation());
          this.fNamespaceContext.setNamespaceContext(paramXMLStreamReader.getNamespaceContext());
          fillQName(this.fElementQName, paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName(), paramXMLStreamReader.getPrefix());
          fillDeclaredPrefixes(paramXMLStreamReader);
          this.fSchemaDOMParser.endElement(this.fElementQName, null);
          this.fNamespaceContext.popContext();
          this.fDepth -= 1;
          if (this.fDepth > 0) {
            break;
          }
          break;
        case 4: 
          this.fTempString.setValues(paramXMLStreamReader.getTextCharacters(), paramXMLStreamReader.getTextStart(), paramXMLStreamReader.getTextLength());
          this.fSchemaDOMParser.characters(this.fTempString, null);
          break;
        case 6: 
          this.fTempString.setValues(paramXMLStreamReader.getTextCharacters(), paramXMLStreamReader.getTextStart(), paramXMLStreamReader.getTextLength());
          this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
          break;
        case 12: 
          this.fSchemaDOMParser.startCDATA(null);
          this.fTempString.setValues(paramXMLStreamReader.getTextCharacters(), paramXMLStreamReader.getTextStart(), paramXMLStreamReader.getTextLength());
          this.fSchemaDOMParser.characters(this.fTempString, null);
          this.fSchemaDOMParser.endCDATA(null);
          break;
        case 3: 
          fillProcessingInstruction(paramXMLStreamReader.getPIData());
          this.fSchemaDOMParser.processingInstruction(paramXMLStreamReader.getPITarget(), this.fTempString, null);
          break;
        case 11: 
          break;
        case 9: 
          break;
        case 5: 
          break;
        case 7: 
          this.fDepth += 1;
        }
      }
      this.fLocationWrapper.setLocation(null);
      this.fNamespaceContext.setNamespaceContext(null);
      this.fSchemaDOMParser.endDocument(null);
    }
  }
  
  private void sendCharactersToSchemaParser(String paramString, boolean paramBoolean)
  {
    if (paramString != null)
    {
      int i = paramString.length();
      int j = i & 0x3FF;
      if (j > 0)
      {
        paramString.getChars(0, j, this.fCharBuffer, 0);
        this.fTempString.setValues(this.fCharBuffer, 0, j);
        if (paramBoolean) {
          this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
        } else {
          this.fSchemaDOMParser.characters(this.fTempString, null);
        }
      }
      int k = j;
      while (k < i)
      {
        k += 1024;
        paramString.getChars(k, k, this.fCharBuffer, 0);
        this.fTempString.setValues(this.fCharBuffer, 0, 1024);
        if (paramBoolean) {
          this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
        } else {
          this.fSchemaDOMParser.characters(this.fTempString, null);
        }
      }
    }
  }
  
  private void fillProcessingInstruction(String paramString)
  {
    int i = paramString.length();
    char[] arrayOfChar = this.fCharBuffer;
    if (arrayOfChar.length < i) {
      arrayOfChar = paramString.toCharArray();
    } else {
      paramString.getChars(0, i, arrayOfChar, 0);
    }
    this.fTempString.setValues(arrayOfChar, 0, i);
  }
  
  private void fillXMLAttributes(StartElement paramStartElement)
  {
    this.fAttributes.removeAllAttributes();
    Iterator localIterator = paramStartElement.getAttributes();
    while (localIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)localIterator.next();
      fillQName(this.fAttributeQName, localAttribute.getName());
      String str = localAttribute.getDTDType();
      int i = this.fAttributes.getLength();
      this.fAttributes.addAttributeNS(this.fAttributeQName, str != null ? str : XMLSymbols.fCDATASymbol, localAttribute.getValue());
      this.fAttributes.setSpecified(i, localAttribute.isSpecified());
    }
  }
  
  private void fillXMLAttributes(XMLStreamReader paramXMLStreamReader)
  {
    this.fAttributes.removeAllAttributes();
    int i = paramXMLStreamReader.getAttributeCount();
    for (int j = 0; j < i; j++)
    {
      fillQName(this.fAttributeQName, paramXMLStreamReader.getAttributeNamespace(j), paramXMLStreamReader.getAttributeLocalName(j), paramXMLStreamReader.getAttributePrefix(j));
      String str = paramXMLStreamReader.getAttributeType(j);
      this.fAttributes.addAttributeNS(this.fAttributeQName, str != null ? str : XMLSymbols.fCDATASymbol, paramXMLStreamReader.getAttributeValue(j));
      this.fAttributes.setSpecified(j, paramXMLStreamReader.isAttributeSpecified(j));
    }
  }
  
  private void addNamespaceDeclarations()
  {
    String str1 = null;
    Object localObject = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    Iterator localIterator = this.fDeclaredPrefixes.iterator();
    while (localIterator.hasNext())
    {
      str3 = (String)localIterator.next();
      str4 = this.fNamespaceContext.getURI(str3);
      if (str3.length() > 0)
      {
        str1 = XMLSymbols.PREFIX_XMLNS;
        localObject = str3;
        this.fStringBuffer.clear();
        this.fStringBuffer.append(str1);
        this.fStringBuffer.append(':');
        this.fStringBuffer.append((String)localObject);
        str2 = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
      }
      else
      {
        str1 = XMLSymbols.EMPTY_STRING;
        localObject = XMLSymbols.PREFIX_XMLNS;
        str2 = XMLSymbols.PREFIX_XMLNS;
      }
      this.fAttributeQName.setValues(str1, (String)localObject, str2, NamespaceContext.XMLNS_URI);
      this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, str4 != null ? str4 : XMLSymbols.EMPTY_STRING);
    }
  }
  
  private void fillDeclaredPrefixes(StartElement paramStartElement)
  {
    fillDeclaredPrefixes(paramStartElement.getNamespaces());
  }
  
  private void fillDeclaredPrefixes(EndElement paramEndElement)
  {
    fillDeclaredPrefixes(paramEndElement.getNamespaces());
  }
  
  private void fillDeclaredPrefixes(Iterator paramIterator)
  {
    this.fDeclaredPrefixes.clear();
    while (paramIterator.hasNext())
    {
      Namespace localNamespace = (Namespace)paramIterator.next();
      String str = localNamespace.getPrefix();
      this.fDeclaredPrefixes.add(str != null ? str : "");
    }
  }
  
  private void fillDeclaredPrefixes(XMLStreamReader paramXMLStreamReader)
  {
    this.fDeclaredPrefixes.clear();
    int i = paramXMLStreamReader.getNamespaceCount();
    for (int j = 0; j < i; j++)
    {
      String str = paramXMLStreamReader.getNamespacePrefix(j);
      this.fDeclaredPrefixes.add(str != null ? str : "");
    }
  }
  
  private void fillQName(com.sun.org.apache.xerces.internal.xni.QName paramQName, javax.xml.namespace.QName paramQName1)
  {
    fillQName(paramQName, paramQName1.getNamespaceURI(), paramQName1.getLocalPart(), paramQName1.getPrefix());
  }
  
  final void fillQName(com.sun.org.apache.xerces.internal.xni.QName paramQName, String paramString1, String paramString2, String paramString3)
  {
    paramString1 = (paramString1 != null) && (paramString1.length() > 0) ? this.fSymbolTable.addSymbol(paramString1) : null;
    paramString2 = paramString2 != null ? this.fSymbolTable.addSymbol(paramString2) : XMLSymbols.EMPTY_STRING;
    paramString3 = (paramString3 != null) && (paramString3.length() > 0) ? this.fSymbolTable.addSymbol(paramString3) : XMLSymbols.EMPTY_STRING;
    String str = paramString2;
    if (paramString3 != XMLSymbols.EMPTY_STRING)
    {
      this.fStringBuffer.clear();
      this.fStringBuffer.append(paramString3);
      this.fStringBuffer.append(':');
      this.fStringBuffer.append(paramString2);
      str = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
    }
    paramQName.setValues(paramString3, paramString2, str, paramString1);
  }
}
