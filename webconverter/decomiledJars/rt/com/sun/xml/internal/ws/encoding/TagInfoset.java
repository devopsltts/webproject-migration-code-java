package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public final class TagInfoset
{
  @NotNull
  public final String[] ns;
  @NotNull
  public final AttributesImpl atts;
  @Nullable
  public final String prefix;
  @Nullable
  public final String nsUri;
  @NotNull
  public final String localName;
  @Nullable
  private String qname;
  private static final String[] EMPTY_ARRAY = new String[0];
  private static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();
  
  public TagInfoset(String paramString1, String paramString2, String paramString3, AttributesImpl paramAttributesImpl, String... paramVarArgs)
  {
    this.nsUri = paramString1;
    this.prefix = paramString3;
    this.localName = paramString2;
    this.atts = paramAttributesImpl;
    this.ns = paramVarArgs;
  }
  
  public TagInfoset(XMLStreamReader paramXMLStreamReader)
  {
    this.prefix = paramXMLStreamReader.getPrefix();
    this.nsUri = paramXMLStreamReader.getNamespaceURI();
    this.localName = paramXMLStreamReader.getLocalName();
    int i = paramXMLStreamReader.getNamespaceCount();
    if (i > 0)
    {
      this.ns = new String[i * 2];
      for (j = 0; j < i; j++)
      {
        this.ns[(j * 2)] = fixNull(paramXMLStreamReader.getNamespacePrefix(j));
        this.ns[(j * 2 + 1)] = fixNull(paramXMLStreamReader.getNamespaceURI(j));
      }
    }
    else
    {
      this.ns = EMPTY_ARRAY;
    }
    int j = paramXMLStreamReader.getAttributeCount();
    if (j > 0)
    {
      this.atts = new AttributesImpl();
      StringBuilder localStringBuilder = new StringBuilder();
      for (int k = 0; k < j; k++)
      {
        localStringBuilder.setLength(0);
        String str1 = paramXMLStreamReader.getAttributePrefix(k);
        String str2 = paramXMLStreamReader.getAttributeLocalName(k);
        String str3;
        if ((str1 != null) && (str1.length() != 0))
        {
          localStringBuilder.append(str1);
          localStringBuilder.append(":");
          localStringBuilder.append(str2);
          str3 = localStringBuilder.toString();
        }
        else
        {
          str3 = str2;
        }
        this.atts.addAttribute(fixNull(paramXMLStreamReader.getAttributeNamespace(k)), str2, str3, paramXMLStreamReader.getAttributeType(k), paramXMLStreamReader.getAttributeValue(k));
      }
    }
    else
    {
      this.atts = EMPTY_ATTRIBUTES;
    }
  }
  
  public void writeStart(ContentHandler paramContentHandler)
    throws SAXException
  {
    for (int i = 0; i < this.ns.length; i += 2) {
      paramContentHandler.startPrefixMapping(fixNull(this.ns[i]), fixNull(this.ns[(i + 1)]));
    }
    paramContentHandler.startElement(fixNull(this.nsUri), this.localName, getQName(), this.atts);
  }
  
  public void writeEnd(ContentHandler paramContentHandler)
    throws SAXException
  {
    paramContentHandler.endElement(fixNull(this.nsUri), this.localName, getQName());
    for (int i = this.ns.length - 2; i >= 0; i -= 2) {
      paramContentHandler.endPrefixMapping(fixNull(this.ns[i]));
    }
  }
  
  public void writeStart(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (this.prefix == null)
    {
      if (this.nsUri == null) {
        paramXMLStreamWriter.writeStartElement(this.localName);
      } else {
        paramXMLStreamWriter.writeStartElement("", this.localName, this.nsUri);
      }
    }
    else {
      paramXMLStreamWriter.writeStartElement(this.prefix, this.localName, this.nsUri);
    }
    for (int i = 0; i < this.ns.length; i += 2) {
      paramXMLStreamWriter.writeNamespace(this.ns[i], this.ns[(i + 1)]);
    }
    for (i = 0; i < this.atts.getLength(); i++)
    {
      String str1 = this.atts.getURI(i);
      if ((str1 == null) || (str1.length() == 0))
      {
        paramXMLStreamWriter.writeAttribute(this.atts.getLocalName(i), this.atts.getValue(i));
      }
      else
      {
        String str2 = this.atts.getQName(i);
        String str3 = str2.substring(0, str2.indexOf(':'));
        paramXMLStreamWriter.writeAttribute(str3, str1, this.atts.getLocalName(i), this.atts.getValue(i));
      }
    }
  }
  
  private String getQName()
  {
    if (this.qname != null) {
      return this.qname;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.prefix != null)
    {
      localStringBuilder.append(this.prefix);
      localStringBuilder.append(':');
      localStringBuilder.append(this.localName);
      this.qname = localStringBuilder.toString();
    }
    else
    {
      this.qname = this.localName;
    }
    return this.qname;
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  public String getNamespaceURI(String paramString)
  {
    int i = this.ns.length / 2;
    for (int j = 0; j < i; j++)
    {
      String str1 = this.ns[(j * 2)];
      String str2 = this.ns[(j * 2 + 1)];
      if (paramString.equals(str1)) {
        return str2;
      }
    }
    return null;
  }
  
  public String getPrefix(String paramString)
  {
    int i = this.ns.length / 2;
    for (int j = 0; j < i; j++)
    {
      String str1 = this.ns[(j * 2)];
      String str2 = this.ns[(j * 2 + 1)];
      if (paramString.equals(str2)) {
        return str1;
      }
    }
    return null;
  }
  
  public List<String> allPrefixes(String paramString)
  {
    int i = this.ns.length / 2;
    ArrayList localArrayList = new ArrayList();
    for (int j = 0; j < i; j++)
    {
      String str1 = this.ns[(j * 2)];
      String str2 = this.ns[(j * 2 + 1)];
      if (paramString.equals(str2)) {
        localArrayList.add(str1);
      }
    }
    return localArrayList;
  }
}
