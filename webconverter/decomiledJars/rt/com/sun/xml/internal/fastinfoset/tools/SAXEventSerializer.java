package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXEventSerializer
  extends DefaultHandler
  implements LexicalHandler
{
  private Writer _writer;
  private boolean _charactersAreCDATA;
  private StringBuffer _characters;
  private Stack _namespaceStack = new Stack();
  protected List _namespaceAttributes;
  
  public SAXEventSerializer(OutputStream paramOutputStream)
    throws IOException
  {
    this._writer = new OutputStreamWriter(paramOutputStream);
    this._charactersAreCDATA = false;
  }
  
  public void startDocument()
    throws SAXException
  {
    try
    {
      this._writer.write("<sax xmlns=\"http://www.sun.com/xml/sax-events\">\n");
      this._writer.write("<startDocument/>\n");
      this._writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      this._writer.write("<endDocument/>\n");
      this._writer.write("</sax>");
      this._writer.flush();
      this._writer.close();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (this._namespaceAttributes == null) {
      this._namespaceAttributes = new ArrayList();
    }
    String str = "xmlns" + paramString1;
    AttributeValueHolder localAttributeValueHolder = new AttributeValueHolder(str, paramString1, paramString2, null, null);
    this._namespaceAttributes.add(localAttributeValueHolder);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    try
    {
      outputCharacters();
      if (this._namespaceAttributes != null)
      {
        arrayOfAttributeValueHolder = new AttributeValueHolder[0];
        arrayOfAttributeValueHolder = (AttributeValueHolder[])this._namespaceAttributes.toArray(arrayOfAttributeValueHolder);
        quicksort(arrayOfAttributeValueHolder, 0, arrayOfAttributeValueHolder.length - 1);
        for (i = 0; i < arrayOfAttributeValueHolder.length; i++)
        {
          this._writer.write("<startPrefixMapping prefix=\"" + arrayOfAttributeValueHolder[i].localName + "\" uri=\"" + arrayOfAttributeValueHolder[i].uri + "\"/>\n");
          this._writer.flush();
        }
        this._namespaceStack.push(arrayOfAttributeValueHolder);
        this._namespaceAttributes = null;
      }
      else
      {
        this._namespaceStack.push(null);
      }
      AttributeValueHolder[] arrayOfAttributeValueHolder = new AttributeValueHolder[paramAttributes.getLength()];
      for (int i = 0; i < paramAttributes.getLength(); i++) {
        arrayOfAttributeValueHolder[i] = new AttributeValueHolder(paramAttributes.getQName(i), paramAttributes.getLocalName(i), paramAttributes.getURI(i), paramAttributes.getType(i), paramAttributes.getValue(i));
      }
      quicksort(arrayOfAttributeValueHolder, 0, arrayOfAttributeValueHolder.length - 1);
      i = 0;
      for (int j = 0; j < arrayOfAttributeValueHolder.length; j++) {
        if (!arrayOfAttributeValueHolder[j].uri.equals("http://www.w3.org/2000/xmlns/")) {
          i++;
        }
      }
      if (i == 0)
      {
        this._writer.write("<startElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\"/>\n");
        return;
      }
      this._writer.write("<startElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\">\n");
      for (j = 0; j < arrayOfAttributeValueHolder.length; j++) {
        if (!arrayOfAttributeValueHolder[j].uri.equals("http://www.w3.org/2000/xmlns/")) {
          this._writer.write("  <attribute qName=\"" + arrayOfAttributeValueHolder[j].qName + "\" localName=\"" + arrayOfAttributeValueHolder[j].localName + "\" uri=\"" + arrayOfAttributeValueHolder[j].uri + "\" value=\"" + arrayOfAttributeValueHolder[j].value + "\"/>\n");
        }
      }
      this._writer.write("</startElement>\n");
      this._writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      outputCharacters();
      this._writer.write("<endElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\"/>\n");
      this._writer.flush();
      AttributeValueHolder[] arrayOfAttributeValueHolder = (AttributeValueHolder[])this._namespaceStack.pop();
      if (arrayOfAttributeValueHolder != null) {
        for (int i = 0; i < arrayOfAttributeValueHolder.length; i++)
        {
          this._writer.write("<endPrefixMapping prefix=\"" + arrayOfAttributeValueHolder[i].localName + "\"/>\n");
          this._writer.flush();
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 == 0) {
      return;
    }
    if (this._characters == null) {
      this._characters = new StringBuffer();
    }
    this._characters.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private void outputCharacters()
    throws SAXException
  {
    if (this._characters == null) {
      return;
    }
    try
    {
      this._writer.write("<characters>" + (this._charactersAreCDATA ? "<![CDATA[" : "") + this._characters + (this._charactersAreCDATA ? "]]>" : "") + "</characters>\n");
      this._writer.flush();
      this._characters = null;
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      outputCharacters();
      this._writer.write("<processingInstruction target=\"" + paramString1 + "\" data=\"" + paramString2 + "\"/>\n");
      this._writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {
    this._charactersAreCDATA = true;
  }
  
  public void endCDATA()
    throws SAXException
  {
    this._charactersAreCDATA = false;
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      outputCharacters();
      this._writer.write("<comment>" + new String(paramArrayOfChar, paramInt1, paramInt2) + "</comment>\n");
      this._writer.flush();
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  private void quicksort(AttributeValueHolder[] paramArrayOfAttributeValueHolder, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      int i = partition(paramArrayOfAttributeValueHolder, paramInt1, paramInt2);
      quicksort(paramArrayOfAttributeValueHolder, paramInt1, i);
      paramInt1 = i + 1;
    }
  }
  
  private int partition(AttributeValueHolder[] paramArrayOfAttributeValueHolder, int paramInt1, int paramInt2)
  {
    AttributeValueHolder localAttributeValueHolder1 = paramArrayOfAttributeValueHolder[(paramInt1 + paramInt2 >>> 1)];
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    for (;;)
    {
      if (localAttributeValueHolder1.compareTo(paramArrayOfAttributeValueHolder[(--j)]) >= 0)
      {
        while (localAttributeValueHolder1.compareTo(paramArrayOfAttributeValueHolder[(++i)]) > 0) {}
        if (i >= j) {
          break;
        }
        AttributeValueHolder localAttributeValueHolder2 = paramArrayOfAttributeValueHolder[i];
        paramArrayOfAttributeValueHolder[i] = paramArrayOfAttributeValueHolder[j];
        paramArrayOfAttributeValueHolder[j] = localAttributeValueHolder2;
      }
    }
    return j;
  }
  
  public static class AttributeValueHolder
    implements Comparable
  {
    public final String qName;
    public final String localName;
    public final String uri;
    public final String type;
    public final String value;
    
    public AttributeValueHolder(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    {
      this.qName = paramString1;
      this.localName = paramString2;
      this.uri = paramString3;
      this.type = paramString4;
      this.value = paramString5;
    }
    
    public int compareTo(Object paramObject)
    {
      try
      {
        return this.qName.compareTo(((AttributeValueHolder)paramObject).qName);
      }
      catch (Exception localException)
      {
        throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
      }
    }
    
    public boolean equals(Object paramObject)
    {
      try
      {
        return ((paramObject instanceof AttributeValueHolder)) && (this.qName.equals(((AttributeValueHolder)paramObject).qName));
      }
      catch (Exception localException)
      {
        throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
      }
    }
    
    public int hashCode()
    {
      int i = 7;
      i = 97 * i + (this.qName != null ? this.qName.hashCode() : 0);
      return i;
    }
  }
}
