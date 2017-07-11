package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StreamWriterBufferProcessor
  extends AbstractProcessor
{
  public StreamWriterBufferProcessor() {}
  
  /**
   * @deprecated
   */
  public StreamWriterBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer)
  {
    setXMLStreamBuffer(paramXMLStreamBuffer, paramXMLStreamBuffer.isFragment());
  }
  
  public StreamWriterBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean)
  {
    setXMLStreamBuffer(paramXMLStreamBuffer, paramBoolean);
  }
  
  public final void process(XMLStreamBuffer paramXMLStreamBuffer, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    setXMLStreamBuffer(paramXMLStreamBuffer, paramXMLStreamBuffer.isFragment());
    process(paramXMLStreamWriter);
  }
  
  public void process(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (this._fragmentMode) {
      writeFragment(paramXMLStreamWriter);
    } else {
      write(paramXMLStreamWriter);
    }
  }
  
  /**
   * @deprecated
   */
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer)
  {
    setBuffer(paramXMLStreamBuffer);
  }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean)
  {
    setBuffer(paramXMLStreamBuffer, paramBoolean);
  }
  
  public void write(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (!this._fragmentMode)
    {
      if (this._treeCount > 1) {
        throw new IllegalStateException("forest cannot be written as a full infoset");
      }
      paramXMLStreamWriter.writeStartDocument();
    }
    for (;;)
    {
      int i = getEIIState(peekStructure());
      paramXMLStreamWriter.flush();
      int j;
      int k;
      String str;
      switch (i)
      {
      case 1: 
        readStructure();
        break;
      case 3: 
      case 4: 
      case 5: 
      case 6: 
        writeFragment(paramXMLStreamWriter);
        break;
      case 12: 
        readStructure();
        j = readStructure();
        k = readContentCharactersBuffer(j);
        str = new String(this._contentCharactersBuffer, k, j);
        paramXMLStreamWriter.writeComment(str);
        break;
      case 13: 
        readStructure();
        j = readStructure16();
        k = readContentCharactersBuffer(j);
        str = new String(this._contentCharactersBuffer, k, j);
        paramXMLStreamWriter.writeComment(str);
        break;
      case 14: 
        readStructure();
        char[] arrayOfChar = readContentCharactersCopy();
        paramXMLStreamWriter.writeComment(new String(arrayOfChar));
        break;
      case 16: 
        readStructure();
        paramXMLStreamWriter.writeProcessingInstruction(readStructureString(), readStructureString());
        break;
      case 17: 
        readStructure();
        paramXMLStreamWriter.writeEndDocument();
        return;
      case 2: 
      case 7: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      case 15: 
      default: 
        throw new XMLStreamException("Invalid State " + i);
      }
    }
  }
  
  public void writeFragment(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if ((paramXMLStreamWriter instanceof XMLStreamWriterEx)) {
      writeFragmentEx((XMLStreamWriterEx)paramXMLStreamWriter);
    } else {
      writeFragmentNoEx(paramXMLStreamWriter);
    }
  }
  
  public void writeFragmentEx(XMLStreamWriterEx paramXMLStreamWriterEx)
    throws XMLStreamException
  {
    int i = 0;
    int j = getEIIState(peekStructure());
    if (j == 1) {
      readStructure();
    }
    do
    {
      j = readEiiState();
      String str1;
      String str2;
      String str3;
      int k;
      int n;
      Object localObject;
      int m;
      switch (j)
      {
      case 1: 
        throw new AssertionError();
      case 3: 
        i++;
        str1 = readStructureString();
        str2 = readStructureString();
        str3 = getPrefixFromQName(readStructureString());
        paramXMLStreamWriterEx.writeStartElement(str3, str2, str1);
        writeAttributes(paramXMLStreamWriterEx, isInscope(i));
        break;
      case 4: 
        i++;
        str1 = readStructureString();
        str2 = readStructureString();
        str3 = readStructureString();
        paramXMLStreamWriterEx.writeStartElement(str1, str3, str2);
        writeAttributes(paramXMLStreamWriterEx, isInscope(i));
        break;
      case 5: 
        i++;
        str1 = readStructureString();
        str2 = readStructureString();
        paramXMLStreamWriterEx.writeStartElement("", str2, str1);
        writeAttributes(paramXMLStreamWriterEx, isInscope(i));
        break;
      case 6: 
        i++;
        str1 = readStructureString();
        paramXMLStreamWriterEx.writeStartElement(str1);
        writeAttributes(paramXMLStreamWriterEx, isInscope(i));
        break;
      case 7: 
        k = readStructure();
        n = readContentCharactersBuffer(k);
        paramXMLStreamWriterEx.writeCharacters(this._contentCharactersBuffer, n, k);
        break;
      case 8: 
        k = readStructure16();
        n = readContentCharactersBuffer(k);
        paramXMLStreamWriterEx.writeCharacters(this._contentCharactersBuffer, n, k);
        break;
      case 9: 
        localObject = readContentCharactersCopy();
        paramXMLStreamWriterEx.writeCharacters((char[])localObject, 0, localObject.length);
        break;
      case 10: 
        localObject = readContentString();
        paramXMLStreamWriterEx.writeCharacters((String)localObject);
        break;
      case 11: 
        localObject = (CharSequence)readContentObject();
        paramXMLStreamWriterEx.writePCDATA((CharSequence)localObject);
        break;
      case 12: 
        m = readStructure();
        n = readContentCharactersBuffer(m);
        str3 = new String(this._contentCharactersBuffer, n, m);
        paramXMLStreamWriterEx.writeComment(str3);
        break;
      case 13: 
        m = readStructure16();
        n = readContentCharactersBuffer(m);
        str3 = new String(this._contentCharactersBuffer, n, m);
        paramXMLStreamWriterEx.writeComment(str3);
        break;
      case 14: 
        char[] arrayOfChar = readContentCharactersCopy();
        paramXMLStreamWriterEx.writeComment(new String(arrayOfChar));
        break;
      case 16: 
        paramXMLStreamWriterEx.writeProcessingInstruction(readStructureString(), readStructureString());
        break;
      case 17: 
        paramXMLStreamWriterEx.writeEndElement();
        i--;
        if (i == 0) {
          this._treeCount -= 1;
        }
        break;
      case 2: 
      case 15: 
      default: 
        throw new XMLStreamException("Invalid State " + j);
      }
    } while ((i > 0) || (this._treeCount > 0));
  }
  
  public void writeFragmentNoEx(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    int i = 0;
    int j = getEIIState(peekStructure());
    if (j == 1) {
      readStructure();
    }
    do
    {
      j = readEiiState();
      String str1;
      String str2;
      String str3;
      int k;
      int n;
      Object localObject;
      int m;
      int i1;
      switch (j)
      {
      case 1: 
        throw new AssertionError();
      case 3: 
        i++;
        str1 = readStructureString();
        str2 = readStructureString();
        str3 = getPrefixFromQName(readStructureString());
        paramXMLStreamWriter.writeStartElement(str3, str2, str1);
        writeAttributes(paramXMLStreamWriter, isInscope(i));
        break;
      case 4: 
        i++;
        str1 = readStructureString();
        str2 = readStructureString();
        str3 = readStructureString();
        paramXMLStreamWriter.writeStartElement(str1, str3, str2);
        writeAttributes(paramXMLStreamWriter, isInscope(i));
        break;
      case 5: 
        i++;
        str1 = readStructureString();
        str2 = readStructureString();
        paramXMLStreamWriter.writeStartElement("", str2, str1);
        writeAttributes(paramXMLStreamWriter, isInscope(i));
        break;
      case 6: 
        i++;
        str1 = readStructureString();
        paramXMLStreamWriter.writeStartElement(str1);
        writeAttributes(paramXMLStreamWriter, isInscope(i));
        break;
      case 7: 
        k = readStructure();
        n = readContentCharactersBuffer(k);
        paramXMLStreamWriter.writeCharacters(this._contentCharactersBuffer, n, k);
        break;
      case 8: 
        k = readStructure16();
        n = readContentCharactersBuffer(k);
        paramXMLStreamWriter.writeCharacters(this._contentCharactersBuffer, n, k);
        break;
      case 9: 
        localObject = readContentCharactersCopy();
        paramXMLStreamWriter.writeCharacters((char[])localObject, 0, localObject.length);
        break;
      case 10: 
        localObject = readContentString();
        paramXMLStreamWriter.writeCharacters((String)localObject);
        break;
      case 11: 
        localObject = (CharSequence)readContentObject();
        if ((localObject instanceof Base64Data)) {
          try
          {
            Base64Data localBase64Data = (Base64Data)localObject;
            localBase64Data.writeTo(paramXMLStreamWriter);
          }
          catch (IOException localIOException)
          {
            throw new XMLStreamException(localIOException);
          }
        } else {
          paramXMLStreamWriter.writeCharacters(((CharSequence)localObject).toString());
        }
        break;
      case 12: 
        m = readStructure();
        i1 = readContentCharactersBuffer(m);
        str3 = new String(this._contentCharactersBuffer, i1, m);
        paramXMLStreamWriter.writeComment(str3);
        break;
      case 13: 
        m = readStructure16();
        i1 = readContentCharactersBuffer(m);
        str3 = new String(this._contentCharactersBuffer, i1, m);
        paramXMLStreamWriter.writeComment(str3);
        break;
      case 14: 
        char[] arrayOfChar = readContentCharactersCopy();
        paramXMLStreamWriter.writeComment(new String(arrayOfChar));
        break;
      case 16: 
        paramXMLStreamWriter.writeProcessingInstruction(readStructureString(), readStructureString());
        break;
      case 17: 
        paramXMLStreamWriter.writeEndElement();
        i--;
        if (i == 0) {
          this._treeCount -= 1;
        }
        break;
      case 2: 
      case 15: 
      default: 
        throw new XMLStreamException("Invalid State " + j);
      }
    } while ((i > 0) || (this._treeCount > 0));
  }
  
  private boolean isInscope(int paramInt)
  {
    return (this._buffer.getInscopeNamespaces().size() > 0) && (paramInt == 1);
  }
  
  private void writeAttributes(XMLStreamWriter paramXMLStreamWriter, boolean paramBoolean)
    throws XMLStreamException
  {
    Set localSet = paramBoolean ? new HashSet() : Collections.emptySet();
    int i = peekStructure();
    if ((i & 0xF0) == 64) {
      i = writeNamespaceAttributes(i, paramXMLStreamWriter, paramBoolean, localSet);
    }
    if (paramBoolean) {
      writeInscopeNamespaces(paramXMLStreamWriter, localSet);
    }
    if ((i & 0xF0) == 48) {
      writeAttributes(i, paramXMLStreamWriter);
    }
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  private void writeInscopeNamespaces(XMLStreamWriter paramXMLStreamWriter, Set<String> paramSet)
    throws XMLStreamException
  {
    Iterator localIterator = this._buffer.getInscopeNamespaces().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = fixNull((String)localEntry.getKey());
      if (!paramSet.contains(str)) {
        paramXMLStreamWriter.writeNamespace(str, (String)localEntry.getValue());
      }
    }
  }
  
  private int writeNamespaceAttributes(int paramInt, XMLStreamWriter paramXMLStreamWriter, boolean paramBoolean, Set<String> paramSet)
    throws XMLStreamException
  {
    do
    {
      String str;
      switch (getNIIState(paramInt))
      {
      case 1: 
        paramXMLStreamWriter.writeDefaultNamespace("");
        if (paramBoolean) {
          paramSet.add("");
        }
        break;
      case 2: 
        str = readStructureString();
        paramXMLStreamWriter.writeNamespace(str, "");
        if (paramBoolean) {
          paramSet.add(str);
        }
        break;
      case 3: 
        str = readStructureString();
        paramXMLStreamWriter.writeNamespace(str, readStructureString());
        if (paramBoolean) {
          paramSet.add(str);
        }
        break;
      case 4: 
        paramXMLStreamWriter.writeDefaultNamespace(readStructureString());
        if (paramBoolean) {
          paramSet.add("");
        }
        break;
      }
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 64);
    return paramInt;
  }
  
  private void writeAttributes(int paramInt, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    do
    {
      switch (getAIIState(paramInt))
      {
      case 1: 
        String str1 = readStructureString();
        String str2 = readStructureString();
        String str3 = getPrefixFromQName(readStructureString());
        paramXMLStreamWriter.writeAttribute(str3, str1, str2, readContentString());
        break;
      case 2: 
        paramXMLStreamWriter.writeAttribute(readStructureString(), readStructureString(), readStructureString(), readContentString());
        break;
      case 3: 
        paramXMLStreamWriter.writeAttribute(readStructureString(), readStructureString(), readContentString());
        break;
      case 4: 
        paramXMLStreamWriter.writeAttribute(readStructureString(), readContentString());
      }
      readStructureString();
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 48);
  }
}
