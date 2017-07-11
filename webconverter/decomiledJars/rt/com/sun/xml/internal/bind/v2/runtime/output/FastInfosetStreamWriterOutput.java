package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.org.jvnet.fastinfoset.VocabularyApplicationData;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class FastInfosetStreamWriterOutput
  extends XMLStreamWriterOutput
{
  private final StAXDocumentSerializer fiout;
  private final Encoded[] localNames;
  private final TablesPerJAXBContext tables;
  
  public FastInfosetStreamWriterOutput(StAXDocumentSerializer paramStAXDocumentSerializer, JAXBContextImpl paramJAXBContextImpl)
  {
    super(paramStAXDocumentSerializer);
    this.fiout = paramStAXDocumentSerializer;
    this.localNames = paramJAXBContextImpl.getUTF8NameTable();
    VocabularyApplicationData localVocabularyApplicationData = this.fiout.getVocabularyApplicationData();
    AppData localAppData = null;
    if ((localVocabularyApplicationData == null) || (!(localVocabularyApplicationData instanceof AppData)))
    {
      localAppData = new AppData();
      this.fiout.setVocabularyApplicationData(localAppData);
    }
    else
    {
      localAppData = (AppData)localVocabularyApplicationData;
    }
    TablesPerJAXBContext localTablesPerJAXBContext = (TablesPerJAXBContext)localAppData.contexts.get(paramJAXBContextImpl);
    if (localTablesPerJAXBContext != null)
    {
      this.tables = localTablesPerJAXBContext;
      this.tables.clearOrResetTables(paramStAXDocumentSerializer.getLocalNameIndex());
    }
    else
    {
      this.tables = new TablesPerJAXBContext(paramJAXBContextImpl, paramStAXDocumentSerializer.getLocalNameIndex());
      localAppData.contexts.put(paramJAXBContextImpl, this.tables);
    }
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws IOException, SAXException, XMLStreamException
  {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (paramBoolean) {
      this.fiout.initiateLowLevelWriting();
    }
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(Name paramName)
    throws IOException
  {
    this.fiout.writeLowLevelTerminationAndMark();
    if (this.nsContext.getCurrent().count() == 0)
    {
      int i = this.tables.elementIndexes[paramName.qNameIndex] - this.tables.indexOffset;
      int j = this.nsUriIndex2prefixIndex[paramName.nsUriIndex];
      if ((i >= 0) && (this.tables.elementIndexPrefixes[paramName.qNameIndex] == j))
      {
        this.fiout.writeLowLevelStartElementIndexed(0, i);
      }
      else
      {
        this.tables.elementIndexes[paramName.qNameIndex] = (this.fiout.getNextElementIndex() + this.tables.indexOffset);
        this.tables.elementIndexPrefixes[paramName.qNameIndex] = j;
        writeLiteral(60, paramName, this.nsContext.getPrefix(j), this.nsContext.getNamespaceURI(j));
      }
    }
    else
    {
      beginStartTagWithNamespaces(paramName);
    }
  }
  
  public void beginStartTagWithNamespaces(Name paramName)
    throws IOException
  {
    NamespaceContextImpl.Element localElement = this.nsContext.getCurrent();
    this.fiout.writeLowLevelStartNamespaces();
    for (int i = localElement.count() - 1; i >= 0; i--)
    {
      String str = localElement.getNsUri(i);
      if ((str.length() != 0) || (localElement.getBase() != 1)) {
        this.fiout.writeLowLevelNamespace(localElement.getPrefix(i), str);
      }
    }
    this.fiout.writeLowLevelEndNamespaces();
    i = this.tables.elementIndexes[paramName.qNameIndex] - this.tables.indexOffset;
    int j = this.nsUriIndex2prefixIndex[paramName.nsUriIndex];
    if ((i >= 0) && (this.tables.elementIndexPrefixes[paramName.qNameIndex] == j))
    {
      this.fiout.writeLowLevelStartElementIndexed(0, i);
    }
    else
    {
      this.tables.elementIndexes[paramName.qNameIndex] = (this.fiout.getNextElementIndex() + this.tables.indexOffset);
      this.tables.elementIndexPrefixes[paramName.qNameIndex] = j;
      writeLiteral(60, paramName, this.nsContext.getPrefix(j), this.nsContext.getNamespaceURI(j));
    }
  }
  
  public void attribute(Name paramName, String paramString)
    throws IOException
  {
    this.fiout.writeLowLevelStartAttributes();
    int i = this.tables.attributeIndexes[paramName.qNameIndex] - this.tables.indexOffset;
    if (i >= 0)
    {
      this.fiout.writeLowLevelAttributeIndexed(i);
    }
    else
    {
      this.tables.attributeIndexes[paramName.qNameIndex] = (this.fiout.getNextAttributeIndex() + this.tables.indexOffset);
      int j = paramName.nsUriIndex;
      if (j == -1)
      {
        writeLiteral(120, paramName, "", "");
      }
      else
      {
        int k = this.nsUriIndex2prefixIndex[j];
        writeLiteral(120, paramName, this.nsContext.getPrefix(k), this.nsContext.getNamespaceURI(k));
      }
    }
    this.fiout.writeLowLevelAttributeValue(paramString);
  }
  
  private void writeLiteral(int paramInt, Name paramName, String paramString1, String paramString2)
    throws IOException
  {
    int i = this.tables.localNameIndexes[paramName.localNameIndex] - this.tables.indexOffset;
    if (i < 0)
    {
      this.tables.localNameIndexes[paramName.localNameIndex] = (this.fiout.getNextLocalNameIndex() + this.tables.indexOffset);
      this.fiout.writeLowLevelStartNameLiteral(paramInt, paramString1, this.localNames[paramName.localNameIndex].buf, paramString2);
    }
    else
    {
      this.fiout.writeLowLevelStartNameLiteral(paramInt, paramString1, i, paramString2);
    }
  }
  
  public void endStartTag()
    throws IOException
  {
    this.fiout.writeLowLevelEndStartElement();
  }
  
  public void endTag(Name paramName)
    throws IOException
  {
    this.fiout.writeLowLevelEndElement();
  }
  
  public void endTag(int paramInt, String paramString)
    throws IOException
  {
    this.fiout.writeLowLevelEndElement();
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      this.fiout.writeLowLevelText(" ");
    }
    if (!(paramPcdata instanceof Base64Data))
    {
      int i = paramPcdata.length();
      if (i < this.buf.length)
      {
        paramPcdata.writeTo(this.buf, 0);
        this.fiout.writeLowLevelText(this.buf, i);
      }
      else
      {
        this.fiout.writeLowLevelText(paramPcdata.toString());
      }
    }
    else
    {
      Base64Data localBase64Data = (Base64Data)paramPcdata;
      this.fiout.writeLowLevelOctets(localBase64Data.get(), localBase64Data.getDataLen());
    }
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      this.fiout.writeLowLevelText(" ");
    }
    this.fiout.writeLowLevelText(paramString);
  }
  
  public void beginStartTag(int paramInt, String paramString)
    throws IOException
  {
    this.fiout.writeLowLevelTerminationAndMark();
    int i = 0;
    if (this.nsContext.getCurrent().count() > 0)
    {
      NamespaceContextImpl.Element localElement = this.nsContext.getCurrent();
      this.fiout.writeLowLevelStartNamespaces();
      for (int j = localElement.count() - 1; j >= 0; j--)
      {
        String str = localElement.getNsUri(j);
        if ((str.length() != 0) || (localElement.getBase() != 1)) {
          this.fiout.writeLowLevelNamespace(localElement.getPrefix(j), str);
        }
      }
      this.fiout.writeLowLevelEndNamespaces();
      i = 0;
    }
    boolean bool = this.fiout.writeLowLevelStartElement(i, this.nsContext.getPrefix(paramInt), paramString, this.nsContext.getNamespaceURI(paramInt));
    if (!bool) {
      this.tables.incrementMaxIndexValue();
    }
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException
  {
    this.fiout.writeLowLevelStartAttributes();
    boolean bool;
    if (paramInt == -1) {
      bool = this.fiout.writeLowLevelAttribute("", "", paramString1);
    } else {
      bool = this.fiout.writeLowLevelAttribute(this.nsContext.getPrefix(paramInt), this.nsContext.getNamespaceURI(paramInt), paramString1);
    }
    if (!bool) {
      this.tables.incrementMaxIndexValue();
    }
    this.fiout.writeLowLevelAttributeValue(paramString2);
  }
  
  static final class AppData
    implements VocabularyApplicationData
  {
    final Map<JAXBContext, FastInfosetStreamWriterOutput.TablesPerJAXBContext> contexts = new WeakHashMap();
    final Collection<FastInfosetStreamWriterOutput.TablesPerJAXBContext> collectionOfContexts = this.contexts.values();
    
    AppData() {}
    
    public void clear()
    {
      Iterator localIterator = this.collectionOfContexts.iterator();
      while (localIterator.hasNext())
      {
        FastInfosetStreamWriterOutput.TablesPerJAXBContext localTablesPerJAXBContext = (FastInfosetStreamWriterOutput.TablesPerJAXBContext)localIterator.next();
        localTablesPerJAXBContext.requireClearTables();
      }
    }
  }
  
  static final class TablesPerJAXBContext
  {
    final int[] elementIndexes;
    final int[] elementIndexPrefixes;
    final int[] attributeIndexes;
    final int[] localNameIndexes;
    int indexOffset;
    int maxIndex;
    boolean requiresClear;
    
    TablesPerJAXBContext(JAXBContextImpl paramJAXBContextImpl, int paramInt)
    {
      this.elementIndexes = new int[paramJAXBContextImpl.getNumberOfElementNames()];
      this.elementIndexPrefixes = new int[paramJAXBContextImpl.getNumberOfElementNames()];
      this.attributeIndexes = new int[paramJAXBContextImpl.getNumberOfAttributeNames()];
      this.localNameIndexes = new int[paramJAXBContextImpl.getNumberOfLocalNames()];
      this.indexOffset = 1;
      this.maxIndex = (paramInt + this.elementIndexes.length + this.attributeIndexes.length);
    }
    
    public void requireClearTables()
    {
      this.requiresClear = true;
    }
    
    public void clearOrResetTables(int paramInt)
    {
      if (this.requiresClear)
      {
        this.requiresClear = false;
        this.indexOffset += this.maxIndex;
        this.maxIndex = (paramInt + this.elementIndexes.length + this.attributeIndexes.length);
        if (this.indexOffset + this.maxIndex < 0) {
          clearAll();
        }
      }
      else
      {
        this.maxIndex = (paramInt + this.elementIndexes.length + this.attributeIndexes.length);
        if (this.indexOffset + this.maxIndex < 0) {
          resetAll();
        }
      }
    }
    
    private void clearAll()
    {
      clear(this.elementIndexes);
      clear(this.attributeIndexes);
      clear(this.localNameIndexes);
      this.indexOffset = 1;
    }
    
    private void clear(int[] paramArrayOfInt)
    {
      for (int i = 0; i < paramArrayOfInt.length; i++) {
        paramArrayOfInt[i] = 0;
      }
    }
    
    public void incrementMaxIndexValue()
    {
      this.maxIndex += 1;
      if (this.indexOffset + this.maxIndex < 0) {
        resetAll();
      }
    }
    
    private void resetAll()
    {
      clear(this.elementIndexes);
      clear(this.attributeIndexes);
      clear(this.localNameIndexes);
      this.indexOffset = 1;
    }
    
    private void reset(int[] paramArrayOfInt)
    {
      for (int i = 0; i < paramArrayOfInt.length; i++) {
        if (paramArrayOfInt[i] > this.indexOffset) {
          paramArrayOfInt[i] = (paramArrayOfInt[i] - this.indexOffset + 1);
        } else {
          paramArrayOfInt[i] = 0;
        }
      }
    }
  }
}
