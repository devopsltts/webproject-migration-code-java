package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.jaxp.validation.WrappedSAXException;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAX2XNI
  implements ContentHandler, XMLDocumentSource
{
  private XMLDocumentHandler fCore;
  private final NamespaceSupport nsContext = new NamespaceSupport();
  private final SymbolTable symbolTable = new SymbolTable();
  private Locator locator;
  private final XMLAttributes xa = new XMLAttributesImpl();
  
  public SAX2XNI(XMLDocumentHandler paramXMLDocumentHandler)
  {
    this.fCore = paramXMLDocumentHandler;
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler)
  {
    this.fCore = paramXMLDocumentHandler;
  }
  
  public XMLDocumentHandler getDocumentHandler()
  {
    return this.fCore;
  }
  
  public void startDocument()
    throws SAXException
  {
    try
    {
      this.nsContext.reset();
      Object localObject;
      if (this.locator == null) {
        localObject = new SimpleLocator(null, null, -1, -1);
      } else {
        localObject = new LocatorWrapper(this.locator);
      }
      this.fCore.startDocument((XMLLocator)localObject, null, this.nsContext, null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw localWrappedSAXException.exception;
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      this.fCore.endDocument(null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw localWrappedSAXException.exception;
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    try
    {
      this.fCore.startElement(createQName(paramString1, paramString2, paramString3), createAttributes(paramAttributes), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw localWrappedSAXException.exception;
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      this.fCore.endElement(createQName(paramString1, paramString2, paramString3), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw localWrappedSAXException.exception;
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      this.fCore.characters(new XMLString(paramArrayOfChar, paramInt1, paramInt2), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw localWrappedSAXException.exception;
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      this.fCore.ignorableWhitespace(new XMLString(paramArrayOfChar, paramInt1, paramInt2), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw localWrappedSAXException.exception;
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    this.nsContext.pushContext();
    this.nsContext.declarePrefix(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString)
  {
    this.nsContext.popContext();
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      this.fCore.processingInstruction(symbolize(paramString1), createXMLString(paramString2), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw localWrappedSAXException.exception;
    }
  }
  
  public void skippedEntity(String paramString) {}
  
  public void setDocumentLocator(Locator paramLocator)
  {
    this.locator = paramLocator;
  }
  
  private QName createQName(String paramString1, String paramString2, String paramString3)
  {
    int i = paramString3.indexOf(':');
    if (paramString2.length() == 0)
    {
      paramString1 = "";
      if (i < 0) {
        paramString2 = paramString3;
      } else {
        paramString2 = paramString3.substring(i + 1);
      }
    }
    String str;
    if (i < 0) {
      str = null;
    } else {
      str = paramString3.substring(0, i);
    }
    if ((paramString1 != null) && (paramString1.length() == 0)) {
      paramString1 = null;
    }
    return new QName(symbolize(str), symbolize(paramString2), symbolize(paramString3), symbolize(paramString1));
  }
  
  private String symbolize(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return this.symbolTable.addSymbol(paramString);
  }
  
  private XMLString createXMLString(String paramString)
  {
    return new XMLString(paramString.toCharArray(), 0, paramString.length());
  }
  
  private XMLAttributes createAttributes(Attributes paramAttributes)
  {
    this.xa.removeAllAttributes();
    int i = paramAttributes.getLength();
    for (int j = 0; j < i; j++) {
      this.xa.addAttribute(createQName(paramAttributes.getURI(j), paramAttributes.getLocalName(j), paramAttributes.getQName(j)), paramAttributes.getType(j), paramAttributes.getValue(j));
    }
    return this.xa;
  }
}
