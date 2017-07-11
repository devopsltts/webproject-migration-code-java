package javax.xml.stream.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamReaderDelegate
  implements XMLStreamReader
{
  private XMLStreamReader reader;
  
  public StreamReaderDelegate() {}
  
  public StreamReaderDelegate(XMLStreamReader paramXMLStreamReader)
  {
    this.reader = paramXMLStreamReader;
  }
  
  public void setParent(XMLStreamReader paramXMLStreamReader)
  {
    this.reader = paramXMLStreamReader;
  }
  
  public XMLStreamReader getParent()
  {
    return this.reader;
  }
  
  public int next()
    throws XMLStreamException
  {
    return this.reader.next();
  }
  
  public int nextTag()
    throws XMLStreamException
  {
    return this.reader.nextTag();
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    return this.reader.getElementText();
  }
  
  public void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    this.reader.require(paramInt, paramString1, paramString2);
  }
  
  public boolean hasNext()
    throws XMLStreamException
  {
    return this.reader.hasNext();
  }
  
  public void close()
    throws XMLStreamException
  {
    this.reader.close();
  }
  
  public String getNamespaceURI(String paramString)
  {
    return this.reader.getNamespaceURI(paramString);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return this.reader.getNamespaceContext();
  }
  
  public boolean isStartElement()
  {
    return this.reader.isStartElement();
  }
  
  public boolean isEndElement()
  {
    return this.reader.isEndElement();
  }
  
  public boolean isCharacters()
  {
    return this.reader.isCharacters();
  }
  
  public boolean isWhiteSpace()
  {
    return this.reader.isWhiteSpace();
  }
  
  public String getAttributeValue(String paramString1, String paramString2)
  {
    return this.reader.getAttributeValue(paramString1, paramString2);
  }
  
  public int getAttributeCount()
  {
    return this.reader.getAttributeCount();
  }
  
  public QName getAttributeName(int paramInt)
  {
    return this.reader.getAttributeName(paramInt);
  }
  
  public String getAttributePrefix(int paramInt)
  {
    return this.reader.getAttributePrefix(paramInt);
  }
  
  public String getAttributeNamespace(int paramInt)
  {
    return this.reader.getAttributeNamespace(paramInt);
  }
  
  public String getAttributeLocalName(int paramInt)
  {
    return this.reader.getAttributeLocalName(paramInt);
  }
  
  public String getAttributeType(int paramInt)
  {
    return this.reader.getAttributeType(paramInt);
  }
  
  public String getAttributeValue(int paramInt)
  {
    return this.reader.getAttributeValue(paramInt);
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    return this.reader.isAttributeSpecified(paramInt);
  }
  
  public int getNamespaceCount()
  {
    return this.reader.getNamespaceCount();
  }
  
  public String getNamespacePrefix(int paramInt)
  {
    return this.reader.getNamespacePrefix(paramInt);
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return this.reader.getNamespaceURI(paramInt);
  }
  
  public int getEventType()
  {
    return this.reader.getEventType();
  }
  
  public String getText()
  {
    return this.reader.getText();
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    return this.reader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
  }
  
  public char[] getTextCharacters()
  {
    return this.reader.getTextCharacters();
  }
  
  public int getTextStart()
  {
    return this.reader.getTextStart();
  }
  
  public int getTextLength()
  {
    return this.reader.getTextLength();
  }
  
  public String getEncoding()
  {
    return this.reader.getEncoding();
  }
  
  public boolean hasText()
  {
    return this.reader.hasText();
  }
  
  public Location getLocation()
  {
    return this.reader.getLocation();
  }
  
  public QName getName()
  {
    return this.reader.getName();
  }
  
  public String getLocalName()
  {
    return this.reader.getLocalName();
  }
  
  public boolean hasName()
  {
    return this.reader.hasName();
  }
  
  public String getNamespaceURI()
  {
    return this.reader.getNamespaceURI();
  }
  
  public String getPrefix()
  {
    return this.reader.getPrefix();
  }
  
  public String getVersion()
  {
    return this.reader.getVersion();
  }
  
  public boolean isStandalone()
  {
    return this.reader.isStandalone();
  }
  
  public boolean standaloneSet()
  {
    return this.reader.standaloneSet();
  }
  
  public String getCharacterEncodingScheme()
  {
    return this.reader.getCharacterEncodingScheme();
  }
  
  public String getPITarget()
  {
    return this.reader.getPITarget();
  }
  
  public String getPIData()
  {
    return this.reader.getPIData();
  }
  
  public Object getProperty(String paramString)
  {
    return this.reader.getProperty(paramString);
  }
}
