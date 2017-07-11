package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

public class XMLEventAllocatorImpl
  implements XMLEventAllocator
{
  public XMLEventAllocatorImpl() {}
  
  public XMLEvent allocate(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    if (paramXMLStreamReader == null) {
      throw new XMLStreamException("Reader cannot be null");
    }
    return getXMLEvent(paramXMLStreamReader);
  }
  
  public void allocate(XMLStreamReader paramXMLStreamReader, XMLEventConsumer paramXMLEventConsumer)
    throws XMLStreamException
  {
    XMLEvent localXMLEvent = getXMLEvent(paramXMLStreamReader);
    if (localXMLEvent != null) {
      paramXMLEventConsumer.add(localXMLEvent);
    }
  }
  
  public XMLEventAllocator newInstance()
  {
    return new XMLEventAllocatorImpl();
  }
  
  XMLEvent getXMLEvent(XMLStreamReader paramXMLStreamReader)
  {
    Object localObject1 = null;
    int i = paramXMLStreamReader.getEventType();
    Object localObject2;
    switch (i)
    {
    case 1: 
      localObject2 = new StartElementEvent(getQName(paramXMLStreamReader));
      fillAttributes((StartElementEvent)localObject2, paramXMLStreamReader);
      if (((Boolean)paramXMLStreamReader.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue())
      {
        fillNamespaceAttributes((StartElementEvent)localObject2, paramXMLStreamReader);
        setNamespaceContext((StartElementEvent)localObject2, paramXMLStreamReader);
      }
      ((StartElementEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    case 2: 
      localObject2 = new EndElementEvent(getQName(paramXMLStreamReader));
      ((EndElementEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      if (((Boolean)paramXMLStreamReader.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue()) {
        fillNamespaceAttributes((EndElementEvent)localObject2, paramXMLStreamReader);
      }
      localObject1 = localObject2;
      break;
    case 3: 
      localObject2 = new ProcessingInstructionEvent(paramXMLStreamReader.getPITarget(), paramXMLStreamReader.getPIData());
      ((ProcessingInstructionEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    case 4: 
      localObject2 = new CharacterEvent(paramXMLStreamReader.getText());
      ((CharacterEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    case 5: 
      localObject2 = new CommentEvent(paramXMLStreamReader.getText());
      ((CommentEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    case 7: 
      localObject2 = new StartDocumentEvent();
      ((StartDocumentEvent)localObject2).setVersion(paramXMLStreamReader.getVersion());
      ((StartDocumentEvent)localObject2).setEncoding(paramXMLStreamReader.getEncoding());
      if (paramXMLStreamReader.getCharacterEncodingScheme() != null) {
        ((StartDocumentEvent)localObject2).setDeclaredEncoding(true);
      } else {
        ((StartDocumentEvent)localObject2).setDeclaredEncoding(false);
      }
      ((StartDocumentEvent)localObject2).setStandalone(paramXMLStreamReader.isStandalone());
      ((StartDocumentEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    case 8: 
      localObject2 = new EndDocumentEvent();
      ((EndDocumentEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    case 9: 
      localObject2 = new EntityReferenceEvent(paramXMLStreamReader.getLocalName(), new EntityDeclarationImpl(paramXMLStreamReader.getLocalName(), paramXMLStreamReader.getText()));
      ((EntityReferenceEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    case 10: 
      localObject1 = null;
      break;
    case 11: 
      localObject2 = new DTDEvent(paramXMLStreamReader.getText());
      ((DTDEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      List localList1 = (List)paramXMLStreamReader.getProperty("javax.xml.stream.entities");
      if ((localList1 != null) && (localList1.size() != 0)) {
        ((DTDEvent)localObject2).setEntities(localList1);
      }
      List localList2 = (List)paramXMLStreamReader.getProperty("javax.xml.stream.notations");
      if ((localList2 != null) && (localList2.size() != 0)) {
        ((DTDEvent)localObject2).setNotations(localList2);
      }
      localObject1 = localObject2;
      break;
    case 12: 
      localObject2 = new CharacterEvent(paramXMLStreamReader.getText(), true);
      ((CharacterEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    case 6: 
      localObject2 = new CharacterEvent(paramXMLStreamReader.getText(), false, true);
      ((CharacterEvent)localObject2).setLocation(paramXMLStreamReader.getLocation());
      localObject1 = localObject2;
      break;
    }
    return localObject1;
  }
  
  protected XMLEvent getNextEvent(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    paramXMLStreamReader.next();
    return getXMLEvent(paramXMLStreamReader);
  }
  
  protected void fillAttributes(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader)
  {
    int i = paramXMLStreamReader.getAttributeCount();
    QName localQName = null;
    AttributeImpl localAttributeImpl = null;
    Object localObject = null;
    for (int j = 0; j < i; j++)
    {
      localQName = paramXMLStreamReader.getAttributeName(j);
      localAttributeImpl = new AttributeImpl();
      localAttributeImpl.setName(localQName);
      localAttributeImpl.setAttributeType(paramXMLStreamReader.getAttributeType(j));
      localAttributeImpl.setSpecified(paramXMLStreamReader.isAttributeSpecified(j));
      localAttributeImpl.setValue(paramXMLStreamReader.getAttributeValue(j));
      paramStartElementEvent.addAttribute(localAttributeImpl);
    }
  }
  
  protected void fillNamespaceAttributes(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader)
  {
    int i = paramXMLStreamReader.getNamespaceCount();
    String str1 = null;
    String str2 = null;
    NamespaceImpl localNamespaceImpl = null;
    for (int j = 0; j < i; j++)
    {
      str1 = paramXMLStreamReader.getNamespaceURI(j);
      str2 = paramXMLStreamReader.getNamespacePrefix(j);
      if (str2 == null) {
        str2 = "";
      }
      localNamespaceImpl = new NamespaceImpl(str2, str1);
      paramStartElementEvent.addNamespaceAttribute(localNamespaceImpl);
    }
  }
  
  protected void fillNamespaceAttributes(EndElementEvent paramEndElementEvent, XMLStreamReader paramXMLStreamReader)
  {
    int i = paramXMLStreamReader.getNamespaceCount();
    String str1 = null;
    String str2 = null;
    NamespaceImpl localNamespaceImpl = null;
    for (int j = 0; j < i; j++)
    {
      str1 = paramXMLStreamReader.getNamespaceURI(j);
      str2 = paramXMLStreamReader.getNamespacePrefix(j);
      if (str2 == null) {
        str2 = "";
      }
      localNamespaceImpl = new NamespaceImpl(str2, str1);
      paramEndElementEvent.addNamespace(localNamespaceImpl);
    }
  }
  
  private void setNamespaceContext(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader)
  {
    NamespaceContextWrapper localNamespaceContextWrapper = (NamespaceContextWrapper)paramXMLStreamReader.getNamespaceContext();
    NamespaceSupport localNamespaceSupport = new NamespaceSupport(localNamespaceContextWrapper.getNamespaceContext());
    paramStartElementEvent.setNamespaceContext(new NamespaceContextWrapper(localNamespaceSupport));
  }
  
  private QName getQName(XMLStreamReader paramXMLStreamReader)
  {
    return new QName(paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName(), paramXMLStreamReader.getPrefix());
  }
}
