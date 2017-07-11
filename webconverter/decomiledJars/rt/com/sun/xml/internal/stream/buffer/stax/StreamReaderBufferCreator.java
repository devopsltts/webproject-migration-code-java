package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamReaderBufferCreator
  extends StreamBufferCreator
{
  private int _eventType;
  private boolean _storeInScopeNamespacesOnElementFragment;
  private Map<String, Integer> _inScopePrefixes;
  
  public StreamReaderBufferCreator() {}
  
  public StreamReaderBufferCreator(MutableXMLStreamBuffer paramMutableXMLStreamBuffer)
  {
    setBuffer(paramMutableXMLStreamBuffer);
  }
  
  public MutableXMLStreamBuffer create(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    if (this._buffer == null) {
      createBuffer();
    }
    store(paramXMLStreamReader);
    return getXMLStreamBuffer();
  }
  
  public MutableXMLStreamBuffer createElementFragment(XMLStreamReader paramXMLStreamReader, boolean paramBoolean)
    throws XMLStreamException
  {
    if (this._buffer == null) {
      createBuffer();
    }
    if (!paramXMLStreamReader.hasNext()) {
      return this._buffer;
    }
    this._storeInScopeNamespacesOnElementFragment = paramBoolean;
    this._eventType = paramXMLStreamReader.getEventType();
    if (this._eventType != 1) {
      do
      {
        this._eventType = paramXMLStreamReader.next();
      } while ((this._eventType != 1) && (this._eventType != 8));
    }
    if (paramBoolean) {
      this._inScopePrefixes = new HashMap();
    }
    storeElementAndChildren(paramXMLStreamReader);
    return getXMLStreamBuffer();
  }
  
  private void store(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    if (!paramXMLStreamReader.hasNext()) {
      return;
    }
    this._eventType = paramXMLStreamReader.getEventType();
    switch (this._eventType)
    {
    case 7: 
      storeDocumentAndChildren(paramXMLStreamReader);
      break;
    case 1: 
      storeElementAndChildren(paramXMLStreamReader);
      break;
    default: 
      throw new XMLStreamException("XMLStreamReader not positioned at a document or element");
    }
    increaseTreeCount();
  }
  
  private void storeDocumentAndChildren(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    storeStructure(16);
    this._eventType = paramXMLStreamReader.next();
    while (this._eventType != 8) {
      switch (this._eventType)
      {
      case 1: 
        storeElementAndChildren(paramXMLStreamReader);
        break;
      case 5: 
        storeComment(paramXMLStreamReader);
        break;
      case 3: 
        storeProcessingInstruction(paramXMLStreamReader);
      case 2: 
      case 4: 
      default: 
        this._eventType = paramXMLStreamReader.next();
      }
    }
    storeStructure(144);
  }
  
  private void storeElementAndChildren(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    if ((paramXMLStreamReader instanceof XMLStreamReaderEx)) {
      storeElementAndChildrenEx((XMLStreamReaderEx)paramXMLStreamReader);
    } else {
      storeElementAndChildrenNoEx(paramXMLStreamReader);
    }
  }
  
  private void storeElementAndChildrenEx(XMLStreamReaderEx paramXMLStreamReaderEx)
    throws XMLStreamException
  {
    int i = 1;
    if (this._storeInScopeNamespacesOnElementFragment) {
      storeElementWithInScopeNamespaces(paramXMLStreamReaderEx);
    } else {
      storeElement(paramXMLStreamReaderEx);
    }
    while (i > 0)
    {
      this._eventType = paramXMLStreamReaderEx.next();
      switch (this._eventType)
      {
      case 1: 
        i++;
        storeElement(paramXMLStreamReaderEx);
        break;
      case 2: 
        i--;
        storeStructure(144);
        break;
      case 13: 
        storeNamespaceAttributes(paramXMLStreamReaderEx);
        break;
      case 10: 
        storeAttributes(paramXMLStreamReaderEx);
        break;
      case 4: 
      case 6: 
      case 12: 
        CharSequence localCharSequence = paramXMLStreamReaderEx.getPCDATA();
        if ((localCharSequence instanceof Base64Data))
        {
          storeStructure(92);
          storeContentObject(localCharSequence);
        }
        else
        {
          storeContentCharacters(80, paramXMLStreamReaderEx.getTextCharacters(), paramXMLStreamReaderEx.getTextStart(), paramXMLStreamReaderEx.getTextLength());
        }
        break;
      case 5: 
        storeComment(paramXMLStreamReaderEx);
        break;
      case 3: 
        storeProcessingInstruction(paramXMLStreamReaderEx);
      }
    }
    this._eventType = paramXMLStreamReaderEx.next();
  }
  
  private void storeElementAndChildrenNoEx(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    int i = 1;
    if (this._storeInScopeNamespacesOnElementFragment) {
      storeElementWithInScopeNamespaces(paramXMLStreamReader);
    } else {
      storeElement(paramXMLStreamReader);
    }
    while (i > 0)
    {
      this._eventType = paramXMLStreamReader.next();
      switch (this._eventType)
      {
      case 1: 
        i++;
        storeElement(paramXMLStreamReader);
        break;
      case 2: 
        i--;
        storeStructure(144);
        break;
      case 13: 
        storeNamespaceAttributes(paramXMLStreamReader);
        break;
      case 10: 
        storeAttributes(paramXMLStreamReader);
        break;
      case 4: 
      case 6: 
      case 12: 
        storeContentCharacters(80, paramXMLStreamReader.getTextCharacters(), paramXMLStreamReader.getTextStart(), paramXMLStreamReader.getTextLength());
        break;
      case 5: 
        storeComment(paramXMLStreamReader);
        break;
      case 3: 
        storeProcessingInstruction(paramXMLStreamReader);
      }
    }
    this._eventType = paramXMLStreamReader.next();
  }
  
  private void storeElementWithInScopeNamespaces(XMLStreamReader paramXMLStreamReader)
  {
    storeQualifiedName(32, paramXMLStreamReader.getPrefix(), paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName());
    if (paramXMLStreamReader.getNamespaceCount() > 0) {
      storeNamespaceAttributes(paramXMLStreamReader);
    }
    if (paramXMLStreamReader.getAttributeCount() > 0) {
      storeAttributes(paramXMLStreamReader);
    }
  }
  
  private void storeElement(XMLStreamReader paramXMLStreamReader)
  {
    storeQualifiedName(32, paramXMLStreamReader.getPrefix(), paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName());
    if (paramXMLStreamReader.getNamespaceCount() > 0) {
      storeNamespaceAttributes(paramXMLStreamReader);
    }
    if (paramXMLStreamReader.getAttributeCount() > 0) {
      storeAttributes(paramXMLStreamReader);
    }
  }
  
  public void storeElement(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    storeQualifiedName(32, paramString3, paramString1, paramString2);
    storeNamespaceAttributes(paramArrayOfString);
  }
  
  public void storeEndElement()
  {
    storeStructure(144);
  }
  
  private void storeNamespaceAttributes(XMLStreamReader paramXMLStreamReader)
  {
    int i = paramXMLStreamReader.getNamespaceCount();
    for (int j = 0; j < i; j++) {
      storeNamespaceAttribute(paramXMLStreamReader.getNamespacePrefix(j), paramXMLStreamReader.getNamespaceURI(j));
    }
  }
  
  private void storeNamespaceAttributes(String[] paramArrayOfString)
  {
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      storeNamespaceAttribute(paramArrayOfString[i], paramArrayOfString[(i + 1)]);
      i += 2;
    }
  }
  
  private void storeAttributes(XMLStreamReader paramXMLStreamReader)
  {
    int i = paramXMLStreamReader.getAttributeCount();
    for (int j = 0; j < i; j++) {
      storeAttribute(paramXMLStreamReader.getAttributePrefix(j), paramXMLStreamReader.getAttributeNamespace(j), paramXMLStreamReader.getAttributeLocalName(j), paramXMLStreamReader.getAttributeType(j), paramXMLStreamReader.getAttributeValue(j));
    }
  }
  
  private void storeComment(XMLStreamReader paramXMLStreamReader)
  {
    storeContentCharacters(96, paramXMLStreamReader.getTextCharacters(), paramXMLStreamReader.getTextStart(), paramXMLStreamReader.getTextLength());
  }
  
  private void storeProcessingInstruction(XMLStreamReader paramXMLStreamReader)
  {
    storeProcessingInstruction(paramXMLStreamReader.getPITarget(), paramXMLStreamReader.getPIData());
  }
}
