package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import com.sun.xml.internal.stream.XMLBufferListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.Vector;

public class XMLEntityScanner
  implements XMLLocator
{
  protected Entity.ScannedEntity fCurrentEntity = null;
  protected int fBufferSize = 8192;
  protected XMLEntityManager fEntityManager;
  protected XMLSecurityManager fSecurityManager = null;
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  private static final boolean DEBUG_ENCODINGS = false;
  private Vector listeners = new Vector();
  private static final boolean[] VALID_NAMES = new boolean[127];
  private static final boolean DEBUG_BUFFER = false;
  private static final boolean DEBUG_SKIP_STRING = false;
  private static final EOFException END_OF_DOCUMENT_ENTITY = new EOFException()
  {
    private static final long serialVersionUID = 980337771224675268L;
    
    public Throwable fillInStackTrace()
    {
      return this;
    }
  };
  protected SymbolTable fSymbolTable = null;
  protected XMLErrorReporter fErrorReporter = null;
  int[] whiteSpaceLookup = new int[100];
  int whiteSpaceLen = 0;
  boolean whiteSpaceInfoNeeded = true;
  protected boolean fAllowJavaEncodings;
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  protected PropertyManager fPropertyManager = null;
  boolean isExternal = false;
  boolean xmlVersionSetExplicitly = false;
  
  public XMLEntityScanner() {}
  
  public XMLEntityScanner(PropertyManager paramPropertyManager, XMLEntityManager paramXMLEntityManager)
  {
    this.fEntityManager = paramXMLEntityManager;
    reset(paramPropertyManager);
  }
  
  public final void setBufferSize(int paramInt)
  {
    this.fBufferSize = paramInt;
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    this.fSymbolTable = ((SymbolTable)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    this.fErrorReporter = ((XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    resetCommon();
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    this.fAllowJavaEncodings = paramXMLComponentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
    this.fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    this.fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    resetCommon();
  }
  
  public final void reset(SymbolTable paramSymbolTable, XMLEntityManager paramXMLEntityManager, XMLErrorReporter paramXMLErrorReporter)
  {
    this.fCurrentEntity = null;
    this.fSymbolTable = paramSymbolTable;
    this.fEntityManager = paramXMLEntityManager;
    this.fErrorReporter = paramXMLErrorReporter;
    this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
    this.fSecurityManager = this.fEntityManager.fSecurityManager;
  }
  
  private void resetCommon()
  {
    this.fCurrentEntity = null;
    this.whiteSpaceLen = 0;
    this.whiteSpaceInfoNeeded = true;
    this.listeners.clear();
    this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
    this.fSecurityManager = this.fEntityManager.fSecurityManager;
  }
  
  public final String getXMLVersion()
  {
    if (this.fCurrentEntity != null) {
      return this.fCurrentEntity.xmlVersion;
    }
    return null;
  }
  
  public final void setXMLVersion(String paramString)
  {
    this.xmlVersionSetExplicitly = true;
    this.fCurrentEntity.xmlVersion = paramString;
  }
  
  public final void setCurrentEntity(Entity.ScannedEntity paramScannedEntity)
  {
    this.fCurrentEntity = paramScannedEntity;
    if (this.fCurrentEntity != null) {
      this.isExternal = this.fCurrentEntity.isExternal();
    }
  }
  
  public Entity.ScannedEntity getCurrentEntity()
  {
    return this.fCurrentEntity;
  }
  
  public final String getBaseSystemId()
  {
    return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
  }
  
  public void setBaseSystemId(String paramString) {}
  
  public final int getLineNumber()
  {
    return this.fCurrentEntity != null ? this.fCurrentEntity.lineNumber : -1;
  }
  
  public void setLineNumber(int paramInt) {}
  
  public final int getColumnNumber()
  {
    return this.fCurrentEntity != null ? this.fCurrentEntity.columnNumber : -1;
  }
  
  public void setColumnNumber(int paramInt) {}
  
  public final int getCharacterOffset()
  {
    return this.fCurrentEntity != null ? this.fCurrentEntity.fTotalCountTillLastLoad + this.fCurrentEntity.position : -1;
  }
  
  public final String getExpandedSystemId()
  {
    return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
  }
  
  public void setExpandedSystemId(String paramString) {}
  
  public final String getLiteralSystemId()
  {
    return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getLiteralSystemId() : null;
  }
  
  public void setLiteralSystemId(String paramString) {}
  
  public final String getPublicId()
  {
    return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null;
  }
  
  public void setPublicId(String paramString) {}
  
  public void setVersion(String paramString)
  {
    this.fCurrentEntity.version = paramString;
  }
  
  public String getVersion()
  {
    if (this.fCurrentEntity != null) {
      return this.fCurrentEntity.version;
    }
    return null;
  }
  
  public final String getEncoding()
  {
    if (this.fCurrentEntity != null) {
      return this.fCurrentEntity.encoding;
    }
    return null;
  }
  
  public final void setEncoding(String paramString)
    throws IOException
  {
    if ((this.fCurrentEntity.stream != null) && ((this.fCurrentEntity.encoding == null) || (!this.fCurrentEntity.encoding.equals(paramString))))
    {
      if ((this.fCurrentEntity.encoding != null) && (this.fCurrentEntity.encoding.startsWith("UTF-16")))
      {
        String str = paramString.toUpperCase(Locale.ENGLISH);
        if (str.equals("UTF-16")) {
          return;
        }
        if (str.equals("ISO-10646-UCS-4"))
        {
          if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
            this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)8);
          } else {
            this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)4);
          }
          return;
        }
        if (str.equals("ISO-10646-UCS-2"))
        {
          if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
            this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)2);
          } else {
            this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)1);
          }
          return;
        }
      }
      this.fCurrentEntity.reader = createReader(this.fCurrentEntity.stream, paramString, null);
      this.fCurrentEntity.encoding = paramString;
    }
  }
  
  public final boolean isExternal()
  {
    return this.fCurrentEntity.isExternal();
  }
  
  public int getChar(int paramInt)
    throws IOException
  {
    if (arrangeCapacity(paramInt + 1, false)) {
      return this.fCurrentEntity.ch[(this.fCurrentEntity.position + paramInt)];
    }
    return -1;
  }
  
  public int peekChar()
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (this.isExternal) {
      return i != 13 ? i : 10;
    }
    return i;
  }
  
  public int scanChar()
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
    if ((i == 10) || ((i == 13) && (this.isExternal)))
    {
      this.fCurrentEntity.lineNumber += 1;
      this.fCurrentEntity.columnNumber = 1;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = ((char)i);
        load(1, false, false);
      }
      if ((i == 13) && (this.isExternal))
      {
        if (this.fCurrentEntity.ch[(this.fCurrentEntity.position++)] != '\n') {
          this.fCurrentEntity.position -= 1;
        }
        i = 10;
      }
    }
    this.fCurrentEntity.columnNumber += 1;
    return i;
  }
  
  public String scanNmtoken()
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = this.fCurrentEntity.position;
    int j = 0;
    for (;;)
    {
      int k = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      boolean bool;
      if (k < 127) {
        j = VALID_NAMES[k];
      } else {
        bool = XMLChar.isName(k);
      }
      if (!bool) {
        break;
      }
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count)
      {
        m = this.fCurrentEntity.position - i;
        invokeListeners(m);
        if (m == this.fCurrentEntity.fBufferSize)
        {
          localObject = new char[this.fCurrentEntity.fBufferSize * 2];
          System.arraycopy(this.fCurrentEntity.ch, i, localObject, 0, m);
          this.fCurrentEntity.ch = ((char[])localObject);
          this.fCurrentEntity.fBufferSize *= 2;
        }
        else
        {
          System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, m);
        }
        i = 0;
        if (load(m, false, false)) {
          break;
        }
      }
    }
    int m = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += m;
    Object localObject = null;
    if (m > 0) {
      localObject = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, m);
    }
    return localObject;
  }
  
  public String scanName()
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = this.fCurrentEntity.position;
    if (XMLChar.isNameStart(this.fCurrentEntity.ch[i]))
    {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count)
      {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[i];
        i = 0;
        if (load(1, false, false))
        {
          this.fCurrentEntity.columnNumber += 1;
          String str1 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
          return str1;
        }
      }
      int j = 0;
      for (;;)
      {
        int m = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        boolean bool;
        if (m < 127) {
          j = VALID_NAMES[m];
        } else {
          bool = XMLChar.isName(m);
        }
        if (!bool) {
          break;
        }
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count)
        {
          int n = this.fCurrentEntity.position - i;
          invokeListeners(n);
          if (n == this.fCurrentEntity.fBufferSize)
          {
            char[] arrayOfChar = new char[this.fCurrentEntity.fBufferSize * 2];
            System.arraycopy(this.fCurrentEntity.ch, i, arrayOfChar, 0, n);
            this.fCurrentEntity.ch = arrayOfChar;
            this.fCurrentEntity.fBufferSize *= 2;
          }
          else
          {
            System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, n);
          }
          i = 0;
          if (load(n, false, false)) {
            break;
          }
        }
      }
    }
    int k = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += k;
    String str2;
    if (k > 0) {
      str2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, k);
    } else {
      str2 = null;
    }
    return str2;
  }
  
  public boolean scanQName(QName paramQName)
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = this.fCurrentEntity.position;
    if (XMLChar.isNameStart(this.fCurrentEntity.ch[i]))
    {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count)
      {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[i];
        i = 0;
        if (load(1, false, false))
        {
          this.fCurrentEntity.columnNumber += 1;
          String str1 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
          paramQName.setValues(null, str1, str1, null);
          return true;
        }
      }
      int j = -1;
      int k = 0;
      Object localObject;
      for (;;)
      {
        m = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        boolean bool;
        if (m < 127) {
          k = VALID_NAMES[m];
        } else {
          bool = XMLChar.isName(m);
        }
        if (!bool) {
          break;
        }
        if (m == 58)
        {
          if (j == -1)
          {
            j = this.fCurrentEntity.position;
            checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, j - i);
          }
        }
        else if (++this.fCurrentEntity.position == this.fCurrentEntity.count)
        {
          int n = this.fCurrentEntity.position - i;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, n - j - 1);
          invokeListeners(n);
          if (n == this.fCurrentEntity.fBufferSize)
          {
            localObject = new char[this.fCurrentEntity.fBufferSize * 2];
            System.arraycopy(this.fCurrentEntity.ch, i, localObject, 0, n);
            this.fCurrentEntity.ch = ((char[])localObject);
            this.fCurrentEntity.fBufferSize *= 2;
          }
          else
          {
            System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, n);
          }
          if (j != -1) {
            j -= i;
          }
          i = 0;
          if (load(n, false, false)) {
            break;
          }
        }
      }
      int m = this.fCurrentEntity.position - i;
      this.fCurrentEntity.columnNumber += m;
      if (m > 0)
      {
        String str2 = null;
        localObject = null;
        String str3 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, m);
        if (j != -1)
        {
          int i1 = j - i;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, i1);
          str2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, i1);
          int i2 = m - i1 - 1;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, j + 1, i2);
          localObject = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, j + 1, i2);
        }
        else
        {
          localObject = str3;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, m);
        }
        paramQName.setValues(str2, (String)localObject, str3, null);
        return true;
      }
    }
    return false;
  }
  
  protected void checkLimit(XMLSecurityManager.Limit paramLimit, Entity.ScannedEntity paramScannedEntity, int paramInt1, int paramInt2)
  {
    this.fLimitAnalyzer.addValue(paramLimit, null, paramInt2);
    if (this.fSecurityManager.isOverLimit(paramLimit, this.fLimitAnalyzer))
    {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", paramLimit.key(), new Object[] { new String(paramScannedEntity.ch, paramInt1, paramInt2), Integer.valueOf(this.fLimitAnalyzer.getTotalValue(paramLimit)), Integer.valueOf(this.fSecurityManager.getLimit(paramLimit)), this.fSecurityManager.getStateLiteral(paramLimit) }, (short)2);
    }
  }
  
  public int scanContent(XMLString paramXMLString)
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
    {
      load(0, true, true);
    }
    else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1)
    {
      invokeListeners(0);
      this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[(this.fCurrentEntity.count - 1)];
      load(1, false, false);
      this.fCurrentEntity.position = 0;
    }
    int i = this.fCurrentEntity.position;
    int j = this.fCurrentEntity.ch[i];
    int k = 0;
    if ((j == 10) || ((j == 13) && (this.isExternal)))
    {
      do
      {
        j = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
        if ((j == 13) && (this.isExternal))
        {
          k++;
          this.fCurrentEntity.lineNumber += 1;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count)
          {
            i = 0;
            this.fCurrentEntity.position = k;
            if (load(k, false, true)) {
              break;
            }
          }
          if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n')
          {
            this.fCurrentEntity.position += 1;
            i++;
          }
          else
          {
            k++;
          }
        }
        else if (j == 10)
        {
          k++;
          this.fCurrentEntity.lineNumber += 1;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count)
          {
            i = 0;
            this.fCurrentEntity.position = k;
            if (load(k, false, true)) {
              break;
            }
          }
        }
        else
        {
          this.fCurrentEntity.position -= 1;
          break;
        }
      } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
      for (m = i; m < this.fCurrentEntity.position; m++) {
        this.fCurrentEntity.ch[m] = '\n';
      }
      m = this.fCurrentEntity.position - i;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1)
      {
        paramXMLString.setValues(this.fCurrentEntity.ch, i, m);
        return -1;
      }
    }
    while (this.fCurrentEntity.position < this.fCurrentEntity.count)
    {
      j = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
      if (!XMLChar.isContent(j)) {
        this.fCurrentEntity.position -= 1;
      }
    }
    int m = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += m - k;
    if (this.fCurrentEntity.reference) {
      checkLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, this.fCurrentEntity, i, m);
    }
    paramXMLString.setValues(this.fCurrentEntity.ch, i, m);
    if (this.fCurrentEntity.position != this.fCurrentEntity.count)
    {
      j = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if ((j == 13) && (this.isExternal)) {
        j = 10;
      }
    }
    else
    {
      j = -1;
    }
    return j;
  }
  
  public int scanLiteral(int paramInt, XMLString paramXMLString)
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
    {
      load(0, true, true);
    }
    else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1)
    {
      invokeListeners(0);
      this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[(this.fCurrentEntity.count - 1)];
      load(1, false, false);
      this.fCurrentEntity.position = 0;
    }
    int i = this.fCurrentEntity.position;
    int j = this.fCurrentEntity.ch[i];
    int k = 0;
    if (this.whiteSpaceInfoNeeded) {
      this.whiteSpaceLen = 0;
    }
    if ((j == 10) || ((j == 13) && (this.isExternal)))
    {
      do
      {
        j = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
        if ((j == 13) && (this.isExternal))
        {
          k++;
          this.fCurrentEntity.lineNumber += 1;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count)
          {
            i = 0;
            this.fCurrentEntity.position = k;
            if (load(k, false, true)) {
              break;
            }
          }
          if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n')
          {
            this.fCurrentEntity.position += 1;
            i++;
          }
          else
          {
            k++;
          }
        }
        else if (j == 10)
        {
          k++;
          this.fCurrentEntity.lineNumber += 1;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count)
          {
            i = 0;
            this.fCurrentEntity.position = k;
            if (load(k, false, true)) {
              break;
            }
          }
        }
        else
        {
          this.fCurrentEntity.position -= 1;
          break;
        }
      } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
      m = 0;
      for (m = i; m < this.fCurrentEntity.position; m++)
      {
        this.fCurrentEntity.ch[m] = '\n';
        storeWhiteSpace(m);
      }
      int n = this.fCurrentEntity.position - i;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1)
      {
        paramXMLString.setValues(this.fCurrentEntity.ch, i, n);
        return -1;
      }
    }
    while (this.fCurrentEntity.position < this.fCurrentEntity.count)
    {
      j = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (((j == paramInt) && ((!this.fCurrentEntity.literal) || (this.isExternal))) || (j == 37) || (!XMLChar.isContent(j))) {
        break;
      }
      if ((this.whiteSpaceInfoNeeded) && (j == 9)) {
        storeWhiteSpace(this.fCurrentEntity.position);
      }
      this.fCurrentEntity.position += 1;
    }
    int m = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += m - k;
    paramXMLString.setValues(this.fCurrentEntity.ch, i, m);
    if (this.fCurrentEntity.position != this.fCurrentEntity.count)
    {
      j = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if ((j == paramInt) && (this.fCurrentEntity.literal)) {
        j = -1;
      }
    }
    else
    {
      j = -1;
    }
    return j;
  }
  
  private void storeWhiteSpace(int paramInt)
  {
    if (this.whiteSpaceLen >= this.whiteSpaceLookup.length)
    {
      int[] arrayOfInt = new int[this.whiteSpaceLookup.length + 100];
      System.arraycopy(this.whiteSpaceLookup, 0, arrayOfInt, 0, this.whiteSpaceLookup.length);
      this.whiteSpaceLookup = arrayOfInt;
    }
    this.whiteSpaceLookup[(this.whiteSpaceLen++)] = paramInt;
  }
  
  public boolean scanData(String paramString, XMLStringBuffer paramXMLStringBuffer)
    throws IOException
  {
    int i = 0;
    int j = paramString.length();
    int k = paramString.charAt(0);
    label974:
    do
    {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
        load(0, true, false);
      }
      boolean bool = false;
      while ((this.fCurrentEntity.position > this.fCurrentEntity.count - j) && (!bool))
      {
        System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
        bool = load(this.fCurrentEntity.count - this.fCurrentEntity.position, false, false);
        this.fCurrentEntity.position = 0;
        this.fCurrentEntity.startPosition = 0;
      }
      if (this.fCurrentEntity.position > this.fCurrentEntity.count - j)
      {
        m = this.fCurrentEntity.count - this.fCurrentEntity.position;
        paramXMLStringBuffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, m);
        this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
        this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
        this.fCurrentEntity.position = this.fCurrentEntity.count;
        this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
        load(0, true, false);
        return false;
      }
      int m = this.fCurrentEntity.position;
      int n = this.fCurrentEntity.ch[m];
      int i1 = 0;
      if ((n == 10) || ((n == 13) && (this.isExternal)))
      {
        do
        {
          n = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
          if ((n == 13) && (this.isExternal))
          {
            i1++;
            this.fCurrentEntity.lineNumber += 1;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count)
            {
              m = 0;
              this.fCurrentEntity.position = i1;
              if (load(i1, false, true)) {
                break;
              }
            }
            if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n')
            {
              this.fCurrentEntity.position += 1;
              m++;
            }
            else
            {
              i1++;
            }
          }
          else if (n == 10)
          {
            i1++;
            this.fCurrentEntity.lineNumber += 1;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count)
            {
              m = 0;
              this.fCurrentEntity.position = i1;
              this.fCurrentEntity.count = i1;
              if (load(i1, false, true)) {
                break;
              }
            }
          }
          else
          {
            this.fCurrentEntity.position -= 1;
            break;
          }
        } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
        for (i2 = m; i2 < this.fCurrentEntity.position; i2++) {
          this.fCurrentEntity.ch[i2] = '\n';
        }
        i2 = this.fCurrentEntity.position - m;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1)
        {
          paramXMLStringBuffer.append(this.fCurrentEntity.ch, m, i2);
          return true;
        }
      }
      while (this.fCurrentEntity.position < this.fCurrentEntity.count)
      {
        n = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
        if (n == k)
        {
          i2 = this.fCurrentEntity.position - 1;
          for (int i3 = 1; i3 < j; i3++)
          {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count)
            {
              this.fCurrentEntity.position -= i3;
              break label974;
            }
            n = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
            if (paramString.charAt(i3) != n)
            {
              this.fCurrentEntity.position -= i3;
              break;
            }
          }
          if (this.fCurrentEntity.position == i2 + j) {
            i = 1;
          }
        }
        else if ((n == 10) || ((this.isExternal) && (n == 13)))
        {
          this.fCurrentEntity.position -= 1;
        }
        else if (XMLChar.isInvalid(n))
        {
          this.fCurrentEntity.position -= 1;
          i2 = this.fCurrentEntity.position - m;
          this.fCurrentEntity.columnNumber += i2 - i1;
          paramXMLStringBuffer.append(this.fCurrentEntity.ch, m, i2);
          return true;
        }
      }
      int i2 = this.fCurrentEntity.position - m;
      this.fCurrentEntity.columnNumber += i2 - i1;
      if (i != 0) {
        i2 -= j;
      }
      paramXMLStringBuffer.append(this.fCurrentEntity.ch, m, i2);
    } while (i == 0);
    return i == 0;
  }
  
  public boolean skipChar(int paramInt)
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (i == paramInt)
    {
      this.fCurrentEntity.position += 1;
      if (paramInt == 10)
      {
        this.fCurrentEntity.lineNumber += 1;
        this.fCurrentEntity.columnNumber = 1;
      }
      else
      {
        this.fCurrentEntity.columnNumber += 1;
      }
      return true;
    }
    if ((paramInt == 10) && (i == 13) && (this.isExternal))
    {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = ((char)i);
        load(1, false, false);
      }
      this.fCurrentEntity.position += 1;
      if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
        this.fCurrentEntity.position += 1;
      }
      this.fCurrentEntity.lineNumber += 1;
      this.fCurrentEntity.columnNumber = 1;
      return true;
    }
    return false;
  }
  
  public boolean isSpace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\n') || (paramChar == '\t') || (paramChar == '\r');
  }
  
  public boolean skipSpaces()
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    }
    if (this.fCurrentEntity == null) {
      return false;
    }
    int i = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (XMLChar.isSpace(i))
    {
      do
      {
        boolean bool = false;
        if ((i == 10) || ((this.isExternal) && (i == 13)))
        {
          this.fCurrentEntity.lineNumber += 1;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1)
          {
            invokeListeners(0);
            this.fCurrentEntity.ch[0] = ((char)i);
            bool = load(1, true, false);
            if (!bool) {
              this.fCurrentEntity.position = 0;
            } else if (this.fCurrentEntity == null) {
              return true;
            }
          }
          if ((i == 13) && (this.isExternal)) {
            if (this.fCurrentEntity.ch[(++this.fCurrentEntity.position)] != '\n') {
              this.fCurrentEntity.position -= 1;
            }
          }
        }
        else
        {
          this.fCurrentEntity.columnNumber += 1;
        }
        if (!bool) {
          this.fCurrentEntity.position += 1;
        }
        if (this.fCurrentEntity.position == this.fCurrentEntity.count)
        {
          load(0, true, true);
          if (this.fCurrentEntity == null) {
            return true;
          }
        }
      } while (XMLChar.isSpace(i = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
      return true;
    }
    return false;
  }
  
  public boolean arrangeCapacity(int paramInt)
    throws IOException
  {
    return arrangeCapacity(paramInt, false);
  }
  
  public boolean arrangeCapacity(int paramInt, boolean paramBoolean)
    throws IOException
  {
    if (this.fCurrentEntity.count - this.fCurrentEntity.position >= paramInt) {
      return true;
    }
    boolean bool = false;
    while (this.fCurrentEntity.count - this.fCurrentEntity.position < paramInt)
    {
      if (this.fCurrentEntity.ch.length - this.fCurrentEntity.position < paramInt)
      {
        invokeListeners(0);
        System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
        this.fCurrentEntity.count -= this.fCurrentEntity.position;
        this.fCurrentEntity.position = 0;
      }
      if (this.fCurrentEntity.count - this.fCurrentEntity.position < paramInt)
      {
        int i = this.fCurrentEntity.position;
        invokeListeners(i);
        bool = load(this.fCurrentEntity.count, paramBoolean, false);
        this.fCurrentEntity.position = i;
        if (bool) {
          break;
        }
      }
    }
    return this.fCurrentEntity.count - this.fCurrentEntity.position >= paramInt;
  }
  
  public boolean skipString(String paramString)
    throws IOException
  {
    int i = paramString.length();
    if (arrangeCapacity(i, false))
    {
      int j = this.fCurrentEntity.position;
      int k = this.fCurrentEntity.position + i - 1;
      int m = i - 1;
      while (paramString.charAt(m--) == this.fCurrentEntity.ch[k]) {
        if (k-- == j)
        {
          this.fCurrentEntity.position += i;
          this.fCurrentEntity.columnNumber += i;
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean skipString(char[] paramArrayOfChar)
    throws IOException
  {
    int i = paramArrayOfChar.length;
    if (arrangeCapacity(i, false))
    {
      int j = this.fCurrentEntity.position;
      int k = this.fCurrentEntity.position + i;
      for (int m = 0; m < i; m++) {
        if (this.fCurrentEntity.ch[(j++)] != paramArrayOfChar[m]) {
          return false;
        }
      }
      this.fCurrentEntity.position += i;
      this.fCurrentEntity.columnNumber += i;
      return true;
    }
    return false;
  }
  
  final boolean load(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    if (paramBoolean2) {
      invokeListeners(paramInt);
    }
    this.fCurrentEntity.fTotalCountTillLastLoad += this.fCurrentEntity.fLastCount;
    int i = this.fCurrentEntity.ch.length - paramInt;
    if ((!this.fCurrentEntity.mayReadChunks) && (i > 64)) {
      i = 64;
    }
    int j = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, paramInt, i);
    boolean bool = false;
    if (j != -1)
    {
      if (j != 0)
      {
        this.fCurrentEntity.fLastCount = j;
        this.fCurrentEntity.count = (j + paramInt);
        this.fCurrentEntity.position = paramInt;
      }
    }
    else
    {
      this.fCurrentEntity.count = paramInt;
      this.fCurrentEntity.position = paramInt;
      bool = true;
      if (paramBoolean1)
      {
        this.fEntityManager.endEntity();
        if (this.fCurrentEntity == null) {
          throw END_OF_DOCUMENT_ENTITY;
        }
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
          load(0, true, false);
        }
      }
    }
    return bool;
  }
  
  protected Reader createReader(InputStream paramInputStream, String paramString, Boolean paramBoolean)
    throws IOException
  {
    if (paramString == null) {
      paramString = "UTF-8";
    }
    String str1 = paramString.toUpperCase(Locale.ENGLISH);
    if (str1.equals("UTF-8")) {
      return new UTF8Reader(paramInputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    if (str1.equals("US-ASCII")) {
      return new ASCIIReader(paramInputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    if (str1.equals("ISO-10646-UCS-4"))
    {
      if (paramBoolean != null)
      {
        bool1 = paramBoolean.booleanValue();
        if (bool1) {
          return new UCSReader(paramInputStream, (short)8);
        }
        return new UCSReader(paramInputStream, (short)4);
      }
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    }
    if (str1.equals("ISO-10646-UCS-2"))
    {
      if (paramBoolean != null)
      {
        bool1 = paramBoolean.booleanValue();
        if (bool1) {
          return new UCSReader(paramInputStream, (short)2);
        }
        return new UCSReader(paramInputStream, (short)1);
      }
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    }
    boolean bool1 = XMLChar.isValidIANAEncoding(paramString);
    boolean bool2 = XMLChar.isValidJavaEncoding(paramString);
    if ((!bool1) || ((this.fAllowJavaEncodings) && (!bool2)))
    {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
      paramString = "ISO-8859-1";
    }
    String str2 = EncodingMap.getIANA2JavaMapping(str1);
    if (str2 == null)
    {
      if (this.fAllowJavaEncodings)
      {
        str2 = paramString;
      }
      else
      {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
        str2 = "ISO8859_1";
      }
    }
    else if (str2.equals("ASCII")) {
      return new ASCIIReader(paramInputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    return new InputStreamReader(paramInputStream, str2);
  }
  
  protected Object[] getEncodingName(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt < 2) {
      return new Object[] { "UTF-8", null };
    }
    int i = paramArrayOfByte[0] & 0xFF;
    int j = paramArrayOfByte[1] & 0xFF;
    if ((i == 254) && (j == 255)) {
      return new Object[] { "UTF-16BE", new Boolean(true) };
    }
    if ((i == 255) && (j == 254)) {
      return new Object[] { "UTF-16LE", new Boolean(false) };
    }
    if (paramInt < 3) {
      return new Object[] { "UTF-8", null };
    }
    int k = paramArrayOfByte[2] & 0xFF;
    if ((i == 239) && (j == 187) && (k == 191)) {
      return new Object[] { "UTF-8", null };
    }
    if (paramInt < 4) {
      return new Object[] { "UTF-8", null };
    }
    int m = paramArrayOfByte[3] & 0xFF;
    if ((i == 0) && (j == 0) && (k == 0) && (m == 60)) {
      return new Object[] { "ISO-10646-UCS-4", new Boolean(true) };
    }
    if ((i == 60) && (j == 0) && (k == 0) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", new Boolean(false) };
    }
    if ((i == 0) && (j == 0) && (k == 60) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", null };
    }
    if ((i == 0) && (j == 60) && (k == 0) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", null };
    }
    if ((i == 0) && (j == 60) && (k == 0) && (m == 63)) {
      return new Object[] { "UTF-16BE", new Boolean(true) };
    }
    if ((i == 60) && (j == 0) && (k == 63) && (m == 0)) {
      return new Object[] { "UTF-16LE", new Boolean(false) };
    }
    if ((i == 76) && (j == 111) && (k == 167) && (m == 148)) {
      return new Object[] { "CP037", null };
    }
    return new Object[] { "UTF-8", null };
  }
  
  final void print() {}
  
  public void registerListener(XMLBufferListener paramXMLBufferListener)
  {
    if (!this.listeners.contains(paramXMLBufferListener)) {
      this.listeners.add(paramXMLBufferListener);
    }
  }
  
  public void invokeListeners(int paramInt)
  {
    for (int i = 0; i < this.listeners.size(); i++)
    {
      XMLBufferListener localXMLBufferListener = (XMLBufferListener)this.listeners.get(i);
      localXMLBufferListener.refresh(paramInt);
    }
  }
  
  public final boolean skipDeclSpaces()
    throws IOException
  {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, false);
    }
    int i = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (XMLChar.isSpace(i))
    {
      boolean bool1 = this.fCurrentEntity.isExternal();
      do
      {
        boolean bool2 = false;
        if ((i == 10) || ((bool1) && (i == 13)))
        {
          this.fCurrentEntity.lineNumber += 1;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1)
          {
            this.fCurrentEntity.ch[0] = ((char)i);
            bool2 = load(1, true, false);
            if (!bool2) {
              this.fCurrentEntity.position = 0;
            }
          }
          if ((i == 13) && (bool1)) {
            if (this.fCurrentEntity.ch[(++this.fCurrentEntity.position)] != '\n') {
              this.fCurrentEntity.position -= 1;
            }
          }
        }
        else
        {
          this.fCurrentEntity.columnNumber += 1;
        }
        if (!bool2) {
          this.fCurrentEntity.position += 1;
        }
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
          load(0, true, false);
        }
      } while (XMLChar.isSpace(i = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
      return true;
    }
    return false;
  }
  
  static
  {
    for (int i = 65; i <= 90; i++) {
      VALID_NAMES[i] = true;
    }
    for (i = 97; i <= 122; i++) {
      VALID_NAMES[i] = true;
    }
    for (i = 48; i <= 57; i++) {
      VALID_NAMES[i] = true;
    }
    VALID_NAMES[45] = true;
    VALID_NAMES[46] = true;
    VALID_NAMES[58] = true;
    VALID_NAMES[95] = true;
  }
}
