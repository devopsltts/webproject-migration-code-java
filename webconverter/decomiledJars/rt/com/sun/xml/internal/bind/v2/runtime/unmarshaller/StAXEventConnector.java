package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

final class StAXEventConnector
  extends StAXConnector
{
  private final XMLEventReader staxEventReader;
  private XMLEvent event;
  private final AttributesImpl attrs = new AttributesImpl();
  private final StringBuilder buffer = new StringBuilder();
  private boolean seenText;
  
  public StAXEventConnector(XMLEventReader paramXMLEventReader, XmlVisitor paramXmlVisitor)
  {
    super(paramXmlVisitor);
    this.staxEventReader = paramXMLEventReader;
  }
  
  public void bridge()
    throws XMLStreamException
  {
    try
    {
      int i = 0;
      this.event = this.staxEventReader.peek();
      if ((!this.event.isStartDocument()) && (!this.event.isStartElement())) {
        throw new IllegalStateException();
      }
      do
      {
        this.event = this.staxEventReader.nextEvent();
      } while (!this.event.isStartElement());
      handleStartDocument(this.event.asStartElement().getNamespaceContext());
      for (;;)
      {
        switch (this.event.getEventType())
        {
        case 1: 
          handleStartElement(this.event.asStartElement());
          i++;
          break;
        case 2: 
          i--;
          handleEndElement(this.event.asEndElement());
          if (i != 0) {
            break;
          }
          break;
        case 4: 
        case 6: 
        case 12: 
          handleCharacters(this.event.asCharacters());
        }
        this.event = this.staxEventReader.nextEvent();
      }
      handleEndDocument();
      this.event = null;
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  protected Location getCurrentLocation()
  {
    return this.event.getLocation();
  }
  
  protected String getCurrentQName()
  {
    QName localQName;
    if (this.event.isEndElement()) {
      localQName = this.event.asEndElement().getName();
    } else {
      localQName = this.event.asStartElement().getName();
    }
    return getQName(localQName.getPrefix(), localQName.getLocalPart());
  }
  
  private void handleCharacters(Characters paramCharacters)
    throws SAXException, XMLStreamException
  {
    if (!this.predictor.expectText()) {
      return;
    }
    this.seenText = true;
    XMLEvent localXMLEvent;
    for (;;)
    {
      localXMLEvent = this.staxEventReader.peek();
      if (!isIgnorable(localXMLEvent)) {
        break;
      }
      this.staxEventReader.nextEvent();
    }
    if (isTag(localXMLEvent))
    {
      this.visitor.text(paramCharacters.getData());
      return;
    }
    this.buffer.append(paramCharacters.getData());
    for (;;)
    {
      localXMLEvent = this.staxEventReader.peek();
      if (isIgnorable(localXMLEvent))
      {
        this.staxEventReader.nextEvent();
      }
      else
      {
        if (isTag(localXMLEvent))
        {
          this.visitor.text(this.buffer);
          this.buffer.setLength(0);
          return;
        }
        this.buffer.append(localXMLEvent.asCharacters().getData());
        this.staxEventReader.nextEvent();
      }
    }
  }
  
  private boolean isTag(XMLEvent paramXMLEvent)
  {
    int i = paramXMLEvent.getEventType();
    return (i == 1) || (i == 2);
  }
  
  private boolean isIgnorable(XMLEvent paramXMLEvent)
  {
    int i = paramXMLEvent.getEventType();
    return (i == 5) || (i == 3);
  }
  
  private void handleEndElement(EndElement paramEndElement)
    throws SAXException
  {
    if ((!this.seenText) && (this.predictor.expectText())) {
      this.visitor.text("");
    }
    QName localQName = paramEndElement.getName();
    this.tagName.uri = fixNull(localQName.getNamespaceURI());
    this.tagName.local = localQName.getLocalPart();
    this.visitor.endElement(this.tagName);
    Iterator localIterator = paramEndElement.getNamespaces();
    while (localIterator.hasNext())
    {
      String str = fixNull(((Namespace)localIterator.next()).getPrefix());
      this.visitor.endPrefixMapping(str);
    }
    this.seenText = false;
  }
  
  private void handleStartElement(StartElement paramStartElement)
    throws SAXException
  {
    Object localObject1 = paramStartElement.getNamespaces();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Namespace)((Iterator)localObject1).next();
      this.visitor.startPrefixMapping(fixNull(((Namespace)localObject2).getPrefix()), fixNull(((Namespace)localObject2).getNamespaceURI()));
    }
    localObject1 = paramStartElement.getName();
    this.tagName.uri = fixNull(((QName)localObject1).getNamespaceURI());
    Object localObject2 = ((QName)localObject1).getLocalPart();
    this.tagName.uri = fixNull(((QName)localObject1).getNamespaceURI());
    this.tagName.local = ((String)localObject2);
    this.tagName.atts = getAttributes(paramStartElement);
    this.visitor.startElement(this.tagName);
    this.seenText = false;
  }
  
  private Attributes getAttributes(StartElement paramStartElement)
  {
    this.attrs.clear();
    Iterator localIterator = paramStartElement.getAttributes();
    while (localIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)localIterator.next();
      QName localQName = localAttribute.getName();
      String str1 = fixNull(localQName.getNamespaceURI());
      String str2 = localQName.getLocalPart();
      String str3 = localQName.getPrefix();
      String str4;
      if ((str3 == null) || (str3.length() == 0)) {
        str4 = str2;
      } else {
        str4 = str3 + ':' + str2;
      }
      String str5 = localAttribute.getDTDType();
      String str6 = localAttribute.getValue();
      this.attrs.addAttribute(str1, str2, str4, str5, str6);
    }
    return this.attrs;
  }
}
