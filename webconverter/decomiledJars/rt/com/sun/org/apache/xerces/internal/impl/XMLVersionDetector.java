package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import java.io.EOFException;
import java.io.IOException;

public class XMLVersionDetector
{
  private static final char[] XML11_VERSION = { '1', '.', '1' };
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  protected static final String fVersionSymbol = "version".intern();
  protected static final String fXMLSymbol = "[xml]".intern();
  protected SymbolTable fSymbolTable;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityManager fEntityManager;
  protected String fEncoding = null;
  private XMLString fVersionNum = new XMLString();
  private final char[] fExpectedVersionString = { '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', ' ', ' ', ' ', ' ', ' ' };
  
  public XMLVersionDetector() {}
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    this.fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    this.fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    this.fEntityManager = ((XMLEntityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
    for (int i = 14; i < this.fExpectedVersionString.length; i++) {
      this.fExpectedVersionString[i] = ' ';
    }
  }
  
  public void startDocumentParsing(XMLEntityHandler paramXMLEntityHandler, short paramShort)
  {
    if (paramShort == 1) {
      this.fEntityManager.setScannerVersion((short)1);
    } else {
      this.fEntityManager.setScannerVersion((short)2);
    }
    this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    this.fEntityManager.setEntityHandler(paramXMLEntityHandler);
    paramXMLEntityHandler.startEntity(fXMLSymbol, this.fEntityManager.getCurrentResourceIdentifier(), this.fEncoding, null);
  }
  
  public short determineDocVersion(XMLInputSource paramXMLInputSource)
    throws IOException
  {
    this.fEncoding = this.fEntityManager.setupCurrentEntity(false, fXMLSymbol, paramXMLInputSource, false, true);
    this.fEntityManager.setScannerVersion((short)1);
    XMLEntityScanner localXMLEntityScanner = this.fEntityManager.getEntityScanner();
    try
    {
      if (!localXMLEntityScanner.skipString("<?xml")) {
        return 1;
      }
      if (!localXMLEntityScanner.skipDeclSpaces())
      {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 5);
        return 1;
      }
      if (!localXMLEntityScanner.skipString("version"))
      {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 6);
        return 1;
      }
      localXMLEntityScanner.skipDeclSpaces();
      if (localXMLEntityScanner.peekChar() != 61)
      {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 13);
        return 1;
      }
      localXMLEntityScanner.scanChar();
      localXMLEntityScanner.skipDeclSpaces();
      int i = localXMLEntityScanner.scanChar();
      this.fExpectedVersionString[14] = ((char)i);
      for (int j = 0; j < XML11_VERSION.length; j++) {
        this.fExpectedVersionString[(15 + j)] = ((char)localXMLEntityScanner.scanChar());
      }
      this.fExpectedVersionString[18] = ((char)localXMLEntityScanner.scanChar());
      fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 19);
      for (j = 0; (j < XML11_VERSION.length) && (this.fExpectedVersionString[(15 + j)] == XML11_VERSION[j]); j++) {}
      if (j == XML11_VERSION.length) {
        return 2;
      }
      return 1;
    }
    catch (EOFException localEOFException)
    {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "PrematureEOF", null, (short)2);
    }
    return 1;
  }
  
  private void fixupCurrentEntity(XMLEntityManager paramXMLEntityManager, char[] paramArrayOfChar, int paramInt)
  {
    Entity.ScannedEntity localScannedEntity = paramXMLEntityManager.getCurrentEntity();
    if (localScannedEntity.count - localScannedEntity.position + paramInt > localScannedEntity.ch.length)
    {
      char[] arrayOfChar = localScannedEntity.ch;
      localScannedEntity.ch = new char[paramInt + localScannedEntity.count - localScannedEntity.position + 1];
      System.arraycopy(arrayOfChar, 0, localScannedEntity.ch, 0, arrayOfChar.length);
    }
    if (localScannedEntity.position < paramInt)
    {
      System.arraycopy(localScannedEntity.ch, localScannedEntity.position, localScannedEntity.ch, paramInt, localScannedEntity.count - localScannedEntity.position);
      localScannedEntity.count += paramInt - localScannedEntity.position;
    }
    else
    {
      for (int i = paramInt; i < localScannedEntity.position; i++) {
        localScannedEntity.ch[i] = ' ';
      }
    }
    System.arraycopy(paramArrayOfChar, 0, localScannedEntity.ch, 0, paramInt);
    localScannedEntity.position = 0;
    localScannedEntity.baseCharOffset = 0;
    localScannedEntity.startPosition = 0;
    localScannedEntity.columnNumber = (localScannedEntity.lineNumber = 1);
  }
}
