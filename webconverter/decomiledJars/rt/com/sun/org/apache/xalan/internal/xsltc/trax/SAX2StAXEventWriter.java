package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

public class SAX2StAXEventWriter
  extends SAX2StAXBaseWriter
{
  private XMLEventWriter writer;
  private XMLEventFactory eventFactory;
  private List namespaceStack = new ArrayList();
  private boolean needToCallStartDocument = false;
  
  public SAX2StAXEventWriter()
  {
    this.eventFactory = XMLEventFactory.newInstance();
  }
  
  public SAX2StAXEventWriter(XMLEventWriter paramXMLEventWriter)
  {
    this.writer = paramXMLEventWriter;
    this.eventFactory = XMLEventFactory.newInstance();
  }
  
  public SAX2StAXEventWriter(XMLEventWriter paramXMLEventWriter, XMLEventFactory paramXMLEventFactory)
  {
    this.writer = paramXMLEventWriter;
    if (paramXMLEventFactory != null) {
      this.eventFactory = paramXMLEventFactory;
    } else {
      this.eventFactory = XMLEventFactory.newInstance();
    }
  }
  
  public XMLEventWriter getEventWriter()
  {
    return this.writer;
  }
  
  public void setEventWriter(XMLEventWriter paramXMLEventWriter)
  {
    this.writer = paramXMLEventWriter;
  }
  
  public XMLEventFactory getEventFactory()
  {
    return this.eventFactory;
  }
  
  public void setEventFactory(XMLEventFactory paramXMLEventFactory)
  {
    this.eventFactory = paramXMLEventFactory;
  }
  
  public void startDocument()
    throws SAXException
  {
    super.startDocument();
    this.namespaceStack.clear();
    this.eventFactory.setLocation(getCurrentLocation());
    this.needToCallStartDocument = true;
  }
  
  private void writeStartDocument()
    throws SAXException
  {
    try
    {
      if (this.docLocator == null) {
        this.writer.add(this.eventFactory.createStartDocument());
      } else {
        try
        {
          this.writer.add(this.eventFactory.createStartDocument(((Locator2)this.docLocator).getEncoding(), ((Locator2)this.docLocator).getXMLVersion()));
        }
        catch (ClassCastException localClassCastException)
        {
          this.writer.add(this.eventFactory.createStartDocument());
        }
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    this.needToCallStartDocument = false;
  }
  
  public void endDocument()
    throws SAXException
  {
    this.eventFactory.setLocation(getCurrentLocation());
    try
    {
      this.writer.add(this.eventFactory.createEndDocument());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    super.endDocument();
    this.namespaceStack.clear();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (this.needToCallStartDocument) {
      writeStartDocument();
    }
    this.eventFactory.setLocation(getCurrentLocation());
    Collection[] arrayOfCollection = { null, null };
    createStartEvents(paramAttributes, arrayOfCollection);
    this.namespaceStack.add(arrayOfCollection[0]);
    try
    {
      String[] arrayOfString = { null, null };
      parseQName(paramString3, arrayOfString);
      this.writer.add(this.eventFactory.createStartElement(arrayOfString[0], paramString1, arrayOfString[1], arrayOfCollection[1].iterator(), arrayOfCollection[0].iterator()));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    finally
    {
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    super.endElement(paramString1, paramString2, paramString3);
    this.eventFactory.setLocation(getCurrentLocation());
    String[] arrayOfString = { null, null };
    parseQName(paramString3, arrayOfString);
    Collection localCollection = (Collection)this.namespaceStack.remove(this.namespaceStack.size() - 1);
    Iterator localIterator = localCollection.iterator();
    try
    {
      this.writer.add(this.eventFactory.createEndElement(arrayOfString[0], paramString1, arrayOfString[1], localIterator));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this.needToCallStartDocument) {
      writeStartDocument();
    }
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
    this.eventFactory.setLocation(getCurrentLocation());
    try
    {
      this.writer.add(this.eventFactory.createComment(new String(paramArrayOfChar, paramInt1, paramInt2)));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
    try
    {
      if (!this.isCDATA)
      {
        this.eventFactory.setLocation(getCurrentLocation());
        this.writer.add(this.eventFactory.createCharacters(new String(paramArrayOfChar, paramInt1, paramInt2)));
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (this.needToCallStartDocument) {
      writeStartDocument();
    }
    super.processingInstruction(paramString1, paramString2);
    try
    {
      this.writer.add(this.eventFactory.createProcessingInstruction(paramString1, paramString2));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void endCDATA()
    throws SAXException
  {
    this.eventFactory.setLocation(getCurrentLocation());
    try
    {
      this.writer.add(this.eventFactory.createCData(this.CDATABuffer.toString()));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    super.endCDATA();
  }
  
  protected void createStartEvents(Attributes paramAttributes, Collection[] paramArrayOfCollection)
  {
    HashMap localHashMap = null;
    ArrayList localArrayList = null;
    String str2;
    Object localObject1;
    if (this.namespaces != null)
    {
      int i = this.namespaces.size();
      for (j = 0; j < i; j++)
      {
        String str1 = (String)this.namespaces.elementAt(j++);
        str2 = (String)this.namespaces.elementAt(j);
        localObject1 = createNamespace(str1, str2);
        if (localHashMap == null) {
          localHashMap = new HashMap();
        }
        localHashMap.put(str1, localObject1);
      }
    }
    String[] arrayOfString = { null, null };
    int j = 0;
    int k = paramAttributes.getLength();
    while (j < k)
    {
      parseQName(paramAttributes.getQName(j), arrayOfString);
      str2 = arrayOfString[0];
      localObject1 = arrayOfString[1];
      String str3 = paramAttributes.getQName(j);
      String str4 = paramAttributes.getValue(j);
      String str5 = paramAttributes.getURI(j);
      Object localObject2;
      if (("xmlns".equals(str3)) || ("xmlns".equals(str2)))
      {
        if (localHashMap == null) {
          localHashMap = new HashMap();
        }
        if (!localHashMap.containsKey(localObject1))
        {
          localObject2 = createNamespace((String)localObject1, str4);
          localHashMap.put(localObject1, localObject2);
        }
      }
      else
      {
        if (str2.length() > 0) {
          localObject2 = this.eventFactory.createAttribute(str2, str5, (String)localObject1, str4);
        } else {
          localObject2 = this.eventFactory.createAttribute((String)localObject1, str4);
        }
        if (localArrayList == null) {
          localArrayList = new ArrayList();
        }
        localArrayList.add(localObject2);
      }
      j++;
    }
    paramArrayOfCollection[0] = (localHashMap == null ? Collections.EMPTY_LIST : localHashMap.values());
    paramArrayOfCollection[1] = (localArrayList == null ? Collections.EMPTY_LIST : localArrayList);
  }
  
  protected Namespace createNamespace(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      return this.eventFactory.createNamespace(paramString2);
    }
    return this.eventFactory.createNamespace(paramString1, paramString2);
  }
}
