package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.SAXException2;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class ContentHandlerAdaptor
  extends DefaultHandler
{
  private final FinalArrayList<String> prefixMap = new FinalArrayList();
  private final XMLSerializer serializer;
  private final StringBuffer text = new StringBuffer();
  
  ContentHandlerAdaptor(XMLSerializer paramXMLSerializer)
  {
    this.serializer = paramXMLSerializer;
  }
  
  public void startDocument()
  {
    this.prefixMap.clear();
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    this.prefixMap.add(paramString1);
    this.prefixMap.add(paramString2);
  }
  
  private boolean containsPrefixMapping(String paramString1, String paramString2)
  {
    for (int i = 0; i < this.prefixMap.size(); i += 2) {
      if ((((String)this.prefixMap.get(i)).equals(paramString1)) && (((String)this.prefixMap.get(i + 1)).equals(paramString2))) {
        return true;
      }
    }
    return false;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    try
    {
      flushText();
      int i = paramAttributes.getLength();
      String str1 = getPrefix(paramString3);
      if (containsPrefixMapping(str1, paramString1)) {
        this.serializer.startElementForce(paramString1, paramString2, str1, null);
      } else {
        this.serializer.startElement(paramString1, paramString2, str1, null);
      }
      for (int j = 0; j < this.prefixMap.size(); j += 2) {
        this.serializer.getNamespaceContext().force((String)this.prefixMap.get(j + 1), (String)this.prefixMap.get(j));
      }
      for (j = 0; j < i; j++)
      {
        String str2 = paramAttributes.getQName(j);
        if ((!str2.startsWith("xmlns")) && (paramAttributes.getURI(j).length() != 0))
        {
          String str3 = getPrefix(str2);
          this.serializer.getNamespaceContext().declareNamespace(paramAttributes.getURI(j), str3, true);
        }
      }
      this.serializer.endNamespaceDecls(null);
      for (j = 0; j < i; j++) {
        if (!paramAttributes.getQName(j).startsWith("xmlns")) {
          this.serializer.attribute(paramAttributes.getURI(j), paramAttributes.getLocalName(j), paramAttributes.getValue(j));
        }
      }
      this.prefixMap.clear();
      this.serializer.endAttributes();
    }
    catch (IOException localIOException)
    {
      throw new SAXException2(localIOException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException2(localXMLStreamException);
    }
  }
  
  private String getPrefix(String paramString)
  {
    int i = paramString.indexOf(':');
    String str = i == -1 ? "" : paramString.substring(0, i);
    return str;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      flushText();
      this.serializer.endElement();
    }
    catch (IOException localIOException)
    {
      throw new SAXException2(localIOException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException2(localXMLStreamException);
    }
  }
  
  private void flushText()
    throws SAXException, IOException, XMLStreamException
  {
    if (this.text.length() != 0)
    {
      this.serializer.text(this.text.toString(), null);
      this.text.setLength(0);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    this.text.append(paramArrayOfChar, paramInt1, paramInt2);
  }
}
